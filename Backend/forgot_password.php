<?php
// Import PHPMailer classes
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

// Ensure the path is correct
require 'vendor/PHPMailer/src/Exception.php';
require 'vendor/PHPMailer/src/PHPMailer.php';
require 'vendor/PHPMailer/src/SMTP.php';

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

// --- Database Connection ---
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "tonespace_db";
$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    send_json_response(['success' => false, 'message' => 'Database connection failed.']);
}

// --- Handle POST Request ---
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $json_data = file_get_contents('php://input');
    $data = json_decode($json_data, true);
    $email = $data['email'] ?? '';

    if (empty($email) || !filter_var($email, FILTER_VALIDATE_EMAIL)) {
        send_json_response(['success' => false, 'message' => 'A valid email is required.']);
    }

    $stmt_check = $conn->prepare("SELECT id FROM users WHERE email = ?");
    $stmt_check->bind_param("s", $email);
    $stmt_check->execute();
    if ($stmt_check->get_result()->num_rows === 0) {
        // Still send a success response to prevent user enumeration
        send_json_response(['success' => true, 'message' => 'If a matching account was found, an email has been sent.']);
    }
    $stmt_check->close();

    $token = bin2hex(random_bytes(50));
    $expires = new DateTime('NOW');
    $expires->add(new DateInterval('PT1H')); // Token expires in 1 hour
    $expires_at = $expires->format('Y-m-d H:i:s');

    // Store the token in the password_resets table
    $stmt_insert = $conn->prepare("INSERT INTO password_resets (email, token, expires_at) VALUES (?, ?, ?)");
    $stmt_insert->bind_param("sss", $email, $token, $expires_at);
    
    if ($stmt_insert->execute()) {
        $mail = new PHPMailer(true);
        try {
            // --- SERVER SETTINGS (Your Credentials) ---
            $mail->isSMTP();
            $mail->Host       = 'smtp.gmail.com';
            $mail->SMTPAuth   = true;
            $mail->Username   = 'kundurivamsi8985@gmail.com'; 
            $mail->Password   = 'qnmxsdhjtcfzoqvd'; 
            $mail->SMTPSecure = PHPMailer::ENCRYPTION_SMTPS;
            $mail->Port       = 465;
            // ------------------------------------------

            $mail->setFrom('kundurivamsi8985@gmail.com', 'ToneSpace Support');
            $mail->addAddress($email);

            //Content
            $reset_link = "http://10.30.249.43/tonespace/reset_page.php?token=" . $token;
            $mail->isHTML(true);
            $mail->Subject = 'Password Reset Request for ToneSpace';
            $mail->Body    = "Hello,<br><br>Click the following link to reset your password. This link is valid for 1 hour.<br><br><a href='$reset_link'>Reset Password</a>";
            $mail->AltBody = "Hello,\n\nCopy and paste this link into your browser to reset your password: " . $reset_link;

            $mail->send();
            send_json_response(['success' => true, 'message' => 'If a matching account was found, an email has been sent.']);
        } catch (Exception $e) {
            send_json_response(['success' => false, 'message' => "Server Error: Could not send email. Mailer Error: {$mail->ErrorInfo}"]);
        }
    } else {
        send_json_response(['success' => false, 'message' => 'Failed to generate reset link.']);
    }
    $stmt_insert->close();
}
$conn->close();
ob_end_flush();
?>