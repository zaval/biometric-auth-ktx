package com.github.zaval.biometricauthentificator

import androidx.compose.runtime.*


enum class BiometricAuthErrorMessage(val message: String){
    NOT_SUPPORTED("Biometric authentication is not available on this device."),
    AUTH_ERROR("Authentication error: "),
    AUTH_FAILED("Authentication failed."),
    NO_HARDWARE("No biometric hardware found."),
    UNAVAILABLE_HARDWARE("Biometric hardware is unavailable."),
    NOT_ENROLLED("No biometric credentials are enrolled.")
}


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class BiometricAuthStorage{
    fun getValue(key: String): String?
    fun setValue(key: String, value: String)
}


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class BiometricAuthHelper{
    fun authenticate(onFailure: (String) -> Unit, onSuccess: (BiometricAuthStorage) -> Unit)
    fun isAvailable(): Boolean

    val title: String
    val subTitle: String
    val cancelText: String
    val server: String

}

@Composable
expect fun rememberBiometricAuthHelper(
    title: String = "Biometric Authentication",
    subTitle: String = "Authenticate to access secure data",
    cancelText: String = "Cancel",
    server: String = "zaval.github.io",
): BiometricAuthHelper

@Composable
fun rememberBiometricAuthStorage(
    title: String = "Biometric Authentication",
    subTitle: String = "Authenticate to access secure data",
    cancelText: String = "Cancel",
    server: String = "zaval.github.io",
    onFailure: (String) -> Unit = {}
): BiometricAuthStorage? {
    val biometricAuthHelper = rememberBiometricAuthHelper(title, subTitle, cancelText, server)
    var biometricAuthStorage: BiometricAuthStorage? by remember { mutableStateOf(null)  }
    LaunchedEffect(biometricAuthHelper){
        if (!biometricAuthHelper.isAvailable()) {
            biometricAuthStorage = null
            onFailure(BiometricAuthErrorMessage.NOT_SUPPORTED.message)
            return@LaunchedEffect
        }
        biometricAuthHelper.authenticate(
            onFailure = {
                biometricAuthStorage = null
                onFailure(it)
                        },
            onSuccess = {
                biometricAuthStorage = it
            }
        )
    }

    return biometricAuthStorage
}