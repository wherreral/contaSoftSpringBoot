<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ContaSoft - Gestión de Usuarios</title>

    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

    <link rel="stylesheet" href="/css/theme.css">

    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        .navbar {
            box-shadow: 0 2px 4px rgba(0,0,0,.1);
        }

        .sidebar {
            position: fixed;
            top: 0;
            left: -280px;
            width: var(--sidebar-width);
            height: 100vh;
            background: var(--sidebar-bg);
            box-shadow: 2px 0 10px rgba(0,0,0,0.1);
            transition: left 0.3s ease;
            z-index: 1050;
            overflow-y: auto;
        }

        .sidebar.active {
            left: 0;
        }

        .sidebar-header {
            padding: 1.5rem;
            background: rgba(0,0,0,0.2);
            color: white;
        }

        .sidebar-header h4 {
            margin: 0;
            font-weight: bold;
        }

        .sidebar-menu {
            padding: 1rem 0;
        }

        .sidebar-menu-item {
            display: block;
            padding: 1rem 1.5rem;
            color: rgba(255,255,255,0.9);
            text-decoration: none;
            transition: all 0.2s;
            border-left: 4px solid transparent;
        }

        .sidebar-menu-item:hover, .sidebar-menu-item.active {
            background: rgba(255,255,255,0.1);
            color: white;
            border-left-color: white;
            padding-left: 2rem;
        }

        .sidebar-menu-item i {
            width: 25px;
            margin-right: 10px;
        }

        .sidebar-section-title {
            padding: 1rem 1.5rem 0.5rem;
            color: rgba(255,255,255,0.6);
            font-size: 0.75rem;
            font-weight: bold;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .sidebar-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.5);
            z-index: 1040;
            display: none;
        }

        .sidebar-overlay.active {
            display: block;
        }

        .sidebar-close {
            position: absolute;
            top: 1.5rem;
            right: 1.5rem;
            background: rgba(255,255,255,0.2);
            border: none;
            color: white;
            width: 32px;
            height: 32px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: background 0.2s;
        }

        .sidebar-close:hover {
            background: rgba(255,255,255,0.3);
        }

        .navbar-menu-btn {
            background: none;
            border: none;
            color: white;
            font-size: 1.5rem;
            cursor: pointer;
            padding: 0.5rem;
            margin-right: 1rem;
            transition: transform 0.2s;
        }

        .navbar-menu-btn:hover {
            transform: scale(1.1);
        }

        .page-header {
            background: transparent;
            padding: 0.75rem 0;
            margin-bottom: 1.5rem;
            border-bottom: 1px solid #e9ecef;
        }

        .user-table-card {
            background: var(--bg-card);
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0,0,0,.08);
            overflow: hidden;
        }

        .table-header {
            background: var(--table-header-bg);
            color: var(--bg-card);
            padding: 1.5rem;
        }

        .btn-action {
            padding: 0.375rem 0.75rem;
            font-size: 0.875rem;
            margin: 0 0.25rem;
        }

        .modal-header {
            background: var(--modal-header-bg);
            color: var(--bg-card);
            padding: 0.75rem 1rem;
        }

        .modal-header .btn-close {
            filter: brightness(0) invert(1);
        }

        .modal-header .modal-title {
            font-size: 1rem;
        }

        .modal-body {
            padding: 1rem;
        }

        .role-badge-admin {
            background-color: #dc3545;
            color: white;
        }

        .role-badge-user {
            background-color: #0d6efd;
            color: white;
        }
    </style>
