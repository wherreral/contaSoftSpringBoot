<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
            --collapse-bg: #f8f9fa;
            --info-badge-bg: #e7f1ff;
            --info-badge-color: #0d6efd;
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
            --collapse-bg: #1f2940;
            --info-badge-bg: rgba(102, 126, 234, 0.2);
            --info-badge-color: #a3b3ff;
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
            transition: opacity 0.3s;
        }
        
        .sidebar-overlay.active {
            display: block;
        }
        
        .sidebar-toggle {
            position: fixed;
            bottom: 30px;
            right: 30px;
            width: 60px;
            height: 60px;
            border-radius: 50%;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
            cursor: pointer;
            z-index: 1000;
            transition: transform 0.2s, box-shadow 0.2s;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
        }
        
        .sidebar-toggle:hover {
            transform: scale(1.1);
            box-shadow: 0 6px 16px rgba(102, 126, 234, 0.5);
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
        
        .menu-badge {
            background: rgba(255,255,255,0.2);
            padding: 0.2rem 0.6rem;
            border-radius: 12px;
            font-size: 0.75rem;
            margin-left: auto;
        }
        
        .client-card {
            transition: transform 0.2s, box-shadow 0.2s;
            border: 1px solid var(--border-color);
            border-radius: 10px;
            overflow: hidden;
            font-size: 0.85rem;
            background: var(--bg-card);
        }
        
        .client-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 6px 12px var(--shadow-color);
        }
        
        .client-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 1rem;
        }
        
        .client-name-link {
            color: white;
            text-decoration: none;
            transition: opacity 0.2s;
        }
        
        .client-name-link:hover {
            opacity: 0.85;
            text-decoration: underline;
        }
        
        .client-body {
            padding: 1rem;
            background: var(--bg-card);
            color: var(--text-color);
        }
        
        .rut-badge {
            background-color: rgba(255,255,255,0.2);
            padding: 0.25rem 0.6rem;
            border-radius: 16px;
            font-size: 0.75rem;
            display: inline-block;
        }
        
        .info-badge {
            background-color: var(--info-badge-bg);
            color: var(--info-badge-color);
            border-radius: 16px;
            padding: 0.2rem 0.5rem;
            font-size: 0.7rem;
            margin-right: 0.4rem;
            display: inline-block;
        }
        
        .action-btn {
            transition: all 0.2s;
            font-size: 0.75rem;
            padding: 0.3rem 0.5rem;
        }
        
        .action-btn:hover {
            transform: scale(1.05);
        }
        
        .collapse-section {
            background-color: var(--collapse-bg);
            border-radius: 8px;
            padding: 1rem;
            margin-top: 1rem;
        }
        
        .address-item, .subsidiary-item {
            background-color: var(--bg-card);
            border-left: 3px solid #667eea;
            padding: 0.75rem;
            margin-bottom: 0.5rem;
            border-radius: 4px;
            color: var(--text-color);
        }
        
        .search-box {
            max-width: 500px;
        }
        
        .stats-card {
            background: var(--bg-card);
            border-radius: 8px;
            padding: 0.75rem 1rem;
            text-align: center;
            border: 1px solid var(--border-color);
            box-shadow: 0 2px 8px var(--shadow-color);
        }
        
        .stats-number {
            font-size: 1.5rem;
            font-weight: bold;
            color: var(--primary-color);
        }
        
        .stats-label {
            font-size: 0.75rem;
            color: var(--text-muted);
        }
        
        .fade-in {
            animation: fadeIn 0.5s;
        }
        
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
        }
        
        /* Drop Zone Styles - Compact */
        .drop-zone-compact {
            border: 2px dashed var(--border-color);
            border-radius: 10px;
            padding: 0.9rem 1.2rem;
            text-align: center;
            transition: all 0.3s ease;
            cursor: pointer;
            background: var(--bg-card);
            display: flex;
            align-items: center;
            gap: 0.9rem;
        }
        
        .drop-zone-compact:hover {
            border-color: var(--primary-color);
            background: rgba(102, 126, 234, 0.1);
        }
        
        .drop-zone-compact.drag-over {
            border-color: var(--primary-color);
            background: rgba(102, 126, 234, 0.15);
        }
        
        .drop-zone-compact .drop-icon {
            font-size: 1.8rem;
            color: var(--primary-color);
        }
        
        .drop-zone-compact .drop-text {
            font-size: 1rem;
            color: var(--text-color);
            margin: 0;
        }
        
        .drop-zone-compact .drop-formats {
            font-size: 0.85rem;
            color: var(--text-muted);
            margin: 0;
        }
        
        .file-preview-compact {
            display: none;
            background: var(--bg-card);
            border: 2px solid var(--primary-color);
            border-radius: 10px;
            padding: 0.6rem 1.2rem;
        }
        
        .file-preview-compact.show {
            display: flex;
            align-items: center;
            gap: 0.75rem;
        }
        
        .file-preview-compact .file-icon {
            font-size: 1.5rem;
            color: #28a745;
        }
        
        .file-preview-compact .file-details {
            flex: 1;
            min-width: 0;
        }
        
        .file-preview-compact .file-name {
            font-weight: bold;
            font-size: 0.8rem;
            color: var(--text-color);
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        
        .file-preview-compact .file-size {
            font-size: 0.7rem;
            color: var(--text-muted);
        }
        
        .file-preview-compact .detected-rut {
            font-size: 0.7rem;
            font-weight: bold;
        }
        
        .file-preview-compact .detected-rut.valid {
            color: #198754;
        }
        
        .file-preview-compact .detected-rut.invalid {
            color: #dc3545;
        }
        
        .file-preview-compact .file-actions {
            display: flex;
            gap: 0.5rem;
            align-items: center;
        }
        
        .file-preview-compact .btn-sm {
            padding: 0.3rem 0.6rem;
            font-size: 0.9rem;
        }
        
        .upload-widget {
            max-width: 480px;
        }
        
        /* Theme Toggle */
        .theme-toggle {
            background: none;
            border: none;
            color: white;
            font-size: 1.25rem;
            cursor: pointer;
            padding: 0.5rem;
            transition: transform 0.3s;
        }
        
        .theme-toggle:hover {
            transform: rotate(20deg);
        }
        
        /* Dark mode specific overrides */
        [data-theme="dark"] .form-control {
            background-color: var(--input-bg);
            border-color: var(--border-color);
            color: var(--text-color);
        }
        
        [data-theme="dark"] .form-control:focus {
            background-color: var(--input-bg);
            border-color: var(--primary-color);
            color: var(--text-color);
        }
        
        [data-theme="dark"] .input-group-text {
            background-color: var(--input-bg);
            border-color: var(--border-color);
            color: var(--text-muted);
        }
        
        [data-theme="dark"] .btn-outline-secondary {
            color: var(--text-muted);
            border-color: var(--border-color);
        }
        
        [data-theme="dark"] .btn-outline-secondary:hover {
            background-color: var(--border-color);
            color: var(--text-color);
        }
        
        [data-theme="dark"] .modal-content {
            background-color: var(--bg-card);
            color: var(--text-color);
        }
        
        [data-theme="dark"] .modal-header,
        [data-theme="dark"] .modal-footer {
            border-color: var(--border-color);
        }
        
        [data-theme="dark"] .text-dark {
            color: var(--text-color) !important;
        }
        
        [data-theme="dark"] .text-muted {
            color: var(--text-muted) !important;
        }
        
        [data-theme="dark"] .bg-white {
            background-color: var(--bg-card) !important;
        }
        
        [data-theme="dark"] .shadow-sm {
            box-shadow: 0 2px 4px var(--shadow-color) !important;
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
    
    <!-- Floating Sidebar Toggle Button -->
    <button class="sidebar-toggle" id="sidebarToggle">
        <i class="bi bi-list"></i>
    </button>

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
                        <a class="nav-link active" href="/"><i class="bi bi-house-fill me-1"></i> Inicio</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#"><i class="bi bi-file-earmark-text me-1"></i> Reportes</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#"><i class="bi bi-gear-fill me-1"></i> Configuración</a>
                    </li>
                    <li class="nav-item">
                        <button class="theme-toggle" id="themeToggle" title="Cambiar tema">
                            <i class="bi bi-moon-fill" id="themeIcon"></i>
                        </button>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container-fluid px-4">
        <!-- Header Section with Upload Widget -->
        <div class="row mb-3 align-items-center">
            <div class="col-lg-4 col-md-5">
                <h1 class="h3 fw-bold text-dark mb-0">
                    <i class="bi bi-people-fill text-primary me-2"></i>
                    Gestión de Clientes
                </h1>
                <small class="text-muted">Administra la información de tus contribuyentes</small>
            </div>
            <div class="col-lg-5 col-md-4">
                <!-- Compact Upload Widget -->
                <div class="upload-widget">
                    <input type="file" id="fileInput" accept=".csv,.xls,.xlsx,.xlsm" style="display: none;" onchange="handleFileSelect(event)">
                    
                    <!-- Drop Zone Compact -->
                    <div class="drop-zone-compact" id="dropZone" onclick="document.getElementById('fileInput').click()">
                        <i class="bi bi-cloud-arrow-up drop-icon"></i>
                        <div>
                            <p class="drop-text"><strong>Importar archivo</strong></p>
                            <p class="drop-formats">El nombre debe contener el RUT (ej: 12345678-9_nomina.csv)</p>
                        </div>
                    </div>
                    
                    <!-- File Preview Compact -->
                    <div class="file-preview-compact" id="filePreview">
                        <i class="bi bi-file-earmark-check file-icon" id="fileIcon"></i>
                        <div class="file-details">
                            <div class="file-name" id="fileName">archivo.csv</div>
                            <div class="file-size" id="fileSize">0 KB</div>
                            <div class="detected-rut" id="detectedRut"></div>
                        </div>
                        <div class="file-actions">
                            <button class="btn btn-success btn-sm" onclick="uploadFile()" id="uploadBtn">
                                <i class="bi bi-upload"></i>
                            </button>
                            <button class="btn btn-outline-danger btn-sm" onclick="removeFile()">
                                <i class="bi bi-x"></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Search Section -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="search-box">
                    <div class="input-group">
                        <span class="input-group-text bg-white">
                            <i class="bi bi-search"></i>
                        </span>
                        <input type="text" id="searchInput" class="form-control" placeholder="Buscar por nombre o RUT...">
                    </div>
                </div>
            </div>
        </div>

        <!-- Clients Grid -->
        <div class="row" id="clientsGrid">
            <c:forEach items="${taxpayers}" var="Client" varStatus="status">
                <div class="col-xl-3 col-lg-4 col-md-6 mb-3 client-item fade-in" data-client-name="${Client.name}" data-client-rut="${Client.rut}">
                    <div class="card client-card shadow-sm">
                        <!-- Card Header -->
                        <div class="client-header">
                            <div class="d-flex justify-content-between align-items-start">
                                <div>
                                    <a href="/clientes?id=${Client.id}" class="client-name-link">
                                        <h6 class="mb-1 fw-bold">
                                            <i class="bi bi-box-arrow-up-right me-1"></i>
                                            ${Client.name}
                                        </h6>
                                    </a>
                                    <div class="rut-badge">
                                        <i class="bi bi-card-text me-1"></i>
                                        ${Client.rut}
                                    </div>
                                </div>
                                <button class="btn btn-light btn-sm py-0 px-1" type="button" data-bs-toggle="collapse" 
                                        data-bs-target="#details${status.index}" aria-expanded="false">
                                    <i class="bi bi-chevron-down"></i>
                                </button>
                            </div>
                        </div>

                        <!-- Card Body -->
                        <div class="client-body">
                            <!-- Info Badges -->
                            <div class="mb-3">
                                <c:if test="${not empty Client.address}">
                                    <span class="info-badge">
                                        <i class="bi bi-geo-alt-fill"></i> ${Client.address.size()} Direcciones
                                    </span>
                                </c:if>
                                <c:if test="${not empty Client.subsidiary}">
                                    <a href="/sucursales" class="info-badge text-decoration-none" style="cursor: pointer;">
                                        <i class="bi bi-building"></i> ${Client.subsidiary.size()} Sucursales
                                    </a>
                                </c:if>
                            </div>

                            <!-- Action Buttons -->
                            <div class="d-grid gap-2">
                                <form method="post" action="/charges" class="mb-2">
                                    <input type="hidden" name="id" value="${Client.id}" />
                                    <button type="submit" class="btn btn-primary w-100 action-btn">
                                        <i class="bi bi-upload me-2"></i>Ver Cargas
                                    </button>
                                </form>
                                <div class="btn-group" role="group">
                                    <button type="button" class="btn btn-outline-secondary action-btn" 
                                            onclick="showClientTemplates(${Client.id}, '${Client.name}', '${Client.rut}')">
                                        <i class="bi bi-file-earmark-text me-1"></i>Templates
                                    </button>
                                    <button type="button" class="btn btn-outline-secondary action-btn">
                                        <i class="bi bi-person-badge me-1"></i>Empleados
                                    </button>
                                </div>
                            </div>

                            <!-- Collapsible Details -->
                            <div class="collapse" id="details${status.index}">
                                <div class="collapse-section">
                                    <!-- Addresses -->
                                    <c:if test="${not empty Client.address}">
                                        <h6 class="fw-bold mb-2">
                                            <i class="bi bi-geo-alt-fill text-primary me-1"></i>
                                            Direcciones
                                        </h6>
                                        <c:forEach items="${Client.address}" var="add">
                                            <div class="address-item">
                                                <i class="bi bi-pin-map me-2"></i>
                                                <strong>${add.name}</strong> ${add.number}
                                            </div>
                                        </c:forEach>
                                    </c:if>

                                    <!-- Subsidiaries -->
                                    <c:if test="${not empty Client.subsidiary}">
                                        <h6 class="fw-bold mt-3 mb-2">
                                            <i class="bi bi-building text-primary me-1"></i>
                                            Sucursales
                                        </h6>
                                        <c:forEach items="${Client.subsidiary}" var="sucursal">
                                            <div class="subsidiary-item">
                                                <i class="bi bi-shop me-2"></i>
                                                ${sucursal.name}
                                            </div>
                                        </c:forEach>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

        <!-- Empty State -->
        <div class="row" id="emptyState" style="display: none;">
            <div class="col-12 text-center py-5">
                <i class="bi bi-inbox display-1 text-muted"></i>
                <h3 class="mt-3 text-muted">No hay clientes registrados</h3>
                <p class="text-muted">Comienza agregando tu primer cliente al sistema</p>
                <a href="/clientes" class="btn btn-primary btn-lg mt-3">
                    <i class="bi bi-plus-circle me-2"></i>Crear Primer Cliente
                </a>
            </div>
        </div>
        
        <!-- Search Empty State -->
        <div class="row" id="searchEmptyState" style="display: none;">
            <div class="col-12 text-center py-5">
                <i class="bi bi-search display-1 text-muted"></i>
                <h3 class="mt-3 text-muted">No se encontraron clientes</h3>
                <p class="text-muted">Intenta con otro término de búsqueda</p>
            </div>
        </div>
    </div>
    
    <!-- Modal: Templates del Cliente -->
    <div class="modal fade" id="clientTemplatesModal" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white;">
                    <h5 class="modal-title">
                        <i class="bi bi-file-earmark-text me-2"></i>
                        Templates de <span id="modal-client-name"></span>
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <small class="text-muted">RUT: <span id="modal-client-rut"></span></small>
                    </div>
                    <div id="templates-loading" class="text-center py-4" style="display: none;">
                        <div class="spinner-border text-primary" role="status">
                            <span class="visually-hidden">Cargando...</span>
                        </div>
                        <p class="mt-2 text-muted">Cargando templates...</p>
                    </div>
                    <div id="templates-list"></div>
                    <div id="templates-empty" class="text-center py-4" style="display: none;">
                        <i class="bi bi-inbox display-4 text-muted"></i>
                        <p class="mt-2 text-muted">Este cliente no tiene templates configurados</p>
                        <a href="/templates" class="btn btn-primary btn-sm">
                            <i class="bi bi-plus-circle me-1"></i>Crear Template
                        </a>
                    </div>
                </div>
                <div class="modal-footer">
                    <a href="/templates" class="btn btn-outline-primary">
                        <i class="bi bi-gear me-1"></i>Gestionar Templates
                    </a>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap 5 JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- JWT Token Interceptor & Authentication -->
    <script>
        // JWT Token Management and Authentication
        (function() {
            // Helper: limpiar sesión llamando al servidor para borrar cookie HttpOnly
            function clearSessionAndRedirect() {
                localStorage.removeItem('jwtToken');
                localStorage.removeItem('username');
                localStorage.removeItem('familyId');
                // Llamar al servidor para borrar la cookie HttpOnly
                var xhr = new XMLHttpRequest();
                xhr.open('POST', '/api/auth/logout', false); // síncrono para que se ejecute antes del redirect
                try { xhr.send(); } catch(e) {}
                window.location.replace('/login');
            }

            // Check if user has a valid (non-expired) token
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
            
            // Setup fetch to include JWT token in all requests
            const originalFetch = window.fetch;
            window.fetch = function(url, options = {}) {
                const token = localStorage.getItem('jwtToken');
                if (token) {
                    options.headers = options.headers || {};
                    options.headers['Authorization'] = 'Bearer ' + token;
                }
                
                return originalFetch(url, options).then(response => {
                    // Handle 401 Unauthorized responses
                    if (response.status === 401 || response.status === 403) {
                        // No usar fetch aquí (estamos dentro del interceptor), usar XMLHttpRequest
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
            
            // Add logout functionality
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
            
            // Display username in navbar
            const username = localStorage.getItem('username');
            if (username) {
                // Add user info to navbar
                const navbar = document.querySelector('.navbar .container-fluid');
                if (navbar && !document.getElementById('userInfo')) {
                    const userInfo = document.createElement('div');
                    userInfo.id = 'userInfo';
                    userInfo.className = 'd-flex align-items-center ms-auto';
                    userInfo.innerHTML = `
                        <span class="text-white me-3">
                            <i class="bi bi-person-circle"></i> ${username}
                        </span>
                        <button class="btn btn-sm btn-outline-light" onclick="logout()">
                            <i class="bi bi-box-arrow-right"></i> Salir
                        </button>
                    `;
                    navbar.appendChild(userInfo);
                }
            }
        })();
    </script>
    
    <!-- Custom JavaScript -->
    <script>
        const API_BASE = 'http://localhost:8080';
        
        // Mostrar templates del cliente
        async function showClientTemplates(clientId, clientName, clientRut) {
            document.getElementById('modal-client-name').textContent = clientName;
            document.getElementById('modal-client-rut').textContent = clientRut;
            document.getElementById('templates-loading').style.display = 'block';
            document.getElementById('templates-list').innerHTML = '';
            document.getElementById('templates-empty').style.display = 'none';
            
            const modal = new bootstrap.Modal(document.getElementById('clientTemplatesModal'));
            modal.show();
            
            try {
                const response = await fetch(API_BASE + '/api/templates/by-taxpayer/' + clientId, {
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem('jwtToken')
                    }
                });
                
                document.getElementById('templates-loading').style.display = 'none';
                
                if (response.ok) {
                    const templates = await response.json();
                    
                    if (templates.length === 0) {
                        document.getElementById('templates-empty').style.display = 'block';
                    } else {
                        renderClientTemplates(templates);
                    }
                } else {
                    document.getElementById('templates-list').innerHTML = 
                        '<div class="alert alert-danger">Error al cargar templates</div>';
                }
            } catch (error) {
                console.error('Error:', error);
                document.getElementById('templates-loading').style.display = 'none';
                document.getElementById('templates-list').innerHTML = 
                    '<div class="alert alert-danger">Error de conexión al cargar templates</div>';
            }
        }
        
        function renderClientTemplates(templates) {
            const container = document.getElementById('templates-list');
            let html = '<div class="list-group">';
            
            templates.forEach(template => {
                const fieldCount = template.value && template.value !== '{}' ? 
                    Object.keys(JSON.parse(template.value)).length : 0;
                const activeClass = template.active ? 'list-group-item-success' : '';
                const activeBadge = template.active ? 
                    '<span class="badge bg-success"><i class="bi bi-check-circle me-1"></i>ACTIVO</span>' : 
                    '<span class="badge bg-secondary">Inactivo</span>';
                
                html += `
                    <div class="list-group-item ${activeClass}">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <h6 class="mb-1">
                                    <i class="bi bi-file-earmark-text me-2"></i>${template.name}
                                </h6>
                                <p class="mb-1 text-muted small">${template.description || 'Sin descripción'}</p>
                                <small><strong>${fieldCount}</strong> campos mapeados</small>
                            </div>
                            <div>
                                ${activeBadge}
                            </div>
                        </div>
                    </div>
                `;
            });
            
            html += '</div>';
            container.innerHTML = html;
        }
        
        // Sidebar functionality
        const sidebar = document.getElementById('sidebar');
        const sidebarToggle = document.getElementById('sidebarToggle');
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
        
        sidebarToggle.addEventListener('click', openSidebar);
        navbarMenuBtn.addEventListener('click', openSidebar);
        sidebarClose.addEventListener('click', closeSidebar);
        sidebarOverlay.addEventListener('click', closeSidebar);
        
        // Close sidebar on ESC key
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape' && sidebar.classList.contains('active')) {
                closeSidebar();
            }
        });
        
        // Search functionality
        document.getElementById('searchInput').addEventListener('input', function(e) {
            const searchTerm = e.target.value.toLowerCase();
            const clientItems = document.querySelectorAll('.client-item');
            let visibleCount = 0;

            clientItems.forEach(function(item) {
                const clientName = item.getAttribute('data-client-name').toLowerCase();
                const clientRut = item.getAttribute('data-client-rut').toLowerCase();
                
                if (clientName.includes(searchTerm) || clientRut.includes(searchTerm)) {
                    item.style.display = '';
                    visibleCount++;
                } else {
                    item.style.display = 'none';
                }
            });

            // Show/hide empty states
            const hasSearchTerm = searchTerm.length > 0;
            document.getElementById('emptyState').style.display = 'none';
            document.getElementById('searchEmptyState').style.display = (hasSearchTerm && visibleCount === 0) ? 'block' : 'none';
        });
        
        // Check if there are no clients on page load
        window.addEventListener('DOMContentLoaded', function() {
            const clientItems = document.querySelectorAll('.client-item');
            if (clientItems.length === 0) {
                document.getElementById('emptyState').style.display = 'block';
            }
        });

        // Add smooth scroll behavior
        document.documentElement.style.scrollBehavior = 'smooth';

        // Console log for debugging
        console.log('ContaSoft - Sistema de gestión de clientes cargado correctamente');
        
        // ===== DROP ZONE FUNCTIONALITY =====
        let selectedFile = null;
        let detectedRut = null;
        let detectedTaxpayer = null;
        
        const dropZone = document.getElementById('dropZone');
        const fileInput = document.getElementById('fileInput');
        const filePreview = document.getElementById('filePreview');
        
        // Extraer RUT del nombre del archivo
        function extractRutFromFilename(filename) {
            // Patrones comunes: 12345678-9, 12.345.678-9, 123456789
            const patterns = [
                /(\d{1,2}\.?\d{3}\.?\d{3}-[\dkK])/i,  // Con guión y dígito verificador
                /(\d{7,8}-[\dkK])/i                      // Sin puntos
            ];
            
            for (const pattern of patterns) {
                const match = filename.match(pattern);
                if (match) {
                    // Normalizar: remover puntos, mantener guión
                    return match[1].replace(/\./g, '').toUpperCase();
                }
            }
            return null;
        }
        
        // Buscar taxpayer por RUT en la lista del servidor
        function findTaxpayerByRut(rut) {
            const taxpayers = [
                <c:forEach items="${taxpayers}" var="Client" varStatus="status">
                    { id: ${Client.id}, name: '${Client.name}', rut: '${Client.rut}' }<c:if test="${!status.last}">,</c:if>
                </c:forEach>
            ];
            
            // Normalizar el RUT buscado
            const normalizedRut = rut.replace(/\./g, '').toUpperCase();
            
            return taxpayers.find(t => {
                const taxpayerRut = t.rut.replace(/\./g, '').toUpperCase();
                return taxpayerRut === normalizedRut;
            });
        }
        
        // Drag and drop events
        dropZone.addEventListener('dragover', function(e) {
            e.preventDefault();
            dropZone.classList.add('drag-over');
        });
        
        dropZone.addEventListener('dragleave', function(e) {
            e.preventDefault();
            dropZone.classList.remove('drag-over');
        });
        
        dropZone.addEventListener('drop', function(e) {
            e.preventDefault();
            dropZone.classList.remove('drag-over');
            
            const files = e.dataTransfer.files;
            if (files.length > 0) {
                handleFile(files[0]);
            }
        });
        
        // Handle file selection from input
        function handleFileSelect(event) {
            const files = event.target.files;
            if (files.length > 0) {
                handleFile(files[0]);
            }
        }
        
        // Process selected file
        function handleFile(file) {
            const validExtensions = ['.csv', '.xls', '.xlsx', '.xlsm'];
            const fileName = file.name.toLowerCase();
            const isValid = validExtensions.some(ext => fileName.endsWith(ext));
            
            if (!isValid) {
                alert('Formato de archivo no soportado. Por favor use CSV, XLS, XLSX o XLSM.');
                return;
            }
            
            selectedFile = file;
            
            // Extraer RUT del nombre del archivo
            detectedRut = extractRutFromFilename(file.name);
            detectedTaxpayer = detectedRut ? findTaxpayerByRut(detectedRut) : null;
            
            // Update preview
            document.getElementById('fileName').textContent = file.name;
            document.getElementById('fileSize').textContent = formatFileSize(file.size);
            
            const detectedRutEl = document.getElementById('detectedRut');
            const fileIcon = document.getElementById('fileIcon');
            const uploadBtn = document.getElementById('uploadBtn');
            
            if (detectedTaxpayer) {
                detectedRutEl.textContent = '✓ ' + detectedTaxpayer.name;
                detectedRutEl.className = 'detected-rut valid';
                fileIcon.className = 'bi bi-file-earmark-check file-icon';
                fileIcon.style.color = '#28a745';
                uploadBtn.disabled = false;
            } else if (detectedRut) {
                detectedRutEl.textContent = '✗ RUT ' + detectedRut + ' no encontrado';
                detectedRutEl.className = 'detected-rut invalid';
                fileIcon.className = 'bi bi-file-earmark-x file-icon';
                fileIcon.style.color = '#dc3545';
                uploadBtn.disabled = true;
            } else {
                detectedRutEl.textContent = '✗ No se detectó RUT en el nombre';
                detectedRutEl.className = 'detected-rut invalid';
                fileIcon.className = 'bi bi-file-earmark-x file-icon';
                fileIcon.style.color = '#dc3545';
                uploadBtn.disabled = true;
            }
            
            // Show preview, hide drop zone
            dropZone.style.display = 'none';
            filePreview.classList.add('show');
        }
        
        // Format file size
        function formatFileSize(bytes) {
            if (bytes === 0) return '0 Bytes';
            const k = 1024;
            const sizes = ['Bytes', 'KB', 'MB', 'GB'];
            const i = Math.floor(Math.log(bytes) / Math.log(k));
            return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
        }
        
        // Remove selected file
        function removeFile() {
            selectedFile = null;
            detectedRut = null;
            detectedTaxpayer = null;
            fileInput.value = '';
            filePreview.classList.remove('show');
            dropZone.style.display = 'flex';
            document.getElementById('uploadBtn').disabled = false;
        }
        
        // Upload file
        async function uploadFile() {
            if (!selectedFile) {
                alert('Por favor seleccione un archivo');
                return;
            }
            
            if (!detectedTaxpayer) {
                alert('No se pudo identificar el cliente. El nombre del archivo debe contener un RUT válido.');
                return;
            }
            
            const uploadBtn = document.getElementById('uploadBtn');
            uploadBtn.disabled = true;
            uploadBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> Procesando...';
            
            const formData = new FormData();
            formData.append('fileUpload', selectedFile);
            
            try {
                const response = await fetch('/importBookAjax', {
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json'
                    },
                    body: formData
                });
                
                const result = await response.json();
                
                if (result.success) {
                    // Éxito - redirigir a la página de PayBookInstances del cliente
                    removeFile();
                    window.location.href = result.redirectUrl;
                } else {
                    // Error - mostrar popup con el mensaje de error
                    alert('Error en la importación:\n\n' + (result.errorMessage || 'Error desconocido'));
                }
            } catch (error) {
                console.error('Upload error:', error);
                alert('Error de conexión al procesar el archivo:\n\n' + error.message);
            } finally {
                uploadBtn.disabled = false;
                uploadBtn.innerHTML = '<i class="bi bi-upload"></i> Subir';
            }
        }
        
        // ===== THEME TOGGLE FUNCTIONALITY =====
        const themeToggle = document.getElementById('themeToggle');
        const themeIcon = document.getElementById('themeIcon');
        const html = document.documentElement;
        
        // Check for saved theme preference or default to light
        const savedTheme = localStorage.getItem('theme') || 'light';
        html.setAttribute('data-theme', savedTheme);
        updateThemeIcon(savedTheme);
        
        function updateThemeIcon(theme) {
            if (theme === 'dark') {
                themeIcon.className = 'bi bi-sun-fill';
            } else {
                themeIcon.className = 'bi bi-moon-fill';
            }
        }
        
        themeToggle.addEventListener('click', function() {
            const currentTheme = html.getAttribute('data-theme');
            const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
            
            html.setAttribute('data-theme', newTheme);
            localStorage.setItem('theme', newTheme);
            updateThemeIcon(newTheme);
        });
    </script>
</body>
</html>