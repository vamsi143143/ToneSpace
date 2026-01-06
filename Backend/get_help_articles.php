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
    // Fetch all help articles, ordered by the 'display_order' column
    $stmt = $conn->prepare("SELECT id, title, content, category FROM help_articles ORDER BY display_order ASC");
    if (!$stmt) {
        send_json_response(['success' => false, 'message' => 'Database query preparation failed.']);
    }
    
    $stmt->execute();
    $result = $stmt->get_result();
    
    $articles = [];
    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $articles[] = $row;
        }
    }
    
    send_json_response(['success' => true, 'articles' => $articles]);

    $stmt->close();
} else {
    send_json_response(['success' => false, 'message' => 'Invalid request method.']);
}

$conn->close();
ob_end_flush();
?>