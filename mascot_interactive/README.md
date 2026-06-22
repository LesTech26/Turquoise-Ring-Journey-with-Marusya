# 🐻 mascot_interactive — Маскот и интерактив

Интерактивный персонаж-гид Маруся для проекта  
«Путешествие по бирюзовому кольцу России».

## 📦 Структура

```
mascot_interactive/
├── src/
│   ├── js/           # JavaScript (7 файлов)
│   ├── css/          # Стили
│   └── templates/    # HTML-шаблон
├── assets/
│   └── img/          # SVG-иконки эмоций
├── README.md
└── package.json
```

## 🚀 Интеграция в проект turquoise

### 1. В `turquoise/templates/header.php` (после основного CSS):
```php
<link rel="stylesheet" href="/mascot_interactive/src/css/marusya.css">
```

### 2. В `turquoise/templates/header.php` (после `<header>`):
```php
<?php include_once __DIR__ . '/../../mascot_interactive/src/templates/marusya.php'; ?>
```

### 3. В `turquoise/templates/footer.php` (перед `</body>`):
```php
<script src="https://cdn.jsdelivr.net/npm/canvas-confetti@1"></script>
<script src="/mascot_interactive/src/js/main.js" defer></script>
```

## 🎯 Возможности

- 5 эмоций: happy, sad, thinking, surprised, celebrate
- Голосовое озвучивание (Web Speech API)
- Всплывающие подсказки
- Анимации побед (конфетти, звёзды)
- Адаптивность (Desktop, Tablet, Mobile)

## 📝 Использование API

```javascript
// Показать сообщение
MarusyaAPI.say('Привет! 👋', 3000);

// Сменить настроение
MarusyaAPI.setMood('celebrate');

// Запустить празднование
MarusyaAPI.celebrate('full');

// Реакция на посещение района
MarusyaAPI.visitDistrict('Ливенский');

// События
document.dispatchEvent(new CustomEvent('districtComplete', {
    detail: { name: 'Ливенский', id: 12 }
}));
```
