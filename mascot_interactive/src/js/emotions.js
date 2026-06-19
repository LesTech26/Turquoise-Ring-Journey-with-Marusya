/**
 * Конфигурация эмоций Маруси
 * Каждая эмоция имеет: иконку, CSS-класс, цвет статуса и анимацию
 */
const MARUSYA_EMOTIONS = {
    happy: {
        label: 'Счастливая',
        icon: 'happy.svg',
        cssClass: 'happy',
        statusColor: '#4CAF50',
        animation: 'marusyaBounce',
        defaultPhrase: 'Я счастлива!'
    },
    sad: {
        label: 'Грустная',
        icon: 'sad.svg',
        cssClass: 'sad',
        statusColor: '#f44336',
        animation: 'marusyaWobble',
        defaultPhrase: 'Мне немного грустно...'
    },
    thinking: {
        label: 'Думает',
        icon: 'thinking.svg',
        cssClass: 'thinking',
        statusColor: '#FF9800',
        animation: 'marusyaThink',
        defaultPhrase: 'Дай подумать...'
    },
    surprised: {
        label: 'Удивлена',
        icon: 'surprised.svg',
        cssClass: 'surprised',
        statusColor: '#2196F3',
        animation: 'marusyaSurprise',
        defaultPhrase: 'Ого! Вот это да!'
    },
    celebrate: {
        label: 'Празднует',
        icon: 'celebrate.svg',
        cssClass: 'celebrate',
        statusColor: '#FFD700',
        animation: 'marusyaCelebrate',
        defaultPhrase: 'Ура! Празднуем!'
    }
};

// Список всех ключей эмоций для быстрого доступа
const EMOTION_KEYS = Object.keys(MARUSYA_EMOTIONS);

/**
 * Получить случайную эмоцию
 * @returns {string} Ключ эмоции
 */
function getRandomEmotion() {
    return EMOTION_KEYS[Math.floor(Math.random() * EMOTION_KEYS.length)];
}

/**
 * Проверить, существует ли эмоция
 * @param {string} emotion - Ключ эмоции
 * @returns {boolean}
 */
function isValidEmotion(emotion) {
    return EMOTION_KEYS.includes(emotion);
}

// Экспорт для Node.js (тесты)
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { MARUSYA_EMOTIONS, EMOTION_KEYS, getRandomEmotion, isValidEmotion };
}