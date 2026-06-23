/* =========================================
   INICIALIZACIÓN Y CARGA DE MÓDULOS
========================================= */
document.addEventListener('DOMContentLoaded', () => {

    // 1. CARGAR LOS INCLUDES CON RUTAS ABSOLUTAS DEL SERVIDOR LOCAL
    const loadIncludes = Promise.all([
        fetch('/includes/header.html').then(res => res.text()).then(data => {
            document.getElementById('header-container').innerHTML = data;
        }),
        fetch('/includes/menu.html').then(res => res.text()).then(data => {
            document.getElementById('menu-container').innerHTML = data;
        }),
        fetch('/includes/footer.html').then(res => res.text()).then(data => {
            document.getElementById('footer-container').innerHTML = data;
        })
    ]);

    // 2. ACTIVAR FUNCIONES DEL MENÚ
    loadIncludes.then(() => {
        const btnToggle = document.getElementById('btn-toggle-menu');
        const sidebar = document.querySelector('.main-sidebar');
        const mainContent = document.querySelector('.main-content');

        // Lógica de apertura y cierre
        if (btnToggle && sidebar && mainContent) {
            btnToggle.addEventListener('click', () => {
                sidebar.classList.toggle('oculto');
                mainContent.classList.toggle('expandido');
            });
        }

        // Resaltar automáticamente la opción del menú actual según la URL
        const currentPath = window.location.pathname;
        document.querySelectorAll('.menu-item').forEach(link => {
            if (link.getAttribute('href') === currentPath) {
                link.classList.add('active');
            } else {
                link.classList.remove('active');
            }
        });

    }).catch(error => console.error("Error cargando los includes:", error));

    // =========================================
    // INICIALIZACIÓN DE GRÁFICOS (Chart.js)
    // =========================================
    const barCanvas = document.getElementById('barChart');
    if (barCanvas) { 
        const ctxBar = barCanvas.getContext('2d');
        new Chart(ctxBar, {
            type: 'bar',
            data: {
                labels: ['[dia]', '[dia]', '[dia]', '[dia]', '[dia]', '[dia]', '[dia]'],
                datasets: [{
                    label: 'Ventas (S/)',
                    data: [800, 1200, 1600, 1050, 1700, 900, 1450],
                    backgroundColor: '#8a1529',
                    borderRadius: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: { y: { beginAtZero: true } },
                plugins: { legend: { display: false } }
            }
        });
    }

    const pieCanvas = document.getElementById('pieChart');
    if (pieCanvas) {
        const ctxPie = pieCanvas.getContext('2d');
        new Chart(ctxPie, {
            type: 'doughnut',
            data: {
                labels: ['[categoria]', '[categoria]', '[categoria]', '[categoria]', '[categoria]'],
                datasets: [{
                    data: [45, 25, 15, 10, 5],
                    backgroundColor: ['#8a1529', '#f39c12', '#27ae60', '#2980b9', '#8e44ad'],
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { position: 'right', labels: { boxWidth: 12 } }
                }
            }
        });
    }
// =========================================
    // LÓGICA VISTA: CONFIRMAR VENTA (cv-)
    // =========================================
    
    // Toggles de botones simples (Ej: Cliente registrado / ocasional)
    const toggleBtns = document.querySelectorAll('.cv-toggle-btn');
    toggleBtns.forEach(btn => {
        btn.addEventListener('click', (e) => {
            // Busca a sus hermanos en el mismo grupo y les quita la clase active
            const siblings = e.target.parentElement.querySelectorAll('.cv-toggle-btn');
            siblings.forEach(s => s.classList.remove('active'));
            // Añade active al botón clickeado
            e.target.classList.add('active');
        });
    });

    // Cajas de selección grandes (Ej: Boleta / Factura)
    const docBoxes = document.querySelectorAll('.cv-doc-box');
    docBoxes.forEach(box => {
        box.addEventListener('click', (e) => {
            // Prevenir errores si se hace clic en el icono interno en lugar de la caja
            const targetBox = e.target.closest('.cv-doc-box');
            
            const siblings = targetBox.parentElement.querySelectorAll('.cv-doc-box');
            siblings.forEach(s => {
                s.classList.remove('active');
                // Cambiar icono a circulo vacío
                const icon = s.querySelector('.cv-radio-icon');
                if (icon) {
                    icon.classList.remove('fa-circle-dot');
                    icon.classList.add('fa-circle');
                }
            });
            
            targetBox.classList.add('active');
            // Cambiar icono a circulo lleno
            const activeIcon = targetBox.querySelector('.cv-radio-icon');
            if (activeIcon) {
                activeIcon.classList.remove('fa-circle');
                activeIcon.classList.add('fa-circle-dot');
            }
        });
    });
    // =========================================
    // LÓGICA VISTA: HISTORIAL (hist-)
    // =========================================
    
    const panelDetalle = document.getElementById('histDetailPanel');
    const btnCloseDetalle = document.getElementById('btnCloseDetail');
    const btnsVerDetalle = document.querySelectorAll('.hist-btn-view');

    // Solo ejecuta si estamos en la pantalla de historial
    if (panelDetalle && btnCloseDetalle && btnsVerDetalle.length > 0) {
        
        // Al hacer clic en la "X", cierra el panel
        btnCloseDetalle.addEventListener('click', () => {
            panelDetalle.classList.remove('active');
        });

        // Al hacer clic en el ojito de cualquier fila, abre el panel
        btnsVerDetalle.forEach(btn => {
            btn.addEventListener('click', () => {
                panelDetalle.classList.add('active');
            });
        });
    }
    // =========================================
    // LÓGICA VISTA: PRODUCTOS E INVENTARIO (prod-)
    // =========================================

    // Panel Lateral: Nuevo Producto
    const btnNuevoProducto = document.getElementById('btnNuevoProducto');
    const panelNuevoProducto = document.getElementById('panelNuevoProducto');
    const closeNuevoProducto = document.getElementById('closeNuevoProducto');

    if (btnNuevoProducto && panelNuevoProducto && closeNuevoProducto) {
        btnNuevoProducto.addEventListener('click', () => panelNuevoProducto.classList.add('active'));
        closeNuevoProducto.addEventListener('click', () => panelNuevoProducto.classList.remove('active'));
    }

    // Panel Inferior: Cargar Stock
    const btnCargarStock = document.getElementById('btnCargarStock');
    const panelCargarStock = document.getElementById('panelCargarStock');
    const closeCargarStock = document.getElementById('closeCargarStock');

    if (btnCargarStock && panelCargarStock && closeCargarStock) {
        btnCargarStock.addEventListener('click', () => panelCargarStock.classList.add('active'));
        closeCargarStock.addEventListener('click', () => panelCargarStock.classList.remove('active'));
    }

    // Panel Inferior: Registrar Merma
    const btnRegistrarMerma = document.getElementById('btnRegistrarMerma');
    const panelRegistrarMerma = document.getElementById('panelRegistrarMerma');
    const closeRegistrarMerma = document.getElementById('closeRegistrarMerma');

    if (btnRegistrarMerma && panelRegistrarMerma && closeRegistrarMerma) {
        btnRegistrarMerma.addEventListener('click', () => panelRegistrarMerma.classList.add('active'));
        closeRegistrarMerma.addEventListener('click', () => panelRegistrarMerma.classList.remove('active'));
    }
    // =========================================
    // LÓGICA VISTA: CLIENTES (cli-)
    // =========================================

    // Panel Lateral (Nuevo/Editar Cliente)
    const panelCliente = document.getElementById('panelClienteForm');
    const btnNuevoCliente = document.getElementById('btnNuevoCliente');
    const closeClienteForm = document.getElementById('closeClienteForm');
    const cancelClienteForm = document.getElementById('cancelClienteForm');
    const btnsEditarCliente = document.querySelectorAll('.btn-editar-cliente');

    if (panelCliente) {
        // Abrir panel (Botón Nuevo)
        if (btnNuevoCliente) btnNuevoCliente.addEventListener('click', () => panelCliente.classList.add('active'));
        
        // Abrir panel (Botones Editar en la tabla)
        btnsEditarCliente.forEach(btn => {
            btn.addEventListener('click', () => panelCliente.classList.add('active'));
        });

        // Cerrar panel
        if (closeClienteForm) closeClienteForm.addEventListener('click', () => panelCliente.classList.remove('active'));
        if (cancelClienteForm) cancelClienteForm.addEventListener('click', () => panelCliente.classList.remove('active'));
    }

    // Panel Inferior (Historial del Cliente)
    const panelHistorial = document.getElementById('panelHistorialCliente');
    const closeHistorial = document.getElementById('closeHistorialCliente');
    const btnsVerHistorial = document.querySelectorAll('.btn-historial-cliente');

    if (panelHistorial && closeHistorial) {
        // Abrir historial (Iconos de reloj en la tabla)
        btnsVerHistorial.forEach(btn => {
            btn.addEventListener('click', () => {
                panelHistorial.classList.add('active');
                // Opcional: hacer un pequeño scroll hacia abajo para asegurar que el usuario lo vea
                setTimeout(() => {
                    panelHistorial.scrollIntoView({ behavior: 'smooth', block: 'end' });
                }, 100);
            });
        });

        // Cerrar historial
        closeHistorial.addEventListener('click', () => panelHistorial.classList.remove('active'));
    }
    // =========================================
    // LÓGICA VISTA: REPORTES (rep-)
    // =========================================

    // 1. Interacción de Pestañas (Tabs)
    const repTabs = document.querySelectorAll('.rep-tab');
    if (repTabs.length > 0) {
        repTabs.forEach(tab => {
            tab.addEventListener('click', (e) => {
                // Quita la clase active de todas
                repTabs.forEach(t => t.classList.remove('active'));
                // Añade active a la clickeada
                e.currentTarget.classList.add('active');
                
                // NOTA: Para este prototipo, todas las tablas están visibles.
                // En un desarrollo completo, aquí ocultarías/mostrarías los section correspondientes.
            });
        });
    }

    // 2. Gráfico "Ventas vs Mermas"
    const repCanvas = document.getElementById('repBarChart');
    if (repCanvas) {
        const ctxRep = repCanvas.getContext('2d');
        new Chart(ctxRep, {
            type: 'bar',
            data: {
                labels: ['Ventas', 'Mermas'],
                datasets: [{
                    label: 'Monto (S/)',
                    data: [8524.70, 61.40], // Datos simulados basados en la imagen
                    backgroundColor: [
                        '#218838', // Verde para ventas
                        '#c62828'  // Rojo para mermas
                    ],
                    borderRadius: 4,
                    barPercentage: 0.5 // Hace las barras más delgadas
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false }, // Oculta la leyenda superior
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return 'S/ ' + context.raw.toFixed(2);
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return (value / 1000) + 'K'; // Formato "10K", "8K"
                            }
                        }
                    }
                }
            }
        });
    }
    // =========================================
    // LÓGICA VISTA: CONFIGURACIÓN (conf-)
    // =========================================

    // Panel Lateral (Nuevo Usuario)
    const panelUsuario = document.getElementById('panelNuevoUsuario');
    const btnNuevoUsuario = document.getElementById('btnNuevoUsuario');
    const closeNuevoUsuario = document.getElementById('closeNuevoUsuario');
    const cancelNuevoUsuario = document.getElementById('cancelNuevoUsuario');

    if (panelUsuario && btnNuevoUsuario) {
        // Abrir panel
        btnNuevoUsuario.addEventListener('click', () => panelUsuario.classList.add('active'));
        
        // Cerrar panel
        if (closeNuevoUsuario) closeNuevoUsuario.addEventListener('click', () => panelUsuario.classList.remove('active'));
        if (cancelNuevoUsuario) cancelNuevoUsuario.addEventListener('click', () => panelUsuario.classList.remove('active'));
    }

    // Toggle Mostrar/Ocultar Contraseña
    const togglePassword = document.getElementById('confTogglePassword');
    const passwordInput = document.getElementById('confPasswordInput');

    if (togglePassword && passwordInput) {
        togglePassword.addEventListener('click', () => {
            // Cambia el tipo de input
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            
            // Cambia el icono
            togglePassword.classList.toggle('fa-eye');
            togglePassword.classList.toggle('fa-eye-slash');
        });
    }
});