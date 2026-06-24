# Путешествие по бирюзовому кольцу России

Мобильное образовательное приложение для детей 7–14 лет по изучению культуры, истории и традиционных народных костюмов районов Орловской области.

---

## 📱 О проекте

**Платформа:** Android 8.0+ (API 26)  
**Язык разработки:** Java  
**Минимальная версия SDK:** API 26  
**Целевая версия SDK:** API 36  
**Архитектура:** MVVM (ViewModel + LiveData)  
**База данных:** Room (SQLite)  

---

## 📁 Полная структура проекта

```
biruse_kolco/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/biruse_kolco/
│   │   │   │   ├── data/
│   │   │   │   │   └── database/
│   │   │   │   │       ├── AppDatabase.java                  # Главный класс БД Room
│   │   │   │   │       ├── converters/
│   │   │   │   │       │   └── Converters.java               # Конвертеры для Room (Gson)
│   │   │   │   │       ├── dao/
│   │   │   │   │       │   ├── AchievementDao.java           # DAO для достижений
│   │   │   │   │       │   ├── CostumeDao.java               # DAO для костюмов
│   │   │   │   │       │   ├── DistrictDao.java              # DAO для районов
│   │   │   │   │       │   ├── GameStatDao.java              # DAO для статистики игр
│   │   │   │   │       │   ├── QuestionDao.java              # DAO для вопросов
│   │   │   │   │       │   └── UserDao.java                  # DAO для пользователя
│   │   │   │   │       └── entities/
│   │   │   │   │           ├── Achievement.java              # Модель достижения
│   │   │   │   │           ├── Costume.java                  # Модель костюма
│   │   │   │   │           ├── District.java                 # Модель района
│   │   │   │   │           ├── GameStat.java                 # Модель статистики игр
│   │   │   │   │           ├── Question.java                 # Модель вопроса
│   │   │   │   │           └── User.java                     # Модель пользователя
│   │   │   │   ├── model/
│   │   │   │   │   └── MainViewModel.java                    # ViewModel для главного экрана
│   │   │   │   ├── repository/
│   │   │   │   │   └── DataRepository.java                   # Репозиторий данных
│   │   │   │   ├── ui/
│   │   │   │   │   ├── splash/
│   │   │   │   │   │   └── SplashActivity.java               # Загрузочный экран
│   │   │   │   │   ├── main/
│   │   │   │   │   │   ├── MainActivity.java                 # Главная активность
│   │   │   │   │   │   └── MainFragment.java                 # Главный фрагмент
│   │   │   │   │   ├── settings/
│   │   │   │   │   │   └── SettingsFragment.java             # Настройки
│   │   │   │   │   ├── achievements/
│   │   │   │   │   │   ├── AchievementsFragment.java         # Достижения
│   │   │   │   │   │   └── AchievementAdapter.java           # Адаптер достижений
│   │   │   │   │   ├── district/
│   │   │   │   │   │   └── DistrictDetailFragment.java       # Экран района (заглушка)
│   │   │   │   │   └── navigation/                           # Навигация (ресурсы)
│   │   │   │   └── utils/
│   │   │   │       ├── Constants.java                        # Константы приложения
│   │   │   │       └── SharedPrefsManager.java               # Управление SharedPreferences
│   │   │   ├── res/
│   │   │   │   ├── anim/
│   │   │   │   │   ├── fade_in.xml                           # Анимация появления
│   │   │   │   │   ├── fade_in_items.xml                     # Анимация для списка
│   │   │   │   │   ├── fade_in_slow.xml                      # Медленное появление
│   │   │   │   │   ├── fade_in_stagger.xml                   # Каскадное появление
│   │   │   │   │   ├── scale_in.xml                          # Масштабирование с появлением
│   │   │   │   │   ├── slide_in_left.xml                     # Выезд слева
│   │   │   │   │   ├── slide_in_right.xml                    # Выезд справа
│   │   │   │   │   ├── slide_out_left.xml                    # Уезд влево
│   │   │   │   │   └── slide_out_right.xml                   # Уезд вправо
│   │   │   │   ├── color/
│   │   │   │   │   └── bottom_nav_selector.xml               # Цвета для нижней навигации
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── background_gradient.xml               # Градиентный фон
│   │   │   │   │   ├── background_splash.xml                 # Фон SplashScreen
│   │   │   │   │   ├── bg_achievement.xml                    # Фон для достижений
│   │   │   │   │   ├── bg_bottom_nav.xml                     # Фон нижней навигации
│   │   │   │   │   ├── bg_bottom_nav_item.xml                # Фон элемента навигации
│   │   │   │   │   ├── bg_card_white.xml                     # Белая карточка
│   │   │   │   │   ├── bg_continue_gradient.xml              # Градиент для кнопки
│   │   │   │   │   ├── bg_district_badge.xml                 # Бейдж района
│   │   │   │   │   ├── bg_dot.xml                            # Точка загрузки
│   │   │   │   │   ├── bg_gold_button.xml                    # Золотая кнопка
│   │   │   │   │   ├── bg_green_button.xml                   # Зеленая кнопка
│   │   │   │   │   ├── bg_header_gradient.xml                # Градиент шапки
│   │   │   │   │   ├── bg_icon_circle_blue.xml               # Синий круг для иконки
│   │   │   │   │   ├── bg_icon_circle_gold.xml               # Золотой круг для иконки
│   │   │   │   │   ├── bg_icon_circle_green.xml              # Зеленый круг для иконки
│   │   │   │   │   ├── bg_icon_circle_red.xml                # Красный круг для иконки
│   │   │   │   │   ├── bg_level.xml                          # Фон уровня
│   │   │   │   │   ├── bg_level_gold.xml                     # Золотой фон уровня
│   │   │   │   │   ├── bg_level_modern.xml                   # Современный фон уровня
│   │   │   │   │   ├── bg_progress_empty.xml                 # Пустой прогресс-бар
│   │   │   │   │   ├── bg_progress_fill.xml                  # Заполненный прогресс-бар
│   │   │   │   │   ├── bg_progress_modern_empty.xml          # Современный пустой
│   │   │   │   │   ├── bg_progress_modern_fill.xml           # Современный заполненный
│   │   │   │   │   ├── bg_text_container.xml                 # Фон для текста
│   │   │   │   │   ├── dot_animation.xml                     # Анимация точки
│   │   │   │   │   ├── gerb.png                              # Изображение герба
│   │   │   │   │   ├── ic_achievement.xml                    # Иконка достижения
│   │   │   │   │   ├── ic_achievements.xml                   # Иконка достижений
│   │   │   │   │   ├── ic_launcher_background.xml            # Фон иконки
│   │   │   │   │   ├── ic_launcher_foreground.xml            # Иконка приложения
│   │   │   │   │   ├── ic_logo.xml                           # Логотип
│   │   │   │   │   ├── ic_map.xml                            # Иконка карты
│   │   │   │   │   └── ic_settings.xml                       # Иконка настроек
│   │   │   │   ├── font/
│   │   │   │   │   ├── lobster.ttf                           # Шрифт Lobster
│   │   │   │   │   ├── lora.ttf                              # Шрифт Lora
│   │   │   │   │   ├── comfortaa.ttf                         # Шрифт Comfortaa
│   │   │   │   │   └── rubik.ttf                             # Шрифт Rubik
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml                     # Главная активность
│   │   │   │   │   ├── activity_splash.xml                   # SplashScreen
│   │   │   │   │   ├── fragment_achievements.xml             # Достижения
│   │   │   │   │   ├── fragment_district_detail.xml          # Экран района
│   │   │   │   │   ├── fragment_main.xml                     # Главный экран
│   │   │   │   │   ├── fragment_settings.xml                 # Настройки
│   │   │   │   │   └── item_achievement.xml                  # Элемент достижения
│   │   │   │   ├── menu/
│   │   │   │   │   └── bottom_nav_menu.xml                   # Нижнее меню
│   │   │   │   ├── mipmap-hdpi/
│   │   │   │   │   ├── ic_launcher.xml                       # Иконка hdpi
│   │   │   │   │   └── ic_launcher_round.xml                 # Круглая иконка hdpi
│   │   │   │   ├── mipmap-mdpi/
│   │   │   │   │   ├── ic_launcher.xml                       # Иконка mdpi
│   │   │   │   │   └── ic_launcher_round.xml                 # Круглая иконка mdpi
│   │   │   │   ├── mipmap-xhdpi/
│   │   │   │   │   ├── ic_launcher.xml                       # Иконка xhdpi
│   │   │   │   │   └── ic_launcher_round.xml                 # Круглая иконка xhdpi
│   │   │   │   ├── mipmap-xxhdpi/
│   │   │   │   │   ├── ic_launcher.xml                       # Иконка xxhdpi
│   │   │   │   │   └── ic_launcher_round.xml                 # Круглая иконка xxhdpi
│   │   │   │   ├── mipmap-xxxhdpi/
│   │   │   │   │   ├── ic_launcher.xml                       # Иконка xxxhdpi
│   │   │   │   │   └── ic_launcher_round.xml                 # Круглая иконка xxxhdpi
│   │   │   │   ├── navigation/
│   │   │   │   │   └── nav_graph.xml                         # Граф навигации
│   │   │   │   ├── values/
│   │   │   │   │   ├── arrays.xml                            # Массивы (языки)
│   │   │   │   │   ├── colors.xml                            # Цветовая палитра
│   │   │   │   │   ├── dimens.xml                            # Размеры
│   │   │   │   │   ├── strings.xml                           # Строковые ресурсы
│   │   │   │   │   ├── themes.xml                            # Темы
│   │   │   │   │   └── themes.xml (night)                    # Темы для ночного режима
│   │   │   │   └── xml/
│   │   │   │       ├── backup_rules.xml                      # Правила бэкапа
│   │   │   │       └── data_extraction_rules.xml             # Правила извлечения
│   │   │   └── AndroidManifest.xml                           # Манифест приложения
│   │   ├── androidTest/                                      # Android-тесты
│   │   └── test/                                             # Unit-тесты
│   ├── build.gradle.kts                                      # Сборка модуля app
│   └── proguard-rules.pro                                    # ProGuard правила
├── build.gradle.kts                                          # Сборка проекта
├── settings.gradle.kts                                       # Настройки проекта
├── gradle.properties                                         # Свойства Gradle
├── gradlew                                                   # Скрипт Gradle (Unix)
└── gradlew.bat                                               # Скрипт Gradle (Windows)
```

