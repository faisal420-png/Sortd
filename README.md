# Sortd

Free, Android-only save-for-later app. Inspired by Albo.

## Status

Scaffold only. Empty Compose project with Hilt + Room + DataStore wired.

## Stack

- Kotlin 1.9.20, Compose BOM 2024.02.00
- Hilt 2.50, Room 2.6.1
- Min SDK 26, Target SDK 34
- Gradle 8.2, AGP 8.1.4, JVM 17

## Build

```
./gradlew assembleDebug
```

## Roadmap

- [ ] Save links (URL + title + thumbnail)
- [ ] Folders / collections
- [ ] List + grid views
- [ ] Map view (saved locations)
- [ ] Share-target intent (save from any app)
- [ ] Reels/Shorts metadata extraction
- [ ] Local-only, no account, free forever
