# 📋 QuickAudit – Field Audit & Reporting App

**QuickAudit** is a modern Android application designed to streamline on-site audits (inventory, safety, quality control, etc.) and enable instant PDF reporting. Built with Kotlin, Spring Boot, and MySQL, this full-stack solution is optimized for field teams and enterprise use.

---

## 📱 Features

- 🔐 **User Authentication** (Firebase/JWT)
- ✅ **Dynamic Audit Checklist** builder
- 📸 **Image Attachments** for observations
- 📍 **GPS Tagging** of audit locations
- 🧾 **PDF Export** for reports
- 🔄 **Offline Mode** with local data sync
- 🎨 **Material 3 UI** with light/dark mode
- 🏗️ **Modular MVVM Architecture**

---

## 🧱 Tech Stack

| Layer          | Tech Used                      |
|----------------|-------------------------------|
| Frontend       | Kotlin, Jetpack Compose, Room |
| Backend        | Kotlin + Spring Boot          |
| Auth           | Firebase Auth / JWT           |
| Storage        | MySQL + Room DB               |
| Networking     | Retrofit, OkHttp              |
| PDF Export     | iText / Android PDF libraries |
| State Mgmt     | ViewModel, LiveData/Flow      |

---

## 📦 Project Structure

quickauditapp/
├── app-android/ # Android app (Kotlin, Jetpack Compose)
└── quickauditapp-backend/ # Backend API (Spring Boot, Kotlin, MySQL)

yaml
Copy
Edit

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Flamingo or later
- JDK 17+
- MySQL 8+
- Maven 3.8+
- Firebase Project (if using Firebase Auth)

### Clone the repo

```bash
git clone https://github.com/yourusername/quickaudit.git
cd quickaudit

🔧 Backend Setup

cd quickauditapp-backend
mvn spring-boot:run
Update application.properties with your MySQL credentials.
Access API at http://localhost:8080/api

📱 Android Setup

Open app-android in Android Studio.
Add Firebase config (google-services.json) if using Firebase.
Run on emulator or physical device.

📄 Example Use Cases

Warehouse Inventory Audit
Fire Safety Compliance Check
Quality Inspection Reports
Facility Maintenance Logs

✨ Future Enhancements

Admin Dashboard (Web)
Push Notifications
Cloud PDF backup
Audit Analytics and Charting

🤝 Contributing

Fork the repo
Create your feature branch (git checkout -b feature/X)
Commit your changes
Push to the branch
Open a Pull Request

📜 License
MIT © Lukesh Gaydhane
