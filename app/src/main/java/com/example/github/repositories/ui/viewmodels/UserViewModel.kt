package com.example.github.repositories.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.github.repositories.data.RepositoryDTO
import com.example.github.repositories.data.UserDTO
import com.example.github.repositories.network.Error
import com.example.github.repositories.network.Failure
import com.example.github.repositories.network.Result
import com.example.github.repositories.network.Success
import com.example.github.repositories.repository.Repository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UserViewModel(private val repository: Repository) : ViewModel() {


    private var _userObserver = MutableLiveData<Result<UserDTO>>()
    val userObserver: LiveData<Result<UserDTO>>
        get() = _userObserver


    private var _repositories = MutableLiveData<Result<List<RepositoryDTO>>>()
    val repositories: LiveData<Result<List<RepositoryDTO>>>
        get() = _repositories


    fun fetchUser(username: String) {

        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            _userObserver.postValue(Failure(throwable))
        }

        viewModelScope.launch(exceptionHandler + Dispatchers.IO) {
            delay(1_000) // This is to simulate network latency, please don't remove!
            val response = repository.getUser(username)
            // Considering api response is incorrect if login is null
            if (response.login.isNullOrEmpty()) {
                _userObserver.postValue(Error(response))
            } else {
                _userObserver.postValue(Success(response))
            }
        }
    }

    fun fetchRepositories(reposUrl: String) {

        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            _repositories.postValue(Failure(throwable))
        }

        viewModelScope.launch(exceptionHandler + Dispatchers.IO) {
            delay(1_000) // This is to simulate network latency, please don't remove!
            val response = repository.getUserRepositories(reposUrl)
            // To represent error state considering API response is incorrect if list size is zero
            if (response.isNotEmpty()) {
                _repositories.postValue(Success(response))
            } else {
                _repositories.postValue(Error(response))
            }
        }

    }
}