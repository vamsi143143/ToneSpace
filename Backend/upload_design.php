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
    // User ID should be sent from your app's session
    if (!isset($_POST['user_id'])) {
        echo json_encode(['success' => false, 'message' => 'User ID is required.']);
        exit();
    }
    
    // Check if a file was uploaded
    if (!isset($_FILES['design_image']) || $_FILES['design_image']['error'] != 0) {
        echo json_encode(['success' => false, 'message' => 'An image file is required. Error code: ' . $_FILES['design_image']['error']]);
        exit();
    }

    $user_id = (int)$_POST['user_id'];
    $design_title = $_POST['title'] ?? 'Untitled Design'; // Optional title

    // --- File Upload Logic ---
    $target_dir = "uploads/designs/";
    // Create directory if it doesn't exist
    if (!file_exists($target_dir)) {
        mkdir($target_dir, 0777, true);
    }

    $file_name = $user_id . '_' . uniqid() . '-' . basename($_FILES["design_image"]["name"]);
    $target_file = $target_dir . $file_name;
    $uploadOk = 1;
    $imageFileType = strtolower(pathinfo($target_file, PATHINFO_EXTENSION));

    // Check if image file is a actual image or fake image
    $check = getimagesize($_FILES["design_image"]["tmp_name"]);
    if($check === false) {
        echo json_encode(['success' => false, 'message' => 'File is not an image.']);
        exit();
    }

    // Allow certain file formats
    if($imageFileType != "jpg" && $imageFileType != "png" && $imageFileType != "jpeg") {
        echo json_encode(['success' => false, 'message' => 'Sorry, only JPG, JPEG, & PNG files are allowed.']);
        exit();
    }

    // Attempt to move the uploaded file
    if (move_uploaded_file($_FILES["design_image"]["tmp_name"], $target_file)) {
        // The URL to be stored in the database
        $design_image_url = "http://" . $_SERVER['HTTP_HOST'] . "/tonespace/" . $target_file;

        // --- Database Insert Logic ---
        $stmt = $conn->prepare("INSERT INTO designs (user_id, title, image_url) VALUES (?, ?, ?)");
        $stmt->bind_param("iss", $user_id, $design_title, $design_image_url);

        if ($stmt->execute()) {
            echo json_encode([
                'success' => true, 
                'message' => 'Design uploaded successfully!', 
                'design_id' => $stmt->insert_id, // Return the new design's ID
                'image_url' => $design_image_url
            ]);
        } else {
            echo json_encode(['success' => false, 'message' => 'Failed to save design information to the database.']);
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