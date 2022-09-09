package com.net.xhttp.config

import androidx.annotation.StringRes
import com.net.xhttp.R

enum class HttpError(val code: Int, @StringRes val message: Int) {
    // 未知错误
    UNKNOWN(-1, R.string.http_error_unknow),

    // 网络连接错误
    CONNECT_ERROR(-2, R.string.http_error_connect),

    // 连接超时
    CONNECT_TIMEOUT(-3, R.string.http_error_connect_timeout),

    // 错误的请求
    BAD_NETWORK(-4, R.string.http_error_bad_network),

    // 数据解析错误
    PARSE_ERROR(-5, R.string.http_error_parse),

    // 取消请求
    CANCEL_REQUEST(-6, R.string.http_cancel_request),
}
