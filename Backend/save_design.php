<?php
// Start output buffering and set JSON header
ob_start();
header('Content-Type: application/json');

// Helper function for clean JSON responses
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

// --- Handle File Upload and POST data ---
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    send_json_response(['success' => false, 'message' => 'Invalid request method.']);
}

$user_id = $_POST['user_id'] ?? null;
$title = $_POST['title'] ?? 'My Saved Design';

if (empty($user_id)) {
    send_json_response(['success' => false, 'message' => 'User ID is required.']);
}

if (!isset($_FILES['design_image'])) {
    send_json_response(['success' => false, 'message' => 'No image file was uploaded.']);
}

// Create the 'uploads' directory if it doesn't exist
$upload_dir = 'uploads/';
if (!is_dir($upload_dir)) {
    mkdir($upload_dir, 0777, true);
}

// Create a unique file name to prevent overwriting
$file_name = uniqid('design_', true) . '_' . basename($_FILES['design_image']['name']);
$target_file = $upload_dir . $file_name;

// Construct the full URL that the app can use to view the image
// Make sure '/tonespace/' matches your project folder name
$base_url = 'http://' . $_SERVER['HTTP_HOST'] . '/tonespace/';
$image_url = $base_url . $target_file;

// Move the uploaded file to the target directory
if (!move_uploaded_file($_FILES['design_image']['tmp_name'], $target_file)) {
    send_json_response(['success' => false, 'message' => 'Sorry, there was an error uploading your file.']);
}

// --- Save to Database using a Transaction ---
$conn->begin_transaction();

try {
    // 1. Insert the new design into the 'designs' table
    $stmt_design = $conn->prepare("INSERT INTO designs (user_id, image_url, title) VALUES (?, ?, ?)");
    $stmt_design->bind_param("iss", $user_id, $image_url, $title);
    $stmt_design->execute();
    $design_id = $conn->insert_id; // Get the ID of the new design record
    $stmt_design->close();

    // 2. Insert a record into the 'saved_items' table to link the user to the design
    $stmt_saved = $conn->prepare("INSERT INTO saved_items (user_id, design_id) VALUES (?, ?)");
    $stmt_saved->bind_param("ii", $user_id, $design_id);
    $stmt_saved->execute();
    $stmt_saved->close();

    // If everything was successful, commit the changes
    $conn->commit();
    send_json_response(['success' => true, 'message' => 'Design saved successfully!']);

} catch (mysqli_sql_exception $exception) {
    // If any part fails, roll back all database changes
    $conn->rollback();
    send_json_response(['success' => false, 'message' => 'Database Error: ' . $exception->getMessage()]);
}

$conn->close();
ob_end_flush();
?>