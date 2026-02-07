<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ContaSoft - Liquidaciones</title>
    
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
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }
        
        .navbar {
            box-shadow: 0 2px 4px var(--shadow-color);
            background-color: var(--navbar-bg) !important;
        }
        
        .main-content {
            flex: 1;
            padding: 2rem 0;
        }
        
        .card {
            background-color: var(--bg-card);
            border: 1px solid var(--border-color);
            border-radius: 12px;
            box-shadow: 0 4px 6px var(--shadow-color);
            transition: all 0.3s;
        }
        
        .card:hover {
            box-shadow: 0 8px 15px var(--shadow-color);
        }
        
        .card-header {
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
            color: white;
            border-radius: 12px 12px 0 0 !important;
            padding: 1rem 1.5rem;
        }
        
        .table-container {
            background-color: var(--bg-card);
            border-radius: 12px;
            box-shadow: 0 4px 6px var(--shadow-color);
            overflow: hidden;
        }
        
        .table {
            margin-bottom: 0;
            color: var(--text-color);
        }
        
        .table thead {
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
            color: white;
        }
        
        .table thead th {
            border: none;
            padding: 1rem;
            font-weight: 600;
            text-transform: uppercase;
            font-size: 0.85rem;
            letter-spacing: 0.5px;
        }
        
        .table tbody tr {
            transition: background-color 0.2s;
        }
        
        .table tbody tr:hover {
            background-color: var(--collapse-bg);
        }
        
        .table tbody td {
            padding: 1rem;
            vertical-align: middle;
            border-color: var(--border-color);
        }
        
        .status-badge {
            padding: 0.4rem 0.8rem;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 600;
            text-transform: uppercase;
        }
        
        .status-pending {
            background-color: #fff3cd;
            color: #856404;
        }
        
        .status-processed {
            background-color: #d4edda;
            color: #155724;
        }
        
        .status-error {
            background-color: #f8d7da;
            color: #721c24;
        }
        
        .btn-action {
            padding: 0.4rem 0.8rem;
            border-radius: 8px;
            font-size: 0.85rem;
            margin: 0 0.2rem;
        }
        
        .page-header {
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
            color: white;
            padding: 2rem;
            border-radius: 12px;
            margin-bottom: 2rem;
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
        }
        
        .page-header h1 {
            margin: 0;
            font-weight: 700;
        }
        
        .page-header p {
            margin: 0.5rem 0 0 0;
            opacity: 0.9;
        }
        
        .theme-toggle {
            background: rgba(255,255,255,0.2);
            border: none;
            color: white;
            width: 40px;
            height: 40px;
            border-radius: 50%;
            cursor: pointer;
            transition: background 0.2s;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .theme-toggle:hover {
            background: rgba(255,255,255,0.3);
        }
        
        .footer {
            background-color: var(--bg-card);
            border-top: 1px solid var(--border-color);
            padding: 1.5rem 0;
            margin-top: auto;
        }
        
        .footer-text {
            color: var(--text-muted);
            font-size: 0.9rem;
        }
        
        .empty-state {
            text-align: center;
            padding: 3rem;
            color: var(--text-muted);
        }
        
        .empty-state i {
            font-size: 4rem;
            margin-bottom: 1rem;
            opacity: 0.5;
        }
        
        .upload-section {
            background-color: var(--bg-card);
            border: 2px dashed var(--border-color);
            border-radius: 12px;
            padding: 2rem;
            text-align: center;
            margin-top: 2rem;
            transition: all 0.3s;
        }
        
        .upload-section:hover {
            border-color: var(--primary-color);
            background-color: var(--collapse-bg);
        }
        
        .back-btn {
            background: rgba(255,255,255,0.2);
            border: 1px solid rgba(255,255,255,0.3);
            color: white;
            padding: 0.5rem 1rem;
            border-radius: 8px;
            text-decoration: none;
            transition: all 0.2s;
        }
        
        .back-btn:hover {
            background: rgba(255,255,255,0.3);
            color: white;
        }
        
        .info-card {
            background: var(--info-badge-bg);
            border-radius: 12px;
            padding: 1rem 1.5rem;
            margin-bottom: 1.5rem;
        }
        
        .info-card-title {
            color: var(--info-badge-color);
            font-weight: 600;
            margin-bottom: 0.25rem;
        }
        
        .info-card-value {
            font-size: 1.5rem;
            font-weight: 700;
            color: var(--text-color);
        }
        
        /* Modal Styles */
        .modal-content {
            background-color: var(--bg-card);
            color: var(--text-color);
            border: 1px solid var(--border-color);
        }
        
        .modal-header {
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
            color: white;
            border-bottom: none;
        }
        
        .modal-header .btn-close {
            filter: brightness(0) invert(1);
        }
        
        .modal-body {
            padding: 0;
        }
        
        .modal-xl {
            max-width: 95%;
        }
        
        .details-table-container {
            max-height: 500px;
            overflow-y: auto;
            overflow-x: auto;
        }
        
        .details-table {
            font-size: 0.75rem;
            margin-bottom: 0;
            white-space: nowrap;
        }
        
        .details-table thead {
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
            color: white;
            position: sticky;
            top: 0;
            z-index: 10;
        }
        
        .details-table thead th {
            padding: 0.5rem 0.4rem;
            font-weight: 600;
            font-size: 0.7rem;
            text-transform: uppercase;
            border: none;
            white-space: nowrap;
        }
        
        .details-table tbody td {
            padding: 0.4rem;
            border-color: var(--border-color);
            vertical-align: middle;
        }
        
        .details-table tbody tr:hover {
            background-color: var(--collapse-bg);
        }
        
        .details-table tbody tr:nth-child(even) {
            background-color: var(--info-badge-bg);
        }
        
        .modal-loading {
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 3rem;
            color: var(--text-muted);
        }
        
        .modal-loading i {
            font-size: 2rem;
            animation: spin 1s linear infinite;
        }
        
        @keyframes spin {
            from { transform: rotate(0deg); }
            to { transform: rotate(360deg); }
        }
        
        .modal-footer {
            background-color: var(--collapse-bg);
            border-top: 1px solid var(--border-color);
        }
        
        .badge-rut {
            background-color: var(--primary-color);
            color: white;
            font-size: 0.7rem;
        }
        
        .text-money {
            font-family: 'Consolas', monospace;
            text-align: right;
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
        .sidebar.active { left: 0; }
        .sidebar-header {
            padding: 1.5rem;
            background: rgba(0,0,0,0.2);
            color: white;
        }
        .sidebar-header h4 { margin: 0; font-weight: bold; }
        .sidebar-menu { padding: 1rem 0; }
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
        .sidebar-menu-item i { width: 25px; margin-right: 10px; }
        .sidebar-section-title {
            padding: 1rem 1.5rem 0.5rem;
            color: rgba(255,255,255,0.6);
            font-size: 0.75rem;
            font-weight: bold;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
        .sidebar-overlay {
            position: fixed; top: 0; left: 0;
            width: 100%; height: 100%;
            background: rgba(0,0,0,0.5);
            z-index: 1040; display: none;
            transition: opacity 0.3s;
        }
        .sidebar-overlay.active { display: block; }
        .sidebar-close {
            position: absolute; top: 1.5rem; right: 1.5rem;
            background: rgba(255,255,255,0.2); border: none;
            color: white; width: 32px; height: 32px;
            border-radius: 50%; display: flex;
            align-items: center; justify-content: center;
            cursor: pointer; transition: background 0.2s;
        }
        .sidebar-close:hover { background: rgba(255,255,255,0.3); }
        .navbar-menu-btn {
            background: none; border: none; color: white;
            font-size: 1.5rem; cursor: pointer;
            padding: 0.5rem; margin-right: 1rem;
            transition: transform 0.2s;
        }
        .navbar-menu-btn:hover { transform: scale(1.1); }

        /* Upload Widget Styles - Compact */
        .upload-widget-compact {
            max-width: 350px;
        }

        .drop-zone-compact {
            border: 2px dashed var(--border-color);
            border-radius: 8px;
            padding: 0.6rem 0.8rem;
            text-align: center;
            transition: all 0.3s ease;
            cursor: pointer;
            background: rgba(255,255,255,0.1);
            display: flex;
            align-items: center;
            gap: 0.6rem;
        }

        .drop-zone-compact:hover {
            border-color: rgba(255,255,255,0.8);
            background: rgba(255,255,255,0.2);
        }

        .drop-zone-compact.drag-over {
            border-color: rgba(255,255,255,0.8);
            background: rgba(255,255,255,0.25);
        }

        .drop-zone-compact .drop-icon {
            font-size: 1.4rem;
            color: rgba(255,255,255,0.9);
        }

        .drop-zone-compact .drop-text {
            font-size: 0.9rem;
            color: white;
            margin: 0;
            font-weight: 500;
        }

        .drop-zone-compact .drop-formats {
            font-size: 0.7rem;
            color: rgba(255,255,255,0.7);
            margin: 0;
        }

        .file-preview-compact {
            display: none;
            background: rgba(255,255,255,0.1);
            border: 2px solid rgba(255,255,255,0.8);
            border-radius: 8px;
            padding: 0.5rem 0.8rem;
        }

        .file-preview-compact.show {
            display: flex;
            align-items: center;
            gap: 0.6rem;
        }

        .file-preview-compact .file-icon {
            font-size: 1.2rem;
            color: rgba(255,255,255,0.9);
        }

        .file-preview-compact .file-details {
            flex: 1;
        }

        .file-preview-compact .file-name {
            font-size: 0.85rem;
            color: white;
            font-weight: 500;
            margin-bottom: 0.1rem;
        }

        .file-preview-compact .file-size {
            font-size: 0.7rem;
            color: rgba(255,255,255,0.7);
            margin-bottom: 0.1rem;
        }

        .file-preview-compact .detected-rut {
            font-size: 0.65rem;
            font-weight: bold;
        }

        .file-preview-compact .detected-rut.valid {
            color: #28a745;
        }

        .file-preview-compact .detected-rut.invalid {
            color: #dc3545;
        }

        .file-preview-compact .file-actions {
            display: flex;
            gap: 0.3rem;
            align-items: center;
        }

        .file-preview-compact .btn-sm {
            padding: 0.25rem 0.5rem;
            font-size: 0.8rem;
        }

        /* Filter Box */
        .filter-box {
            display: flex;
            align-items: center;
            background: var(--bg-card);
            padding: 0.75rem 1rem;
            border-radius: 8px;
            border: 1px solid var(--border-color);
            box-shadow: 0 2px 4px var(--shadow-color);
        }

        .filter-box .form-label {
            font-weight: 500;
            color: var(--text-color);
            margin-bottom: 0;
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
                <i class="bi bi-house-fill"></i>Inicio
            </a>
            <a href="/clientes" class="sidebar-menu-item">
                <i class="bi bi-people-fill"></i>CRUD Clientes
            </a>
            <a href="/sucursales" class="sidebar-menu-item">
                <i class="bi bi-building"></i>CRUD Sucursales
            </a>
            <a href="/templates" class="sidebar-menu-item">
                <i class="bi bi-file-earmark-text-fill"></i>CRUD Templates
            </a>
            <div class="sidebar-section-title mt-3">Reportes y Datos</div>
            <a href="/reportes" class="sidebar-menu-item">
                <i class="bi bi-graph-up"></i>Reportes
            </a>
            <a href="/importar" class="sidebar-menu-item">
                <i class="bi bi-upload"></i>Importar Datos
            </a>
            <div class="sidebar-section-title mt-3">Sistema</div>
            <a href="/configuracion" class="sidebar-menu-item">
                <i class="bi bi-gear-fill"></i>Configuración
            </a>
            <a href="#" onclick="logout()" class="sidebar-menu-item">
                <i class="bi bi-box-arrow-right"></i>Cerrar Sesión
            </a>
        </div>
    </div>
    <div class="sidebar-overlay" id="sidebarOverlay"></div>

    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
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
                        <a class="nav-link active" href="#"><i class="bi bi-file-earmark-text me-1"></i> Cargas</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/configuracion"><i class="bi bi-gear-fill me-1"></i> Configuración</a>
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

    <!-- Main Content -->
    <main class="main-content">
        <div class="container">
            <!-- Page Header -->
            <div class="page-header">
                <div class="d-flex justify-content-between align-items-center flex-wrap">
                    <div>
                        <h1><i class="bi bi-journal-text me-2"></i>Liquidaciones</h1>
                        <p>Listado de liquidaciones importadas para el cliente</p>
                    </div>
                    <div class="d-flex align-items-center gap-3">
                        <!-- Compact Upload Widget -->
                        <div class="upload-widget-compact">
                            <input type="file" id="fileInput" accept=".csv,.xls,.xlsx,.xlsm" style="display: none;" onchange="handleFileSelect(event)">
                            
                            <!-- Drop Zone Compact -->
                            <div class="drop-zone-compact" id="dropZone" onclick="document.getElementById('fileInput').click()">
                                <i class="bi bi-cloud-arrow-up drop-icon"></i>
                                <div>
                                    <p class="drop-text"><strong>Importar archivo</strong></p>
                                    <p class="drop-formats">RUT en nombre (ej: 12345678-9.csv)</p>
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
                        <a href="/" class="back-btn">
                            <i class="bi bi-arrow-left me-1"></i> Volver al Inicio
                        </a>
                    </div>
                </div>
            </div>
            
            <!-- Stats Cards -->
            <div class="row mb-4">
                <div class="col-md-4">
                    <div class="info-card">
                        <div class="info-card-title"><i class="bi bi-files me-1"></i> Total de Cargas</div>
                        <div class="info-card-value">
                            <c:choose>
                                <c:when test="${not empty pbi}">${pbi.size()}</c:when>
                                <c:otherwise>0</c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="info-card">
                        <div class="info-card-title"><i class="bi bi-person me-1"></i> RUT Cliente</div>
                        <div class="info-card-value">
                            <c:choose>
                                <c:when test="${not empty pbi}">${pbi[0].rut}</c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="info-card">
                        <div class="info-card-title"><i class="bi bi-clock-history me-1"></i> Última Versión</div>
                        <div class="info-card-value">
                            <c:choose>
                                <c:when test="${not empty pbi}">${pbi[0].version}</c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Filter Section -->
            <c:if test="${not empty pbi}">
                <div class="row mb-3">
                    <div class="col-12">
                        <div class="d-flex justify-content-end">
                            <div class="filter-box">
                                <label for="monthFilter" class="form-label me-2 mb-0">
                                    <i class="bi bi-funnel me-1"></i>Filtrar por Mes:
                                </label>
                                <select id="monthFilter" class="form-select form-select-sm" style="width: auto; min-width: 150px;">
                                    <option value="">Todos los meses</option>
                                    <option value="ENERO">Enero</option>
                                    <option value="FEBRERO">Febrero</option>
                                    <option value="MARZO">Marzo</option>
                                    <option value="ABRIL">Abril</option>
                                    <option value="MAYO">Mayo</option>
                                    <option value="JUNIO">Junio</option>
                                    <option value="JULIO">Julio</option>
                                    <option value="AGOSTO">Agosto</option>
                                    <option value="SEPTIEMBRE">Septiembre</option>
                                    <option value="OCTUBRE">Octubre</option>
                                    <option value="NOVIEMBRE">Noviembre</option>
                                    <option value="DICIEMBRE">Diciembre</option>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>

            <!-- Table -->
            <c:choose>
                <c:when test="${not empty pbi}">
                    <div class="table-container">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th><i class="bi bi-hash me-1"></i>Versión</th>
                                    <th><i class="bi bi-calendar me-1"></i>Mes</th>
                                    <th><i class="bi bi-file-earmark me-1"></i>Archivo</th>
                                    <th><i class="bi bi-info-circle me-1"></i>Detalles</th>
                                    <th><i class="bi bi-flag me-1"></i>Estado</th>
                                    <th class="text-center"><i class="bi bi-gear me-1"></i>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${pbi}" var="PBI">
                                    <tr>
                                        <td>
                                            <span class="badge bg-primary fs-6">${PBI.version}</span>
                                        </td>
                                        <td>
                                            <strong>${PBI.month}</strong>
                                        </td>
                                        <td>
                                            <i class="bi bi-file-earmark-spreadsheet text-success me-1"></i>
                                            ${PBI.fileName}
                                        </td>
                                        <td>
                                            <span class="text-muted">${PBI.details}</span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${PBI.status == 'PROCESADO'}">
                                                    <span class="status-badge status-processed">
                                                        <i class="bi bi-check-circle me-1"></i>${PBI.status}
                                                    </span>
                                                </c:when>
                                                <c:when test="${PBI.status == 'ERROR'}">
                                                    <span class="status-badge status-error">
                                                        <i class="bi bi-x-circle me-1"></i>${PBI.status}
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="status-badge status-pending">
                                                        <i class="bi bi-hourglass-split me-1"></i>${PBI.status}
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-center">
                                            <button class="btn btn-outline-primary btn-action btn-ver-detalles" 
                                                    data-id="${PBI.id}" data-month="${PBI.month}" title="Ver Detalles">
                                                <i class="bi bi-eye"></i>
                                            </button>
                                            <button class="btn btn-outline-danger btn-action btn-descargar-pdf" 
                                                    data-id="${PBI.id}" title="Descargar PDF">
                                                <i class="bi bi-file-earmark-pdf"></i>
                                            </button>
                                            <button class="btn btn-outline-info btn-action btn-cotizaciones" 
                                                    data-id="${PBI.id}" title="Ver Cotizaciones">
                                                <i class="bi bi-receipt"></i>
                                            </button>
                                            <form method="post" action="/process" style="display: inline;">
                                                <input type="hidden" name="id" value="${PBI.id}" />
                                                <button type="submit" class="btn btn-outline-success btn-action" title="Procesar">
                                                    <i class="bi bi-play-circle"></i>
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="card">
                        <div class="card-body empty-state">
                            <i class="bi bi-inbox"></i>
                            <h4>No hay cargas registradas</h4>
                            <p>Aún no se han importado archivos para este cliente.</p>
                            <a href="/" class="btn btn-primary mt-2">
                                <i class="bi bi-upload me-1"></i> Importar Archivo
                            </a>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>

            <!-- Upload Section -->
            <div class="upload-section">
                <i class="bi bi-cloud-upload fs-1 text-primary mb-2"></i>
                <h5>¿Desea cargar otro archivo?</h5>
                <p class="text-muted mb-3">Puede importar una nueva liquidación desde aquí</p>
                <a href="/" class="btn btn-primary">
                    <i class="bi bi-plus-circle me-1"></i> Nueva Importación
                </a>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-6">
                    <span class="footer-text">
                        <i class="bi bi-calculator-fill me-1"></i>
                        <strong>ContaSoft</strong> © 2026 - Sistema de Gestión Contable
                    </span>
                </div>
                <div class="col-md-6 text-md-end">
                    <span class="footer-text">
                        Desarrollado con <i class="bi bi-heart-fill text-danger"></i> por HP Software
                    </span>
                </div>
            </div>
        </div>
    </footer>

    <!-- Modal Ver Detalles -->
    <div class="modal fade" id="detallesModal" tabindex="-1" aria-labelledby="detallesModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-xl modal-dialog-scrollable">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="detallesModalLabel">
                        <i class="bi bi-list-ul me-2"></i>Detalles de Liquidación - <span id="modalMonth"></span>
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
                </div>
                <div class="modal-body">
                    <div id="detallesLoading" class="modal-loading">
                        <div class="text-center">
                            <i class="bi bi-arrow-repeat"></i>
                            <p class="mt-2">Cargando detalles...</p>
                        </div>
                    </div>
                    <div id="detallesContent" class="details-table-container" style="display: none;">
                        <table class="table details-table table-bordered">
                            <thead>
                                <tr>
                                    <th>RUT</th>
                                    <th>Centro Costo</th>
                                    <th>Sueldo Base</th>
                                    <th>Días Trab.</th>
                                    <th>Sueldo Mensual</th>
                                    <th>Gratificación</th>
                                    <th>Bono Prod.</th>
                                    <th>Horas Extra</th>
                                    <th>Total H.E.</th>
                                    <th><strong>Total Imponible</strong></th>
                                    <th>Colación</th>
                                    <th>Movilización</th>
                                    <th>Asig. Fam.</th>
                                    <th>Desgaste</th>
                                    <th><strong>Total No Imponible</strong></th>
                                    <th><strong>Total Haber</strong></th>
                                    <th>Previsión</th>
                                    <th>% Prev.</th>
                                    <th>Valor Prev.</th>
                                    <th>Salud</th>
                                    <th>% Salud</th>
                                    <th>Valor Salud</th>
                                    <th>AFC</th>
                                    <th>TOTAL DCTO. PREVISIONAL</th>
                                    <th>Asig. Fam.</th>
                                    <th><strong>Renta Líq. Imp.</strong></th>
                                    <th>IUT</th>
                                </tr>
                            </thead>
                            <tbody id="detallesTableBody">
                            </tbody>
                        </table>
                    </div>
                    <div id="detallesEmpty" class="modal-loading" style="display: none;">
                        <div class="text-center">
                            <i class="bi bi-inbox" style="animation: none;"></i>
                            <p class="mt-2">No hay detalles para mostrar</p>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <span class="me-auto text-muted" id="detallesCount"></span>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        <i class="bi bi-x-circle me-1"></i>Cerrar
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        // Formatear número como moneda chilena
        function formatMoney(value) {
            if (value === null || value === undefined) return '$0';
            return '$' + Math.round(value).toLocaleString('es-CL');
        }
        
        // Formatear porcentaje
        function formatPercent(value) {
            if (value === null || value === undefined) return '0%';
            return value.toFixed(2) + '%';
        }

        // Ver Detalles click handler
        document.querySelectorAll('.btn-ver-detalles').forEach(function(btn) {
            btn.addEventListener('click', async function() {
                const pbiId = this.getAttribute('data-id');
                const month = this.getAttribute('data-month');
                
                // Mostrar modal
                const modal = new bootstrap.Modal(document.getElementById('detallesModal'));
                document.getElementById('modalMonth').textContent = month || 'Sin mes';
                document.getElementById('detallesLoading').style.display = 'flex';
                document.getElementById('detallesContent').style.display = 'none';
                document.getElementById('detallesEmpty').style.display = 'none';
                document.getElementById('detallesCount').textContent = '';
                modal.show();
                
                try {
                    const response = await fetch('/api/paybookdetails?pbiId=' + pbiId, {
                        method: 'GET',
                        headers: {
                            'Accept': 'application/json'
                        }
                    });
                    
                    const data = await response.json();
                    
                    document.getElementById('detallesLoading').style.display = 'none';
                    
                    if (data && data.length > 0) {
                        const tbody = document.getElementById('detallesTableBody');
                        tbody.innerHTML = '';
                        
                        data.forEach(function(item) {
                            const row = document.createElement('tr');
                            row.innerHTML = 
                                '<td><span class="badge badge-rut">' + (item.rut || '-') + '</span></td>' +
                                '<td>' + (item.centroCosto || '-') + '</td>' +
                                '<td class="text-money">' + formatMoney(item.sueldoBase) + '</td>' +
                                '<td class="text-center">' + (item.diasTrabajados || 0) + '</td>' +
                                '<td class="text-money">' + formatMoney(item.sueldoMensual) + '</td>' +
                                '<td class="text-money">' + formatMoney(item.gratificacion) + '</td>' +
                                '<td class="text-money">' + formatMoney(item.bonoProduccion) + '</td>' +
                                '<td class="text-center">' + (item.horasExtra || 0) + '</td>' +
                                '<td class="text-money">' + formatMoney(item.totalHoraExtra) + '</td>' +
                                '<td class="text-money">' + formatMoney(item.totalImponible) + '</td>' +
                                '<td class="text-money">' + formatMoney(item.colacion) + '</td>' +
                                '<td class="text-money">' + formatMoney(item.movilizacion) + '</td>' +
                                '<td class="text-money">' + formatMoney(item.totalAsignacionFamiliar) + '</td>' +
                                '<td class="text-money">' + formatMoney(item.descuentoHerramientas) + '</td>' +
                                '<td class="text-money">' + formatMoney((item.colacion || 0) + (item.movilizacion || 0) + (item.totalAsignacionFamiliar || 0) + (item.descuentoHerramientas || 0)) + '</td>' +
                                '<td class="text-money">' + formatMoney(item.totalHaber) + '</td>' +
                                '<td>' + (item.prevision || '-') + '</td>' +
                                '<td class="text-center">' + formatPercent(item.porcentajePrevision) + '</td>' +
                                '<td class="text-money">' + formatMoney(item.valorPrevision) + '</td>' +
                                '<td>' + (item.salud || '-') + '</td>' +
                                '<td class="text-center">' + formatPercent(item.saludPorcentaje) + '</td>' +
                                '<td class="text-money">' + formatMoney(item.valorSalud) + '</td>' +
                                '<td class="text-money">' + formatMoney(item.valorAFC) + '</td>' +
                                '<td class="text-money">' + formatMoney(item.totalDctoPrevisional) + '</td>' +
                                '<td class="text-money">' + formatMoney(item.totalAsignacionFamiliar) + '</td>' +
                                '<td class="text-money">' + formatMoney(item.rentaLiquidaImponible) + '</td>' +
                                '<td class="text-money">' + formatMoney(item.valorIUT) + '</td>';
                            tbody.appendChild(row);
                        });
                        
                        document.getElementById('detallesContent').style.display = 'block';
                        document.getElementById('detallesCount').textContent = 'Total: ' + data.length + ' registros';
                    } else {
                        document.getElementById('detallesEmpty').style.display = 'flex';
                    }
                } catch (error) {
                    console.error('Error:', error);
                    document.getElementById('detallesLoading').style.display = 'none';
                    document.getElementById('detallesEmpty').style.display = 'flex';
                    document.getElementById('detallesEmpty').querySelector('p').textContent = 'Error al cargar los detalles';
                }
            });
        });

        // Ver Cotizaciones click handler
        document.querySelectorAll('.btn-cotizaciones').forEach(function(btn) {
            btn.addEventListener('click', function() {
                const id = this.getAttribute('data-id');
                window.location.href = '/chargesDetails?id=' + id;
            });
        });

        // Descargar PDF click handler
        document.querySelectorAll('.btn-descargar-pdf').forEach(function(btn) {
            btn.addEventListener('click', function() {
                const id = this.getAttribute('data-id');
                fetch('/api/reports/paybook/' + id + '/pdf')
                    .then(response => response.blob())
                    .then(blob => {
                        const url = window.URL.createObjectURL(blob);
                        const a = document.createElement('a');
                        a.href = url;
                        a.download = 'paybook-' + id + '.pdf';
                        a.click();
                        window.URL.revokeObjectURL(url);
                    })
                    .catch(error => {
                        console.error('Error downloading PDF:', error);
                        alert('Error al descargar el PDF');
                    });
            });
        });

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

        // ===== SIDEBAR FUNCTIONALITY =====
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

        // Logout function
        function logout() {
            if (confirm('¿Está seguro que desea cerrar sesión?')) {
                fetch('/api/auth/logout', { method: 'POST' })
                .finally(function() {
                    localStorage.removeItem('jwtToken');
                    localStorage.removeItem('username');
                    localStorage.removeItem('familyId');
                    window.location.replace('/login');
                });
            }
        }

        // ===== UPLOAD WIDGET FUNCTIONALITY =====
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

        // Buscar taxpayer por RUT (simulado para este contexto)
        function findTaxpayerByRut(rut) {
            // En esta página, asumimos que el RUT es válido para el cliente actual
            return { id: 1, name: 'Cliente Actual', rut: rut };
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
                detectedRutEl.textContent = '✓ Archivo válido';
                detectedRutEl.className = 'detected-rut valid';
                fileIcon.className = 'bi bi-file-earmark-check file-icon';
                fileIcon.style.color = '#28a745';
                uploadBtn.disabled = false;
            } else if (detectedRut) {
                detectedRutEl.textContent = '✗ RUT detectado pero no válido';
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

            const uploadBtn = document.getElementById('uploadBtn');
            uploadBtn.disabled = true;
            uploadBtn.innerHTML = '<i class="bi bi-hourglass-split"></i>';

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
                    // Éxito - recargar la página para mostrar la nueva carga
                    removeFile();
                    location.reload();
                } else {
                    // Error - mostrar mensaje
                    alert('Error en la importación:\n\n' + (result.errorMessage || 'Error desconocido'));
                }
            } catch (error) {
                console.error('Upload error:', error);
                alert('Error de conexión al procesar el archivo:\n\n' + error.message);
            } finally {
                uploadBtn.disabled = false;
                uploadBtn.innerHTML = '<i class="bi bi-upload"></i>';
            }
        }

        // ===== MONTH FILTER FUNCTIONALITY =====
        document.getElementById('monthFilter').addEventListener('change', function() {
            const selectedMonth = this.value;
            const tableRows = document.querySelectorAll('.table tbody tr');

            tableRows.forEach(row => {
                const month = row.querySelector('.btn-ver-detalles').getAttribute('data-month');
                if (selectedMonth === '' || month === selectedMonth) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            });

            // Update stats if needed
            updateFilteredStats();
        });

        // Update stats based on visible rows
        function updateFilteredStats() {
            const visibleRows = document.querySelectorAll('.table tbody tr[style=""], .table tbody tr:not([style*="none"])');
            const totalFiltered = visibleRows.length;

            // Update the total count card
            const totalCard = document.querySelector('.info-card-value');
            if (totalCard) {
                // Store original total
                if (!totalCard.dataset.original) {
                    totalCard.dataset.original = totalCard.textContent.trim();
                }

                if (document.getElementById('monthFilter').value === '') {
                    totalCard.textContent = totalCard.dataset.original;
                } else {
                    totalCard.textContent = totalFiltered;
                }
            }
        }
    </script>
</body>
</html>