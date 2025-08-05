# Modern Android Finance Tracker App

A modern, cloud-powered personal finance tracking Android app built using Kotlin and Jetpack Compose with Material3. Track expenses, manage budgets, and gain insights into your financial behavior — all with a sleek and intuitive UI.

## ✨ Features

- **Onboarding & Authentication**  
  Multi-step onboarding with Firebase email/password and Google sign-in.

- **Dashboard**  
  Financial overview, categorized spending, recent transactions, and visual summaries.

- **Transactions**  
  Add, edit, delete, filter, and sort transactions. Firestore backend ensures real-time sync.

- **Budget Management**  
  Supports 50/30/20 budgeting, custom categories, progress indicators, and validation.

- **Analytics**  
  Visual charts (Pie/Bar), category breakdowns, monthly trends, and smart insights.

- **Settings & Profile**  
  Edit profile (avatar, name, currency), theme toggling, export to CSV, and logout.

- **Material3 & Dark Mode**  
  Fully responsive UI with dynamic theming and smooth animations.

- **Data Export**  
  Export your transactions as `.csv` for personal records or sharing.

## 🛠 Tech Stack & Architecture

- **UI**: Kotlin, Jetpack Compose, Material3  
- **Backend**: Firebase Authentication, Cloud Firestore  
- **Architecture**: MVVM + Repository Pattern  
- **Async & State Management**: Coroutines, StateFlow

## 🚧 Future Advancements

The current version provides a solid foundation for manual finance management. Planned enhancements aim to improve automation, intelligence, and user engagement:

- **🔄 Smart Categorization of Online Payments**  
  Automatically categorize expenses based on merchant names, transaction metadata, or payment methods (e.g., UPI, card, wallets). through reading the SMS 

- **🧠 AI-Driven Financial Assistant**  
  Integrate lightweight ML models to offer personalized insights such as spending alerts, budget suggestions, and savings goals.

- **🎮 Gamification Elements**  
  Introduce reward systems like streaks for daily logging, achievement badges, progress bars, and visual motivators to encourage consistent usage.

- **📊 Predictive Analytics**  
  Visualize trends and forecasts using historical spending data, and identify unusual spikes with anomaly detection.

- **🔔 Smart Notifications**  
  Notify users about nearing budgets, recurring subscriptions, and weekly/monthly summaries through custom reminders.


## 🚀 Getting Started

### Prerequisites

- Android Studio (latest version)
- Firebase project with Auth & Firestore enabled

### Firebase Setup

1. Create a project at [Firebase Console](https://console.firebase.google.com).
2. Add an Android app & download `google-services.json` into `app/` directory.
3. Enable Email/Password & Google Sign-in.
4. Enable Firestore DB.
5. Add your SHA-1 and SHA-256 fingerprints in Firebase settings.

