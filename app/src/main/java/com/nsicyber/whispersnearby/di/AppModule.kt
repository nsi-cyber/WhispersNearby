package com.nsicyber.whispersnearby.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.nsicyber.whispersnearby.data.repository.ChatRepositoryImpl
import com.nsicyber.whispersnearby.domain.repository.ChatRepository
import com.nsicyber.whispersnearby.utils.LocationUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideLocationUtils(@ApplicationContext context: Context): LocationUtils {
        return LocationUtils(context)
    }



    @Provides
    @Singleton
    fun provideChatRepository(
        firestore: FirebaseFirestore
    ): ChatRepository {
        return ChatRepositoryImpl(firestore)
    }
}
