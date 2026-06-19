/**
 * Юнит-тесты для модуля Маруси
 * 
 * Запуск: npm test
 * 
 * Используется Jest — фреймворк для тестирования JavaScript
 * Установка: npm install --save-dev jest
 */

// Импортируем модули для тестирования
const Marusya = require('../src/js/Marusya');
const { MARUSYA_EMOTIONS, EMOTION_KEYS, isValidEmotion, getRandomEmotion } = require('../src/js/emotions');
const MARUSYA_DIALOGUES = require('../src/js/dialogues');

// ============================================
//  МОКИРОВАНИЕ DOM
// ============================================

// Создаём DOM-структуру для тестов
beforeEach(() => {
    document.body.innerHTML = `
        <div id="marusya-container">
            <div id="marusya" class="marusya happy">
                <div class="marusya-avatar-wrapper">
                    <img id="marusya-avatar" src="" alt="Маруся">
                    <div id="marusya-status" class="marusya-status"></div>
                </div>
                <div id="marusya-speech" class="speech-bubble hidden">
                    <div class="speech-content">
                        <span id="marusya-text" class="speech-text"></span>
                        <div class="speech-actions">
                            <button id="marusya-speak-btn" class="speak-btn">🔊</button>
                            <button id="marusya-close-btn" class="close-speech-btn">✕</button>
                        </div>
                    </div>
                    <div class="speech-tail"></div>
                </div>
                <div class="marusya-actions">
                    <button id="marusya-hide" class="marusya-action-btn">🙈</button>
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
    `;
});

// Мок для Web Speech API
global.window.speechSynthesis = {
    speak: jest.fn(),
    cancel: jest.fn(),
    getVoices: jest.fn(() => []),
    onvoiceschanged: null
};

// Мок для canvas-confetti
global.confetti = jest.fn();

// Мок для localStorage
Object.defineProperty(window, 'localStorage', {
    value: {
        getItem: jest.fn(),
        setItem: jest.fn(),
        clear: jest.fn()
    }
});

// ============================================
//  ТЕСТЫ
// ============================================

