package com.example.squadmaker

import android.app.Application
import com.example.squadmaker.di.networkModule
import com.example.squadmaker.di.repositoryModule
import com.example.squadmaker.di.roomDatabaseModule
import com.example.squadmaker.di.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin

class MySquadApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MySquadApplication)
            fragmentFactory()
            modules(
                listOf(
                    networkModule,
                    roomDatabaseModule,
                    repositoryModule,
                    viewModelsModule
                )
            )
        }
    }
}