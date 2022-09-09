package com.wan.xhttp

import com.net.xhttp.bean.BaseResponse
import com.net.xhttp.bean.RequestResult
import com.net.xhttp.RetrofitHelper
import com.net.xhttp.config.SimpleRequestListener
import io.reactivex.Single
import kotlinx.coroutines.flow.MutableSharedFlow

class WanRepository {

    companion object {
        val instance: WanRepository by lazy { WanRepository() }
    }

    /*协成示例--mvi*/
    suspend fun getHomeBannerDataByMvi(mBannerDataState: MutableSharedFlow<RequestResult<List<BannerEntity>>>) {
        RetrofitHelper.instance.requestSafely(WanService::class.java, mBannerDataState) {
            it.getHomeBanner()
        }
    }

    /*协成示例-callback*/
    suspend fun getHomeBannerData(simpleRequestListener: SimpleRequestListener<List<BannerEntity>>) {
        RetrofitHelper.instance.requestSafely(WanService::class.java, simpleRequestListener) {
            it.getHomeBanner()
        }
    }

    /*rxjava示例*/
    fun getArticleList(page: Int): Single<BaseResponse<ArticlePage>> {
        val remote = RetrofitHelper.instance.getService(WanService::class.java)
        return remote.getHomeArticleList(page)
    }


}