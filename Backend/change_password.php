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

    $user_id = $data['user_id'] ?? null;
    $current_password = $data['current_password'] ?? null;
    $new_password = $data['new_password'] ?? null;

    if (empty($user_id) || empty($current_password) || empty($new_password)) {
        send_json_response(['success' => false, 'message' => 'All fields are required.']);
    }

    // Fetch the current hashed password from the database
    $stmt = $conn->prepare("SELECT password FROM users WHERE id = ?");
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        $user = $result->fetch_assoc();
        $hashed_password = $user['password'];

        // Verify the current password
        if (password_verify($current_password, $hashed_password)) {
            
            // Hash the new password
            $new_hashed_password = password_hash($new_password, PASSWORD_BCRYPT);

            // Update the password in the database
            $update_stmt = $conn->prepare("UPDATE users SET password = ? WHERE id = ?");
            $update_stmt->bind_param("si", $new_hashed_password, $user_id);

            if ($update_stmt->execute()) {
                send_json_response(['success' => true, 'message' => 'Password changed successfully.']);
            } else {
                send_json_response(['success' => false, 'message' => 'Database Error: ' . $update_stmt->error]);
            }
            $update_stmt->close();
        } else {
            send_json_response(['success' => false, 'message' => 'Incorrect current password.']);
        }
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