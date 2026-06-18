<?php
require_once __DIR__ . '/templates/header.php';
$slug = $_GET['slug'] ?? '';
?>
<main>
    <h1>Район: <?= htmlspecialchars($slug) ?></h1>
</main>
<?php require_once __DIR__ . '/templates/footer.php'; ?>