package com.nvsp.covidtester

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.nvsp.covidtester.activity.MainActivity
import com.nvsp.nvmesapplibrary.App

import com.nvsp.testerapp.di.*


import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import kotlin.system.exitProcess


class BaseApp:Application() {
    lateinit var guid:String
    var urlApi:String ="http:/asda/"
    @SuppressLint("HardwareIds")

    override fun onCreate() {
        super.onCreate()
        instance=this
        appContext = applicationContext
        guid=android.provider.Settings.Secure.getString(appContext.contentResolver, android.provider.Settings.Secure.ANDROID_ID)
        App.params(appContext,Const.dbName)
        startKoin()
    }

    fun startKoin(){
        startKoin {
            //Zde inicializujeme vse pro DI

            App.di(appContext)
            androidLogger(Level.ERROR)  //logovani Koinu
            androidContext(appContext)
            modules(
                viewModelModule,
                repositoryModule,
                databaseModule,
                daoModule,
                volleyModule,
                apiModule
                //settingsModule
            ) //pole modulu
        }
    }

    fun refresh(){
        Log.d("BASEAPP", "RESTART KOIN")
        stopKoin()
        startKoin()
    }
    @SuppressLint("UnspecifiedImmutableFlag")
    fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        val mPendingIntentId = 1
        val mPendingIntent = PendingIntent.getActivity(
            applicationContext,
            mPendingIntentId,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val mgr = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr[AlarmManager.RTC, System.currentTimeMillis() + 100] = mPendingIntent
        exitProcess(0)
    }
    companion object {
        fun reset(){
            this.instance.restartApp()
        }
        lateinit var instance: BaseApp
        lateinit var appContext: Context
            private set
    }
}