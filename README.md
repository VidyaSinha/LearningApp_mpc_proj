# LearningApp (Android, Kotlin)

LearningApp is a camera-first learning tool that lets users capture or pick an image, detect and label objects on-device (ML Kit), and review previous scans. It stores annotated images with results, offers a History screen with delete, and has a simple yellow/white rounded UI.

## Features
- Splash → Login/Signup → Main flow (back stack handled)
- Capture preview via TakePicturePreview or pick from Gallery
- ML Kit Object Detection + Image Labeling
- Drawn boxes and confidence on the output image
- Persistent scan history (annotated bitmap + text summary)
- History screen with per‑item Delete
- Quick Actions with a Recents toggle showing top 5 (scrollable)
- Yellow/white theme with rounded components

## Tech Stack
- Kotlin, AndroidX, Material 3 Light
- ML Kit: object-detection, image-labeling
- Activity Result APIs
- Persistence: app files + SharedPreferences (JSON)

## Structure
- `app/src/main/AndroidManifest.xml`: Activities and launcher
- `app/src/main/java/com/example/learningapp/MainActivity.kt`: Camera/gallery, detection, save to history, Recents toggle
- `app/src/main/java/com/example/learningapp/Detector.kt`: ML Kit pipeline and attributes
- `app/src/main/java/com/example/learningapp/DetectionResult.kt`: Detection data model
- `app/src/main/java/com/example/learningapp/ScanHistoryStore.kt`: Save/load/delete scan entries
- `app/src/main/java/com/example/learningapp/HistoryActivity.kt`: History UI
- Layouts: `activity_main.xml`, `activity_history.xml`, `item_history.xml`, `activity_login.xml`, `activity_sign_up.xml`, `activity_splash.xml`
- Drawables: rounded backgrounds `bg_rounded_*.xml`, `progress_bar.xml`, `splash_logo.png`, `mainbg.png`

## Setup
1. Open `LearningApp/` in Android Studio (JDK 11, compileSdk 34, minSdk 24).
2. Let Gradle sync. Dependencies are in `app/build.gradle.kts` and `gradle/libs.versions.toml`.

## Run
- Run the `app` configuration on a device/emulator.
- First launch: Splash → Login. If no account, use Sign Up, then Login.

## Permissions
Declared in manifest:
- `android.permission.CAMERA`
- `android.permission.READ_EXTERNAL_STORAGE` (maxSdkVersion 32)

Camera uses `ActivityResultContracts.TakePicturePreview()`; gallery uses the photo picker intent.

## Usage
- Main screen: tap “Open Camera” or “Gallery”.
- After detection, results show and the annotated image + summary is saved automatically.
- Tap “See all learnt objects” to open History; use Delete to remove items.
- Tap the “Recent” quick action to toggle a scrollable view of the top 5 recent scans.

## Storage
- Images: `filesDir/scans/scan_<timestamp>.jpg`
- Index: `SharedPreferences` key `ScanHistoryPrefs/scans` (JSON array of entries)

## Theming
- Colors in `res/values/colors.xml`; theme in `res/values/themes.xml`
- Rounded shapes in `res/drawable/bg_rounded_light.xml`, `bg_rounded_yellow.xml`, `bg_rounded_card.xml`
- Hero banner image: `res/drawable/mainbg.png`

## Troubleshooting
- Camera preview is null on emulator: try a physical device or ensure emulator camera is enabled.
- First run may download ML Kit models; wait for completion if detection is slow.
- On Android 13+, legacy external storage permission is ignored; gallery picker still works.

## Roadmap Ideas
- Room database + thumbnails & paging
- Better overlay UI and bottom navigation
- Export/share scans
- Automated tests for Detector and ScanHistoryStore

## License
Provided as-is for learning purposes.
