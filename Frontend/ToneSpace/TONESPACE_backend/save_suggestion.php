<?php 
header('Content-Type: application/json'); 
require_once 'db.php'; 
 
$data = json_decode(file_get_contents('php://input'), true); 
$userId = $data['user_id'] ?? 0; 
$suggestion = $data['suggestion'] ?? ''; 
 
try { 
    $stmt = $pdo-
    $stmt-
        'user_id' => $userId, 
        'suggestion' => $suggestion 
    ]); 
 
    echo json_encode(['status' => 'success', 'message' => 'Suggestion submitted successfully']); 
ECHO is off.
} catch (PDOException $e) { 
    http_response_code(500); 
    echo json_encode(['status' => 'error', 'message' => 'Failed to save suggestion']); 
} 
?> 
