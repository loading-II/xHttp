package com.net.xhttp.config

sealed class ParseResult<out T : Any> {

    /* 网络请求前，可以操作loading相关UI  */
    data class BeforeRequest<out T : Any>(val data: T?) : ParseResult<T>()

    /* 请求成功，返回成功响应  */
    data class Success<out T : Any>(val data: T?) : ParseResult<T>()

    /* 网络请求前，可以操作loading相关UI  */
    data class AfterRequest<out T : Any>(val data: T?) : ParseResult<T>()

    /* 请求成功，返回失败响应 */
    data class Failure(val code: Int, var msg: String? = null) :
        ParseResult<Nothing>()

    /* 请求失败，抛出异常 */
    data class ERROR(val ex: Throwable, val error: HttpError) : ParseResult<Nothing>()

    private var successBlock: (suspend (data: T?) -> Unit)? = null
    private var failureBlock: (suspend (code: Int, msg: String?) -> Unit)? = null
    private var errorBlock: (suspend (ex: Throwable, error: HttpError) -> Unit)? = null
    private var cancelBlock: (suspend () -> Unit)? = null
    private var beforeBlock: (suspend (data: T?) -> Unit)? = null
    private var afterBlock: (suspend (data: T?) -> Unit)? = null


    /**
     * 设置网络请求前的操作UI-loading相关
     */
    fun doBeforeRequest(beforeRequest: (suspend (data: T?) -> Unit)?): ParseResult<T> {
        this.beforeBlock = beforeRequest
        return this
    }

    /**
     * 设置网络请求后的操作UI-loading相关
     */
    fun doAfterRequest(afterTerminate: (suspend (data: T?) -> Unit)?): ParseResult<T> {
        this.afterBlock = afterTerminate
        return this
    }

    /**
     * 设置网络请求成功处理
     */
    fun doSuccess(successBlock: (suspend (data: T?) -> Unit)?): ParseResult<T> {
        this.successBlock = successBlock
        return this
    }

    /**
     * 设置网络请求失败处理
     */
    fun doFailure(failureBlock: (suspend (code: Int, msg: String?) -> Unit)?): ParseResult<T> {
        this.failureBlock = failureBlock
        return this
    }

    /**
     * 设置网络请求异常处理
     */
    fun doError(errorBlock: (suspend (ex: Throwable, error: HttpError) -> Unit)?): ParseResult<T> {
        this.errorBlock = errorBlock
        return this
    }

    /**
     * 设置网络请求取消处理
     */
    fun doCancel(cancelBlock: (suspend () -> Unit)?): ParseResult<T> {
        this.cancelBlock = cancelBlock
        return this
    }

    suspend fun execute() {
        when (this) {
            is BeforeRequest<T> -> beforeBlock?.invoke(data)
            is Success<T> -> successBlock?.invoke(data)
            is AfterRequest<T> -> afterBlock?.invoke(data)
            is Failure -> failureBlock?.invoke(code, msg)
            is ERROR -> {
                if (this.error == HttpError.CANCEL_REQUEST) {
                    cancelBlock?.invoke()
                } else {
                    errorBlock?.invoke(ex, error)
                }
            }
        }
    }
}