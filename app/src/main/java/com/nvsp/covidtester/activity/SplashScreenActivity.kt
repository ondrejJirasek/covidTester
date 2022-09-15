package com.nvsp.covidtester.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.nvsp.covidtester.BaseApp
import com.nvsp.covidtester.BuildConfig
import com.nvsp.covidtester.ViewModels.*
import com.nvsp.nvmesapplibrary.App

import com.nvsp.nvmesapplibrary.architecture.BaseActivity
import com.nvsp.nvmesapplibrary.architecture.InfoDialog
import com.nvsp.nvmesapplibrary.constants.Const
import com.nvsp.nvmesapplibrary.login.LoginActivity
import com.nvsp.nvmesapplibrary.login.models.User
import com.nvsp.nvmesapplibrary.settings.SettingsActivity
import com.nvsp.nvmesapplibrary.utils.model.ApkInfo

import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.getViewModel


class SplashScreenActivity :  AppCompatActivity() {
    private val mViewModel: SplashViewModel by lazy {
        getViewModel(
            null,
            SplashViewModel::class
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("SPLASH", "oncreateSplash")
        super.onCreate(savedInstanceState)


        lifecycleScope.launch {
            Log.d("MainActivity", "LOGOUt ")
            mViewModel.logOut()
        }

        mViewModel.checkInputParams(intent)


        mViewModel.activeSetting.observe(this, {
            Log.d("OBSERVE", "Settings:${it!=null}")
            it?.let{set->

                mViewModel.loadSettings(set)
            }?:(startActivity(SettingsActivity.createIntent(this)))

        })
        mViewModel.splashCheckStatus.observe(this) { action ->
            when (action) {
                LOADED_SETTING -> {
                    mViewModel.testConnection(mViewModel.activeSetting.value?.ipAddress ?: ("NOIP"))
                }
                NO_CONECTED -> {
                    startActivity(SettingsActivity.createIntent(this))
                }
                CONECTED -> {
                    mViewModel.testVersion(){apkInfo, apkExist, error ->
                        if(error)
                            startActivity(SettingsActivity.createIntent(this))
                        else
                            apkInfo?.let { updaterDialog(it,apkExist)  }?: kotlin.run { mViewModel.splashCheckStatus.postValue(UPDATED)}

                    }
                }
                UPDATED -> {
                    mViewModel.loadRemoteSettings(this)
                }
                LOADED_REMOTE_SETTING -> {
                    mViewModel.testLogin()
                }
                NOT_LOGGED -> {
                    startLoginActivity()
                }
                LOGGED -> {
                    startApp()
                }
            }
        }
        mViewModel.loadedSettings.observe(this,{
            Log.d("OBSERVE", "loadedSettings:${it}")
            if(it){

               // if(mViewModel.connectionLiveData.value==true) {

                mViewModel.testVersion(){apkInfo, apkExist, error ->
                    if(error)
                        startActivity(SettingsActivity.createIntent(this))
                    else
                        apkInfo?.let { updaterDialog(it,apkExist)  }?: kotlin.run { mViewModel.splashCheckStatus.postValue(
                            UPDATED)}

                }
   }
})
mViewModel.login.observe(this, {
    Log.d("OBSERVE", "login:${it!=null}")
   if(it==null) {
       if (mViewModel.splashFunFinish.value == true)
           startActivity(LoginActivity.createIntent(this))
   }else
       continueToApp()
})
mViewModel.splashFunFinish.observe(this,{
    Log.d("OBSERVE", "finish:${it}")
   if(it)
       continueToApp()
})
}
    fun Context.getAppName(): String = applicationInfo.loadLabel(packageManager).toString()
    fun updaterDialog(apk: ApkInfo, apkExist:Boolean){
        val localVersion = mViewModel.updater.getVersion(BuildConfig.VERSION_NAME)
        val nameAPK =getAppName()

        val dialog = InfoDialog(this)
        dialog.setTitle(getString(com.nvsp.nvmesapplibrary.R.string.Aktualizace))
        if(apkExist){
            if(localVersion!=apk.version){
                if(localVersion>apk.version){
                    dialog.showWithMessage("Vaše verze aplikace $nameAPK je ve verzi $localVersion na serveru je k dispozici verze ${apk.version}. Přejete si snížit verzi? ", level = Const.LEVEL_UPDATE){
                        if(it)
                            mViewModel.downloadFile(this)
                        dialog.dismiss()


                        mViewModel.splashCheckStatus.postValue(UPDATED)
                    }
                }
                if(localVersion<apk.version){
                    dialog.showWithMessage("Je k dispozici novější verze aplikace $nameAPK ${apk.version}. Vaše verze je $localVersion. Přejete si aplikaci aktualizovat? ", level = Const.LEVEL_UPDATE){
                        if(it)
                            mViewModel.downloadFile(this)
                        dialog.dismiss()
                        mViewModel.splashCheckStatus.postValue(UPDATED)
                    }
                }
            }else{
                mViewModel.splashCheckStatus.postValue(UPDATED)

            }            }else{
            mViewModel.splashCheckStatus.postValue(UPDATED)
        }




    }
    private fun startApp(){
        startActivity(MainActivity.createIntent(this))
        finish()
    }
    private fun startLoginActivity(){
        val intent = Intent(this, LoginActivity::class.java)
        //intent.putExtras(BaseApp.createLoginBundle())
        startActivity(intent)
    }
private fun continueToApp(){
if(mViewModel.splashFunFinish.value==true)
if(mViewModel.login.value!=null){
startActivity(MainActivity.createIntent(this))
finish()}
else
  startActivity(LoginActivity.createIntent(this))
}

}