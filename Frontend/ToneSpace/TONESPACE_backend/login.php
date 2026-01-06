<?php 
header('Content-Type: application/json'); 
require_once 'db.php'; 
 
$data = json_decode(file_get_contents('php://input'), true); 
$username = $data['username'] ?? ''; 
$password = $data['password'] ?? ''; 
 
try { 
    $stmt = $pdo-
    $stmt- => $username]); 
    $user = $stmt-
 
    if ($user && password_verify($password, $user['password'])) { 
        echo json_encode(['status' => 'success', 'message' => 'Login successful', 'user_id' => $user['id']]); 
    } else { 
        http_response_code(401); 
        echo json_encode(['status' => 'error', 'message' => 'Invalid credentials']); 
    } 
} catch (PDOException $e) { 
    http_response_code(500); 
    echo json_encode(['status' => 'error', 'message' => 'Database error']); 
} 
?> 
