/**
 * Голосовое сопровождение (Web Speech API)
 * Использует встроенный синтез речи в браузере
 */
class MarusyaSpeech {
    constructor() {
        this.isEnabled = true;
        this.voice = null;
        this.rate = 0.9;      // Скорость речи
        this.pitch = 1.2;     // Высота тона (женский голос)
        this.volume = 1;      // Громкость
        this.isSpeaking = false;
        this.synthesis = window.speechSynthesis;
        this.voicesLoaded = false;
        
        this.init();
    }
    
    /**
     * Инициализация: загрузка голосов
     */
    init() {
        if (this.synthesis) {
            // Проверяем, загружены ли голоса
            if (this.synthesis.getVoices().length > 0) {
                this.setVoice();
            } else {
                // Ждём загрузки голосов
                this.synthesis.onvoiceschanged = () => {
                    this.setVoice();
                };
            }
        } else {
            console.warn('Web Speech API не поддерживается в этом браузере');
            this.isEnabled = false;
        }
    }
    
    /**
     * Выбор голоса (женский русский)
     */
    setVoice() {
        const voices = this.synthesis.getVoices();
        // Ищем женский русский голос
        this.voice = voices.find(v => 
            v.lang.includes('ru') && 
            (v.name.includes('Female') || v.name.includes('женский'))
        ) || voices.find(v => v.lang.includes('ru')) || voices[0];
        
        this.voicesLoaded = true;
    }
    
    /**
     * Озвучить текст
     * @param {string} text - Текст для озвучивания
     * @param {Object} options - Дополнительные опции
     * @returns {Promise}
     */
    speak(text, options = {}) {
        return new Promise((resolve, reject) => {
            // Проверка доступности
            if (!this.isEnabled || !this.synthesis) {
                reject(new Error('Speech synthesis not available'));
                return;
            }
            
            // Отменяем текущую речь
            this.cancel();
            
            // Создаём utterance
            const utterance = new SpeechSynthesisUtterance(text);
            utterance.lang = options.lang || 'ru-RU';
            utterance.rate = options.rate || this.rate;
            utterance.pitch = options.pitch || this.pitch;
            utterance.volume = options.volume || this.volume;
            
            if (this.voice) {
                utterance.voice = this.voice;
            }
            
            this.isSpeaking = true;
            
            utterance.onend = () => {
                this.isSpeaking = false;
                resolve();
            };
            
            utterance.onerror = (event) => {
                this.isSpeaking = false;
                if (event.error !== 'canceled') {
                    reject(new Error(`Speech error: ${event.error}`));
                } else {
                    resolve(); // Отмена — не ошибка
                }
            };
            
            this.synthesis.speak(utterance);
        });
    }
    
    /**
     * Отменить текущую речь
     */
    cancel() {
        if (this.synthesis) {
            this.synthesis.cancel();
        }
        this.isSpeaking = false;
    }
    
    /**
     * Включить/выключить озвучивание
     * @returns {boolean} Новое состояние
     */
    toggle() {
        this.isEnabled = !this.isEnabled;
        if (!this.isEnabled) {
            this.cancel();
        }
        return this.isEnabled;
    }
    
    /**
     * Проверить, поддерживается ли Web Speech API
     * @returns {boolean}
     */
    static isSupported() {
        return 'speechSynthesis' in window;
    }
}

// Создаём синглтон для использования во всём модуле
const marusyaSpeech = new MarusyaSpeech();

// Экспорт для Node.js (тесты)
if (typeof module !== 'undefined' && module.exports) {
    module.exports = marusyaSpeech;
}