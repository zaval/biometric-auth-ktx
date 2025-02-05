package com.github.zaval.biometricauthentificator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricViewModel
import androidx.fragment.app.FragmentActivity
import androidx.test.core.app.ApplicationProvider
import io.mockk.*
import org.junit.Before

import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.Executor
import kotlin.test.Test

//Calculating task graph as no cached configuration is available for tasks: :biometricauthetificator:cleanTest :biometricauthetificator:cleanTestDebugUnitTest :biometricauthetificator:testDebugUnitTest --tests com.github.zaval.biometricauthentificator.BiometricEncryptedPreferencesTest.test setupBiometricAccess when biometric is no hardware

@Config(manifest=Config.NONE)
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
//        context = ApplicationProvider.getApplicationContext<FragmentActivity>()
        executor = mockk()
        sharedPreferences = mockk()

    }

    @Test
    fun `test setupBiometricAccess when biometric is no hardware`(){

        mockkConstructor(BiometricManager::class){
            mockkConstructor(BiometricPrompt::class){
                mockkConstructor(BiometricViewModel::class){
                    every { anyConstructed<BiometricManager>().canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) } returns BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
                    every {
                        anyConstructed<BiometricPrompt>().authenticate(any())
                    } returns Unit

                    val biometricEncryptedPreferences = BiometricEncryptedPreferences(
                        title = "Test Title",
                        subTitle = "Test Subtitle",
                        cancelText = "Cancel",
                        server = "Test Server",
                        context = context
                    )

                    var failureReason: String? = null
                    biometricEncryptedPreferences.setupBiometricAccess(
                        onSuccess = { println(it) },
                        onFailure = {
                            println(it)
                            failureReason = it
                        }
                    )
                    assert(failureReason == BiometricAuthErrorMessage.NO_HARDWARE.message)
                }
            }
        }

    }

    @Test
    fun `test setupBiometricAccess when biometric is unavailable hardware`(){

        mockkConstructor(BiometricManager::class){
            mockkConstructor(BiometricPrompt::class){
                mockkConstructor(BiometricViewModel::class){
                    every { anyConstructed<BiometricManager>().canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) } returns BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE

                    val biometricEncryptedPreferences = BiometricEncryptedPreferences(
                        title = "Test Title",
                        subTitle = "Test Subtitle",
                        cancelText = "Cancel",
                        server = "Test Server",
                        context = context
                    )

                    var failureReason: String? = null
                    biometricEncryptedPreferences.setupBiometricAccess(
                        onSuccess = { println(it)},
                        onFailure = {
                            println(it)
                            failureReason = it
                        }
                    )
                    assert(failureReason == BiometricAuthErrorMessage.UNAVAILABLE_HARDWARE.message)
                }
            }
        }



    }

    @Test
    fun `test setupBiometricAccess when biometric not enrolled`(){

        mockkConstructor(BiometricManager::class){
            mockkConstructor(BiometricPrompt::class){
                mockkConstructor(BiometricViewModel::class){
                    every { anyConstructed<BiometricManager>().canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) } returns BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED

                    val biometricEncryptedPreferences = BiometricEncryptedPreferences(
                        title = "Test Title",
                        subTitle = "Test Subtitle",
                        cancelText = "Cancel",
                        server = "Test Server",
                        context = context
                    )

                    var failureReason: String? = null
                    biometricEncryptedPreferences.setupBiometricAccess(
                        onSuccess = { println(it) },
                        onFailure = {
                            println(it)
                            failureReason = it
                        }
                    )
                    assert(failureReason == BiometricAuthErrorMessage.NOT_ENROLLED.message)
                }
            }
        }



    }

    @Test
    fun `test setupBiometricAccess when biometric is available`(){
        mockkConstructor(BiometricManager::class){
            mockkConstructor(BiometricPrompt::class){
                mockkConstructor(BiometricViewModel::class){
                    mockkConstructor(BiometricPrompt.PromptInfo.Builder::class){
                        mockkStatic(BiometricManager::from){
                            val biometricManager = mockk<BiometricManager>()
                            every {
                                BiometricManager.from(any())
                            } returns biometricManager

                            every {
                                anyConstructed<BiometricPrompt>().authenticate(any())
                            } returns Unit
                            every { biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) } returns BiometricManager.BIOMETRIC_SUCCESS
                            val biometricEncryptedPreferences = BiometricEncryptedPreferences(
                                title = "Test Title",
                                subTitle = "Test Subtitle",
                                cancelText = "Cancel",
                                server = "Test Server",
                                context = context
                            )

                            biometricEncryptedPreferences.setupBiometricAccess(
                                onSuccess = { println(it) },
                                onFailure = { println(it) }
                            )

                            verify { anyConstructed<BiometricPrompt>().authenticate(any()) }
                        }
                    }
                }
            }
        }

    }
}