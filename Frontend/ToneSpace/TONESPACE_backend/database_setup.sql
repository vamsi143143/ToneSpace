-- Create the database 
CREATE DATABASE IF NOT EXISTS tonedb; 
USE tonedb; 
 
-- Users table 
CREATE TABLE IF NOT EXISTS users ( 
    id INT AUTO_INCREMENT PRIMARY KEY, 
    username VARCHAR(50) UNIQUE NOT NULL, 
    email VARCHAR(100) UNIQUE NOT NULL, 
    password VARCHAR(255) NOT NULL, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
); 
 
-- Mood entries table 
CREATE TABLE IF NOT EXISTS mood_entries ( 
    id INT AUTO_INCREMENT PRIMARY KEY, 
    user_id INT NOT NULL, 
    mood VARCHAR(50) NOT NULL, 
    notes TEXT, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE 
); 
 
-- Suggestions table 
CREATE TABLE IF NOT EXISTS suggestions ( 
    id INT AUTO_INCREMENT PRIMARY KEY, 
    user_id INT NOT NULL, 
    suggestion TEXT NOT NULL, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE 
); 
 
-- Insert test user (password: password) 
INSERT INTO users (username, email, password) VALUES 
('testuser', 'test@example.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'); 
 
-- Insert sample mood entries 
INSERT INTO mood_entries (user_id, mood, notes) VALUES 
(1, 'happy', 'Feeling great today!'), 
(1, 'calm', 'Had a relaxing day'); 
