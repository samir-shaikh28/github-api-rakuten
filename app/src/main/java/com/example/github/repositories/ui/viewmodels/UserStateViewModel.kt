package com.example.github.repositories.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.github.repositories.data.RepositoryDTO

class UserStateViewModel : ViewModel() {
    var selectedRepo = MutableLiveData<RepositoryDTO>()
}