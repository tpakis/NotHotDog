# Releasing

Pushing a tag that starts with `v` runs [`.github/workflows/release.yml`](../.github/workflows/release.yml). It runs the unit tests, builds a signed release APK, and creates a GitHub release with the APK attached and generated notes.

## Publishing a version

Tag a commit that contains the workflow, usually on `main`:

```sh
git tag v1.0.0
git push origin v1.0.0
```

Follow the run in the repo's **Actions** tab. The APK appears on the releases page as `NotHotDog-v1.0.0.apk`.

| APK field | Comes from |
|---|---|
| `versionName` | The tag, such as `v1.0.0` |
| `versionCode` | The workflow's run number, which increases with every release |

If a run fails, fix the problem, delete the tag, and push it again from the fixed commit:

```sh
git tag -d v1.0.0
git push origin :refs/tags/v1.0.0
```

