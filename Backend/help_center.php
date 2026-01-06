<?php
include "db.php";

$data = json_decode(file_get_contents("php://input"), true);

$user_id = $data["user_id"];
$question = strtolower($data["question"]);

$autoReply = "Thanks for contacting ToneSpace. Our team will get back to you shortly.";

// Find matching FAQ
$result = $conn->query("SELECT keyword, reply FROM help_faq");

while ($row = $result->fetch_assoc()) {
    if (strpos($question, $row["keyword"]) !== false) {
        $autoReply = $row["reply"];
        break;
    }
}

// Save ticket
$stmt = $conn->prepare(
    "INSERT INTO help_tickets (user_id, question, auto_reply)
     VALUES (?,?,?)"
);
$stmt->bind_param("iss", $user_id, $question, $autoReply);
$stmt->execute();

// Send response
echo json_encode([
    "status" => "success",
    "reply" => $autoReply
]);
