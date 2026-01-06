<?php
header('Content-Type: application/json');

// Database connection
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "tonespace";

$conn = new mysqli($servername, $username, $password, $dbname);if ($conn->connect_error) {
    die(json_encode(['success' => false, 'message' => 'Database connection failed: ' . $conn->connect_error]));
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $user_id = $_POST['user_id'] ?? null;
    $item_id = $_POST['item_id'] ?? null;
    $item_type = $_POST['item_type'] ?? null; // e.g., 'design', 'palette', 'furniture'

    if (!$user_id || !$item_id || !$item_type) {
        echo json_encode(['success' => false, 'message' => 'User ID, item ID, and item type are required.']);
        exit();
    }

    // --- Check if the item is already saved ---
    $check_stmt = $conn->prepare("SELECT id FROM saved_items WHERE user_id = ? AND item_id = ? AND item_type = ?");
    $check_stmt->bind_param("iis", $user_id, $item_id, $item_type);
    $check_stmt->execute();
    $result = $check_stmt->get_result();

    if ($result->num_rows > 0) {
        // --- Item is already saved, so UNSAVE it ---
        $row = $result->fetch_assoc();
        $saved_item_id = $row['id'];
        
        $delete_stmt = $conn->prepare("DELETE FROM saved_items WHERE id = ?");
        $delete_stmt->bind_param("i", $saved_item_id);
        
        if($delete_stmt->execute()){
             echo json_encode(['success' => true, 'action' => 'unsaved', 'message' => 'Item removed from your saved list.']);
        } else {
             echo json_encode(['success' => false, 'message' => 'Failed to remove item.']);
        }
        $delete_stmt->close();
    } else {
        // --- Item is not saved, so SAVE it ---
        $stmt = $conn->prepare("INSERT INTO saved_items (user_id, item_id, item_type) VALUES (?, ?, ?)");
        $stmt->bind_param("iis", $user_id, $item_id, $item_type);

        if ($stmt->execute()) {
            echo json_encode([
                'success' => true,
                'action' => 'saved',
                'message' => 'Item saved successfully!',
                'saved_item_id' => $stmt->insert_id
            ]);
        } else {
            echo json_encode(['success' => false, 'message' => 'Failed to save item.']);
        }
        $stmt->close();
    }
    
    $check_stmt->close();

} else {
    echo json_encode(['success' => false, 'message' => 'Invalid request method. Please use POST.']);
}

$conn->close();
?>