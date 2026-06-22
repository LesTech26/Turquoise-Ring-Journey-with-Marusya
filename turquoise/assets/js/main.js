/**
 * main.js
 * Базовые интерактивные элементы шаблона:
 *   - мобильное гамбургер-меню
 *   - выпадающее меню профиля
 *   - открытие/закрытие модальных окон (<dialog>)
 *   - сворачиваемый сайдбар админки на мобильных
 *
 * Игровая логика (quiz/puzzle/coloring) и анимация маскота
 * подключаются отдельными скриптами другими разработчиками.
 */
(function () {
    'use strict';

    /* ---------- Мобильное меню ---------- */
    var burger = document.getElementById('burgerBtn');
    var nav = document.getElementById('main-nav');

    if (burger && nav) {
        burger.addEventListener('click', function () {
            var isOpen = nav.classList.toggle('is-open');
            burger.setAttribute('aria-expanded', isOpen ? 'true' : 'false');
            burger.setAttribute('aria-label', isOpen ? 'Закрыть меню' : 'Открыть меню');
        });

        // Закрытие меню при клике на ссылку (мобильный UX)
        nav.querySelectorAll('a').forEach(function (link) {
            link.addEventListener('click', function () {
                nav.classList.remove('is-open');
                burger.setAttribute('aria-expanded', 'false');
            });
        });
    }

    /* ---------- Меню аккаунта ---------- */
    var accountTrigger = document.getElementById('accountMenuTrigger');
    var accountDropdown = document.getElementById('accountMenuDropdown');

    if (accountTrigger && accountDropdown) {
        accountTrigger.addEventListener('click', function (e) {
            e.stopPropagation();
            var isOpen = accountDropdown.classList.toggle('is-open');
            accountTrigger.setAttribute('aria-expanded', isOpen ? 'true' : 'false');
        });

        document.addEventListener('click', function (e) {
            if (!accountDropdown.contains(e.target) && e.target !== accountTrigger) {
                accountDropdown.classList.remove('is-open');
                accountTrigger.setAttribute('aria-expanded', 'false');
            }
        });

        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape') {
                accountDropdown.classList.remove('is-open');
                accountTrigger.setAttribute('aria-expanded', 'false');
            }
        });
    }

    /* ---------- Модальные окна ---------- */
    window.openModal = function (id) {
        var modal = document.getElementById(id);
        if (modal && typeof modal.showModal === 'function') {
            modal.showModal();
        }
    };

    window.closeModal = function (id) {
        var modal = document.getElementById(id);
        if (modal && typeof modal.close === 'function') {
            modal.close();
        }
    };

    document.querySelectorAll('.modal').forEach(function (modal) {
        modal.querySelectorAll('[data-modal-close]').forEach(function (btn) {
            btn.addEventListener('click', function () { modal.close(); });
        });
        // Закрытие по клику на backdrop
        modal.addEventListener('click', function (e) {
            var rect = modal.getBoundingClientRect();
            var inside = e.clientX >= rect.left && e.clientX <= rect.right &&
                         e.clientY >= rect.top && e.clientY <= rect.bottom;
            if (!inside) modal.close();
        });
    });

    /* ---------- Сайдбар админки на мобильных ---------- */
    var adminToggle = document.getElementById('adminSidebarToggle');
    var adminSidebar = document.getElementById('adminSidebar');

    if (adminToggle && adminSidebar) {
        adminToggle.addEventListener('click', function () {
            adminSidebar.classList.toggle('is-open');
        });
    }
})();
