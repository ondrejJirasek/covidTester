package com.nvsp.testerapp.di

import androidx.lifecycle.SavedStateHandle
import com.nvsp.covidtester.ViewModels.FirstFragmentViewModel
import com.nvsp.covidtester.ViewModels.LoginViewModel
import com.nvsp.covidtester.ViewModels.MainViewModel
import com.nvsp.covidtester.ViewModels.SplashViewModel

import com.nvsp.nvmesapplibrary.architecture.CommunicationViewModel
import com.nvsp.nvmesapplibrary.login.LoginActivityViewModel
import com.nvsp.nvmesapplibrary.login.LoginByIDViewModel
import com.nvsp.nvmesapplibrary.login.LoginByNameViewModel
import com.nvsp.nvmesapplibrary.queue.GenericQueueViewModel
import com.nvsp.nvmesapplibrary.queue.work_queue.WorkQueueViewModel
import com.nvsp.nvmesapplibrary.queue.work_queue.WorkQueueViewModel2
import com.nvsp.nvmesapplibrary.settings.SettingsActivityViewModel
import org.koin.androidx.viewmodel.dsl.viewModel


import org.koin.dsl.module

val viewModelModule = module {

    viewModel { GenericQueueViewModel(get(), get()) }
    viewModel { WorkQueueViewModel(get(), get()) }
    viewModel { WorkQueueViewModel2(get(), get()) }
    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { LoginActivityViewModel(get(), get())}
    viewModel { SettingsActivityViewModel(get()) }
    viewModel { LoginByNameViewModel(get(),get()) }
    viewModel { LoginByIDViewModel(get(),get()) }
    viewModel { CommunicationViewModel(get(), get()) }

    viewModel { FirstFragmentViewModel(get(),get()) }
    viewModel { SplashViewModel(get()) }
    viewModel { MainViewModel(get()) }

    //viewModel { AddTaskViewModel(get(), get()) }
fun provideSavedStateHandle():SavedStateHandle{
    return SavedStateHandle()
}
    factory {provideSavedStateHandle()  } // neposila singleton
}