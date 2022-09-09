package com.net.xhttp

import com.net.xhttp.interceptors.NetInterceptor
import com.net.xhttp.interceptors.RequestHandler
import okhttp3.Interceptor


class HttpHelper {

    companion object {
        private val mHttpHelper by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            HttpHelper()
        }

        fun getInstance() = mHttpHelper
    }

    private var isDebug: Boolean = false
    private val mBaseUrl = "mBaseUrl"
    private val mDebugBaseUrl = "mDebugBaseUrl"
    private var mInterceptorList = arrayListOf<Interceptor>()


    //baseURL 支持多个 BaseUrl
    private var mBaseUrlMap: MutableMap<String, String> = HashMap()

    /*//header 支持自定义请求header
    private var mHeaderMap: MutableMap<String, String> = HashMap()*/

    fun isDebug(): Boolean {
        return isDebug
    }

    fun isDebug(isDebug: Boolean): HttpHelper {
        this.isDebug = isDebug
        return this
    }

    fun getBaseUrl(): String {
        val key = if (isDebug) mDebugBaseUrl else mBaseUrl
        return mBaseUrlMap[key] ?: ""
    }

    fun setBaseUrl(url: String): HttpHelper {
        addBaseUrl(mBaseUrl, url)
        return this
    }

    fun setDebugBaseUrl(url: String): HttpHelper {
        addBaseUrl(mDebugBaseUrl, url)
        return this
    }

    fun addBaseUrl(key: String, value: String): HttpHelper {
        mBaseUrlMap[key] = value
        return this
    }

    fun addBaseUrl(map: MutableMap<String, String>): HttpHelper {
        mBaseUrlMap.putAll(map)
        return this
    }

    fun getBaseUrlMap() = mBaseUrlMap

    fun addRequestHandler(handler: RequestHandler): HttpHelper {
        mInterceptorList.add(NetInterceptor(handler))
        return this
    }

    fun addRequestInterceptor(interceptor: Interceptor): HttpHelper {
        mInterceptorList.add(interceptor)
        return this
    }

    fun build() {
        initHttp(getBaseUrl(), mInterceptorList, isDebug)
    }

    private fun initHttp(baseUrl: String, interceptor: List<Interceptor> = mutableListOf(), isDebug: Boolean) {
        RetrofitHelper.instance.apply {
            setBaseUrl(baseUrl)
            interceptor.forEach {
                addInterceptor(it)
            }
            isDebug(isDebug)
        }.builder()
    }


}