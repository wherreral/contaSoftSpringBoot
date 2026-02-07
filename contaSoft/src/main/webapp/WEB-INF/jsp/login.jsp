<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ContaSoft - Login</title>
    
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    
    <style>
        :root {
            --primary-color: #667eea;
            --secondary-color: #764ba2;
        }
        
        body {
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        .login-container {
            max-width: 450px;
            width: 100%;
            padding: 20px;
        }
        
        .login-card {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            padding: 40px;
            animation: slideUp 0.5s ease-out;
        }
        
        @keyframes slideUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        .logo-container {
            text-align: center;
            margin-bottom: 30px;
        }
        
        .logo-icon {
            font-size: 60px;
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }
        
        .logo-text {
            font-size: 28px;
            font-weight: bold;
            color: #333;
            margin-top: 10px;
        }
        
        .form-label {
            font-weight: 600;
            color: #555;
            margin-bottom: 8px;
        }
        
        .form-control {
            border-radius: 10px;
            border: 2px solid #e0e0e0;
            padding: 12px 15px;
            transition: all 0.3s;
        }
        
        .form-control:focus {
            border-color: var(--primary-color);
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }
        
        .input-group-text {
            border-radius: 10px 0 0 10px;
            border: 2px solid #e0e0e0;
            border-right: none;
            background-color: #f8f9fa;
        }
        
        .input-group .form-control {
            border-left: none;
            border-radius: 0 10px 10px 0;
        }
        
        .btn-login {
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            border: none;
            border-radius: 10px;
            padding: 12px;
            font-size: 16px;
            font-weight: 600;
            color: white;
            width: 100%;
            margin-top: 20px;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        
        .btn-login:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
        }
        
        .btn-login:active {
            transform: translateY(0);
        }
        
        .register-link {
            text-align: center;
            margin-top: 20px;
            color: #666;
        }
        
        .register-link a {
            color: var(--primary-color);
            text-decoration: none;
            font-weight: 600;
            transition: color 0.3s;
        }
        
        .register-link a:hover {
            color: var(--secondary-color);
        }
        
        .alert {
            border-radius: 10px;
            animation: shake 0.5s;
        }
        
        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            25% { transform: translateX(-10px); }
            75% { transform: translateX(10px); }
        }
        
        .spinner-border-sm {
            width: 1rem;
            height: 1rem;
            border-width: 0.15em;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="login-card">
            <div class="logo-container">
                <i class="bi bi-clipboard-data logo-icon"></i>
                <div class="logo-text">ContaSoft</div>
                <p class="text-muted mt-2">Sistema de Gestión Contable</p>
            </div>
            
            <div id="alertContainer"></div>
            
            <form id="loginForm">
                <div class="mb-3">
                    <label for="username" class="form-label">Usuario</label>
                    <div class="input-group">
                        <span class="input-group-text">
                            <i class="bi bi-person"></i>
                        </span>
                        <input type="text" class="form-control" id="username" 
                               placeholder="Ingrese su usuario" required>
                    </div>
                </div>
                
                <div class="mb-3">
                    <label for="password" class="form-label">Contraseña</label>
                    <div class="input-group">
                        <span class="input-group-text">
                            <i class="bi bi-lock"></i>
                        </span>
                        <input type="password" class="form-control" id="password" 
                               placeholder="Ingrese su contraseña" required>
                    </div>
                </div>
                
                <button type="submit" class="btn btn-login" id="loginBtn">
                    <span id="btnText">Iniciar Sesión</span>
                    <span id="btnSpinner" class="d-none">
                        <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                        Iniciando sesión...
                    </span>
                </button>
            </form>
            
            <div class="register-link">
                ¿No tienes una cuenta? <a href="/register">Regístrate aquí</a>
            </div>
        </div>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        // Check if user is already logged in with a valid (non-expired) token
        window.addEventListener('DOMContentLoaded', function() {
            const token = localStorage.getItem('jwtToken');
            if (token) {
                try {
                    const payload = JSON.parse(atob(token.split('.')[1]));
                    const exp = payload.exp * 1000; // JWT exp is in seconds
                    if (exp > Date.now()) {
                        // Set cookie so server-side can read it, then redirect
                        const maxAge = Math.floor((exp - Date.now()) / 1000);
                        document.cookie = 'JWT_TOKEN=' + token + '; path=/; max-age=' + maxAge;
                        window.location.href = '/';
                        return;
                    }
                } catch(e) {
                    console.warn('Token inválido, limpiando...');
                }
                // Token expired or invalid, clean up
                localStorage.removeItem('jwtToken');
                localStorage.removeItem('username');
                localStorage.removeItem('familyId');
                var xhr = new XMLHttpRequest();
                xhr.open('POST', '/api/auth/logout', false);
                try { xhr.send(); } catch(e2) {}
            }
        });

        document.getElementById('loginForm').addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            const loginBtn = document.getElementById('loginBtn');
            const btnText = document.getElementById('btnText');
            const btnSpinner = document.getElementById('btnSpinner');
            const alertContainer = document.getElementById('alertContainer');
            
            alertContainer.innerHTML = '';
            
            loginBtn.disabled = true;
            btnText.classList.add('d-none');
            btnSpinner.classList.remove('d-none');
            
            try {
                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        username: username,
                        password: password
                    })
                });
                
                const authHeader = response.headers.get('Authorization');
                
                if (response.ok && authHeader) {
                    const token = authHeader.replace('Bearer ', '');
                    
                    localStorage.setItem('jwtToken', token);
                    
                    const tokenPayload = JSON.parse(atob(token.split('.')[1]));
                    localStorage.setItem('username', tokenPayload.name);
                    localStorage.setItem('familyId', tokenPayload.family);
                    
                    // Set cookie so browser navigations carry the token
                    const maxAge = Math.floor((tokenPayload.exp * 1000 - Date.now()) / 1000);
                    document.cookie = 'JWT_TOKEN=' + token + '; path=/; max-age=' + maxAge;
                    
                    alertContainer.innerHTML = `
                        <div class="alert alert-success" role="alert">
                            <i class="bi bi-check-circle-fill"></i> ¡Inicio de sesión exitoso! Redirigiendo...
                        </div>
                    `;
                    
                    setTimeout(() => {
                        window.location.href = '/';
                    }, 1000);
                    
                } else {
                    throw new Error('Credenciales inválidas');
                }
                
            } catch (error) {
                console.error('Error during login:', error);
                
                alertContainer.innerHTML = `
                    <div class="alert alert-danger" role="alert">
                        <i class="bi bi-exclamation-triangle-fill"></i> 
                        Error: Usuario o contraseña incorrectos
                    </div>
                `;
                
                loginBtn.disabled = false;
                btnText.classList.remove('d-none');
                btnSpinner.classList.add('d-none');
            }
        });
    </script>
</body>
</html>
