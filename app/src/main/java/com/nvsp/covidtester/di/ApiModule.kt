package com.nvsp.testerapp.di



import com.nvsp.covidtester.BaseApp
import com.nvsp.nvmesapplibrary.communication.volley.ServiceVolley
import com.nvsp.nvmesapplibrary.communication.volley.VolleySingleton

import org.koin.dsl.module


val apiModule = module {
    fun provideApi(singleton: VolleySingleton): ServiceVolley {

        return ServiceVolley(singleton, BaseApp.instance.urlApi)
    }

    single {
        provideApi(get())
    }

}