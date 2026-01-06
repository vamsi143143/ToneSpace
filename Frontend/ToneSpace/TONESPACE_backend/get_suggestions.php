<?php 
header('Content-Type: application/json'); 
require_once 'db.php'; 
 
$userId = $_GET['user_id'] ?? 0; 
 
if (!$userId) { 
    http_response_code(400); 
    echo json_encode(['status' => 'error', 'message' => 'User ID is required']); 
    exit(); 
} 
 
try { 
    $stmt = $pdo-
    $stmt- => $userId]); 
    $suggestions = $stmt-
 
    echo json_encode(['status' => 'success', 'data' => $suggestions]); 
ECHO is off.
} catch (PDOException $e) { 
    http_response_code(500); 
    echo json_encode(['status' => 'error', 'message' => 'Failed to fetch suggestions']); 
} 
?> 
