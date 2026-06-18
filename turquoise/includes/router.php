<?php
/**
 * Маршрутизатор (ЧПУ-ссылки)
 *
 * Подключается в index.php или в точке входа.
 * .htaccess перенаправляет все запросы на index.php.
 */

class Router
{
    private array $routes = [];

    // ------------------------------------------------------------------
    // Регистрация маршрутов
    // ------------------------------------------------------------------

    public function get(string $pattern, callable $handler): void
    {
        $this->routes[] = ['GET', $pattern, $handler];
    }

    public function post(string $pattern, callable $handler): void
    {
        $this->routes[] = ['POST', $pattern, $handler];
    }

    public function any(string $pattern, callable $handler): void
    {
        $this->routes[] = ['ANY', $pattern, $handler];
    }

    // ------------------------------------------------------------------
    // Диспетчеризация
    // ------------------------------------------------------------------

    public function dispatch(): void
    {
        $method = $_SERVER['REQUEST_METHOD'];
        $uri    = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
        $basePath = parse_url(BASE_URL, PHP_URL_PATH); // '/turquoise'
        $uri = '/' . trim($uri, '/');
        $uri = '/' . trim(substr($uri, strlen($basePath)), '/');
        if ($uri === '//') $uri = '/';

        foreach ($this->routes as [$routeMethod, $pattern, $handler]) {
            if ($routeMethod !== 'ANY' && $routeMethod !== $method) {
                continue;
            }

            $regex  = $this->patternToRegex($pattern);
            if (preg_match($regex, $uri, $matches)) {
                // Извлечь именованные параметры
                $params = array_filter(
                    $matches,
                    fn($k) => !is_int($k),
                    ARRAY_FILTER_USE_KEY
                );
                call_user_func($handler, $params);
                return;
            }
        }

        // 404
        $this->notFound();
    }

    /**
     * Конвертация шаблона маршрута в регулярное выражение
     * Пример: /district/{slug}  →  /district/(?P<slug>[a-z0-9-]+)
     */
    private function patternToRegex(string $pattern): string
    {
        $regex = preg_replace_callback('/\{(\w+)\}/', function ($m) {
            return '(?P<' . $m[1] . '>[a-zA-Z0-9_-]+)';
        }, $pattern);

        // Экранируем точку для front.php
        $regex = str_replace('.', '\.', $regex);

        return '#^' . $regex . '$#u';
    }

    private function notFound(): void
    {
        http_response_code(404);
        // Показать шаблон 404 если есть, иначе — заглушка
        $tpl = BASE_PATH . '/templates/404.php';
        if (file_exists($tpl)) {
            include $tpl;
        } else {
            echo '<h1>404 — Страница не найдена</h1>';
        }
    }
}

// ------------------------------------------------------------------
// Точка входа: регистрация всех маршрутов
// ------------------------------------------------------------------

$router = new Router();

// Публичные страницы
$router->get('/',          fn() => include BASE_PATH . '/index.php');
$router->get('/front.php', fn() => include BASE_PATH . '/index.php');
$router->get('/district/{slug}', function ($p) {
    $_GET['slug'] = $p['slug'];
    include BASE_PATH . '/district.php';
});
$router->get('/games',          fn() => include BASE_PATH . '/games.php');
$router->get('/achievements',   fn() => include BASE_PATH . '/achievements.php');
$router->get('/media',          fn() => include BASE_PATH . '/media.php');
$router->get('/about',          fn() => include BASE_PATH . '/about.php');
$router->get('/contacts',       fn() => include BASE_PATH . '/contacts.php');

// Аутентификация
$router->any('/login',           fn() => include BASE_PATH . '/login.php');
$router->any('/register',        fn() => include BASE_PATH . '/register.php');
$router->any('/logout',          fn() => include BASE_PATH . '/logout.php');
$router->any('/forgot-password', fn() => include BASE_PATH . '/forgot-password.php');
$router->any('/reset-password',  fn() => include BASE_PATH . '/reset-password.php');

// Личный кабинет
$router->any('/profile',         fn() => include BASE_PATH . '/profile.php');

// API
$router->post('/api/save-progress', fn() => include BASE_PATH . '/api/save-progress.php');
$router->post('/api/quiz-results',  fn() => include BASE_PATH . '/api/quiz-results.php');
$router->post('/api/costume-save',  fn() => include BASE_PATH . '/api/costume-save.php');
$router->get('/api/progress',       fn() => include BASE_PATH . '/api/progress.php');

// Админ-панель
$router->any('/admin',              fn() => include BASE_PATH . '/admin/index.php');
$router->any('/admin/districts',    fn() => include BASE_PATH . '/admin/districts.php');
$router->any('/admin/content',      fn() => include BASE_PATH . '/admin/content.php');
$router->any('/admin/quiz',         fn() => include BASE_PATH . '/admin/quiz.php');
$router->any('/admin/costume',      fn() => include BASE_PATH . '/admin/costume.php');
$router->any('/admin/users',        fn() => include BASE_PATH . '/admin/users.php');
$router->any('/admin/stats',        fn() => include BASE_PATH . '/admin/stats.php');

return $router;
