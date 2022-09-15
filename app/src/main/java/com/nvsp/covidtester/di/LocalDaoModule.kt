package com.nvsp.testerapp.di



import com.nvsp.nvmesapplibrary.database.LibDao
import com.nvsp.nvmesapplibrary.database.LibDatabase
import org.koin.dsl.module

val daoModule = module {
    fun provideLibDao(database: LibDatabase):LibDao = database.getLibDao()
    single {
        provideLibDao(get())
    }
}