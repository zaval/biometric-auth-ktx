package com.github.zaval.biometricauthentificator

import android.content.Context
import android.content.SharedPreferences
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class BiometricEncryptedPreferences(
    private val title: String,
    private val subTitle: String,
    private val cancelText: String,
    private val server: String,
    private val context: Context
) {

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private val executor: Executor = Executors.newSingleThreadExecutor()

    private var failureReason: String? = null

    fun setupBiometricAccess(
        onSuccess: (SharedPreferences) -> Unit,
        onFailure: (String) -> Unit
    ) {
        failureReason = null
        if (!isBiometricAvailable()) {
            onFailure(failureReason ?: BiometricAuthErrorMessage.NOT_SUPPORTED.message)
            return
        }

        setupBiometricPrompt(onSuccess, onFailure)

        // Show the biometric prompt
        biometricPrompt.authenticate(promptInfo)
    }

    private fun setupBiometricPrompt(
        onSuccess: (SharedPreferences) -> Unit,
        onFailure: (String) -> Unit
    ) {
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subTitle)
            .setNegativeButtonText(cancelText)
            .build()

        biometricPrompt = BiometricPrompt(
            context as FragmentActivity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onFailure("${BiometricAuthErrorMessage.AUTH_ERROR.message}: $errString")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val encryptedSharedPreferences = createEncryptedSharedPreferences()
                    onSuccess(encryptedSharedPreferences)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailure(BiometricAuthErrorMessage.AUTH_FAILED.message)
                }
            }
        )
    }

    private fun createEncryptedSharedPreferences(): SharedPreferences {
        // Create a MasterKey that requires user authentication for access
        val masterKeyAlias = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .setUserAuthenticationRequired(true, 30) // Require biometric authentication every 30 seconds
            .build()

        return EncryptedSharedPreferences.create(
            context,
//            "secure_biometric_prefs",
            server
                .replace(".", "_")
                .replace("/", "_")
                .replace(":", "_"),
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                failureReason = BiometricAuthErrorMessage.NO_HARDWARE.message
                false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                failureReason = BiometricAuthErrorMessage.UNAVAILABLE_HARDWARE.message
                false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Prompts the user to create credentials that your app accepts.
//                if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
//                        putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
//                                 BIOMETRIC_STRONG)
//                    }
//                    (context as FragmentActivity).startActivityForResult(enrollIntent, REQUEST_CODE)
//                }
                failureReason = BiometricAuthErrorMessage.NOT_ENROLLED.message
                false
                }
            else -> false
        }
    }
}