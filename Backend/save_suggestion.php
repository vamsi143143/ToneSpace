<?php
include "db.php";

$data = json_decode(file_get_contents("php://input"), true);

$user_id = $data['user_id'];
$image = $data['image_path'];
$mood = $data['mood_theme'];
$colors = $data['color_palette'];
$layout = $data['layout_suggestion'];

$query = "INSERT INTO ai_suggestions 
(user_id,image_path,mood_theme,color_palette,layout_suggestion)
VALUES ('$user_id','$image','$mood','$colors','$layout')";

if (mysqli_query($conn, $query)) {
    echo json_encode(["status" => "success"]);
} else {
    echo json_encode(["status" => "error"]);
}
?>
