package com.example.moengagenewsapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moengagenewsapp.networking.RemoteAPI

class MainViewModelFactory(val remoteAPI: RemoteAPI) :ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(remoteAPI) as T
    }
}