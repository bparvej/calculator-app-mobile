# Simple Calculator

A minimal Android calculator app built with Kotlin, Jetpack Compose, and Material 3.

## Features

- Basic arithmetic: addition, subtraction, multiplication, division
- Decimal input and percentage calculation
- Clear, backspace, equals, and negative number support
- Light and dark theme support
- No external backend or network permissions

## Project structure

- `app/` – Android app module
- `gradle/wrapper/` – Gradle wrapper files
- `.github/workflows/android.yml` – GitHub Actions build pipeline

## Local development

1. Install JDK 17.
2. Install Android Studio or use the GitHub Actions CI environment.
3. Sync the project with Gradle.
4. Run the app on an emulator or a connected device.

## Release signing

Do not commit signing keys or passwords to source control.

This project intentionally uses the debug signing config for the CI-only release build to keep the workflow buildable without a local keystore. For Google Play publishing, replace it with a secure release keystore stored in GitHub Actions secrets or another secret manager.

Recommended approach:

1. Generate a keystore locally:
   `keytool -genkeypair -v -storetype PKCS12 -keystore release.keystore -alias upload -keyalg RSA -keysize 2048 -validity 10000`
2. Store the keystore file as base64 and keep the password, key password, and alias in GitHub Actions secrets such as `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_PASSWORD`, and `KEY_ALIAS`.
3. Decode the keystore in CI and use the `signingConfigs` block in the `app/build.gradle.kts` file to sign the release AAB before upload.
4. Upload the signed artifact to Google Play Console and keep the keystore in a secure, access-controlled location.

## Build with GitHub Actions

Push to the main branch or trigger the workflow manually. The workflow builds a debug APK and a release AAB and uploads both as artifacts.

## Notes

This project intentionally keeps the implementation simple and avoids unnecessary abstractions.
