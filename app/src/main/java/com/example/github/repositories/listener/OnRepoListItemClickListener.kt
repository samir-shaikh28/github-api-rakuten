package com.example.github.repositories.listener

import com.example.github.repositories.data.RepositoryDTO

interface OnRepoListItemClickListener {
    fun openFragmentDetail(repositoryDTO: RepositoryDTO)
}
