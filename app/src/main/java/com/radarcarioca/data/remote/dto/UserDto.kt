package com.radarcarioca.data.remote.dto

import com.google.firebase.firestore.PropertyName
import com.radarcarioca.domain.model.UserProfile
import com.radarcarioca.domain.model.UserRole

data class UserDto(
    val uid: String = "",
    val email: String = "",
    @get:PropertyName("display_name") @set:PropertyName("display_name")
    var displayName: String = "",
    @get:PropertyName("photo_url") @set:PropertyName("photo_url")
    var photoUrl: String = "",
    val role: String = "FREE",
    @get:PropertyName("created_at") @set:PropertyName("created_at")
    var createdAt: Long = 0L,
    @get:PropertyName("tester_granted_by") @set:PropertyName("tester_granted_by")
    var testerGrantedBy: String? = null,
    @get:PropertyName("tester_expires_at") @set:PropertyName("tester_expires_at")
    var testerExpiresAt: Long? = null
) {
    fun toDomain() = UserProfile(
        uid = uid,
        email = email,
        displayName = displayName,
        photoUrl = photoUrl,
        role = runCatching { UserRole.valueOf(role) }.getOrDefault(UserRole.FREE),
        testerGrantedBy = testerGrantedBy,
        testerExpiresAt = testerExpiresAt
    )
}
