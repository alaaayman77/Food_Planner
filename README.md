# 🍽️ Munchly — Meal Planning & Recipe Discovery App

Munchly is an Android mobile application that helps users discover recipes, plan meals, and manage their favorite dishes in one place.
The app provides a seamless experience with offline support, cloud synchronization, and personalized meal planning features.

This project was developed during the **Information Technology Institute (ITI) Intensive Training**.

---

## 📱 Features

* 🏠 Home screen with daily meal inspiration
* 🌍 Browse meals by categories and countries
* 🔍 Advanced search with multi-filter support
* ❤️ Favorites management
* 📅 Meal planning and scheduling
* 👤 User profile with password update
* 🔐 Google & Twitter authentication
* 👥 Guest mode (browse without account)
* 🔄 Real-time synchronization between local database and cloud
* 📡 Offline support using Room Database

---

## 🏗️ Architecture

The project follows **MVP (Model–View–Presenter)** architecture to ensure:

* Separation of concerns
* Testability
* Maintainability
* Scalable code structure

---

## ⚙️ Tech Stack

* **Language:** Java
* **Architecture:** MVP (Model–View–Presenter)
* **Reactive Programming:** RxJava
* **Networking:** Retrofit
* **Local Database:** Room
* **Authentication:** Firebase Authentication
* **Cloud Database:** Firebase Firestore
* **API:** TheMealDB API

---

## 🔄 Data Flow

1. Data is fetched from **TheMealDB API** using Retrofit.
2. Responses are processed asynchronously using RxJava.
3. Data is cached locally using Room Database.
4. User-specific data is synchronized with Firebase Firestore.
5. Authentication is handled via Firebase Authentication.

---

## 🚀 Getting Started

### Prerequisites

* Android Studio (latest version recommended)
* Minimum SDK: 24+
* Internet connection for API & Firebase services

### Installation

1. Clone the repository:

```bash
git clone https://github.com/YOUR_USERNAME/Munchly.git
```

2. Open the project in Android Studio.

3. Add your Firebase configuration file:

```
app/google-services.json
```

4. Sync Gradle and run the app.

---

## 🔑 Firebase Setup

To run this project, you need to:

* Create a Firebase project
* Enable Authentication (Google & Email)
* Enable Firestore Database
* Download `google-services.json` into the app folder

---


## 🎯 Learning Outcomes

This project helped strengthen my skills in:

* Android development with Java
* Clean architecture principles
* Reactive programming with RxJava
* REST API integration
* Local caching and offline-first design
* Firebase authentication and cloud synchronization
* Designing scalable mobile applications


