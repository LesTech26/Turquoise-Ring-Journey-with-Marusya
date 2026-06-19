/**
 * Анимации побед и празднований
 * Конфетти, звёзды, ленты
 */
class MarusyaCelebrations {
    constructor() {
        this.isActive = false;
        this.effects = [];
        this.hasConfetti = typeof confetti !== 'undefined';
    }
    
    /**
     * Конфетти (салют)
     * @param {Object} options - Настройки
     */
    confetti(options = {}) {
        if (!this.hasConfetti) {
            console.warn('canvas-confetti не загружен');
            return;
        }
        
        const defaults = {
            particleCount: 150,
            spread: 70,
            origin: { y: 0.6 },
            colors: ['#17A2B8', '#FFD700', '#CC0000', '#1E3A8A', '#4A7C59', '#E08D3C']
        };
        
        const config = { ...defaults, ...options };
        confetti(config);
        
        // Дополнительный залп с другой стороны
        setTimeout(() => {
            confetti({
                ...config,
                origin: { y: 0.5, x: 0.2 }
            });
        }, 200);
        
        setTimeout(() => {
            confetti({
                ...config,
                origin: { y: 0.5, x: 0.8 }
            });
        }, 400);
    }
    
    /**
     * Звёздный дождь
     * @param {HTMLElement} container - Контейнер для эффекта
     */
    stars(container = document.body) {
        const count = 50;
        const colors = ['#FFD700', '#FF6B6B', '#4ECDC4', '#FFE66D', '#A8E6CF'];
        
        for (let i = 0; i < count; i++) {
            const star = document.createElement('div');
            star.className = 'celebration-star';
            star.style.cssText = `
                position: fixed;
                left: ${Math.random() * 100}vw;
                top: ${-10 + Math.random() * 10}vh;
                width: ${6 + Math.random() * 14}px;
                height: ${6 + Math.random() * 14}px;
                background: ${colors[Math.floor(Math.random() * colors.length)]};
                border-radius: 50%;
                pointer-events: none;
                z-index: 10000;
                animation: starFall ${2 + Math.random() * 3}s ease-in forwards;
                animation-delay: ${Math.random() * 0.5}s;
                box-shadow: 0 0 20px ${colors[Math.floor(Math.random() * colors.length)]};
            `;
            container.appendChild(star);
            
            setTimeout(() => {
                star.remove();
            }, 5000);
        }
    }
    
    /**
     * Цветные ленты
     * @param {HTMLElement} container - Контейнер для эффекта
     */
    ribbons(container = document.body) {
        const colors = ['#17A2B8', '#FFD700', '#CC0000', '#1E3A8A', '#4A7C59'];
        const count = 30;
        
        for (let i = 0; i < count; i++) {
            const ribbon = document.createElement('div');
            ribbon.className = 'celebration-ribbon';
            const width = 20 + Math.random() * 40;
            ribbon.style.cssText = `
                position: fixed;
                left: ${Math.random() * 100}vw;
                top: -20px;
                width: ${width}px;
                height: ${4 + Math.random() * 6}px;
                background: ${colors[Math.floor(Math.random() * colors.length)]};
                transform: rotate(${Math.random() * 360}deg);
                pointer-events: none;
                z-index: 10000;
                animation: ribbonFall ${2 + Math.random() * 2}s ease-in forwards;
                animation-delay: ${Math.random() * 0.8}s;
                border-radius: 2px;
                opacity: 0.8;
            `;
            container.appendChild(ribbon);
            
            setTimeout(() => {
                ribbon.remove();
            }, 5000);
        }
    }
    
    /**
     * Большое празднование (всё вместе)
     * @param {string} type - Тип празднования
     * @param {HTMLElement} container - Контейнер
     */
    celebrate(type = 'full', container = document.body) {
        if (this.isActive) return;
        this.isActive = true;
        
        switch (type) {
            case 'confetti':
                this.confetti();
                break;
            case 'stars':
                this.stars(container);
                break;
            case 'ribbons':
                this.ribbons(container);
                break;
            case 'full':
            default:
                this.confetti({ particleCount: 100 });
                setTimeout(() => this.stars(container), 300);
                setTimeout(() => this.ribbons(container), 600);
                setTimeout(() => this.confetti({ particleCount: 80, spread: 50 }), 800);
                break;
        }
        
        // Сбрасываем флаг через 3 секунды
        setTimeout(() => {
            this.isActive = false;
        }, 3000);
    }
    
    /**
     * Добавить CSS для анимаций (если ещё нет)
     */
    static injectStyles() {
        if (document.getElementById('marusya-celebrations-styles')) return;
        
        const styles = document.createElement('style');
        styles.id = 'marusya-celebrations-styles';
        styles.textContent = `
            @keyframes starFall {
                0% {
                    transform: translateY(0) rotate(0deg) scale(1);
                    opacity: 1;
                }
                100% {
                    transform: translateY(110vh) rotate(720deg) scale(0.2);
                    opacity: 0;
                }
            }
            
            @keyframes ribbonFall {
                0% {
                    transform: translateY(0) rotate(0deg) scaleX(1);
                    opacity: 0.8;
                }
                100% {
                    transform: translateY(110vh) rotate(720deg) scaleX(2);
                    opacity: 0;
                }
            }
            
            .celebration-star,
            .celebration-ribbon {
                animation-fill-mode: forwards;
            }
        `;
        document.head.appendChild(styles);
    }
}

// Инжектим стили при загрузке
MarusyaCelebrations.injectStyles();

// Экспорт для Node.js (тесты)
if (typeof module !== 'undefined' && module.exports) {
    module.exports = MarusyaCelebrations;
}