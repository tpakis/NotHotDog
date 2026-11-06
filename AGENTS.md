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

Single-module (`:app`) Android app, still at the Android Studio Compose template stage: `MainActivity` renders a placeholder `Greeting` inside `NotHotDogTheme`. There is no app-specific architecture yet.

## Build setup

- **AGP 9 with built-in Kotlin:** only `com.android.application` and `org.jetbrains.kotlin.plugin.compose` are applied. Do not add `org.jetbrains.kotlin.android`; AGP 9 compiles Kotlin itself.
- **Dependencies:** declared in the version catalog `gradle/libs.versions.toml` and referenced as `libs.*`. Compose library versions come from the Compose BOM, so those entries have no version. Repositories are set in `settings.gradle.kts` only (`FAIL_ON_PROJECT_REPOS`).
- **SDK levels:** `compileSdk` 37, `targetSdk` 36, `minSdk` 24.
- **JVM:** the Gradle daemon runs on a JDK 21 toolchain (`gradle/gradle-daemon-jvm.properties`), while app sources compile to Java 11 compatibility.
- **R8:** disabled for release (`optimization { enable = false }` in `app/build.gradle.kts`). Keep rules go in `app/src/main/keepRules/`, not `proguard-rules.pro`.
- **Configuration cache:** enabled in `gradle.properties`, so custom Gradle tasks and build logic must be compatible with it.
