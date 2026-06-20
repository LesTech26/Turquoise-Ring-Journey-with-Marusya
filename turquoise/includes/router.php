<?php
/**
 * router.php
 * Простой роутер: разбирает чистый URL (после mod_rewrite в front.php)
 * и подключает соответствующий PHP-файл.
 *
 * Поддерживает:
 *   /                       -> index.php
 *   /login /register / ...  -> login.php / register.php / ...
 *   /district/<slug>        -> district.php (slug кладётся в $_GET['slug'])
 *   /admin/...               -> admin/...
 *   /games/<name>            -> games/<name>.php
 */

class Router
{
    /** @var array<string,string> простые маршруты "путь" => "файл" */
    private array $staticRoutes = [
        ''                 => 'index.php',
        'index'            => 'index.php',
        'about'            => 'about.php',
        'contacts'         => 'contacts.php',
        'achievements'     => 'achievements.php',
        'media'            => 'media.php',
        'games'            => 'games.php',
        'login'            => 'login.php',
        'logout'           => 'logout.php',
        'register'         => 'register.php',
        'profile'          => 'profile.php',
        'forgot-password'  => 'forgot-password.php',
        'reset-password'   => 'reset-password.php',
    ];

    public function dispatch(): void
    {
        $uri  = parse_url($_SERVER['REQUEST_URI'] ?? '/', PHP_URL_PATH) ?? '/';
        $base = rtrim(BASE_URL, '/');

        if ($base !== '' && str_starts_with($uri, $base)) {
            $uri = substr($uri, strlen($base));
        }

        $uri = trim($uri, '/');
        $segments = $uri === '' ? [] : explode('/', $uri);

        // /admin/...
        if (($segments[0] ?? '') === 'admin') {
            $this->dispatchAdmin(array_slice($segments, 1));
            return;
        }

        // /district/<slug>
        if (($segments[0] ?? '') === 'district' && isset($segments[1])) {
            $_GET['slug'] = $segments[1];
            $this->load('district.php');
            return;
        }

        // /games/<name>
        if (($segments[0] ?? '') === 'games' && isset($segments[1])) {
            $file = 'games/' . basename($segments[1]) . '.php';
            if (file_exists(BASE_PATH . '/' . $file)) {
                $this->load($file);
                return;
            }
            $this->notFound();
            return;
        }

        $key = $segments[0] ?? '';
        if (isset($this->staticRoutes[$key])) {
            $this->load($this->staticRoutes[$key]);
            return;
        }

        $this->notFound();
    }

    private function dispatchAdmin(array $segments): void
    {
        $page = $segments[0] ?? 'index';
        $file = 'admin/' . basename($page) . '.php';

        if (file_exists(BASE_PATH . '/' . $file)) {
            $this->load($file);
        } else {
            $this->notFound();
        }
    }

    private function load(string $relativePath): void
    {
        require BASE_PATH . '/' . $relativePath;
    }

    private function notFound(): void
    {
        http_response_code(404);
        require BASE_PATH . '/404.php';
    }
}

$router = new Router();
