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

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (!isset($_POST['user_id'])) {
        echo json_encode(['success' => false, 'message' => 'User ID is required.']);
        exit();
    }
    
    if (!isset($_FILES['photo']) || $_FILES['photo']['error'] != 0) {
        echo json_encode(['success' => false, 'message' => 'An image file is required.']);
        exit();
    }

    $user_id = (int)$_POST['user_id'];
    $caption = $_POST['caption'] ?? 'Untitled Photo'; // Optional caption

    // --- File Upload Logic ---
    $target_dir = "uploads/photos/";
    if (!file_exists($target_dir)) {
        mkdir($target_dir, 0777, true);
    }

    $file_name = $user_id . '_' . uniqid() . '-' . basename($_FILES["photo"]["name"]);
    $target_file = $target_dir . $file_name;
    $imageFileType = strtolower(pathinfo($target_file, PATHINFO_EXTENSION));

    // Basic validation
    $check = getimagesize($_FILES["photo"]["tmp_name"]);
    if($check === false) {
        echo json_encode(['success' => false, 'message' => 'File is not an image.']);
        exit();
    }

    if($imageFileType != "jpg" && $imageFileType != "png" && $imageFileType != "jpeg") {
        echo json_encode(['success' => false, 'message' => 'Sorry, only JPG, JPEG, & PNG files are allowed.']);
        exit();
    }

    // Move the uploaded file
    if (move_uploaded_file($_FILES["photo"]["tmp_name"], $target_file)) {
        $photo_url = "http://" . $_SERVER['HTTP_HOST'] . "/tonespace/" . $target_file;

        // --- Database Insert Logic ---
        $stmt = $conn->prepare("INSERT INTO photos (user_id, caption, image_url) VALUES (?, ?, ?)");
        $stmt->bind_param("iss", $user_id, $caption, $photo_url);

        if ($stmt->execute()) {
            echo json_encode([
                'success' => true, 
                'message' => 'Photo uploaded successfully!', 
                'photo_id' => $stmt->insert_id,
                'image_url' => $photo_url
            ]);
        } else {
            echo json_encode(['success' => false, 'message' => 'Failed to save photo information to the database.']);
        }
        $stmt->close();

    } else {
        echo json_encode(['success' => false, 'message' => 'Sorry, there was an error uploading your file.']);
    }

} else {
    echo json_encode(['success' => false, 'message' => 'Invalid request method. Please use POST.']);
}

$conn->close();
?>