package app.dating.appsuccessor.notification.api

import app.dating.appsuccessor.notification.model.PushNotification
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {

    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAA0py9h_0:APA91bFJY9Fle3a57_fYT-3J4ujgxw1aDNP7HWdgYAjMS25wDT6f1wuK1o-d8i2R8XWKO85rk8TQ1szHleiRh67X0lwkJQFfdnnY-tHIGhkds8J28_xevPiI7zGrWRdfXdiy0f29pRT_"
    )

    @POST("fcm/send")
    fun sendNotification(@Body notification: PushNotification)
            : Call<PushNotification>
}