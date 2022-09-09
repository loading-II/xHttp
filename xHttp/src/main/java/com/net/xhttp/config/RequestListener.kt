package com.net.xhttp.config


interface RequestListener<in T : Any> {
    suspend fun beforeRequest()
    suspend fun afterRequest()
    suspend fun onSuccess(data: T?)
    suspend fun onError(ex: Throwable, error: HttpError)
    suspend fun onFailure(code: Int, msg: String?)
}

interface SimpleRequestListener<in T : Any> : RequestListener<T> {
    override suspend fun beforeRequest() {}

    override suspend fun afterRequest() {}

    override suspend fun onSuccess(data: T?)

    override suspend fun onError(ex: Throwable, error: HttpError) {}

    override suspend fun onFailure(code: Int, msg: String?)

}
