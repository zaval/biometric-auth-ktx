package com.github.zaval.biometricauthentificator

import androidx.compose.runtime.Composable


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