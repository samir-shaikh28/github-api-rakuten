package com.example.github.repositories.network

sealed class Result<out T : Any?>

data class Success<out T : Any>(val data: T) : Result<T>()

data class Error<out T : Any?>(val data: T?) : Result<T>()

data class Failure(val throwable: Throwable) : Result<Nothing>()