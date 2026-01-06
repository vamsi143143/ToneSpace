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
    if (!isset($_POST['design_id'])) {
        echo json_encode(['success' => false, 'message' => 'Design ID is required.']);
        exit();
    }
    $design_id = (int)$_POST['design_id'];

    // --- Check if analysis already exists ---
    $check_stmt = $conn->prepare("SELECT results_json FROM ai_analyses WHERE design_id = ?");
    $check_stmt->bind_param("i", $design_id);
    if ($check_stmt->execute()) {
        $result = $check_stmt->get_result();
        if ($result->num_rows > 0) {
            $existing_analysis = $result->fetch_assoc();
            echo json_encode(['success' => true, 'analysis' => json_decode($existing_analysis['results_json'])]);
            $check_stmt->close();
            $conn->close();
            exit();
        }
    }
    $check_stmt->close();


    // --- AI Analysis Simulation ---
    // In a real app, you'd send the image to a service like Google Vision AI or a custom model.
    // For now, we simulate a delay and generate mock data.
    sleep(3); // Simulate processing time

    // Mock data generation
    $mock_colors = ['#B2B2B2', '#E2E2E2', '#F2F2F2', '#D2D2D2', '#C2C2C2'];
    $mock_furniture = [
        ['name' => 'Modern Sofa', 'style' => 'Minimalist', 'url' => 'http://example.com/sofa'],
        ['name' => 'Oak Coffee Table', 'style' => 'Scandinavian', 'url' => 'http://example.com/table'],
        ['name' => 'Abstract Wall Art', 'style' => 'Contemporary', 'url' => 'http://example.com/art']
    ];
    $mock_layout_tips = [
        "Create a focal point around the main window.",
        "Use a large area rug to define the seating area.",
        "Incorporate plants to add a touch of nature."
    ];

    $analysis_results = [
        'color_palette' => $mock_colors,
        'furniture_recommendations' => $mock_furniture,
        'layout_suggestions' => $mock_layout_tips
    ];

    $results_json = json_encode($analysis_results);


    // --- Save Analysis to Database ---
    $stmt = $conn->prepare("INSERT INTO ai_analyses (design_id, results_json) VALUES (?, ?)");
    $stmt->bind_param("is", $design_id, $results_json);

    if ($stmt->execute()) {
        echo json_encode(['success' => true, 'analysis' => $analysis_results]);
    } else {
        echo json_encode(['success' => false, 'message' => 'Failed to save analysis results.']);
    }

    $stmt->close();
} else {
    echo json_encode(['success' => false, 'message' => 'Invalid request method. Please use POST.']);
}

$conn->close();
?>