# UI specification

The evaluating and result screens copy the app as it appears in the show. Proportions were measured from screenshots and frames of the [scene](https://www.youtube.com/watch?v=tWwCK95X6go), so they are close estimates. Colors are less certain, because the footage shows the app on a dim monitor. The home screen and share dialog are original designs in the same style.

## Scaling

Graphics scale with `unit`, the shorter side of the screen, and not with a fixed `dp` size. Text on these screens uses a size derived from `unit`, so it fills the same share of the screen on every phone and ignores the system font scale. This is deliberate, since it's closer to artwork than to body text, but keep it in mind for accessibility.

## Colors

Defined in `app/src/main/java/ai/thanasakis/nothotdog/ui/theme/Color.kt`.

| Name | Value | Use |
|---|---|---|
| `HotdogGreen` | `#35C422` | Hotdog banner, Take photo, icon background |
| `NotHotdogRed` | `#B3201C` | Not hotdog banner, Choose photo |
| `SpinnerHead` | `#E8264A` | Largest dot |
| `SpinnerTail` | `#FFF3D6` | Smallest dot |
| `HomeBackground` | `#141414` | Home screen and dialog |

## Lettering

`OutlinedText` draws the text twice: a black stroke with a soft shadow, then a white fill on top. The stroke layer has its semantics cleared, so that TalkBack and UI tests see the text once. The font is Nunito Black, bundled as `res/font/nunito_variable.ttf`. It's a close open-licensed match for the show's rounded lettering, not the original font. Its license is in `app/src/main/assets/licenses/nunito_OFL.txt`.

## Evaluating screen

In `ui/EvaluatingScreen.kt`:

| Element | Value |
|---|---|
| Photo | Fills the screen with `ContentScale.Crop` and a 4 dp blur |
| Wash | White at 40% opacity |
| Ring | 11 dots in 12 slots, leaving one gap, radius `0.22 × unit` |
| Ring center | 35% of the screen height |
| Dot radius | `0.043 × unit` at the head, down to `0.027 × unit` at the tail |
| Dot outline | Black, `0.006 × unit` |
| Motion | Steps clockwise one slot at a time, one turn per 1200 ms |
| Text | `0.135 × unit`, centered at 62% of the screen height |

## Result screen

In `ui/ResultScreen.kt`:

| Element | Value |
|---|---|
| Banner height | `0.27 × unit`, plus the system bar inset |
| Badge radius | `0.1875 × unit` |
| Badge center | `0.06 × unit` beyond the banner's inner edge |
| White edge | `0.006 × unit`, around the combined banner and badge shape |
| Glyphs | White at `0.13 × radius` over black at `0.22 × radius`, with square caps |
| Text | `0.135 × unit`, centered in the banner |
| Transition | None. It's a hard cut, as in the show |

The Hotdog banner is at the top and Not hotdog is at the bottom. The banners extend under the system bars, and `MainActivity` keeps the system icons light so that they stay readable.

## App icon

An adaptive icon made of vector drawables in `res/drawable/`: `ic_launcher_background`, `ic_launcher_foreground`, and `ic_launcher_monochrome` for themed icons. There are no raster fallbacks, because `minSdk` 26 always uses the adaptive icon. Check icon changes in a real launcher, since the preview renderer appeared to ignore the group scale in the foreground. `docs/assets/app-icon.svg` is a hand-made copy for the README, so update it when the icon changes.

## Not reproduced from the show

- The in-app camera viewfinder
- Any sound. Audio from the clip wasn't analysed
