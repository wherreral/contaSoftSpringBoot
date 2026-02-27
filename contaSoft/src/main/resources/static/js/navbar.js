/**
 * navbar.js - Navbar compartido para todas las páginas autenticadas
 * Requiere: auth.js cargado previamente
 * Uso: loadNavbar('inicio') | loadNavbar('clientes') | etc.
 */
function loadNavbar(activeItem) {
    var items = [
        { key: 'inicio', label: 'Inicio', icon: 'bi-house-fill', href: '/' },
        { key: 'reportes', label: 'Reportes', icon: 'bi-file-earmark-text', href: '/reportes' },
        { key: 'configuracion', label: 'Configuraci\u00f3n', icon: 'bi-gear-fill', href: '/configuracion' }
    ];

    var navItemsHTML = '';
    for (var i = 0; i < items.length; i++) {
        var item = items[i];
        var isActive = (item.key === activeItem) ? ' active' : '';
        navItemsHTML +=
            '<li class="nav-item">' +
                '<a class="nav-link' + isActive + '" href="' + item.href + '">' +
                    '<i class="bi ' + item.icon + ' me-1"></i> ' + item.label +
                '</a>' +
            '</li>';
    }

    var navbarHTML =
        '<nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-4">' +
            '<div class="container-fluid">' +
                '<button class="navbar-menu-btn" id="navbarMenuBtn">' +
                    '<i class="bi bi-list"></i>' +
                '</button>' +
                '<a class="navbar-brand" href="/">' +
                    '<i class="bi bi-calculator-fill me-2"></i>' +
                    '<strong>ContaSoft</strong>' +
                '</a>' +
                '<button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">' +
                    '<span class="navbar-toggler-icon"></span>' +
                '</button>' +
                '<div class="collapse navbar-collapse" id="navbarNav">' +
                    '<ul class="navbar-nav ms-auto">' +
                        navItemsHTML +
                        '<li class="nav-item">' +
                            '<button class="theme-toggle" id="themeToggle" title="Cambiar tema">' +
                                '<i class="bi bi-moon-fill" id="themeIcon"></i>' +
                            '</button>' +
                        '</li>' +
                    '</ul>' +
                '</div>' +
            '</div>' +
        '</nav>';

    document.getElementById('navbar-container').innerHTML = navbarHTML;

    // User info
    _addUserInfo();

    // Theme toggle
    _initThemeToggle();
}

function _addUserInfo() {
    var username = (localStorage.getItem('username') || '').trim();
    if (!username) {
        try {
            var token = localStorage.getItem('jwtToken');
            if (token) {
                var payload = JSON.parse(atob(token.split('.')[1] || ''));
                username = ((payload.name || payload.sub || '') + '').trim();
                if (username) localStorage.setItem('username', username);
            }
        } catch (e) {}
    }
    username = username || 'Usuario';

    var navbar = document.querySelector('.navbar .container-fluid');
    if (navbar && !document.getElementById('userInfo')) {
        var userInfo = document.createElement('div');
        userInfo.id = 'userInfo';
        userInfo.className = 'd-flex align-items-center ms-auto';
        userInfo.innerHTML =
            '<span class="text-white me-3">' +
                '<i class="bi bi-person-circle"></i> ' + username +
            '</span>' +
            '<button class="btn btn-sm btn-outline-light" onclick="logout()">' +
                '<i class="bi bi-box-arrow-right"></i> Salir' +
            '</button>';
        navbar.appendChild(userInfo);
    }
}

function _initThemeToggle() {
    var themeToggle = document.getElementById('themeToggle');
    var themeIcon = document.getElementById('themeIcon');
    var html = document.documentElement;

    var savedTheme = localStorage.getItem('theme') || 'light';
    html.setAttribute('data-theme', savedTheme);
    _applyThemeVariables(savedTheme);
    _updateThemeIcon(themeIcon, savedTheme);

    themeToggle.addEventListener('click', function() {
        var currentTheme = html.getAttribute('data-theme');
        var newTheme = currentTheme === 'dark' ? 'light' : 'dark';
        html.setAttribute('data-theme', newTheme);
        localStorage.setItem('theme', newTheme);
        _applyThemeVariables(newTheme);
        _updateThemeIcon(themeIcon, newTheme);
    });
}

function _updateThemeIcon(iconEl, theme) {
    if (theme === 'dark') {
        iconEl.className = 'bi bi-sun-fill';
    } else {
        iconEl.className = 'bi bi-moon-fill';
    }
}

function _applyThemeVariables(theme) {
    var vars = {
        light: {
            '--primary-color': '#667eea',
            '--secondary-color': '#764ba2',
            '--success-color': '#198754',
            '--bg-color': '#f8f9fa',
            '--bg-card': '#ffffff',
            '--text-color': '#212529',
            '--text-muted': '#6c757d',
            '--border-color': '#dee2e6',
            '--navbar-bg': '#0d6efd',
            '--input-bg': '#ffffff',
            '--collapse-bg': '#f8f9fa',
            '--info-badge-bg': '#e7f1ff',
            '--info-badge-color': '#0d6efd'
        },
        dark: {
            '--primary-color': '#667eea',
            '--secondary-color': '#764ba2',
            '--success-color': '#198754',
            '--bg-color': '#1a1a2e',
            '--bg-card': '#16213e',
            '--text-color': '#e4e6eb',
            '--text-muted': '#b0b3b8',
            '--border-color': '#3a3a5c',
            '--navbar-bg': '#0f3460',
            '--input-bg': '#1f2940',
            '--collapse-bg': '#1f2940',
            '--info-badge-bg': '#0f2a5a',
            '--info-badge-color': '#a3b3ff'
        }
    };

    var root = document.documentElement;
    var map = vars[theme] || vars.light;
    Object.keys(map).forEach(function(k) {
        try { root.style.setProperty(k, map[k]); } catch (e) {}
    });
}
