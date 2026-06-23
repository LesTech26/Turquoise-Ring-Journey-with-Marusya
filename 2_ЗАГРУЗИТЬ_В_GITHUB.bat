@echo off
chcp 65001 >nul
echo ═══════════════════════════════════════════════════════════
echo   АВТОМАТИЧЕСКАЯ ЗАГРУЗКА ИГР В GITHUB
echo ═══════════════════════════════════════════════════════════
echo.

REM Устанавливаем путь к Git
set "PATH=C:\Program Files\Git\bin;%PATH%"

echo [1/8] Проверка Git...
git --version
if errorlevel 1 (
    echo.
    echo ОШИБКА: Git не установлен!
    echo Сначала запустите: 1_УСТАНОВИ_GIT.txt
    echo.
    pause
    exit /b 1
)

echo.
echo [2/8] Переход в папку проекта...
cd /d "%~dp0"
echo Текущая папка: %CD%

echo.
echo [3/8] Проверка удаленного репозитория...
git remote -v
if errorlevel 1 (
    echo Добавляю удаленный репозиторий...
    git remote add origin https://github.com/LesTech26/Turquoise-Ring-Journey-with-Marusya.git
)

echo.
echo [4/8] Получение последних изменений...
git fetch origin

echo.
echo [5/8] Переключение на ветку site...
git checkout site
if errorlevel 1 (
    echo Создаю новую ветку site...
    git checkout -b site origin/site
)

echo.
echo [6/8] Добавление новых игр...
git add turquoise/games/crossword.php
git add turquoise/games/guess-district.php
git add turquoise/games/dress-up.php
git add turquoise/games/memory.php
git add turquoise/games/spot-difference.php
git add turquoise/games/true-false.php

echo.
echo [7/8] Создание коммита...
git commit -m "Добавлены 6 новых игр: кроссворд, угадай район, собери костюм, мемори, найди отличие, правда/неправда"

echo.
echo [8/8] Отправка на GitHub...
git push origin site

echo.
echo ═══════════════════════════════════════════════════════════
echo   ГОТОВО! Игры загружены на GitHub
echo ═══════════════════════════════════════════════════════════
echo.
echo Проверь на GitHub:
echo https://github.com/LesTech26/Turquoise-Ring-Journey-with-Marusya/tree/site
echo.
pause
