<?php
$conn = new mysqli(
    "localhost",     // host
    "root",          // username
    "",              // password
    "tonespace_db"   // database name
);

if ($conn->connect_error) {
    die(json_encode([
        "status" => "error",
        "message" => "Database connection failed"
    ]));
}
?>
