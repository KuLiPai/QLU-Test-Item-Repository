package com.example.qlutestitemrepository.data.network

import com.example.qlutestitemrepository.data.model.GitHubFileItem
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubApiService {
    @GET("repos/Torchman005/QLU-Test-Item-Files/contents/{path}")
    suspend fun getContents(@Path("path", encoded = true) path: String = ""): List<GitHubFileItem>
}
