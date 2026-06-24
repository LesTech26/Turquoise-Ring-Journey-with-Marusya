/**
 * Marusya.js
 * Главный класс Маруси — маскот проекта
 * Адаптировано под проект turquoise
 */

class Marusya {
    constructor(options = {}) {
        // Элементы DOM
        this.container = document.getElementById('marusya-container');
        this.element = document.getElementById('marusya');
        this.avatar = document.getElementById('marusya-avatar');
        this.speech = document.getElementById('marusya-speech');
        this.text = document.getElementById('marusya-text');
        this.status = document.getElementById('marusya-status');
        this.menu = document.getElementById('marusya-menu');
        
        // Состояние
        this.currentMood = 'happy';
        this.isSpeaking = false;
        this.speechTimeout = null;
        this.isVisible = true;
        this.soundEnabled = true;
        this.currentDistrict = null;
        this.tourCompleted = false;
        
        // Подсистемы
        this.speech = new MarusyaSpeech();
        this.tooltips = new MarusyaTooltips(this);
        this.celebrations = new MarusyaCelebrations();
        
        // Настройки
        this.speechDelay = options.speechDelay || 4000;
        this.autoSpeak = options.autoSpeak !== false;
        this.assetPath = options.assetPath || '/mascot_interactive/assets/img/marusya/';
        
        this.init();
    }
    
    init() {
        this.setupEventListeners();
        this.setMood('happy');
        this.welcome();
        
        const tourShown = localStorage.getItem('marusya_tour_shown');
        if (!tourShown) {
            setTimeout(() => this.startTour('main'), 3000);
        }
        
        if (this.autoSpeak) {
            this.startAutoTips();
        }
        
        console.log('🐻 Маруся готова!');
    }
    
