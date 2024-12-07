package com.nsicyber.whispersnearby.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.nsicyber.whispersnearby.data.remote.ChatMessage
import com.nsicyber.whispersnearby.domain.repository.ChatRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.imperiumlabs.geofirestore.GeoFirestore
import org.imperiumlabs.geofirestore.listeners.GeoQueryDataEventListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChatRepository {

    private val geoFirestore: GeoFirestore by lazy {
        GeoFirestore(firestore.collection("messages"))
    }

    override suspend fun reportMessage(messageId: String, deviceId: String) {
        try {
            val documentRef = firestore.collection("messages").document(messageId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(documentRef)
                val reportedBy = snapshot.get("reportedBy") as? List<String> ?: emptyList()
                if (!(deviceId in reportedBy)){
                    if (reportedBy.size + 1 > 3) {
                        transaction.delete(documentRef)
                    } else {
                        val updatedReportedBy = reportedBy + deviceId
                        transaction.update(
                            documentRef, mapOf(
                                "reportedBy" to updatedReportedBy,
                            )
                        )
                    }
                }
            }.await()
        } catch (_: Exception) {

        }

    }


    override fun getNearbyMessages(
        latitude: Double,
        longitude: Double,
        radius: Double,
        secretCode: String?
    ): Flow<List<ChatMessage>> {
        return callbackFlow {
            val geoQuery =
                geoFirestore.queryAtLocation(GeoPoint(latitude, longitude), radius)
            val messageList = mutableListOf<ChatMessage>()

            geoQuery.addGeoQueryDataEventListener(object : GeoQueryDataEventListener {

                override fun onDocumentChanged(
                    documentSnapshot: DocumentSnapshot,
                    location: GeoPoint
                ) {
                }

                override fun onDocumentEntered(
                    documentSnapshot: DocumentSnapshot,
                    location: GeoPoint
                ) {
                    val message = documentSnapshot.toObject(ChatMessage::class.java)
                    if (message != null) {
                        secretCode?.let {
                            if (message.secretCode == secretCode) {
                                messageList.add(message)
                                messageList.sortByDescending { it.timestamp }
                                trySend(messageList.toList())
                            }
                        } ?: run {
                            if (message.secretCode.isBlank()) {
                                messageList.add(message)
                                messageList.sortByDescending { it.timestamp }
                                trySend(messageList.toList())
                            }
                        }

                    }
                }

                override fun onDocumentExited(documentSnapshot: DocumentSnapshot) {
                    val message = documentSnapshot.toObject(ChatMessage::class.java)
                    if (message != null) {
                        secretCode?.let {
                            if (message.secretCode == secretCode) {
                                messageList.add(message)
                                messageList.sortByDescending { it.timestamp }

                                trySend(messageList.toList())
                            }
                        } ?: run {
                            if (message.secretCode.isBlank()) {
                                messageList.add(message)
                                messageList.sortByDescending { it.timestamp }
                                trySend(messageList.toList())
                            }
                        }
                    }
                }

                override fun onDocumentMoved(
                    documentSnapshot: DocumentSnapshot,
                    location: GeoPoint
                ) {
                }

                override fun onGeoQueryError(exception: Exception) {
                    close(exception)
                }

                override fun onGeoQueryReady() {}
            })

            awaitClose { geoQuery.removeAllListeners() }
        }
    }

    override suspend fun deleteAllMessages() {
        val querySnapshot = firestore.collection("messages")
            .get()
            .await()

        querySnapshot.documents.forEach { document ->
            firestore.collection("messages").document(document.id).delete().await()
        }
    }

    override suspend fun sendMessage(message: ChatMessage) {
        val documentRef = firestore.collection("messages").document()
        val data = message.copy(id = documentRef.id)
        documentRef.set(data).await()
        geoFirestore.setLocation(
            documentRef.id,
            GeoPoint(message.latitude, message.longitude)
        )
    }

}
