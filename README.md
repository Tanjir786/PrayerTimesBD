# Prayer Times BD (Android Studio starter)

A Bengali-first Android prayer-time app inspired by the supplied reference screenshot. It includes a district selector for Bangladesh, a next-prayer countdown, sunrise/sunset, daily prayer rows, and per-prayer UI alarm toggles.

## Open and run
1. Install Android Studio (current stable) and Android SDK 35.
2. Open this folder as an existing Gradle project.
3. Let Gradle sync, then run on an Android device/emulator.

## Important accuracy and production notes
- Bangladesh uses Asia/Dhaka (UTC+6); local prayer times still vary by district.
- This starter calculates approximate solar times locally. Fajr/Isha angles and Asr method can differ by local Islamic authority; validate against a trusted Bangladesh timetable before release.
- District-center coordinates are approximate and should be verified before publishing.
- Alarm controls are UI-only in this starter; Android notification permissions, exact alarms, sound/vibration, background scheduling, and reboot rescheduling are not implemented yet.
- Hijri date is currently placeholder text and should be connected to a vetted Hijri calendar source or a configurable calculation.
- For a public release, add calculation-method settings, madhhab/Asr options, manual minute offsets, location permission/GPS, offline tests, accessibility, and verification against Bangladesh Islamic Foundation timetables.