    setupEventListeners() {
        // Клик по Марусе
        this.element.addEventListener('click', (e) => {
            e.stopPropagation();
            this.handleClick();
        });
        
        // Кнопка озвучивания
        const speakBtn = document.getElementById('marusya-speak-btn');
        if (speakBtn) {
            speakBtn.addEventListener('click', (e) => {
                e.stopPropagation();
                this.speak(this.text.textContent);
            });
        }
        
        // Кнопка закрытия речи
        const closeBtn = document.getElementById('marusya-close-btn');
        if (closeBtn) {
            closeBtn.addEventListener('click', (e) => {
                e.stopPropagation();
                this.hideSpeech();
            });
        }
        
        // Кнопка скрытия маскота
        const hideBtn = document.getElementById('marusya-hide');
        if (hideBtn) {
            hideBtn.addEventListener('click', (e) => {
                e.stopPropagation();
                this.toggleVisibility();
            });
        }
        
        // Кнопки меню
        document.querySelectorAll('#marusya-menu button[data-action]').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.stopPropagation();
                const action = btn.dataset.action;
                this.handleMenuAction(action);
                this.toggleMenu(false);
            });
        });
        
        // Закрыть меню при клике вне
        document.addEventListener('click', (e) => {
            if (!this.element.contains(e.target)) {
                this.toggleMenu(false);
            }
        });
        
        // Реакция на события от других модулей
        document.addEventListener('districtComplete', (e) => {
            this.onDistrictComplete(e.detail);
        });
        
        document.addEventListener('gameWin', (e) => {
            this.onGameWin(e.detail);
        });
        
        document.addEventListener('achievementUnlocked', (e) => {
            this.onAchievement(e.detail);
        });
    }
    
    welcome() {
        const messages = MARUSYA_DIALOGUES.welcome;
        const msg = messages[Math.floor(Math.random() * messages.length)];
        this.say(msg, 5000);
    }
    
    say(text, duration = 4000) {
        if (this.speechTimeout) {
            clearTimeout(this.speechTimeout);
        }
        
        this.text.textContent = text;
        this.speech.classList.remove('hidden');
        this.speech.classList.add('show');
        
        if (this.soundEnabled && this.autoSpeak) {
            this.speak(text);
        }
        
        if (duration > 0) {
            this.speechTimeout = setTimeout(() => {
                this.hideSpeech();
            }, duration);
        }
    }
    
    hideSpeech() {
        this.speech.classList.remove('show');
        this.speech.classList.add('hidden');
        if (this.speechTimeout) {
            clearTimeout(this.speechTimeout);
            this.speechTimeout = null;
        }
    }
    
    speak(text) {
        if (!this.soundEnabled) return;
        this.speech.speak(text).catch(err => {
            console.warn('Ошибка озвучивания:', err);
        });
    }
    
    setMood(mood) {
        if (!isValidEmotion(mood)) return;
        
        EMOTION_KEYS.forEach(key => {
            this.element.classList.remove(key);
            if (this.status) {
                this.status.classList.remove(key);
            }
        });
        
        this.element.classList.add(mood);
        if (this.status) {
            this.status.classList.add(mood);
        }
        
        const emotion = MARUSYA_EMOTIONS[mood];
        if (emotion && this.avatar) {
            this.avatar.src = this.assetPath + emotion.icon;
            this.avatar.alt = emotion.label;
        }
        
        this.currentMood = mood;
    }
    
    handleClick() {
        this.toggleMenu();
        
        const reactions = MARUSYA_DIALOGUES.click_reactions;
        const msg = reactions[Math.floor(Math.random() * reactions.length)];
        this.say(msg, 3000);
        
        const randomMood = getRandomEmotion();
        this.setMood(randomMood);
        setTimeout(() => {
            this.setMood('happy');
        }, 1500);
    }
    
    toggleMenu(show = null) {
        if (show === null) {
            this.menu.classList.toggle('hidden');
        } else if (show) {
            this.menu.classList.remove('hidden');
        } else {
            this.menu.classList.add('hidden');
        }
    }
    
    handleMenuAction(action) {
        switch (action) {
            case 'help':
                this.say('Я всегда готова помочь! Задавай вопросы! ❓', 5000);
                this.startTour('main');
                break;
            case 'reset':
                if (confirm('Сбросить весь прогресс?')) {
                    localStorage.clear();
                    this.say('Прогресс сброшен! Начинаем заново! 🔄', 4000);
                    document.dispatchEvent(new CustomEvent('progressReset'));
                }
                break;
            case 'settings':
                this.toggleSound();
                break;
        }
    }
    
    toggleSound() {
        this.soundEnabled = !this.soundEnabled;
        const msg = this.soundEnabled 
            ? MARUSYA_DIALOGUES.settings.sound_on 
            : MARUSYA_DIALOGUES.settings.sound_off;
        this.say(msg, 3000);
    }
    
    toggleVisibility() {
        this.isVisible = !this.isVisible;
        this.container.style.display = this.isVisible ? 'block' : 'none';
        
        if (this.isVisible) {
            this.say('Я вернулась! 👋', 3000);
        }
    }
    
    showTooltip(element, text, options = {}) {
        this.tooltips.show(element, text, options);
    }
    
    startTour(tourName = 'main') {
        const tours = this.tooltips.getTours();
        const steps = tours[tourName];
        
        if (!steps) {
            console.warn(`Тур "${tourName}" не найден`);
            return;
        }
        
        this.tooltips.startTour(steps);
        localStorage.setItem('marusya_tour_shown', 'true');
    }
    
    onDistrictComplete(data) {
        const msg = MARUSYA_DIALOGUES.district_complete[
            Math.floor(Math.random() * MARUSYA_DIALOGUES.district_complete.length)
        ];
        this.say(msg, 4000);
        this.setMood('celebrate');
        this.celebrations.celebrate('confetti');
        setTimeout(() => this.setMood('happy'), 3000);
    }
    
    onGameWin(data) {
        const msgs = MARUSYA_DIALOGUES.game.win;
        const msg = msgs[Math.floor(Math.random() * msgs.length)];
        this.say(msg, 4000);
        this.setMood('celebrate');
        this.celebrations.celebrate('full');
        setTimeout(() => this.setMood('happy'), 3000);
    }
    
    onAchievement(data) {
        const msgs = MARUSYA_DIALOGUES.achievement;
        const msg = msgs[Math.floor(Math.random() * msgs.length)];
        this.say(msg, 4000);
        this.setMood('surprised');
        this.celebrations.celebrate('stars');
        setTimeout(() => this.setMood('happy'), 2500);
    }
    
    visitDistrict(districtName) {
        const dialogues = MARUSYA_DIALOGUES.district_visit;
        const msg = dialogues[districtName] || `Отличный выбор — ${districtName}! 🌟`;
        this.say(msg, 4000);
        this.setMood('happy');
        this.currentDistrict = districtName;
    }
    
    showTipForElement(selector, tipKey = 'map') {
        const element = document.querySelector(selector);
        if (!element) return;
        
        const tip = MARUSYA_DIALOGUES.tooltips[tipKey];
        if (tip) {
            this.showTooltip(element, tip, { duration: 6000, highlight: true });
        }
    }
    
    startAutoTips() {
        // Подсказка по карте на главной
        if (document.querySelector('.district-grid')) {
            setTimeout(() => {
                this.showTipForElement('.district-grid', 'map');
            }, 4000);
        }
        
        // Подсказка по играм
        if (document.querySelector('.game-layout')) {
            setTimeout(() => {
                this.showTipForElement('.game-panel', 'games');
            }, 5000);
        }
    }
    
    randomPhrase() {
        const phrases = MARUSYA_DIALOGUES.random;
        const msg = phrases[Math.floor(Math.random() * phrases.length)];
        this.say(msg, 5000);
    }
}