package app.dating.appsuccessor.model

import java.io.Serializable

data class UserModel(
    val number: String? = null,
    val name: String? = null,
    val location: String? = null,
    val email: String? = null,
    val gender: String? = null,
    val relationship: String? = null,
    val star: String? = null,
    val images: ArrayList<String>? = null,
    val dob: String? = null,
    val story: String? = null,
    val fcmToken: String? = null,
    val status: String? = null,
    val lookingFor: String? = null,
    val likeToDate: String? = null,
    var locationRange: String? = null,
    val hideMyName: Boolean? = null,
    val rightSwiped: List<String>? = emptyList()
) : Serializable