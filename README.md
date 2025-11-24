✍️ Story Generator (AI-Powered)

A fully independent personal project that generates creative stories using Google Gemini AI, built with Spring Boot, MySQL, and a clean web interface.
The system transforms simple words into rich AI-generated stories while maintaining secure key management and robust backend logic.

⭐ Key Features
🤖 AI Story Generation

Enter simple comma-separated words

Optional genre/style

Generates a full creative story instantly

Powered by Google Gemini API

Strong error handling for invalid keys, quota limits, etc.

🔐 Secure Backend

Spring Boot REST API

API key is loaded through environment variables

No hardcoded secrets

Includes Spring Security (optional login support)

💾 Story History Database

Each story request is stored with:
✔ Words
✔ Genre
✔ Final generated story
✔ Timestamp

Uses Spring Data JPA + MySQL

🌐 Simple & Clean Web Interface

User-friendly HTML/CSS interface

Instant story display

Popup-style error messages

Mobile-friendly

🛠️ Technologies Used
Frontend

HTML

CSS

JavaScript

Backend

Spring Boot

Spring Web

Spring Data JPA

Spring Security

WebClient / OkHttp

(Optional) Spring AI Gemini Integration

Database

MySQL

AI

Google Gemini API

REST integration

Build Tool

Maven

🚀 How to Run the Project
1️⃣ Install Requirements

Java 21+

MySQL 8+

Maven

Google Gemini API Key

2️⃣ Create the Database
CREATE DATABASE story_generator;

3️⃣ Configure application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/story_generator
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

ai.gemini.key=${AI_GEMINI_KEY}

4️⃣ Add Your Gemini API Key
PowerShell
setx AI_GEMINI_KEY "YOUR_ACTUAL_KEY_HERE"


Then restart your terminal or IDE.

5️⃣ Build & Start the App
mvn clean package
mvn spring-boot:run


or:

java -jar target/story-generator-0.0.1-SNAPSHOT.jar

6️⃣ Open in Browser

🌐 http://localhost:8080

📱 Features for Users
✨ Story Creation

Enter keywords

Choose creative genre

Generate stories

Save to history

View instantly in UI

🛡️ Backend Safety Features

API key never exposed

Handles 400, 401, 403, 429, 500 AI errors

Auto-retry for quota wait time

🧪 Testing Areas

API key validation

Quota exceeded handling

Database insert/retrieval

Frontend form validation

Spring Security login flow (if enabled)

Error popup display

❗ Troubleshooting

Issue	Cause	Fix

Invalid API key	Wrong key or missing	Set correct AI_GEMINI_KEY

429 Too Many Requests	Free quota limit	Wait 1 minute or upgrade plan

MySQL connection failure	Wrong credentials	Update username/password

Port 8080 busy	Another service running	Change to server.port=8081

Maven plugin missing	Wrong directory	Run Maven in folder with pom.xml


📄 License

This project is licensed under the MIT License.
