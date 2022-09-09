package com.wan.xhttp

import android.util.Log
import com.net.xhttp.interceptors.RequestHandler
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class MyRequestHandler : RequestHandler {

    private val TAG = "Guoql"

    override fun onBeforeRequest(request: Request, chain: Interceptor.Chain): Request {
        val original = chain.request()
        val url = original.url
        Log.e(TAG, "onBeforeRequest: ======${System.currentTimeMillis()}")
        return addHeader(chain, HashMap(), url)
    }

    override fun onAfterRequest(response: Response, chain: Interceptor.Chain): Response {

        return response
    }

    private fun addHeader(chain: Interceptor.Chain, headerMap: MutableMap<String, String>, url: HttpUrl): Request {
        val builder = chain.request().newBuilder()
        headerMap.forEach { (key, value) ->
            builder.header(key, value)
        }
        return builder.url(url).build()
    }
}