package com.core_app

import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.core_app.di.RetrofitModule
import com.core_app.di.dataStoreKoinModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module

abstract class BaseApplication: MultiDexApplication() {
    abstract val apiUrl: String
    companion object{
        private lateinit var instance: BaseApplication
        fun getInstance() = instance
    }

    override fun onCreate() {
        MultiDex.install(this)
        super.onCreate()
        instance = this
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.DEBUG else Level.NONE)
            androidContext(this@BaseApplication)
            modules(addModuleKoin())

        }
    }

    open fun addModuleKoin(): MutableList<Module>{
        val modules = arrayListOf<Module>()
        modules.add(RetrofitModule)
        modules.add(dataStoreKoinModule)
        return modules
    }
}