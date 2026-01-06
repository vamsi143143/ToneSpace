<?php
// Start output buffering to catch any stray text, warnings, or errors
ob_start();

// This helper function ensures a clean JSON response is always sent
function send_json_response($data) {
    // If the headers haven't been sent yet, send the JSON header
    if (!headers_sent()) {
        header('Content-Type: application/json');
    }
    // Clear any previously buffered output (like PHP warnings)
    ob_clean();
    // Echo the clean JSON data and stop the script
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
    send_json_response(['success' => false, 'message' => 'Database connection failed: ' . $conn->connect_error]);
}

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    send_json_response(['success' => false, 'message' => 'Invalid request method.']);
}
    
$email = $_POST['email'] ?? '';
$password = $_POST['password'] ?? '';

if (empty($email) || empty($password)) {
    send_json_response(['success' => false, 'message' => 'Email and password are required.']);
}

$stmt = $conn->prepare("SELECT id, name, email, password, profile_image_url FROM users WHERE email = ?");
if (!$stmt) {
    send_json_response(['success' => false, 'message' => 'Database query preparation failed.']);
}

$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    send_json_response(['success' => false, 'message' => 'User not found.']);
}

$user = $result->fetch_assoc();
$hashed_password = $user['password'];

if (password_verify($password, $hashed_password)) {
    unset($user['password']); // Don't send the password hash back to the app
    send_json_response(['success' => true, 'message' => 'Login successful!', 'user' => $user]);
} else {
    send_json_response(['success' => false, 'message' => 'Incorrect password.']);
}

$stmt->close();
$conn->close();

// Fallback to clean up the buffer if the script somehow didn't exit in the helper
ob_end_flush();
?>