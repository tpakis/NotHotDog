<p align="center">
  <img src="docs/assets/app-icon.svg" alt="Not Hotdog app icon" width="128" height="128">
</p>

<h1 align="center">Not Hotdog</h1>

<p align="center">
  <a href="https://developer.android.com"><img src="https://img.shields.io/badge/Android-API_26%2B-3DDC84?logo=android&logoColor=white" alt="Android API 26+"></a>
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-2.4-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin 2.4"></a>
  <a href="https://developer.android.com/compose"><img src="https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?logo=jetpackcompose&logoColor=white" alt="Jetpack Compose with Material 3"></a>
  <a href="https://developer.android.com/ai/gemini-nano"><img src="https://img.shields.io/badge/Gemini_Nano-on--device-8E75B2?logo=googlegemini&logoColor=white" alt="Gemini Nano on-device"></a>
  <a href="https://developers.google.com/ml-kit/genai/prompt/android"><img src="https://img.shields.io/badge/ML_Kit-GenAI_Prompt_API-4285F4?logo=google&logoColor=white" alt="ML Kit GenAI Prompt API"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-yellow" alt="MIT License"></a>
</p>

An Android demo of the "Not Hotdog" app from the TV show *Silicon Valley*. Take or choose a photo and it tells you whether it's a hotdog, using Gemini Nano on the device.

It's an unofficial fan project with no connection to the show or its owners.

## Demo

<p align="center">
  <img src="docs/assets/demo.gif" alt="The app evaluating a photo of a hotdog and answering Hotdog, then a photo of a pizza and answering Not hotdog" width="270">
</p>

Recorded on a Pixel 10 with real on-device results. The GIF skips the step of choosing a photo.

## Screenshots

| Home | Evaluating | Hotdog |
|---|---|---|
| <img src="docs/assets/screenshots/home.png" alt="Home screen with Take photo and Choose photo buttons" width="240"> | <img src="docs/assets/screenshots/evaluating.png" alt="A washed-out photo with a ring of dots and the text Evaluating" width="240"> | <img src="docs/assets/screenshots/hotdog.png" alt="A hot dog with a green Hotdog banner and check mark" width="240"> |

| Not hotdog | Settings | Share |
|---|---|---|
| <img src="docs/assets/screenshots/not-hotdog.png" alt="A pizza with a red Not hotdog banner and X" width="240"> | <img src="docs/assets/screenshots/settings.png" alt="Settings dialog with model choices and answer length" width="240"> | <img src="docs/assets/screenshots/share.png" alt="Share dialog with a QR code and link to this repository" width="240"> |

Sample photos: [hot dog](https://commons.wikimedia.org/wiki/File:Hot_dog_with_mustard.png) by Renee Comet, National Cancer Institute (public domain), and [pizza](https://commons.wikimedia.org/wiki/File:Pizza-3007395.jpg) by igorovsyannykov (CC0).

## Getting started

```sh
./gradlew installDebug
```

Classification needs a device with Gemini Nano, such as a Pixel 9 or 10. Signed APKs are on the [releases page](https://github.com/tpakis/NotHotDog/releases).

## Documentation

See the [documentation](docs/README.md) for how the app works, its architecture, and how to work on it. Agents should also read [`AGENTS.md`](AGENTS.md).

## License

The code is available under the [MIT License](LICENSE). The bundled Nunito font is under the [SIL Open Font License](app/src/main/assets/licenses/nunito_OFL.txt). The license doesn't grant any rights to the show or its name.
