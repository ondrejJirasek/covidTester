package com.nvsp.covidtester.ViewModels


import android.util.Log
import com.nvsp.covidtester.BaseApp

import com.nvsp.nvmesapplibrary.architecture.BaseViewModel
import com.nvsp.nvmesapplibrary.constants.Const
import com.nvsp.nvmesapplibrary.database.LibRepository
import com.nvsp.nvmesapplibrary.settings.models.Settings


class MainViewModel(private val repository: LibRepository):BaseViewModel(repository){
    fun loadSettings(set:Settings){
                Const.URL_BASE = set.baseUrl()
                BaseApp.instance.urlApi=set.baseUrl()
                Log.d("URL", "URL IS: ${Const.URL_BASE}")
                BaseApp.instance.refresh()

    }
}