# AGENTS.md

This file provides guidance to AI coding agents when working with code in this repository.

## Commands

```sh
./gradlew assembleDebug          # build the debug APK
./gradlew installDebug           # build and install on a connected device/emulator
./gradlew lint                   # Android lint (report in app/build/reports/)
./gradlew test                   # JVM unit tests (app/src/test)
./gradlew connectedAndroidTest   # instrumented tests; needs a device/emulator (app/src/androidTest)

# single test (unit, then instrumented)
./gradlew :app:testDebugUnitTest --tests "ai.thanasakis.nothotdog.ExampleUnitTest.addition_isCorrect"
./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=ai.thanasakis.nothotdog.ExampleInstrumentedTest
```

## Project state

Single-module (`:app`) Compose demo of the "Not Hotdog" app from Silicon Valley. `NotHotDogViewModel` holds the screen state (home, evaluating, result) and `HotdogClassifier` asks Gemini Nano through the ML Kit GenAI Prompt API, which runs in AICore and is only available on supported devices. Emulators report it as unavailable, so the full flow needs a physical device such as a Pixel 9 or 10.

## Build setup

- **AGP 9 with built-in Kotlin:** only `com.android.application` and `org.jetbrains.kotlin.plugin.compose` are applied. Do not add `org.jetbrains.kotlin.android`; AGP 9 compiles Kotlin itself. The root `build.gradle.kts` raises the bundled KGP to the catalog's `kotlin` version, because ML Kit GenAI depends on Kotlin 2.4.
- **Dependencies:** declared in the version catalog `gradle/libs.versions.toml` and referenced as `libs.*`. Compose library versions come from the Compose BOM, so those entries have no version. Repositories are set in `settings.gradle.kts` only (`FAIL_ON_PROJECT_REPOS`).
- **SDK levels:** `compileSdk` 37, `targetSdk` 36, `minSdk` 26 (required by ML Kit GenAI).
- **JVM:** the Gradle daemon runs on a JDK 21 toolchain (`gradle/gradle-daemon-jvm.properties`), while app sources compile to Java 11 compatibility.
- **R8:** disabled for release (`optimization { enable = false }` in `app/build.gradle.kts`). Keep rules go in `app/src/main/keepRules/`, not `proguard-rules.pro`.
- **Configuration cache:** enabled in `gradle.properties`, so custom Gradle tasks and build logic must be compatible with it.
