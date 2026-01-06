<?php
// This single file handles both displaying the form and processing the password update.

$message = '';
$message_type = '';
$show_form = true;
$token = $_GET['token'] ?? $_POST['token'] ?? '';

// --- Database Connection ---
$conn = new mysqli("localhost", "root", "", "tonespace_db");

if ($conn->connect_error) {
    $message = "Database connection failed: " . $conn->connect_error;
    $message_type = 'error';
    $show_form = false;
}

// Check if the form was submitted (POST request)
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $new_password = $_POST['new_password'] ?? '';

    if (empty($token) || empty($new_password)) {
        $message = "Invalid request. Please enter a new password.";
        $message_type = 'error';
    } else if (strlen($new_password) < 8) {
        $message = "Password must be at least 8 characters long.";
        $message_type = 'error';
    } else if ($conn) {
        // Find the user associated with a valid, non-expired token
        $stmt = $conn->prepare("SELECT email FROM password_resets WHERE token = ? AND expires_at > NOW()");
        $stmt->bind_param("s", $token);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $row = $result->fetch_assoc();
            $email = $row['email'];
            
            // Hash the new password and update the user's record
            $new_hashed_password = password_hash($new_password, PASSWORD_BCRYPT);
            $update_stmt = $conn->prepare("UPDATE users SET password = ? WHERE email = ?");
            $update_stmt->bind_param("ss", $new_hashed_password, $email);
            $update_stmt->execute();
            $update_stmt->close();
            
            // Delete the used token from the database
            $delete_stmt = $conn->prepare("DELETE FROM password_resets WHERE token = ?");
            $delete_stmt->bind_param("s", $token);
            $delete_stmt->execute();
            $delete_stmt->close();
            
            $message = "Success! Your password has been updated. You can now close this window and log in with your new password in the app.";
            $message_type = 'success';
            $show_form = false; // Hide the form after success
        } else {
            $message = "This link is invalid or has expired. Please request a new password reset link from the app.";
            $message_type = 'error';
            $show_form = false; // Hide the form on token failure
        }
        $stmt->close();
    }
} else {
    // If it's a GET request, just check if the token exists in the URL
    if (empty($token)) {
        $message = "Invalid link. No token was provided.";
        $message_type = 'error';
        $show_form = false;
    }
}

if ($conn) {
    $conn->close();
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Your Password</title>
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; background-color: #F5F5F5; }
        .container { max-width: 400px; width: 90%; border: 1px solid #E0E0E0; padding: 2rem; border-radius: 12px; background-color: white; box-shadow: 0 4px 12px rgba(0,0,0,0.08); text-align: center; }
        h2 { margin-top: 0; color: #333; }
        p { color: #666; }
        input[type="password"] { width: 100%; box-sizing: border-box; padding: 12px; margin-bottom: 1rem; border-radius: 8px; border: 1px solid #ccc; font-size: 1rem; }
        button { width: 100%; padding: 14px; background-color: #5C3A21; color: white; border: none; cursor: pointer; border-radius: 8px; font-size: 1rem; font-weight: bold; }
        button:hover { background-color: #4a2e1a; }
        .message { padding: 1rem; text-align: center; border-radius: 8px; margin-top: 1rem; font-weight: 500; }
        .error { background-color: #FFEBEE; color: #C62828; }
        .success { background-color: #E8F5E9; color: #2E7D32; }
    </style>
</head>
<body>

<div class="container">
    <h2>Reset Your Password</h2>

    <?php if (!empty($message)): ?>
        <p class="message <?php echo $message_type; ?>"><?php echo htmlspecialchars($message); ?></p>
    <?php endif; ?>

    <?php if ($show_form): ?>
        <p>Enter your new password below.</p>
        <form method="POST" action="reset_page.php">
            <input type="hidden" name="token" value="<?php echo htmlspecialchars($token); ?>">
            <input type="password" id="new_password" name="new_password" placeholder="New Password" required>
            <button type="submit">Update Password</button>
        </form>
    <?php endif; ?>

</div>

</body>
</html>