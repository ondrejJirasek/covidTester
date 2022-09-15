package com.nvsp.covidtester.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle

import com.nvsp.nvmesapplibrary.architecture.CommunicationViewModel
import com.nvsp.nvmesapplibrary.communication.volley.ServiceVolley
import com.nvsp.nvmesapplibrary.database.LibRepository

class LoginViewModel(val handle: SavedStateHandle, private val apiRemoteRepository:ServiceVolley, private val localRepository: LibRepository) : CommunicationViewModel(localRepository,apiRemoteRepository) {

    val  showProgress = MutableLiveData<Boolean>()// apiRemoteRepository.showProgress
    val  error = MutableLiveData<String>()



}