<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Templates - ContaSoft</title>
    
    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

    <link rel="stylesheet" href="/css/theme.css">

    <style>
        body {
            background: var(--bg-color);
        }
        
        /* Sidebar Styles */
        .sidebar {
            position: fixed;
            top: 0;
            left: -300px;
            width: 280px;
            height: 100vh;
            background: var(--sidebar-bg);
            z-index: 1050;
            transition: left 0.3s ease;
            box-shadow: 4px 0 20px rgba(0,0,0,0.2);
        }
        
        .sidebar.active {
            left: 0;
        }
        
        .sidebar-header {
            padding: 2rem 1.5rem;
            color: white;
            border-bottom: 1px solid rgba(255,255,255,0.1);
            position: relative;
        }
        
        .sidebar-menu-item {
            display: flex;
            align-items: center;
            padding: 1rem 1.5rem;
            color: rgba(255,255,255,0.8);
            text-decoration: none;
            transition: all 0.3s;
            border-left: 3px solid transparent;
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
        
        .template-card {
            border: 2px solid var(--border-color);
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 15px;
            transition: all 0.3s ease;
            background: var(--bg-card);
        }
        
        .template-card:hover {
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            transform: translateY(-2px);
        }
        
        .template-card.active {
            border-color: var(--primary-color);
            background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
        }
        
        .badge-active {
            background: var(--table-header-bg);
            color: var(--bg-card);
            padding: 5px 15px;
            border-radius: 20px;
        }
        
        .field-library {
            max-height: 600px;
            overflow-y: auto;
            border: 1px solid var(--border-color);
            border-radius: 8px;
            padding: 15px;
            background: var(--bg-card);
        }
        
        .field-item {
            padding: 10px;
            margin: 5px 0;
            background: var(--bg-card);
            border: 1px solid var(--border-color);
            border-radius: 6px;
            cursor: pointer;
            transition: all 0.2s;
        }
        
        .field-item:hover {
            background: #f8f9fa;
            border-color: var(--primary-color);
        }
        
        .field-item.selected {
            background: #e7f3ff;
            border-color: var(--primary-color);
        }
        
        .field-item.required::before {
            content: "✅ ";
            font-size: 14px;
        }
        
        .field-item.optional::before {
            content: "⭕ ";
            font-size: 14px;
        }
        
        .mapped-fields-container {
            min-height: 400px;
            border: 2px dashed #dee2e6;
            border-radius: 8px;
            padding: 20px;
            background: white;
        }
        
        .mapped-field-row {
            display: flex;
            align-items: center;
            padding: 12px;
            margin: 8px 0;
            background: white;
            border: 1px solid #dee2e6;
            border-radius: 6px;
            transition: all 0.2s;
        }
        
        .mapped-field-row:hover {
            background: #f8f9fa;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .csv-preview {
            background: #2d2d2d;
            color: #d4d4d4;
            padding: 15px;
            border-radius: 6px;
            font-family: 'Courier New', monospace;
            font-size: 14px;
            word-break: break-all;
        }
        
        .validation-alert {
            position: sticky;
            top: 0;
            z-index: 10;
        }
        
        @media (max-width: 768px) {
            .field-library {
                max-height: 300px;
                margin-bottom: 20px;
            }
        }
    </style>
</head>
<body>
	<!-- Sidebar (cargado por sidebar.js) -->
	<div id="sidebar-container"></div>

	<!-- Navbar (cargado por navbar.js) -->
	<div id="navbar-container"></div>

    <!-- Vista de Listado de Templates -->
    <div id="template-list-view" class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2><i class="bi bi-file-earmark-text-fill me-2"></i>Gestión de Templates</h2>
            <div>
                <button class="btn btn-primary" onclick="showCreateModal()">
                    <i class="bi bi-plus-circle me-2"></i>Nuevo Template
                </button>
            </div>
        </div>
        
        <!-- Grid de Templates -->
        <div id="templates-grid" class="row">
            <!-- Se llena dinámicamente con JavaScript -->
        </div>
    </div>
    
    <!-- Vista del Editor de Template -->
    <div id="template-editor-view" class="container-fluid mt-4" style="display: none;">
        <div class="row mb-3">
            <div class="col">
                <button class="btn btn-outline-secondary" onclick="showListView()">
                    <i class="bi bi-arrow-left me-2"></i>Volver a Templates
                </button>
                <span class="ms-3 fs-5 fw-bold">Editando: <span id="editor-template-name"></span></span>
            </div>
        </div>
        
        <!-- Alerta de Validación -->
        <div id="validation-alert" class="validation-alert" style="display: none;"></div>
        
        <div class="row">
            <!-- Columna Izquierda: Biblioteca de Campos -->
            <div class="col-md-4">
                <div class="card">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0"><i class="bi bi-book me-2"></i>Biblioteca de Campos</h5>
                    </div>
                    <div class="card-body">
                        <input type="text" id="field-search" class="form-control mb-3" 
                               placeholder="🔍 Buscar campo..." onkeyup="filterFields()">
                        
                        <div class="field-library" id="field-library">
                            <!-- Campos Obligatorios -->
                            <h6 class="text-success fw-bold">✅ CAMPOS OBLIGATORIOS</h6>
                            <div id="required-fields"></div>
                            
                            <hr class="my-3">
                            
                            <!-- Campos Opcionales -->
                            <h6 class="text-warning fw-bold">⭕ CAMPOS OPCIONALES</h6>
                            <div id="optional-fields"></div>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Columna Derecha: Campos Mapeados -->
            <div class="col-md-8">
                <div class="card">
                    <div class="card-header bg-success text-white">
                        <h5 class="mb-0"><i class="bi bi-bullseye me-2"></i>Campos Seleccionados (Orden CSV)</h5>
                    </div>
                    <div class="card-body">
                        <div class="mapped-fields-container" id="mapped-fields-container">
                            <p class="text-muted text-center">
                                <i class="bi bi-arrow-left-circle me-2"></i>
                                Selecciona campos de la biblioteca para agregarlos
                            </p>
                        </div>
                        
                        <button class="btn btn-outline-primary mt-3" onclick="showAddFieldModal()">
                            <i class="bi bi-plus-circle me-2"></i>Agregar Campo
                        </button>
                        
                        <hr class="my-4">
                        
                        <!-- Vista Previa CSV -->
                        <h6 class="fw-bold">Vista Previa del Header CSV:</h6>
                        <div class="csv-preview" id="csv-preview">
                            (vacío)
                        </div>
                        
                        <div class="mt-4 d-flex justify-content-end gap-2">
                            <button class="btn btn-secondary" onclick="showListView()">
                                <i class="bi bi-x-circle me-2"></i>Cancelar
                            </button>
                            <button class="btn btn-success" onclick="saveTemplate()">
                                <i class="bi bi-save me-2"></i>Guardar Template
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Modal: Crear Nuevo Template -->
    <div class="modal fade" id="createTemplateModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Crear Nuevo Template</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label">Contribuyente *</label>
                        <select class="form-select" id="new-template-taxpayer" required>
                            <option value="">-- Seleccione un contribuyente --</option>
                        </select>
                        <div class="form-text">Seleccione el contribuyente al que pertenecerá este template</div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Nombre del Template *</label>
                        <input type="text" class="form-control" id="new-template-name" 
                               placeholder="Ej: Nómina Mensual">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Descripción (opcional)</label>
                        <textarea class="form-control" id="new-template-description" 
                                  rows="3" placeholder="Descripción breve del template"></textarea>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="activate-immediately">
                        <label class="form-check-label" for="activate-immediately">
                            Activar inmediatamente
                        </label>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="button" class="btn btn-primary" onclick="createTemplate()">Crear</button>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Modal: Información del Campo -->
    <div class="modal fade" id="fieldInfoModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-info text-white">
                    <h5 class="modal-title"><i class="bi bi-info-circle me-2"></i><span id="field-info-name"></span></h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <h6>📋 Descripción:</h6>
                    <p id="field-info-description"></p>
                    
                    <h6>📌 Tipo:</h6>
                    <p><span id="field-info-type" class="badge"></span></p>
                    
                    <h6>💡 Ejemplos de uso:</h6>
                    <ul id="field-info-examples"></ul>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Modal: Agregar Campo -->
    <div class="modal fade" id="addFieldModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Seleccionar Campo para Agregar</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <select class="form-select" id="field-select" size="10">
                        <!-- Se llena dinámicamente -->
                    </select>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="button" class="btn btn-primary" onclick="addSelectedField()">Agregar</button>
                </div>
            </div>
        </div>
    </div>

	<!-- Bootstrap JS -->
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

	<!-- Módulos compartidos -->
	<script src="/js/auth.js"></script>
	<script src="/js/sidebar.js"></script>
	<script src="/js/navbar.js"></script>
	<script>
		loadNavbar('templates');
		loadSidebar('templates');
	</script>

	<!-- JavaScript de la aplicación -->
    <script>
        // Configuración de la API
        const API_BASE = 'http://localhost:8080';
        
        // Estado de la aplicación
        let currentTemplate = null;
        let fieldDefinitions = [];
        let mappedFields = [];
        let taxpayers = []; // Lista de contribuyentes
        
        // Ejemplos de valores por campo
        const fieldExamples = {
            'RUT': ['12345678-9', '98765432-1', '15961705-3'],
            'CENTRO_COSTO': ['CASA_MATRIZ', 'SUCURSAL_NORTE', 'BODEGA_SUR'],
            'SUELDO_BASE': ['450000', '550000', '1200000'],
            'DT': ['30', '15', '22'],
            'PREVISION': ['AFP CAPITAL', 'AFP HABITAT', 'AFP PROVIDA'],
            'SALUD': ['FONASA', 'ISAPRE CONSALUD', 'ISAPRE COLMENA'],
            'SALUD_PORCENTAJE': ['7', '8.5', '10'],
            'BONO': ['50000', '100000', '75000'],
            'HORAS_EXTRA': ['10', '5', '20'],
            'ASIG_FAMILIAR': ['15000', '30000', '45000']
        };
        
        // Cargar al iniciar
        document.addEventListener('DOMContentLoaded', function() {
            console.log('aqui3');
            loadFieldDefinitions();
            loadTaxpayers(); // Cargar contribuyentes
            console.log('aqui4');
            loadTemplates();
        });
        
        // Cargar lista de contribuyentes
        async function loadTaxpayers() {
            try {
                const response = await fetch(API_BASE + '/api/templates/taxpayers', {
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem('token')
                    }
                });
                taxpayers = await response.json();
                console.log('Taxpayers cargados:', taxpayers.length);
            } catch (error) {
                console.error('Error loading taxpayers:', error);
                showAlert('Error al cargar contribuyentes', 'danger');
            }
        }
        
        // Cargar definiciones de campos desde el backend
        async function loadFieldDefinitions() {
            try {
                const response = await fetch(API_BASE + '/api/templates/field-definitions', {
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem('token')
                    }
                });
                console.log('aqui');
                fieldDefinitions = await response.json();
                renderFieldLibrary();
            } catch (error) {
                console.error('Error loading field definitions:', error);
                showAlert('Error al cargar definiciones de campos', 'danger');
            }
        }
        
        // Cargar lista de templates
        async function loadTemplates() {
            try {
                const response = await fetch(API_BASE + '/api/templates', {
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem('token')
                    }
                });
                const templates = await response.json();
                console.log('aqui11');
                renderTemplatesGrid(templates);
            } catch (error) {
                console.error('Error loading templates:', error);
                showAlert('Error al cargar templates', 'danger');
            }
        }
        
        // Renderizar grid de templates
        function renderTemplatesGrid(templates) {
            const grid = document.getElementById('templates-grid');
            grid.innerHTML = '';
            
            if (templates.length === 0) {
                grid.innerHTML = `
                    <div class="col-12 text-center py-5">
                        <i class="bi bi-inbox" style="font-size: 64px; color: #ccc;"></i>
                        <p class="text-muted mt-3">No hay templates creados</p>
                        <button class="btn btn-primary" onclick="showCreateModal()">Crear Primer Template</button>
                    </div>
                `;
                return;
            }
            
            templates.forEach(template => {
                const fieldCount = template.value && template.value !== '{}' ? 
                    Object.keys(JSON.parse(template.value)).length : 0;
                const activeClass = template.active ? 'active' : '';
                const activeBadge = template.active ? 
                    '<span class="badge badge-active">✓ ACTIVO</span>' : 
                    '<span class="badge bg-secondary">Inactivo</span>';
                
                // Buscar el taxpayer asociado (si está disponible)
                const taxpayerInfo = template.taxpayerId ? 
                    taxpayers.find(t => t.id === template.taxpayerId) : null;
                const taxpayerBadge = taxpayerInfo ? 
                    '<span class="badge bg-info text-dark mb-2"><i class="bi bi-building me-1"></i>' + taxpayerInfo.name + '</span>' : 
                    (template.taxpayerName ? '<span class="badge bg-info text-dark mb-2"><i class="bi bi-building me-1"></i>' + template.taxpayerName + '</span>' : '');
                
                const card = `
                    <div class="col-md-6 col-lg-4">
                        <div class="template-card ${activeClass}">
                            <div class="d-flex justify-content-between align-items-start mb-2">
                                <h5 class="mb-0"><i class="bi bi-file-earmark-text me-2"></i>${template.name}</h5>
                                ${activeBadge}
                            </div>
                            ${taxpayerBadge}
                            <p class="text-muted small">${template.description || 'Sin descripción'}</p>
                            <p class="mb-3"><strong>${fieldCount}</strong> campos mapeados</p>
                            
                            <div class="btn-group w-100" role="group">
                                <button class="btn btn-sm btn-outline-primary" onclick="editTemplate(${template.id})">
                                    <i class="bi bi-pencil"></i> Editar
                                </button>
                                ${!template.active ? `
                                    <button class="btn btn-sm btn-outline-success" onclick="activateTemplate(${template.id})">
                                        <i class="bi bi-check-circle"></i> Activar
                                    </button>
                                ` : ''}
                                ${!template.active ? `
                                    <button class="btn btn-sm btn-outline-danger" onclick="deleteTemplate(${template.id})">
                                        <i class="bi bi-trash"></i> Eliminar
                                    </button>
                                ` : ''}
                            </div>
                        </div>
                    </div>
                `;
                
                grid.innerHTML += card;
            });
        }
        
        // Renderizar biblioteca de campos
        function renderFieldLibrary() {
            const requiredContainer = document.getElementById('required-fields');
            const optionalContainer = document.getElementById('optional-fields');
            
            requiredContainer.innerHTML = '';
            optionalContainer.innerHTML = '';
            
            fieldDefinitions.forEach(field => {
                const fieldHtml = `
                    <div class="field-item ${field.required ? 'required' : 'optional'}" 
                         onclick="showFieldInfo('${field.fieldName}')">
                        <strong>${field.fieldName}</strong>
                        <br>
                        <small class="text-muted">${field.fieldDescription}</small>
                        <button class="btn btn-sm btn-outline-primary float-end" 
                                onclick="event.stopPropagation(); addFieldToMapping('${field.fieldName}')">
                            <i class="bi bi-plus"></i>
                        </button>
                    </div>
                `;
                
                if (field.required) {
                    requiredContainer.innerHTML += fieldHtml;
                } else {
                    optionalContainer.innerHTML += fieldHtml;
                }
            });
        }
        
        // Filtrar campos
        function filterFields() {
            const search = document.getElementById('field-search').value.toLowerCase();
            const items = document.querySelectorAll('.field-item');
            
            items.forEach(item => {
                const text = item.textContent.toLowerCase();
                item.style.display = text.includes(search) ? 'block' : 'none';
            });
        }
        
        // Mostrar modal con información del campo
        function showFieldInfo(fieldName) {
            const field = fieldDefinitions.find(f => f.fieldName === fieldName);
            if (!field) return;
            
            document.getElementById('field-info-name').textContent = field.fieldName;
            document.getElementById('field-info-description').textContent = field.fieldDescription;
            
            const typeSpan = document.getElementById('field-info-type');
            typeSpan.textContent = field.required ? 'Campo Obligatorio' : 'Campo Opcional';
            typeSpan.className = field.required ? 'badge bg-success' : 'badge bg-warning';
            
            const examples = fieldExamples[fieldName] || ['N/A'];
            const examplesList = document.getElementById('field-info-examples');
            examplesList.innerHTML = examples.map(ex => `<li>${ex}</li>`).join('');
            
            new bootstrap.Modal(document.getElementById('fieldInfoModal')).show();
        }
        
        // Agregar campo al mapeo
        function addFieldToMapping(systemFieldName) {
            if (mappedFields.some(f => f.systemField === systemFieldName)) {
                showAlert('Este campo ya está mapeado', 'warning');
                return;
            }
            
            mappedFields.push({
                csvHeader: systemFieldName,
                systemField: systemFieldName
            });
            
            renderMappedFields();
            updateCsvPreview();
            validateMapping();
        }
        
        // Renderizar campos mapeados
        function renderMappedFields() {
            const container = document.getElementById('mapped-fields-container');
            
            if (mappedFields.length === 0) {
                container.innerHTML = `
                    <p class="text-muted text-center">
                        <i class="bi bi-arrow-left-circle me-2"></i>
                        Selecciona campos de la biblioteca para agregarlos
                    </p>
                `;
                return;
            }
            
            container.innerHTML = '';
            
            mappedFields.forEach((field, index) => {
                const fieldDef = fieldDefinitions.find(f => f.fieldName === field.systemField);
                const icon = fieldDef?.required ? '✅' : '⭕';
                
                const row = document.createElement('div');
                row.className = 'mapped-field-row';
                row.innerHTML = `
                    <span class="me-2 fw-bold">${index + 1}.</span>
                    <input type="text" class="form-control me-2" style="max-width: 200px;" 
                           value="${field.csvHeader}" 
                           onchange="updateCsvHeader(${index}, this.value)"
                           placeholder="Nombre en CSV">
                    <span class="mx-2">→</span>
                    <span class="me-auto">${icon} <strong>${field.systemField}</strong> 
                        <i class="bi bi-info-circle text-primary" 
                           onclick="showFieldInfo('${field.systemField}')" 
                           style="cursor: pointer;"></i>
                    </span>
                    <button class="btn btn-sm btn-outline-danger" onclick="removeMappedField(${index})">
                        <i class="bi bi-x"></i>
                    </button>
                `;
                
                container.appendChild(row);
            });
        }
        
        // Actualizar header CSV de un campo
        function updateCsvHeader(index, newValue) {
            mappedFields[index].csvHeader = newValue;
            updateCsvPreview();
        }
        
        // Eliminar campo mapeado
        function removeMappedField(index) {
            mappedFields.splice(index, 1);
            renderMappedFields();
            updateCsvPreview();
            validateMapping();
        }
        
        // Actualizar vista previa CSV
        function updateCsvPreview() {
            const preview = document.getElementById('csv-preview');
            const headers = mappedFields.map(f => f.csvHeader).join(';');
            preview.textContent = headers || '(vacío)';
        }
        
        // Validar mapeo (campos obligatorios)
        function validateMapping() {
            const requiredFields = fieldDefinitions.filter(f => f.required).map(f => f.fieldName);
            const mappedSystemFields = mappedFields.map(f => f.systemField);
            const missingFields = requiredFields.filter(f => !mappedSystemFields.includes(f));
            
            const alert = document.getElementById('validation-alert');
            
            if (missingFields.length > 0) {
                alert.className = 'validation-alert alert alert-warning';
                alert.innerHTML = `
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    <strong>Faltan ${missingFields.length} campos obligatorios:</strong> ${missingFields.join(', ')}
                `;
                alert.style.display = 'block';
                return false;
            } else {
                alert.style.display = 'none';
                return true;
            }
        }
        
        // Mostrar vista de lista
        function showListView() {
            document.getElementById('template-list-view').style.display = 'block';
            document.getElementById('template-editor-view').style.display = 'none';
            loadTemplates();
        }
        
        // Mostrar vista de editor
        function showEditorView(template) {
            currentTemplate = template;
            
            try {
                if (template.value && template.value !== '{}') {
                    const mapping = JSON.parse(template.value);
                    mappedFields = Object.entries(mapping).map(([sys, csv]) => ({
                        csvHeader: csv,
                        systemField: sys
                    }));
                } else {
                    mappedFields = [];
                }
            } catch (e) {
                console.error('Error parsing template value:', e);
                mappedFields = [];
            }
            
            document.getElementById('template-list-view').style.display = 'none';
            document.getElementById('template-editor-view').style.display = 'block';
            document.getElementById('editor-template-name').textContent = template.name;
            
            renderMappedFields();
            updateCsvPreview();
            validateMapping();
        }
        
        // Editar template existente
        async function editTemplate(templateId) {
            try {
                const response = await fetch(API_BASE + '/api/templates/' + templateId, {
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem('token')
                    }
                });
                const template = await response.json();
                showEditorView(template);
            } catch (error) {
                console.error('Error loading template:', error);
                showAlert('Error al cargar template', 'danger');
            }
        }
        
        // Guardar template
        async function saveTemplate() {
            if (!validateMapping()) {
                showAlert('Por favor, agrega todos los campos obligatorios', 'warning');
                return;
            }
            
            const value = {};
            mappedFields.forEach(field => {
                value[field.systemField] = field.csvHeader;
            });
            
            const payload = {
                name: currentTemplate.name,
                description: currentTemplate.description,
                value: JSON.stringify(value)
            };
            
            try {
                const url = API_BASE + '/api/templates/' + currentTemplate.id;
                const response = await fetch(url, {
                    method: 'PUT',
                    headers: { 
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + localStorage.getItem('token')
                    },
                    body: JSON.stringify(payload)
                });
                
                if (response.ok) {
                    showAlert('Template guardado exitosamente', 'success');
                    setTimeout(() => showListView(), 1500);
                } else {
                    const error = await response.json();
                    showAlert('Error al guardar template: ' + (error.error || 'Error desconocido'), 'danger');
                }
            } catch (error) {
                console.error('Error saving template:', error);
                showAlert('Error al guardar template', 'danger');
            }
        }
        
        // Mostrar modal de creación
        function showCreateModal() {
            document.getElementById('new-template-name').value = '';
            document.getElementById('new-template-description').value = '';
            document.getElementById('activate-immediately').checked = false;
            
            // Poblar selector de taxpayers
            const select = document.getElementById('new-template-taxpayer');
            select.innerHTML = '<option value="">-- Seleccione un contribuyente --</option>';
            taxpayers.forEach(tp => {
                const option = document.createElement('option');
                option.value = tp.id;
                option.textContent = tp.name + ' (' + tp.rut + ')';
                select.appendChild(option);
            });
            
            new bootstrap.Modal(document.getElementById('createTemplateModal')).show();
        }
        
        // Crear nuevo template
        async function createTemplate() {
            const taxpayerId = document.getElementById('new-template-taxpayer').value;
            const name = document.getElementById('new-template-name').value.trim();
            const description = document.getElementById('new-template-description').value.trim();
            const activateImmediately = document.getElementById('activate-immediately').checked;
            
            if (!taxpayerId) {
                showAlert('Debe seleccionar un contribuyente', 'warning');
                return;
            }
            
            if (!name) {
                showAlert('El nombre es obligatorio', 'warning');
                return;
            }
            
            const payload = {
                taxpayerId: parseInt(taxpayerId),
                name: name,
                description: description,
                value: '{}',
                activateImmediately: activateImmediately
            };
            
            try {
                const response = await fetch(API_BASE + '/api/templates', {
                    method: 'POST',
                    headers: { 
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + localStorage.getItem('token')
                    },
                    body: JSON.stringify(payload)
                });
                
                if (response.ok) {
                    const template = await response.json();
                    bootstrap.Modal.getInstance(document.getElementById('createTemplateModal')).hide();
                    showAlert('Template creado exitosamente', 'success');
                    showEditorView(template);
                } else {
                    const error = await response.json();
                    showAlert('Error al crear template: ' + (error.error || 'Error desconocido'), 'danger');
                }
            } catch (error) {
                console.error('Error creating template:', error);
                showAlert('Error al crear template', 'danger');
            }
        }
        
        // Activar template
        async function activateTemplate(templateId) {
            try {
                const response = await fetch(API_BASE + '/api/templates/' + templateId + '/activate', {
                    method: 'PUT',
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem('token')
                    }
                });
                
                if (response.ok) {
                    showAlert('Template activado exitosamente', 'success');
                    loadTemplates();
                } else {
                    const error = await response.json();
                    showAlert('Error al activar template: ' + (error.error || 'Error desconocido'), 'danger');
                }
            } catch (error) {
                console.error('Error activating template:', error);
                showAlert('Error al activar template', 'danger');
            }
        }
        
        // Eliminar template
        async function deleteTemplate(templateId) {
            if (!confirm('¿Estás seguro de eliminar este template?')) return;
            
            try {
                const response = await fetch(API_BASE + '/api/templates/' + templateId, {
                    method: 'DELETE',
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem('token')
                    }
                });
                
                if (response.ok || response.status === 204) {
                    showAlert('Template eliminado exitosamente', 'success');
                    loadTemplates();
                } else {
                    const error = await response.json();
                    showAlert(error.error || 'No se puede eliminar el template', 'warning');
                }
            } catch (error) {
                console.error('Error deleting template:', error);
                showAlert('Error al eliminar template', 'danger');
            }
        }
        
        // Mostrar modal para agregar campo
        function showAddFieldModal() {
            const select = document.getElementById('field-select');
            select.innerHTML = '';
            
            fieldDefinitions.forEach(field => {
                if (!mappedFields.some(f => f.systemField === field.fieldName)) {
                    const option = document.createElement('option');
                    option.value = field.fieldName;
                    option.textContent = `${field.required ? '✅' : '⭕'} ${field.fieldName} - ${field.fieldDescription}`;
                    select.appendChild(option);
                }
            });
            
            new bootstrap.Modal(document.getElementById('addFieldModal')).show();
        }
        
        // Agregar campo seleccionado del modal
        function addSelectedField() {
            const select = document.getElementById('field-select');
            const selectedField = select.value;
            
            if (selectedField) {
                addFieldToMapping(selectedField);
                bootstrap.Modal.getInstance(document.getElementById('addFieldModal')).hide();
            }
        }
        
        // Mostrar alerta
        function showAlert(message, type) {
            const alertDiv = document.createElement('div');
            alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3`;
            alertDiv.style.zIndex = '9999';
            alertDiv.innerHTML = `
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            `;
            document.body.appendChild(alertDiv);
            
            setTimeout(() => {
                alertDiv.remove();
            }, 5000);
        }
        
    </script>
</body>
</html>
