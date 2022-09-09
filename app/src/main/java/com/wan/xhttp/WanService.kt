package com.wan.xhttp

import com.net.xhttp.bean.BaseResponse
import com.net.xhttp.DoMainUrl
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface WanService {


    @Headers("$DoMainUrl:baidu")
    @GET("banner/json")
    suspend fun getHomeBanner(): BaseResponse<List<BannerEntity>>

    @GET("article/list/{page}/json")
    fun getHomeArticleList(@Path("page") page: Int): Single<BaseResponse<ArticlePage>>


}