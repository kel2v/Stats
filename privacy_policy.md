# Privacy Policy for Stats

- **App name:** Stats
- **Package name:** `com.bytemanager.stats`
- **App version:** 1.0.0
- **Effective date:** October 23, 2026

## 1. Introduction

This Privacy Policy explains how the Stats app (“Stats” or “the app”) handles information. Stats is a battery utility: it shows your device’s current battery information and, if you choose to turn it on, keeps a history of that information on your device so you can view it as graphs.

By using Stats, you agree to the practices described in this policy. If you do not agree, please do not use the app.

## 2. Summary

- **Stats works on your device.** The app does not knowingly access the internet or upload any data from your device. (Files you export yourself and Android’s own backup features are covered in Sections 6 and 7.)
- **No accounts, ads or tracking.** The app does not require an account, does not ask for personal information such as your name, email address or phone number, does not show ads, and does not collect analytics or app-usage metadata.
- **Logging is your choice.** Battery info logging is **off by default**. The app only logs battery information if you turn logging on.
- **You stay in control.** You can switch features on or off in the app, and you can export, import or reset the logging database at any time.

The sections below explain these points in more detail.

## 3. Information the app handles

### 3.1 Current battery information

To show your battery status on the home screen, the app reads information provided by the Android system: battery technology, battery health, charging status, battery level (%), battery voltage (V) and battery temperature (°C or °F). This information is displayed to you on your device.

### 3.2 Battery info logging (optional)

Logging is turned off by default. If you turn it on, the app runs a foreground service that listens for battery updates from the Android system and saves the following to a database stored locally on your device:

- timestamp
- battery health
- charging status
- battery level (%)
- battery voltage
- battery temperature

This data is used to show your battery history as graphs (see Section 4).

### 3.3 App settings

Your choices in the app, such as whether logging is turned on, whether notifications are shown, and which graph options are enabled, are stored locally on your device so the app can remember them.

### 3.4 Technical error log

The app records technical information about critical errors, together with the context in which they occurred, by adding it to a private file in the app’s internal storage. In version 1.0.0:

- the app only adds to this file; it has no feature for viewing, sharing or deleting it, and
- the app does **not** send the file to the developer or to anyone else (see Section 7 regarding Android’s own backups).

The file exists to support diagnosing problems with the app. It stays on your device until you clear the app’s storage in Android’s settings or uninstall the app.

In a future update, the app may offer a way to use the contents of this file to report issues to the developer. If that happens, this Privacy Policy will be updated to describe how it works.

## 4. How the information is used

The information described above is used on your device only, to provide the app’s features:

- **Home screen:** showing your current battery information.
- **History:** recording battery information over time, if you have turned logging on.
- **Notifications:** showing the latest battery level, temperature and voltage in a notification, with the temperature shown as the notification icon so you can see it without opening the notification panel. This is optional. You can turn it on or off in the in-app settings, and it requires battery info logging to be turned on.
- **Graphs:** showing your logged data as day-wise, 24-hour graphs of battery level, temperature and voltage. Graph options such as moving average and curve smoothing (Catmull-Rom) are display settings that you can turn on or off in the in-app settings.
- **Import, export and reset:** letting you manage the logging database (see Section 6).

The developer does not use this information for advertising, profiling or analytics.

## 5. Permissions and notifications

Stats declares the following permissions in its manifest:

- `POST_NOTIFICATIONS`: lets the app show notifications. The app prompts you to enable notifications when it launches if they are not already enabled. This is optional. You may decline, in which case notifications will not be shown, and you can change your decision at any time in Android’s system settings. Even if notifications are enabled in Android’s settings, you can still turn them off in the app’s in-app settings. Notifications are generated locally on your device.
- `FOREGROUND_SERVICE` and `FOREGROUND_SERVICE_SPECIAL_USE`: let the logging run as a foreground service so it can keep running in the background and battery updates are not missed. Android requires a foreground service to show a notification while it is running.
- `RECEIVE_BOOT_COMPLETED`: lets the app restart the logging service after your device restarts (if logging is turned on), so battery updates are not missed because the app was not running after boot.

These permissions are used only for the purposes above. They do not give the app access to your contacts, location, camera, microphone, photos or other personal content.

## 6. Import, export and reset

From within the app, you can import, export or reset (clear) the logging database. These actions take place only when you choose to perform them.

- **Export** creates a file containing your logged battery data. The app does not send this file anywhere on its own. Once exported, the file is outside the app’s control, and how you store or share it is up to you.
- **Import** loads logged data from a file that you provide into the app’s local database.
- **Reset** removes the logged data from the app’s database.

## 7. Backups

The app does not opt out of Android’s backup features and does not define its own backup rules, so Android’s default backup behavior applies. Depending on your device and settings, Android (for example, through Google’s backup service or a device-to-device transfer to a new device) may include the app’s data, such as its settings, logged battery data and technical error log, in backups or transfers. These backups are created and managed by Android and your backup provider, not by Stats, and the developer has no access to them. You can manage or turn off device backups in your device’s system settings.

## 8. Sharing and third parties

The developer does not sell, rent or share your data, and the app itself does not knowingly send any data to the developer or to any third party. Stats does not include third-party advertising, analytics or crash-reporting services.

Stats is built using open-source libraries and tools, including AndroidX/Jetpack libraries (for example Compose, Material 3, Navigation, Lifecycle, Room, WorkManager and DataStore), Dagger Hilt, the Vico charting library, Kotlin and KSP, along with build and testing tools. These are used to build and run the app’s on-device features. To the best of the developer’s knowledge, none of them are used to collect or transmit data from your device.

## 9. Data retention and your choices

- Turn battery info logging and notifications on or off in the app’s in-app settings. Notifications can also be controlled in Android’s system settings.
- Delete your logged data by resetting the logging database in the app.
- Delete all of the app’s data (logged data, settings and the technical error log) by clearing the app’s storage in Android’s settings or by uninstalling the app.
- Files you have exported and any Android backups are separate from the app’s internal data and may not be removed by the actions above. Manage exported files where you saved them, and manage backups in your device’s system settings or with your backup provider. Keep in mind that a backup may be used to restore the app’s data if you reinstall the app.

Because the developer does not collect or hold your data, there is no data held by the developer to access, correct or delete on request.

## 10. Security

The app stores its data in its private storage on your device, which Android protects with its app sandbox, and the app is not designed to send your data off your device. No method of storage is completely secure, however, and the protection of your data also depends on your device’s own security, such as screen lock and system updates. Files that you export, and any backups, are outside the app’s protection.

## 11. Children’s privacy

Stats is a general-purpose utility app. It does not knowingly collect personal information from anyone, including children.

## 12. Changes to this policy

The developer may update this Privacy Policy from time to time, for example when new features are added in a later version of the app. The updated policy will show a new effective date at the top. Please review it periodically.

## 13. Contact

If you have questions about this Privacy Policy, you can contact the developer:

- **Developer:** [ByteManager]
- **Email:** [play9u8gstoreg7@proton.me]
