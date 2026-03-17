<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ContaSoft - Procesados</title>

    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <!-- Theme CSS -->
    <link rel="stylesheet" href="/css/theme.css">

    <style>
        :root {
            --shadow-color: rgba(0,0,0,0.08);
        }
        [data-theme="dark"] {
            --shadow-color: rgba(0,0,0,0.3);
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        /* Sidebar Styles */
        .sidebar {
            position: fixed;
            top: 0;
            left: -280px;
            width: var(--sidebar-width);
            height: 100vh;
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

        .main-content {
            flex: 1;
            padding: 2rem 0;
        }

        .card {
            background-color: var(--bg-card);
            border: 1px solid var(--border-color);
            border-radius: 12px;
            box-shadow: 0 4px 6px var(--shadow-color);
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
            cursor: pointer;
        }

        .table tbody tr:hover {
            background-color: var(--collapse-bg);
        }

        .table tbody td {
            padding: 1rem;
            vertical-align: middle;
            border-color: var(--border-color);
        }

        .table tbody tr.selected {
            background-color: rgba(102, 126, 234, 0.15);
            border-left: 3px solid var(--primary-color);
        }

        .status-badge {
            padding: 0.35em 0.75em;
            border-radius: 50px;
            font-size: 0.8rem;
            font-weight: 600;
        }

        .status-processed {
            background-color: rgba(25, 135, 84, 0.1);
            color: #198754;
        }

        .btn-action {
            width: 36px;
            height: 36px;
            padding: 0;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            border-radius: 8px;
            margin: 0 2px;
        }

        .text-money {
            text-align: right;
            font-family: 'Consolas', 'Courier New', monospace;
            font-size: 0.85rem;
        }

        .details-section {
            margin-top: 1.5rem;
        }

        .details-table-container {
            overflow-x: auto;
            max-height: 500px;
            overflow-y: auto;
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

        .details-table tfoot td {
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
            color: white;
            font-weight: 700;
            font-size: 0.8rem;
            padding: 0.6rem 0.4rem;
            white-space: nowrap;
            position: sticky;
            bottom: 0;
        }

        .badge-rut {
            background-color: rgba(102, 126, 234, 0.15);
            color: var(--primary-color);
            font-weight: 600;
            font-size: 0.8rem;
        }

        .empty-state {
            text-align: center;
            padding: 3rem 1rem;
            color: var(--text-muted);
        }

        .empty-state i {
            font-size: 3rem;
            margin-bottom: 1rem;
            display: block;
        }

        .filter-section {
            background-color: var(--bg-card);
            border-radius: 12px;
            padding: 1rem 1.5rem;
            margin-bottom: 1.5rem;
            box-shadow: 0 2px 4px var(--shadow-color);
        }

        .pdf-actions {
            margin-top: 1rem;
            display: flex;
            gap: 0.5rem;
        }

        .footer {
            background-color: var(--bg-card);
            border-top: 1px solid var(--border-color);
            padding: 1rem 0;
            color: var(--text-muted);
            font-size: 0.85rem;
        }
    </style>
</head>
<body>
    <!-- Sidebar -->
    <div id="sidebar-container"></div>

    <!-- Navbar -->
    <div id="navbar-container"></div>

    <!-- Main Content -->
    <main class="main-content">
        <div class="container-fluid px-4">
            <!-- Page Header -->
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="mb-1">
                        <i class="bi bi-journal-check me-2" style="color: var(--primary-color);"></i>
                        Liquidaciones Procesadas
                    </h2>
                    <p class="text-muted mb-0">Historial de liquidaciones procesadas</p>
                </div>
            </div>

            <!-- Filters -->
            <div class="filter-section">
                <div class="row g-3 align-items-end">
                    <div class="col-md-4">
                        <label for="clientFilter" class="form-label fw-semibold">
                            <i class="bi bi-people me-1"></i>Cliente
                        </label>
                        <select id="clientFilter" class="form-select">
                            <option value="">Todos los clientes</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label for="monthFilter" class="form-label fw-semibold">
                            <i class="bi bi-calendar me-1"></i>Mes
                        </label>
                        <select id="monthFilter" class="form-select">
                            <option value="">Todos los meses</option>
                            <option>ENERO</option>
                            <option>FEBRERO</option>
                            <option>MARZO</option>
                            <option>ABRIL</option>
                            <option>MAYO</option>
                            <option>JUNIO</option>
                            <option>JULIO</option>
                            <option>AGOSTO</option>
                            <option>SEPTIEMBRE</option>
                            <option>OCTUBRE</option>
                            <option>NOVIEMBRE</option>
                            <option>DICIEMBRE</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <button class="btn btn-outline-secondary" onclick="clearFilters()">
                            <i class="bi bi-x-circle me-1"></i>Limpiar Filtros
                        </button>
                    </div>
                </div>
            </div>

            <!-- Loading -->
            <div id="loadingSection" class="text-center py-5">
                <div class="spinner-border text-primary" role="status"></div>
                <p class="mt-2 text-muted">Cargando procesados...</p>
            </div>

            <!-- Header Table -->
            <div id="headerSection" style="display: none;">
                <div class="table-container">
                    <table class="table table-hover mb-0">
                        <thead>
                            <tr>
                                <th><i class="bi bi-hash me-1"></i>Versi&oacute;n</th>
                                <th><i class="bi bi-calendar me-1"></i>Mes / A&ntilde;o</th>
                                <th><i class="bi bi-person me-1"></i>RUT Cliente</th>
                                <th><i class="bi bi-building me-1"></i>Cliente</th>
                                <th><i class="bi bi-file-earmark me-1"></i>Archivo</th>
                                <th><i class="bi bi-clock me-1"></i>Fecha Procesamiento</th>
                                <th class="text-center"><i class="bi bi-gear me-1"></i>Acciones</th>
                            </tr>
                        </thead>
                        <tbody id="headerTableBody">
                        </tbody>
                    </table>
                </div>
                <p class="text-muted mt-2" id="headerCount"></p>
            </div>

            <!-- Empty State -->
            <div id="emptySection" style="display: none;">
                <div class="card">
                    <div class="card-body empty-state">
                        <i class="bi bi-inbox"></i>
                        <h4>No hay liquidaciones procesadas</h4>
                        <p>A&uacute;n no se han procesado liquidaciones. Vaya a Cargas para procesar.</p>
                        <a href="/charges" class="btn btn-primary mt-2">
                            <i class="bi bi-file-earmark-text me-1"></i> Ir a Cargas
                        </a>
                    </div>
                </div>
            </div>

            <!-- Detail Section -->
            <div id="detailSection" class="details-section" style="display: none;">
                <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">
                            <i class="bi bi-list-ul me-2"></i>Detalle - <span id="detailTitle"></span>
                        </h5>
                        <div class="pdf-actions">
                            <button class="btn btn-sm btn-light" id="btnToggleTotals" onclick="toggleTotals()">
                                <i class="bi bi-calculator me-1"></i>Ver Totales
                            </button>
                            <button class="btn btn-sm btn-light" onclick="downloadSelectedPdf()">
                                <i class="bi bi-file-earmark-pdf me-1"></i>Descargar Seleccionados
                            </button>
                            <button class="btn btn-sm btn-light" onclick="downloadAllPdf()">
                                <i class="bi bi-files me-1"></i>Descargar Todo PDF
                            </button>
                        </div>
                    </div>
                    <div class="card-body p-0">
                        <div id="detailLoading" class="text-center py-4" style="display: none;">
                            <div class="spinner-border spinner-border-sm text-primary"></div>
                            <span class="ms-2">Cargando detalles...</span>
                        </div>
                        <div class="details-table-container">
                            <table class="table details-table table-bordered mb-0">
                                <thead>
                                    <tr>
                                        <th><input type="checkbox" id="selectAll" onchange="toggleSelectAll(this)"></th>
                                        <th>RUT</th>
                                        <th>Nombre</th>
                                        <th>Centro Costo</th>
                                        <th>R&eacute;gimen</th>
                                        <th>Sueldo Base</th>
                                        <th>D&iacute;as Trab.</th>
                                        <th>Sueldo Mensual</th>
                                        <th>Gratificaci&oacute;n</th>
                                        <th>Bono Prod.</th>
                                        <th>Horas Extra</th>
                                        <th>Total H.E.</th>
                                        <th><strong>Total Imponible</strong></th>
                                        <th>Colaci&oacute;n</th>
                                        <th>Movilizaci&oacute;n</th>
                                        <th>Desgaste</th>
                                        <th>Cant. Asig. Fam.</th>
                                        <th>Monto Asig. Fam.</th>
                                        <th><strong>Total Haber</strong></th>
                                        <th>Previsi&oacute;n</th>
                                        <th>% Prev.</th>
                                        <th>Valor Prev.</th>
                                        <th>Salud</th>
                                        <th>% Salud</th>
                                        <th>Valor Salud</th>
                                        <th>AFC</th>
                                        <th><strong>Renta L&iacute;q. Imp.</strong></th>
                                        <th>IUT</th>
                                        <th>Pr&eacute;stamos</th>
                                        <th>Anticipo</th>
                                        <th><strong>ALCANCE L&Iacute;QUIDO</strong></th>
                                    </tr>
                                </thead>
                                <tbody id="detailTableBody">
                                </tbody>
                                <tfoot id="detailTableFoot" style="display: none;">
                                </tfoot>
                            </table>
                        </div>
                        <div class="p-3 border-top d-flex justify-content-between align-items-center">
                            <span class="text-muted" id="detailCount"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-6">
                    <span>
                        <i class="bi bi-calculator-fill me-1"></i>
                        <strong>ContaSoft</strong> &copy; 2026 - Sistema de Gesti&oacute;n Contable
                    </span>
                </div>
                <div class="col-md-6 text-md-end">
                    <span>Desarrollado con <i class="bi bi-heart-fill text-danger"></i> por HP Software</span>
                </div>
            </div>
        </div>
    </footer>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

    <!-- Shared modules -->
    <script src="/js/auth.js"></script>
    <script src="/js/sidebar.js"></script>
    <script src="/js/navbar.js"></script>
    <script>
        loadNavbar('procesados');
        loadSidebar('procesados');
    </script>

    <script>
        let allProcessed = [];
        let currentProcessedId = null;
        let currentOriginalId = null;
        let currentDetails = [];
        const preselectedTaxpayerId = '${selectedTaxpayerId}';

        // Format helpers
        function formatMoney(value) {
            if (value === null || value === undefined) return '$0';
            return '$' + Math.round(value).toLocaleString('es-CL');
        }

        function formatPercent(value) {
            if (value === null || value === undefined) return '0%';
            return value.toFixed(2) + '%';
        }

        function formatDate(dateStr) {
            if (!dateStr) return '-';
            const d = new Date(dateStr);
            return d.toLocaleDateString('es-CL') + ' ' + d.toLocaleTimeString('es-CL', {hour: '2-digit', minute: '2-digit'});
        }

        // Load clients for filter
        async function loadClients() {
            try {
                const response = await fetch('/api/ui/clientes', {
                    headers: { 'Accept': 'application/json' }
                });
                const data = await response.json();
                if (data.success && data.data) {
                    const select = document.getElementById('clientFilter');
                    data.data.forEach(function(client) {
                        const opt = document.createElement('option');
                        opt.value = client.id;
                        opt.textContent = client.name + ' - ' + client.rut;
                        select.appendChild(opt);
                    });

                    if (preselectedTaxpayerId) {
                        select.value = preselectedTaxpayerId;
                    }
                }
            } catch (e) {
                console.error('Error loading clients:', e);
            }
        }

        // Load processed records
        async function loadProcessed() {
            document.getElementById('loadingSection').style.display = 'block';
            document.getElementById('headerSection').style.display = 'none';
            document.getElementById('emptySection').style.display = 'none';
            document.getElementById('detailSection').style.display = 'none';

            try {
                let url = '/api/ui/procesados';
                const taxpayerId = document.getElementById('clientFilter').value;
                if (taxpayerId) {
                    url += '?taxpayerId=' + taxpayerId;
                }

                const response = await fetch(url, {
                    headers: { 'Accept': 'application/json' }
                });
                const data = await response.json();

                document.getElementById('loadingSection').style.display = 'none';

                if (data.success && data.data && data.data.length > 0) {
                    allProcessed = data.data;
                    renderHeaderTable(allProcessed);
                    document.getElementById('headerSection').style.display = 'block';
                } else {
                    allProcessed = [];
                    document.getElementById('emptySection').style.display = 'block';
                }
            } catch (e) {
                console.error('Error loading processed:', e);
                document.getElementById('loadingSection').style.display = 'none';
                document.getElementById('emptySection').style.display = 'block';
            }
        }

        // Render header table
        function renderHeaderTable(items) {
            const tbody = document.getElementById('headerTableBody');
            tbody.innerHTML = '';

            const monthFilter = document.getElementById('monthFilter').value;

            let filtered = items;
            if (monthFilter) {
                filtered = items.filter(function(item) { return item.month === monthFilter; });
            }

            if (filtered.length === 0) {
                document.getElementById('headerSection').style.display = 'none';
                document.getElementById('emptySection').style.display = 'block';
                return;
            }

            filtered.forEach(function(item) {
                const row = document.createElement('tr');
                row.setAttribute('data-id', item.id);
                row.setAttribute('data-original-id', item.originalPayBookInstanceId);
                row.onclick = function() { selectRow(this, item.id, item.originalPayBookInstanceId, item.month, item.year); };

                row.innerHTML =
                    '<td><span class="badge bg-primary fs-6">' + item.version + '</span></td>' +
                    '<td><strong>' + (item.month || '-') + '</strong> ' + (item.year || '') + '</td>' +
                    '<td><span class="badge badge-rut">' + (item.taxpayerRut || item.rut || '-') + '</span></td>' +
                    '<td>' + (item.taxpayerName || '-') + '</td>' +
                    '<td><i class="bi bi-file-earmark-spreadsheet text-success me-1"></i>' + (item.fileName || '-') + '</td>' +
                    '<td>' + formatDate(item.created) + '</td>' +
                    '<td class="text-center">' +
                        '<button class="btn btn-outline-danger btn-action" onclick="event.stopPropagation(); downloadPdf(' + item.originalPayBookInstanceId + ')" title="Descargar PDF">' +
                            '<i class="bi bi-file-earmark-pdf"></i>' +
                        '</button>' +
                    '</td>';

                tbody.appendChild(row);
            });

            document.getElementById('headerCount').textContent = 'Total: ' + filtered.length + ' registros procesados';
        }

        // Select a row and load details
        async function selectRow(rowEl, processedId, originalId, month, year) {
            // Highlight row
            document.querySelectorAll('#headerTableBody tr').forEach(function(r) { r.classList.remove('selected'); });
            rowEl.classList.add('selected');

            currentProcessedId = processedId;
            currentOriginalId = originalId;

            // Reset totals when switching rows
            totalsVisible = false;
            document.getElementById('detailTableFoot').style.display = 'none';
            var btnTot = document.getElementById('btnToggleTotals');
            btnTot.classList.remove('btn-warning');
            btnTot.classList.add('btn-light');
            btnTot.innerHTML = '<i class="bi bi-calculator me-1"></i>Ver Totales';

            document.getElementById('detailTitle').textContent = (month || '') + ' ' + (year || '');
            document.getElementById('detailSection').style.display = 'block';
            document.getElementById('detailLoading').style.display = 'block';
            document.getElementById('detailTableBody').innerHTML = '';
            document.getElementById('detailCount').textContent = '';

            try {
                const response = await fetch('/api/ui/procesados/' + processedId + '/details', {
                    headers: { 'Accept': 'application/json' }
                });
                const data = await response.json();

                document.getElementById('detailLoading').style.display = 'none';

                if (data.success && data.data && data.data.length > 0) {
                    currentDetails = data.data;
                    renderDetailTable(data.data);
                    document.getElementById('detailCount').textContent = 'Total: ' + data.data.length + ' empleados';
                } else {
                    currentDetails = [];
                    document.getElementById('detailCount').textContent = 'Sin detalles';
                }
            } catch (e) {
                console.error('Error loading details:', e);
                document.getElementById('detailLoading').style.display = 'none';
                document.getElementById('detailCount').textContent = 'Error al cargar detalles';
            }
        }

        // Render detail table
        function renderDetailTable(details) {
            const tbody = document.getElementById('detailTableBody');
            tbody.innerHTML = '';

            details.forEach(function(item) {
                const colacion = Number(item.colacion || 0);
                const movilizacion = Number(item.movilizacion || 0);
                const desgaste = Number(item.descuentoHerramientas || 0);
                const montoAsigFam = Number(item.totalAsignacionFamiliar || 0);
                const totalHaber = Number(item.totalHaber || 0);

                const dctoPrev = Number(item.valorPrevision || 0) + Number(item.valorSalud || 0);
                const afcValor = Number(item.valorAFC || 0);
                const iut = Number(item.valorIUT || 0);
                const prestamos = Number(item.prestamos || 0);
                const anticipo = Number(item.anticipo || 0);

                const totalDescuentos = dctoPrev + afcValor + iut + prestamos + anticipo;
                const alcanceLiquido = totalHaber - totalDescuentos;

                const row = document.createElement('tr');
                row.innerHTML =
                    '<td><input type="checkbox" class="detail-checkbox" data-rut="' + (item.rut || '') + '"></td>' +
                    '<td><span class="badge badge-rut">' + (item.rut || '-') + '</span></td>' +
                    '<td>' + (item.nombreTrabajador || '-') + '</td>' +
                    '<td>' + (item.centroCosto || '-') + '</td>' +
                    '<td>' + (item.regimen || '-') + '</td>' +
                    '<td class="text-money">' + formatMoney(item.sueldoBase) + '</td>' +
                    '<td class="text-center">' + (item.diasTrabajados || 0) + '</td>' +
                    '<td class="text-money">' + formatMoney(item.sueldoMensual) + '</td>' +
                    '<td class="text-money">' + formatMoney(item.gratificacion) + '</td>' +
                    '<td class="text-money">' + formatMoney(item.bonoProduccion) + '</td>' +
                    '<td class="text-center">' + (item.horasExtra || 0) + '</td>' +
                    '<td class="text-money">' + formatMoney(item.totalHoraExtra) + '</td>' +
                    '<td class="text-money fw-bold">' + formatMoney(item.totalImponible) + '</td>' +
                    '<td class="text-money">' + formatMoney(item.colacion) + '</td>' +
                    '<td class="text-money">' + formatMoney(item.movilizacion) + '</td>' +
                    '<td class="text-money">' + formatMoney(item.descuentoHerramientas) + '</td>' +
                    '<td class="text-center">' + (item.asignacionFamiliar || 0) + '</td>' +
                    '<td class="text-money">' + formatMoney(montoAsigFam) + '</td>' +
                    '<td class="text-money fw-bold">' + formatMoney(item.totalHaber) + '</td>' +
                    '<td>' + (item.prevision || '-') + '</td>' +
                    '<td class="text-center">' + formatPercent(item.porcentajePrevision) + '</td>' +
                    '<td class="text-money">' + formatMoney(item.valorPrevision) + '</td>' +
                    '<td>' + (item.salud || '-') + '</td>' +
                    '<td class="text-center">' + formatPercent(item.saludPorcentaje) + '</td>' +
                    '<td class="text-money">' + formatMoney(item.valorSalud) + '</td>' +
                    '<td class="text-money">' + formatMoney(item.valorAFC) + '</td>' +
                    '<td class="text-money fw-bold">' + formatMoney(item.rentaLiquidaImponible) + '</td>' +
                    '<td class="text-money">' + formatMoney(item.valorIUT) + '</td>' +
                    '<td class="text-money">' + formatMoney(prestamos) + '</td>' +
                    '<td class="text-money">' + formatMoney(anticipo) + '</td>' +
                    '<td class="text-money fw-bold">' + formatMoney(alcanceLiquido) + '</td>';

                tbody.appendChild(row);
            });
        }

        // Select all checkboxes
        function toggleSelectAll(checkbox) {
            document.querySelectorAll('.detail-checkbox').forEach(function(cb) {
                cb.checked = checkbox.checked;
            });
        }

        // Toggle totals row
        let totalsVisible = false;
        function toggleTotals() {
            totalsVisible = !totalsVisible;
            const tfoot = document.getElementById('detailTableFoot');
            const btn = document.getElementById('btnToggleTotals');
            if (totalsVisible && currentDetails.length > 0) {
                renderTotalsRow(currentDetails);
                tfoot.style.display = '';
                btn.classList.remove('btn-light');
                btn.classList.add('btn-warning');
                btn.innerHTML = '<i class="bi bi-calculator me-1"></i>Ocultar Totales';
            } else {
                tfoot.style.display = 'none';
                btn.classList.remove('btn-warning');
                btn.classList.add('btn-light');
                btn.innerHTML = '<i class="bi bi-calculator me-1"></i>Ver Totales';
            }
        }

        function renderTotalsRow(details) {
            var t = {
                sueldoBase: 0, sueldoMensual: 0, gratificacion: 0, bonoProduccion: 0,
                totalHoraExtra: 0, totalImponible: 0, colacion: 0, movilizacion: 0,
                descuentoHerramientas: 0, totalAsignacionFamiliar: 0, totalHaber: 0,
                valorPrevision: 0, valorSalud: 0, valorAFC: 0, rentaLiquidaImponible: 0,
                valorIUT: 0, prestamos: 0, anticipo: 0, alcanceLiquido: 0
            };
            details.forEach(function(item) {
                t.sueldoBase += Number(item.sueldoBase || 0);
                t.sueldoMensual += Number(item.sueldoMensual || 0);
                t.gratificacion += Number(item.gratificacion || 0);
                t.bonoProduccion += Number(item.bonoProduccion || 0);
                t.totalHoraExtra += Number(item.totalHoraExtra || 0);
                t.totalImponible += Number(item.totalImponible || 0);
                t.colacion += Number(item.colacion || 0);
                t.movilizacion += Number(item.movilizacion || 0);
                t.descuentoHerramientas += Number(item.descuentoHerramientas || 0);
                t.totalAsignacionFamiliar += Number(item.totalAsignacionFamiliar || 0);
                t.totalHaber += Number(item.totalHaber || 0);
                t.valorPrevision += Number(item.valorPrevision || 0);
                t.valorSalud += Number(item.valorSalud || 0);
                t.valorAFC += Number(item.valorAFC || 0);
                t.rentaLiquidaImponible += Number(item.rentaLiquidaImponible || 0);
                t.valorIUT += Number(item.valorIUT || 0);
                t.prestamos += Number(item.prestamos || 0);
                t.anticipo += Number(item.anticipo || 0);
                var dctoPrev = Number(item.valorPrevision || 0) + Number(item.valorSalud || 0);
                var afcValor = Number(item.valorAFC || 0);
                var iut = Number(item.valorIUT || 0);
                var prest = Number(item.prestamos || 0);
                var antic = Number(item.anticipo || 0);
                t.alcanceLiquido += Number(item.totalHaber || 0) - (dctoPrev + afcValor + iut + prest + antic);
            });
            var tfoot = document.getElementById('detailTableFoot');
            tfoot.innerHTML = '<tr>' +
                '<td></td>' +
                '<td><strong>TOTALES</strong></td>' +
                '<td></td>' +
                '<td></td>' +
                '<td class="text-money">' + formatMoney(t.sueldoBase) + '</td>' +
                '<td></td>' +
                '<td class="text-money">' + formatMoney(t.sueldoMensual) + '</td>' +
                '<td class="text-money">' + formatMoney(t.gratificacion) + '</td>' +
                '<td class="text-money">' + formatMoney(t.bonoProduccion) + '</td>' +
                '<td></td>' +
                '<td class="text-money">' + formatMoney(t.totalHoraExtra) + '</td>' +
                '<td class="text-money">' + formatMoney(t.totalImponible) + '</td>' +
                '<td class="text-money">' + formatMoney(t.colacion) + '</td>' +
                '<td class="text-money">' + formatMoney(t.movilizacion) + '</td>' +
                '<td class="text-money">' + formatMoney(t.descuentoHerramientas) + '</td>' +
                '<td></td>' +
                '<td class="text-money">' + formatMoney(t.totalAsignacionFamiliar) + '</td>' +
                '<td class="text-money">' + formatMoney(t.totalHaber) + '</td>' +
                '<td></td>' +
                '<td></td>' +
                '<td class="text-money">' + formatMoney(t.valorPrevision) + '</td>' +
                '<td></td>' +
                '<td></td>' +
                '<td class="text-money">' + formatMoney(t.valorSalud) + '</td>' +
                '<td class="text-money">' + formatMoney(t.valorAFC) + '</td>' +
                '<td class="text-money">' + formatMoney(t.rentaLiquidaImponible) + '</td>' +
                '<td class="text-money">' + formatMoney(t.valorIUT) + '</td>' +
                '<td class="text-money">' + formatMoney(t.prestamos) + '</td>' +
                '<td class="text-money">' + formatMoney(t.anticipo) + '</td>' +
                '<td class="text-money">' + formatMoney(t.alcanceLiquido) + '</td>' +
                '</tr>';
        }

        // Download PDF for the original PayBookInstance
        function downloadPdf(originalId) {
            fetch('/api/reports/paybook/' + originalId + '/pdf')
                .then(function(response) { return response.blob(); })
                .then(function(blob) {
                    const url = window.URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = 'liquidacion-procesada-' + originalId + '.pdf';
                    a.click();
                    window.URL.revokeObjectURL(url);
                })
                .catch(function(error) {
                    console.error('Error downloading PDF:', error);
                    alert('Error al descargar el PDF');
                });
        }

        // Download all PDF
        function downloadAllPdf() {
            if (!currentOriginalId) {
                alert('Seleccione una liquidaci\u00f3n primero');
                return;
            }
            downloadPdf(currentOriginalId);
        }

        // Download selected PDF filtered by selected employee RUTs
        function downloadSelectedPdf() {
            const checked = document.querySelectorAll('.detail-checkbox:checked');
            if (checked.length === 0) {
                alert('Seleccione al menos un empleado');
                return;
            }
            if (!currentOriginalId) {
                alert('Seleccione una liquidaci\u00f3n primero');
                return;
            }
            const ruts = Array.from(checked).map(function(cb) { return cb.dataset.rut; });
            const rutsParam = '?ruts=' + ruts.join(',');
            fetch('/api/reports/paybook/' + currentOriginalId + '/pdf' + rutsParam)
                .then(function(response) { return response.blob(); })
                .then(function(blob) {
                    const url = window.URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = 'liquidacion-seleccionados-' + currentOriginalId + '.pdf';
                    a.click();
                    window.URL.revokeObjectURL(url);
                })
                .catch(function(error) {
                    console.error('Error downloading PDF:', error);
                    alert('Error al descargar el PDF');
                });
        }

        // Filter handlers
        document.getElementById('clientFilter').addEventListener('change', function() {
            loadProcessed();
        });

        document.getElementById('monthFilter').addEventListener('change', function() {
            renderHeaderTable(allProcessed);
            document.getElementById('detailSection').style.display = 'none';
        });

        function clearFilters() {
            document.getElementById('clientFilter').value = '';
            document.getElementById('monthFilter').value = '';
            loadProcessed();
        }

        // Initialize
        document.addEventListener('DOMContentLoaded', async function() {
            await loadClients();
            await loadProcessed();
        });
    </script>
</body>
</html>
