#  Biometric authentificator

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.zaval/biometricauth?style=flat)
![GitHub License](https://img.shields.io/github/license/zaval/biometric-auth-ktx)


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

### Android

Use `FragmentActivity` instead of `AppCompatActivity` in main activity class parent.

### iOS

Add `NSFaceIDUsageDescription` key in Info.plist file of your project

```xml
<key>NSFaceIDUsageDescription</key>
<string>Privacy - Face ID Usage Description</string>
```


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

### Remember Encrypted Storage

```kotlin
val biometricAuthStorage = rememberBiometricAuthStorage {
    println("Auth storage $it")
    val accessToken = it.getValue("token")
    // accessToken is a saved value

    it.setValue("token", accessToken)
    // Save accessToken to the encrypted storage
}
```

## API Documentation

### rememberBiometricAuthHelper

**rememberBiometricAuthHelper** is a composable function that provides an instance of the **BiometricAuthHelper**. This helper is used to manage biometric authentication in a Kotlin Multiplatform project.

#### Parameters

- `title: String`: The title which displays in the authentication dialog presented to the user.
- `subTitle: String`: The title which displays in the authentication dialog presented to the user. (Visible on Android only)
- `cancelText: String`: Text for the Cancel button on the authentication dialog
- `server: String`: The server string used by **BiometricAuthStorage** for storing secret values

### rememberBiometricAuthHelper

**rememberBiometricAuthStorage** is a composable function that provides an instance of the **BiometricAuthStorage**. This helper is used to access the encrypted key-value storage in a Kotlin Multiplatform project.

#### Parameters

- `title: String`: The title which displays in the authentication dialog presented to the user.
- `subTitle: String`: The title which displays in the authentication dialog presented to the user. (Visible on Android only)
- `cancelText: String`: Text for the Cancel button on the authentication dialog
- `server: String`: The server string used by **BiometricAuthStorage** for storing secret values
- `onFailure: (String) -> Unit`: A callback invoked when authentication fails, providing an error message.

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

```
Copyright 2025 Dmytrii Zavalnyi.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
