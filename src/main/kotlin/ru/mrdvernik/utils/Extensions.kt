package ru.mrdvernik.utils

import java.time.Duration

fun Long.withOffset(offset: Duration) = this + offset.toMillis()
