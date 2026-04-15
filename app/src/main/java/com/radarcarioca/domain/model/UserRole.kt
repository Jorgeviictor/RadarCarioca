package com.radarcarioca.domain.model

enum class UserRole {
    ADMIN_MASTER,
    TESTER,
    PREMIUM,
    FREE;

    /** Admin e Tester têm acesso completo sem assinatura */
    val hasPrivilegedAccess: Boolean
        get() = this == ADMIN_MASTER || this == TESTER
}
