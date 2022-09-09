package com.net.xhttp.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


class NetInterceptor(private val handler: RequestHandler?) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        handler?.let {
            request = handler.onBeforeRequest(request, chain)
        }
        return chain.proceed(request)
    }
}
