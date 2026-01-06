<?php
include "db.php";

$data = json_decode(file_get_contents("php://input"), true);

$user_id = $data["user_id"];
$enabled = $data["enabled"];

$stmt = $conn->prepare(
    "REPLACE INTO user_notifications (user_id, enabled) VALUES (?,?)"
);
$stmt->bind_param("ii", $user_id, $enabled);
$stmt->execute();

echo json_encode(["status"=>"success"]);
