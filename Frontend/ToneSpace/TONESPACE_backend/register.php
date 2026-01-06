<?php 
header('Content-Type: application/json'); 
require_once 'db.php'; 
 
$data = json_decode(file_get_contents('php://input'), true); 
$username = $data['username'] ?? ''; 
$email = $data['email'] ?? ''; 
$password = password_hash($data['password'] ?? '', PASSWORD_DEFAULT); 
 
try { 
    // Check if username already exists 
    $stmt = $pdo-
    $stmt- => $username]); 
ECHO is off.
    if ($stmt- { 
        http_response_code(400); 
        echo json_encode(['status' => 'error', 'message' => 'Username already exists']); 
        exit(); 
    } 
 
    // Create new user 
    $stmt = $pdo-
    $stmt-
        'username' => $username, 
        'email' => $email, 
        'password' => $password 
    ]); 
 
    $userId = $pdo-
    echo json_encode(['status' => 'success', 'message' => 'User registered successfully', 'user_id' => $userId]); 
ECHO is off.
} catch (PDOException $e) { 
    http_response_code(500); 
    echo json_encode(['status' => 'error', 'message' => 'Registration failed']); 
} 
?> 
