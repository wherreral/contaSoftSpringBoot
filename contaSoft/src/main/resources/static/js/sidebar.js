function loadSidebar(activeItem) {
    const userRole = parseInt(localStorage.getItem('userRole') || '2');
    const isAdmin = (userRole === 1);

    const adminSection = isAdmin ? `
                <div class="sidebar-section-title mt-3">Administraci&oacute;n</div>
                <a href="/usuarios" class="sidebar-menu-item ${activeItem === 'usuarios' ? 'active' : ''}">
                    <i class="bi bi-person-gear"></i>Usuarios
                </a>
                <a href="/configuracion" class="sidebar-menu-item ${activeItem === 'configuracion' ? 'active' : ''}">
                    <i class="bi bi-gear-fill"></i>Configuraci&oacute;n
                </a>
    ` : '';

    const sidebarHTML = `
        <div class="sidebar" id="sidebar">
            <div class="sidebar-header">
                <button class="sidebar-close" id="sidebarClose">
                    <i class="bi bi-x-lg"></i>
                </button>
                <h4>
                    <i class="bi bi-calculator-fill me-2"></i>
                    ContaSoft
                </h4>
                <small>Sistema de Gesti&oacute;n</small>
            </div>
            <div class="sidebar-menu">
                <div class="sidebar-section-title">Gesti&oacute;n Principal</div>
                <a href="/" class="sidebar-menu-item ${activeItem === 'inicio' ? 'active' : ''}">
                    <i class="bi bi-house-fill"></i>Inicio
                </a>
                <a href="/clientes" class="sidebar-menu-item ${activeItem === 'clientes' ? 'active' : ''}">
                    <i class="bi bi-people-fill"></i>Clientes
                </a>
                <a href="/sucursales" class="sidebar-menu-item ${activeItem === 'sucursales' ? 'active' : ''}">
                    <i class="bi bi-building"></i>Sucursales
                </a>
                <a href="/templates" class="sidebar-menu-item ${activeItem === 'templates' ? 'active' : ''}">
                    <i class="bi bi-file-earmark-text-fill"></i>Templates
                </a>
                <a href="/procesados" class="sidebar-menu-item ${activeItem === 'procesados' ? 'active' : ''}">
                    <i class="bi bi-journal-check"></i>Procesados
                </a>
                <div class="sidebar-section-title mt-3">Reportes y Datos</div>
                <a href="/charges" class="sidebar-menu-item ${activeItem === 'charges' ? 'active' : ''}">
                    <i class="bi bi-file-earmark-text"></i>Cargas
                </a>
                <a href="/importar" class="sidebar-menu-item ${activeItem === 'importar' ? 'active' : ''}">
                    <i class="bi bi-upload"></i>Importar Datos
                </a>
                ${adminSection}
                <div class="sidebar-section-title mt-3">Sistema</div>
                <a href="/ayuda" class="sidebar-menu-item ${activeItem === 'ayuda' ? 'active' : ''}">
                    <i class="bi bi-question-circle-fill"></i>Ayuda
                </a>
            </div>
        </div>
        <div class="sidebar-overlay" id="sidebarOverlay"></div>
    `;

    document.getElementById('sidebar-container').innerHTML = sidebarHTML;

    // Add event listeners for sidebar toggle
    const sidebar = document.getElementById('sidebar');
    const sidebarOverlay = document.getElementById('sidebarOverlay');
    const sidebarClose = document.getElementById('sidebarClose');
    const navbarMenuBtn = document.getElementById('navbarMenuBtn');

    function toggleSidebar() {
        sidebar.classList.toggle('active');
        sidebarOverlay.classList.toggle('active');
    }

    if (navbarMenuBtn) navbarMenuBtn.addEventListener('click', toggleSidebar);
    sidebarClose.addEventListener('click', toggleSidebar);
    sidebarOverlay.addEventListener('click', toggleSidebar);
}
