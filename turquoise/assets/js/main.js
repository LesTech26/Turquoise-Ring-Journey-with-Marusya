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

    /* ---------- Мобильное меню навигации + меню аккаунта (взаимоисключающие) ---------- */
    var burger = document.getElementById('burgerBtn');
    var nav = document.getElementById('main-nav');
    var accountTrigger = document.getElementById('accountMenuTrigger');
    var accountDropdown = document.getElementById('accountMenuDropdown');

    function setNavOpen(isOpen) {
        if (!nav || !burger) return;
        nav.classList.toggle('is-open', isOpen);
        burger.setAttribute('aria-expanded', isOpen ? 'true' : 'false');
        burger.setAttribute('aria-label', isOpen ? 'Закрыть меню' : 'Открыть меню');
    }

    function setAccountOpen(isOpen) {
        if (!accountDropdown || !accountTrigger) return;
        accountDropdown.classList.toggle('is-open', isOpen);
        accountTrigger.setAttribute('aria-expanded', isOpen ? 'true' : 'false');
    }

    if (burger && nav) {
        burger.addEventListener('click', function () {
            var willOpen = !nav.classList.contains('is-open');
            if (willOpen) setAccountOpen(false); // закрываем меню профиля, чтобы не пересекались
            setNavOpen(willOpen);
        });

        // Закрытие меню при клике на ссылку (мобильный UX)
        nav.querySelectorAll('a').forEach(function (link) {
            link.addEventListener('click', function () { setNavOpen(false); });
        });
    }

    if (accountTrigger && accountDropdown) {
        accountTrigger.addEventListener('click', function (e) {
            e.stopPropagation();
            var willOpen = !accountDropdown.classList.contains('is-open');
            if (willOpen) setNavOpen(false); // закрываем нав-меню, чтобы не пересекались
            setAccountOpen(willOpen);
        });

        document.addEventListener('click', function (e) {
            if (!accountDropdown.contains(e.target) && e.target !== accountTrigger) {
                setAccountOpen(false);
            }
        });

        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape') {
                setAccountOpen(false);
                setNavOpen(false);
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

    /* ---------- Анимация прогресс-бара профиля ---------- */
    var progressFill = document.getElementById('profileProgressFill');
    if (progressFill) {
        requestAnimationFrame(function () {
            setTimeout(function () {
                progressFill.classList.add('is-ready');
            }, 50);
        });
    }

    /* ---------- Сайдбар админки на мобильных ---------- */
    var adminToggle = document.getElementById('adminSidebarToggle');
    var adminSidebar = document.getElementById('adminSidebar');
    var adminOverlay = document.getElementById('adminSidebarOverlay');

    function setAdminSidebar(isOpen) {
        if (!adminSidebar) return;
        adminSidebar.classList.toggle('is-open', isOpen);
        if (adminOverlay) adminOverlay.classList.toggle('is-open', isOpen);
        document.body.style.overflow = isOpen ? 'hidden' : '';
    }

    if (adminSidebar) {
        // Гарантируем закрытое состояние при каждой загрузке страницы
        setAdminSidebar(false);
    }

    if (adminToggle && adminSidebar) {
        adminToggle.addEventListener('click', function () {
            setAdminSidebar(!adminSidebar.classList.contains('is-open'));
        });
    }
    if (adminOverlay) {
        adminOverlay.addEventListener('click', function () {
            setAdminSidebar(false);
        });
    }
    if (adminSidebar) {
        adminSidebar.querySelectorAll('a').forEach(function (link) {
            link.addEventListener('click', function () { setAdminSidebar(false); });
        });
    }
})();
