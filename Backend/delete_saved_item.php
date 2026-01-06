<?php
ob_start();
header('Content-Type: application/json');

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

// --- Handle POST Request ---
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    
    $json_data = file_get_contents('php://input');
    $data = json_decode($json_data, true);

    $saved_item_id = $data['saved_item_id'] ?? null;
    $user_id = $data['user_id'] ?? null;

    if (empty($saved_item_id) || empty($user_id)) {
        send_json_response(['success' => false, 'message' => 'Item ID and User ID are required.']);
    }

    // Prepare statement to delete the item, ensuring it belongs to the correct user
    $stmt = $conn->prepare("DELETE FROM saved_items WHERE id = ? AND user_id = ?");
    if (!$stmt) {
        send_json_response(['success' => false, 'message' => 'Database query preparation failed.']);
    }
    
    $stmt->bind_param("ii", $saved_item_id, $user_id);

    if ($stmt->execute()) {
        if ($stmt->affected_rows > 0) {
            send_json_response(['success' => true, 'message' => 'Item deleted successfully.']);
        } else {
            send_json_response(['success' => false, 'message' => 'Item not found or you do not have permission to delete it.']);
        }
    } else {
        send_json_response(['success' => false, 'message' => 'Database Error: ' . $stmt->error]);
    }
    
    $stmt->close();
} else {
    send_json_response(['success' => false, 'message' => 'Invalid request method.']);
}

$conn->close();
ob_end_flush();
?>