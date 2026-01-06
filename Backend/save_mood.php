<?php
include "db.php";

$data = json_decode(file_get_contents("php://input"), true);

$user_id = $data['user_id'];
$mood = $data['mood'];
$room = $data['room_type'];

$query = "INSERT INTO moods (user_id, mood, room_type) 
          VALUES ('$user_id','$mood','$room')";

if (mysqli_query($conn, $query)) {
    echo json_encode(["status" => "success"]);
} else {
    echo json_encode(["status" => "error"]);
}
?>
