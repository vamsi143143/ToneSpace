<?php
require "config.php";

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

// ✅ Correct include paths
require __DIR__ . '/phpmailer/src/Exception.php';
require __DIR__ . '/phpmailer/src/PHPMailer.php';
require __DIR__ . '/phpmailer/src/SMTP.php';

$data = json_decode(file_get_contents("php://input"), true);
$email = $data["email"] ?? "";

if (!$email) {
    echo json_encode(["status"=>"error","message"=>"Email required"]);
    exit;
}

$token = bin2hex(random_bytes(32));
$expiry = date("Y-m-d H:i:s", strtotime("+15 minutes"));

$stmt = $conn->prepare(
    "UPDATE users SET reset_token=?, reset_expiry=? WHERE email=?"
);
$stmt->bind_param("sss", $token, $expiry, $email);
$stmt->execute();

if ($stmt->affected_rows === 0) {
    echo json_encode(["status"=>"error","message"=>"Email not found"]);
    exit;
}

$resetLink = "http://10.30.249.43/tonespace/reset_password.php?token=$token";

$mail = new PHPMailer(true);

try {
    // SMTP CONFIG
    $mail->isSMTP();
    $mail->Host       = 'smtp.gmail.com';
    $mail->SMTPAuth   = true;
    $mail->Username   = 'YOUR_GMAIL@gmail.com';        // 🔴 CHANGE
    $mail->Password   = 'YOUR_16_DIGIT_APP_PASSWORD';  // 🔴 CHANGE
    $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
    $mail->Port       = 587;

    // MAIL CONTENT
    $mail->setFrom($mail->Username, 'ToneSpace');
    $mail->addAddress($email);

    $mail->isHTML(true);
    $mail->Subject = 'Reset your ToneSpace password';
    $mail->Body = "
        <h2>Password Reset</h2>
        <p>Click below to reset your password:</p>
        <a href='$resetLink'
           style='padding:10px 20px;
                  background:#6A5AE0;
                  color:white;
                  text-decoration:none;
                  border-radius:5px'>
           Reset Password
        </a>
        <p>This link expires in 15 minutes.</p>
    ";

    $mail->send();

    echo json_encode([
        "status" => "success",
        "message" => "Reset link sent"
    ]);

} catch (Exception $e) {
    echo json_encode([
        "status" => "error",
        "message" => "Mail error: " . $mail->ErrorInfo
    ]);
}
