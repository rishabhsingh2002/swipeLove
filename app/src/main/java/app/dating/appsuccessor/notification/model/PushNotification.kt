package app.dating.appsuccessor.notification.model

data class PushNotification(
    val data: NotificationData,
    val to: String? = ""
)