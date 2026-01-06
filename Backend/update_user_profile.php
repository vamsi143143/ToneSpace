<?php
// Start output buffering to catch any stray text or warningsob_start();
header('Content-Type: application/json');

// Helper function to send a clean JSON response, no matter what.
function send_json_response($data) {
    // If headers haven't been sent, set the content type
    if (!headers_sent()) {
        header('Content-Type: application/json');
    }
    // Clear any previously buffered output (this wipes PHP warnings)
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

// --- Handle POST Request ---
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    
    // Read raw JSON from the request body
    $json_data = file_get_contents('php://input');
    $data = json_decode($json_data, true);

    // Read variables from the decoded JSON data
    $user_id = $data['user_id'] ?? null;
    
    if (empty($user_id)) {
        send_json_response(['success' => false, 'message' => 'User ID is required.']);
    }
    
    // Dynamically build the SQL query based on the fields provided
    $fields_to_update = [];
    $params = [];
    $types = "";

    // Use array_key_exists to correctly handle fields that might be sent as empty strings
    if (array_key_exists('name', $data))   { $fields_to_update[] = "name = ?";   $params[] = $data['name'];   $types .= "s"; }
    if (array_key_exists('phone', $data))  { $fields_to_update[] = "phone = ?";  $params[] = $data['phone'];  $types .= "s"; }
    if (array_key_exists('gender', $data)) { $fields_to_update[] = "gender = ?"; $params[] = $data['gender']; $types .= "s"; }
    if (array_key_exists('dob', $data))    { $fields_to_update[] = "dob = ?";    $params[] = $data['dob'];    $types .= "s"; }

    if (empty($fields_to_update)) {
        send_json_response(['success' => true, 'message' => 'No new information was provided to update.']);
    }

    $sql = "UPDATE users SET " . implode(", ", $fields_to_update) . " WHERE id = ?";
    $params[] = $user_id;
    $types .= "i";
    
    $stmt = $conn->prepare($sql);
    if (!$stmt) {
        send_json_response(['success' => false, 'message' => 'Database query preparation failed: ' . $conn->error]);
    }

    $stmt->bind_param($types, ...array_values($params));

    if ($stmt->execute()) {
        send_json_response(['success' => true, 'message' => 'Profile updated successfully.']);
    } else {
        // Include the specific database error for easier debugging
        send_json_response(['success' => false, 'message' => 'Database Error: ' . $stmt->error]);
    }
    
    $stmt->close();
} else {
     send_json_response(['success' => false, 'message' => 'Invalid request method.']);
}

$conn->close();

// Fallback to clean up the buffer if the script somehow didn't exit
ob_end_flush();
?>