#  Biometric authentificator

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.zaval/biometricauth?style=flat)

[![compose-mp-version](https://img.shields.io/badge/compose--multiplatform-1.7.0-blue)](https://github.com/JetBrains/compose-multiplatform)
[![kotlin-version](https://img.shields.io/badge/kotlin-2.0.20-blue)](https://github.com/JetBrains/kotlin)

![badge-iOS](https://img.shields.io/badge/Platform-iOS-lightgray)
![badge-Android](https://img.shields.io/badge/Platform-Android-brightgreen)


Biometric Authenticator is a Kotlin Multiplatform library designed to provide seamless biometric authentication, supporting fingerprint on Android and FaceID on iOS.

## Features

**Biometric Authentication**: Restrict access to sensitive logic using biometric authorization.

 **Encrypted Storage**: Securely store sensitive data such as passwords, tokens, and more.

## Installation

Add the following dependency to your Kotlin Multiplatform project:

```kotlin
dependencies {
    implementation("io.github.zaval:biometricauth:<latest-version>")
}
```

Replace <latest-version> with the latest version available on Maven Central.

## Usage

### Simple Authorization

Authenticate users with biometric authentication:

```kotlin
@Composable
fun AuthPage() {

    val biometricAuthHelper = rememberBiometricAuthHelper()

    Button(
        onClick = {
            biometricAuthHelper.authenticate(
                onFailure = {errorMessage ->
                    println(errorMessage)
                },
            ) {
                // Execute authorized code here
            }
        }
    ) {
        Text("Login with Biometric")
    }
}
```

### Access Encrypted Storage After Authorization

```kotlin
@Composable
fun AuthPage() {
    val biometricAuthHelper = rememberBiometricAuthHelper()

    Button(
        onClick = {
            biometricAuthHelper.authenticate(
                onFailure = {errorMessage ->
                    println(errorMessage)
                },
            ) {authStorage ->
                // save some secret value
                authStorage.setValue("token", "s3cretT0ken")
                // read secret value
                val token = authStorage.getValue("token")
            }
        }
    ) {
        Text("Login with Biometric")
    }
}
```

## API Documentation

### BiometricAuthHelper

The **BiometricAuthHelper** is the core class for managing biometric authentication.

#### Methods

- `authenticate(onFailure: (String) -> Unit, onSuccess: (BiometricAuthStorage) -> Unit)`
  - Initiates biometric authentication.
  - **Parameters**:
    - `onFailure`: A callback invoked when authentication fails, providing an error message.
    - `onSuccess`: A callback invoked upon successful authentication, providing access to BiometricAuthStorage.

#### Usage Example

```kotlin
val biometricAuthHelper = rememberBiometricAuthHelper()
biometricAuthHelper.authenticate(
    onFailure = { errorMessage ->
        println("Authentication failed: $errorMessage")
    },
    onSuccess = { authStorage ->
        println("Authentication successful!")
    }
)
```

### BiometricAuthStorage

The **BiometricAuthStorage** provides secure storage for sensitive data, accessible only after successful authentication.

#### Methods

- `setValue(key: String, value: String)`
  - Stores a key-value pair securely.
  - **Parameters**:
    - `key`: The identifier for the data.
    - `value`: The data to be stored.

- `getValue(key: String): String?`
  - Retrieves the stored value for the given key.
  - **Parameters**:
    - `key`: The identifier for the data.
  - **Returns**:
    - The stored value, or null if the key does not exist.

#### Usage Example

```kotlin
authStorage.setValue("token", "s3cretT0ken")
val token = authStorage.getValue("token")
println("Retrieved token: $token")
```

## Platforms

- Android: Supports fingerprint authentication.
  - `androidx.biometric` for authorization
  - `EncryptedSharedPreferences` to store secret values
- iOS: Supports FaceID and TouchID.
  - `LocalAuthentication` for authorization
  - `Keychain` to store secret values


## Contributing

Contributions are welcome! Please feel free to submit issues, feature requests, or pull requests.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.