# SocialGenie

SocialGenie is a cross-platform AI social media content generator, image designer, and scheduling suite built for Android using Kotlin and Jetpack Compose.

## Features

- **Content Creation**: Use AI to draft and organize social media posts.
- **Material 3 Expressive UI**: Aesthetic and modern design leveraging Jetpack Compose and Material 3 guidelines.
- **Scheduling**: Queue generated content for later publication.
- **Analytics & Templates**: Pre-built templates and a dashboard to analyze social media engagement.

## Requirements

- Android Studio or standard Gradle build environment
- Kotlin version 1.9+
- targetSdk 34+

## Building the App

This project uses Gradle for its build system. To compile and run the application locally:

### Option 1: Terminal

Run standard Gradle tasks:
```bash
gradle assembleDebug
```

To run unit tests:
```bash
gradle testDebugUnitTest
```

### Option 2: Android Studio

- Open Android Studio.
- Select `Open an existing Android Studio project` and navigate to this repository's root folder.
- Let Gradle sync and then select `Run -> Run 'app'` from the top menu.

## CI/CD

We provide a basic GitHub Actions workflow in `.github/workflows/android.yml` to automatically build and test the application on push and pull requests to the `main` branch. 
