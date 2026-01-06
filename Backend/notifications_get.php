<?php
include "db.php";

$user_id = $_GET["user_id"];

$stmt = $conn->prepare(
    "SELECT enabled FROM user_notifications WHERE user_id=?"
);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result()->fetch_assoc();

echo json_encode([
    "status"=>"success",
    "enabled"=>$result["enabled"] ?? 1
]);
