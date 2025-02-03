package com.github.zaval.biometricauthentificator

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.CoreFoundation.*

@OptIn(ExperimentalForeignApi::class)
fun String.toCFString(): CFStringRef? = CFStringCreateWithCString(null, this, kCFStringEncodingUTF8)

@OptIn(ExperimentalForeignApi::class)
fun ByteArray.toCFData(): CFDataRef? {
    return this.usePinned { pinned ->
        CFDataCreate(
            null, // Allocator (null uses default allocator)
            pinned.addressOf(0).reinterpret(), // Pointer to the data
            this.size.toLong() // Length of the data
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
fun Boolean.toCFBoolean(): CFBooleanRef? = if (this) kCFBooleanTrue else kCFBooleanFalse