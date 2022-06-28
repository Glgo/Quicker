package net.getquicker.net

import net.getquicker.bean.PushRequestBean
import net.getquicker.bean.ResultBean
import retrofit2.http.Body
import retrofit2.http.POST

interface HttpApi {
    @POST("to/quicker")
    suspend fun request(@Body param: PushRequestBean): ResultBean
}