<?php 
header('Content-Type: application/json'); 
require_once 'db.php'; 
 
$data = json_decode(file_get_contents('php://input'), true); 
$userId = $data['user_id'] ?? 0; 
$mood = $data['mood'] ?? ''; 
$notes = $data['notes'] ?? ''; 
 
try { 
    $stmt = $pdo-
    $stmt-
        'user_id' => $userId, 
        'mood' => $mood, 
        'notes' => $notes 
    ]); 
 
    echo json_encode(['status' => 'success', 'message' => 'Mood saved successfully']); 
ECHO is off.
} catch (PDOException $e) { 
    http_response_code(500); 
    echo json_encode(['status' => 'error', 'message' => 'Failed to save mood']); 
} 
?> 
