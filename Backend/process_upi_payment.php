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
    $user_id = $_POST['user_id'] ?? null;
    $amount = $_POST['amount'] ?? null;
    $upi_id = $_POST['upi_id'] ?? null;

    // Basic validation
    if (!$user_id || !$amount || !$upi_id) {
        echo json_encode(['success' => false, 'message' => 'User ID, amount, and UPI ID are required.']);
        exit();
    }

    if (!filter_var($amount, FILTER_VALIDATE_FLOAT) || $amount <= 0) {
        echo json_encode(['success' => false, 'message' => 'Invalid amount.']);
        exit();
    }

    // --- Payment Gateway Simulation ---
    // In a real app, you would call your payment gateway's API here.
    // For this example, we'll simulate a process that can either succeed or fail.
    
    $transaction_id = 'txn_' . uniqid();
    // Let's simulate a 50/50 chance of success for demonstration
    $isPaymentSuccessful = rand(0, 1) == 1; 

    $status = $isPaymentSuccessful ? 'successful' : 'failed';

    // --- Save Transaction to Database ---
    $stmt = $conn->prepare("INSERT INTO transactions (user_id, amount, upi_id, transaction_id, status) VALUES (?, ?, ?, ?, ?)");
    $stmt->bind_param("idsss", $user_id, $amount, $upi_id, $transaction_id, $status);

    if ($stmt->execute()) {
        if ($isPaymentSuccessful) {
            // If the payment was for a subscription, you might trigger the subscription update logic here as well.
            echo json_encode([
                'success' => true, 
                'message' => 'Payment successful!',
                'transaction_id' => $transaction_id
            ]);
        } else {
            echo json_encode([
                'success' => false, 
                'message' => 'Payment failed. Please try again.',
                'transaction_id' => $transaction_id
            ]);
        }
    } else {
        echo json_encode(['success' => false, 'message' => 'Failed to record transaction.']);
    }

    $stmt->close();
} else {
    echo json_encode(['success' => false, 'message' => 'Invalid request method. Please use POST.']);
}

$conn->close();
?>