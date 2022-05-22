package com.example.github.repositories.data

object LocalDataStore {

    private val bookmarks = mutableListOf<Int>()

    fun bookmarkRepo(id: Int, bookmarked: Boolean) {
        if (bookmarked)
            bookmarks.add(id)
        else
            bookmarks.remove(id)
    }

    fun getBookmarks() = bookmarks
}