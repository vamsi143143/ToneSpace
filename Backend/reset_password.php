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

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "tonespace_db";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    send_json_response(['success' => false, 'message' => 'Database connection failed.']);
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    
    $json_data = file_get_contents('php://input');
    $data = json_decode($json_data, true);

    $email = $data['email'] ?? null;
    $new_password = $data['new_password'] ?? null;

    if (empty($email) || empty($new_password)) {
        send_json_response(['success' => false, 'message' => 'Email and new password are required.']);
    }

    // Hash the new password
    $new_hashed_password = password_hash($new_password, PASSWORD_BCRYPT);

    // Update the password in the database for the matching email
    $stmt = $conn->prepare("UPDATE users SET password = ? WHERE email = ?");
    $stmt->bind_param("ss", $new_hashed_password, $email);

    if ($stmt->execute()) {
        if ($stmt->affected_rows > 0) {
            send_json_response(['success' => true, 'message' => 'Password has been reset successfully.']);
        } else {
            send_json_response(['success' => false, 'message' => 'No account found with that email address.']);
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