package com.nvsp.covidtester.ViewModels

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.google.gson.Gson
import com.nvsp.covidtester.BaseApp
import com.nvsp.covidtester.BuildConfig
import com.nvsp.nvmesapplibrary.App

import com.nvsp.nvmesapplibrary.architecture.BaseViewModel
import com.nvsp.nvmesapplibrary.communication.volley.ServiceVolley
import com.nvsp.nvmesapplibrary.communication.volley.VolleySingleton
import com.nvsp.nvmesapplibrary.constants.Const
import com.nvsp.nvmesapplibrary.database.LibRepository
import com.nvsp.nvmesapplibrary.login.models.User
import com.nvsp.nvmesapplibrary.rpc.OutData
import com.nvsp.nvmesapplibrary.settings.models.Settings
import com.nvsp.nvmesapplibrary.utils.CommonUtils

import com.nvsp.nvmesapplibrary.utils.model.ApkInfo
import com.nvsp.nvmesapplibrary.utils.model.RemoteSettings
import com.nvsp.nvmesapplibrary.utils.updater.Updater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

const val LOADED_SETTING=1
const val CONECTED=2
const val UPDATED=3
const val LOADED_REMOTE_SETTING=4
const val LOGGED=5
const val NOT_LOGGED=105
const val NO_CONECTED=102
const val NOT_LOADED_REMOTE_SETTING=104
class SplashViewModel(private val repository: LibRepository): BaseViewModel(repository){
    val api :ServiceVolley by lazy { ServiceVolley(VolleySingleton.getInstance(), BaseApp.instance.urlApi) }
    val updater: Updater by lazy { Updater(api) }
    var splashCheckStatus= MutableLiveData<Int>(0)

    var splashFunFinish= MutableLiveData<Boolean>(false)
    var loadedSettings= MutableLiveData<Boolean>(false)
    fun loadSettings(set:Settings){
        Const.URL_BASE = set.baseUrl()
        BaseApp.instance.urlApi=set.baseUrl()
        Log.d("URL", "URL IS: ${Const.URL_BASE}")
        BaseApp.instance.refresh()
        loadedSettings.value=true

    }
    fun skipSettings(){
        splashFunFinish.value=true
    }
    fun testVersion(ret:(ApkInfo?, Boolean,Boolean)->Unit){

        updater.checkVersion(){apk, apkExist,error ->
            Log.d("APK","APK:${apk?.version} ACTUAL VERSION :${BuildConfig.VERSION_CODE}")
            ret(apk, apkExist, error)
        }


    }

    fun downloadFile(context: Context){
        updater.enqueueDownload(context)
    }

    fun checkInputParams(intent: Intent):Boolean{
        val ss=intent.getStringExtra("login")
        if(ss==null)
            return false
        else {
            val jsonLogin = JSONObject(ss)
            val user= User().create(jsonLogin)
            user.app = App.appId

        writeUserToDb(user)
        return true
        }
    }
    fun writeUserToDb(user:User){
        CoroutineScope(Dispatchers.IO).launch {

            repository.insertUser(user)  }
    }
    fun testConnection(ip:String){
        var numOfLostPacket=0
        for(i in 0 until 5){
            Log.d("Test Connection", "test $i ... lost $numOfLostPacket ")
            if(isNetworkAvailable(ip)) {
                numOfLostPacket = 0
                splashCheckStatus.postValue(CONECTED)
            }else
                numOfLostPacket++
            if(numOfLostPacket >2)
                splashCheckStatus.postValue(NO_CONECTED)
        }


    }

    private fun isNetworkAvailable(ip:String): Boolean {
        if(ip.contains("ngrok"))
            return true
        else{
            val runtime = Runtime.getRuntime()
            try {
                val ipProcess = runtime.exec("/system/bin/ping -c 1 $ip")
                val exitValue = ipProcess.waitFor()
                return exitValue == 0

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            return false
        }
    }
    fun testLogin(){
        login.value?.let {
            splashCheckStatus.postValue(LOGGED)
        }?: kotlin.run {
            splashCheckStatus.postValue(NOT_LOGGED)
        }
    }
    fun loadRemoteSettings(context: Context){

            splashCheckStatus.postValue(LOADED_REMOTE_SETTING)

    }
}