---

## 🎨 Цветовая палитра

| Цвет | HEX | Применение |
|------|-----|------------|
| Бирюзовый | `#17A2B8` | Основной цвет приложения |
| Золотой | `#FFD700` | Акцентный цвет (кнопки, награды) |
| Ореховый | `#8B7355` | Фоновые элементы |
| Красный | `#CC0000` | Элементы костюмов |
| Синий | `#1E3A8A` | Элементы костюмов |
| Бежевый | `#F5F0E8` | Фон страниц |
| Тёмный | `#2A2420` | Текст и заголовки |

---

## 🔤 Шрифты

| Шрифт | Применение |
|-------|------------|
| **Lobster** | Заголовки |
| **Lora** | Основной текст |
| **Comfortaa** | Кнопки и меню |
| **Rubik** | Интерфейсные элементы |

**Источники шрифтов:**
- [Lobster](https://fonts.google.com/specimen/Lobster)
- [Lora](https://fonts.google.com/specimen/Lora)
- [Comfortaa](https://fonts.google.com/specimen/Comfortaa)
- [Rubik](https://fonts.google.com/specimen/Rubik)

---

## 📦 Зависимости

```gradle
dependencies {
    implementation 'androidx.appcompat:appcompat:1.7.0'
    implementation 'com.google.android.material:material:1.12.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.navigation:navigation-fragment:2.7.7'
    implementation 'androidx.navigation:navigation-ui:2.7.7'
    implementation 'androidx.room:room-runtime:2.6.1'
    annotationProcessor 'androidx.room:room-compiler:2.6.1'
    implementation 'androidx.lifecycle:lifecycle-viewmodel:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-livedata:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-runtime:2.7.0'
    annotationProcessor 'androidx.lifecycle:lifecycle-compiler:2.7.0'
    implementation 'com.github.bumptech.glide:glide:4.16.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.16.0'
    implementation 'com.google.code.gson:gson:2.10.1'
}
```

---

## 📋 Описание ключевых файлов

### 🗄️ База данных (data/database/)

| Файл | Описание |
|------|----------|
| `AppDatabase.java` | Главный класс базы данных Room. Содержит все DAO и сущности. |
| `converters/Converters.java` | Конвертеры для работы с типами данных в Room (Gson). |
| `dao/UserDao.java` | Операции с пользователем (получение, обновление, очки). |
| `dao/DistrictDao.java` | Операции с районами (получение всех, завершение, прогресс). |
| `dao/CostumeDao.java` | Операции с костюмами (получение по району). |
| `dao/GameStatDao.java` | Операции со статистикой игр. |
| `dao/QuestionDao.java` | Операции с вопросами викторины. |
| `dao/AchievementDao.java` | Операции с достижениями (получение, разблокировка). |
| `entities/*.java` | Модели данных (User, District, Costume, GameStat, Question, Achievement). |

### 🧠 Модели и репозиторий (model/, repository/)

| Файл | Описание |
|------|----------|
| `MainViewModel.java` | ViewModel для главного экрана. Содержит логику загрузки данных, прогресса и достижений. |
| `DataRepository.java` | Репозиторий для работы с БД. Предоставляет единый интерфейс доступа к данным. |

### 🖥️ UI (ui/)

| Файл | Описание |
|------|----------|
| `splash/SplashActivity.java` | Загрузочный экран с анимацией. Переход в MainActivity через 3 секунды. |
| `main/MainActivity.java` | Главная активность с нижней навигацией (Navigation Component). |
| `main/MainFragment.java` | Главный экран с информацией о пользователе, прогрессе и кнопками быстрого доступа. Есть место для маскота (заглушка). |
| `settings/SettingsFragment.java` | Настройки (звук, язык, сброс прогресса). |
| `achievements/AchievementsFragment.java` | Список достижений с RecyclerView. |
| `achievements/AchievementAdapter.java` | Адаптер для списка достижений. |
| `district/DistrictDetailFragment.java` | Экран района (заглушка для разработчика 2). |

### 🛠️ Утилиты (utils/)

| Файл | Описание |
|------|----------|
| `Constants.java` | Константы приложения (имена игр, очки, достижения). |
| `SharedPrefsManager.java` | Управление SharedPreferences (звук, пользователь). |

---

## 🚀 Сборка и запуск

### Требования
- Android Studio Ladybug или новее
- Android SDK 36
- Gradle 8.5.0+

### Шаги
1. Клонируйте репозиторий
2. Откройте проект в Android Studio
3. Скачайте шрифты и поместите в `res/font/`
4. Нажмите `Sync Project with Gradle Files`
5. Запустите приложение на эмуляторе или устройстве

---

## 📝 Задачи для разработчиков

### 🔨 Разработчик 2 — Карта и контент (Задачи №8–14)

**Файлы для доработки:**

| Файл | Задача |
|------|--------|
| `ui/district/DistrictDetailFragment.java` | Реализовать полный экран района: история, факты, фотографии, временная лента |
| `model/MainViewModel.java` | Заменить тестовые данные на реальные с картинками и гербами |
| `res/layout/fragment_main.xml` | Добавить интерактивную карту Орловской области вместо кнопки "Карта" |
| `data/database/entities/District.java` | Добавить поля для фото, временной ленты, описания костюма |
| `data/database/dao/DistrictDao.java` | Добавить методы поиска и фильтрации районов |
| `res/values/strings.xml` | Добавить названия 24 районов с ударениями |
| `utils/Constants.java` | Добавить константы для районов |

**ТODO в коде:**
```java
// TODO: Для Разработчика 2 - добавить полное описание района
// TODO: Для Разработчика 2 - добавить полную историю района
// TODO: Для Разработчика 2 - добавить герб
// TODO: Для Разработчика 2 - добавить изображения
// TODO: Для Разработчика 2 - добавить интересные факты
// TODO: Для Разработчика 2 - добавить временную ленту
// TODO: Для Разработчика 2 - добавить описание костюма
```

---

### 🔨 Разработчик 3 — Маскот и интерактив (Задачи №15–21)

**Файлы для доработки:**

| Файл | Задача |
|------|--------|
| `res/layout/fragment_main.xml` | Заменить заглушку `card_mascot` на 3D-модель Маруси |
| `data/database/entities/Costume.java` | Добавить поля для 3D-моделей |
| `data/database/dao/CostumeDao.java` | Добавить методы для переодевания |
| `ui/main/MainFragment.java` | Добавить анимации и диалоги с Марусей |
| `ui/settings/SettingsFragment.java` | Добавить настройки для маскота |
| `res/drawable/` | Добавить 3D-модели (.glb/.fbx) |

**ТODO в коде:**
```java
// TODO: Для Разработчика 3 - добавить 3D модель Маруси
// TODO: Для Разработчика 3 - добавить звуки
// TODO: Для Разработчика 3 - добавить диалоговую систему
// TODO: Для Разработчика 3 - добавить методы для переодевания
```

---

### 🔨 Разработчик 4 — Игры и развлечения (Задачи №22–30)

**Файлы для доработки:**

| Файл | Задача |
|------|--------|
| `ui/main/MainFragment.java` | Добавить обработку клика по кнопке "Игры" |
| `data/database/entities/Question.java` | Добавить поля для вопросов викторины |
| `data/database/dao/GameStatDao.java` | Добавить методы для статистики игр |
| `data/database/dao/QuestionDao.java` | Добавить методы для викторины |
| `utils/Constants.java` | Добавить константы для игр |

**Новые файлы (создать):**

| Файл | Описание |
|------|----------|
| `ui/games/ViktorinaFragment.java` | Викторина по районам |
| `ui/games/KrossvordFragment.java` | Кроссворды |
| `ui/games/RaskaskaFragment.java` | Раскраски |
| `ui/games/GameMenuFragment.java` | Меню игр |

**ТODO в коде:**
```java
// TODO: Для Разработчика 4 - добавить игры
// TODO: Для Разработчика 4 - добавить методы для викторины
// TODO: Для Разработчика 4 - добавить методы для статистики игр
```

---

### 🔨 Разработчик 5 — Админ-панель (Задачи №31–38)

**Файлы для доработки:**

| Файл | Задача |
|------|--------|
| `ui/settings/SettingsFragment.java` | Добавить скрытый вход в админ-панель (5 нажатий на логотип) |
| `utils/Constants.java` | Добавить константы для админ-панели |

**Новые файлы (создать):**

| Файл | Описание |
|------|----------|
| `ui/admin/AdminActivity.java` | Админ-панель |
| `ui/admin/AdminFragment.java` | Фрагменты админки |
| `ui/admin/AdminDistrictAdapter.java` | Адаптер для управления районами |

**ТODO в коде:**
```java
// TODO: Для Разработчика 5 - добавить скрытый вход в админ-панель
// TODO: Для Разработчика 5 - добавить константы для админ-панели
```

---

### 🎨 3D-художник — 3D-моделирование (Задачи №39–45)

**Задачи:**

| № | Задача | Описание |
|---|--------|----------|
| 39 | Создание модели Маруси | 3D-модель в мультяшном стиле с русским народным колоритом |
| 40 | Текстурирование | Кокошник, сарафан, вышивка |
| 41 | Риггинг | Создание скелета для анимации |
| 42 | Анимации | 12+ анимаций (приветствие✅, радость✅, удивление, задумчивость, указание, танец✅, огорчение, кивок, отрицание, хлопки, прыжки✅, улыбка) |
| 43 | Экспорт моделей | В формате .glb/.fbx для Unity/Sceneform |
| 44 | Элементы костюмов | 40+ элементов костюмов для переодевания |
| 45 | NPC персонажи | 3–5 персонажей для мини-игр |

---

## 👥 Команда

| Роль | Ответственность |
|------|-----------------|
| **Разработчик 1** | Ядро, навигация, база данных, ViewModel |
| **Разработчик 2** | Карта, контент районов |
| **Разработчик 3** | Маскот, 3D-интерактив |
| **Разработчик 4** | Игры и развлечения |
| **Разработчик 5** | Админ-панель |
| **3D-художник** | 3D-моделирование |
