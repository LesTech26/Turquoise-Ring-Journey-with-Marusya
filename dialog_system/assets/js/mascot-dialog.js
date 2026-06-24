/**
 * Диалоговая система Маруси
 * Ветвление, печать текста, анимации
 */

(function() {
    'use strict';

    // --- DOM-элементы ---
    const toggleBtn = document.getElementById('mascot-toggle');
    const windowEl = document.getElementById('mascot-window');
    const closeBtn = document.getElementById('mascot-close');
    const messagesEl = document.getElementById('mascot-messages');
    const optionsEl = document.getElementById('mascot-options');
    const typingEl = document.getElementById('mascot-typing');

    let isOpen = false;
    let isTyping = false;
    let currentDialogId = 'greeting';
    let dialogHistory = [];

    // --- Вспомогательные функции ---

    // Эффект печати текста
    function typeMessage(text, element, callback) {
        isTyping = true;
        typingEl.style.display = 'block';
        element.textContent = '';
        let index = 0;
        const speed = 25; // мс на символ

        function typeChar() {
            if (index < text.length) {
                element.textContent += text.charAt(index);
                index++;
                // Прокрутка вниз
                messagesEl.scrollTop = messagesEl.scrollHeight;
                setTimeout(typeChar, speed);
            } else {
                isTyping = false;
                typingEl.style.display = 'none';
                if (callback) callback();
            }
        }

        // Небольшая задержка перед началом печати
        setTimeout(typeChar, 300);
    }

    // Добавить сообщение в чат
    function addMessage(text, sender, callback) {
        const div = document.createElement('div');
        div.className = `mascot-message ${sender}`;

        if (sender === 'bot') {
            const avatarSpan = document.createElement('span');
            avatarSpan.className = 'mascot-msg-avatar';
            avatarSpan.textContent = '🌸';
            div.appendChild(avatarSpan);

            const textSpan = document.createElement('span');
            textSpan.className = 'mascot-msg-text';
            div.appendChild(textSpan);

            messagesEl.appendChild(div);
            messagesEl.scrollTop = messagesEl.scrollHeight;

            // Печать с эффектом
            typeMessage(text, textSpan, callback);
        } else {
            // Сообщение пользователя — мгновенно
            div.textContent = text;
            messagesEl.appendChild(div);
            messagesEl.scrollTop = messagesEl.scrollHeight;
            if (callback) callback();
        }
    }

    // Показать варианты ответов
    function showOptions(options) {
        optionsEl.innerHTML = '';
        if (!options || options.length === 0) {
            // Если вариантов нет — кнопка для закрытия
            const btn = document.createElement('button');
            btn.className = 'mascot-option-btn';
            btn.textContent = 'Закрыть';
            btn.addEventListener('click', () => {
                closeDialog();
            });
            optionsEl.appendChild(btn);
            return;
        }

        options.forEach((opt, index) => {
            const btn = document.createElement('button');
            btn.className = 'mascot-option-btn';
            btn.textContent = opt.text;
            // Задержка для анимации появления
            btn.style.animationDelay = `${index * 0.08}s`;
            btn.addEventListener('click', () => {
                handleOptionClick(opt);
            });
            optionsEl.appendChild(btn);
        });
    }

    // Обработка клика по варианту
    function handleOptionClick(option) {
        if (isTyping) return;

        // Добавить сообщение пользователя
        addMessage(option.text, 'user', () => {
            // Переход к следующему диалогу
            if (option.next && dialogData[option.next]) {
                currentDialogId = option.next;
                showDialog(currentDialogId);
            } else {
                // Если next === null или не найден — завершаем
                addMessage('Спасибо за беседу! Заходи ещё! 😊', 'bot', () => {
                    setTimeout(() => {
                        closeDialog();
                    }, 2000);
                });
                optionsEl.innerHTML = '';
            }
        });

        // Скрыть кнопки во время ответа
        optionsEl.innerHTML = '';
    }

    // Показать диалог по ID
    function showDialog(dialogId) {
        const dialog = dialogData[dialogId];
        if (!dialog) {
            // Если диалог не найден — показать приветствие
            showDialog('greeting');
            return;
        }

        // Добавить сообщение бота
        addMessage(dialog.text, 'bot', () => {
            // Показать варианты после окончания печати
            showOptions(dialog.options);
        });
    }

    // Открыть диалог
    function openDialog() {
        if (isOpen) return;
        isOpen = true;
        windowEl.style.display = 'flex';
        // Очистка
        messagesEl.innerHTML = '';
        optionsEl.innerHTML = '';
        currentDialogId = 'greeting';
        // Показать приветствие
        showDialog('greeting');
        // Анимация
        windowEl.style.animation = 'none';
        requestAnimationFrame(() => {
            windowEl.style.animation = 'slide-up 0.4s cubic-bezier(0.34, 1.56, 0.64, 1)';
        });
    }

    // Закрыть диалог
    function closeDialog() {
        isOpen = false;
        windowEl.style.display = 'none';
        // Остановить печать
        isTyping = false;
        typingEl.style.display = 'none';
    }

    // --- Инициализация ---

    // Открыть/закрыть по клику на триггер
    toggleBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        if (isOpen) {
            closeDialog();
        } else {
            openDialog();
        }
    });

    // Закрыть по крестику
    closeBtn.addEventListener('click', closeDialog);

    // Закрыть по клику вне окна
    document.addEventListener('click', (e) => {
        if (isOpen) {
            const target = e.target;
            if (!windowEl.contains(target) && target !== toggleBtn && !toggleBtn.contains(target)) {
                closeDialog();
            }
        }
    });

    // Закрыть по Escape
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && isOpen) {
            closeDialog();
        }
    });

    // Авто-открытие через 3 секунды после загрузки (только 1 раз)
    let autoOpened = false;
    setTimeout(() => {
        if (!autoOpened && !isOpen) {
            autoOpened = true;
            openDialog();
            // Автозакрытие через 30 секунд, если пользователь не взаимодействовал
            setTimeout(() => {
                if (isOpen && !autoOpened) {
                    closeDialog();
                }
            }, 30000);
        }
    }, 3000);

    console.log('🌸 Диалоговая система Маруси загружена!');
})();