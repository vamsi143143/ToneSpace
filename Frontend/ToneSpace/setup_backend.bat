@echo off
echo Setting up ToneSpace Backend...
echo.

REM Remove existing directory if it exists
if exist "TONESPACE_backend" (
    echo Removing existing TONESPACE_backend directory...
    rmdir /s /q TONESPACE_backend
)

REM Create new directory
mkdir TONESPACE_backend
cd TONESPACE_backend

echo Creating database configuration...
echo ^<?php > db.php
echo $host = 'localhost'; >> db.php
echo $dbname = 'tonedb'; >> db.php
echo $username = 'root'; >> db.php
echo $password = ''; >> db.php
echo. >> db.php
echo try { >> db.php
echo     $pdo = new PDO("mysql:host=$host;dbname=$dbname", $username, $password); >> db.php
echo     $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION); >> db.php
echo } catch(PDOException $e) { >> db.php
echo     echo "Connection failed: " . $e->getMessage(); >> db.php
echo     die(); >> db.php
echo } >> db.php
echo ?^> >> db.php

echo Creating login endpoint...
echo ^<?php > login.php
echo header('Content-Type: application/json'); >> login.php
echo require_once 'db.php'; >> login.php
echo. >> login.php
echo $data = json_decode(file_get_contents('php://input'), true); >> login.php
echo $username = $data['username'] ?? ''; >> login.php
echo $password = $data['password'] ?? ''; >> login.php
echo. >> login.php
echo try { >> login.php
echo     $stmt = $pdo->prepare("SELECT * FROM users WHERE username = :username"); >> login.php
echo     $stmt->execute(['username' ^=^> $username]); >> login.php
echo     $user = $stmt->fetch(PDO::FETCH_ASSOC); >> login.php
echo. >> login.php
echo     if ($user ^&^& password_verify($password, $user['password'])) { >> login.php
echo         echo json_encode(['status' ^=^> 'success', 'message' ^=^> 'Login successful', 'user_id' ^=^> $user['id']]); >> login.php
echo     } else { >> login.php
echo         http_response_code(401); >> login.php
echo         echo json_encode(['status' ^=^> 'error', 'message' ^=^> 'Invalid credentials']); >> login.php
echo     } >> login.php
echo } catch (PDOException $e) { >> login.php
echo     http_response_code(500); >> login.php
echo     echo json_encode(['status' ^=^> 'error', 'message' ^=^> 'Database error']); >> login.php
echo } >> login.php
echo ?^> >> login.php

echo Creating database setup script...
echo -- Create the database > database_setup.sql
echo CREATE DATABASE IF NOT EXISTS tonedb; >> database_setup.sql
echo USE tonedb; >> database_setup.sql
echo. >> database_setup.sql
echo -- Users table >> database_setup.sql
echo CREATE TABLE IF NOT EXISTS users ( >> database_setup.sql
echo     id INT AUTO_INCREMENT PRIMARY KEY, >> database_setup.sql
echo     username VARCHAR(50) UNIQUE NOT NULL, >> database_setup.sql
echo     email VARCHAR(100) UNIQUE NOT NULL, >> database_setup.sql
echo     password VARCHAR(255) NOT NULL, >> database_setup.sql
echo     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP >> database_setup.sql
echo ); >> database_setup.sql
echo. >> database_setup.sql
echo -- Mood entries table >> database_setup.sql
echo CREATE TABLE IF NOT EXISTS mood_entries ( >> database_setup.sql
echo     id INT AUTO_INCREMENT PRIMARY KEY, >> database_setup.sql
echo     user_id INT NOT NULL, >> database_setup.sql
echo     mood VARCHAR(50) NOT NULL, >> database_setup.sql
echo     notes TEXT, >> database_setup.sql
echo     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, >> database_setup.sql
echo     FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE >> database_setup.sql
echo ); >> database_setup.sql
echo. >> database_setup.sql
echo -- Suggestions table >> database_setup.sql
echo CREATE TABLE IF NOT EXISTS suggestions ( >> database_setup.sql
echo     id INT AUTO_INCREMENT PRIMARY KEY, >> database_setup.sql
echo     user_id INT NOT NULL, >> database_setup.sql
echo     suggestion TEXT NOT NULL, >> database_setup.sql
echo     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, >> database_setup.sql
echo     FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE >> database_setup.sql
echo ); >> database_setup.sql
echo. >> database_setup.sql
echo -- Insert test user (password: password) >> database_setup.sql
echo INSERT INTO users (username, email, password) VALUES >> database_setup.sql
echo ('testuser', 'test@example.com', '^$2y^$10^$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi^'); >> database_setup.sql
echo. >> database_setup.sql
echo -- Insert sample mood entries >> database_setup.sql
echo INSERT INTO mood_entries (user_id, mood, notes) VALUES >> database_setup.sql
echo (1, 'happy', 'Feeling great today!'), >> database_setup.sql
echo (1, 'calm', 'Had a relaxing day'); >> database_setup.sql

