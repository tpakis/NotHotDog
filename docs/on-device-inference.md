# On-device inference

The app uses the [ML Kit GenAI Prompt API](https://developers.google.com/ml-kit/genai/prompt/android) (`com.google.mlkit:genai-prompt`), which calls Gemini Nano through AICore, an Android system service. The app doesn't ship a model. AICore owns the download and the updates, and all apps on the device share the same copy.

All of this code is in `app/src/main/java/ai/thanasakis/nothotdog/classifier/HotdogClassifier.kt`.

## Detecting a capable device

`HotdogClassifier.prepare()` calls `GenerativeModel.checkStatus()` and maps the result to `ModelState`:

| `FeatureStatus` | `ModelState` | Notes |
|---|---|---|
| `AVAILABLE` | `Ready` | The buttons are enabled |
| `DOWNLOADABLE`, `DOWNLOADING` | `Downloading`, then `Ready` | Calls `download()` and reports progress |
| `UNAVAILABLE` | `Unavailable` | Also used for any exception in this flow |

Don't check against a list of device models. `checkStatus()` is the source of truth. It can return `UNAVAILABLE` on a supported phone that hasn't fetched its AICore configuration yet, which is why the home screen offers **Check again**.

## Supported devices

As of November 2026, Google's [device support list](https://developers.google.com/ml-kit/genai) for direct model access includes:

| Model version | Google devices listed |
|---|---|
| `nano-v3` | Pixel 9 and Pixel 10 families |
| `nano-v4` | Pixel 11 family |

Many other manufacturers are listed, some under `nano-v2`. Check that page for the current list. Only a Pixel 10 and an emulator have been tested with this app.

## The request

```kotlin
generateContentRequest(ImagePart(photo.fitInto(1024)), TextPart(PROMPT)) {
    temperature = 0f
    topK = 1
    this.maxOutputTokens = maxOutputTokens // 3 by default
}
```

The prompt is:

```text
Does this photo show a hot dog, meaning a sausage served in a sliced bun?
##
Answer with one word: YES or NO.
```

- **Temperature 0 and top-K 1** make this as deterministic as the settings allow, since it's classification and not creative writing.
- **`maxOutputTokens`** defaults to 3, which leaves no room for reasoning, and output length is the main factor in inference speed. It's adjustable in settings.
- **The `##` delimiter** follows Google's guidance for Gemini Nano, which says it helps the model tell the parts of a prompt apart.
- **The definition of a hot dog** is in the prompt so that the model has a clear criterion for sausages without buns or sandwiches. How well this works on near-misses hasn't been tested.

The app never sets `enableThinking`, since it only needs the verdict.

## Parsing the answer

`parseAnswer` accepts `yes` or `no` as the first word, in any case and with leading punctuation, so `**YES**` and `No.` both work. Anything else returns `null`, and `isHotdog` turns that into an error. `ParseAnswerTest` covers this.

## Choosing a model

The settings dialog lets the user choose a model, using `ModelChoice` in `classifier/ModelChoice.kt`. The choice is saved by `SettingsStore`. The default is `ModelConfig.DEFAULT`, which is Stable and Full.

A model is selected by two values rather than by name: `ModelReleaseStage` (`STABLE`, `PREVIEW`) and `ModelPreference` (`FULL`, `FAST`). The API can't list installed models, so `probeModelChoices()` creates a client for each of the four combinations and calls `checkStatus()`, as [Google's guide](https://developers.google.com/ml-kit/genai/prompt/android/select-model) suggests. For installed ones it also reads `getBaseModelName()` and `getTokenLimit()`.

This is what a Pixel 10 enrolled in the AICore Developer Preview reported:

| Choice | Result |
|---|---|
| Stable, Full | `nano-v3` |
| Stable, Fast | Not available |
| Preview, Full | `nano-v4-full` |
| Preview, Fast | `nano-v4-fast` |

All three installed models reported a limit of 32768 tokens. Other devices will differ, and a device outside the [Developer Preview](https://developers.google.com/ml-kit/genai/aicore-dev-preview) won't get preview models.

- Switching models has been tested, but classifying photos has only been tested on `nano-v3`. The accuracy and speed of the preview models are unknown.
- Selecting a model that isn't installed starts its download, which is large. AICore's guide says these downloads need Wi-Fi.
- If a saved model later reports as unavailable, the app goes back to the default and says so.
- Preview models are for testing. Google's terms don't allow preview services in production, and its guide recommends Stable for public releases.

## Token settings

- **`getTokenLimit()`** is the model's total for input and output together. It's read-only, so the app only displays it.
- **`maxOutputTokens`** is set per request, and the settings dialog offers 3, 16, 64 or 256. The default of 3 is enough for one word. Raising it can help if a model starts its answer with other text, but `parseAnswer` still only accepts `yes` or `no` as the first word.

## Failure modes

| Situation | Behaviour |
|---|---|
| Device unsupported, or AICore missing | `ModelState.Unavailable` |
| Download fails | `ModelState.Unavailable`, with a warning in logcat under `HotdogClassifier` |
| `GenAiException` during inference | Back to home with an error, logged under `NotHotDogViewModel` |
| Unexpected answer | The same, through the `IOException` from `isHotdog` |
| A quota or foreground limit is hit | The same, as far as the docs indicate. See [Cost and limits](#cost-and-limits) |

On an emulator, `generateContent` fails with `[ErrorCode -101] AICore is either not installed or the installed version is too low.`

## Limits to design around

- **Speed.** Expect several seconds per photo on a Pixel 10. The first request after launch is the slowest because the model has to load. `GenerativeModel.warmup()` exists to reduce this, but the app doesn't call it.
- **Beta API.** The library is at `1.0.0-beta5`, so expect changes between versions.
- **Kotlin version.** This library version depends on Kotlin 2.4, which is why the root `build.gradle.kts` upgrades the Kotlin Gradle plugin that AGP 9 bundles.

## Cost and limits

There's no per-request fee, API key or token billing, because nothing runs on a server. Google lists "no additional server cost incurred for each API call" as a benefit of these APIs. The cost falls on the device instead, in battery, storage and time.

That doesn't mean unlimited. These are the limits that apply:

| Limit | What happens |
|---|---|
| Too many requests in a short period | `ErrorCode.BUSY`. Google suggests retrying with exponential backoff |
| A long-duration quota, such as a daily one | `ErrorCode.PER_APP_BATTERY_USE_QUOTA_EXCEEDED` |
| The app isn't the top foreground app | `ErrorCode.BACKGROUND_USE_BLOCKED`, even from a foreground service |
| Prompt size | Google's guide says input must stay under 4000 tokens |

The prompt size limit isn't a concern for this app. On a Pixel 10 with `nano-v3`, `countTokens` gave 37 tokens for the prompt and 295 with an image added, so an image costs 258 tokens. That number was the same for every size tested, from 64×64 to 4000×3000, so resolution doesn't affect it. `getTokenLimit()` reported 32768 on that device, which doesn't match the 4000 in the guide, so check on the device if you ever send much longer prompts or many images. Other model versions may count differently.

Google doesn't publish the quota numbers in the pages this was checked against, and none of these errors have been triggered in testing. The app treats them all as a failed evaluation, so it shows the generic error and doesn't retry. Leaving the app mid-evaluation should therefore end in that error too.

Read the [ML Kit GenAI API Additional Terms of Service](https://developers.google.com/ml-kit/genai-terms) before shipping. They allow production use but say that Google may enforce rate limits, they require you and your audience to be 18 or over, and they don't allow services marked as preview in production. This library is still in beta.

## Permissions and privacy

Classification runs on the device. The merged manifest contains `INTERNET` and `ACCESS_NETWORK_STATE`, which the app doesn't declare itself. They come from `com.google.android.datatransport:transport-backend-cct`, which ML Kit uses to upload its diagnostics and usage analytics. See Google's [data disclosure](https://developers.google.com/ml-kit/android-data-disclosure) for what it covers.
