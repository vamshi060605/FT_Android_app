# Modern Android Finance Tracker App

A modern, cloud-powered personal finance tracker Android app built with Jetpack Compose and Material3. Manage your finances with a beautiful, intuitive UI, real-time data sync, and secure authentication.

---

## Features
- **Onboarding & Authentication**: Multi-step onboarding, email/password & Google sign-in (Firebase Auth).
- **Dashboard**: Financial summary, categorized spending, recent transactions, and charts.
- **Transactions**: Add, edit, delete, filter, and sort transactions. Firestore integration.
- **Budget**: 50/30/20 split, custom categories, progress bars, and validation.
- **Analytics**: Pie/bar charts, category breakdowns, monthly trends, and smart insights.
- **Settings & Profile**: Edit profile (avatar, name, currency), theme toggle, data export, and logout.
- **Material3 & Dark Mode**: Modern UI with dark/light mode toggle and consistent theming.
- **Data Export**: Export transactions as CSV for sharing or backup.

---

## Tech Stack & Architecture
- **Kotlin** & **Jetpack Compose** for UI
- **Material3** for modern design
- **Firebase Authentication** (email/password, Google)
- **Cloud Firestore** for real-time data
- **MVVM Architecture** with repositories and view models
- **StateFlow** and **coroutines** for reactive UI

---

## Getting Started

### Prerequisites
- Android Studio (latest recommended)
- Kotlin, Jetpack Compose, Material3
- [Firebase Project](https://console.firebase.google.com/)

### Firebase Setup
1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/).
2. Add an Android app to your Firebase project.
3. Download the `google-services.json` file and place it in the `app/` directory.
4. Enable **Authentication** (Email/Password, Google) in Firebase Console.
5. Enable **Cloud Firestore** in Firebase Console.
6. Add your app's SHA-1 and SHA-256 fingerprints in Firebase settings (for Google sign-in).

### Project Setup
1. Clone or download this repository.
2. Open in Android Studio.
3. Sync Gradle and build the project.
4. Run on an emulator or device.

---

## Usage & Customization
- **Currency**: Defaults to INR. Change in Settings/Profile screen.
- **Categories**: Edit category names and splits in Budget screen.
- **Theming**: Toggle dark/light mode in Settings.
- **Data Export**: Export your transactions as CSV from Settings.
- **Profile**: Edit avatar, name, and currency in one dialog.

---

## Support & About
- For support, feedback, or feature requests, use the Settings > Support section in the app.
- About and credits are available in the Settings > About section.

---

## License
MIT 