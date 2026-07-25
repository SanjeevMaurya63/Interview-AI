# 🤖 IntervueAI - AI-Powered Mock Interviews

Precision in Recruitment — AI-powered mock interview platform built with **Spring Boot (Java)** and **Vite + React (TypeScript)**.

## 🌟 Overview

The **IntervueAI** is a next-gen AI-powered mock interview platform built to help students and professionals prepare for interviews in a way that actually feels real. It goes beyond generic questions using your resume, job role, and round type to generate personalized, industry-relevant interviews.

What makes IntervueAI special is its ability to give smart, AI-generated feedback after each session including performance insights, improvement tips, and evaluation reports — so you're not just practicing, you're leveling up.

## ✨ Features

- 🔐 **User Authentication** — Sign Up and Sign In with JWT-based authentication (Spring Boot Security)
- 🎛️ **Interactive Dashboard** — Manage your interview preparations with an intuitive interface
- 🤖 **AI-Powered Interview Generation** — Personalized mock interviews using Google Gemini AI
- 📝 **Real-Time Feedback** — AI-generated feedback after each session with ratings, improvement tips, and evaluation summaries

## 🛠️ Tech Stack

### Backend
- ☕ **Java 17** + **Spring Boot 3.4**
- 🔒 **Spring Security** + **JWT Authentication**
- 🗄️ **MySQL** + **Spring Data JPA**
- 🧠 **Spring AI** + **Google Gemini**

### Frontend
- ⚛️ **React 19** + **TypeScript**
- ⚡ **Vite** (build tool)
- 🎨 **Tailwind CSS v4**
- 🧩 **React Router v7**
- 📡 **Axios** (HTTP client)

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- MySQL Server
- Maven

### Backend Setup

1. **Configure MySQL** — Ensure MySQL is running on `localhost:3306`

2. **Set environment variables** (or update `application.properties`):
   ```
   DB_USERNAME=your_mysql_username
   DB_PASSWORD=your_mysql_password
   GOOGLE_GENERATIVE_AI_API_KEY=your_gemini_api_key
   ```

3. **Run the backend**:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   Server starts on `http://localhost:8080`

### Frontend Setup

1. **Install dependencies**:
   ```bash
   cd frontend
   npm install
   ```

2. **Run dev server**:
   ```bash
   npm run dev
   ```
   Opens on `http://localhost:5173` (proxies API to backend)

### Using Run Scripts

- `run-backend.bat` — Starts Spring Boot backend
- `run-frontend.bat` — Starts Vite React frontend

## 📜 License

This project is licensed under the **MIT License**.

---

### 🎉 Happy Coding & Best of Luck for Your Interviews! 🚀

