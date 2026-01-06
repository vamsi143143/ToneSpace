<?php
// Start output buffering to catch any stray text or warnings
ob_start();
header('Content-Type: application/json');

// Helper function to send a clean JSON response
function send_json_response($data) {
    if (!headers_sent()) {
        header('Content-Type: application/json');
    }
    ob_clean();
    echo json_encode($data);
    exit();
}

// --- Database Connection ---
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "tonespace_db";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    send_json_response(['success' => false, 'message' => 'Database connection failed.']);
}

// --- Handle GET Request ---
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $user_id = $_GET['user_id'] ?? null;

    if (empty($user_id)) {
        send_json_response(['success' => false, 'message' => 'User ID is required.']);
    }

    // Fetch all notifications for the user, newest first
    $stmt = $conn->prepare("SELECT id, message, is_read, created_at FROM notifications WHERE user_id = ? ORDER BY created_at DESC");
    if (!$stmt) {
        send_json_response(['success' => false, 'message' => 'Database query preparation failed.']);
    }
    
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $notifications = [];
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            // To make it more JSON-friendly, cast is_read to a boolean
            $row['is_read'] = (bool)$row['is_read'];
            $notifications[] = $row;
        }
    }
    
    send_json_response(['success' => true, 'notifications' => $notifications]);

    $stmt->close();
} else {
    send_json_response(['success' => false, 'message' => 'Invalid request method.']);
}

$conn->close();
ob_end_flush();
?>