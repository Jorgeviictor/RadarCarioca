package com.radarcarioca.domain.model

data class UserProfile(
    val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String = "",
    val role: UserRole = UserRole.FREE,
    val testerGrantedBy: String? = null,
    val testerExpiresAt: Long? = null
) {
    val canAccessPremiumFeatures: Boolean
        get() = role.hasPrivilegedAccess
}
