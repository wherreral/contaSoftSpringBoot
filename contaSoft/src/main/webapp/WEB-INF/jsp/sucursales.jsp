<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ContaSoft - Gestión de Sucursales</title>
    
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    
    <style>
        :root {
            --primary-color: #667eea;
            --secondary-color: #764ba2;
            --success-color: #198754;
            --sidebar-width: 280px;
            
            /* Light Theme */
            --bg-color: #f8f9fa;
            --bg-card: #ffffff;
            --text-color: #212529;
            --text-muted: #6c757d;
            --border-color: #dee2e6;
            --navbar-bg: #0d6efd;
            --input-bg: #ffffff;
            --shadow-color: rgba(0,0,0,0.08);
        }
        
        [data-theme="dark"] {
            --bg-color: #1a1a2e;
            --bg-card: #16213e;
            --text-color: #e4e6eb;
            --text-muted: #b0b3b8;
            --border-color: #3a3a5c;
            --navbar-bg: #0f3460;
            --input-bg: #1f2940;
            --shadow-color: rgba(0,0,0,0.3);
        }
        
        body {
            background-color: var(--bg-color);
            color: var(--text-color);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            transition: background-color 0.3s, color 0.3s;
        }
        
        .navbar {
            box-shadow: 0 2px 4px var(--shadow-color);
            background-color: var(--navbar-bg) !important;
        }
        
        /* Sidebar Styles */
        .sidebar {
            position: fixed;
            top: 0;
            left: -280px;
            width: var(--sidebar-width);
            height: 100vh;
            background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
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
        
        .sidebar-menu-item:hover {
            background: rgba(255,255,255,0.1);
            color: white;
            border-left-color: white;
            padding-left: 2rem;
        }
        
        .sidebar-menu-item i {
            width: 25px;
            margin-right: 10px;
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
        
        .navbar-menu-btn {
            background: none;
            border: none;
            color: white;
            font-size: 1.5rem;
            cursor: pointer;
            padding: 0.5rem;
            margin-right: 1rem;
        }
        
        .card {
            background-color: var(--bg-card);
            border: 1px solid var(--border-color);
            box-shadow: 0 2px 8px var(--shadow-color);
            margin-bottom: 1.5rem;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        
        .card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px var(--shadow-color);
        }
        
        .btn-gradient {
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
            border: none;
            color: white;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        
        .btn-gradient:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
            color: white;
        }
        
        .table {
            color: var(--text-color);
        }
        
        .table thead {
            background-color: var(--bg-card);
            border-bottom: 2px solid var(--border-color);
        }
        
        .form-control, .form-select {
            background-color: var(--input-bg);
            color: var(--text-color);
            border: 1px solid var(--border-color);
        }
        
        .form-control:focus, .form-select:focus {
            background-color: var(--input-bg);
            color: var(--text-color);
            border-color: var(--primary-color);
        }
        
        .modal-content {
            background-color: var(--bg-card);
            color: var(--text-color);
        }
        
        .badge-custom {
            padding: 0.5em 0.8em;
            font-size: 0.85rem;
        }
        
        .theme-toggle {
            background: none;
            border: none;
            color: white;
            font-size: 1.2rem;
            cursor: pointer;
            padding: 0.5rem;
            margin-left: 1rem;
            transition: transform 0.2s;
        }
        
        .theme-toggle:hover {
            transform: scale(1.1);
        }
    </style>
</head>
<body>
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark">
        <div class="container-fluid">
            <button class="navbar-menu-btn" id="navbarMenuBtn">
                <i class="bi bi-list"></i>
            </button>
            <a class="navbar-brand fw-bold" href="/">
                <i class="bi bi-building me-2"></i>ContaSoft
            </a>
            <div class="ms-auto d-flex align-items-center">
                <button class="theme-toggle" id="themeToggle" title="Cambiar tema">
                    <i class="bi bi-sun-fill" id="themeIcon"></i>
                </button>
                <span class="navbar-text text-white ms-3">
                    <i class="bi bi-person-circle me-1"></i>${pageContext.request.userPrincipal.name}
                </span>
            </div>
        </div>
    </nav>

    <!-- Sidebar -->
    <div class="sidebar" id="sidebar">
        <div class="sidebar-header">
            <h4><i class="bi bi-building"></i> ContaSoft</h4>
            <p class="mb-0 small">Sistema de Gestión</p>
        </div>
        <div class="sidebar-menu">
            <a href="/" class="sidebar-menu-item">
                <i class="bi bi-house-door"></i>Inicio
            </a>
            <a href="/clientes" class="sidebar-menu-item">
                <i class="bi bi-people"></i>Clientes
            </a>
            <a href="/sucursales" class="sidebar-menu-item">
                <i class="bi bi-shop"></i>Sucursales
            </a>
            <a href="/templates" class="sidebar-menu-item">
                <i class="bi bi-file-earmark-spreadsheet"></i>Templates
            </a>
        </div>
    </div>
    <div class="sidebar-overlay" id="sidebarOverlay"></div>

    <!-- Main Content -->
    <div class="container-fluid py-4">
        <div class="row mb-4">
            <div class="col">
                <h2 class="mb-0"><i class="bi bi-shop me-2"></i>Gestión de Sucursales</h2>
                <p class="text-muted">Administra los centros de costo de tus clientes</p>
            </div>
            <div class="col-auto">
                <button class="btn btn-gradient" data-bs-toggle="modal" data-bs-target="#subsidiaryModal" onclick="resetForm()">
                    <i class="bi bi-plus-circle me-2"></i>Nueva Sucursal
                </button>
            </div>
        </div>

        <!-- Subsidiaries Table -->
        <div class="card">
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>ID Sucursal</th>
                                <th>Nombre</th>
                                <th>Nickname</th>
                                <th>Dirección</th>
                                <th>Cliente (RUT)</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody id="subsidiariesTableBody">
                            <c:forEach items="${subsidiaries}" var="subsidiary">
                                <tr>
                                    <td><span class="badge bg-primary badge-custom">${subsidiary.subsidiaryId}</span></td>
                                    <td><strong>${subsidiary.name}</strong></td>
                                    <td>${subsidiary.nickname != null ? subsidiary.nickname : '-'}</td>
                                    <td>${subsidiary.address != null ? subsidiary.address : '-'}</td>
                                    <td>
                                        <c:if test="${subsidiary.taxpayer != null}">
                                            ${subsidiary.taxpayer.rut} - ${subsidiary.taxpayer.name}
                                        </c:if>
                                    </td>
                                    <td>
                                        <button class="btn btn-sm btn-outline-primary" 
                                            onclick="editSubsidiary(${subsidiary.id}, 
                                                '${subsidiary.name}', 
                                                '${subsidiary.nickname != null ? subsidiary.nickname : ''}', 
                                                '${subsidiary.address != null ? subsidiary.address : ''}', 
                                                ${subsidiary.taxpayer.id})">
                                            <i class="bi bi-pencil"></i>
                                        </button>
                                        <button class="btn btn-sm btn-outline-danger" onclick="deleteSubsidiary(${subsidiary.id}, '${subsidiary.name}')">
                                            <i class="bi bi-trash"></i>
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Subsidiary Modal -->
    <div class="modal fade" id="subsidiaryModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalTitle">Nueva Sucursal</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <form id="subsidiaryForm">
                        <input type="hidden" id="subsidiaryId">
                        
                        <div class="mb-3">
                            <label for="taxpayerId" class="form-label">Cliente *</label>
                            <select class="form-select" id="taxpayerId" required>
                                <option value="">Seleccione un cliente...</option>
                                <c:forEach items="${taxpayers}" var="taxpayer">
                                    <option value="${taxpayer.id}">${taxpayer.rut} - ${taxpayer.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        
                        <div class="mb-3">
                            <label for="subsidiaryName" class="form-label">Nombre *</label>
                            <input type="text" class="form-control" id="subsidiaryName" required>
                        </div>
                        
                        <div class="mb-3">
                            <label for="subsidiaryNickname" class="form-label">Nickname</label>
                            <input type="text" class="form-control" id="subsidiaryNickname">
                        </div>
                        
                        <div class="mb-3">
                            <label for="subsidiaryAddress" class="form-label">Dirección</label>
                            <textarea class="form-control" id="subsidiaryAddress" rows="3"></textarea>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="button" class="btn btn-gradient" onclick="saveSubsidiary()">Guardar</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        // Sidebar functionality
        const sidebar = document.getElementById('sidebar');
        const sidebarOverlay = document.getElementById('sidebarOverlay');
        const navbarMenuBtn = document.getElementById('navbarMenuBtn');

        function toggleSidebar() {
            sidebar.classList.toggle('active');
            sidebarOverlay.classList.toggle('active');
        }

        navbarMenuBtn.addEventListener('click', toggleSidebar);
        sidebarOverlay.addEventListener('click', toggleSidebar);

        // Theme toggle
        const themeToggle = document.getElementById('themeToggle');
        const themeIcon = document.getElementById('themeIcon');
        const currentTheme = localStorage.getItem('theme') || 'light';
        document.documentElement.setAttribute('data-theme', currentTheme);
        updateThemeIcon(currentTheme);

        themeToggle.addEventListener('click', () => {
            const theme = document.documentElement.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
            document.documentElement.setAttribute('data-theme', theme);
            localStorage.setItem('theme', theme);
            updateThemeIcon(theme);
        });

        function updateThemeIcon(theme) {
            themeIcon.className = theme === 'dark' ? 'bi bi-moon-fill' : 'bi bi-sun-fill';
        }

        // Form functions
        function resetForm() {
            document.getElementById('subsidiaryForm').reset();
            document.getElementById('subsidiaryId').value = '';
            document.getElementById('modalTitle').textContent = 'Nueva Sucursal';
        }

        function editSubsidiary(id, name, nickname, address, taxpayerId) {
            document.getElementById('subsidiaryId').value = id;
            document.getElementById('subsidiaryName').value = name;
            document.getElementById('subsidiaryNickname').value = nickname || '';
            document.getElementById('subsidiaryAddress').value = address || '';
            document.getElementById('taxpayerId').value = taxpayerId;
            document.getElementById('modalTitle').textContent = 'Editar Sucursal';
            
            new bootstrap.Modal(document.getElementById('subsidiaryModal')).show();
        }

        function saveSubsidiary() {
            const id = document.getElementById('subsidiaryId').value;
            const name = document.getElementById('subsidiaryName').value;
            const nickname = document.getElementById('subsidiaryNickname').value;
            const address = document.getElementById('subsidiaryAddress').value;
            const taxpayerId = document.getElementById('taxpayerId').value;

            if (!name || !taxpayerId) {
                alert('Por favor complete los campos requeridos');
                return;
            }

            const url = id ? '/sucursales/update' : '/sucursales/create';
            const formData = new FormData();
            if (id) formData.append('id', id);
            formData.append('name', name);
            formData.append('nickname', nickname);
            formData.append('address', address);
            formData.append('taxpayerId', taxpayerId);

            fetch(url, {
                method: 'POST',
                body: formData
            })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => {
                        throw new Error('Server error: ' + text.substring(0, 200));
                    });
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    alert(data.message);
                    location.reload();
                } else {
                    alert('Error: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error al guardar la sucursal: ' + error.message);
            });
        }

        function deleteSubsidiary(id, name) {
            if (!confirm('¿Está seguro de eliminar la sucursal "' + name + '"?')) {
                return;
            }

            const formData = new FormData();
            formData.append('id', id);

            fetch('/sucursales/delete', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert(data.message);
                    location.reload();
                } else {
                    alert('Error: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error al eliminar la sucursal');
            });
        }
    </script>
</body>
</html>
