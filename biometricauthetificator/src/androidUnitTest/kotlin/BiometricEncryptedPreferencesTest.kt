package com.github.zaval.biometricauthentificator

import android.content.Context
import android.content.SharedPreferences
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricViewModel
import androidx.fragment.app.FragmentActivity
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.Executor

@RunWith(RobolectricTestRunner::class)
class BiometricEncryptedPreferencesTest{

    private lateinit var context: Context
    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var executor: Executor
    private lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setup() {
        fragmentActivity = Robolectric.buildActivity(FragmentActivity::class.java).setup().get()
        context = fragmentActivity
        executor = mockk()
        sharedPreferences = mockk()

        mockkConstructor(BiometricManager::class)
        mockkConstructor(BiometricPrompt::class)
        mockkConstructor(BiometricViewModel::class)
        every { anyConstructed<BiometricManager>().canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) } returns BiometricManager.BIOMETRIC_SUCCESS
        every {
            anyConstructed<BiometricPrompt>().authenticate(any())
        } returns Unit

    }

    @Test
    fun `test setupBiometricAccess when biometric is no hardware`(){
        val biometricEncryptedPreferences = BiometricEncryptedPreferences(
            title = "Test Title",
            subTitle = "Test Subtitle",
            cancelText = "Cancel",
            server = "Test Server",
            context = context
        )

        every { anyConstructed<BiometricManager>().canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) } returns BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
        var failureReason: String? = null
        biometricEncryptedPreferences.setupBiometricAccess(
            onSuccess = { },
            onFailure = { failureReason = it }
        )
        assert(failureReason == BiometricAuthErrorMessage.NO_HARDWARE.message)
    }

    @Test
    fun `test setupBiometricAccess when biometric is unavailable hardware`(){
        val biometricEncryptedPreferences = BiometricEncryptedPreferences(
            title = "Test Title",
            subTitle = "Test Subtitle",
            cancelText = "Cancel",
            server = "Test Server",
            context = context
        )

        every { anyConstructed<BiometricManager>().canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) } returns BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE
        var failureReason: String? = null
        biometricEncryptedPreferences.setupBiometricAccess(

            onSuccess = { },
            onFailure = { failureReason = it }
        )
        assert(failureReason == BiometricAuthErrorMessage.UNAVAILABLE_HARDWARE.message)
    }

    @Test
    fun `test setupBiometricAccess when biometric not enrolled`(){
        val biometricEncryptedPreferences = BiometricEncryptedPreferences(
            title = "Test Title",
            subTitle = "Test Subtitle",
            cancelText = "Cancel",
            server = "Test Server",
            context = context
        )

        every { anyConstructed<BiometricManager>().canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) } returns BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
        var failureReason: String? = null
        biometricEncryptedPreferences.setupBiometricAccess(
            onSuccess = { },
            onFailure = { failureReason = it }
        )
        print(failureReason)
        assert(failureReason == BiometricAuthErrorMessage.NOT_ENROLLED.message)
    }

    @Test
    fun `test setupBiometricAccess when biometric is available`(){

        every { anyConstructed<BiometricManager>().canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) } returns BiometricManager.BIOMETRIC_SUCCESS

        val biometricEncryptedPreferences = BiometricEncryptedPreferences(
            title = "Test Title",
            subTitle = "Test Subtitle",
            cancelText = "Cancel",
            server = "Test Server",
            context = context
        )

        biometricEncryptedPreferences.setupBiometricAccess(
            onSuccess = { },
            onFailure = { }
        )

        verify { anyConstructed<BiometricPrompt>().authenticate(any()) }

    }
}