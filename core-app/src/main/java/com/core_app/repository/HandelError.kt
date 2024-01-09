package com.core_app.repository

import androidx.core.util.Consumer
import com.core_app.extension.value
import kotlinx.coroutines.flow.MutableSharedFlow
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class HandelError(private val callbackError: MutableSharedFlow<Pair<ErrorType, String>>? = null) :
    Consumer<Throwable> {
    override fun accept(throwable: Throwable) {
        throwable.printStackTrace()
        callbackError?.tryEmit(getError(throwable))
    }

    fun getError(throwable: Throwable): Pair<ErrorType, String> {
        throwable.printStackTrace()
        if (throwable is IOException ||
            throwable is SocketTimeoutException ||
            throwable is UnknownHostException ||
            throwable is ConnectException
        ) {
            return Pair(ErrorType.INTERNET, "")
        } else {
            if (throwable is HttpException) {
                try {
                    val jsonObject =
                        JSONObject(throwable.response()?.errorBody()?.string().value())
                    if (jsonObject.has("status_message")) {
                        val mgs = jsonObject.getString("status_message")
                        if (mgs.isNotEmpty()) {
                            return Pair(ErrorType.FROM_API, mgs)
                        }
                    }
                } catch (e: Exception) {
                    throwable.printStackTrace()
                }
                return Pair(ErrorType.UNKNOW, "")
            }
            return Pair(ErrorType.UNKNOW, "")
        }
    }
}