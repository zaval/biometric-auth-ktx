package com.github.zaval.biometricauthentificator

import androidx.compose.runtime.Composable
import kotlinx.cinterop.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.CoreFoundation.*
import platform.Foundation.NSError
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthentication
import platform.Security.*
import androidx.compose.runtime.remember


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class BiometricAuthHelper(
    actual val title: String,
    actual val subTitle: String,
    actual val cancelText: String,
    actual val server: String,
) {

    private val mainScope = CoroutineScope(Dispatchers.Main)
    private val context = LAContext()

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual fun authenticate(onFailure: (String) -> Unit, onSuccess: (BiometricAuthStorage) -> Unit) {
        mainScope.launch {

            val errorPtr = nativeHeap.alloc<ObjCObjectVar<NSError?>>()
            context.touchIDAuthenticationAllowableReuseDuration = 30.0
            if (context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthentication, error = errorPtr.ptr)) {
//                val reason = "Authenticate to access the app"
                val reason = "$title\n$subTitle"
                context.evaluatePolicy(
                    LAPolicyDeviceOwnerAuthentication,
                    localizedReason = reason,
                ){success, error ->

                    if (success) {
                        onSuccess(BiometricAuthStorage(server, context))
                    } else {
                        onFailure(error?.localizedDescription ?: BiometricAuthErrorMessage.AUTH_ERROR.message)
                        println(errorPtr.value)
                    }
                    nativeHeap.free(errorPtr)
                }
            } else {
                onFailure(BiometricAuthErrorMessage.NOT_SUPPORTED.message)
            }

        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun isAvailable(): Boolean {
        return context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthentication, error = null)
    }

}

@Composable
actual fun rememberBiometricAuthHelper(
    title: String,
    subTitle: String,
    cancelText: String,
    server: String,
): BiometricAuthHelper{
    return remember {
        BiometricAuthHelper(
            title = title,
            subTitle = subTitle,
            cancelText = cancelText,
            server = server,
        )
    }
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class BiometricAuthStorage(
    val server: String,
    val context: LAContext
) {
    @OptIn(ExperimentalForeignApi::class)
    actual fun getValue(key: String): String? {

        val queryDict = CFDictionaryCreateMutable(null, 0, null, null)
        CFDictionaryAddValue(queryDict, kSecClass, kSecClassInternetPassword)
        CFDictionaryAddValue(queryDict, kSecAttrServer, server.toCFString())
        CFDictionaryAddValue(queryDict, kSecAttrAccount, key.toCFString())
        CFDictionaryAddValue(queryDict, kSecMatchLimit, kSecMatchLimitOne)
        CFDictionaryAddValue(queryDict, kSecReturnAttributes, true.toCFBoolean())
        val contextPtr = context.objcPtr()
        val contextRef = interpretCPointer<COpaquePointerVar>(contextPtr)
        CFDictionaryAddValue(queryDict, kSecUseAuthenticationContext, contextRef)
        CFDictionaryAddValue(queryDict, kSecReturnData, true.toCFBoolean())

        val resultPtr = nativeHeap.alloc<CFTypeRefVar>()

        val status = SecItemCopyMatching(queryDict, resultPtr.ptr)
        CFRelease(queryDict)
        if (status == errSecSuccess) {

            @Suppress("UNCHECKED_CAST")
            val existingItem = resultPtr.value as? CFDictionaryRef

            if (existingItem!= null){
                @Suppress("UNCHECKED_CAST") val secretData = CFDictionaryGetValue(existingItem, kSecValueData) as? CFDataRef
                @Suppress("UNCHECKED_CAST") val account = CFDictionaryGetValue(existingItem, kSecAttrAccount) as? CFStringRef
                if (secretData!= null && account!= null){
                    val secretBytes = CFDataGetBytePtr(secretData)
                    val secretLength = CFDataGetLength(secretData)

                    // Create a ByteArray from the raw bytes
                    val byteArray = ByteArray(secretLength.convert()) { index ->
                        secretBytes!![index].toByte()
                    }

                    // Convert the ByteArray to a Kotlin String
                    val secretValue = byteArray.decodeToString()

                    // Return the password (or use it as needed)
                    return secretValue
                } else {
                    println("Error: passwordData or account is null")
                }

            } else {
                println("Error: existingItem is null")
            }

        } else {
            println("Error: SecItemCopyMatching failed with status $status")
        }
        nativeHeap.free(resultPtr)

        return null
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun setValue(key: String, value: String) {
        val access = SecAccessControlCreateWithFlags(
            null,
            kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly,
            1u,
            null
        )

        val queryDict = CFDictionaryCreateMutable(null, 0, null, null)
        CFDictionaryAddValue(queryDict, kSecClass, kSecClassInternetPassword)
        CFDictionaryAddValue(queryDict, kSecAttrAccount, key.toCFString())
        CFDictionaryAddValue(queryDict, kSecAttrServer, server.toCFString())
        CFDictionaryAddValue(queryDict, kSecAttrAccessControl, access)
        val contextPtr = context.objcPtr()
        val contextRef = interpretCPointer<COpaquePointerVar>(contextPtr)
        CFDictionaryAddValue(queryDict, kSecUseAuthenticationContext, contextRef)
        CFDictionaryAddValue(queryDict, kSecValueData, value.encodeToByteArray().toCFData())

        val status = SecItemAdd(queryDict, null)
        if (status != errSecSuccess) {
            println("Error: SecItemAdd failed with status $status")
        }
    }
}