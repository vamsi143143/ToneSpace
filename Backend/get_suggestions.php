<?php
include "db.php";

$user_id = $_GET['user_id'];

$query = mysqli_query($conn, 
"SELECT * FROM ai_suggestions WHERE user_id='$user_id' ORDER BY created_at DESC");

$result = [];
while ($row = mysqli_fetch_assoc($query)) {
    $result[] = $row;
}

echo json_encode($result);
?>
