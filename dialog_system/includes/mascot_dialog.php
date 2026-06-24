<?php
/**
 * Диалоговая система с Марусей
 * Использование: require_once 'includes/mascot_dialog.php'; renderMascotDialog();
 */

function getDialogData() {
    return [
        'greeting' => [
            'id' => 'greeting',
            'text' => 'Привет! Я Маруся — твой гид по бирюзовому кольцу России! Хочешь узнать что-то новое?',
            'options' => [
                ['text' => 'Расскажи о районах!', 'next' => 'districts'],
                ['text' => 'Покажи костюмы!', 'next' => 'costumes'],
                ['text' => 'Что такое Бирюзовое кольцо?', 'next' => 'about_ring'],
            ]
        ],
        'districts' => [
            'id' => 'districts',
            'text' => 'В Орловской области 24 удивительных района! Каждый со своей историей и традициями. Какой тебя интересует?',
            'options' => [
                ['text' => 'Где начать?', 'next' => 'start_place'],
                ['text' => 'Самый красивый район?', 'next' => 'beautiful'],
                ['text' => 'Расскажи про все!', 'next' => 'all_districts'],
                ['text' => 'Вернуться назад', 'next' => 'greeting'],
            ]
        ],
        'costumes' => [
            'id' => 'costumes',
            'text' => 'Народные костюмы — это наша гордость! В каждом районе свои узоры и цвета. Хочешь примерить?',
            'options' => [
                ['text' => 'Да! Хочу переодеться!', 'next' => 'dress_up'],
                ['text' => 'Расскажи про орнаменты', 'next' => 'ornaments'],
                ['text' => 'Вернуться назад', 'next' => 'greeting'],
            ]
        ],
        'about_ring' => [
            'id' => 'about_ring',
            'text' => 'Бирюзовое кольцо — это туристический маршрут по Орловской области. Он объединяет 24 района с уникальной культурой и природой!',
            'options' => [
                ['text' => 'Расскажи о районах!', 'next' => 'districts'],
                ['text' => 'Покажи костюмы!', 'next' => 'costumes'],
                ['text' => 'Спасибо!', 'next' => 'goodbye'],
            ]
        ],
        'start_place' => [
            'id' => 'start_place',
            'text' => 'Начни с Болховского района! Там очень красивая природа и старинные храмы. А ещё там делают самые вкусные пряники! 🍪',
            'options' => [
                ['text' => 'Расскажи ещё!', 'next' => 'districts'],
                ['text' => 'Покажи костюмы!', 'next' => 'costumes'],
                ['text' => 'Вернуться назад', 'next' => 'greeting'],
            ]
        ],
        'beautiful' => [
            'id' => 'beautiful',
            'text' => 'Орловский район славится своими усадьбами, а Ливенский — кружевными промыслами. Но все районы красивы по-своему!',
            'options' => [
                ['text' => 'Расскажи ещё!', 'next' => 'districts'],
                ['text' => 'Покажи костюмы!', 'next' => 'costumes'],
                ['text' => 'Вернуться назад', 'next' => 'greeting'],
            ]
        ],
        'all_districts' => [
            'id' => 'all_districts',
            'text' => 'Все 24 района ждут тебя на карте! Переходи на главную страницу и кликай на любой — узнаешь много интересного! 🗺️',
            'options' => [
                ['text' => 'Расскажи ещё!', 'next' => 'districts'],
                ['text' => 'Покажи костюмы!', 'next' => 'costumes'],
                ['text' => 'Вернуться назад', 'next' => 'greeting'],
            ]
        ],
        'dress_up' => [
            'id' => 'dress_up',
            'text' => 'Отлично! Переходи в раздел «Собери костюм» — там ты сможешь создать свой уникальный образ! 👗',
            'options' => [
                ['text' => 'Расскажи про орнаменты', 'next' => 'ornaments'],
                ['text' => 'Вернуться назад', 'next' => 'costumes'],
            ]
        ],
        'ornaments' => [
            'id' => 'ornaments',
            'text' => 'Орнаменты на костюмах — это не просто узоры. Каждый символ что-то значит: солнце, земля, вода, урожай. Это язык наших предков!',
            'options' => [
                ['text' => 'Хочу примерить костюм!', 'next' => 'dress_up'],
                ['text' => 'Вернуться назад', 'next' => 'costumes'],
            ]
        ],
        'goodbye' => [
            'id' => 'goodbye',
            'text' => 'Всегда рада помочь! Заходи ещё — я покажу тебе что-то новое! 😊',
            'options' => [
                ['text' => 'Пока!', 'next' => null],
            ]
        ],
    ];
}

function renderMascotDialog() {
    $dialogs = getDialogData();
    ?>
    <div id="mascot-dialog" class="mascot-dialog-container">
        <!-- Кнопка-триггер для открытия диалога -->
        <button id="mascot-toggle" class="mascot-toggle-btn" aria-label="Открыть диалог с Марусей">
            <span class="mascot-toggle-avatar">🌸</span>
            <span class="mascot-toggle-label">Маруся</span>
            <span class="mascot-toggle-badge">💬</span>
        </button>

        <!-- Окно диалога -->
        <div id="mascot-window" class="mascot-window" style="display: none;">
            <div class="mascot-header">
                <div class="mascot-avatar-wrapper">
                    <div class="mascot-avatar" id="mascot-avatar">🌸</div>
                    <span class="mascot-status online"></span>
                </div>
                <div class="mascot-header-info">
                    <span class="mascot-name">Маруся</span>
                    <span class="mascot-subtitle">Твой гид по Бирюзовому кольцу</span>
                </div>
                <button class="mascot-close" id="mascot-close">✕</button>
            </div>

            <div class="mascot-messages" id="mascot-messages">
                <!-- Сообщения будут добавляться сюда -->
            </div>

            <div class="mascot-options" id="mascot-options">
                <!-- Кнопки вариантов ответов -->
            </div>

            <div class="mascot-footer">
                <span class="mascot-typing" id="mascot-typing" style="display: none;">Маруся печатает...</span>
            </div>
        </div>
    </div>

    <script>
        // Данные диалогов передаются в JS
        const dialogData = <?php echo json_encode($dialogs); ?>;
    </script>
    <?php
}
?>