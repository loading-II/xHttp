package com.wan.xhttp

import android.app.Application
import com.net.xhttp.HttpHelper
import com.net.xhttp.MoreBaseUrlHandler

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initHttp()
    }

    private fun initHttp() {
        HttpHelper.getInstance()
            .isDebug(BuildConfig.DEBUG)
            .setBaseUrl("https://www.wanandroid.com/")
            .setDebugBaseUrl("https://www.wanandroid.com/")
            .addBaseUrl("baidu", "https://www.baidu.com/")
            .addBaseUrl("sohu", "https://www.sohu.com/")
            .addRequestHandler(MyRequestHandler())
            .addRequestHandler(MoreBaseUrlHandler())
            .build()
    }


}