package com.example.di

import android.content.Context
import com.example.ai.AIContextBuilder
import com.example.data.local.AppDatabase
import com.example.data.local.StudyDao
import com.example.data.repository.AIRepository
import com.example.data.repository.StudyRepository

object AppModule {
    @Volatile
    private var database: AppDatabase? = null

    @Volatile
    private var studyRepo: StudyRepository? = null

    @Volatile
    private var aiRepo: AIRepository? = null

    fun provideAppDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            database ?: AppDatabase.getDatabase(context).also { database = it }
        }
    }

    fun provideStudyDao(context: Context): StudyDao {
        return provideAppDatabase(context).studyDao()
    }

    fun provideStudyRepository(context: Context): StudyRepository {
        return studyRepo ?: synchronized(this) {
            studyRepo ?: StudyRepository(provideStudyDao(context)).also { studyRepo = it }
        }
    }

    fun provideAIContextBuilder(): AIContextBuilder {
        return AIContextBuilder()
    }

    fun provideAIRepository(context: Context): AIRepository {
        return aiRepo ?: synchronized(this) {
            val studyRepository = provideStudyRepository(context)
            aiRepo ?: AIRepository(
                studyRepository = studyRepository,
                aiContextBuilder = provideAIContextBuilder()
            ).also { aiRepo = it }
        }
    }
}
