<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ContaSoft - Configuración del Sistema</title>
    
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
    </div>
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
        
        .sidebar-menu-item.active {
            background: rgba(255,255,255,0.15);
            border-left-color: white;
        }
        
        .sidebar-menu-item i {
            width: 25px;
            margin-right: 10px;
        }
        
        .sidebar-section-title {
            padding: 1rem 1.5rem 0.5rem;
            color: rgba(255,255,255,0.6);
            font-size: 0.75rem;
            font-weight: 600;
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
        
        .nav-tabs .nav-link {
            color: var(--text-color);
        }
        
        .nav-tabs .nav-link.active {
            background-color: var(--bg-card);
            color: var(--primary-color);
            border-color: var(--border-color) var(--border-color) var(--bg-card);
        }
        
        .tab-content {
            padding: 1.5rem 0;
        }
    </style>
</head>
<body>
    <!-- Sidebar -->
    <div class="sidebar" id="sidebar">
        <div class="sidebar-header">
            <h4><i class="bi bi-calculator-fill"></i> ContaSoft</h4>
            <p class="mb-0 small">Sistema de Gestión Contable</p>
        </div>
        <div class="sidebar-menu">
            <div class="sidebar-section-title">Menú Principal</div>
            
            <a href="/" class="sidebar-menu-item">
                <i class="bi bi-house-fill"></i>
                Inicio
            </a>
            
            <a href="/clientes" class="sidebar-menu-item">
                <i class="bi bi-people-fill"></i>
                CRUD Clientes
            </a>
            
            <a href="/sucursales" class="sidebar-menu-item">
                <i class="bi bi-building"></i>
                CRUD Sucursales
            </a>
            
            <a href="/templates" class="sidebar-menu-item">
                <i class="bi bi-file-earmark-text-fill"></i>
                CRUD Templates
            </a>
            
            <div class="sidebar-section-title mt-3">Reportes y Datos</div>
            
            <a href="/reportes" class="sidebar-menu-item">
                <i class="bi bi-graph-up"></i>
                Reportes
            </a>
            
            <a href="/importar" class="sidebar-menu-item">
                <i class="bi bi-upload"></i>
                Importar Datos
            </a>
            
            <div class="sidebar-section-title mt-3">Sistema</div>
            
            <a href="/configuracion" class="sidebar-menu-item active">
                <i class="bi bi-gear-fill"></i>
                Configuración
            </a>
            
            <a href="/ayuda" class="sidebar-menu-item">
                <i class="bi bi-question-circle-fill"></i>
                Ayuda
            </a>
        </div>
    </div>
    
    <!-- Sidebar Overlay -->
    <div class="sidebar-overlay" id="sidebarOverlay"></div>

    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-4">
        <div class="container-fluid">
            <button class="navbar-menu-btn" id="navbarMenuBtn">
                <i class="bi bi-list"></i>
            </button>
            <a class="navbar-brand" href="/">
                <i class="bi bi-calculator-fill me-2"></i>
                <strong>ContaSoft</strong>
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="/"><i class="bi bi-house-fill me-1"></i> Inicio</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="/configuracion"><i class="bi bi-gear-fill me-1"></i> Configuración</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container-fluid px-4">
        <!-- Page Header -->
        <div class="page-header mb-4">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h2 class="mb-1">
                        <i class="bi bi-gear-fill text-primary me-2"></i>
                        Configuración del Sistema
                    </h2>
                    <p class="text-muted mb-0">Administra los parámetros y catálogos del sistema</p>
                </div>
            </div>
        </div>

        <!-- Tabs for different configurations -->
        <ul class="nav nav-tabs" id="configTabs" role="tablist">
            <li class="nav-item" role="presentation">
                <button class="nav-link active" id="afp-tab" data-bs-toggle="tab" data-bs-target="#afp" type="button" role="tab">
                    <i class="bi bi-shield-check me-2"></i>AFP (Previsión)
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="isapres-tab" data-bs-toggle="tab" data-bs-target="#isapres" type="button" role="tab">
                    <i class="bi bi-heart-pulse me-2"></i>Isapres (Salud)
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="otros-tab" data-bs-toggle="tab" data-bs-target="#otros" type="button" role="tab">
                    <i class="bi bi-gear me-2"></i>Otros Parámetros
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="varios-tab" data-bs-toggle="tab" data-bs-target="#varios" type="button" role="tab">
                    <i class="bi bi-list me-2"></i>Varios
                </button>
            </li>
        </ul>

        <div class="tab-content" id="configTabContent">
            <!-- AFP Tab -->
            <div class="tab-pane fade show active" id="afp" role="tabpanel">
                <div class="card">
                    <div class="card-header bg-gradient text-white d-flex justify-content-between align-items-center" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                        <h5 class="mb-0"><i class="bi bi-shield-check me-2"></i>Gestión de AFP</h5>
                        <button class="btn btn-light btn-sm" data-bs-toggle="modal" data-bs-target="#afpModal" onclick="resetAFPForm()">
                            <i class="bi bi-plus-circle me-1"></i>Nueva AFP
                        </button>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-hover">
                                <thead>
                                    <tr>
                                        <th width="8%">ID</th>
                                        <th width="40%">Nombre AFP</th>
                                        <th width="20%">Mi Nickname</th>
                                        <th width="12%">Porcentaje (%)</th>
                                        <th width="20%">Acciones</th>
                                    </tr>
                                </thead>
                                <tbody id="afpTableBody">
                                    <tr>
                                        <td colspan="5" class="text-center">
                                            <div class="spinner-border text-primary" role="status">
                                                <span class="visually-hidden">Cargando...</span>
                                            </div>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Isapres Tab (placeholder) -->
            <div class="tab-pane fade" id="isapres" role="tabpanel">
                <div class="card">
                    <div class="card-header bg-gradient text-white d-flex justify-content-between align-items-center" style="background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);">
                        <h5 class="mb-0"><i class="bi bi-heart-pulse me-2"></i>Gestión de Isapres</h5>
                        <button class="btn btn-light btn-sm" data-bs-toggle="modal" data-bs-target="#healthModal" onclick="resetHealthForm()">
                            <i class="bi bi-plus-circle me-1"></i>Nueva Isapre
                        </button>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-hover">
                                <thead>
                                    <tr>
                                        <th width="8%">ID</th>
                                        <th width="30%">Nombre Isapre</th>
                                        <th width="20%">Nickname</th>
                                        <th width="15%">Porcentaje (%)</th>
                                        <th width="27%">Acciones</th>
                                    </tr>
                                </thead>
                                <tbody id="healthTableBody">
                                    <tr>
                                        <td colspan="5" class="text-center">
                                            <div class="spinner-border text-primary" role="status">
                                                <span class="visually-hidden">Cargando...</span>
                                            </div>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Otros Tab (placeholder) -->
            <div class="tab-pane fade" id="otros" role="tabpanel">
                <div class="card">
                    <div class="card-body">
                        <div class="alert alert-info">
                            <i class="bi bi-info-circle me-2"></i>
                            <strong>Próximamente:</strong> Otros parámetros de configuración del sistema.
                        </div>
                    </div>
                </div>
            </div>
            <!-- Varios Tab -->
            <div class="tab-pane fade" id="varios" role="tabpanel">
                <div class="card">
                    <div class="card-header bg-gradient text-white d-flex justify-content-between align-items-center" style="background: linear-gradient(135deg, #ff7e5f 0%, #feb47b 100%);">
                        <h5 class="mb-0"><i class="bi bi-list me-2"></i>Gestión de Varios</h5>
                        <!-- System parameters are global and read-only; hide create button -->
                        <button class="btn btn-light btn-sm" disabled title="Parámetros del sistema - solo lectura">
                            <i class="bi bi-lock-fill me-1"></i>Solo lectura
                        </button>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-hover">
                                <thead>
                                    <tr>
                                        <th width="8%">ID</th>
                                        <th width="25%">Nombre</th>
                                        <th width="20%">Valor</th>
                                        <th width="30%">Descripción</th>
                                        <th width="17%">Acciones</th>
                                    </tr>
                                </thead>
                                <tbody id="variosTableBody">
                                    <tr>
                                        <td colspan="4" class="text-center">
                                            <div class="spinner-border text-primary" role="status">
                                                <span class="visually-hidden">Cargando...</span>
                                            </div>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- AFP Modal -->
    <div class="modal fade" id="afpModal" tabindex="-1" aria-labelledby="afpModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="afpModalLabel">Nueva AFP</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="afpForm">
                        <input type="hidden" id="afpId">
                        
                        <div class="mb-3">
                            <label for="afpName" class="form-label">Nombre de la AFP *</label>
                            <input type="text" class="form-control" id="afpName" required placeholder="Ej: AFP Capital">
                        </div>
                        
                        <!-- Global nickname removed: use tenant nicknames instead -->
                        
                        <div class="mb-3">
                            <label for="afpPercentage" class="form-label">Porcentaje (%) *</label>
                            <input type="number" step="0.01" min="0" max="100" class="form-control" id="afpPercentage" required placeholder="Ej: 11.44">
                            <div class="form-text">Ingrese el porcentaje de cotización (sin el símbolo %)</div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="button" class="btn btn-gradient" onclick="saveAFP()">
                        <i class="bi bi-save me-1"></i>Guardar
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Health/Isapre Modal -->
    <div class="modal fade" id="healthModal" tabindex="-1" aria-labelledby="healthModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="healthModalLabel">Nueva Isapre</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="healthForm">
                        <input type="hidden" id="healthId">
                        
                        <div class="mb-3">
                            <label for="healthName" class="form-label">Nombre de la Isapre *</label>
                            <input type="text" class="form-control" id="healthName" required placeholder="Ej: Isapre Banmédica">
                        </div>
                        
                        <div class="mb-3">
                            <label for="healthNickname" class="form-label">Nickname</label>
                            <input type="text" class="form-control" id="healthNickname" placeholder="Ej: Banmédica">
                            <div class="form-text">Nombre corto o abreviatura (opcional)</div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="healthPercentage" class="form-label">Porcentaje (%) *</label>
                            <input type="number" step="0.01" min="0" max="100" class="form-control" id="healthPercentage" required placeholder="Ej: 7.00">
                            <div class="form-text">Ingrese el porcentaje de cotización (sin el símbolo %)</div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="button" class="btn btn-gradient" onclick="saveHealth()">
                        <i class="bi bi-save me-1"></i>Guardar
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Varios Modal -->
    <div class="modal fade" id="variosModal" tabindex="-1" aria-labelledby="variosModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="variosModalLabel">Nuevo Item</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="variosForm">
                        <input type="hidden" id="variosId">

                        <div class="mb-3">
                            <label for="variosName" class="form-label">Nombre *</label>
                            <input type="text" class="form-control" id="variosName" required placeholder="Ej: clave_parametro">
                        </div>

                        <div class="mb-3">
                            <label for="variosValue" class="form-label">Valor *</label>
                            <input type="text" class="form-control" id="variosValue" required placeholder="Ej: 1234 o true">
                        </div>

                        <div class="mb-3">
                            <label for="variosDescription" class="form-label">Descripción</label>
                            <textarea class="form-control" id="variosDescription" rows="3" placeholder="Descripción opcional"></textarea>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="button" class="btn btn-gradient" onclick="saveVario()">
                        <i class="bi bi-save me-1"></i>Guardar
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        // ==================== JWT TOKEN MANAGEMENT ====================
        (function() {
            // Helper: limpiar sesión llamando al servidor para borrar cookie HttpOnly
            function clearSessionAndRedirect() {
                localStorage.removeItem('jwtToken');
                localStorage.removeItem('username');
                localStorage.removeItem('familyId');
                var xhr = new XMLHttpRequest();
                xhr.open('POST', '/api/auth/logout', false);
                try { xhr.send(); } catch(e) {}
                window.location.replace('/login');
            }

            const token = localStorage.getItem('jwtToken');
            if (!token) {
                clearSessionAndRedirect();
                return;
            }
            try {
                const payload = JSON.parse(atob(token.split('.')[1]));
                const exp = payload.exp * 1000;
                if (exp <= Date.now()) {
                    clearSessionAndRedirect();
                    return;
                }
            } catch(e) {
                clearSessionAndRedirect();
                return;
            }

            // Intercept fetch to include JWT token
            const originalFetch = window.fetch;
            window.fetch = function(url, options = {}) {
                const token = localStorage.getItem('jwtToken');
                if (token) {
                    options.headers = options.headers || {};
                    options.headers['Authorization'] = 'Bearer ' + token;
                }
                return originalFetch(url, options).then(response => {
                    if (response.status === 401 || response.status === 403) {
                        localStorage.removeItem('jwtToken');
                        localStorage.removeItem('username');
                        localStorage.removeItem('familyId');
                        var xhr = new XMLHttpRequest();
                        xhr.open('POST', '/api/auth/logout', false);
                        try { xhr.send(); } catch(e) {}
                        window.location.replace('/login');
                    }
                    return response;
                });
            };

            // Logout function
            window.logout = function() {
                if (confirm('¿Está seguro que desea cerrar sesión?')) {
                    fetch('/api/auth/logout', { method: 'POST' })
                    .finally(function() {
                        localStorage.removeItem('jwtToken');
                        localStorage.removeItem('username');
                        localStorage.removeItem('familyId');
                        window.location.replace('/login');
                    });
                }
            };
        })();

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

        // Theme toggle (si se desea agregar)
        const currentTheme = localStorage.getItem('theme') || 'light';
        document.documentElement.setAttribute('data-theme', currentTheme);

        // Load AFP data on page load
        document.addEventListener('DOMContentLoaded', function() {
            console.log('DOM Content Loaded - Loading AFP data');
            loadAFPData();
            // Load Varios data on initial page load
            loadVariosData();
        });

        // Escape HTML to prevent XSS
        function escapeHtml(text) {
            const map = {
                '&': '&amp;',
                '<': '&lt;',
                '>': '&gt;',
                '"': '&quot;',
                "'": '&#039;'
            };
            return text.replace(/[&<>"']/g, m => map[m]);
        }

        // Load AFP data from API
        function loadAFPData() {
            console.log('Loading AFP data...');
            fetch('/api/ui/afp', {
                method: 'GET',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                }
            })
                .then(response => {
                    console.log('Response status:', response.status);
                    if (!response.ok) {
                        throw new Error('HTTP error! status: ' + response.status);
                    }
                    return response.json();
                })
                .then(data => {
                    console.log('AFP data received:', data);
                    if (data.success && data.data) {
                        displayAFPData(data.data);
                    } else {
                        throw new Error(data.message || 'Error al cargar datos');
                    }
                })
                .catch(error => {
                    console.error('Error loading AFP data:', error);
                    document.getElementById('afpTableBody').innerHTML = `
                        <tr>
                            <td colspan="6" class="text-center text-danger">
                                <i class="bi bi-exclamation-triangle me-2"></i>
                                Error al cargar los datos: \${error.message}
                            </td>
                        </tr>
                    `;
                });
        }

        // Display AFP data in table
        function displayAFPData(afpList) {
            console.log('Displaying AFP data, count:', afpList.length);
            const tbody = document.getElementById('afpTableBody');
            
            if (!afpList || afpList.length === 0) {
                tbody.innerHTML = `
                    <tr>
                        <td colspan="5" class="text-center text-muted">
                            <i class="bi bi-inbox me-2"></i>
                            No hay AFP registradas
                        </td>
                    </tr>
                `;
                return;
            }

            tbody.innerHTML = afpList.map(afp => {
                const safeName = escapeHtml(afp.name);
                const safeTenantNickname = afp.tenantNickname ? escapeHtml(afp.tenantNickname) : '';
                return `
                <tr>
                    <td><span class="badge bg-primary badge-custom">\${afp.id}</span></td>
                    <td><strong>\${afp.name}</strong></td>
                    <td>
                        <div class="input-group input-group-sm">
                            <input type="text" class="form-control" id="tenantNick_\${afp.id}" value="\${safeTenantNickname}" placeholder="Mi alias...">
                            <button class="btn btn-outline-success" onclick="saveNickname(\${afp.id})" title="Guardar mi nickname">
                                <i class="bi bi-check-lg"></i>
                            </button>
                        </div>
                    </td>
                    <td>\${afp.percentaje.toFixed(2)}%</td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary" onclick="editAFP(\${afp.id}, '\${safeName}', \${afp.percentaje})" title="Editar">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteAFP(\${afp.id}, '\${safeName}')" title="Eliminar">
                            <i class="bi bi-trash"></i>
                        </button>
                    </td>
                </tr>
                `;
            }).join('');
        }

        // Reset AFP form
        function resetAFPForm() {
            document.getElementById('afpForm').reset();
            document.getElementById('afpId').value = '';
            document.getElementById('afpModalLabel').textContent = 'Nueva AFP';
        }

        // Edit AFP
        function editAFP(id, name, percentaje) {
            document.getElementById('afpId').value = id;
            document.getElementById('afpName').value = name;
            document.getElementById('afpPercentage').value = percentaje;
            document.getElementById('afpModalLabel').textContent = 'Editar AFP';
            
            new bootstrap.Modal(document.getElementById('afpModal')).show();
        }

        // Save AFP (create or update)
        function saveAFP() {
            const id = document.getElementById('afpId').value;
            const name = document.getElementById('afpName').value.trim();
            const percentaje = parseFloat(document.getElementById('afpPercentage').value);

            if (!name || isNaN(percentaje)) {
                alert('Por favor complete todos los campos requeridos correctamente');
                return;
            }

            if (percentaje < 0 || percentaje > 100) {
                alert('El porcentaje debe estar entre 0 y 100');
                return;
            }

            const url = id ? `/api/ui/afp/\${id}` : '/api/ui/afp';
            const method = id ? 'PUT' : 'POST';
            
            const data = {
                name: name,
                percentaje: percentaje
            };

            console.log(`\${method} \${url}`, data);

            fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
            .then(response => response.json())
            .then(data => {
                console.log('Response:', data);
                if (data.success) {
                    alert(data.message || 'AFP guardada exitosamente');
                    bootstrap.Modal.getInstance(document.getElementById('afpModal')).hide();
                    loadAFPData();
                } else {
                    alert('Error: ' + (data.message || 'Error desconocido'));
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error al guardar la AFP: ' + error.message);
            });
        }

        // Delete AFP
        function deleteAFP(id, name) {
            if (!confirm(`¿Está seguro de eliminar la AFP "${name}"?`)) {
                return;
            }

            fetch(`/api/ui/afp/${id}`, {
                method: 'DELETE'
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert(data.message || 'AFP eliminada exitosamente');
                    loadAFPData();
                } else {
                    alert('Error: ' + (data.message || 'Error desconocido'));
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error al eliminar la AFP: ' + error.message);
            });
        }

        // ==================== AFP NICKNAME POR TENANT ====================

        function saveNickname(afpId) {
            const input = document.getElementById('tenantNick_' + afpId);
            const nickname = input.value.trim();

            fetch('/api/ui/afp/' + afpId + '/nickname', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ nickname: nickname || null })
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Feedback visual
                    input.classList.add('is-valid');
                    setTimeout(() => input.classList.remove('is-valid'), 2000);
                } else {
                    alert('Error: ' + (data.message || 'Error desconocido'));
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error al guardar nickname: ' + error.message);
            });
        }

        // ==================== HEALTH/ISAPRES FUNCTIONS ====================
        
        // Load Health data on tab click
        document.getElementById('isapres-tab').addEventListener('click', function() {
            loadHealthData();
        });
        // Load Varios data on tab click
        document.getElementById('varios-tab').addEventListener('click', function() {
            loadVariosData();
        });

        // Load Health data from API
        function loadHealthData() {
            console.log('Loading Health data...');
            fetch('/api/ui/health', {
                method: 'GET',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                }
            })
                .then(response => {
                    console.log('Response status:', response.status);
                    if (!response.ok) {
                        throw new Error('HTTP error! status: ' + response.status);
                    }
                    return response.json();
                })
                .then(data => {
                    console.log('Health data received:', data);
                    if (data.success && data.data) {
                        displayHealthData(data.data);
                    } else {
                        throw new Error(data.message || 'Error al cargar datos');
                    }
                })
                .catch(error => {
                    console.error('Error loading Health data:', error);
                    document.getElementById('healthTableBody').innerHTML = `
                        <tr>
                            <td colspan="5" class="text-center text-danger">
                                <i class="bi bi-exclamation-triangle me-2"></i>
                                Error al cargar los datos: \${error.message}
                            </td>
                        </tr>
                    `;
                });
        }

        // Display Health data in table
        function displayHealthData(healthList) {
            console.log('Displaying Health data, count:', healthList.length);
            const tbody = document.getElementById('healthTableBody');
            
            if (!healthList || healthList.length === 0) {
                tbody.innerHTML = `
                    <tr>
                        <td colspan="5" class="text-center text-muted">
                            <i class="bi bi-inbox me-2"></i>
                            No hay Isapres registradas
                        </td>
                    </tr>
                `;
                return;
            }

            tbody.innerHTML = healthList.map(health => {
                const safeName = escapeHtml(health.name);
                const safeNickname = health.nickname ? escapeHtml(health.nickname) : '-';
                return `
                <tr>
                    <td><span class="badge bg-success badge-custom">\${health.id}</span></td>
                    <td><strong>\${health.name}</strong></td>
                    <td>\${health.nickname || '-'}</td>
                    <td>\${health.pecentaje.toFixed(2)}%</td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary" onclick="editHealth(\${health.id}, '\${safeName}', '\${safeNickname}', \${health.pecentaje})" title="Editar">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteHealth(\${health.id}, '\${safeName}')" title="Eliminar">
                            <i class="bi bi-trash"></i>
                        </button>
                    </td>
                </tr>
                `;
            }).join('');
        }

        // Reset Health form
        function resetHealthForm() {
            document.getElementById('healthForm').reset();
            document.getElementById('healthId').value = '';
            document.getElementById('healthModalLabel').textContent = 'Nueva Isapre';
        }

        // Edit Health
        function editHealth(id, name, nickname, percentaje) {
            document.getElementById('healthId').value = id;
            document.getElementById('healthName').value = name;
            document.getElementById('healthNickname').value = nickname === '-' ? '' : nickname;
            document.getElementById('healthPercentage').value = percentaje;
            document.getElementById('healthModalLabel').textContent = 'Editar Isapre';
            
            new bootstrap.Modal(document.getElementById('healthModal')).show();
        }

        // Save Health (create or update)
        function saveHealth() {
            const id = document.getElementById('healthId').value;
            const name = document.getElementById('healthName').value.trim();
            const nickname = document.getElementById('healthNickname').value.trim();
            const percentaje = parseFloat(document.getElementById('healthPercentage').value);

            if (!name || isNaN(percentaje)) {
                alert('Por favor complete todos los campos requeridos correctamente');
                return;
            }

            if (percentaje < 0 || percentaje > 100) {
                alert('El porcentaje debe estar entre 0 y 100');
                return;
            }

            const url = id ? `/api/ui/health/\${id}` : '/api/ui/health';
            const method = id ? 'PUT' : 'POST';
            
            const data = {
                name: name,
                nickname: nickname || null,
                percentaje: percentaje
            };

            console.log(`\${method} \${url}`, data);

            fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
            .then(response => response.json())
            .then(data => {
                console.log('Response:', data);
                if (data.success) {
                    alert(data.message || 'Isapre guardada exitosamente');
                    bootstrap.Modal.getInstance(document.getElementById('healthModal')).hide();
                    loadHealthData();
                } else {
                    alert('Error: ' + (data.message || 'Error desconocido'));
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error al guardar la Isapre: ' + error.message);
            });
        }

        // Delete Health
        function deleteHealth(id, name) {
            if (!confirm(`¿Está seguro de eliminar la Isapre "\${name}"?`)) {
                return;
            }

            fetch(`/api/ui/health/\${id}`, {
                method: 'DELETE'
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert(data.message || 'Isapre eliminada exitosamente');
                    loadHealthData();
                } else {
                    alert('Error: ' + (data.message || 'Error desconocido'));
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error al eliminar la Isapre: ' + error.message);
            });
        }
        // ==================== VARIOS FUNCTIONS ====================

        // Load Varios data from API
        function loadVariosData() {
            console.log('Loading Varios data...');
            fetch('/api/ui/varios', {
                method: 'GET',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                }
            })
                .then(response => {
                    console.log('Response status:', response.status);
                    if (!response.ok) {
                        throw new Error('HTTP error! status: ' + response.status);
                    }
                    return response.json();
                })
                .then(data => {
                    console.log('Varios data received:', data);
                    if (data.success && data.data) {
                        displayVariosData(data.data);
                    } else {
                        throw new Error(data.message || 'Error al cargar datos');
                    }
                })
                .catch(error => {
                    console.error('Error loading Varios data:', error);
                    document.getElementById('variosTableBody').innerHTML = `
                        <tr>
                            <td colspan="5" class="text-center text-danger">
                                <i class="bi bi-exclamation-triangle me-2"></i>
                                Error al cargar los datos: \${error.message}
                            </td>
                        </tr>
                    `;
                });
        }

        // Display Varios data in table
        function displayVariosData(variosList) {
            console.log('Displaying Varios data, count:', variosList.length);
            const tbody = document.getElementById('variosTableBody');
            
            if (!variosList || variosList.length === 0) {
                tbody.innerHTML = `
                    <tr>
                        <td colspan="5" class="text-center text-muted">
                            <i class="bi bi-inbox me-2"></i>
                            No hay registros
                        </td>
                    </tr>
                `;
                return;
            }

            tbody.innerHTML = variosList.map(v => {
                const safeName = escapeHtml(v.name || '');
                const safeValue = escapeHtml((v.value !== undefined && v.value !== null) ? String(v.value) : '');
                const safeDesc = v.description ? escapeHtml(v.description) : '-';
                return `
                <tr>
                    <td><span class="badge bg-secondary badge-custom">\${v.id}</span></td>
                    <td><strong>\${safeName}</strong></td>
                    <td>\${safeValue}</td>
                    <td>\${safeDesc}</td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary" onclick="editVario(\${v.id}, '\${safeName}', '\${safeValue}', '\${escapeHtml(v.description || '')}')" title="Editar">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteVario(\${v.id}, '\${safeName}')" title="Eliminar">
                            <i class="bi bi-trash"></i>
                        </button>
                    </td>
                </tr>
                `;
            }).join('');
        }

        // Reset Varios form
        function resetVariosForm() {
            document.getElementById('variosForm').reset();
            document.getElementById('variosId').value = '';
            document.getElementById('variosModalLabel').textContent = 'Nuevo Item';
        }

        // Edit Vario
        function editVario(id, name, value, description) {
            document.getElementById('variosId').value = id;
            document.getElementById('variosName').value = name || '';
            document.getElementById('variosValue').value = value || '';
            document.getElementById('variosDescription').value = (description === '-' ? '' : description) || '';
            document.getElementById('variosModalLabel').textContent = 'Editar Item';
            new bootstrap.Modal(document.getElementById('variosModal')).show();
        }

        // Save Vario (create or update)
        function saveVario() {
            const id = document.getElementById('variosId').value;
            const name = document.getElementById('variosName').value.trim();
            const value = document.getElementById('variosValue').value.trim();
            const description = document.getElementById('variosDescription').value.trim();

            if (!name) {
                alert('Por favor ingrese el nombre');
                return;
            }

            const url = id ? `/api/ui/varios/${id}` : '/api/ui/varios';
            const method = id ? 'PUT' : 'POST';
            
            const data = {
                name: name,
                value: value,
                description: description || null
            };

            console.log(`${method} ${url}`, data);

            fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
            .then(response => response.json())
            .then(data => {
                console.log('Response:', data);
                if (data.success) {
                    alert(data.message || 'Registro guardado exitosamente');
                    bootstrap.Modal.getInstance(document.getElementById('variosModal')).hide();
                    loadVariosData();
                } else {
                    alert('Error: ' + (data.message || 'Error desconocido'));
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error al guardar el registro: ' + error.message);
            });
        }

        // Delete Vario
        function deleteVario(id, name) {
            if (!confirm(`¿Está seguro de eliminar "${name}"?`)) {
                return;
            }

            fetch(`/api/ui/varios/${id}`, {
                method: 'DELETE'
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert(data.message || 'Registro eliminado exitosamente');
                    loadVariosData();
                } else {
                    alert('Error: ' + (data.message || 'Error desconocido'));
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error al eliminar el registro: ' + error.message);
            });
        }
    </script>
</body>
</html>