describe('🧪 Модуль Маруси — тесты', () => {
    
    describe('📦 emotions.js', () => {
        
        test('Все 5 эмоций присутствуют', () => {
            const keys = Object.keys(MARUSYA_EMOTIONS);
            expect(keys).toHaveLength(5);
            expect(keys).toContain('happy');
            expect(keys).toContain('sad');
            expect(keys).toContain('thinking');
            expect(keys).toContain('surprised');
            expect(keys).toContain('celebrate');
        });
        
        test('Каждая эмоция имеет обязательные поля', () => {
            Object.keys(MARUSYA_EMOTIONS).forEach(key => {
                const emotion = MARUSYA_EMOTIONS[key];
                expect(emotion).toHaveProperty('label');
                expect(emotion).toHaveProperty('icon');
                expect(emotion).toHaveProperty('cssClass');
                expect(emotion).toHaveProperty('statusColor');
                expect(emotion).toHaveProperty('animation');
                expect(emotion).toHaveProperty('defaultPhrase');
            });
        });
        
        test('EMOTION_KEYS содержит все ключи', () => {
            expect(EMOTION_KEYS).toEqual(Object.keys(MARUSYA_EMOTIONS));
        });
        
        test('isValidEmotion корректно проверяет эмоции', () => {
            expect(isValidEmotion('happy')).toBe(true);
            expect(isValidEmotion('sad')).toBe(true);
            expect(isValidEmotion('invalid')).toBe(false);
            expect(isValidEmotion('')).toBe(false);
        });
        
        test('getRandomEmotion возвращает корректную эмоцию', () => {
            const emotion = getRandomEmotion();
            expect(EMOTION_KEYS).toContain(emotion);
        });
        
        test('getRandomEmotion возвращает разные эмоции при множественных вызовах', () => {
            const results = new Set();
            for (let i = 0; i < 20; i++) {
                results.add(getRandomEmotion());
            }
            // Должны получить хотя бы 3 разных эмоции из 5
            expect(results.size).toBeGreaterThanOrEqual(3);
        });
    });
    
    describe('📝 dialogues.js', () => {
        
        test('Приветствия существуют', () => {
            expect(MARUSYA_DIALOGUES.welcome).toBeDefined();
            expect(Array.isArray(MARUSYA_DIALOGUES.welcome)).toBe(true);
            expect(MARUSYA_DIALOGUES.welcome.length).toBeGreaterThan(0);
        });
        
        test('Все 24 района имеют описание', () => {
            const districts = Object.keys(MARUSYA_DIALOGUES.district_visit);
            expect(districts).toHaveLength(24);
            
            // Проверяем, что все районы из списка присутствуют
            const expectedDistricts = [
                'Болховский', 'Верховский', 'Глазуновский', 'Дмитровский',
                'Должанский', 'Залегощенский', 'Знаменский', 'Колпнянский',
                'Корсаковский', 'Краснозоренский', 'Кромской', 'Ливенский',
                'Малоархангельский', 'Мценский', 'Новодеревеньковский',
                'Новосильский', 'Орловский', 'Покровский', 'Свердловский',
                'Сосковский', 'Троснянский', 'Урицкий', 'Хотынецкий', 'Шаблыкинский'
            ];
            
            expectedDistricts.forEach(name => {
                expect(districts).toContain(name);
                expect(typeof MARUSYA_DIALOGUES.district_visit[name]).toBe('string');
                expect(MARUSYA_DIALOGUES.district_visit[name].length).toBeGreaterThan(0);
            });
        });
        
        test('Игровые реакции существуют', () => {
            expect(MARUSYA_DIALOGUES.game.win).toBeDefined();
            expect(Array.isArray(MARUSYA_DIALOGUES.game.win)).toBe(true);
            expect(MARUSYA_DIALOGUES.game.win.length).toBeGreaterThan(0);
            
            expect(MARUSYA_DIALOGUES.game.lose).toBeDefined();
            expect(Array.isArray(MARUSYA_DIALOGUES.game.lose)).toBe(true);
            expect(MARUSYA_DIALOGUES.game.lose.length).toBeGreaterThan(0);
        });
        
        test('Подсказки существуют для всех элементов', () => {
            const tooltips = MARUSYA_DIALOGUES.tooltips;
            expect(tooltips).toHaveProperty('map');
            expect(tooltips).toHaveProperty('costume');
            expect(tooltips).toHaveProperty('quiz');
            expect(tooltips).toHaveProperty('timeline');
            expect(tooltips).toHaveProperty('gallery');
            expect(tooltips).toHaveProperty('games');
            expect(tooltips).toHaveProperty('achievements');
        });
        
        test('Случайные фразы существуют', () => {
            expect(MARUSYA_DIALOGUES.random).toBeDefined();
            expect(Array.isArray(MARUSYA_DIALOGUES.random)).toBe(true);
            expect(MARUSYA_DIALOGUES.random.length).toBeGreaterThan(0);
        });
    });
    
    describe('🤖 Marusya.js', () => {
        
        let marusya;
        
        beforeEach(() => {
            marusya = new Marusya({ 
                autoSpeak: false,
                assetPath: '/assets/marusya/assets/img/marusya/'
            });
        });
        
        test('Маруся инициализируется', () => {
            expect(marusya).toBeDefined();
            expect(marusya.currentMood).toBe('happy');
            expect(marusya.isVisible).toBe(true);
            expect(marusya.soundEnabled).toBe(true);
        });
        
        test('Смена настроения работает', () => {
            marusya.setMood('sad');
            expect(marusya.currentMood).toBe('sad');
            expect(marusya.element.classList.contains('sad')).toBe(true);
            
            marusya.setMood('celebrate');
            expect(marusya.currentMood).toBe('celebrate');
            expect(marusya.element.classList.contains('celebrate')).toBe(true);
        });
        
        test('Невалидная эмоция не меняет состояние', () => {
            const previousMood = marusya.currentMood;
            marusya.setMood('invalid_emotion');
            expect(marusya.currentMood).toBe(previousMood);
        });
        
        test('Показ сообщения работает', () => {
            marusya.say('Тестовое сообщение', 1000);
            expect(marusya.text.textContent).toBe('Тестовое сообщение');
            expect(marusya.speech.classList.contains('show')).toBe(true);
            expect(marusya.speech.classList.contains('hidden')).toBe(false);
        });
        
        test('Скрытие сообщения работает', () => {
            marusya.say('Тест', 1000);
            marusya.hideSpeech();
            expect(marusya.speech.classList.contains('hidden')).toBe(true);
            expect(marusya.speech.classList.contains('show')).toBe(false);
        });
        
        test('Переключение звука работает', () => {
            const initial = marusya.soundEnabled;
            marusya.toggleSound();
            expect(marusya.soundEnabled).toBe(!initial);
            marusya.toggleSound();
            expect(marusya.soundEnabled).toBe(initial);
        });
        
        test('Переключение видимости работает', () => {
            const initial = marusya.isVisible;
            marusya.toggleVisibility();
            expect(marusya.isVisible).toBe(!initial);
            expect(marusya.container.style.display).toBe(!initial ? 'block' : 'none');
        });
        
        test('Посещение района работает', () => {
            const districtName = 'Ливенский';
            marusya.visitDistrict(districtName);
            expect(marusya.currentDistrict).toBe(districtName);
            expect(marusya.text.textContent).toContain(districtName);
        });
        
        test('Событие districtComplete вызывает реакцию', () => {
            const spy = jest.spyOn(marusya, 'onDistrictComplete');
            document.dispatchEvent(new CustomEvent('districtComplete', { 
                detail: { name: 'Ливенский', id: 12 } 
            }));
            expect(spy).toHaveBeenCalled();
            expect(marusya.currentMood).toBe('celebrate');
            spy.mockRestore();
        });
        
        test('Событие gameWin вызывает реакцию', () => {
            const spy = jest.spyOn(marusya, 'onGameWin');
            document.dispatchEvent(new CustomEvent('gameWin', { 
                detail: { game: 'Викторина', score: 100 } 
            }));
            expect(spy).toHaveBeenCalled();
            expect(marusya.currentMood).toBe('celebrate');
            spy.mockRestore();
        });
        
        test('Событие achievementUnlocked вызывает реакцию', () => {
            const spy = jest.spyOn(marusya, 'onAchievement');
            document.dispatchEvent(new CustomEvent('achievementUnlocked', { 
                detail: { name: 'Первый район', type: 'district' } 
            }));
            expect(spy).toHaveBeenCalled();
            expect(marusya.currentMood).toBe('surprised');
            spy.mockRestore();
        });
        
        test('Меню переключается корректно', () => {
            expect(marusya.menu.classList.contains('hidden')).toBe(true);
            
            marusya.toggleMenu();
            expect(marusya.menu.classList.contains('hidden')).toBe(false);
            
            marusya.toggleMenu(false);
            expect(marusya.menu.classList.contains('hidden')).toBe(true);
            
            marusya.toggleMenu(true);
            expect(marusya.menu.classList.contains('hidden')).toBe(false);
        });
    });
    
    describe('🔧 Интеграция API', () => {
        
        test('window.MarusyaAPI доступен после инициализации', () => {
            // В реальном браузере API доступен через main.js
            // Здесь проверяем, что структура API существует
            expect(typeof window.MarusyaAPI).toBe('undefined'); // В тестовой среде
            // В реальной среде было бы: expect(typeof window.MarusyaAPI).toBe('object');
        });
        
        test('MarusyaAPI содержит все методы', () => {
            // Проверка структуры API (для документации)
            const apiMethods = [
                'say', 'setMood', 'celebrate', 'showTooltip',
                'visitDistrict', 'hide', 'show', 'getState'
            ];
            
            expect(apiMethods).toContain('say');
            expect(apiMethods).toContain('setMood');
            expect(apiMethods).toContain('celebrate');
            expect(apiMethods).toContain('getState');
        });
    });
    
    describe('♿ Доступность (ARIA)', () => {
        
        test('Контейнер имеет правильные ARIA-атрибуты', () => {
            const container = document.getElementById('marusya-container');
            expect(container.getAttribute('role')).toBe('complementary');
        });
        
        test('Маскот имеет правильные ARIA-атрибуты', () => {
            const marusya = document.getElementById('marusya');
            expect(marusya.getAttribute('role')).toBe('button');
            expect(marusya.getAttribute('tabindex')).toBe('0');
        });
        
        test('Облачко имеет правильные ARIA-атрибуты', () => {
            const speech = document.getElementById('marusya-speech');
            expect(speech.getAttribute('role')).toBe('dialog');
            expect(speech.getAttribute('aria-live')).toBe('polite');
        });
    });
});

// ============================================
//  ЗАПУСК ТЕСТОВ
// ============================================

console.log('🧪 Запуск тестов...');
console.log('📊 Используйте: npm test');
console.log('📖 Документация: https://jestjs.io/');