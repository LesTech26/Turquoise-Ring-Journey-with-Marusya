@echo off
chcp 65001 >nul
cls
echo.
echo ═══════════════════════════════════════════════════════════
echo   БЫСТРАЯ НАСТРОЙКА И ЗАГРУЗКА
echo ═══════════════════════════════════════════════════════════
echo.

REM Добавляем Git в PATH
set "PATH=C:\Program Files\Git\bin;%PATH%"

echo [ШАГ 1] Проверка Git...
git --version
if errorlevel 1 (
    echo.
    echo ОШИБКА: Git не найден!
    echo Проверьте что Git установлен в: C:\Program Files\Git\
    echo.
    pause
    exit /b 1
)

echo.
echo [ШАГ 2] Настройка имени и email...
git config --global user.name "Developer4"
git config --global user.email "developer4@turquoise.local"
echo Имя: Developer4
echo Email: developer4@turquoise.local

echo.
echo [ШАГ 3] Инициализация репозитория...
cd /d "%~dp0"
if not exist ".git" (
    git init
    echo Репозиторий инициализирован
)

echo.
echo [ШАГ 4] Добавление удаленного репозитория...
git remote remove origin 2>nul
git remote add origin https://github.com/LesTech26/Turquoise-Ring-Journey-with-Marusya.git
echo Репозиторий добавлен

echo.
echo [ШАГ 5] Получение веток с GitHub...
git fetch origin
if errorlevel 1 (
    echo.
    echo ВНИМАНИЕ: Не удалось получить данные с GitHub
    echo Проверьте интернет-соединение
    echo.
    pause
    exit /b 1
)

echo.
echo [ШАГ 6] Переключение на ветку site...
git checkout -B site origin/site
if errorlevel 1 (
    echo Создаю новую ветку site...
    git checkout -b site
)

echo.
echo [ШАГ 7] Добавление новых игр...
git add turquoise/games/crossword.php
git add turquoise/games/guess-district.php
git add turquoise/games/dress-up.php
git add turquoise/games/memory.php
git add turquoise/games/spot-difference.php
git add turquoise/games/true-false.php

echo.
echo [ШАГ 8] Проверка что будет добавлено...
git status

echo.
echo [ШАГ 9] Создание коммита...
git commit -m "Добавлены 6 новых игр: кроссворд, угадай район, собери костюм, мемори, найди отличие, правда/неправда"
if errorlevel 1 (
    echo.
    echo Нет изменений для коммита или файлы уже добавлены ранее
    echo.
)

echo.
echo [ШАГ 10] Отправка на GitHub...
echo.
echo ВНИМАНИЕ: Сейчас может появиться запрос логина/пароля!
echo.
echo Логин: LesTech26
echo Пароль: Personal Access Token (не обычный пароль!)
echo.
echo Как получить токен:
echo 1. https://github.com/settings/tokens
echo 2. Generate new token - Classic
echo 3. Выбери: repo (все галочки)
echo 4. Скопируй токен и используй как пароль
echo.
pause

git push origin site

if errorlevel 1 (
    echo.
    echo ═══════════════════════════════════════════════════════════
    echo   ОШИБКА ПРИ ОТПРАВКЕ
    echo ═══════════════════════════════════════════════════════════
    echo.
    echo Возможные причины:
    echo - Неправильный логин/пароль
    echo - Нет доступа к репозиторию
    echo - Проблемы с интернетом
    echo.
    echo Попробуй ещё раз или обратись за помощью
    echo.
    pause
    exit /b 1
)

echo.
echo ═══════════════════════════════════════════════════════════
echo   ГОТОВО! Игры успешно загружены на GitHub!
echo ═══════════════════════════════════════════════════════════
echo.
echo Проверь результат:
echo https://github.com/LesTech26/Turquoise-Ring-Journey-with-Marusya/tree/site
echo.
echo В папке turquoise/games/ должны быть твои 6 новых файлов!
echo.
pause