echo Creating test interface...
echo ^<!DOCTYPE html^> > test_api.html
echo ^<html^> >> test_api.html
echo ^<head^> >> test_api.html
echo     ^<title^>ToneSpace API Tester^</title^> >> test_api.html
echo     ^<style^> >> test_api.html
echo         body { font-family: Arial, sans-serif; max-width: 800px; margin: 0 auto; padding: 20px; } >> test_api.html
echo         .endpoint { margin-bottom: 20px; padding: 15px; border: 1px solid #ddd; border-radius: 5px; } >> test_api.html
echo         button { padding: 8px 15px; margin: 5px 0; cursor: pointer; } >> test_api.html
echo         pre { background: #f5f5f5; padding: 10px; border-radius: 5px; overflow-x: auto; } >> test_api.html
echo         .success { color: green; } >> test_api.html
echo         .error { color: red; } >> test_api.html
echo     ^</style^> >> test_api.html
echo ^</head^> >> test_api.html
echo ^<body^> >> test_api.html
echo     ^<h1^>ToneSpace API Tester^</h1^> >> test_api.html
echo     ^<div class="endpoint"^> >> test_api.html
echo         ^<h2^>1. Register User^</h2^> >> test_api.html
echo         ^<div^> >> test_api.html
echo             ^<input type="text" id="regUsername" placeholder="Username" value="testuser"^> >> test_api.html
echo             ^<input type="email" id="regEmail" placeholder="Email" value="test@example.com"^> >> test_api.html
echo             ^<input type="password" id="regPassword" placeholder="Password" value="password"^> >> test_api.html
echo             ^<button onclick="registerUser()"^>Register^</button^> >> test_api.html
echo         ^</div^> >> test_api.html
echo         ^<pre id="registerResult"^>^</pre^> >> test_api.html
echo     ^</div^> >> test_api.html
echo     ^<div class="endpoint"^> >> test_api.html
echo         ^<h2^>2. Login^</h2^> >> test_api.html
echo         ^<div^> >> test_api.html
echo             ^<input type="text" id="loginUsername" placeholder="Username" value="testuser"^> >> test_api.html
echo             ^<input type="password" id="loginPassword" placeholder="Password" value="password"^> >> test_api.html
echo             ^<button onclick="loginUser()"^>Login^</button^> >> test_api.html
echo         ^</div^> >> test_api.html
echo         ^<pre id="loginResult"^>^</pre^> >> test_api.html
echo     ^</div^> >> test_api.html
echo     ^<div class="endpoint"^> >> test_api.html
echo         ^<h2^>3. Save Mood^</h2^> >> test_api.html
echo         ^<div^> >> test_api.html
echo             ^<input type="text" id="moodUserId" placeholder="User ID" value="1"^> >> test_api.html
echo             ^<select id="mood"^> >> test_api.html
echo                 ^<option value="happy"^>Happy^</option^> >> test_api.html
echo                 ^<option value="sad"^>Sad^</option^> >> test_api.html
echo                 ^<option value="angry"^>Angry^</option^> >> test_api.html
echo                 ^<option value="calm"^>Calm^</option^> >> test_api.html
echo                 ^<option value="anxious"^>Anxious^</option^> >> test_api.html
echo             ^</select^> >> test_api.html
echo             ^<input type="text" id="moodNotes" placeholder="Notes" value="Feeling good today"^> >> test_api.html
echo             ^<button onclick="saveMood()"^>Save Mood^</button^> >> test_api.html
echo         ^</div^> >> test_api.html
echo         ^<pre id="moodResult"^>^</pre^> >> test_api.html
echo     ^</div^> >> test_api.html
echo     ^<script^> >> test_api.html
echo         const API_BASE = 'http://localhost/TONESPACE_backend'; >> test_api.html
echo. >> test_api.html
echo         async function makeRequest(url, method = 'GET', data = null) { >> test_api.html
echo             const options = { >> test_api.html
echo                 method, >> test_api.html
echo                 headers: { 'Content-Type': 'application/json' }, >> test_api.html
echo             }; >> test_api.html
echo             >> test_api.html
echo             if (data) { >> test_api.html
echo                 options.body = JSON.stringify(data); >> test_api.html
echo             } >> test_api.html
echo. >> test_api.html
echo             try { >> test_api.html
echo                 const response = await fetch(url, options); >> test_api.html
echo                 const result = await response.json(); >> test_api.html
echo                 return { success: response.ok, data: result }; >> test_api.html
echo             } catch (error) { >> test_api.html
echo                 return { success: false, error: error.message }; >> test_api.html
echo             } >> test_api.html
echo         } >> test_api.html
echo. >> test_api.html
echo         async function registerUser() { >> test_api.html
echo             const username = document.getElementById('regUsername').value; >> test_api.html
echo             const email = document.getElementById('regEmail').value; >> test_api.html
echo             const password = document.getElementById('regPassword').value; >> test_api.html
echo. >> test_api.html
echo             const result = await makeRequest(`${API_BASE}/register.php`, 'POST', { >> test_api.html
echo                 username, >> test_api.html
echo                 email, >> test_api.html
echo                 password >> test_api.html
echo             }); >> test_api.html
echo. >> test_api.html
echo             const output = document.getElementById('registerResult'); >> test_api.html
echo             output.className = result.success ? 'success' : 'error'; >> test_api.html
echo             output.textContent = JSON.stringify(result.data || result.error, null, 2); >> test_api.html
echo         } >> test_api.html
echo. >> test_api.html
echo         async function loginUser() { >> test_api.html
echo             const username = document.getElementById('loginUsername').value; >> test_api.html
echo             const password = document.getElementById('loginPassword').value; >> test_api.html
echo. >> test_api.html
echo             const result = await makeRequest(`${API_BASE}/login.php`, 'POST', { >> test_api.html
echo                 username, >> test_api.html
echo                 password >> test_api.html
echo             }); >> test_api.html
echo. >> test_api.html
echo             const output = document.getElementById('loginResult'); >> test_api.html
echo             output.className = result.success ? 'success' : 'error'; >> test_api.html
echo             output.textContent = JSON.stringify(result.data || result.error, null, 2); >> test_api.html
echo. >> test_api.html
echo             if (result.success && result.data && result.data.user_id) { >> test_api.html
echo                 document.getElementById('moodUserId').value = result.data.user_id; >> test_api.html
echo             } >> test_api.html
echo         } >> test_api.html
echo. >> test_api.html
echo         async function saveMood() { >> test_api.html
echo             const userId = document.getElementById('moodUserId').value; >> test_api.html
echo             const mood = document.getElementById('mood').value; >> test_api.html
echo             const notes = document.getElementById('moodNotes').value; >> test_api.html
echo. >> test_api.html
echo             const result = await makeRequest(`${API_BASE}/save_mood.php`, 'POST', { >> test_api.html
echo                 user_id: userId, >> test_api.html
echo                 mood, >> test_api.html
echo                 notes >> test_api.html
echo             }); >> test_api.html
echo. >> test_api.html
echo             const output = document.getElementById('moodResult'); >> test_api.html
echo             output.className = result.success ? 'success' : 'error'; >> test_api.html
echo             output.textContent = JSON.stringify(result.data || result.error, null, 2); >> test_api.html
echo         } >> test_api.html
echo     ^</script^> >> test_api.html
echo ^</body^> >> test_api.html
echo ^</html^> >> test_api.html

echo Creating remaining PHP endpoints...

:: Create register.php
echo ^<?php > register.php
echo header('Content-Type: application/json'); >> register.php
echo require_once 'db.php'; >> register.php
echo. >> register.php
echo $data = json_decode(file_get_contents('php://input'), true); >> register.php
echo $username = $data['username'] ?? ''; >> register.php
echo $email = $data['email'] ?? ''; >> register.php
echo $password = password_hash($data['password'] ?? '', PASSWORD_DEFAULT); >> register.php
echo. >> register.php
echo try { >> register.php
echo     // Check if username already exists >> register.php
echo     $stmt = $pdo->prepare("SELECT id FROM users WHERE username = :username"); >> register.php
echo     $stmt->execute(['username' ^=^> $username]); >> register.php
echo     >> register.php
echo     if ($stmt->rowCount() ^> 0) { >> register.php
echo         http_response_code(400); >> register.php
echo         echo json_encode(['status' ^=^> 'error', 'message' ^=^> 'Username already exists']); >> register.php
echo         exit(); >> register.php
echo     } >> register.php
echo. >> register.php
echo     // Create new user >> register.php
echo     $stmt = $pdo->prepare("INSERT INTO users (username, email, password) VALUES (:username, :email, :password)"); >> register.php
echo     $stmt->execute([ >> register.php
echo         'username' ^=^> $username, >> register.php
echo         'email' ^=^> $email, >> register.php
echo         'password' ^=^> $password >> register.php
echo     ]); >> register.php
echo. >> register.php
echo     $userId = $pdo->lastInsertId(); >> register.php
echo     echo json_encode(['status' ^=^> 'success', 'message' ^=^> 'User registered successfully', 'user_id' ^=^> $userId]); >> register.php
echo     >> register.php
echo } catch (PDOException $e) { >> register.php
echo     http_response_code(500); >> register.php
echo     echo json_encode(['status' ^=^> 'error', 'message' ^=^> 'Registration failed']); >> register.php
echo } >> register.php
echo ?^> >> register.php

:: Create save_mood.php
echo ^<?php > save_mood.php
echo header('Content-Type: application/json'); >> save_mood.php
echo require_once 'db.php'; >> save_mood.php
echo. >> save_mood.php
echo $data = json_decode(file_get_contents('php://input'), true); >> save_mood.php
echo $userId = $data['user_id'] ?? 0; >> save_mood.php
echo $mood = $data['mood'] ?? ''; >> save_mood.php
echo $notes = $data['notes'] ?? ''; >> save_mood.php
echo. >> save_mood.php
echo try { >> save_mood.php
echo     $stmt = $pdo->prepare("INSERT INTO mood_entries (user_id, mood, notes, created_at) VALUES (:user_id, :mood, :notes, NOW())"); >> save_mood.php
echo     $stmt->execute([ >> save_mood.php
echo         'user_id' ^=^> $userId, >> save_mood.php
echo         'mood' ^=^> $mood, >> save_mood.php
echo         'notes' ^=^> $notes >> save_mood.php
echo     ]); >> save_mood.php
echo. >> save_mood.php
echo     echo json_encode(['status' ^=^> 'success', 'message' ^=^> 'Mood saved successfully']); >> save_mood.php
echo     >> save_mood.php
echo } catch (PDOException $e) { >> save_mood.php
echo     http_response_code(500); >> save_mood.php
echo     echo json_encode(['status' ^=^> 'error', 'message' ^=^> 'Failed to save mood']); >> save_mood.php
echo } >> save_mood.php
echo ?^> >> save_mood.php

:: Create save_suggestion.php
echo ^<?php > save_suggestion.php
echo header('Content-Type: application/json'); >> save_suggestion.php
echo require_once 'db.php'; >> save_suggestion.php
echo. >> save_suggestion.php
echo $data = json_decode(file_get_contents('php://input'), true); >> save_suggestion.php
echo $userId = $data['user_id'] ?? 0; >> save_suggestion.php
echo $suggestion = $data['suggestion'] ?? ''; >> save_suggestion.php
echo. >> save_suggestion.php
echo try { >> save_suggestion.php
echo     $stmt = $pdo->prepare("INSERT INTO suggestions (user_id, suggestion, created_at) VALUES (:user_id, :suggestion, NOW())"); >> save_suggestion.php
echo     $stmt->execute([ >> save_suggestion.php
echo         'user_id' ^=^> $userId, >> save_suggestion.php
echo         'suggestion' ^=^> $suggestion >> save_suggestion.php
echo     ]); >> save_suggestion.php
echo. >> save_suggestion.php
echo     echo json_encode(['status' ^=^> 'success', 'message' ^=^> 'Suggestion submitted successfully']); >> save_suggestion.php
echo     >> save_suggestion.php
echo } catch (PDOException $e) { >> save_suggestion.php
echo     http_response_code(500); >> save_suggestion.php
echo     echo json_encode(['status' ^=^> 'error', 'message' ^=^> 'Failed to save suggestion']); >> save_suggestion.php
echo } >> save_suggestion.php
echo ?^> >> save_suggestion.php

:: Create get_suggestions.php
echo ^<?php > get_suggestions.php
echo header('Content-Type: application/json'); >> get_suggestions.php
echo require_once 'db.php'; >> get_suggestions.php
echo. >> get_suggestions.php
echo $userId = $_GET['user_id'] ?? 0; >> get_suggestions.php
echo. >> get_suggestions.php
echo if (!$userId) { >> get_suggestions.php
echo     http_response_code(400); >> get_suggestions.php
echo     echo json_encode(['status' ^=^> 'error', 'message' ^=^> 'User ID is required']); >> get_suggestions.php
echo     exit(); >> get_suggestions.php
echo } >> get_suggestions.php
echo. >> get_suggestions.php
echo try { >> get_suggestions.php
echo     $stmt = $pdo->prepare("SELECT * FROM suggestions WHERE user_id = :user_id ORDER BY created_at DESC"); >> get_suggestions.php
echo     $stmt->execute(['user_id' ^=^> $userId]); >> get_suggestions.php
echo     $suggestions = $stmt->fetchAll(PDO::FETCH_ASSOC); >> get_suggestions.php
echo. >> get_suggestions.php
echo     echo json_encode(['status' ^=^> 'success', 'data' ^=^> $suggestions]); >> get_suggestions.php
echo     >> get_suggestions.php
echo } catch (PDOException $e) { >> get_suggestions.php
echo     http_response_code(500); >> get_suggestions.php
echo     echo json_encode(['status' ^=^> 'error', 'message' ^=^> 'Failed to fetch suggestions']); >> get_suggestions.php
echo } >> get_suggestions.php
echo ?^> >> get_suggestions.php

echo.
echo Backend setup complete!
echo.
echo Next steps:
echo 1. Start XAMPP and ensure Apache and MySQL are running
echo 2. Import the database_setup.sql file into phpMyAdmin
echo 3. Access the test interface at: http://localhost/TONESPACE_backend/test_api.html
echo.
pause
