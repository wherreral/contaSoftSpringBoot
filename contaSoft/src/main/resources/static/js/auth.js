/**
 * auth.js - Módulo de autenticación compartido
 * Incluir en todas las páginas autenticadas ANTES de otros scripts.
 */
(function() {
    // Verificar token
    var token = localStorage.getItem('jwtToken');
    if (!token) {
        window.location.replace('/login');
        return;
    }

    // JWT Fetch Interceptor
    var originalFetch = window.fetch;
    window.fetch = function(url, options) {
        options = options || {};
        var tok = localStorage.getItem('jwtToken');
        if (tok) {
            options.headers = options.headers || {};
            if (options.headers instanceof Headers) {
                options.headers.set('Authorization', 'Bearer ' + tok);
            } else {
                options.headers['Authorization'] = 'Bearer ' + tok;
            }
        }
        return originalFetch(url, options).then(function(response) {
            if (response.status === 401 || response.status === 403) {
                clearSessionAndRedirect();
            }
            return response;
        });
    };

    // Logout
    window.logout = function() {
        if (confirm('\u00bfEst\u00e1 seguro que desea cerrar sesi\u00f3n?')) {
            fetch('/api/auth/logout', { method: 'POST' })
            .finally(function() {
                clearSessionAndRedirect();
            });
        }
    };

    // Clear session helper
    function clearSessionAndRedirect() {
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('username');
        localStorage.removeItem('familyId');
        window.location.replace('/login');
    }
    window.clearSessionAndRedirect = clearSessionAndRedirect;
})();
