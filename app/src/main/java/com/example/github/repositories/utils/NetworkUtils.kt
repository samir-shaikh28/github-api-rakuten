package com.example.github.repositories.utils

import android.content.Context
import android.net.ConnectivityManager

object NetworkUtils {

    @JvmStatic
    fun isNetworkAvailable(context: Context?): Boolean {
        context?.let {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
        } ?: run {
            return false
        }
    }
}