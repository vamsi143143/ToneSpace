<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");

$conn = new mysqli("localhost", "root", "", "tonespace");

if ($conn->connect_error) {
    echo json_encode(["status"=>"error","message"=>"DB connection failed"]);
    exit;
}

$data = json_decode(file_get_contents("php://input"), true);

$user_id   = $data['user_id'] ?? null;
$full_name = $data['full_name'] ?? '';
$phone     = $data['phone'] ?? '';
$gender    = $data['gender'] ?? '';
$dob       = $data['dob'] ?? '';

if (!$user_id) {
    echo json_encode(["status"=>"error","message"=>"User ID required"]);
    exit;
}

$sql = "UPDATE user_profile 
        SET full_name=?, phone=?, gender=?, dob=? 
        WHERE user_id=?";

$stmt = $conn->prepare($sql);
$stmt->bind_param("ssssi", $full_name, $phone, $gender, $dob, $user_id);

if ($stmt->execute()) {
    echo json_encode([
        "status" => "success",
        "message" => "Personal info saved"
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "Save failed"
    ]);
}

$stmt->close();
$conn->close();
