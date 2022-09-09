package com.net.xhttp

import com.net.xhttp.interceptors.RequestHandler
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

const val DoMainUrl = "base_url"

/**
 * 支持多 BaseURL 拦截器
 */
class MoreBaseUrlHandler : RequestHandler {

    override fun onBeforeRequest(request: Request, chain: Interceptor.Chain): Request {
        var oldHttpUrl: HttpUrl = request.url
        var builder: Request.Builder = request.newBuilder();

        val headerValues: List<String> = request.headers(DoMainUrl)
        return if (headerValues.isNotEmpty()) {
            var newFullUrl: HttpUrl = reBaseUrl(builder, headerValues, oldHttpUrl)
            chain.request().newBuilder().url(newFullUrl).build()
        } else {
            val original = chain.request()
            chain.request().newBuilder().url(original.url).build()
        }
    }

    override fun onAfterRequest(response: Response, chain: Interceptor.Chain): Response {
//        var e: ApiException? = null
//        when {
//            401 == response.code -> {
//                throw ApiException("登录已过期,请重新登录!")
//            }
//            403 == response.code -> {
//                e = ApiException("禁止访问!")
//            }
//            404 == response.code -> {
//                e = ApiException("链接错误")
//            }
//            503 == response.code -> {
//                e = ApiException("服务器升级中!")
//            }
//            response.code > 300 -> {
//                val message = response.body!!.string()
//                e = if (NetUtils.check(message)) {
//                    ApiException("服务器内部错误!")
//                } else {
//                    ApiException(message)
//                }
//            }
//        }
//        if (!NetUtils.check(e)) {
//            throw e!!
//        }
        return response
    }


    private fun reBaseUrl(builder: Request.Builder, headerValues: List<String>, oldHttpUrl: HttpUrl): HttpUrl {
        //如果有这个header，先将配置的header删除，因此header仅用作app和okhttp之间使用
        /*builder.removeHeader(com.wan.base.utils.BaseApi.URL_HEADER)*/
        //匹配获得新的BaseUrl
        val headerValue: String = headerValues[0];
        var newBaseUrl: HttpUrl? = null
        val defaultBaseUrl = HttpHelper.getInstance().getBaseUrl()
        if (headerValue.isEmpty()) {
            newBaseUrl = defaultBaseUrl.toHttpUrlOrNull()
        } else {
            val mOtherBaseUrl = HttpHelper.getInstance().getBaseUrlMap().get(headerValue)
            if (mOtherBaseUrl.isNullOrEmpty()) {
                newBaseUrl = defaultBaseUrl.toHttpUrlOrNull()
            } else {
                newBaseUrl = mOtherBaseUrl.toHttpUrlOrNull()
            }
        }
        var newFullUrl: HttpUrl = oldHttpUrl.newBuilder()
            .scheme(newBaseUrl!!.scheme)//更换网络协议
            .host(newBaseUrl!!.host)//更换主机名
            .port(newBaseUrl!!.port)//更换端口
            .build();
        return newFullUrl
    }

    private fun addHeader(chain: Interceptor.Chain, headerMap: MutableMap<String, String>, url: HttpUrl): Request {
        val builder = chain.request().newBuilder()
        headerMap.forEach { (key, value) ->
            builder.header(key, value)
        }
        return builder.url(url).build()
    }
}