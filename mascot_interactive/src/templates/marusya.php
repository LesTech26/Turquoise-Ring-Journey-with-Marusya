<!-- Маскот Маруся -->
<div id="marusya-container">
    <div id="marusya" class="marusya happy" role="button" aria-label="Маруся - ваш гид">
        <div class="marusya-avatar-wrapper">
            <img 
                src="/assets/marusya/assets/img/marusya/happy.svg" 
                alt="Маруся" 
                id="marusya-avatar"
                class="marusya-avatar"
            >
            <div class="marusya-status" id="marusya-status"></div>
        </div>
        
        <div id="marusya-speech" class="speech-bubble hidden">
            <div class="speech-content">
                <span id="marusya-text" class="speech-text"></span>
                <div class="speech-actions">
                    <button id="marusya-speak-btn" class="speak-btn" title="Озвучить">🔊</button>
                    <button id="marusya-close-btn" class="close-speech-btn" title="Закрыть">✕</button>
                </div>
            </div>
            <div class="speech-tail"></div>
        </div>
        
        <div class="marusya-actions">
            <button class="marusya-action-btn" id="marusya-hide" title="Спрятать Марусю">🙈</button>
        </div>
    </div>
    
    <div id="marusya-menu" class="marusya-menu hidden">
        <ul>
            <li><button data-action="help">❓ Помощь</button></li>
            <li><button data-action="reset">🔄 Сбросить прогресс</button></li>
            <li><button data-action="settings">⚙️ Настройки</button></li>
        </ul>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/canvas-confetti@1"></script>
<script src="/assets/marusya/src/js/dialogues.js"></script>
<script src="/assets/marusya/src/js/emotions.js"></script>
<script src="/assets/marusya/src/js/speech.js"></script>
<script src="/assets/marusya/src/js/tooltips.js"></script>
<script src="/assets/marusya/src/js/celebrations.js"></script>
<script src="/assets/marusya/src/js/Marusya.js"></script>
<script src="/assets/marusya/src/js/main.js"></script>