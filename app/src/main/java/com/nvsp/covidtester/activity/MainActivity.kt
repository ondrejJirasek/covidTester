package com.nvsp.covidtester.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.nvsp.covidtester.BaseApp
import com.nvsp.covidtester.BuildConfig
import com.nvsp.covidtester.R
import com.nvsp.covidtester.ViewModels.MainViewModel
import com.nvsp.covidtester.databinding.ActivityMainBinding
import com.nvsp.nvmesapplibrary.App
import com.nvsp.nvmesapplibrary.architecture.BaseActivity
import com.nvsp.nvmesapplibrary.constants.Keys
import com.nvsp.nvmesapplibrary.login.LoginActivity
import com.nvsp.nvmesapplibrary.settings.SettingsActivity
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(MainViewModel::class)  {

    private var conState=false
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var startActivityForResult : ActivityResultLauncher<Intent>
    companion object {
        fun createIntent(context: Context): Intent = Intent(context, MainActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)

        startActivityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                restartApp()
            }
        }

        mViewModel.activeSetting.observe(this, {
            it?.let{set->
                mViewModel.loadSettings(set)
            }
        })

        setContentView(binding.root)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Covid Log"

        toolbar.backButtonVisible(false)

        mViewModel.login.observe(this,{
           if(it==null)
               startLogin()

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings ->{
                startSettings()
                true  }
            R.id.action_LogOut->{
                lifecycleScope.launch {  mViewModel.logOut() }
                true
            }
            R.id.action_finish->{
                this.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
    private fun startSettings(){
        val intent = Intent(this, SettingsActivity::class.java)
        val bundle=Bundle()
        bundle.putString(Keys.APP_NAME, BaseApp.instance.packageName)

        bundle.putString(Keys.APP_ID, BuildConfig.APPLICATION_ID)
        intent.putExtras(bundle)
        startActivityForResult.launch(intent )
    }
    private fun restartApp(){
        BaseApp.instance.restartApp()
    }
    fun startLogin(){
        val intent = Intent(this, LoginActivity::class.java)
        val bundle=Bundle()
        bundle.putString(Keys.APP_NAME,"TESTER app")

        bundle.putString(Keys.APP_ID, BuildConfig.APPLICATION_ID)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}