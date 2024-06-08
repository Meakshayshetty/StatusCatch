package com.devatrii.statussaver.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.statusdownloader.repository.StatusRepository
import com.example.statusdownloader.viewmodels.StatusViewModel

class StatusViewModelFactory(private val repo: StatusRepository):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StatusViewModel(repo) as T
    }
}