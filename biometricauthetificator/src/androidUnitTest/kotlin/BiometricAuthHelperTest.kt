package com.github.zaval.biometricauthentificator

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import io.mockk.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class BiometricAuthHelperTest {

    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        sharedPreferences = mockk()
        sharedPreferencesEditor = mockk()

        every { sharedPreferences.edit() } returns sharedPreferencesEditor
        every { sharedPreferencesEditor.putString(any(), any()) } returns sharedPreferencesEditor
        every { sharedPreferencesEditor.apply() } just Runs
    }

    @Test
    fun `test BiometricAuthStorage getValue`() {
        val biometricAuthStorage = BiometricAuthStorage(sharedPreferences)
        every { sharedPreferences.getString("testKey", null) } returns "testValue"

        val result = biometricAuthStorage.getValue("testKey")

        assertEquals("testValue", result)
        verify { sharedPreferences.getString("testKey", null) }
    }

    @Test
    fun `test BiometricAuthStorage setValue`() {
        val biometricAuthStorage = BiometricAuthStorage(sharedPreferences)

        biometricAuthStorage.setValue("testKey", "testValue")

        verify {
            sharedPreferences.edit()
            sharedPreferencesEditor.putString("testKey", "testValue")
            sharedPreferencesEditor.apply()
        }
    }

    @Test
    fun `test BiometricAuthHelper authenticate success`() {
        val biometricEncryptedPreferences = mockk<BiometricEncryptedPreferences>()
        mockkConstructor(BiometricEncryptedPreferences::class)
        every {
            anyConstructed<BiometricEncryptedPreferences>().setupBiometricAccess(
                onSuccess = any(),
                onFailure = any()
            )
        } answers {
            val onSuccess = firstArg<(SharedPreferences) -> Unit>()
            onSuccess(sharedPreferences)
        }

        val biometricAuthHelper = BiometricAuthHelper(
            title = "Test Title",
            subTitle = "Test Subtitle",
            cancelText = "Cancel",
            server = "Test Server",
            context = context
        )

        var successCalled = false
        biometricAuthHelper.authenticate(
            onFailure = { },
            onSuccess = { biometricAuthStorage ->
                successCalled = true
                assertEquals(sharedPreferences, biometricAuthStorage.SharedPreference)
            }
        )

        assert(successCalled)
        verify {
            anyConstructed<BiometricEncryptedPreferences>().setupBiometricAccess(
                onFailure = any(),
                onSuccess = any()
            )
        }
    }

    @Test
    fun `test BiometricAuthHelper authenticate failure`() {
        val biometricEncryptedPreferences = mockk<BiometricEncryptedPreferences>()
        mockkConstructor(BiometricEncryptedPreferences::class)
        every {
            anyConstructed<BiometricEncryptedPreferences>().setupBiometricAccess(
                onSuccess = any(),
                onFailure = any(),
            )
        } answers {
            val onFailure = secondArg<(String) -> Unit>()
            onFailure("Authentication failed")
        }

        val biometricAuthHelper = BiometricAuthHelper(
            title = "Test Title",
            subTitle = "Test Subtitle",
            cancelText = "Cancel",
            server = "Test Server",
            context = context
        )

        var failureCalled = false
        biometricAuthHelper.authenticate(
            onFailure = { error ->
                failureCalled = true
                assertEquals("Authentication failed", error)
            },
            onSuccess = { }
        )

        assert(failureCalled)
        verify {
            anyConstructed<BiometricEncryptedPreferences>().setupBiometricAccess(
                onFailure = any(),
                onSuccess = any()
            )
        }
    }
}