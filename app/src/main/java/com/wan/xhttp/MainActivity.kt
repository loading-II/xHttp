package com.wan.xhttp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.net.xhttp.async
import com.net.xhttp.bindLifeCycle
import com.net.xhttp.bean.RequestResult
import com.net.xhttp.config.SimpleRequestListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initListener()
    }


    private fun initListener() {
        lifecycleScope.launchWhenCreated {
            launch {
                mBannerDataState.asSharedFlow().distinctUntilChanged().collect {
                    Log.e(TAG, "initListener: $it")
                }
            }
        }

    }

    //=======================以下是简单示例=======================
    fun getBannerData(view: View) {
        Log.e(TAG, "getBannerData: ====")
        getBannerByFlow()
//        getArticleByRxjaav()
//        getBannerByCoroutines()
    }

    val mBannerDataState = MutableSharedFlow<RequestResult<List<BannerEntity>>>()
    private fun getBannerByFlow() {
        CoroutineScope(Dispatchers.IO).launch {
            WanRepository.instance.getHomeBannerDataByMvi(mBannerDataState)
        }
    }

    private fun getArticleByRxjaav() {
        WanRepository.instance.getArticleList(0)
            .async({}, {})
            .bindLifeCycle(this)
            .subscribe({
                Log.e(TAG, "onLoadData: ${it}")
            }, {
                Log.e(TAG, "onLoadData: ${it.message}")
            })
    }

    private fun getBannerByCoroutines() {
        CoroutineScope(Dispatchers.IO).launch {
            WanRepository.instance.getHomeBannerData(object : SimpleRequestListener<List<BannerEntity>> {
                override suspend fun onSuccess(data: List<BannerEntity>?) {
                    Log.e(TAG, "onSuccess: $data")
                }

                override suspend fun onFailure(code: Int, msg: String?) {
                    Log.e(TAG, "onFailure: $msg")
                }

            })
        }
    }


}