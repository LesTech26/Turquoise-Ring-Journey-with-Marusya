<?php
require_once __DIR__ . '/includes/config.php';
require_once __DIR__ . '/includes/db.php';
require_once __DIR__ . '/includes/functions.php';
require_once __DIR__ . '/includes/auth.php';

requireLogin();

$user   = currentUser();
$userId = (int)$user['id'];
$errors = [];

// ------------------------------------------------------------------
// Обновление профиля
// ------------------------------------------------------------------
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (!csrf_verify()) {
        $errors[] = 'Ошибка безопасности. Повторите попытку.';
    } else {
        $action = $_POST['_action'] ?? 'update_profile';

        // --- Обновление имени / аватара ---
        if ($action === 'update_profile') {
            $username = trim($_POST['username'] ?? '');
            if (mb_strlen($username) < 2 || mb_strlen($username) > 50) {
                $errors[] = 'Имя: от 2 до 50 символов.';
            }

            $avatarPath = null;
            if (!empty($_FILES['avatar']['name'])) {
                $avatarPath = uploadFile($_FILES['avatar'], 'avatars');
                if (!$avatarPath) {
                    $errors[] = 'Ошибка загрузки аватара. Допустимы JPEG, PNG, WebP (до 10 МБ).';
                }
            }

            if (!$errors) {
                if ($avatarPath) {
                    db()->prepare('UPDATE users SET username = ?, avatar = ? WHERE id = ?')
                        ->execute([$username, $avatarPath, $userId]);
                } else {
                    db()->prepare('UPDATE users SET username = ? WHERE id = ?')
                        ->execute([$username, $userId]);
                }
                // Обновить сессию
                $_SESSION['user']['username'] = $username;
                if ($avatarPath) {
                    $_SESSION['user']['avatar'] = $avatarPath;
                }
                setFlash('success', 'Профиль обновлён.');
                redirect('/profile');
            }
        }

        // --- Смена пароля ---
        if ($action === 'change_password') {
            $currentPwd = $_POST['current_password'] ?? '';
            $newPwd     = $_POST['new_password'] ?? '';
            $confirmPwd = $_POST['confirm_password'] ?? '';

            $stmt = db()->prepare('SELECT password_hash FROM users WHERE id = ?');
            $stmt->execute([$userId]);
            $hash = $stmt->fetchColumn();

            if (!password_verify($currentPwd, $hash)) {
                $errors[] = 'Текущий пароль введён неверно.';
            } elseif (mb_strlen($newPwd) < 8) {
                $errors[] = 'Новый пароль должен содержать не менее 8 символов.';
            } elseif ($newPwd !== $confirmPwd) {
                $errors[] = 'Пароли не совпадают.';
            } else {
                $newHash = password_hash($newPwd, PASSWORD_BCRYPT, ['cost' => HASH_COST]);
                db()->prepare('UPDATE users SET password_hash = ? WHERE id = ?')
                    ->execute([$newHash, $userId]);
                setFlash('success', 'Пароль изменён.');
                redirect('/profile');
            }
        }

        // --- Сброс прогресса ---
        if ($action === 'reset_progress') {
            db()->prepare('DELETE FROM user_progress WHERE user_id = ?')->execute([$userId]);
            db()->prepare('DELETE FROM user_achievements WHERE user_id = ?')->execute([$userId]);
            setFlash('success', 'Прогресс сброшен.');
            redirect('/profile');
        }
    }
}

// ------------------------------------------------------------------
// Прогресс по районам
// ------------------------------------------------------------------
$progress = db()->prepare(
    'SELECT d.id, d.name, d.slug, up.is_completed, up.quiz_score, up.collected_costume, up.completed_at
     FROM districts d
     LEFT JOIN user_progress up ON d.id = up.district_id AND up.user_id = ?
     WHERE d.is_active = 1
     ORDER BY d.sort_order ASC'
);
$progress->execute([$userId]);
$districts = $progress->fetchAll();

$completedCount = array_sum(array_column($districts, 'is_completed'));

// ------------------------------------------------------------------
// Достижения
// ------------------------------------------------------------------
$achievements = db()->prepare(
    'SELECT achievement_type, achievement_value, earned_at
     FROM user_achievements WHERE user_id = ? ORDER BY earned_at DESC'
);
$achievements->execute([$userId]);
$achievements = $achievements->fetchAll();

// ------------------------------------------------------------------
// История игр
// ------------------------------------------------------------------
$gameHistory = db()->prepare(
    'SELECT gh.game_type, d.name AS district, gh.score, gh.played_at
     FROM game_history gh
     LEFT JOIN districts d ON d.id = gh.district_id
     WHERE gh.user_id = ?
     ORDER BY gh.played_at DESC
     LIMIT 20'
);
$gameHistory->execute([$userId]);
$gameHistory = $gameHistory->fetchAll();

