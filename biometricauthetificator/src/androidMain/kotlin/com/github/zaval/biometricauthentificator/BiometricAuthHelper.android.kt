package com.github.zaval.biometricauthentificator

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class BiometricAuthStorage(val SharedPreference: SharedPreferences){
    actual fun getValue(key: String): String? {
        return SharedPreference.getString(key, null)
    }

    actual fun setValue(key: String, value: String) {
        SharedPreference.edit().putString(key, value).apply()
    }

}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class BiometricAuthHelper(
    actual val title: String,
    actual val subTitle: String,
    actual val cancelText: String,
    actual val server: String,
    val context: Context
) {

    private val biometricEncryptedPreferences = BiometricEncryptedPreferences(
        title,
        subTitle,
        cancelText,
        server,
        context
    )

    actual fun authenticate(onFailure: (String) -> Unit, onSuccess: (BiometricAuthStorage) -> Unit) {

        biometricEncryptedPreferences.setupBiometricAccess(
            onFailure = onFailure,
            onSuccess = {sharedPreferences ->
                onSuccess(BiometricAuthStorage(sharedPreferences))
            }
        )

    }

    actual fun isAvailable(): Boolean {
        return biometricEncryptedPreferences.isBiometricAvailable()
    }

}

@Composable
actual fun rememberBiometricAuthHelper(
    title: String,
    subTitle: String,
    cancelText: String,
    server: String,
): BiometricAuthHelper{
    val context = LocalContext.current
    return remember {
        BiometricAuthHelper(
            title = title,
            subTitle = subTitle,
            cancelText = cancelText,
            server = server,
            context = context
        )
    }
}