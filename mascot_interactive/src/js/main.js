/**
 * main.js
 * Точка входа — инициализация Маруси
 * Адаптировано под проект turquoise
 */

document.addEventListener('DOMContentLoaded', function () {
    // Проверяем, есть ли контейнер Маруси
    const container = document.getElementById('marusya-container');
    if (!container) {
        console.warn('🐻 Контейнер Маруси не найден. Убедитесь, что подключили шаблон.');
        return;
    }

    // Путь к ассетам (можно переопределить через глобальную переменную)
    const assetPath = window.MARUSYA_ASSET_PATH || '/mascot_interactive/assets/img/marusya/';
    const videoPath = window.MARUSYA_VIDEO_PATH || '/mascot_interactive/assets/video/marusya/';
    const placeholder = document.getElementById('marusya-placeholder');

    if (placeholder && !placeholder.dataset.marusyaMounted) {
        placeholder.textContent = '';
        placeholder.appendChild(container);
        placeholder.dataset.marusyaMounted = 'true';
        container.classList.add('marusya-container--embedded');
    }

    // Создаём экземпляр
    const marusya = new Marusya({
        mood: 'happy',
        autoSpeak: true,
        speechDelay: 4000,
        assetPath: assetPath,
        videoPath: videoPath
    });

    // Сохраняем в глобальный доступ
    window.marusya = marusya;

    // API для других модулей
    window.MarusyaAPI = {
        /** Показать сообщение */
        say: (text, duration) => marusya.say(text, duration),

        /** Сменить настроение */
        setMood: (mood) => marusya.setMood(mood),

        /** Запустить жест/мимику */
        gesture: (gesture, duration) => marusya.addGesture(gesture, duration),

        /** Запустить празднование */
        celebrate: (type) => marusya.celebrations.celebrate(type),

        /** Показать подсказку */
        showTooltip: (selector, tipKey) => marusya.showTipForElement(selector, tipKey),

        /** Реакция на посещение района */
        visitDistrict: (name) => marusya.visitDistrict(name),

        /** Реакция на ответ в квизе */
        quizAnswer: (correct) => marusya.onQuizAnswer({ correct }),

        /** Реакция на проигрыш в игре */
        gameLose: () => marusya.onGameLose(),

        /** Реакция на собранный костюм */
        costumeComplete: () => marusya.onCostumeComplete(),

        /** Скрыть Марусю */
        hide: () => marusya.toggleVisibility(),

        /** Показать Марусю */
        show: () => {
            if (!marusya.isVisible) {
                marusya.toggleVisibility();
            }
        },

        /** Получить текущее состояние */
        getState: () => ({
            mood: marusya.currentMood,
            visible: marusya.isVisible,
            soundEnabled: marusya.soundEnabled,
            currentDistrict: marusya.currentDistrict
        })
    };

    console.log('🐻 Маруся загружена! Используйте window.MarusyaAPI для взаимодействия');
});
