# J.A.R.V.I.S — Native Android Launcher

A native Kotlin Android HOME launcher for Android 9 (API 28) and newer.

## First-build features
- Registers as an Android HOME launcher and as a normal app activity.
- Discovers launchable installed apps using Android package visibility queries.
- Shows real installed-app icons in a four-column grid.
- Searches installed apps and launches the selected app.
- Dark navy and electric-blue ZYVIX styling with clock and date.

## Build
GitHub Actions builds a debug APK on every push to `main` and on manual dispatch. Download the APK from the workflow run's Artifacts section.

## Install
Install the generated debug APK, press Home, select **JARVIS OS**, and choose Always to make it your default launcher. This is the native launcher foundation; assistant intelligence, widgets, and deeper device controls are not implemented in this first build.
