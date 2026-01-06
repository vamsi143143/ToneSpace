<?php
header('Content-Type: application/json');

// --- Database Connection ---
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "tonespace_db";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die(json_encode(['success' => false, 'message' => 'Database connection failed: ' . $conn->connect_error]));
}

// --- Handle POST Request ---
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    
    $email = $_POST['email'] ?? '';
    $password = $_POST['password'] ?? '';

    // --- Basic Validation ---
    if (empty($email) || empty($password)) {
        echo json_encode(['success' => false, 'message' => 'Email and password fields are required.']);
        exit();
    }

    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        echo json_encode(['success' => false, 'message' => 'The provided email is not valid.']);
        exit();
    }

    // --- ADDED: Server-Side Password Validation ---
    $passwordPattern = '/^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$/';
    if (!preg_match($passwordPattern, $password)) {
        echo json_encode(['success' => false, 'message' => 'Password must be 8+ chars and include an uppercase, lowercase, and number.']);
        exit();
    }
    
    // --- Check for Existing User ---
    $stmt = $conn->prepare("SELECT id FROM users WHERE email = ?");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        echo json_encode(['success' => false, 'message' => 'An account with this email already exists.']);
        $stmt->close();
        $conn->close();
        exit();
    }
    $stmt->close();

    // --- Create New User ---
    $hashed_password = password_hash($password, PASSWORD_BCRYPT);
    
    $stmt = $conn->prepare("INSERT INTO users (email, password) VALUES (?, ?)");
    $stmt->bind_param("ss", $email, $hashed_password);

    if ($stmt->execute()) {
        echo json_encode(['success' => true, 'message' => 'Registration successful! You can now log in.']);
    } else {
        echo json_encode(['success' => false, 'message' => 'An error occurred during registration. Please try again.']);
    }

    $stmt->close();

} else {
    echo json_encode(['success' => false, 'message' => 'Invalid request method. Please use POST.']);
}

$conn->close();
?>