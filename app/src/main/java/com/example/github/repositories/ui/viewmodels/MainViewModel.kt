package com.example.github.repositories.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.github.repositories.data.ORDER
import com.example.github.repositories.data.QUERY
import com.example.github.repositories.data.RepositoryDTO
import com.example.github.repositories.data.SORT
import com.example.github.repositories.network.Error
import com.example.github.repositories.network.Failure
import com.example.github.repositories.network.Result
import com.example.github.repositories.network.Success
import com.example.github.repositories.repository.Repository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {

    private var _repositories = MutableLiveData<Result<List<RepositoryDTO>>>()
    val repositories: LiveData<Result<List<RepositoryDTO>>>
        get() = _repositories

    fun fetchItems() {

        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            _repositories.postValue(Failure(throwable))
        }

        viewModelScope.launch(exceptionHandler + Dispatchers.IO) {
            delay(1_000) // This is to simulate network latency, please don't remove!
            val response = repository.searchRepositories(QUERY, SORT, ORDER)
            // To represent error state considering API response is incorrect if list size is zero
            if (response.total_count.toInt() > 0) {
                _repositories.postValue(Success(response.items))
            } else {
                _repositories.postValue(Error(response.items))
            }
        }
    }


    fun refresh() {
        fetchItems()
    }
}