// ------------------------------------------------------------------
// Шаблон
// ------------------------------------------------------------------
include __DIR__ . '/templates/header.php';
?>

<main class="profile">
    <h1>Личный кабинет</h1>

    <?php foreach (getFlash() as $f): ?>
        <div class="alert alert--<?= e($f['type']) ?>"><?= e($f['message']) ?></div>
    <?php endforeach; ?>

    <?php if ($errors): ?>
        <div class="alert alert--error">
            <?php foreach ($errors as $err): ?>
                <p><?= e($err) ?></p>
            <?php endforeach; ?>
        </div>
    <?php endif; ?>

    <!-- Данные профиля -->
    <section class="profile__info">
        <h2>Профиль</h2>
        <form method="POST" enctype="multipart/form-data">
            <?= csrf_field() ?>
            <input type="hidden" name="_action" value="update_profile">
            <label>Имя пользователя
                <input type="text" name="username" value="<?= e($user['username']) ?>" required>
            </label>
            <label>Аватар
                <input type="file" name="avatar" accept="image/*">
                <?php if (!empty($user['avatar'])): ?>
                    <img src="<?= e(UPLOAD_URL . $user['avatar']) ?>" height="60" alt="Аватар" class="avatar">
                <?php endif; ?>
            </label>
            <button type="submit" class="btn btn--primary">Сохранить</button>
        </form>

        <h3>Сменить пароль</h3>
        <form method="POST">
            <?= csrf_field() ?>
            <input type="hidden" name="_action" value="change_password">
            <label>Текущий пароль <input type="password" name="current_password" required></label>
            <label>Новый пароль   <input type="password" name="new_password" required></label>
            <label>Подтверждение  <input type="password" name="confirm_password" required></label>
            <button type="submit" class="btn">Изменить пароль</button>
        </form>
    </section>

    <!-- Прогресс по районам -->
    <section class="profile__progress">
        <h2>Прогресс: <?= $completedCount ?> / <?= count($districts) ?> районов</h2>
        <div class="progress-bar">
            <div class="progress-bar__fill" style="width:<?= count($districts) ? round($completedCount / count($districts) * 100) : 0 ?>%"></div>
        </div>
        <ul class="district-list">
            <?php foreach ($districts as $d): ?>
                <li class="district-list__item <?= $d['is_completed'] ? 'completed' : '' ?>">
                    <a href="<?= BASE_URL ?>/district/<?= e($d['slug']) ?>"><?= e($d['name']) ?></a>
                    <?php if ($d['is_completed']): ?>
                        <span class="badge badge--green">✓ Пройден</span>
                        <span>Счёт: <?= (int)$d['quiz_score'] ?></span>
                    <?php endif; ?>
                </li>
            <?php endforeach; ?>
        </ul>

        <form method="POST" onsubmit="return confirm('Сбросить весь прогресс? Это действие необратимо.')">
            <?= csrf_field() ?>
            <input type="hidden" name="_action" value="reset_progress">
            <button type="submit" class="btn btn--danger">Сбросить прогресс</button>
        </form>
    </section>

    <!-- Достижения -->
    <section class="profile__achievements">
        <h2>Достижения</h2>
        <?php if ($achievements): ?>
            <ul>
                <?php foreach ($achievements as $a): ?>
                    <li><?= e($a['achievement_type']) ?> — <?= e($a['achievement_value'] ?? '') ?> (<?= e($a['earned_at']) ?>)</li>
                <?php endforeach; ?>
            </ul>
        <?php else: ?>
            <p>Достижений пока нет. Изучайте районы!</p>
        <?php endif; ?>
    </section>

    <!-- История игр -->
    <section class="profile__history">
        <h2>История игр</h2>
        <?php if ($gameHistory): ?>
            <table>
                <thead><tr><th>Игра</th><th>Район</th><th>Счёт</th><th>Дата</th></tr></thead>
                <tbody>
                <?php foreach ($gameHistory as $g): ?>
                    <tr>
                        <td><?= e($g['game_type']) ?></td>
                        <td><?= e($g['district'] ?? '—') ?></td>
                        <td><?= (int)$g['score'] ?></td>
                        <td><?= e($g['played_at']) ?></td>
                    </tr>
                <?php endforeach; ?>
                </tbody>
            </table>
        <?php else: ?>
            <p>Вы ещё не играли.</p>
        <?php endif; ?>
    </section>
</main>

<?php include __DIR__ . '/templates/footer.php'; ?>
