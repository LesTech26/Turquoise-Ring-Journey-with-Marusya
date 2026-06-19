/**
 * Система всплывающих подсказок
 * Показывает тултипы с текстом у указанных элементов
 */
class MarusyaTooltips {
    constructor(marusya) {
        this.marusya = marusya;
        this.activeTooltips = new Set();
        this.tooltipTimeout = null;
        this.isTourActive = false;
        
        // Создаём элемент для подсказки, если его нет
        this.tooltipElement = document.getElementById('marusya-tooltip');
        if (!this.tooltipElement) {
            this.createTooltipElement();
        }
    }
    
    /**
     * Создать элемент для подсказки
     */
    createTooltipElement() {
        const tooltip = document.createElement('div');
        tooltip.id = 'marusya-tooltip';
        tooltip.className = 'marusya-tooltip hidden';
        tooltip.innerHTML = `
            <div class="tooltip-content">
                <span class="tooltip-text"></span>
                <button class="tooltip-close">✕</button>
            </div>
            <div class="tooltip-arrow"></div>
        `;
        document.body.appendChild(tooltip);
        this.tooltipElement = tooltip;
        
        // Обработчик закрытия
        tooltip.querySelector('.tooltip-close').addEventListener('click', () => {
            this.hide();
        });
    }
    
    /**
     * Показать подсказку для элемента
     * @param {HTMLElement} element - Целевой элемент
     * @param {string} text - Текст подсказки
     * @param {Object} options - Дополнительные опции
     */
    show(element, text, options = {}) {
        const {
            position = 'top',
            delay = 0,
            duration = 5000,
            highlight = false
        } = options;
        
        if (delay > 0) {
            setTimeout(() => {
                this._showTooltip(element, text, position, duration, highlight);
            }, delay);
        } else {
            this._showTooltip(element, text, position, duration, highlight);
        }
    }
    
    /**
     * Внутренний метод показа подсказки
     */
    _showTooltip(element, text, position, duration, highlight) {
        // Очищаем старый таймер
        if (this.tooltipTimeout) {
            clearTimeout(this.tooltipTimeout);
        }
        
        const rect = element.getBoundingClientRect();
        const tooltip = this.tooltipElement;
        const textEl = tooltip.querySelector('.tooltip-text');
        
        textEl.textContent = text;
        tooltip.classList.remove('hidden');
        
        // Позиционирование
        this.positionTooltip(tooltip, rect, position);
        
        // Подсветка элемента
        if (highlight) {
            element.classList.add('tooltip-highlight');
            element.style.position = 'relative';
            element.style.zIndex = '9998';
        }
        
        // Авто-скрытие
        if (duration > 0) {
            this.tooltipTimeout = setTimeout(() => {
                this.hide(element);
            }, duration);
        }
    }
    
    /**
     * Позиционирование тултипа
     */
    positionTooltip(tooltip, rect, position) {
        const tooltipRect = tooltip.getBoundingClientRect();
        let top, left;
        const spacing = 12;
        
        switch (position) {
            case 'top':
                top = rect.top - tooltipRect.height - spacing;
                left = rect.left + (rect.width - tooltipRect.width) / 2;
                break;
            case 'bottom':
                top = rect.bottom + spacing;
                left = rect.left + (rect.width - tooltipRect.width) / 2;
                break;
            case 'left':
                top = rect.top + (rect.height - tooltipRect.height) / 2;
                left = rect.left - tooltipRect.width - spacing;
                break;
            case 'right':
                top = rect.top + (rect.height - tooltipRect.height) / 2;
                left = rect.right + spacing;
                break;
            default:
                top = rect.top - tooltipRect.height - spacing;
                left = rect.left + (rect.width - tooltipRect.width) / 2;
        }
        
        // Проверка выхода за границы экрана
        const viewportWidth = window.innerWidth;
        const viewportHeight = window.innerHeight;
        
        if (left < 10) left = 10;
        if (left + tooltipRect.width > viewportWidth - 10) {
            left = viewportWidth - tooltipRect.width - 10;
        }
        if (top < 10) top = 10;
        if (top + tooltipRect.height > viewportHeight - 10) {
            top = viewportHeight - tooltipRect.height - 10;
        }
        
        tooltip.style.top = `${top}px`;
        tooltip.style.left = `${left}px`;
    }
    
    /**
     * Скрыть подсказку
     */
    hide(element) {
        if (this.tooltipTimeout) {
            clearTimeout(this.tooltipTimeout);
            this.tooltipTimeout = null;
        }
        
        this.tooltipElement.classList.add('hidden');
        
        // Убираем подсветку
        if (element) {
            element.classList.remove('tooltip-highlight');
            element.style.position = '';
            element.style.zIndex = '';
        }
    }
    
    /**
     * Запустить тур по сайту
     */
    startTour(steps) {
        if (this.isTourActive) return;
        this.isTourActive = true;
        
        let currentStep = 0;
        
        const showStep = () => {
            if (currentStep >= steps.length) {
                this.isTourActive = false;
                return;
            }
            
            const step = steps[currentStep];
            const element = document.querySelector(step.selector);
            
            if (!element) {
                currentStep++;
                showStep();
                return;
            }
            
            this.show(element, step.text, {
                position: step.position || 'top',
                duration: step.duration || 5000,
                highlight: true
            });
            
            // Переход к следующему шагу по клику
            const onNext = () => {
                this.hide(element);
                currentStep++;
                setTimeout(showStep, 300);
            };
            
            this.tooltipElement.addEventListener('click', onNext, { once: true });
        };
        
        showStep();
    }
    
    /**
     * Предопределённые туры
     */
    getTours() {
        return {
            main: [
                { selector: '#main-map', text: 'Здесь ты можешь увидеть карту Орловской области. Кликай на районы! 🗺️', position: 'top' },
                { selector: '.district-card:first-child', text: 'Это карточка района. Здесь есть вся информация! 📇', position: 'top' },
                { selector: '#marusya', text: 'А это я — Маруся! Я буду помогать тебе в путешествии! 👋', position: 'left' }
            ],
            district: [
                { selector: '.district-title', text: 'Добро пожаловать в этот район! Узнай его историю! 📖', position: 'bottom' },
                { selector: '.timeline', text: 'Здесь временная лента. Листай, чтобы узнать события! ⏳', position: 'top' },
                { selector: '.costume-3d', text: 'Здесь ты можешь собрать традиционный костюм! 👗', position: 'top' },
                { selector: '.quiz-container', text: 'А здесь — викторина! Проверь свои знания! ❓', position: 'top' }
            ]
        };
    }
}

// Экспорт для Node.js (тесты)
if (typeof module !== 'undefined' && module.exports) {
    module.exports = MarusyaTooltips;
}