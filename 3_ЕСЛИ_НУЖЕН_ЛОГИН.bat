@echo off
chcp 65001 >nul
echo ═══════════════════════════════════════════════════════════
echo   НАСТРОЙКА GIT (первый раз)
echo ═══════════════════════════════════════════════════════════
echo.

REM Устанавливаем путь к Git
set "PATH=C:\Program Files\Git\bin;%PATH%"

echo Если Git спросит логин/пароль при push:
echo.
echo 1. Логин: твой логин GitHub
echo 2. Пароль: НЕ обычный пароль!
echo.
echo    Нужен Personal Access Token (PAT):
echo    https://github.com/settings/tokens
echo.
echo    Создай новый token с правами:
echo    - repo (все галочки)
echo.
echo    Скопируй token и используй как пароль
echo.
echo ═══════════════════════════════════════════════════════════
echo   Настройка имени и email (ОБЯЗАТЕЛЬНО)
echo ═══════════════════════════════════════════════════════════
echo.

set /p USERNAME="Введи своё имя: "
set /p EMAIL="Введи свой email: "

git config --global user.name "%USERNAME%"
git config --global user.email "%EMAIL%"

echo.
echo Настройки сохранены!
echo.
echo Имя: %USERNAME%
echo Email: %EMAIL%
echo.
echo Теперь запусти: 2_ЗАГРУЗИТЬ_В_GITHUB.bat
echo.
pause
