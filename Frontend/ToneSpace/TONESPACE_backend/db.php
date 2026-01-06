<?php 
$host = 'localhost'; 
$dbname = 'tonedb'; 
$username = 'root'; 
$password = ''; 
 
try { 
    $pdo = new PDO("mysql:host=$host;dbname=$dbname", $username, $password); 
    $pdo-, PDO::ERRMODE_EXCEPTION); 
} catch(PDOException $e) { 
    echo "Connection failed: " . $e-
    die(); 
} 
?> 