</head>
<body>
    <!-- Sidebar -->
    <div id="sidebar-container"></div>

    <!-- Navbar -->
    <div id="navbar-container"></div>

    <div class="container-fluid px-4">
        <!-- Page Header -->
        <div class="page-header">
            <div class="row align-items-center">
                <div class="col-md-6">
                    <h5 class="mb-0 text-secondary fw-normal">
                        <i class="bi bi-person-gear me-2"></i>
                        Gesti&oacute;n de Usuarios
                    </h5>
                </div>
                <div class="col-md-6 text-end">
                    <button class="btn btn-primary" id="btnNuevoUsuario">
                        <i class="bi bi-person-plus me-2"></i>
                        Nuevo Usuario
                    </button>
                </div>
            </div>
        </div>

        <!-- Alert Container -->
        <div id="alertContainer"></div>

        <!-- Users Table -->
        <div class="user-table-card">
            <div class="table-header d-flex justify-content-between align-items-center">
                <div>
                    <h6 class="mb-0"><i class="bi bi-people me-2"></i>Usuarios del Tenant</h6>
                </div>
                <span class="badge bg-light text-dark" id="userCount">0 usuarios</span>
            </div>
            <div class="table-responsive">
                <table class="table table-hover mb-0" id="usersTable">
                    <thead>
                        <tr>
                            <th>Usuario</th>
                            <th>Nombre</th>
                            <th>Tel&eacute;fono</th>
                            <th>Rol</th>
                            <th>Fecha Creaci&oacute;n</th>
                            <th class="text-center">Acciones</th>
                        </tr>
                    </thead>
                    <tbody id="usersTableBody">
                        <tr>
                            <td colspan="6" class="text-center py-4 text-muted">
                                <div class="spinner-border spinner-border-sm me-2" role="status"></div>
                                Cargando usuarios...
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Modal: Nuevo Usuario -->
    <div class="modal fade" id="nuevoUsuarioModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title"><i class="bi bi-person-plus me-2"></i>Nuevo Usuario</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <form id="nuevoUsuarioForm">
                        <div class="mb-3">
                            <label for="nuevoUsername" class="form-label">Usuario (email o login)</label>
                            <input type="text" class="form-control" id="nuevoUsername" required
                                   placeholder="ej: juan@empresa.cl" minlength="4" maxlength="100">
                            <div class="form-text">M&iacute;nimo 4 caracteres. Solo letras, n&uacute;meros, @, ., - y _</div>
                        </div>
                        <div class="mb-3">
                            <label for="nuevoPassword" class="form-label">Contrase&ntilde;a</label>
                            <input type="password" class="form-control" id="nuevoPassword" required minlength="6">
                            <div class="form-text">M&iacute;nimo 6 caracteres</div>
                        </div>
                        <div class="mb-3">
                            <label for="nuevoNombre" class="form-label">Nombre completo</label>
                            <input type="text" class="form-control" id="nuevoNombre" placeholder="Juan P&eacute;rez">
                        </div>
                        <div class="mb-3">
                            <label for="nuevoTelefono" class="form-label">Tel&eacute;fono</label>
                            <input type="text" class="form-control" id="nuevoTelefono" placeholder="+56 9 1234 5678">
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="button" class="btn btn-primary" id="btnGuardarUsuario">
                        <i class="bi bi-check-circle me-1"></i>Crear Usuario
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal: Reset Password -->
    <div class="modal fade" id="resetPasswordModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title"><i class="bi bi-key me-2"></i>Resetear Contrase&ntilde;a</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <p>Resetear contrase&ntilde;a para: <strong id="resetUsername"></strong></p>
                    <div class="mb-3">
                        <label for="newPassword" class="form-label">Nueva contrase&ntilde;a</label>
                        <input type="password" class="form-control" id="newPassword" required minlength="6">
                        <div class="form-text">M&iacute;nimo 6 caracteres</div>
                    </div>
                    <input type="hidden" id="resetUserId">
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="button" class="btn btn-warning" id="btnResetPassword">
                        <i class="bi bi-key me-1"></i>Resetear
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap 5 JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

    <!-- Shared modules -->
    <script src="/js/auth.js"></script>
    <script src="/js/sidebar.js"></script>
    <script src="/js/navbar.js"></script>
    <script>
        loadNavbar('usuarios');
        loadSidebar('usuarios');
    </script>

    <script>
        const API_URL = '/api/ui/usuarios';
        let nuevoUsuarioModal;
        let resetPasswordModal;

        document.addEventListener('DOMContentLoaded', function() {
            nuevoUsuarioModal = new bootstrap.Modal(document.getElementById('nuevoUsuarioModal'));
            resetPasswordModal = new bootstrap.Modal(document.getElementById('resetPasswordModal'));

            document.getElementById('btnNuevoUsuario').addEventListener('click', function() {
                document.getElementById('nuevoUsuarioForm').reset();
                nuevoUsuarioModal.show();
            });

            document.getElementById('btnGuardarUsuario').addEventListener('click', crearUsuario);
            document.getElementById('btnResetPassword').addEventListener('click', resetPassword);

            loadUsers();
        });

        function getHeaders() {
            const token = localStorage.getItem('jwtToken');
            return {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            };
        }

        function showAlert(message, type) {
            const container = document.getElementById('alertContainer');
            container.innerHTML = '<div class="alert alert-' + type + ' alert-dismissible fade show" role="alert">' +
                message +
                '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>' +
                '</div>';
            setTimeout(function() { container.innerHTML = ''; }, 5000);
        }

        async function loadUsers() {
            try {
                const response = await fetch(API_URL, { headers: getHeaders() });

                if (response.status === 403) {
                    showAlert('No tienes permisos para ver esta pagina', 'danger');
                    return;
                }

                const data = await response.json();
                if (data.success) {
                    renderUsers(data.data);
                    document.getElementById('userCount').textContent = data.total + ' usuarios';
                } else {
                    showAlert(data.message || 'Error al cargar usuarios', 'danger');
                }
            } catch (error) {
                console.error('Error loading users:', error);
                showAlert('Error de conexion al cargar usuarios', 'danger');
            }
        }

        function renderUsers(users) {
            const tbody = document.getElementById('usersTableBody');
            if (!users || users.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" class="text-center py-4 text-muted">No hay usuarios registrados</td></tr>';
                return;
            }

            tbody.innerHTML = users.map(function(user) {
                const roleBadge = user.role === 1
                    ? '<span class="badge role-badge-admin">Admin</span>'
                    : '<span class="badge role-badge-user">Usuario</span>';

                const fecha = user.created ? new Date(user.created).toLocaleDateString('es-CL') : '-';

                const isAdmin = user.role === 1;
                const actions = isAdmin
                    ? '<span class="text-muted small">-</span>'
                    : '<button class="btn btn-sm btn-outline-warning btn-action" onclick="showResetPassword(' + user.id + ', \'' + escapeHtml(user.username) + '\')">' +
                      '<i class="bi bi-key"></i></button>' +
                      '<button class="btn btn-sm btn-outline-danger btn-action" onclick="confirmDelete(' + user.id + ', \'' + escapeHtml(user.username) + '\')">' +
                      '<i class="bi bi-trash"></i></button>';

                return '<tr>' +
                    '<td>' + escapeHtml(user.username) + '</td>' +
                    '<td>' + escapeHtml(user.name || '-') + '</td>' +
                    '<td>' + escapeHtml(user.phone || '-') + '</td>' +
                    '<td>' + roleBadge + '</td>' +
                    '<td>' + fecha + '</td>' +
                    '<td class="text-center">' + actions + '</td>' +
                    '</tr>';
            }).join('');
        }

        function escapeHtml(text) {
            if (!text) return '';
            var div = document.createElement('div');
            div.appendChild(document.createTextNode(text));
            return div.innerHTML;
        }

        async function crearUsuario() {
            const username = document.getElementById('nuevoUsername').value.trim();
            const password = document.getElementById('nuevoPassword').value;
            const name = document.getElementById('nuevoNombre').value.trim();
            const phone = document.getElementById('nuevoTelefono').value.trim();

            if (!username || !password) {
                showAlert('Usuario y contrasena son obligatorios', 'warning');
                return;
            }

            try {
                const response = await fetch(API_URL, {
                    method: 'POST',
                    headers: getHeaders(),
                    body: JSON.stringify({ username: username, password: password, name: name, phone: phone })
                });

                const data = await response.json();
                if (data.success) {
                    nuevoUsuarioModal.hide();
                    showAlert('Usuario creado exitosamente', 'success');
                    loadUsers();
                } else {
                    showAlert(data.message || 'Error al crear usuario', 'danger');
                }
            } catch (error) {
                console.error('Error creating user:', error);
                showAlert('Error de conexion', 'danger');
            }
        }

        function showResetPassword(userId, username) {
            document.getElementById('resetUserId').value = userId;
            document.getElementById('resetUsername').textContent = username;
            document.getElementById('newPassword').value = '';
            resetPasswordModal.show();
        }

        async function resetPassword() {
            const userId = document.getElementById('resetUserId').value;
            const newPassword = document.getElementById('newPassword').value;

            if (!newPassword || newPassword.length < 6) {
                showAlert('La contrasena debe tener al menos 6 caracteres', 'warning');
                return;
            }

            try {
                const response = await fetch(API_URL + '/' + userId + '/reset-password', {
                    method: 'PUT',
                    headers: getHeaders(),
                    body: JSON.stringify({ newPassword: newPassword })
                });

                const data = await response.json();
                if (data.success) {
                    resetPasswordModal.hide();
                    showAlert('Contrasena actualizada exitosamente', 'success');
                } else {
                    showAlert(data.message || 'Error al resetear contrasena', 'danger');
                }
            } catch (error) {
                console.error('Error resetting password:', error);
                showAlert('Error de conexion', 'danger');
            }
        }

        function confirmDelete(userId, username) {
            if (confirm('Estas seguro de eliminar al usuario "' + username + '"? Esta accion no se puede deshacer.')) {
                deleteUser(userId);
            }
        }

        async function deleteUser(userId) {
            try {
                const response = await fetch(API_URL + '/' + userId, {
                    method: 'DELETE',
                    headers: getHeaders()
                });

                const data = await response.json();
                if (data.success) {
                    showAlert('Usuario eliminado exitosamente', 'success');
                    loadUsers();
                } else {
                    showAlert(data.message || 'Error al eliminar usuario', 'danger');
                }
            } catch (error) {
                console.error('Error deleting user:', error);
                showAlert('Error de conexion', 'danger');
            }
        }
    </script>
</body>
</html>
