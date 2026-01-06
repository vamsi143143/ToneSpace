<?php
header('Content-Type: application/json');

// Database connection
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "tonespace";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die(json_encode(['success' => false, 'message' => 'Database connection failed: ' . $conn->connect_error]));
}

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    if (!isset($_GET['user_id'])) {
        echo json_encode(['success' => false, 'message' => 'User ID is required.']);
        exit();
    }
    $user_id = (int)$_GET['user_id'];

    // This query retrieves saved designs. You can expand it with UNION ALL to include other item types.
    $stmt = $conn->prepare("
        SELECT 
            si.id as saved_item_id, 
            si.item_type, 
            si.created_at as saved_at,
            d.id as design_id,
            d.title as design_title,
            d.image_url as design_image_url
        FROM 
            saved_items si
        JOIN 
            designs d ON si.item_id = d.id
        WHERE 
            si.user_id = ? AND si.item_type = 'design'
        ORDER BY 
            si.created_at DESC
    ");
    
    $stmt->bind_param("i", $user_id);

    if ($stmt->execute()) {
        $result = $stmt->get_result();
        $saved_items = [];
        if ($result->num_rows > 0) {
            while($row = $result->fetch_assoc()) {
                $saved_items[] = $row;
            }
        }
        echo json_encode(['success' => true, 'saved_items' => $saved_items]);
    } else {
        echo json_encode(['success' => false, 'message' => 'Failed to retrieve saved items.']);
    }

    $stmt->close();
} else {
    echo json_encode(['success' => false, 'message' => 'Invalid request method. Please use GET.']);
}

$conn->close();
?>