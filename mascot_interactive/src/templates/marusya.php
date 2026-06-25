<?php
// Template for the Marusya mascot module. It can be included from turquoise pages
// without changing the first developer's files.

$config = require __DIR__ . '/../../config.php';

$base = $config['base_path'];
$asset = $config['asset_path'];
$video = $config['video_path'];

$scriptName = str_replace('\\', '/', $_SERVER['SCRIPT_NAME'] ?? '');
$projectBase = '';
foreach (['/turquoise/', '/mascot_interactive/'] as $marker) {
    $markerPos = strpos($scriptName, $marker);
    if ($markerPos !== false) {
        $projectBase = substr($scriptName, 0, $markerPos);
        break;
    }
}
$projectBase = rtrim($projectBase, '/');
$mascotBase = $projectBase . '/mascot_interactive';
$mascotBaseEsc = htmlspecialchars($mascotBase, ENT_QUOTES, 'UTF-8');
?>
<link rel="stylesheet" href="<?= $base ?>/src/css/marusya.css">

<div id="marusya-container">
    <div id="marusya" class="marusya happy" role="button" aria-label="Маруся - ваш гид">
        <div class="marusya-avatar-wrapper">
            <video
                id="marusya-video"
                class="marusya-avatar marusya-video"
                src="<?= $mascotBaseEsc ?>/assets/video/marusya/greeting.mp4"
                muted
                autoplay
                loop
                playsinline
                preload="metadata"
                aria-hidden="true"
            ></video>
            <img
                src="<?= $mascotBaseEsc ?>/assets/img/marusya/happy.svg"
                alt="Маруся"
                id="marusya-avatar"
                class="marusya-avatar marusya-avatar-fallback"
                onerror="this.src='data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22%3E%3Ccircle cx=%2250%22 cy=%2250%22 r=%2245%22 fill=%22%2317A2B8%22/%3E%3Ctext x=%2250%22 y=%2265%22 text-anchor=%22middle%22 font-size=%2240%22%3E%F0%9F%98%8A%3C/text%3E%3C/svg%3E'"
            >
            <div class="marusya-status" id="marusya-status"></div>
        </div>

        <div id="marusya-speech" class="speech-bubble hidden">
            <div class="speech-content">
                <span id="marusya-text" class="speech-text"></span>
                <div class="speech-actions">
                    <button id="marusya-speak-btn" class="speak-btn" title="Озвучить" aria-label="Озвучить">🔊</button>
                    <button id="marusya-close-btn" class="close-speech-btn" title="Закрыть" aria-label="Закрыть">×</button>
                </div>
            </div>
            <div class="speech-tail"></div>
        </div>

        <div class="marusya-actions">
            <button class="marusya-action-btn" id="marusya-hide" title="Спрятать Марусю" aria-label="Спрятать Марусю">🙈</button>
        </div>
    </div>

    <div id="marusya-menu" class="marusya-menu hidden">
        <ul>
            <li><button data-action="help">? Помощь</button></li>
            <li><button data-action="reset">↻ Сбросить прогресс</button></li>
            <li><button data-action="settings">⚙ Настройки</button></li>
        </ul>
    </div>
</div>

<script>
    window.MARUSYA_ASSET_PATH = '<?= $mascotBaseEsc ?>/assets/img/marusya/';
    window.MARUSYA_VIDEO_PATH = '<?= $mascotBaseEsc ?>/assets/video/marusya/';
</script>
<script src="<?= $base ?>/src/js/dialogues.js"></script>
<script src="<?= $base ?>/src/js/emotions.js"></script>
<script src="<?= $base ?>/src/js/speech.js"></script>
<script src="<?= $base ?>/src/js/tooltips.js"></script>
<script src="<?= $base ?>/src/js/celebrations.js"></script>
<script src="<?= $base ?>/src/js/Marusya.js"></script>
<script src="<?= $base ?>/src/js/main.js"></script>
