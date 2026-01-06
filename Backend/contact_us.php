<?php
// Import PHPMailer classes
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

// Ensure the path is correct based on your file structure
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
        send_json_response(['success' => false, 'message' => 'A valid email address is required.']);
    }

    $stmt_check = $conn->prepare("SELECT id FROM users WHERE email = ?");
    $stmt_check->bind_param("s", $email);
    $stmt_check->execute();
    $result = $stmt_check->get_result();

    if ($result->num_rows === 0) {
        send_json_response(['success' => true, 'message' => 'If a matching account was found, an email was sent.']);
    }
    $stmt_check->close();

    $token = bin2hex(random_bytes(50));
    $expires = new DateTime('NOW');
    $expires->add(new DateInterval('PT1H'));
    $expires_at = $expires->format('Y-m-d H:i:s');

    $stmt_insert = $conn->prepare("INSERT INTO password_resets (email, token, expires_at) VALUES (?, ?, ?)");
    $stmt_insert->bind_param("sss", $email, $token, $expires_at);
    
    if ($stmt_insert->execute()) {
        
        $mail = new PHPMailer(true);

        try {
            // --- SERVER SETTINGS ---
            $mail->isSMTP();
            $mail->Host       = 'smtp.gmail.com';
            $mail->SMTPAuth   = true;
            
            // Your full Gmail address
            $mail->Username   = 'kundurivamsi8985@gmail.com'; 
            
            // The 16-character App Password you generated
            $mail->Password   = 'qnmxsdhjtcfzoqvd'; 
            
            $mail->SMTPSecure = PHPMailer::ENCRYPTION_SMTPS;
            $mail->Port       = 465;
            // -------------------------

            //Recipients
            $mail->setFrom('kundurivamsi8985@gmail.com', 'ToneSpace Support');
            $mail->addAddress($email);

            //Content
            $reset_link = "http://10.30.249.43/tonespace/reset_page.php?token=" . $token;
            $mail->isHTML(true);
            $mail->Subject = 'Password Reset Request for ToneSpace';
            $mail->Body    = "Hello,<br><br>Click the following link to reset your password. This link is valid for 1 hour.<br><br><a href='$reset_link'>Reset Password</a>";
            $mail->AltBody = "Hello,\n\nCopy and paste this link into your browser to reset your password: " . $reset_link;

            $mail->send();
            send_json_response(['success' => true, 'message' => 'If a matching account was found, an email was sent.']);

        } catch (Exception $e) {
            send_json_response(['success' => false, 'message' => "Server Error: Could not send email. Mailer Error: {$mail->ErrorInfo}"]);
        }

    } else {
        send_json_response(['success' => false, 'message' => 'Failed to generate reset link.']);
    }
    
    $stmt_insert->close();

} else {
    send_json_response(['success' => false, 'message' => 'Invalid request method.']);
}

$conn->close();
ob_end_flush();
?>