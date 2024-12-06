package com.nsicyber.whispersnearby.di

import android.content.Context
import android.content.res.AssetManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.nsicyber.whispersnearby.data.repository.CameraRepositoryImpl
import com.nsicyber.whispersnearby.data.repository.ChatRepositoryImpl
import com.nsicyber.whispersnearby.data.repository.EmotionRecognitionMlRepositoryImpl
import com.nsicyber.whispersnearby.data.repository.EmotionRecognitionRepositoryImpl
 import com.nsicyber.whispersnearby.domain.repository.CameraRepository
import com.nsicyber.whispersnearby.domain.repository.ChatRepository
import com.nsicyber.whispersnearby.domain.repository.EmotionRecognitionMlRepository
import com.nsicyber.whispersnearby.domain.repository.EmotionRecognitionRepository
import com.nsicyber.whispersnearby.domain.useCase.EmotionRecognitionMlUseCase
import com.nsicyber.whispersnearby.domain.useCase.EmotionRecognitionUseCase
import com.nsicyber.whispersnearby.utils.LocationUtils
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.channels.FileChannel
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
    fun provideInterpreter(assetManager: AssetManager): Interpreter {
        val fileDescriptor = assetManager.openFd("simple_classifier.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        val buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        return Interpreter(buffer)
    }




    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideAssetManager(context: Context): AssetManager = context.assets

    @Provides
    @Singleton
    fun provideEmotionRecognitionRepository(
        interpreter: Interpreter
    ): EmotionRecognitionRepository = EmotionRecognitionRepositoryImpl(interpreter)



    @Provides
    @Singleton
    fun provideChatRepository(
        firestore: FirebaseFirestore
    ): ChatRepository {
        return ChatRepositoryImpl(firestore)
    }


    @Provides
    @Singleton
    fun provideFaceDetector(): FaceDetector {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
        return FaceDetection.getClient(options)
    }

    @Provides
    @Singleton
    fun provideEmotionRecognitionMlRepository(faceDetector: FaceDetector): EmotionRecognitionMlRepository {
        return EmotionRecognitionMlRepositoryImpl(faceDetector)
    }

    @Provides
    @Singleton
    fun provideEmotionRecognitionMlUseCase(repository: EmotionRecognitionMlRepository): EmotionRecognitionMlUseCase {
        return EmotionRecognitionMlUseCase(repository)
    }

}
