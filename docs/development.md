# Development guide

Build commands and build rules are in [`AGENTS.md`](../AGENTS.md). This guide covers what they don't: how to test this app and how to make common changes.

## Choosing a device

| Target | What you can test |
|---|---|
| Physical device with Gemini Nano | Everything |
| Emulator | Home, the unavailable state, share dialog, camera and picker launches |
| Compose previews | The look of every screen, with no device |

The emulator has no AICore, so it can't classify anything.

## Compose previews

The screens are in `ui/Previews.kt`. Android Studio shows them, or use the [Android CLI](https://developer.android.com/tools/agents/android-cli):

```sh
android preview list "?:app"
android preview render "?:app" \
  --fqn=ai.thanasakis.nothotdog.ui.PreviewsKt.HotdogPreview \
  -o /tmp/previews
```

The `?:app` target is required, because the root project isn't an Android project. The previews are `HomeReadyPreview`, `HomeUnavailablePreview`, `EvaluatingPreview`, `HotdogPreview`, `NotHotdogPreview`, `ShareDialogPreview` and `SettingsDialogPreview`. They use a drawn placeholder image rather than a real photo.

## Testing

```sh
./gradlew test                  # JVM tests, no device needed
./gradlew connectedAndroidTest  # instrumented tests, on every connected device
```

| Tests | Where | Covers | Needs |
|---|---|---|---|
| `ParseAnswerTest`, `ModelChoiceTest` | `app/src/test` | Answer parsing, model variants | Nothing |
| `SettingsStoreTest`, `PhotoLoaderTest` | `app/src/androidTest` | Saved settings, decoding, EXIF rotation, downsampling | Any device |
| `HomeScreenTest`, `EvaluationScreensTest`, `SettingsDialogTest` | `app/src/androidTest/.../ui` | Each screen and button, with fake inputs | Any device |
| `MainActivityTest` | `app/src/androidTest/.../ui` | The real app: home, share, settings, camera and picker launches | Any device. The camera and picker tests are skipped without a ready model |

Before running instrumented tests:

- **Unlock the device.** Tests can't drive a locked screen.
- **Expect the app to be uninstalled afterwards.** The Gradle task removes it, and its saved settings with it, so run `./gradlew installDebug` again.

`MainActivityTest` stubs external activities with Espresso Intents, so it never opens the real camera or photo picker and never touches personal photos.

### What the tests don't cover

No automated test classifies a photo. A result depends on the model, so a test would be slow and could fail for reasons that have nothing to do with the code. `NotHotDogViewModel` also has no direct tests, because it creates `HotdogClassifier` itself, so the evaluation flow and the fallback to the default model are untested. Letting a fake classifier be passed in would make those testable.

Check these by hand after changing the flow:

1. **Take photo:** capture, confirm, and get a result.
2. **Choose photo:** pick an image and get a result.
3. **Back:** while evaluating, this cancels and returns home.
4. **Settings:** a new model choice survives a restart and the next photo uses it. Avoid selecting a model that isn't installed unless you mean to download it.
5. **Share:** the QR code scans to the right URL.

### Testing the flow on an emulator

To exercise the camera, picker and error handling without a capable device, temporarily change this line in `HomeScreen`, and don't commit it:

```kotlin
val ready = true // normally: model == ModelState.Ready
```

Inference then fails with error `-101`, and the app should return home with "Couldn't evaluate that photo. Try again."

## Debugging

```sh
adb logcat -s HotdogClassifier NotHotDogViewModel
```

| Symptom | Likely cause |
|---|---|
| "Gemini Nano isn't available" on a supported phone | AICore hasn't fetched its configuration yet. Wait, keep the phone online, and tap **Check again**. Also check logcat for a download failure |
| Returning from the camera goes straight home | The capture wasn't saved. Look for `CAM_StateSavePic` or similar camera errors in logcat |
| `Class 'kotlin.Unit' was compiled with an incompatible version of Kotlin` | The Kotlin version in `libs.versions.toml` is too old for ML Kit. Keep `kotlin` at 2.4 or later |
| `Couldn't evaluate that photo` | Look for `Evaluation failed` in logcat, with the `GenAiException` or the unexpected answer |

## Common changes

| To change... | Edit |
|---|---|
| The question or the answer format | `PROMPT` and `parseAnswer` in `HotdogClassifier.kt`, and `ParseAnswerTest` |
| What gets detected, making it a "Not cat" app for example | `PROMPT`, the result strings in `strings.xml`, and the app name |
| The available models or answer lengths | `ModelChoice.ALL` in `ModelChoice.kt`, `MAX_OUTPUT_TOKEN_OPTIONS` in `SettingsDialog.kt` |
| Colors or proportions | See the [UI specification](ui-specification.md) |
| The shared link | `share_url` in `strings.xml`. The QR code follows it |
| Screens or flow | Add to `Screen` in `NotHotDogViewModel.kt` and to the `when` in `NotHotDogApp.kt` |
| The camera, to use an in-app viewfinder | Replace the `TakePicture` launcher in `NotHotDogApp.kt`, and add the permission flow |

Add a preview and a UI test for any new screen.

## Notes for agents

- **Start with** [`AGENTS.md`](../AGENTS.md) for build rules, then the page above that matches the task.
- **Installed skills.** The Android CLI has relevant ones: `ml-kit-genai-prompt-api`, `camerax` and `android-cli`.
- **Check API names against the current version.** This is a beta API that changes quickly, and samples can lag behind. Use the reference docs and let the compiler confirm. Google's terms forbid reverse engineering these services, so don't decompile them.
- **Inspect with screenshots when the spinner is on screen.** Tools that wait for an idle UI may not cope with its endless animation, and `uiautomator dump` was also seen failing with a null root node on an emulator. `adb exec-out screencap -p` works anywhere.
- **Respect the test phone.** It's probably someone's personal device. Don't unlock it, and remove any test photos you push, including after a failed run. A deleted file can stay in the picker until it's rescanned.
- **Never capture the photo picker.** It shows the owner's library and can take many seconds to close after a photo is tapped, so a fixed delay isn't enough. Before saving or viewing a frame, check by code that it matches the expected screen, and discard everything else unviewed. The same applies to recordings. Don't assume the newest item in the picker is the file you pushed, either.
- **Report what you verified.** Say whether a claim comes from a real device, an emulator or a preview. In particular, an emulator can't confirm anything about classification.
- **Don't add `org.jetbrains.kotlin.android`.** AGP 9 compiles Kotlin itself.
