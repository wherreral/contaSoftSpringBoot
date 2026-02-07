<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ContaSoft - Gestión de Clientes</title>
    
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    
    <style>
        :root {
            --primary-color: #0d6efd;
            --sidebar-width: 280px;
        }
        
        body {
            background-color: #f8f9fa;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        .navbar {
            box-shadow: 0 2px 4px rgba(0,0,0,.1);
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
        
        .client-table-card {
            background: white;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0,0,0,.08);
            overflow: hidden;
        }
        
        .table-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 1.5rem;
        }
        
        .btn-action {
            padding: 0.375rem 0.75rem;
            font-size: 0.875rem;
            margin: 0 0.25rem;
        }
        
        .modal-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 0.75rem 1rem;
        }
        
        .modal-header .btn-close {
            filter: brightness(0) invert(1);
        }
        
        .modal-header .modal-title {
            font-size: 1rem;
        }
        
        .modal-body {
            padding: 1.25rem;
        }
        
        .modal-footer {
            padding: 0.75rem 1rem;
        }
        
        .form-label {
            font-weight: 500;
            color: #495057;
            font-size: 0.875rem;
            margin-bottom: 0.25rem;
        }
        
        .form-control, .form-select {
            font-size: 0.875rem;
        }
        
        .section-divider {
            font-size: 0.7rem;
            color: #6c757d;
            text-transform: uppercase;
            font-weight: 600;
            letter-spacing: 0.5px;
            margin: 0.75rem 0 0.5rem 0;
            padding-bottom: 0.25rem;
            border-bottom: 1px solid #e9ecef;
        }
        
        .modal-body {
            padding: 1rem;
        }
        
        .modal-footer {
            padding: 0.75rem 1rem;
        }
        
        .form-label {
            font-weight: 500;
            color: #495057;
            font-size: 0.875rem;
            margin-bottom: 0.25rem;
        }
        
        .form-control, .form-select {
            font-size: 0.875rem;
            padding: 0.375rem 0.75rem;
        }
        
        .required-field::after {
            content: " *";
            color: #dc3545;
        }
        
        .loading-spinner {
            display: none;
            text-align: center;
            padding: 2rem;
        }
        
        .empty-state {
            text-align: center;
            padding: 3rem;
            color: #6c757d;
        }
        
        .empty-state i {
            font-size: 4rem;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>
    <!-- Sidebar -->
    <div class="sidebar" id="sidebar">
        <div class="sidebar-header">
            <button class="sidebar-close" id="sidebarClose">
                <i class="bi bi-x-lg"></i>
            </button>
            <h4>
                <i class="bi bi-calculator-fill me-2"></i>
                ContaSoft
            </h4>
            <small>Sistema de Gestión</small>
        </div>
        
        <div class="sidebar-menu">
            <div class="sidebar-section-title">Gestión Principal</div>
            
            <a href="/" class="sidebar-menu-item">
                <i class="bi bi-house-fill"></i>
                Inicio
            </a>
            
            <a href="/clientes" class="sidebar-menu-item active">
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
            
            <a href="/configuracion" class="sidebar-menu-item">
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
                        <a class="nav-link active" href="/clientes"><i class="bi bi-people-fill me-1"></i> Clientes</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/configuracion"><i class="bi bi-gear-fill me-1"></i> Configuración</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container-fluid px-4">
        <!-- Page Header -->
        <div class="page-header">
            <div class="row align-items-center">
                <div class="col-md-6">
                    <h5 class="mb-0 text-secondary fw-normal">
                        <i class="bi bi-people-fill me-2"></i>
                        Gestión de Clientes
                    </h5>
                </div>
                <div class="col-md-6 text-end">
                    <button class="btn btn-primary" id="btnNuevoCliente">
                        <i class="bi bi-plus-circle me-2"></i>
                        Nuevo Cliente
                    </button>
                </div>
            </div>
        </div>

        <!-- Clients Table -->
        <div class="client-table-card">
            <div class="table-header">
                <h5 class="mb-0">
                    <i class="bi bi-table me-2"></i>
                    Lista de Clientes
                    <span class="badge bg-light text-dark ms-2" id="totalClientes">0</span>
                </h5>
            </div>
            
            <!-- Loading Spinner -->
            <div class="loading-spinner" id="loadingSpinner">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Cargando...</span>
                </div>
                <p class="mt-2">Cargando clientes...</p>
            </div>
            
            <!-- Empty State -->
            <div class="empty-state" id="emptyState" style="display: none;">
                <i class="bi bi-inbox text-muted"></i>
                <h4>No hay clientes registrados</h4>
                <p>Comienza agregando un nuevo cliente</p>
                <button class="btn btn-primary" onclick="document.getElementById('btnNuevoCliente').click()">
                    <i class="bi bi-plus-circle me-2"></i>
                    Agregar Primer Cliente
                </button>
            </div>
            
            <!-- Table -->
            <div class="table-responsive">
                <table class="table table-hover mb-0" id="clientesTable" style="display: none;">
                    <thead class="table-light">
                        <tr>
                            <th>ID</th>
                            <th>Razón Social</th>
                            <th>RUT</th>
                            <th>Dirección</th>
                            <th>Representante Legal</th>
                            <th class="text-center">Acciones</th>
                        </tr>
                    </thead>
                    <tbody id="clientesTableBody">
                        <!-- Datos cargados dinámicamente -->
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Modal Crear/Editar Cliente -->
    <div class="modal fade" id="clienteModal" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="clienteModalLabel">
                        <i class="bi bi-person-plus-fill me-2"></i>
                        Nuevo Cliente
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <form id="clienteForm">
                        <input type="hidden" id="clienteId">
                        
                        <!-- Información del Cliente -->
                        <div class="section-divider">
                            <i class="bi bi-building me-1"></i>
                            Información del Cliente
                        </div>
                        
                        <div class="row mb-2">
                            <div class="col-md-6">
                                <label for="razonSocial" class="form-label required-field">Razón Social</label>
                                <input type="text" class="form-control" id="razonSocial" required>
                            </div>
                            <div class="col-md-6">
                                <label for="rutCliente" class="form-label required-field">RUT Cliente</label>
                                <input type="text" class="form-control" id="rutCliente" placeholder="12.345.678-9" required>
                            </div>
                        </div>
                        
                        <!-- Dirección -->
                        <div class="section-divider">
                            <i class="bi bi-geo-alt-fill me-1"></i>
                            Dirección
                        </div>
                        
                        <div class="row mb-2">
                            <div class="col-md-6">
                                <label for="region" class="form-label required-field">Región</label>
                                <select class="form-select" id="region" required>
                                    <option value="">Seleccionar región...</option>
                                    <option value="Región Metropolitana">Región Metropolitana</option>
                                    <option value="Región de Valparaíso">Región de Valparaíso</option>
                                    <option value="Región del Biobío">Región del Biobío</option>
                                    <option value="Región de La Araucanía">Región de La Araucanía</option>
                                    <option value="Región de Los Lagos">Región de Los Lagos</option>
                                    <option value="Región de Antofagasta">Región de Antofagasta</option>
                                    <option value="Región de Coquimbo">Región de Coquimbo</option>
                                    <option value="Región de O'Higgins">Región de O'Higgins</option>
                                    <option value="Región del Maule">Región del Maule</option>
                                    <option value="Región de Ñuble">Región de Ñuble</option>
                                    <option value="Región de Los Ríos">Región de Los Ríos</option>
                                    <option value="Región de Aysén">Región de Aysén</option>
                                    <option value="Región de Magallanes">Región de Magallanes</option>
                                    <option value="Región de Arica y Parinacota">Región de Arica y Parinacota</option>
                                    <option value="Región de Tarapacá">Región de Tarapacá</option>
                                    <option value="Región de Atacama">Región de Atacama</option>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label for="comuna" class="form-label required-field">Comuna</label>
                                <input type="text" class="form-control" id="comuna" required>
                            </div>
                        </div>
                        
                        <div class="row mb-3">
                            <div class="col-md-8">
                                <label for="calle" class="form-label required-field">Calle</label>
                                <input type="text" class="form-control" id="calle" required>
                            </div>
                            <div class="col-md-4">
                                <label for="numero" class="form-label required-field">Número</label>
                                <input type="text" class="form-control" id="numero" required>
                            </div>
                        </div>
                        
                        <!-- Representante Legal -->
                        <div class="section-divider">
                            <i class="bi bi-person-badge me-1"></i>
                            Representante Legal
                        </div>
                        
                        <div class="row mb-2">
                            <div class="col-md-6">
                                <label for="nombreRepresentante" class="form-label">Nombre Completo</label>
                                <input type="text" class="form-control" id="nombreRepresentante">
                            </div>
                            <div class="col-md-6">
                                <label for="rutRepresentante" class="form-label">RUT Representante</label>
                                <input type="text" class="form-control" id="rutRepresentante" placeholder="12.345.678-9">
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        <i class="bi bi-x-circle me-2"></i>
                        Cancelar
                    </button>
                    <button type="button" class="btn btn-primary" id="btnGuardarCliente">
                        <i class="bi bi-save me-2"></i>
                        Guardar Cliente
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap 5 JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Custom JavaScript -->
    <script>
        // Sidebar functionality
        const sidebar = document.getElementById('sidebar');
        const navbarMenuBtn = document.getElementById('navbarMenuBtn');
        const sidebarClose = document.getElementById('sidebarClose');
        const sidebarOverlay = document.getElementById('sidebarOverlay');
        
        function openSidebar() {
            sidebar.classList.add('active');
            sidebarOverlay.classList.add('active');
            document.body.style.overflow = 'hidden';
        }
        
        function closeSidebar() {
            sidebar.classList.remove('active');
            sidebarOverlay.classList.remove('active');
            document.body.style.overflow = '';
        }
        
        navbarMenuBtn.addEventListener('click', openSidebar);
        sidebarClose.addEventListener('click', closeSidebar);
        sidebarOverlay.addEventListener('click', closeSidebar);
        
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape' && sidebar.classList.contains('active')) {
                closeSidebar();
            }
        });
        
        // Cliente Management
        const API_BASE_URL = 'http://localhost:8080/api/ui/clientes';
        let clienteModal;
        let isEditMode = false;
        let currentClienteId = null;
        
        // JWT Token Interceptor - Add Authorization header to all fetch requests
        (function() {
            const originalFetch = window.fetch;
            window.fetch = function(url, options = {}) {
                const token = localStorage.getItem('jwtToken');
                if (token) {
                    options.headers = options.headers || {};
                    if (options.headers instanceof Headers) {
                        options.headers.set('Authorization', 'Bearer ' + token);
                    } else {
                        options.headers['Authorization'] = 'Bearer ' + token;
                    }
                }
                return originalFetch(url, options).then(response => {
                    if (response.status === 401 || response.status === 403) {
                        console.warn('Token expirado o inválido, redirigiendo a login...');
                        fetch('/api/auth/logout', { method: 'POST' })
                        .finally(function() {
                            localStorage.removeItem('jwtToken');
                            localStorage.removeItem('username');
                            localStorage.removeItem('familyId');
                            window.location.replace('/login');
                        });
                    }
                    return response;
                });
            };
        })();
        
        document.addEventListener('DOMContentLoaded', function() {
            clienteModal = new bootstrap.Modal(document.getElementById('clienteModal'));
            
            // Check if there's a client ID in the URL parameter
            const urlParams = new URLSearchParams(window.location.search);
            const clientId = urlParams.get('id');
            
            console.log('Client ID from URL:', clientId);
            
            // Load clients on page load
            if (clientId && clientId !== '') {
                console.log('Loading single client:', clientId);
                // If there's a client ID, load only that specific client
                loadSingleCliente(clientId);
                // And open the edit modal after a short delay
                setTimeout(function() {
                    editarCliente(clientId);
                }, 800);
            } else {
                console.log('Loading all clients');
                // Otherwise, load all clients
                loadClientes();
            }
            
            // Nuevo cliente button
            document.getElementById('btnNuevoCliente').addEventListener('click', function() {
                openModalNuevoCliente();
            });
            
            // Guardar cliente button
            document.getElementById('btnGuardarCliente').addEventListener('click', function() {
                guardarCliente();
            });
        });
        
        function loadClientes() {
            showLoading();

            fetch(API_BASE_URL)
                .then(response => response.json())
                .then(data => {
                    hideLoading();
                    console.log('API response:', JSON.stringify(data));

                    if (data.success && data.data) {
                        displayClientes(data.data);
                        document.getElementById('totalClientes').textContent = data.data.length;
                    } else {
                        showError('Error al cargar clientes');
                    }
                })
                .catch(error => {
                    hideLoading();
                    console.error('Error:', error);
                    showError('Error de conexión al cargar clientes');
                });
        }
        
        function loadSingleCliente(clientId) {
            console.log('Fetching client from API:', `${API_BASE_URL}/${clientId}`);
            showLoading();
            let URL = API_BASE_URL+"/"+clientId;
            console.log('URL:', URL);
            fetch(URL)
                .then(response => {
                    console.log('Response status:', response.status);
                    console.log('Response content-type:', response.headers.get('content-type'));
                    
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    
                    const contentType = response.headers.get('content-type');
                    if (!contentType || !contentType.includes('application/json')) {
                        throw new Error('La respuesta no es JSON. Verifica la configuración del servidor.');
                    }
                    
                    return response.json();
                })
                .then(data => {
                    console.log('Client data received:', data);
                    hideLoading();
                    
                    if (data.success && data.data) {
                        // Display as a single-item array
                        displayClientes([data.data]);
                        document.getElementById('totalClientes').textContent = '1';
                    } else {
                        showError('Error al cargar cliente');
                    }
                })
                .catch(error => {
                    hideLoading();
                    console.error('Error completo:', error);
                    showError('Error: ' + error.message);
                });
        }
        
        function displayClientes(clientes) {
            const tbody = document.getElementById('clientesTableBody');
            const table = document.getElementById('clientesTable');
            const emptyState = document.getElementById('emptyState');

            console.log('displayClientes called with:', clientes);

            if (!Array.isArray(clientes) || clientes.length === 0) {
                table.style.display = 'none';
                emptyState.style.display = 'block';
                return;
            }

            table.style.display = 'table';
            emptyState.style.display = 'none';

            tbody.innerHTML = '';

            clientes.forEach(function(cliente) {
                console.log('Rendering cliente:', cliente);

                var id = cliente.id || '';
                var name = cliente.name || '';
                var rut = cliente.rut || '';
                var direccion = 'Sin dirección';
                if (cliente.address && Array.isArray(cliente.address) && cliente.address.length > 0) {
                    var addr = cliente.address[0];
                    direccion = (addr.name || '') + ' ' + (addr.number || '') + ', ' + (addr.comuna || '');
                }
                var representante = cliente.lastname || 'No especificado';

                var tr = document.createElement('tr');
                tr.innerHTML =
                    '<td>' + id + '</td>' +
                    '<td><strong>' + name + '</strong></td>' +
                    '<td>' + rut + '</td>' +
                    '<td>' + direccion + '</td>' +
                    '<td>' + representante + '</td>' +
                    '<td class="text-center">' +
                        '<button class="btn btn-sm btn-primary btn-action" onclick="editarCliente(' + id + ')">' +
                            '<i class="bi bi-pencil-fill"></i> Editar' +
                        '</button> ' +
                        '<a href="/charges?id=' + id + '" class="btn btn-sm btn-success btn-action">' +
                            '<i class="bi bi-upload"></i> Cargas' +
                        '</a>' +
                    '</td>';
                tbody.appendChild(tr);
            });
        }
        
        function openModalNuevoCliente() {
            isEditMode = false;
            currentClienteId = null;
            document.getElementById('clienteModalLabel').innerHTML = '<i class="bi bi-person-plus-fill me-2"></i>Nuevo Cliente';
            document.getElementById('clienteForm').reset();
            document.getElementById('clienteId').value = '';
            clienteModal.show();
        }
        
        function editarCliente(id) {
            isEditMode = true;
            currentClienteId = id;
            let URL = API_BASE_URL+"/"+currentClienteId;
            fetch(URL)
                .then(response => response.json())
                .then(data => {
                    if (data.success && data.data) {
                        const cliente = data.data;
                        
                        document.getElementById('clienteModalLabel').innerHTML = '<i class="bi bi-pencil-fill me-2"></i>Editar Cliente';
                        document.getElementById('clienteId').value = cliente.id;
                        document.getElementById('razonSocial').value = cliente.name || '';
                        document.getElementById('rutCliente').value = cliente.rut || '';
                        
                        // Cargar dirección
                        if (cliente.address && cliente.address.length > 0) {
                            const addr = cliente.address[0];
                            document.getElementById('region').value = addr.province || '';
                            document.getElementById('comuna').value = addr.comuna || '';
                            document.getElementById('calle').value = addr.name || '';
                            document.getElementById('numero').value = addr.number || '';
                        }
                        
                        // Cargar representante legal (parseado del campo lastname)
                        if (cliente.lastname) {
                            const parts = cliente.lastname.split(' - RUT: ');
                            document.getElementById('nombreRepresentante').value = parts[0] || '';
                            document.getElementById('rutRepresentante').value = parts[1] || '';
                        }
                        
                        clienteModal.show();
                    } else {
                        showError('Error al cargar datos del cliente');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    showError('Error de conexión al cargar cliente');
                });
        }
        
        function guardarCliente() {
            const form = document.getElementById('clienteForm');
            
            if (!form.checkValidity()) {
                form.reportValidity();
                return;
            }
            
            const clienteData = {
                razonSocial: document.getElementById('razonSocial').value,
                rutCliente: document.getElementById('rutCliente').value,
                direccion: {
                    region: document.getElementById('region').value,
                    comuna: document.getElementById('comuna').value,
                    calle: document.getElementById('calle').value,
                    numero: document.getElementById('numero').value
                },
                nombreRepresentante: document.getElementById('nombreRepresentante').value,
                rutRepresentante: document.getElementById('rutRepresentante').value
            };
            
            const url = isEditMode ? `${API_BASE_URL}/${currentClienteId}` : API_BASE_URL;
            const method = isEditMode ? 'PUT' : 'POST';
            
            fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(clienteData)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    clienteModal.hide();
                    showSuccess(data.message);
                    loadClientes();
                } else {
                    showError(data.message || 'Error al guardar cliente');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showError('Error de conexión al guardar cliente');
            });
        }
        
        function showLoading() {
            document.getElementById('loadingSpinner').style.display = 'block';
            document.getElementById('clientesTable').style.display = 'none';
            document.getElementById('emptyState').style.display = 'none';
        }
        
        function hideLoading() {
            document.getElementById('loadingSpinner').style.display = 'none';
        }
        
        function showSuccess(message) {
            alert('✓ ' + message);
        }
        
        function showError(message) {
            alert('✗ ' + message);
        }
    </script>
</body>
</html>
