# Not Hotdog documentation

Not Hotdog is an Android demo of the fake app from the TV show *Silicon Valley*. You give it a photo and it answers "Hotdog" or "Not hotdog". The answer comes from Gemini Nano running on the phone, so no photo leaves the device for classification.

It is an unofficial fan project and has no connection to the show or its owners.

## Where to read next

| If you want to... | Read |
|---|---|
| Understand what the app does, screen by screen | [How the app works](how-it-works.md) |
| Find your way around the code | [Architecture](architecture.md) |
| Change the model, the prompt or device support | [On-device inference](on-device-inference.md) |
| Change how something looks | [UI specification](ui-specification.md) |
| Build, test and debug it, as a person or an agent | [Development guide](development.md) |
| Publish a new version | [Releasing](releasing.md) |

Agents should also read [`AGENTS.md`](../AGENTS.md), which holds the build rules.

## Quick start

You need Android Studio or the Android SDK, and JDK 21 for the Gradle daemon.

```sh
./gradlew installDebug
```

Classification only works on a device with Gemini Nano, such as a Pixel 9 or 10. On an emulator the app runs but shows "Gemini Nano isn't available on this device" and keeps the photo buttons disabled.

## Current status

The full flow has been run on a Pixel 10 on Android 17. Hotdog and pizza photos were classified correctly, with the Evaluating screen visible for about 1.3 to 2 seconds in the two runs that measured it. That is a smoke test with `nano-v3`, not a measure of accuracy or typical speed. The preview models in settings haven't classified anything yet. Near-misses such as sandwiches haven't been tried.

Some of the details in these docs will go stale, especially device support and library versions. Where the code and the docs disagree, trust the code and fix the docs.
