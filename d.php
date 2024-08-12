<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

// Предопределенные лицензии
$validLicenses = [
    "Your_Valid_Key_1",
    "YOUR_VALID_LICENSE_KEY_2",
];

$invalidLicenses = [
    "YOUR_INVALID_LICENSE_KEY_1",
    "YOUR_INVALID_LICENSE_KEY_2",
];

// Получаем лицензионный ключ из запроса
$license_key = isset($_GET['key']) ? $_GET['key'] : '';

// Проверяем, передан ли лицензионный ключ
if (empty($license_key)) {
    echo json_encode(["status" => "error", "message" => "No license key provided."]);
    exit;
}

// Проверяем, действителен ли ключ
if (in_array($license_key, $validLicenses)) {
    echo json_encode(["status" => "success", "message" => "valid"]);
} elseif (in_array($license_key, $invalidLicenses)) {
    echo json_encode(["status" => "error", "message" => "invalid"]);
} else {
    echo json_encode(["status" => "error", "message" => "license not found"]);
}
?>