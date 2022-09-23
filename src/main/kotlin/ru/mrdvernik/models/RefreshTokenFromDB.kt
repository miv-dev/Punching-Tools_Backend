package ru.mrdvernik.models

import java.util.*

data class RefreshTokenFromDB(val uuid: UUID, val refreshToken: String, val expiresAt: Long)
