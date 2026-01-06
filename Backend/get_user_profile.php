<?php
// Start output buffering to catch any stray text or warnings
ob_start();
header('Content-Type: application/json');

// Helper function to send a clean JSON response
function send_json_response($data) {
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

    // UPDATED: Selects all the personal information fields
    $stmt = $conn->prepare("SELECT id, name, email, phone, gender, dob, profile_image_url FROM users WHERE id = ?");
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        $user = $result->fetch_assoc();
        send_json_response(['success' => true, 'user' => $user]);
    } else {
        send_json_response(['success' => false, 'message' => 'User not found.']);
    }
    $stmt->close();

} else {
    send_json_response(['success' => false, 'message' => 'Invalid request method.']);
}

$conn->close();
ob_end_flush();
?>