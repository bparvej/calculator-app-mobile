# Google Play publishing notes

## Root cause of the missing AAB

The release AAB is not generated unless the required Android signing secrets are available in the GitHub Actions environment and the workflow is allowed to run the release build.

From the project files:

- `.github/workflows/android.yml` checks for the following repository secrets:
  - `ANDROID_KEYSTORE_BASE64`
  - `ANDROID_KEYSTORE_PASSWORD`
  - `ANDROID_KEY_ALIAS`
  - `ANDROID_KEY_PASSWORD`
- If any of those are missing, the workflow skips the signed release build and only produces the debug APK.
- In `app/build.gradle.kts`, the app defines a `release` signing config that reads from environment variables and only signs the release bundle when those values are present.

This means: no valid keystore + no secret values = no signed AAB artifact.

## Required GitHub repository secrets

Add these exact repository secrets in GitHub:

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

These are required to produce a signed release AAB for Google Play upload.

## Keystore generation commands

Run these on a secure local machine:

```bash
keytool -genkeypair \
  -v \
  -keystore upload-keystore.jks \
  -storetype JKS \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias my-upload-key \
  -storepass "YOUR_KEYSTORE_PASSWORD" \
  -keypass "YOUR_KEY_PASSWORD" \
  -dname "CN=Your Name, OU=Android, O=Your Company, L=City, ST=State, C=Country"
```

Then encode the keystore for GitHub Secrets:

```bash
base64 -i upload-keystore.jks
```

Example secret values:

- `ANDROID_KEYSTORE_BASE64`: the full output from the base64 command
- `ANDROID_KEYSTORE_PASSWORD`: the value used for `-storepass`
- `ANDROID_KEY_ALIAS`: `my-upload-key`
- `ANDROID_KEY_PASSWORD`: the value used for `-keypass`

Assumption: this is a standard upload keystore for Google Play. If you are using an existing Play upload key, reuse that keystore instead of generating a new one.

## GitHub setup steps

1. Open the repository on GitHub.
2. Go to `Settings` → `Secrets and variables` → `Actions`.
3. Add each secret exactly as listed above.
4. Save the values.
5. Push a new commit or trigger the workflow manually.

The workflow will then:

- decode the keystore from `ANDROID_KEYSTORE_BASE64`
- set `ANDROID_KEYSTORE_FILE`
- run `./gradlew bundleRelease`
- upload the signed AAB as a workflow artifact if signing is configured

## AAB artifact path after a successful workflow run

After a successful build, the release AAB is created at:

```text
app/build/outputs/bundle/release/app-release.aab
```

In GitHub Actions, it will appear as the workflow artifact named:

- `release-aab`

## VersionCode bump requirement before upload

Google Play requires a higher `versionCode` for every new upload.

In `app/build.gradle.kts`, the app currently has:

```kotlin
versionCode = 2
versionName = "1.1.0"
```

Before uploading to Google Play, bump `versionCode` to a larger integer than the current release. Example:

```kotlin
versionCode = 3
versionName = "1.1.1"
```

Rules:

- `versionCode` must be an integer and increase for every new release
- `versionName` is user-facing and can be semantic versioning like `1.1.1`
- Do not reuse a lower `versionCode` for a new upload

Assumption: this app is being prepared for initial production release and no previous Play release exists yet. If the app is already in Play, use the next valid code above the current production version.

## Step-by-step Google Play Console upload checklist

1. Make sure the Android app is built successfully.
2. Ensure the release AAB is signed with a valid upload keystore.
3. Increase `versionCode` in `app/build.gradle.kts` before publishing.
4. Commit and push the version bump to GitHub.
5. Trigger the GitHub Action or push to the main branch.
6. Download the generated `app-release.aab` artifact from GitHub Actions.
7. Open Google Play Console.
8. Create or select the app.
9. Go to `Release` → `Testing` or `Production` depending on your track.
10. Click `Create new release`.
11. Upload the signed AAB file.
12. Review the app information, target audience, and content rating.
13. Fill in the privacy policy and data safety section.
14. Add screenshots, feature graphic, and store listing text.
15. Review Android version compatibility and permissions.
16. Save the draft or complete the release.
17. Submit for review if required by the chosen track.
18. Wait for Google Play review and address any policy issues if needed.

## Final checklist

- [ ] GitHub secrets added
- [ ] Keystore generated securely
- [ ] `ANDROID_KEYSTORE_BASE64` value set
- [ ] `ANDROID_KEYSTORE_PASSWORD` set
- [ ] `ANDROID_KEY_ALIAS` set
- [ ] `ANDROID_KEY_PASSWORD` set
- [ ] Workflow run completed successfully
- [ ] AAB artifact generated at `app/build/outputs/bundle/release/app-release.aab`
- [ ] `versionCode` increased before upload
- [ ] App listing text prepared
- [ ] Privacy policy added
- [ ] Store screenshots ready
- [ ] AAB uploaded to Google Play Console
- [ ] Release submitted for review

## Notes

- No app code change was made for this note.
- This document is intentionally concise and practical for publishing setup.
- If you already have a valid Play upload keystore, do not generate a new one unless required.
