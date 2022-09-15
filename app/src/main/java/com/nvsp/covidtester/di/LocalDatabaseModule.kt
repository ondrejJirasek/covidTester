package com.nvsp.testerapp.di


import com.nvsp.covidtester.BaseApp
import com.nvsp.covidtester.Const
import com.nvsp.nvmesapplibrary.database.LibDatabase

import org.koin.dsl.module

val databaseModule = module {
    fun provideDatabase():LibDatabase = LibDatabase.getDatabase(BaseApp.appContext, Const.dbName)
    single {  provideDatabase()} //vola Singleton jen jednu instanci
}