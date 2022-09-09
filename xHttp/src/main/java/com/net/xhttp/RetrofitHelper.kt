package com.net.xhttp

import android.net.ParseException
import android.util.Log
import android.util.LruCache
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.itkacher.okhttpprofiler.OkHttpProfilerInterceptor
import com.google.gson.Gson
import com.net.xhttp.config.HttpError
import com.net.xhttp.config.SimpleRequestListener
import com.net.xhttp.bean.BaseResponse
import com.net.xhttp.bean.RequestResult
import com.net.xhttp.bean.RequestState
import com.net.xhttp.typeadapter.*
import kotlinx.coroutines.flow.MutableSharedFlow
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit


class RetrofitHelper private constructor() {

    private var serviceCache: LruCache<String, Any>
    private val DEFAULT_CONNECT_TIMEOUT_MILLS = 10L //默认联系超时时长
    private val DEFAULT_READ_TIMEOUT_MILLS = 10L //默认写超时时长
    private val DEFAULT_WRITE_TIMEOUT_MILLS = 10L //默认读操作超时时间
    private val SERVICE_CACHE_COUNT = 20 // 最多缓存的service数量
    private var baseUrl: String? = null
    private var interceptorArray: MutableList<Interceptor> = ArrayList()
    private lateinit var retrofit: Retrofit
    private var isDebug: Boolean = false
    private val TAG = "OkHttp"

    companion object {
        val instance: RetrofitHelper by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RetrofitHelper()
        }
    }

    init {
        serviceCache = LruCache(SERVICE_CACHE_COUNT)
    }

    fun setBaseUrl(url: String) {
        baseUrl = url
    }

    fun addToken(key: String, value: String) {

    }

    fun addInterceptor(interceptor: Interceptor) {
        interceptorArray.add(interceptor)
    }

    fun isDebug(isDebug: Boolean) {
        this.isDebug = isDebug
    }

    private fun getHttpClient(): OkHttpClient {
        var builder = OkHttpClient.Builder()
        builder.apply {
            connectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLS, TimeUnit.SECONDS)
            readTimeout(DEFAULT_READ_TIMEOUT_MILLS, TimeUnit.SECONDS)
            writeTimeout(DEFAULT_WRITE_TIMEOUT_MILLS, TimeUnit.SECONDS)
            if (interceptorArray.size > 0) {
                interceptorArray.forEachIndexed { _, interceptor ->
                    addInterceptor(interceptor)
                }
            }
            if (isDebug) {
                addInterceptor(OkHttpProfilerInterceptor())
            }
            val httpLoggerIntercepter = HttpLoggingInterceptor()
            httpLoggerIntercepter.level = HttpLoggingInterceptor.Level.BODY
            addInterceptor(httpLoggerIntercepter)
        }
        return builder.build()
    }

    private fun getGsonConverterFactory(): GsonConverterFactory {
        val gson = GsonBuilder() //配置你的Gson
            .setDateFormat("yyyy-MM-dd hh:mm:ss")
            .registerTypeAdapter(Int::class.java, IntegerTypeAdapter())
            .registerTypeAdapter(Double::class.java, DoubleTypeAdapter())
            .registerTypeAdapter(Long::class.java, LongTypeAdapter())
            .registerTypeAdapter(Float::class.java, FloatTypeAdapter())
            .registerTypeAdapter(String::class.java, StringTypeAdapter())
            .serializeNulls()//支持序列化null的参数
            .enableComplexMapKeySerialization()//支持将序列化key为object的map,默认只能序列化key为st
            .create()
        return GsonConverterFactory.create(gson)
    }

    /**
     * 获取service对象
     *
     * @param service api所在接口类
     */
    fun <T> getService(service: Class<T>): T {
        var retrofitService: T? = serviceCache.get(service.canonicalName) as T
        if (retrofitService == null) {
            retrofitService = retrofit.create(service)
            serviceCache.put(service.canonicalName, retrofitService)
        }
        return retrofitService!!
    }


    /**
     * 解析网络请求异常
     */
    private fun parseException(e: Throwable): HttpError {
        return when (e) {
            is HttpException -> HttpError.BAD_NETWORK
            is ConnectException, is UnknownHostException -> HttpError.CONNECT_ERROR
            is InterruptedIOException -> HttpError.CONNECT_TIMEOUT
            is JsonParseException, is JSONException, is ParseException, is ClassCastException -> HttpError.PARSE_ERROR
            is CancellationException -> HttpError.CANCEL_REQUEST
            else -> HttpError.UNKNOWN
        }
    }


    fun builder(): RetrofitHelper {
        if (baseUrl.isNullOrEmpty()) {
            return throw IllegalStateException("Your Retrofit BaseUrl is null,please init it………… ")
        }

        baseUrl?.let {
            retrofit = Retrofit.Builder()
                .baseUrl(it)
                .client(getHttpClient())
                .addConverterFactory(getGsonConverterFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        }
        return instance
    }

    /**
     * 获取 retrofit 供外部调用，进行最直接的网络请求
     */
    fun getRetrofit(): Retrofit {
        return retrofit
    }

    /**
     * 建议调用此方法发送网络请求
     * 因为协程中出现异常时，会直接抛出异常，所以使用try...catch方法捕获异常
     */
    suspend fun <T : Any, D : Any> requestSafely(
        apiInterface: Class<T>, flow: MutableSharedFlow<RequestResult<D>>,
        call: suspend (T) -> BaseResponse<D>
    ) {
        flow.emit(RequestResult(RequestState.OnLoading))
        try {
            val s = getService(apiInterface as Class<T>)

            val response = call(s)
            if (response.isSuccessful()) {
                flow.emit(RequestResult(RequestState.OnSuccess, code = response.code, data = response.value))
            } else {
                flow.emit(RequestResult(RequestState.OnFail, code = response.code, error = "${response.msg}"))
            }

        } catch (ex: Throwable) {
            flow.emit(RequestResult(RequestState.OnFail, code = -1, error = "${ex.message}"))
        }
    }


    /**
     * 建议调用此方法发送网络请求
     * 因为协程中出现异常时，会直接抛出异常，所以使用try...catch方法捕获异常
     */
    suspend fun <T : Any, D : Any> requestSafely(
        apiInterface: Class<T>, simpleRequestListener: SimpleRequestListener<D>, call: suspend (T) -> BaseResponse<D>
    ) {
        simpleRequestListener.beforeRequest()
        try {
            val s = getService(apiInterface as Class<T>)

            val response = call(s)
            if (response.isSuccessful()) {
                simpleRequestListener.onSuccess(response.value)
                simpleRequestListener.afterRequest()
            } else {
                simpleRequestListener.onFailure(response.code, response.msg)
                simpleRequestListener.afterRequest()
            }
        } catch (ex: Throwable) {
            if (isDebug) {
                Log.e(TAG, "requestSafely: ${ex.message}")
            }
            simpleRequestListener.onError(ex, parseException(ex))
            simpleRequestListener.afterRequest()
        }

    }

    /**
     * 公共函数:getRequestBody
     */
    fun getRequestBody(params: HashMap<String, Any>): RequestBody {
        val bodyMap = HashMap<String, Any>()
        //bodyMap["Device"] = "android"
        //bodyMap["App"] = "appName"
        //其他参数
        bodyMap.putAll(params)
        return Gson().toJson(bodyMap).toRequestBody("application/json;charset=utf-8".toMediaTypeOrNull())
    }


}