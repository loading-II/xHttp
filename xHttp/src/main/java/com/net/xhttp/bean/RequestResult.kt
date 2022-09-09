package com.net.xhttp.bean

data class RequestResult<D : Any>(
    val state: RequestState,
    val data: D? = null,
    val code: Int? = null,
    val error: String? = null,
    val tag: Any? = null
)

sealed class RequestState {
    object OnLoading : RequestState()
    object OnSuccess : RequestState()
    object OnFail : RequestState()
}
