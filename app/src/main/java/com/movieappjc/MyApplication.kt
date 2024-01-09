package com.movieappjc

import com.core_app.BaseApplication
import com.core_app.SingletonHolder
import com.movieappjc.common.constants.Endpoints
import com.movieappjc.di.apiModule
import com.movieappjc.di.appNavigatorModule
import com.movieappjc.di.databaseModule
import com.movieappjc.di.repositoryModule
import com.movieappjc.di.useCaseModule
import com.movieappjc.di.viewModelModule
import org.koin.core.module.Module

class MyApplication : BaseApplication() {

    companion object : SingletonHolder<MyApplication>(::MyApplication)

    override val apiUrl: String
        get() = Endpoints.apiUrl

    override fun addModuleKoin(): MutableList<Module> {
        val modules = super.addModuleKoin()
        modules.addAll(
            arrayListOf(
                databaseModule,
                apiModule,
                repositoryModule,
                useCaseModule,
                appNavigatorModule,
                viewModelModule
            )
        )
        return modules
    }
}