package com.net.xhttp.bean

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
open class BaseResponse<T> : Serializable {
    @SerializedName(value = "code",alternate = ["errorCode","retcode"])
    var code: Int = 0

    @SerializedName(value = "msg", alternate = ["message","errorMsg"])
    var msg: String? = null

    @SerializedName(value = "value", alternate = ["data"])
    var value: T? = null

    fun isSuccessful() = code == 0
}