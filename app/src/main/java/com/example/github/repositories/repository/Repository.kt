package com.example.github.repositories.repository

import com.example.github.repositories.data.RepositoryDTO
import com.example.github.repositories.data.Response
import com.example.github.repositories.data.UserDTO
import com.example.github.repositories.network.GitHubEndpoints
import com.example.github.repositories.network.Result

class Repository(private val endPoints: GitHubEndpoints) {

    suspend fun searchRepositories(query: String, sort: String, order: String): Response {
        return endPoints.searchRepositories(q = query, sort = sort, order = order)
    }

    suspend fun getUser(userName: String): UserDTO {
        return endPoints.getUser(username = userName)
    }

    suspend fun getUserRepositories(userRepoUrl: String): List<RepositoryDTO> {
        return endPoints.getUserRepositories(userRepo = userRepoUrl)
    }
}