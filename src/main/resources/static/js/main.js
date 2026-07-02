/* =========================================
   INICIALIZACIÓN Y CARGA DE MÓDULOS
========================================= */
document.addEventListener('DOMContentLoaded', () => {

    // 1. CARGAR LOS INCLUDES CON RUTAS ABSOLUTAS DEL SERVIDOR LOCAL
    const loadIncludes = Promise.all([
        fetch('/includes/header.html').then(res => res.text()).then(data => {
            const el = document.getElementById('header-container');
            if (el) el.innerHTML = data;
        }),
        fetch('/includes/menu.html').then(res => res.text()).then(data => {
            const el = document.getElementById('menu-container');
            if (el) el.innerHTML = data;
        }),
        fetch('/includes/footer.html').then(res => res.text()).then(data => {
            const el = document.getElementById('footer-container');
            if (el) el.innerHTML = data;
        })
    ]);

    // 2. ACTIVAR FUNCIONES DEL MENÚ Y HEADER
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

        // Estado de la Caja
        window.cajaEstaAbierta = false; // Variable global para usar en logout
        fetch('/api/movimientoscaja')
            .then(res => res.json())
            .then(data => {
                const isOpen = data.some(m => !m.fhCierre);
                window.cajaEstaAbierta = isOpen; // Actualizamos el estado

                // --- LÓGICA DE PERMISOS Y CAJA ---
                const userStr = localStorage.getItem('user');
                if (userStr) {
                    const user = JSON.parse(userStr);

                    if (user.permisos) {
                        document.querySelectorAll('.menu-item').forEach(link => {
                            const mod = link.getAttribute('data-module');
                            
                            // 1. Ocultar si no tiene permiso
                            let hasPermission = mod === 'dashboard' || user.permisos[mod] !== false;
                            
                            // 2. Regla especial: Cajero (idrol 3) con caja cerrada
                            if (user.idrol === 3 && !isOpen) {
                                if (mod && mod !== 'dashboard' && mod !== 'modCaja') {
                                    hasPermission = false;
                                }
                            }
                            
                            // 3. Regla especial: Anomalías solo para Admin (idrol 1)
                            if (mod === 'modAnomalias' && user.idrol !== 1) {
                                hasPermission = false;
                            }

                            if (!hasPermission) {
                                link.parentElement.style.display = 'none';
                            }
                        });

                        // Protección de ruta (Frontend)
                        const currentMenu = document.querySelector(`.menu-item[href="${currentPath}"]`);
                        if (currentMenu) {
                            const mod = currentMenu.getAttribute('data-module');
                            let allowed = mod === 'dashboard' || user.permisos[mod] !== false;
                            
                            if (user.idrol === 3 && !isOpen && mod && mod !== 'dashboard' && mod !== 'modCaja') {
                                allowed = false;
                            }
                            
                            if (mod === 'modAnomalias' && user.idrol !== 1) {
                                allowed = false;
                            }

                            if (!allowed) {
                                if (user.idrol === 3 && !isOpen) {
                                    alert('Debes abrir la caja antes de realizar ventas u otras acciones.');
                                    window.location.href = '/caja/index.html';
                                } else {
                                    alert('No tienes permiso para acceder a este módulo.');
                                    window.location.href = '/dashboard.html';
                                }
                            }
                        }
                    }

                    const userNameEl = document.getElementById('header-user-name');
                    const userRoleEl = document.getElementById('header-user-role');
                    if (userNameEl) userNameEl.innerText = user.nombreCompleto || (user.nombre + ' ' + user.apellido);
                    if (userRoleEl) userRoleEl.innerText = user.rol || 'Vendedor';
                    
                    // --- RELLENAR VENDEDOR EN NUEVA VENTA (CONFIRMACIÓN) ---
                    const confirmVendedorDisplay = document.getElementById('confirmVendedorDisplay');
                    const confirmVendedorSelect = document.getElementById('confirmVendedorSelect');
                    if (confirmVendedorDisplay && confirmVendedorSelect) {
                        confirmVendedorDisplay.value = user.nombreCompleto || (user.nombre + ' ' + user.apellido);
                        confirmVendedorSelect.value = user.idusuario;
                    }

                    // --- RELLENAR CAJERO EN APERTURA DE CAJA ---
                    const selectCajero = document.getElementById('selectCajero');
                    if (selectCajero) {
                        selectCajero.value = user.idusuario;
                    }

                    // --- ANOMALIAS NOTIFICATION (SOLO ADMIN) ---
                    if (user.idrol === 1) {
                        const anomaliaBell = document.getElementById('header-anomalia-bell');
                        if (anomaliaBell) {
                            anomaliaBell.style.display = 'inline-block';
                            checkAnomalias();
                            
                            // Check every 60 seconds
                            setInterval(checkAnomalias, 60000);
                            
                            // Listen for custom event from anomalias/index.html
                            document.addEventListener('anomaliasActualizadas', checkAnomalias);
                        }
                    }
                    
                    function checkAnomalias() {
                        fetch('/api/anomalias/no-leidas')
                            .then(res => res.json())
                            .then(data => {
                                const badge = document.getElementById('header-anomalia-badge');
                                if (badge) {
                                    if (data && data.length > 0) {
                                        badge.innerText = data.length > 99 ? '99+' : data.length;
                                        badge.style.display = 'block';
                                    } else {
                                        badge.style.display = 'none';
                                    }
                                }
                            })
                            .catch(err => console.error("Error check anomalias:", err));
                    }
                }
                // ---------------------------------

                const icon = document.getElementById('header-caja-icon');
                const title = document.getElementById('header-caja-title');
                const msg = document.getElementById('header-caja-msg');
                const container = document.getElementById('header-caja-container');

                if (icon && title && msg && container) {
                    if (isOpen) {
                        icon.className = 'fa-solid fa-lock-open';
                        title.innerText = 'CAJA ABIERTA';
                        msg.innerText = 'Listo para ventas';
                        container.style.color = '#2e7d32';
                        icon.style.color = '#2e7d32';
                    } else {
                        icon.className = 'fa-solid fa-lock';
                        title.innerText = 'CAJA CERRADA';
                        msg.innerText = 'Apertura requerida';
                        container.style.color = '#c62828';
                        icon.style.color = '#c62828';
                    }
                }

                // Mostrar alerta bonita si está en nueva venta y la caja está cerrada
                if (window.location.pathname.includes('/nuevaventa') && !isOpen) {
                    mostrarAlertaCajaCerrada();
                }

                // Fecha actual
                const now = new Date();
                const dateOptions = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
                const datetimeEl = document.getElementById('header-datetime');
                if (datetimeEl) datetimeEl.innerText = now.toLocaleDateString('es-ES', dateOptions);

                // Lógica de Logout
                const btnLogout = document.getElementById('btn-logout');
                if (btnLogout) {
                    btnLogout.addEventListener('click', () => {
                        const userStr = localStorage.getItem('user');
                        const user = JSON.parse(userStr || '{}');
                        
                        if (user && user.idusuario) {
                            fetch(`/api/usuarios/logout/${user.idusuario}`, {
                                method: 'POST'
                            })
                            .then(async res => {
                                if (!res.ok) {
                                    const errorText = await res.text();
                                    throw new Error(errorText);
                                }
                                localStorage.removeItem('user');
                                window.location.href = '/login.html';
                            })
                            .catch(err => {
                                alert(err.message);
                            });
                        } else {
                            localStorage.removeItem('user');
                            window.location.href = '/login.html';
                        }
                    });
                }
            })
            .catch(err => {
                console.error("Error al obtener estado de caja", err);
                const title = document.getElementById('header-caja-title');
                const icon = document.getElementById('header-caja-icon');
                if (title) title.innerText = 'Estado Desconocido';
                if (icon) icon.className = 'fa-solid fa-circle-question';
            });

    }).catch(error => console.error("Error cargando los includes:", error));

    function mostrarAlertaCajaCerrada() {
        // Verificar si ya existe la alerta
        if (document.getElementById('cajaCerradaOverlay')) return;
        
        const overlay = document.createElement('div');
        overlay.id = 'cajaCerradaOverlay';
        overlay.style.position = 'fixed';
        overlay.style.top = '0';
        overlay.style.left = '0';
        overlay.style.width = '100vw';
        overlay.style.height = '100vh';
        overlay.style.backgroundColor = 'rgba(0, 0, 0, 0.6)';
        overlay.style.display = 'flex';
        overlay.style.alignItems = 'center';
        overlay.style.justifyContent = 'center';
        overlay.style.zIndex = '9999';
        
        const modal = document.createElement('div');
        modal.style.backgroundColor = '#fff';
        modal.style.padding = '30px 40px';
        modal.style.borderRadius = '12px';
        modal.style.boxShadow = '0 10px 25px rgba(0,0,0,0.2)';
        modal.style.textAlign = 'center';
        modal.style.maxWidth = '400px';
        modal.style.animation = 'scaleUp 0.3s ease-out forwards';
        
        // Estilos para la animación
        const style = document.createElement('style');
        style.innerHTML = `@keyframes scaleUp { from { transform: scale(0.8); opacity: 0; } to { transform: scale(1); opacity: 1; } }`;
        document.head.appendChild(style);
        
        modal.innerHTML = `
            <i class="fa-solid fa-triangle-exclamation" style="font-size: 3rem; color: #f39c12; margin-bottom: 15px;"></i>
            <h2 style="margin: 0 0 10px 0; color: #333; font-size: 1.5rem;">Caja Cerrada</h2>
            <p style="color: #666; margin-bottom: 25px; line-height: 1.5;">No puedes realizar ventas con la caja cerrada. Por favor, abre una nueva caja para continuar.</p>
            <div style="display: flex; gap: 15px; justify-content: center;">
                <button id="btnIrACaja" style="background-color: var(--color-primario); color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: bold; transition: all 0.2s;">
                    <i class="fa-solid fa-cash-register"></i> Abrir Caja
                </button>
                <button id="btnCerrarModalCaja" style="background-color: #e0e0e0; color: #333; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: bold; transition: all 0.2s;">
                    Cancelar
                </button>
            </div>
        `;
        
        overlay.appendChild(modal);
        document.body.appendChild(overlay);
        
        document.getElementById('btnIrACaja').addEventListener('click', () => {
            window.location.href = '/caja';
        });
        
        document.getElementById('btnCerrarModalCaja').addEventListener('click', () => {
            document.body.removeChild(overlay);
        });
    }

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
    // =========================================
    // INICIALIZACIÓN DE GRÁFICO PREDICTIVO IA
    // =========================================
    const predCanvas = document.getElementById('prediccionChart');
    if (predCanvas) {
        const ctxPred = predCanvas.getContext('2d');
        
        // Fetch data from Spring Boot which talks to Python Microservice
        fetch('/api/reportes/prediccion')
            .then(res => res.json())
            .then(data => {
                const historico = data.historico || [];
                const prediccion = data.prediccion || [];
                
                // Combine labels
                const labels = [];
                const historicoData = [];
                const prediccionData = [];
                
                historico.forEach(item => {
                    labels.push(item.fecha);
                    historicoData.push(item.ingresos);
                    prediccionData.push(null);
                });
                
                // Connect the last historical point with the first prediction point
                if (historico.length > 0 && prediccion.length > 0) {
                    const lastHist = historico[historico.length - 1];
                    prediccionData[prediccionData.length - 1] = lastHist.ingresos;
                }
                
                prediccion.forEach(item => {
                    labels.push(item.fecha);
                    historicoData.push(null);
                    prediccionData.push(item.prediccion);
                });
                
                new Chart(ctxPred, {
                    type: 'line',
                    data: {
                        labels: labels,
                        datasets: [
                            {
                                label: 'Ventas Históricas (S/)',
                                data: historicoData,
                                borderColor: '#2e7d32',
                                backgroundColor: 'rgba(46, 125, 50, 0.1)',
                                fill: true,
                                tension: 0.1,
                                borderWidth: 2,
                                pointRadius: 2
                            },
                            {
                                label: 'Predicción IA (S/)',
                                data: prediccionData,
                                borderColor: '#1976d2',
                                borderDash: [5, 5],
                                backgroundColor: 'transparent',
                                fill: false,
                                tension: 0.1,
                                borderWidth: 2,
                                pointRadius: 0
                            }
                        ]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        interaction: {
                            mode: 'index',
                            intersect: false,
                        },
                        scales: {
                            x: {
                                ticks: { maxTicksLimit: 15 } // Avoid crowding x axis
                            },
                            y: { 
                                beginAtZero: true 
                            }
                        },
                        plugins: {
                            legend: { position: 'top' },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        let label = context.dataset.label || '';
                                        if (label) {
                                            label += ': ';
                                        }
                                        if (context.parsed.y !== null) {
                                            label += 'S/ ' + context.parsed.y.toFixed(2);
                                        }
                                        return label;
                                    }
                                }
                            }
                        }
                    }
                });
            })
            .catch(err => {
                console.error("Error al cargar la predicción:", err);
                ctxPred.font = "14px Arial";
                ctxPred.fillStyle = "#c62828";
                ctxPred.textAlign = "center";
                ctxPred.fillText("Error al cargar la predicción. Asegúrese de que el microservicio IA esté en ejecución.", predCanvas.width/2, predCanvas.height/2);
            });
    }

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
    const btnCargarStock = document.getElementById('btnCargarStock');
    const btnRegistrarMerma = document.getElementById('btnRegistrarMerma');

    const panelNuevoProducto = document.getElementById('panelNuevoProducto');
    const panelCargarStock = document.getElementById('panelCargarStock');
    const panelRegistrarMerma = document.getElementById('panelRegistrarMerma');

    const closeNuevoProducto = document.getElementById('closeNuevoProducto');
    const closeCargarStock = document.getElementById('closeCargarStock');
    const closeRegistrarMerma = document.getElementById('closeRegistrarMerma');

    const prodFormCancelBtn = document.getElementById('prodFormCancelBtn');
    const stockFormCancelBtn = document.getElementById('stockFormCancelBtn');
    const mermaFormCancelBtn = document.getElementById('mermaFormCancelBtn');

    // List of panels to manage single-panel active status
    const allProductPanels = [panelNuevoProducto, panelCargarStock, panelRegistrarMerma];

    function showProductPanel(panelToShow) {
        allProductPanels.forEach(p => {
            if (p) p.classList.remove('active');
        });
        if (panelToShow) {
            panelToShow.classList.add('active');
        }
    }

    function closeAllProductPanels() {
        allProductPanels.forEach(p => {
            if (p) p.classList.remove('active');
        });
    }

    // Form fields for Nuevo Producto
    const prodFormId = document.getElementById('prodFormId');
    const prodPanelTitle = document.getElementById('prodPanelTitle');
    const prodFormCodigo = document.getElementById('prodFormCodigo');
    const prodFormNombre = document.getElementById('prodFormNombre');
    const prodFormCategoria = document.getElementById('prodFormCategoria');
    const prodFormPresentacion = document.getElementById('prodFormPresentacion');
    const prodFormPrecio = document.getElementById('prodFormPrecio');
    const prodFormStockMinimo = document.getElementById('prodFormStockMinimo');
    const prodFormDescripcion = document.getElementById('prodFormDescripcion');
    const prodFormSaveBtn = document.getElementById('prodFormSaveBtn');

    // Form fields for Cargar Stock
    const stockFormProducto = document.getElementById('stockFormProducto');
    const stockFormCantidad = document.getElementById('stockFormCantidad');
    const stockFormUnidad = document.getElementById('stockFormUnidad');
    const stockFormFecha = document.getElementById('stockFormFecha');
    const stockFormCosto = document.getElementById('stockFormCosto');
    const stockFormProveedor = document.getElementById('stockFormProveedor');
    const stockFormObservaciones = document.getElementById('stockFormObservaciones');
    const stockFormSaveBtn = document.getElementById('stockFormSaveBtn');

    // Form fields for Registrar Merma
    const mermaFormProducto = document.getElementById('mermaFormProducto');
    const mermaFormCantidad = document.getElementById('mermaFormCantidad');
    const mermaFormUnidad = document.getElementById('mermaFormUnidad');
    const mermaFormFecha = document.getElementById('mermaFormFecha');
    const mermaFormMotivo = document.getElementById('mermaFormMotivo');
    const mermaFormDescripcion = document.getElementById('mermaFormDescripcion');
    const mermaFormSaveBtn = document.getElementById('mermaFormSaveBtn');

    // Toggles side panel open/close
    if (btnNuevoProducto) {
        btnNuevoProducto.addEventListener('click', () => {
            if (prodPanelTitle) prodPanelTitle.innerText = "Nuevo Producto";
            if (prodFormId) prodFormId.value = "";
            if (prodFormCodigo) prodFormCodigo.value = "";
            if (prodFormNombre) prodFormNombre.value = "";
            if (prodFormCategoria) prodFormCategoria.value = "";
            if (prodFormPresentacion) prodFormPresentacion.value = "";
            if (prodFormPrecio) prodFormPrecio.value = "";
            if (prodFormStockMinimo) prodFormStockMinimo.value = "";
            if (prodFormDescripcion) prodFormDescripcion.value = "";
            showProductPanel(panelNuevoProducto);
        });
    }

    if (btnCargarStock) {
        btnCargarStock.addEventListener('click', () => {
            if (stockFormProducto) stockFormProducto.value = "";
            if (stockFormCantidad) stockFormCantidad.value = "";
            if (stockFormUnidad) stockFormUnidad.innerText = "kg";
            if (stockFormCosto) stockFormCosto.value = "";
            if (stockFormProveedor) stockFormProveedor.value = "";
            if (stockFormObservaciones) stockFormObservaciones.value = "";
            showProductPanel(panelCargarStock);
        });
    }

    if (btnRegistrarMerma) {
        btnRegistrarMerma.addEventListener('click', () => {
            if (mermaFormProducto) mermaFormProducto.value = "";
            if (mermaFormCantidad) mermaFormCantidad.value = "";
            if (mermaFormUnidad) mermaFormUnidad.innerText = "kg";
            if (mermaFormMotivo) mermaFormMotivo.value = "";
            if (mermaFormDescripcion) mermaFormDescripcion.value = "";
            showProductPanel(panelRegistrarMerma);
        });
    }

    [closeNuevoProducto, prodFormCancelBtn].forEach(btn => {
        if (btn) btn.addEventListener('click', closeAllProductPanels);
    });
    [closeCargarStock, stockFormCancelBtn].forEach(btn => {
        if (btn) btn.addEventListener('click', closeAllProductPanels);
    });
    [closeRegistrarMerma, mermaFormCancelBtn].forEach(btn => {
        if (btn) btn.addEventListener('click', closeAllProductPanels);
    });

    // Dynamic Unit Selection based on chosen product
    if (stockFormProducto) {
        stockFormProducto.addEventListener('change', () => {
            const opt = stockFormProducto.options[stockFormProducto.selectedIndex];
            const unidad = opt ? opt.getAttribute('data-unidad') : 'kg';
            if (stockFormUnidad) stockFormUnidad.innerText = unidad || 'kg';
        });
    }
    if (mermaFormProducto) {
        mermaFormProducto.addEventListener('change', () => {
            const opt = mermaFormProducto.options[mermaFormProducto.selectedIndex];
            const unidad = opt ? opt.getAttribute('data-unidad') : 'kg';
            if (mermaFormUnidad) mermaFormUnidad.innerText = unidad || 'kg';
        });
    }

    // =========================================
    // PRODUCT ACTIONS (EDIT & DELETE & DIRECT STOCK LOAD)
    // =========================================
    document.querySelectorAll('.btn-editar-producto').forEach(btn => {
        btn.addEventListener('click', () => {
            if (prodPanelTitle) prodPanelTitle.innerText = "Editar Producto";

            const id = btn.getAttribute('data-id') || '';
            const nombre = btn.getAttribute('data-nombre') || '';
            const precio = btn.getAttribute('data-precio') || '';
            const cat = btn.getAttribute('data-categoria') || '';
            const pres = btn.getAttribute('data-presentacion') || '';
            const stockMin = btn.getAttribute('data-stockminimo') || '';
            const desc = btn.getAttribute('data-descripcion') || '';
            const cod = btn.getAttribute('data-codigo') || '';

            if (prodFormId) prodFormId.value = id;
            if (prodFormCodigo) prodFormCodigo.value = cod;
            if (prodFormNombre) prodFormNombre.value = nombre;
            if (prodFormCategoria) prodFormCategoria.value = cat;
            if (prodFormPresentacion) prodFormPresentacion.value = pres;
            if (prodFormPrecio) prodFormPrecio.value = precio;
            if (prodFormStockMinimo) prodFormStockMinimo.value = stockMin;
            if (prodFormDescripcion) prodFormDescripcion.value = desc;

            showProductPanel(panelNuevoProducto);
        });
    });

    document.querySelectorAll('.btn-cargar-stock-directo').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id') || '';
            if (stockFormProducto) {
                stockFormProducto.value = id;
                stockFormProducto.dispatchEvent(new Event('change'));
            }
            if (stockFormCantidad) stockFormCantidad.value = "";
            if (stockFormCosto) stockFormCosto.value = "";
            if (stockFormProveedor) stockFormProveedor.value = "";
            if (stockFormObservaciones) stockFormObservaciones.value = "";
            showProductPanel(panelCargarStock);
        });
    });

    document.querySelectorAll('.btn-eliminar-producto').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');
            if (!id) return;
            if (confirm('¿Está seguro de que desea desactivar este producto?')) {
                fetch(`/api/productos/${id}`, {
                    method: 'DELETE'
                })
                    .then(res => {
                        if (!res.ok) throw new Error('Error al desactivar el producto');
                        return res.text();
                    })
                    .then(() => {
                        window.location.reload();
                    })
                    .catch(err => {
                        alert(err.message);
                        console.error(err);
                    });
            }
        });
    });

    // =========================================
    // SUBMIT FORM: SAVE/EDIT PRODUCT
    // =========================================
    if (prodFormSaveBtn) {
        prodFormSaveBtn.addEventListener('click', (e) => {
            e.preventDefault();

            const id = prodFormId ? prodFormId.value : '';
            const nombre = prodFormNombre ? prodFormNombre.value.trim() : '';
            const catId = prodFormCategoria ? prodFormCategoria.value : '';
            const presId = prodFormPresentacion ? prodFormPresentacion.value : '';
            const precio = prodFormPrecio ? parseFloat(prodFormPrecio.value) : 0;
            const stockMin = prodFormStockMinimo ? parseInt(prodFormStockMinimo.value) : 10;
            const desc = prodFormDescripcion ? prodFormDescripcion.value.trim() : '';

            if (!nombre || !catId || !presId || isNaN(precio) || isNaN(stockMin)) {
                alert('Por favor, complete todos los campos requeridos marcados con (*)');
                return;
            }

            const isEdit = id !== '';
            const method = isEdit ? 'PUT' : 'POST';

            const payload = {
                nombre: nombre,
                precio: precio,
                descripcion: desc,
                stockMinimo: stockMin,
                categoria: { idcategoria: parseInt(catId) },
                presentacion: { idpresentacion: parseInt(presId) }
            };

            if (isEdit) {
                payload.idproducto = parseInt(id);
            }

            const formData = new FormData();
            formData.append('producto', new Blob([JSON.stringify(payload)], { type: 'application/json' }));
            
            const imagenInput = document.getElementById('prodFormImagen');
            if (imagenInput && imagenInput.files.length > 0) {
                formData.append('imagen', imagenInput.files[0]);
            }

            prodFormSaveBtn.disabled = true;
            prodFormSaveBtn.innerText = 'Guardando...';

            fetch('/api/productos', {
                method: method,
                body: formData
            })
                .then(res => {
                    if (!res.ok) {
                        return res.text().then(text => { throw new Error(text || 'Error al guardar producto') });
                    }
                    return res.json();
                })
                .then(() => {
                    window.location.reload();
                })
                .catch(err => {
                    alert(err.message);
                    prodFormSaveBtn.disabled = false;
                    prodFormSaveBtn.innerText = 'Guardar Producto';
                });
        });
    }

    // =========================================
    // SUBMIT FORM: CARGAR STOCK (INGRESO)
    // =========================================
    if (stockFormSaveBtn) {
        stockFormSaveBtn.addEventListener('click', (e) => {
            e.preventDefault();

            const prodId = stockFormProducto ? stockFormProducto.value : '';
            const cantidad = stockFormCantidad ? stockFormCantidad.value.trim() : '';
            const fecha = stockFormFecha ? stockFormFecha.value : '';
            const costo = stockFormCosto ? stockFormCosto.value.trim() : '';
            const proveedor = stockFormProveedor ? stockFormProveedor.value.trim() : '';
            const observaciones = stockFormObservaciones ? stockFormObservaciones.value.trim() : '';

            if (!prodId || !cantidad || !fecha) {
                alert('Por favor, complete los campos obligatorios: Producto, Cantidad y Fecha.');
                return;
            }

            // IngresoProduccion API requires an IngresoRequestDTO
            const payload = {
                dniResponsable: '00000000',
                nombreResponsable: 'Sistema',
                detalle: observaciones || `Ingreso de Mercaderia - Prov: ${proveedor || 'No especificado'}`,
                detalles: [{
                    idproducto: parseInt(prodId),
                    nroLote: 'LOTE-' + new Date().toISOString().slice(0, 10).replace(/-/g, ''),
                    cantidad: cantidad,
                    fechaVencimiento: new Date(new Date().setFullYear(new Date().getFullYear() + 1)).toISOString().slice(0, 10) // Vence en 1 año
                }]
            };

            stockFormSaveBtn.disabled = true;
            stockFormSaveBtn.innerText = 'Guardando...';

            fetch('/api/ingresos', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            })
                .then(res => {
                    if (!res.ok) {
                        return res.text().then(text => { throw new Error(text || 'Error al guardar stock') });
                    }
                    return res.json();
                })
                .then(() => {
                    alert('Stock cargado exitosamente.');
                    window.location.reload();
                })
                .catch(err => {
                    alert(err.message);
                    stockFormSaveBtn.disabled = false;
                    stockFormSaveBtn.innerText = 'Guardar Ingreso';
                });
        });
    }

    // =========================================
    // SUBMIT FORM: REGISTRAR MERMA
    // =========================================
    if (mermaFormSaveBtn) {
        mermaFormSaveBtn.addEventListener('click', (e) => {
            e.preventDefault();

            const prodId = mermaFormProducto ? mermaFormProducto.value : '';
            const cantidad = mermaFormCantidad ? parseInt(mermaFormCantidad.value, 10) : 0;
            const motivoId = mermaFormMotivo ? mermaFormMotivo.value : '';
            const desc = mermaFormDescripcion ? mermaFormDescripcion.value.trim() : '';

            if (!prodId || !cantidad || isNaN(cantidad) || cantidad <= 0 || !motivoId) {
                alert('Por favor, complete los campos obligatorios y asegúrese de que la cantidad sea mayor a cero.');
                return;
            }

            const payload = {
                idproducto: parseInt(prodId),
                cantidad: cantidad,
                idmotivo: parseInt(motivoId)
            };

            mermaFormSaveBtn.disabled = true;
            mermaFormSaveBtn.innerText = 'Guardando...';

            fetch('/api/mermas', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            })
                .then(res => {
                    if (!res.ok) {
                        return res.text().then(text => { throw new Error(text || 'Error al registrar merma') });
                    }
                    return res.text();
                })
                .then(() => {
                    alert('Merma registrada exitosamente.');
                    window.location.reload();
                })
                .catch(err => {
                    alert(err.message);
                    mermaFormSaveBtn.disabled = false;
                    mermaFormSaveBtn.innerText = 'Registrar Merma';
                });
        });
    }

    // =========================================
    // PRODUCT REACTIVE FILTERS & SEARCH
    // =========================================
    const prodSearchInput = document.getElementById('prodSearchInput');
    const prodCategorySelect = document.getElementById('prodCategorySelect');
    const productRows = document.querySelectorAll('.prod-table tbody tr');

    function filterProductos() {
        if (!productRows || productRows.length === 0) return;

        const query = prodSearchInput ? prodSearchInput.value.toLowerCase().trim() : '';
        const catVal = prodCategorySelect ? prodCategorySelect.value : '';

        productRows.forEach(row => {
            if (row.id === 'prodEmptyRow') return;

            const rowSearch = (row.getAttribute('th:data-search') || row.getAttribute('data-search') || '').toLowerCase();
            const rowCat = row.getAttribute('th:data-categoria') || row.getAttribute('data-categoria') || '';

            const matchesSearch = query === '' || rowSearch.includes(query);
            const matchesCat = catVal === '' || rowCat === catVal;

            if (matchesSearch && matchesCat) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });

        // Mostrar fila de "no hay resultados" si es necesario
        let visibleCount = 0;
        productRows.forEach(row => {
            if (row.id !== 'prodEmptyRow' && row.style.display !== 'none') {
                visibleCount++;
            }
        });

        let emptyRow = document.getElementById('prodEmptyRow');
        if (visibleCount === 0) {
            if (!emptyRow) {
                emptyRow = document.createElement('tr');
                emptyRow.id = 'prodEmptyRow';
                emptyRow.innerHTML = '<td colspan="9" style="text-align: center; color: #777; padding: 20px;">No se encontraron productos con los filtros aplicados.</td>';
                const tbody = document.querySelector('.prod-table tbody');
                if (tbody) tbody.appendChild(emptyRow);
            } else {
                emptyRow.style.display = '';
            }
        } else {
            if (emptyRow) emptyRow.style.display = 'none';
        }
    }

    if (prodSearchInput) {
        prodSearchInput.addEventListener('input', filterProductos);
    }
    if (prodCategorySelect) {
        prodCategorySelect.addEventListener('change', filterProductos);
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
    const btnGuardarCliente = document.getElementById('btnGuardarCliente');

    // Form fields
    const formIdCliente = document.getElementById('formIdCliente');
    const formTipoCliente = document.getElementById('formTipoCliente');
    const formNroDocumento = document.getElementById('formNroDocumento');
    const formNombre = document.getElementById('formNombre');
    const formTelefono = document.getElementById('formTelefono');
    const formCorreo = document.getElementById('formCorreo');
    const formDireccion = document.getElementById('formDireccion');

    if (panelCliente) {
        // Abrir panel (Botón Nuevo)
        if (btnNuevoCliente) {
            btnNuevoCliente.addEventListener('click', () => {
                if (formIdCliente) formIdCliente.value = '';
                if (formTipoCliente) formTipoCliente.selectedIndex = 0;
                if (formNroDocumento) formNroDocumento.value = '';
                if (formNombre) formNombre.value = '';
                if (formTelefono) formTelefono.value = '';
                if (formCorreo) formCorreo.value = '';
                if (formDireccion) formDireccion.value = '';

                panelCliente.classList.add('active');
            });
        }

        // Abrir panel (Botones Editar en la tabla)
        btnsEditarCliente.forEach(btn => {
            btn.addEventListener('click', () => {
                const id = btn.getAttribute('data-id');
                if (!id) return;

                fetch(`/api/clientes/${id}`)
                    .then(res => {
                        if (!res.ok) throw new Error('Error al obtener datos del cliente');
                        return res.json();
                    })
                    .then(data => {
                        if (formIdCliente) formIdCliente.value = data.idcliente || '';
                        if (formTipoCliente) formTipoCliente.value = data.idtipocliente || '';
                        if (formNroDocumento) formNroDocumento.value = data.nroDocumento || '';

                        const name = data.nombre || data.razonsocial || '';
                        if (formNombre) formNombre.value = name;

                        // Real database fields
                        if (formTelefono) {
                            formTelefono.value = data.telefono || '';
                        }
                        if (formCorreo) {
                            formCorreo.value = name.toLowerCase().trim().replace(/ /g, '.') + '@gmail.com';
                        }
                        if (formDireccion) {
                            formDireccion.value = 'Jr. Charapita ' + (data.nroDocumento || '');
                        }

                        panelCliente.classList.add('active');
                    })
                    .catch(err => {
                        alert('No se pudo cargar la información del cliente.');
                        console.error(err);
                    });
            });
        });

        // Guardar Cliente (Nuevo o Editado)
        if (btnGuardarCliente) {
            btnGuardarCliente.addEventListener('click', (e) => {
                e.preventDefault();

                const id = formIdCliente ? formIdCliente.value : '';
                const tipoClienteId = formTipoCliente ? formTipoCliente.value : '';
                const nroDoc = formNroDocumento ? formNroDocumento.value.trim() : '';
                const name = formNombre ? formNombre.value.trim() : '';

                if (!nroDoc || !name || !tipoClienteId) {
                    alert('Por favor, complete todos los campos obligatorios (*).');
                    return;
                }

                const isEdit = id !== '';
                const method = isEdit ? 'PUT' : 'POST';

                const clientData = {
                    nombre: name,
                    razonsocial: name,
                    nroDocumento: nroDoc,
                    idtipocliente: parseInt(tipoClienteId),
                    telefono: formTelefono ? formTelefono.value.trim() : '',
                    estado: true
                };

                if (isEdit) {
                    clientData.idcliente = parseInt(id);
                }

                fetch('/api/clientes', {
                    method: method,
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(clientData)
                })
                    .then(res => {
                        if (!res.ok) {
                            return res.text().then(text => { throw new Error(text || 'Error al guardar cliente') });
                        }
                        return res.json();
                    })
                    .then(() => {
                        window.location.reload();
                    })
                    .catch(err => {
                        alert('Ocurrió un error al guardar el cliente: ' + err.message);
                        console.error(err);
                    });
            });
        }

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
                const id = btn.getAttribute('data-id');
                if (!id) return;

                const titleSpan = panelHistorial.querySelector('.cli-client-name');
                if (titleSpan) titleSpan.innerHTML = 'Cargando historial...';

                const tableBody = panelHistorial.querySelector('.cli-table-history tbody');
                if (tableBody) tableBody.innerHTML = '<tr><td colspan="7" style="text-align: center; padding: 15px;">Cargando compras...</td></tr>';

                const prefList = panelHistorial.querySelector('.cli-pref-list');
                if (prefList) prefList.innerHTML = '<div style="text-align: center; font-size: 0.85rem; color: #888; padding: 15px;">Cargando...</div>';

                panelHistorial.classList.add('active');

                // Fetch customer purchases and preferences
                fetch(`/api/clientes/${id}/historial`)
                    .then(res => {
                        if (!res.ok) throw new Error('Error al cargar historial');
                        return res.json();
                    })
                    .then(data => {
                        // 1. Nombre y DNI del cliente
                        const clientName = data.cliente.nombre || data.cliente.razonsocial;
                        const clientDoc = data.cliente.nroDocumento;
                        if (titleSpan) {
                            titleSpan.innerHTML = `${clientName} <small>(Doc: ${clientDoc})</small>`;
                        }

                        // Guardar datos en el botón de exportación
                        const btnExport = document.getElementById('btnExportarHistorialCliente');
                        if (btnExport) {
                            btnExport.setAttribute('data-name', clientName);
                            btnExport.setAttribute('data-doc', clientDoc);
                        }

                        // 2. Tabla de compras
                        if (tableBody) {
                            if (data.compras.length === 0) {
                                tableBody.innerHTML = '<tr><td colspan="7" style="text-align: center; color: #888; padding: 15px;">No tiene compras registradas.</td></tr>';
                            } else {
                                tableBody.innerHTML = data.compras.map(c => {
                                    let badgeClass = 'cli-bg-blue';
                                    let iconClass = 'fa-solid fa-credit-card';
                                    const methodLower = (c.metodo || '').toLowerCase();

                                    if (methodLower.includes('efectivo')) {
                                        badgeClass = 'cli-bg-efectivo';
                                        iconClass = 'fa-solid fa-money-bill';
                                    } else if (methodLower.includes('yape')) {
                                        badgeClass = 'cli-bg-yape';
                                        iconClass = 'fa-solid fa-mobile-screen';
                                    }

                                    return `
                                        <tr>
                                            <td>${c.fecha}</td>
                                            <td>${c.comprobante}</td>
                                            <td>${c.tipo}</td>
                                            <td>S/ ${parseFloat(c.total).toFixed(2)}</td>
                                            <td><span class="cli-badge ${badgeClass}"><i class="${iconClass}"></i> ${c.metodo}</span></td>
                                            <td>${c.vendedor}</td>
                                            <td><a href="/historial?ventaId=${c.idventa}" class="cli-link"><i class="fa-solid fa-arrow-up-right-from-square" style="font-size:0.75rem; margin-right:4px;"></i>Ver detalle</a></td>
                                        </tr>
                                    `;
                                }).join('');
                            }
                        }

                        // 3. Productos Preferidos
                        if (prefList) {
                            if (data.productosPreferidos.length === 0) {
                                prefList.innerHTML = '<div style="text-align: center; font-size: 0.85rem; color: #888; padding: 15px;">Sin compras recurrentes.</div>';
                            } else {
                                prefList.innerHTML = data.productosPreferidos.map((p, idx) => {
                                    const totalImporte = p.totalImporte != null ? parseFloat(p.totalImporte).toFixed(2) : '0.00';
                                    return `
                                    <div class="cli-pref-item">
                                        <span class="cli-pref-num">${idx + 1}</span>
                                        <div class="cli-heart-icon" style="width:30px; height:30px; font-size:0.9rem; background:#fff; border:1px solid #f0f0f0;">
                                            <i class="fa-solid fa-basket-shopping" style="color:#a85867;"></i>
                                        </div>
                                        <div class="cli-pref-info">
                                            <strong>${p.nombre}</strong>
                                            <small>Cantidad total: <b>${p.totalQty} ${p.unidad}</b> &bull; Promedio: ${p.promedioCantidad} ${p.unidad}/compra</small>
                                            <small style="color:#8a1529; font-weight:600;">Total gastado: S/ ${totalImporte}</small>
                                        </div>
                                    </div>
                                    `;
                                }).join('');
                            }
                        }
                    })
                    .catch(err => {
                        console.error(err);
                        if (titleSpan) titleSpan.innerHTML = 'Error al cargar';
                        if (tableBody) tableBody.innerHTML = '<tr><td colspan="7" style="text-align: center; color: #d32f2f; padding: 15px;">No se pudo cargar el historial del cliente.</td></tr>';
                        if (prefList) prefList.innerHTML = '<div style="text-align: center; color: #d32f2f; padding: 15px;">Error</div>';
                    });

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
    // FILTROS Y PAGINACIÓN DE CLIENTES (cli-)
    // =========================================
    const clienteSearchInput = document.getElementById('clienteSearchInput');
    const clienteTipoSelect = document.getElementById('clienteTipoSelect');
    const clientePageSizeSelect = document.getElementById('clientePageSizeSelect');
    const clientePaginationContainer = document.getElementById('clientePaginationContainer');
    const cliShowingCount = document.getElementById('cliShowingCount');
    const cliTotalCount = document.getElementById('cliTotalCount');
    const clientRows = document.querySelectorAll('.cli-table tbody tr:not([colspan])');

    let cliCurrentPage = 1;
    let cliPageSize = 10;

    function filterAndPaginateClientes() {
        if (!clientRows || clientRows.length === 0) return;

        const query = clienteSearchInput ? clienteSearchInput.value.toLowerCase().trim() : '';
        const tipoVal = clienteTipoSelect ? clienteTipoSelect.value : '';

        // 1. Filtrar filas
        let visibleRows = [];
        clientRows.forEach(row => {
            if (row.id === 'clienteEmptyRow') return;
            const rowSearch = (row.getAttribute('data-search') || '').toLowerCase();
            const rowTipo = row.getAttribute('data-tipo') || '';

            const matchesSearch = query === '' || rowSearch.includes(query);
            const matchesTipo = tipoVal === '' || rowTipo === tipoVal;

            if (matchesSearch && matchesTipo) {
                visibleRows.push(row);
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });

        // Actualizar total count
        const totalRows = visibleRows.length;
        if (cliTotalCount) cliTotalCount.innerText = totalRows;

        // Si no hay filas visibles
        if (totalRows === 0) {
            let emptyRow = document.getElementById('clienteEmptyRow');
            if (!emptyRow) {
                emptyRow = document.createElement('tr');
                emptyRow.id = 'clienteEmptyRow';
                emptyRow.innerHTML = '<td colspan="7" style="text-align: center; color: #777; padding: 20px;">No se encontraron clientes con los filtros aplicados.</td>';
                const tbody = document.querySelector('.cli-table tbody');
                if (tbody) tbody.appendChild(emptyRow);
            } else {
                emptyRow.style.display = '';
            }

            if (cliShowingCount) cliShowingCount.innerText = '0';
            if (clientePaginationContainer) clientePaginationContainer.innerHTML = '';
            return;
        } else {
            const emptyRow = document.getElementById('clienteEmptyRow');
            if (emptyRow) emptyRow.style.display = 'none';
        }

        // 2. Calcular paginación
        const totalPages = Math.ceil(totalRows / cliPageSize);

        // Clampar página actual
        if (cliCurrentPage > totalPages) cliCurrentPage = totalPages;
        if (cliCurrentPage < 1) cliCurrentPage = 1;

        // Ocultar todas las filas visibles primero
        visibleRows.forEach(row => row.style.display = 'none');

        // Mostrar solo las filas de la página actual
        const start = (cliCurrentPage - 1) * cliPageSize;
        const end = Math.min(start + cliPageSize, totalRows);

        for (let i = start; i < end; i++) {
            visibleRows[i].style.display = '';
        }

        // Actualizar texto: "Mostrando X-Y de Z clientes"
        if (cliShowingCount) {
            cliShowingCount.innerText = `${start + 1}-${end}`;
        }

        // 3. Renderizar botones de paginación
        if (clientePaginationContainer) {
            clientePaginationContainer.innerHTML = '';

            // Botón Primero «
            const btnFirst = document.createElement('span');
            btnFirst.innerHTML = '&laquo;';
            if (cliCurrentPage === 1) {
                btnFirst.classList.add('disabled');
            } else {
                btnFirst.addEventListener('click', () => { cliCurrentPage = 1; filterAndPaginateClientes(); });
            }
            clientePaginationContainer.appendChild(btnFirst);

            // Botón Anterior <
            const btnPrev = document.createElement('span');
            btnPrev.innerHTML = '&lt;';
            if (cliCurrentPage === 1) {
                btnPrev.classList.add('disabled');
            } else {
                btnPrev.addEventListener('click', () => { cliCurrentPage--; filterAndPaginateClientes(); });
            }
            clientePaginationContainer.appendChild(btnPrev);

            // Páginas numéricas
            const maxPageButtons = 5;
            let startPage = Math.max(1, cliCurrentPage - Math.floor(maxPageButtons / 2));
            let endPage = Math.min(totalPages, startPage + maxPageButtons - 1);

            if (endPage - startPage + 1 < maxPageButtons) {
                startPage = Math.max(1, endPage - maxPageButtons + 1);
            }

            for (let p = startPage; p <= endPage; p++) {
                const btnPage = document.createElement('span');
                btnPage.innerText = p;
                if (p === cliCurrentPage) {
                    btnPage.classList.add('active');
                } else {
                    btnPage.addEventListener('click', () => { cliCurrentPage = p; filterAndPaginateClientes(); });
                }
                clientePaginationContainer.appendChild(btnPage);
            }

            // Botón Siguiente >
            const btnNext = document.createElement('span');
            btnNext.innerHTML = '&gt;';
            if (cliCurrentPage === totalPages) {
                btnNext.classList.add('disabled');
            } else {
                btnNext.addEventListener('click', () => { cliCurrentPage++; filterAndPaginateClientes(); });
            }
            clientePaginationContainer.appendChild(btnNext);

            // Botón Último »
            const btnLast = document.createElement('span');
            btnLast.innerHTML = '&raquo;';
            if (cliCurrentPage === totalPages) {
                btnLast.classList.add('disabled');
            } else {
                btnLast.addEventListener('click', () => { cliCurrentPage = totalPages; filterAndPaginateClientes(); });
            }
            clientePaginationContainer.appendChild(btnLast);
        }
    }

    // Registrar listeners para cambios reactivos en filtros y paginación
    if (clienteSearchInput) {
        clienteSearchInput.addEventListener('input', () => {
            cliCurrentPage = 1;
            filterAndPaginateClientes();
        });
    }

    if (clienteTipoSelect) {
        clienteTipoSelect.addEventListener('change', () => {
            cliCurrentPage = 1;
            filterAndPaginateClientes();
        });
    }

    if (clientePageSizeSelect) {
        clientePageSizeSelect.addEventListener('change', () => {
            cliPageSize = parseInt(clientePageSizeSelect.value) || 10;
            cliCurrentPage = 1;
            filterAndPaginateClientes();
        });
    }

    // Inicializar filtros y paginación de clientes al cargar la página
    if (document.querySelector('.cli-table')) {
        filterAndPaginateClientes();
    }

    // =========================================
    // EXPORTACIÓN DEL HISTORIAL DEL CLIENTE A EXCEL
    // =========================================
    function exportTableToExcel(tableId, filename = '') {
        const table = document.getElementById(tableId);
        if (!table) return;

        // Clonar la tabla para no alterar la visualización en la interfaz
        const clone = table.cloneNode(true);
        const rows = clone.querySelectorAll('tr');

        // Eliminar la última columna (que contiene el enlace de "Ver detalle")
        rows.forEach(row => {
            if (row.cells.length > 0) {
                row.deleteCell(row.cells.length - 1);
            }
        });

        const cleanHtml = clone.outerHTML;

        // Plantilla de Excel con estilos básicos
        const excelTemplate = `
            <html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40">
            <head>
                <meta charset="utf-8">
                <!--[if gte mso 9]>
                <xml>
                    <x:ExcelWorkbook>
                        <x:ExcelWorksheets>
                            <x:ExcelWorksheet>
                                <x:Name>Historial de Compras</x:Name>
                                <x:WorksheetOptions>
                                    <x:DisplayGridlines/>
                                </x:WorksheetOptions>
                            </x:ExcelWorksheet>
                        </x:ExcelWorksheets>
                    </x:ExcelWorkbook>
                </xml>
                <![endif]-->
                <style>
                    table { border-collapse: collapse; width: 100%; font-family: Arial, sans-serif; }
                    th { background-color: #8a1529; color: #ffffff; font-weight: bold; padding: 8px; border: 1px solid #dddddd; text-align: left; }
                    td { padding: 8px; border: 1px solid #dddddd; }
                    tr:nth-child(even) { background-color: #f2f2f2; }
                </style>
            </head>
            <body>
                ${cleanHtml}
            </body>
            </html>
        `;

        const blob = new Blob([excelTemplate], { type: 'application/vnd.ms-excel;charset=utf-8;' });
        const url = URL.createObjectURL(blob);

        const a = document.createElement('a');
        a.href = url;
        a.download = filename ? filename + '.xls' : 'historial_compras.xls';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
    }

    const btnExportarHistorialCliente = document.getElementById('btnExportarHistorialCliente');
    if (btnExportarHistorialCliente) {
        btnExportarHistorialCliente.addEventListener('click', (e) => {
            e.preventDefault();
            const name = btnExportarHistorialCliente.getAttribute('data-name') || 'Cliente';
            const doc = btnExportarHistorialCliente.getAttribute('data-doc') || '00000000';
            const filename = `Historial_Compras_${name.replace(/[^a-zA-Z0-9]/g, '_')}_${doc}`;
            exportTableToExcel('cliTableHistory', filename);
        });
    }
    // =========================================
    // LÓGICA VISTA: REPORTES (rep-)
    // =========================================

    // 1. Interacción de Pestañas (Tabs)
    const repBlockVentas = document.getElementById('repBlockVentas');
    const repBlockMermas = document.getElementById('repBlockMermas');
    const repBlockInventario = document.getElementById('repBlockInventario');

    const tabVentas = document.getElementById('tabVentas');
    const tabMermas = document.getElementById('tabMermas');
    const tabInventario = document.getElementById('tabInventario');

    const repFilterFechaInicio = document.getElementById('repFilterFechaInicio');
    const repFilterFechaFin = document.getElementById('repFilterFechaFin');
    const repFilterCaja = document.getElementById('repFilterCaja');
    const repFilterVendedor = document.getElementById('repFilterVendedor');
    const repFilterCategoria = document.getElementById('repFilterCategoria');

    const repFilterBtnApply = document.getElementById('repFilterBtnApply');
    const repFilterBtnClean = document.getElementById('repFilterBtnClean');

    // Chart.js global reference
    let repChart = null;

    // Tabs toggle logic
    function switchTab(activeTab, blockToShow) {
        [tabVentas, tabMermas, tabInventario].forEach(tab => {
            if (tab) tab.classList.remove('active');
        });
        if (activeTab) activeTab.classList.add('active');

        [repBlockVentas, repBlockMermas, repBlockInventario].forEach(block => {
            if (block) block.style.display = 'none';
        });
        if (blockToShow) {
            blockToShow.style.display = 'block';
        }
    }

    if (tabVentas && repBlockVentas) {
        tabVentas.addEventListener('click', () => switchTab(tabVentas, repBlockVentas));
    }
    if (tabMermas && repBlockMermas) {
        tabMermas.addEventListener('click', () => {
            switchTab(tabMermas, repBlockMermas);
            if (repChart) repChart.resize();
        });
    }
    if (tabInventario && repBlockInventario) {
        tabInventario.addEventListener('click', () => switchTab(tabInventario, repBlockInventario));
    }



    // Main Filter logic
    function applyGlobalFilters() {
        const dateStartStr = repFilterFechaInicio ? repFilterFechaInicio.value : '';
        const dateEndStr = repFilterFechaFin ? repFilterFechaFin.value : '';
        const sellerVal = repFilterVendedor ? repFilterVendedor.value.toLowerCase().trim() : '';
        const catVal = repFilterCategoria ? repFilterCategoria.value : '';

        // 1. FILTER VENTAS
        let ventasSubtotalSum = 0;
        let ventasIgvSum = 0;
        let ventasTotalSum = 0;
        let visibleVentasCount = 0;

        const ventaRows = document.querySelectorAll('.rep-venta-row');
        ventaRows.forEach(row => {
            const rowFecha = row.getAttribute('data-fecha') || '';
            const rowVendedor = (row.getAttribute('data-vendedor') || '').toLowerCase().trim();

            const total = parseFloat(row.getAttribute('data-total') || '0');
            const applyIgv = row.getAttribute('data-apply-igv') === 'true';
            let subtotal = total;
            let igv = 0;
            if (applyIgv) {
                subtotal = total / 1.18;
                igv = total - subtotal;
            }

            const matchesStart = dateStartStr === '' || rowFecha >= dateStartStr;
            const matchesEnd = dateEndStr === '' || rowFecha <= dateEndStr;
            const matchesSeller = sellerVal === '' || rowVendedor === sellerVal;

            if (matchesStart && matchesEnd && matchesSeller) {
                row.style.display = '';
                ventasSubtotalSum += subtotal;
                ventasIgvSum += igv;
                ventasTotalSum += total;
                visibleVentasCount++;
            } else {
                row.style.display = 'none';
            }
        });

        const repSumVentasSubtotal = document.getElementById('repSumVentasSubtotal');
        const repSumVentasIgv = document.getElementById('repSumVentasIgv');
        const repSumVentasTotal = document.getElementById('repSumVentasTotal');
        const repVentasCount = document.getElementById('repVentasCount');
        const repVentasEmptyRow = document.getElementById('repVentasEmptyRow');

        if (repSumVentasSubtotal) repSumVentasSubtotal.innerText = ventasSubtotalSum.toFixed(2);
        if (repSumVentasIgv) repSumVentasIgv.innerText = ventasIgvSum.toFixed(2);
        if (repSumVentasTotal) repSumVentasTotal.innerText = ventasTotalSum.toFixed(2);
        if (repVentasCount) repVentasCount.innerText = `Mostrando ${visibleVentasCount} de ${ventaRows.length} ventas`;
        if (repVentasEmptyRow) {
            repVentasEmptyRow.style.display = (visibleVentasCount === 0) ? '' : 'none';
        }

        // 2. FILTER MERMAS
        let mermasCantidadSum = 0;
        let mermasValorSum = 0;
        let visibleMermasCount = 0;

        const mermaRows = document.querySelectorAll('.rep-merma-row');
        mermaRows.forEach(row => {
            const rowFecha = row.getAttribute('data-fecha') || '';
            const rowCat = row.getAttribute('data-categoria') || '';
            const cant = parseFloat(row.getAttribute('data-cantidad') || '0');
            const valor = parseFloat(row.getAttribute('data-valor') || '0');

            const matchesStart = dateStartStr === '' || rowFecha >= dateStartStr;
            const matchesEnd = dateEndStr === '' || rowFecha <= dateEndStr;
            const matchesCat = catVal === '' || rowCat === catVal;

            if (matchesStart && matchesEnd && matchesCat) {
                row.style.display = '';
                mermasCantidadSum += cant;
                mermasValorSum += valor;
                visibleMermasCount++;
            } else {
                row.style.display = 'none';
            }
        });

        const repTotalMermaCant = document.getElementById('repTotalMermaCant');
        const repTotalMermaValor = document.getElementById('repTotalMermaValor');
        const repMermasEmptyRow = document.getElementById('repMermasEmptyRow');

        if (repTotalMermaCant) repTotalMermaCant.innerText = mermasCantidadSum.toFixed(2);
        if (repTotalMermaValor) repTotalMermaValor.innerText = mermasValorSum.toFixed(2);
        if (repMermasEmptyRow) {
            repMermasEmptyRow.style.display = (visibleMermasCount === 0) ? '' : 'none';
        }

        const repMermaPercentage = document.getElementById('repMermaPercentage');
        if (repMermaPercentage) {
            const pct = (ventasTotalSum > 0) ? (mermasValorSum / ventasTotalSum) * 100 : 0;
            repMermaPercentage.innerText = pct.toFixed(2);
        }

        // 3. FILTER INVENTARIOS
        let inventarioStockSum = 0;
        let inventarioValorSum = 0;
        let visibleInventariosCount = 0;

        const inventarioRows = document.querySelectorAll('.rep-inventario-row');
        inventarioRows.forEach(row => {
            const rowCat = row.getAttribute('data-categoria') || '';
            const stock = parseFloat(row.getAttribute('data-stock') || '0');
            const valor = parseFloat(row.getAttribute('data-valor') || '0');

            const matchesCat = catVal === '' || rowCat === catVal;

            if (matchesCat) {
                row.style.display = '';
                inventarioStockSum += stock;
                inventarioValorSum += valor;
                visibleInventariosCount++;
            } else {
                row.style.display = 'none';
            }
        });

        const repTotalInventarioStock = document.getElementById('repTotalInventarioStock');
        const repTotalInventarioValor = document.getElementById('repTotalInventarioValor');
        const repInventarioEmptyRow = document.getElementById('repInventarioEmptyRow');

        if (repTotalInventarioStock) repTotalInventarioStock.innerText = inventarioStockSum.toFixed(2);
        if (repTotalInventarioValor) repTotalInventarioValor.innerText = inventarioValorSum.toFixed(2);
        if (repInventarioEmptyRow) {
            repInventarioEmptyRow.style.display = (visibleInventariosCount === 0) ? '' : 'none';
        }

        // 4. UPDATE CHART
        if (repChart) {
            repChart.data.datasets[0].data = [ventasTotalSum, mermasValorSum];
            repChart.update();
        }
    }

    // Initialize Chart
    const repCanvas = document.getElementById('repBarChart');
    if (repCanvas) {
        const ctxRep = repCanvas.getContext('2d');
        repChart = new Chart(ctxRep, {
            type: 'bar',
            data: {
                labels: ['Ventas Totales', 'Pérdidas por Mermas'],
                datasets: [{
                    label: 'Monto en Soles (S/)',
                    data: [0, 0],
                    backgroundColor: [
                        '#2e7d32',
                        '#c62828'
                    ],
                    borderColor: [
                        '#1b5e20',
                        '#b71c1c'
                    ],
                    borderWidth: 1,
                    borderRadius: 6,
                    barThickness: 35
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                return 'S/ ' + context.raw.toFixed(2);
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: { color: '#eaeaea' },
                        ticks: {
                            callback: function (value) {
                                return 'S/ ' + value;
                            }
                        }
                    },
                    x: {
                        grid: { display: false }
                    }
                }
            }
        });
    }

    if (repFilterBtnApply) {
        repFilterBtnApply.addEventListener('click', applyGlobalFilters);
    }

    if (repFilterBtnClean) {
        repFilterBtnClean.addEventListener('click', () => {
            if (repFilterFechaInicio) repFilterFechaInicio.value = repFilterFechaInicio.getAttribute('value') || '';
            if (repFilterFechaFin) repFilterFechaFin.value = repFilterFechaFin.getAttribute('value') || '';
            if (repFilterCaja) repFilterCaja.value = '';
            if (repFilterVendedor) repFilterVendedor.value = '';
            if (repFilterCategoria) repFilterCategoria.value = '';
            applyGlobalFilters();
        });
    }

    // =========================================
    // EXPORTS: EXCEL & PDF
    // =========================================
    function getVisibleTableHTML(tableId, title) {
        const table = document.getElementById(tableId);
        if (!table) return '';

        let html = `
        <html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40">
        <head>
            <meta charset="UTF-8">
            <!--[if gte mso 9]>
            <xml>
                <x:ExcelWorkbook>
                    <x:ExcelWorksheets>
                        <x:ExcelWorksheet>
                            <x:Name>${title}</x:Name>
                            <x:WorksheetOptions>
                                <x:DisplayGridlines/>
                            </x:WorksheetOptions>
                        </x:ExcelWorksheet>
                    </x:ExcelWorksheets>
                </x:ExcelWorkbook>
            </xml>
            <![endif]-->
            <style>
                body { font-family: Arial, sans-serif; }
                h2 { color: #e65100; margin-bottom: 5px; }
                p { color: #555; margin-bottom: 20px; font-size: 13px; }
                table { border-collapse: collapse; width: 100%; margin-top: 10px; }
                th { background-color: #f5f5f5; border: 1px solid #ddd; padding: 10px; text-align: left; font-weight: bold; }
                td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                .total-row { font-weight: bold; background-color: #eaeaea; }
            </style>
        </head>
        <body>
            <h2>${title}</h2>
            <p>Generado automáticamente el ${new Date().toLocaleString()}</p>
            <table>
                <thead>
        `;

        const headers = table.querySelectorAll('thead tr th');
        html += '<tr>';
        headers.forEach(th => {
            html += `<th>${th.innerText}</th>`;
        });
        html += '</tr></thead><tbody>';

        const rows = table.querySelectorAll('tbody tr');
        rows.forEach(row => {
            if (row.style.display !== 'none' && row.id !== 'repVentasEmptyRow' && row.id !== 'repMermasEmptyRow' && row.id !== 'repInventarioEmptyRow') {
                html += '<tr>';
                row.querySelectorAll('td').forEach(td => {
                    html += `<td>${td.innerText}</td>`;
                });
                html += '</tr>';
            }
        });

        html += '</tbody>';

        const footers = table.querySelectorAll('tfoot tr');
        if (footers.length > 0) {
            html += '<tfoot>';
            footers.forEach(footRow => {
                html += '<tr class="total-row">';
                footRow.querySelectorAll('td').forEach(td => {
                    html += `<td colspan="${td.getAttribute('colspan') || 1}">${td.innerText}</td>`;
                });
                html += '</tr>';
            });
            html += '</tfoot>';
        }

        html += '</table></body></html>';
        return html;
    }

    function exportToExcelBlob(tableId, title, filename) {
        const tableHTML = getVisibleTableHTML(tableId, title);
        if (!tableHTML) return;

        const blob = new Blob([tableHTML], { type: 'application/vnd.ms-excel;charset=utf-8' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
    }

    function generatePDFReport(elementId, title) {
        const element = document.getElementById(elementId);
        if (!element || typeof html2pdf === 'undefined') {
            alert('Librería de PDF no cargada o contenedor no encontrado.');
            return;
        }

        const exportActions = element.querySelector('.rep-export-actions');
        if (exportActions) exportActions.style.display = 'none';

        const opt = {
            margin: 10,
            filename: `${title.replace(/\s+/g, '_').toLowerCase()}_${new Date().toISOString().slice(0, 10)}.pdf`,
            image: { type: 'jpeg', quality: 0.98 },
            html2canvas: { scale: 2, useCORS: true },
            jsPDF: { unit: 'mm', format: 'a4', orientation: 'landscape' }
        };

        html2pdf().set(opt).from(element).save().then(() => {
            if (exportActions) exportActions.style.display = 'flex';
        }).catch(err => {
            console.error(err);
            if (exportActions) exportActions.style.display = 'flex';
        });
    }

    const btnExportVentasExcel = document.getElementById('btnExportVentasExcel');
    const btnExportVentasPdf = document.getElementById('btnExportVentasPdf');
    if (btnExportVentasExcel) {
        btnExportVentasExcel.addEventListener('click', () => {
            exportToExcelBlob('tableVentas', 'Reporte de Ventas Detallado', 'reporte_ventas_detallado.xls');
        });
    }
    if (btnExportVentasPdf) {
        btnExportVentasPdf.addEventListener('click', () => {
            generatePDFReport('repBlockVentas', 'Reporte de Ventas Detallado');
        });
    }

    const btnExportMermasExcel = document.getElementById('btnExportMermasExcel');
    const btnExportMermasPdf = document.getElementById('btnExportMermasPdf');
    if (btnExportMermasExcel) {
        btnExportMermasExcel.addEventListener('click', () => {
            exportToExcelBlob('tableMermas', 'Reporte de Mermas y Pérdidas', 'reporte_mermas.xls');
        });
    }
    if (btnExportMermasPdf) {
        btnExportMermasPdf.addEventListener('click', () => {
            generatePDFReport('repBlockMermas', 'Reporte de Mermas y Pérdidas');
        });
    }

    const btnExportInventarioExcel = document.getElementById('btnExportInventarioExcel');
    const btnExportInventarioPdf = document.getElementById('btnExportInventarioPdf');
    if (btnExportInventarioExcel) {
        btnExportInventarioExcel.addEventListener('click', () => {
            exportToExcelBlob('tableInventario', 'Reporte de Inventario y Valorización', 'reporte_inventario.xls');
        });
    }
    if (btnExportInventarioPdf) {
        btnExportInventarioPdf.addEventListener('click', () => {
            generatePDFReport('repBlockInventario', 'Reporte de Inventario y Valorización');
        });
    }


    applyGlobalFilters();
    // =========================================
    // LÓGICA VISTA: CAJA Y MOVIMIENTOS
    // =========================================
    const btnConfirmarApertura = document.getElementById('btnConfirmarApertura');
    const btnConfirmarCierre = document.getElementById('btnConfirmarCierre');
    const inputMontoReal = document.getElementById('inputMontoReal');
    const valMontoEsperadoSpan = document.getElementById('valMontoEsperado');
    const boxDiferencia = document.getElementById('boxDiferencia');
    const valDiferencia = document.getElementById('valDiferencia');

    // 1. Cálculo dinámico de cuadre de caja
    if (inputMontoReal && valMontoEsperadoSpan && boxDiferencia && valDiferencia) {
        const montoEsperado = parseFloat(valMontoEsperadoSpan.getAttribute('data-esperado') || '0');

        inputMontoReal.addEventListener('input', () => {
            const montoRealVal = inputMontoReal.value.trim();
            if (montoRealVal === '') {
                valDiferencia.innerText = 'S/ 0.00';
                boxDiferencia.style.background = '#f4f6f9';
                boxDiferencia.style.color = '#555';
                return;
            }

            const montoReal = parseFloat(montoRealVal);
            const diferencia = montoReal - montoEsperado;

            if (Math.abs(diferencia) < 0.009) {
                valDiferencia.innerText = 'S/ 0.00 (Sin diferencia)';
                boxDiferencia.style.background = '#e8f5e9';
                boxDiferencia.style.color = '#2e7d32';
            } else if (diferencia > 0) {
                valDiferencia.innerText = `+ S/ ${diferencia.toFixed(2)} (Sobrante)`;
                boxDiferencia.style.background = '#e8f5e9';
                boxDiferencia.style.color = '#2e7d32';
            } else {
                valDiferencia.innerText = `- S/ ${Math.abs(diferencia).toFixed(2)} (Faltante)`;
                boxDiferencia.style.background = '#ffebee';
                boxDiferencia.style.color = '#c62828';
            }
        });
    }

    // 2. Realizar Apertura de Caja
    if (btnConfirmarApertura) {
        btnConfirmarApertura.addEventListener('click', async (e) => {
            e.preventDefault();
            const idcaja = document.getElementById('selectCaja').value;
            const idusuario = document.getElementById('selectCajero').value;
            const montoinicialVal = document.getElementById('inputMontoInicial').value.trim();
            const observaciones = document.getElementById('textareaObsApertura').value.trim();

            if (montoinicialVal === '') {
                alert('Debe ingresar un monto inicial de apertura.');
                return;
            }

            const montoinicial = parseFloat(montoinicialVal);
            if (isNaN(montoinicial) || montoinicial < 0) {
                alert('El monto inicial debe ser un número positivo.');
                return;
            }

            // --- VERIFICACIÓN DE CONTRASEÑA ---
            const userStr = localStorage.getItem('user');
            if (!userStr) {
                alert("Sesión no válida. Inicie sesión nuevamente.");
                window.location.href = '/login.html';
                return;
            }
            const authUser = JSON.parse(userStr);
            
            const password = prompt(`Por motivos de seguridad, ingrese la contraseña de ${authUser.correo} para ABRIR la caja:`);
            if (password === null) return; // Cancelado por el usuario

            try {
                const authRes = await fetch('/api/usuarios/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ correo: authUser.correo, contrasena: password })
                });

                if (!authRes.ok) {
                    alert('Contraseña incorrecta. Acción denegada.');
                    return;
                }

                // --- PROCEDER CON LA APERTURA ---
                const payload = {
                    idcaja: parseInt(idcaja),
                    idusuario: parseInt(idusuario),
                    montoinicial: montoinicial,
                    observaciones: observaciones
                };

                const res = await fetch('/api/movimientoscaja/abrir', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                if (!res.ok) {
                    const text = await res.text();
                    throw new Error(text || 'Error al abrir caja');
                }

                window.location.reload();
            } catch (err) {
                alert('Error: ' + err.message);
                console.error(err);
            }
        });
    }

    // 3. Realizar Cierre de Caja
    if (btnConfirmarCierre) {
        btnConfirmarCierre.addEventListener('click', async (e) => {
            e.preventDefault();
            const panelCierre = document.getElementById('panelCierreCaja');
            const idmovimiento = panelCierre.getAttribute('data-id');
            const montofinalVal = document.getElementById('inputMontoReal').value.trim();

            if (montofinalVal === '') {
                alert('Debe ingresar el monto real contado en efectivo.');
                return;
            }

            const montofinal = parseFloat(montofinalVal);
            if (isNaN(montofinal) || montofinal < 0) {
                alert('El monto real debe ser un número positivo.');
                return;
            }

            // --- VERIFICACIÓN DE CONTRASEÑA ---
            const userStr = localStorage.getItem('user');
            if (!userStr) {
                alert("Sesión no válida. Inicie sesión nuevamente.");
                window.location.href = '/login.html';
                return;
            }
            const authUser = JSON.parse(userStr);
            
            const password = prompt(`Por motivos de seguridad, ingrese la contraseña de ${authUser.correo} para CERRAR la caja:`);
            if (password === null) return; // Cancelado por el usuario

            try {
                const authRes = await fetch('/api/usuarios/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ correo: authUser.correo, contrasena: password })
                });

                if (!authRes.ok) {
                    alert('Contraseña incorrecta. Acción denegada.');
                    return;
                }

                // --- PROCEDER CON EL CIERRE ---
                const payload = { montofinal: montofinal };

                const res = await fetch(`/api/movimientoscaja/cerrar/${idmovimiento}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                if (!res.ok) {
                    const text = await res.text();
                    throw new Error(text || 'Error al cerrar caja');
                }

                window.location.reload();
            } catch (err) {
                alert('Error: ' + err.message);
                console.error(err);
            }
        });
    }

    // =========================================================
    // LÓGICA COMPLETA DE NUEVA VENTA Y CONFIRMAR VENTA
    // =========================================================

    // --- ESTADO GLOBAL POS ---
    let carrito = JSON.parse(localStorage.getItem('pos_cart')) || [];
    let selectedPayMethod = JSON.parse(localStorage.getItem('pos_pay_method')) || null;
    let fetchRecommendations;

    // Inicializar método de pago por defecto si no existe o no tiene estructura
    if (!selectedPayMethod || typeof selectedPayMethod !== 'object' || !selectedPayMethod.id) {
        const posPaymentSelect = document.getElementById('posPaymentSelect');
        if (posPaymentSelect && posPaymentSelect.options.length > 0) {
            const selectedOption = posPaymentSelect.options[posPaymentSelect.selectedIndex >= 0 ? posPaymentSelect.selectedIndex : 0];
            selectedPayMethod = {
                id: parseInt(selectedOption.value),
                nombre: selectedOption.getAttribute('data-nombre')
            };
        } else {
            selectedPayMethod = null;
        }
        if (selectedPayMethod) {
            localStorage.setItem('pos_pay_method', JSON.stringify(selectedPayMethod));
        }
    }

    const cartTableBody = document.getElementById('cartTableBody');
    const cartSubtotal = document.getElementById('cartSubtotal');
    const cartIgv = document.getElementById('cartIgv');
    const cartTotal = document.getElementById('cartTotal');
    const btnConfirmarVenta = document.getElementById('btnConfirmarVenta');

    function saveCartToStorage() {
        localStorage.setItem('pos_cart', JSON.stringify(carrito));
    }

    function savePayMethodToStorage() {
        localStorage.setItem('pos_pay_method', JSON.stringify(selectedPayMethod));
    }

    // RENDERIZAR CARRITO (POS INDEX)
    function renderCart() {
        if (!cartTableBody) return;
        if (carrito.length === 0) {
            cartTableBody.innerHTML = `<tr><td colspan="5" style="text-align: center; color: #888; padding: 20px;">El carrito de compras está vacío.</td></tr>`;
            if (cartSubtotal) cartSubtotal.innerText = 'S/ 0.00';
            if (cartIgv) cartIgv.innerText = 'S/ 0.00';
            if (cartTotal) cartTotal.innerText = 'S/ 0.00';
            if (typeof fetchRecommendations === 'function') {
                fetchRecommendations();
            }
            return;
        }

        let html = '';
        let total = 0;
        carrito.forEach((item) => {
            const itemSubtotal = item.precio * item.cantidad;
            total += itemSubtotal;
            html += `
                <tr data-id="${item.id}">
                    <td>
                        <strong>${item.nombre}</strong><br>
                        <small style="color: #888;">Cód: ${item.codigo}</small>
                    </td>
                    <td>
                        <div style="display: flex; align-items: center; justify-content: center; gap: 5px;">
                            <button type="button" class="btn-qty-minus" data-id="${item.id}" style="padding: 2px 8px; border: 1px solid #ccc; background: #fff; cursor: pointer; border-radius: 3px;">-</button>
                            <input type="number" class="input-qty" data-id="${item.id}" value="${item.cantidad}" min="1" max="${item.stockMax}" style="width: 55px; text-align: center; border: 1px solid #ccc; border-radius: 3px;" />
                            <button type="button" class="btn-qty-plus" data-id="${item.id}" style="padding: 2px 8px; border: 1px solid #ccc; background: #fff; cursor: pointer; border-radius: 3px;">+</button>
                        </div>
                    </td>
                    <td>S/ ${parseFloat(item.precio).toFixed(2)}</td>
                    <td>S/ ${parseFloat(itemSubtotal).toFixed(2)}</td>
                    <td>
                        <button type="button" class="btn-remove-cart" data-id="${item.id}" style="border: none; background: none; color: #e74c3c; cursor: pointer; font-size: 1.1rem;" title="Eliminar">
                            <i class="fa-solid fa-trash-can"></i>
                        </button>
                    </td>
                </tr>
            `;
        });

        cartTableBody.innerHTML = html;

        // Los precios de los productos son precios NETOS (sin IGV).
        // Cuando se activa el IGV, se suma 18% encima del subtotal neto.
        const applyIgv = localStorage.getItem('pos_apply_igv') !== 'false';
        const cartApplyIgv = document.getElementById('cartApplyIgv');
        if (cartApplyIgv) {
            cartApplyIgv.checked = applyIgv;
        }

        // 'total' aquí es la suma de precios netos de los items
        const subtotal = total;
        const igv = applyIgv ? subtotal * 0.18 : 0.00;
        const totalFinal = subtotal + igv;

        if (cartSubtotal) cartSubtotal.innerText = `S/ ${subtotal.toFixed(2)}`;
        if (cartIgv) cartIgv.innerText = `S/ ${igv.toFixed(2)}`;
        if (cartTotal) cartTotal.innerText = `S/ ${totalFinal.toFixed(2)}`;

        if (typeof fetchRecommendations === 'function') {
            fetchRecommendations();
        }
    }

    // inicializar carrito si estamos en POS
    if (cartTableBody) {
        // --- ESTADO Y LÓGICA DE RECOMENDACIONES IA ---
        let ignoredRecommendations = new Set();
        let recommendationsDismissed = false;
        let lastCartIds = "";
        let activeRecommendations = [];

        // Crear contenedor dinámico para el overlay de recomendaciones
        const recOverlay = document.createElement('div');
        recOverlay.id = 'ai-recommendations-overlay';
        recOverlay.className = 'ai-rec-overlay hidden';
        document.body.appendChild(recOverlay);

        function hideRecommendationsOverlay() {
            recOverlay.classList.add('hidden');
            recOverlay.innerHTML = '';
        }

        fetchRecommendations = async function() {
            // Obtener IDs ordenados de productos en el carrito
            const currentIds = carrito.map(item => item.id).sort((a, b) => a - b).join(',');
            
            if (!currentIds) {
                hideRecommendationsOverlay();
                lastCartIds = "";
                ignoredRecommendations.clear();
                recommendationsDismissed = false;
                return;
            }

            if (recommendationsDismissed) return;

            if (currentIds !== lastCartIds) {
                // El carrito cambió de productos, reseteamos ignorados y descarte para refrescar sugerencias
                ignoredRecommendations.clear();
                recommendationsDismissed = false;
                lastCartIds = currentIds;
            } else {
                // El carrito no ha cambiado en productos (solo cantidades), no hacemos fetch redundante
                return;
            }

            try {
                const response = await fetch(`/api/ia/recommend?carrito=${currentIds}`);
                if (!response.ok) throw new Error('Error al obtener recomendaciones de la API');
                const data = await response.json();
                
                // Filtrar las recomendaciones devueltas
                const filtered = data.filter(prod => {
                    const inCart = carrito.some(item => item.id === prod.idproducto);
                    const ignored = ignoredRecommendations.has(prod.idproducto);
                    
                    // Verificar si el producto sugerido está en el catálogo y tiene stock > 0
                    const card = document.querySelector(`.pos-product-card[data-id="${prod.idproducto}"]`);
                    const stock = card ? parseInt(card.getAttribute('data-stock')) : 0;
                    
                    return !inCart && !ignored && stock > 0;
                });

                if (filtered.length > 0) {
                    activeRecommendations = filtered;
                    renderRecommendationsOverlay();
                } else {
                    hideRecommendationsOverlay();
                }
            } catch (err) {
                console.error('Error al consultar recomendaciones de la IA:', err);
            }
        }

        function renderRecommendationsOverlay() {
            if (activeRecommendations.length === 0) {
                hideRecommendationsOverlay();
                return;
            }

            let html = `
                <div class="ai-rec-header">
                    <h3><i class="fa-solid fa-lightbulb"></i> Sugerencias de compra (IA)</h3>
                </div>
                <div class="ai-rec-body">
            `;

            activeRecommendations.forEach(prod => {
                const imgUrl = prod.imagenUrl || '/img/placeholder-prod.png';
                const precioFormatted = parseFloat(prod.precio).toFixed(2);
                
                // Obtener datos del DOM del catálogo
                const card = document.querySelector(`.pos-product-card[data-id="${prod.idproducto}"]`);
                const codigo = card ? card.getAttribute('data-codigo') : `PROD${prod.idproducto}`;
                const stockMax = card ? parseInt(card.getAttribute('data-stock')) : 999;
                const unidad = card ? card.getAttribute('data-unidad') : 'unidades';

                html += `
                    <div class="ai-rec-item" data-id="${prod.idproducto}">
                        <div class="ai-rec-item-info">
                            <img src="${imgUrl}" class="ai-rec-item-img" alt="${prod.nombre}">
                            <div class="ai-rec-item-details">
                                <span class="ai-rec-item-name">${prod.nombre}</span>
                                <span class="ai-rec-item-price">S/ ${precioFormatted}</span>
                            </div>
                        </div>
                        <div class="ai-rec-item-actions">
                            <button type="button" class="ai-rec-btn-add" data-id="${prod.idproducto}" data-nombre="${prod.nombre}" data-codigo="${codigo}" data-precio="${prod.precio}" data-stock="${stockMax}" data-unidad="${unidad}">
                                <i class="fa-solid fa-plus"></i> Agregar
                            </button>
                            <button type="button" class="ai-rec-btn-ignore" data-id="${prod.idproducto}">
                                <i class="fa-solid fa-eye-slash"></i> Ignorar
                            </button>
                        </div>
                    </div>
                `;
            });

            html += `
                </div>
                <div class="ai-rec-footer">
                    <button type="button" class="ai-rec-btn-cancel-all" id="ai-rec-cancel-all">Ignorar todas las sugerencias</button>
                </div>
            `;

            recOverlay.innerHTML = html;
            recOverlay.classList.remove('hidden');

            // Event listeners
            recOverlay.querySelectorAll('.ai-rec-btn-add').forEach(btn => {
                btn.addEventListener('click', () => {
                    const id = parseInt(btn.getAttribute('data-id'));
                    const nombre = btn.getAttribute('data-nombre');
                    const codigo = btn.getAttribute('data-codigo');
                    const precio = parseFloat(btn.getAttribute('data-precio'));
                    const stockMax = parseInt(btn.getAttribute('data-stock'));
                    const unidad = btn.getAttribute('data-unidad');

                    const success = addToCartById(id, nombre, codigo, precio, stockMax, unidad);
                    if (success) {
                        activeRecommendations = activeRecommendations.filter(p => p.idproducto !== id);
                        renderRecommendationsOverlay();
                    }
                });
            });

            recOverlay.querySelectorAll('.ai-rec-btn-ignore').forEach(btn => {
                btn.addEventListener('click', () => {
                    const id = parseInt(btn.getAttribute('data-id'));
                    ignoredRecommendations.add(id);
                    activeRecommendations = activeRecommendations.filter(p => p.idproducto !== id);
                    renderRecommendationsOverlay();
                });
            });

            const btnCancelAll = recOverlay.querySelector('#ai-rec-cancel-all');
            if (btnCancelAll) {
                btnCancelAll.addEventListener('click', () => {
                    activeRecommendations.forEach(p => ignoredRecommendations.add(p.idproducto));
                    activeRecommendations = [];
                    recommendationsDismissed = true;
                    hideRecommendationsOverlay();
                });
            }
        }

        renderCart();

        const cartApplyIgv = document.getElementById('cartApplyIgv');
        if (cartApplyIgv) {
            cartApplyIgv.addEventListener('change', (e) => {
                localStorage.setItem('pos_apply_igv', e.target.checked);
                renderCart();
            });
        }

        // Controladores del Carrito (Delegación de eventos)
        cartTableBody.addEventListener('click', (e) => {
            const btnMinus = e.target.closest('.btn-qty-minus');
            const btnPlus = e.target.closest('.btn-qty-plus');
            const btnRemove = e.target.closest('.btn-remove-cart');

            if (btnMinus) {
                const id = parseInt(btnMinus.getAttribute('data-id'));
                const item = carrito.find(i => i.id === id);
                if (item && item.cantidad > 1) {
                    item.cantidad--;
                    saveCartToStorage();
                    renderCart();
                }
            }
            if (btnPlus) {
                const id = parseInt(btnPlus.getAttribute('data-id'));
                const item = carrito.find(i => i.id === id);
                if (item) {
                    if (item.cantidad < item.stockMax) {
                        item.cantidad++;
                        saveCartToStorage();
                        renderCart();
                    } else {
                        alert(`No hay suficiente stock disponible. Stock máximo: ${item.stockMax}`);
                    }
                }
            }
            if (btnRemove) {
                const id = parseInt(btnRemove.getAttribute('data-id'));
                carrito = carrito.filter(i => i.id !== id);
                saveCartToStorage();
                renderCart();
            }
        });

        cartTableBody.addEventListener('change', (e) => {
            const inputQty = e.target.closest('.input-qty');
            if (inputQty) {
                const id = parseInt(inputQty.getAttribute('data-id'));
                const item = carrito.find(i => i.id === id);
                if (item) {
                    let val = parseInt(inputQty.value);
                    if (isNaN(val) || val < 1) {
                        val = 1;
                    }
                    if (val > item.stockMax) {
                        alert(`No hay suficiente stock disponible. Stock máximo: ${item.stockMax}`);
                        val = item.stockMax;
                    }
                    item.cantidad = val;
                    saveCartToStorage();
                    renderCart();
                }
            }
        });
    }

    // FUNCIÓN AUXILIAR PARA AGREGAR AL CARRITO POR ID
    function addToCartById(id, nombre, codigo, precio, stockMax, unidad) {
        if (stockMax <= 0) {
            alert('Este producto está agotado (sin stock).');
            return false;
        }

        const existing = carrito.find(i => i.id === id);
        if (existing) {
            if (existing.cantidad < stockMax) {
                existing.cantidad++;
            } else {
                alert(`No se puede agregar más. Stock máximo: ${stockMax}`);
                return false;
            }
        } else {
            carrito.push({
                id: id,
                nombre: nombre,
                codigo: codigo,
                precio: precio,
                cantidad: 1,
                stockMax: stockMax,
                unidad: unidad
            });
        }

        saveCartToStorage();
        renderCart();
        return true;
    }

    // CLIC EN TARJETAS DE PRODUCTO
    document.querySelectorAll('.btn-pos-add-to-cart').forEach(card => {
        card.addEventListener('click', () => {
            const id = parseInt(card.getAttribute('data-id'));
            const nombre = card.getAttribute('data-nombre');
            const codigo = card.getAttribute('data-codigo');
            const precio = parseFloat(card.getAttribute('data-precio'));
            const stockMax = parseInt(card.getAttribute('data-stock'));
            const unidad = card.getAttribute('data-unidad');
            addToCartById(id, nombre, codigo, precio, stockMax, unidad);
        });
    });

    // FILTRO DE CATEGORÍAS Y BÚSQUEDA (POS INDEX)
    const posCategoriesRow = document.getElementById('posCategoriesRow');
    const posSearchInput = document.getElementById('posSearchInput');
    const productCards = document.querySelectorAll('.btn-pos-add-to-cart');

    function filterProducts() {
        const activeCatBtn = posCategoriesRow ? posCategoriesRow.querySelector('.pos-cat-btn.active') : null;
        const catId = activeCatBtn ? activeCatBtn.getAttribute('data-id') : '';
        const query = posSearchInput ? posSearchInput.value.toLowerCase().trim() : '';

        productCards.forEach(card => {
            const cardCat = card.getAttribute('data-categoria');
            const cardNombre = card.getAttribute('data-nombre').toLowerCase();
            const cardCodigo = card.getAttribute('data-codigo').toLowerCase();

            const matchesCategory = (catId === '' || cardCat === catId);
            const matchesSearch = (query === '' || cardNombre.includes(query) || cardCodigo.includes(query));

            if (matchesCategory && matchesSearch) {
                card.style.display = 'block';
            } else {
                card.style.display = 'none';
            }
        });
    }

    if (posCategoriesRow) {
        posCategoriesRow.addEventListener('click', (e) => {
            const catBtn = e.target.closest('.pos-cat-btn');
            if (catBtn) {
                posCategoriesRow.querySelectorAll('.pos-cat-btn').forEach(btn => btn.classList.remove('active'));
                catBtn.classList.add('active');
                filterProducts();
            }
        });
    }

    if (posSearchInput) {
        posSearchInput.addEventListener('input', filterProducts);
    }



    // SELECCIÓN DE MÉTODO DE PAGO EN EL COMBOBOX DE POS
    const posPaymentSelect = document.getElementById('posPaymentSelect');
    if (posPaymentSelect) {
        // Establecer el método activo guardado en el select
        posPaymentSelect.value = selectedPayMethod.id;

        posPaymentSelect.addEventListener('change', () => {
            const selectedOption = posPaymentSelect.options[posPaymentSelect.selectedIndex];
            selectedPayMethod = {
                id: parseInt(posPaymentSelect.value),
                nombre: selectedOption.getAttribute('data-nombre')
            };
            savePayMethodToStorage();
        });
    }

    // VALIDACIÓN AL CLICKEAR "PAGAR / CONFIRMAR VENTA"
    if (btnConfirmarVenta) {
        btnConfirmarVenta.addEventListener('click', (e) => {
            if (!window.cajaEstaAbierta) {
                e.preventDefault();
                mostrarAlertaCajaCerrada();
                return;
            }
            if (carrito.length === 0) {
                e.preventDefault();
                alert('El carrito de compras está vacío. Agregue algún producto antes de proceder.');
            }
        });
    }


    // --- LÓGICA DE CONFIRMAR VENTA (confirmar.html) ---
    const confirmTableBody = document.getElementById('confirmTableBody');
    const confirmClienteSelect = document.getElementById('confirmClienteSelect');
    const confirmNroDocumento = document.getElementById('confirmNroDocumento');
    const confirmTipoClienteDisplay = document.getElementById('confirmTipoClienteDisplay');
    const confirmDireccion = document.getElementById('confirmDireccion');
    const confirmCorreo = document.getElementById('confirmCorreo');
    const confirmTelefono = document.getElementById('confirmTelefono');
    const confirmMetodoSelect = document.getElementById('confirmMetodoSelect');
    const confirmVendedorSelect = document.getElementById('confirmVendedorSelect');
    const confirmComprobanteSerie = document.getElementById('confirmComprobanteSerie');
    const btnConfirmarFinal = document.getElementById('btnConfirmarFinal');
    const clientToggleBtnsCustom = document.querySelectorAll('.cv-toggle-btn[data-target="cliente"]');
    const groupClienteSelect = document.getElementById('groupClienteSelect');

    // Función auxiliar para obtener o crear cliente ocasional
    async function getOrCreateOccasionalClient() {
        try {
            const response = await fetch('/api/clientes');
            if (!response.ok) throw new Error('Error al listar clientes');
            const clientes = await response.json();
            let occasional = clientes.find(c => c.nroDocumento === '00000000');
            if (occasional) {
                return occasional;
            }

            // Si no existe, lo creamos
            const newClientResponse = await fetch('/api/clientes', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    nombre: 'Cliente Ocasional',
                    razonsocial: 'Cliente Ocasional',
                    nroDocumento: '00000000',
                    idtipocliente: 1, // Persona Natural por defecto
                    estado: true
                })
            });
            if (!newClientResponse.ok) throw new Error('Error al crear cliente ocasional');
            occasional = await newClientResponse.json();
            return occasional;
        } catch (err) {
            console.error('Error en getOrCreateOccasionalClient:', err);
            return null;
        }
    }

    if (confirmTableBody) {
        // Cargamos carrito
        const confirmCart = JSON.parse(localStorage.getItem('pos_cart')) || [];
        if (confirmCart.length === 0) {
            alert('El carrito de compras está vacío. Regresando al Punto de Venta.');
            window.location.href = '/nuevaventa';
            return;
        }

        // Renderizar tabla resumen
        let tableHtml = '';
        let total = 0;
        confirmCart.forEach(item => {
            const sub = item.precio * item.cantidad;
            total += sub;
            tableHtml += `
                <tr>
                    <td>${item.nombre}<br><small>(${item.codigo})</small></td>
                    <td>${item.cantidad} ${item.unidad === 'Kilogramo' ? 'kg' : 'und'}</td>
                    <td>S/ ${parseFloat(item.precio).toFixed(2)}</td>
                    <td>S/ ${parseFloat(sub).toFixed(2)}</td>
                </tr>
            `;
        });
        confirmTableBody.innerHTML = tableHtml;

        // Cálculos e impresión de totalizadores
        const toggleIgvCheckbox = document.getElementById('toggleIgvCheckbox');

        // 'total' aquí es la suma de precios netos de los items del carrito.
        // La función recalcula sumando IGV encima cuando está activo.
        function recalcularTotalesConfirmar(subtotalNeto) {
            const applyIgv = localStorage.getItem('pos_apply_igv') !== 'false';

            if (toggleIgvCheckbox) {
                toggleIgvCheckbox.checked = applyIgv;
            }

            const tax = applyIgv ? subtotalNeto * 0.18 : 0.00;
            const totalFinal = subtotalNeto + tax;

            document.getElementById('confirmSubtotal').innerText = `S/ ${subtotalNeto.toFixed(2)}`;
            document.getElementById('confirmIgv').innerText = `S/ ${tax.toFixed(2)}`;
            document.getElementById('confirmTotal').innerText = `S/ ${totalFinal.toFixed(2)}`;

            const badgeAmount = document.getElementById('confirmPaymentAmountBadge');
            if (badgeAmount) {
                badgeAmount.innerText = `S/ ${totalFinal.toFixed(2)}`;
            }
        }

        if (toggleIgvCheckbox) {
            toggleIgvCheckbox.addEventListener('change', (e) => {
                localStorage.setItem('pos_apply_igv', e.target.checked);
                recalcularTotalesConfirmar(total);
            });
        }

        recalcularTotalesConfirmar(total);

        // Inicializar Método de Pago de acuerdo al POS
        let initialPayMethod = JSON.parse(localStorage.getItem('pos_pay_method'));
        if (!initialPayMethod || !initialPayMethod.id) {
            initialPayMethod = {
                id: 1,
                nombre: 'Efectivo'
            };
        }
        if (confirmMetodoSelect && initialPayMethod) {
            confirmMetodoSelect.value = initialPayMethod.id;
        }
        const confirmMetodoNombre = document.getElementById('confirmMetodoNombre');
        if (confirmMetodoNombre && initialPayMethod) {
            confirmMetodoNombre.value = initialPayMethod.nombre;
        }

        const badgeMethod = document.getElementById('confirmPaymentMethodBadge');
        const badgeAmount = document.getElementById('confirmPaymentAmountBadge');
        if (badgeMethod && initialPayMethod) badgeMethod.innerText = `Metodo: ${initialPayMethod.nombre}`;
        if (badgeAmount) badgeAmount.innerText = `S/ ${total.toFixed(2)}`;

        // Configuración y visualización dinámica del Código de Comprobación
        function setupCodigoComprobacion(metodoId, metodoNombre) {
            const groupCodigo = document.getElementById('groupCodigoComprobacion');
            const lblCodigo = document.getElementById('lblCodigoComprobacion');
            const inputCodigo = document.getElementById('confirmCodigoComprobacion');

            if (!groupCodigo || !lblCodigo || !inputCodigo) return;

            const nameLower = metodoNombre.toLowerCase();

            // 1. EFECTIVO
            if (metodoId === 1 || nameLower.includes('efectivo')) {
                groupCodigo.style.display = 'none';
                inputCodigo.value = '';
                inputCodigo.required = false;
            }
            // 2. YAPE / PLIN (Presencial)
            else if (metodoId === 2 || (nameLower.includes('yape') && nameLower.includes('plin')) || nameLower.includes('presencial')) {
                groupCodigo.style.display = 'block';
                lblCodigo.innerHTML = 'Código de Verificación Dinámico <span class="cv-text-red">*</span>';
                inputCodigo.placeholder = 'Ingresar 3 dígitos visibles en la app del cliente';
                inputCodigo.maxLength = 3;
                inputCodigo.required = true;
            }
            // 3. YAPE (E-commerce / Pasarela Web)
            else if (metodoId === 5 || nameLower.includes('e-commerce') || nameLower.includes('pasarela')) {
                groupCodigo.style.display = 'block';
                lblCodigo.innerHTML = 'Código de Aprobación de Compra <span class="cv-text-red">*</span>';
                inputCodigo.placeholder = 'Ingresar 6 dígitos de aprobación';
                inputCodigo.maxLength = 6;
                inputCodigo.required = true;
            }
            // 4. TARJETA DE CRÉDITO / DÉBITO (POS Físico)
            else if (metodoId === 4 || nameLower.includes('tarjeta') || nameLower.includes('pos') || nameLower.includes('niubiz') || nameLower.includes('izipay')) {
                groupCodigo.style.display = 'block';
                lblCodigo.innerHTML = 'Número de Operación / Autorización <span class="cv-text-red">*</span>';
                inputCodigo.placeholder = 'Ingresar entre 4 y 8 caracteres del váucher';
                inputCodigo.removeAttribute('maxLength');
                inputCodigo.required = true;
            }
            // 5. TRANSFERENCIA BANCARIA / DEPÓSITO
            else if (metodoId === 3 || nameLower.includes('transferencia') || nameLower.includes('depósito') || nameLower.includes('deposito')) {
                groupCodigo.style.display = 'block';
                lblCodigo.innerHTML = 'Número de Operación Bancaria <span class="cv-text-red">*</span>';
                inputCodigo.placeholder = 'Ingresar número de operación bancaria';
                inputCodigo.removeAttribute('maxLength');
                inputCodigo.required = true;
            }
            else {
                groupCodigo.style.display = 'block';
                lblCodigo.innerHTML = 'Código de Verificación / Operación <span class="cv-text-red">*</span>';
                inputCodigo.placeholder = 'Ingresar código de operación';
                inputCodigo.removeAttribute('maxLength');
                inputCodigo.required = true;
            }
        }

        function actualizarEstadoPagoBadge() {
            const badge = document.getElementById('confirmPaymentBadge');
            const icon = document.getElementById('confirmPaymentIcon');
            const title = document.getElementById('confirmPaymentTitle');
            const subtext = document.getElementById('confirmPaymentMethodBadge');
            const confirmCodigoComprobacion = document.getElementById('confirmCodigoComprobacion');

            if (!badge || !icon || !title) return;

            const metodoId = initialPayMethod.id;
            const metodoNombre = initialPayMethod.nombre;
            const nameLower = metodoNombre.toLowerCase();
            const codigoVal = confirmCodigoComprobacion ? confirmCodigoComprobacion.value.trim() : '';

            // 1. EFECTIVO -> Siempre recibido
            if (metodoId === 1 || nameLower.includes('efectivo')) {
                badge.className = 'cv-success-alert';
                icon.className = 'fa-solid fa-shield-check cv-success-icon';
                title.innerText = 'Pago recibido';
                if (subtext) subtext.innerText = `Metodo: ${metodoNombre}`;
                return;
            }

            // 2. Otros métodos -> Depende de si se ingresó un código válido
            let esValido = false;

            if (codigoVal !== '') {
                // 2. YAPE / PLIN (Presencial) -> Numérico de exactamente 3 dígitos
                if (metodoId === 2 || (nameLower.includes('yape') && nameLower.includes('plin')) || nameLower.includes('presencial')) {
                    esValido = /^\d{3}$/.test(codigoVal);
                }
                // 3. YAPE (E-commerce / Pasarela Web) -> Numérico de exactamente 6 dígitos
                else if (metodoId === 5 || nameLower.includes('e-commerce') || nameLower.includes('pasarela')) {
                    esValido = /^\d{6}$/.test(codigoVal);
                }
                // 4. TARJETA DE CRÉDITO / DÉBITO (POS Físico) -> Alfanumérico, entre 4 y 8 caracteres
                else if (metodoId === 4 || nameLower.includes('tarjeta') || nameLower.includes('pos') || nameLower.includes('niubiz') || nameLower.includes('izipay')) {
                    esValido = /^[a-zA-Z0-9]{4,8}$/.test(codigoVal);
                }
                // 5. TRANSFERENCIA BANCARIA / DEPÓSITO -> Alfanumérico
                else if (metodoId === 3 || nameLower.includes('transferencia') || nameLower.includes('depósito') || nameLower.includes('deposito')) {
                    esValido = /^[a-zA-Z0-9]+$/.test(codigoVal);
                } else {
                    esValido = true;
                }
            }

            if (esValido) {
                badge.className = 'cv-success-alert';
                icon.className = 'fa-solid fa-shield-check cv-success-icon';
                title.innerText = 'Pago recibido';
                if (subtext) subtext.innerText = `Metodo: ${metodoNombre} (Cod: ${codigoVal})`;
            } else {
                badge.className = 'cv-pending-alert';
                icon.className = 'fa-solid fa-circle-exclamation cv-pending-icon';
                title.innerText = 'Pago Pendiente de Verificación';
                if (subtext) {
                    if (codigoVal === '') {
                        subtext.innerText = `Metodo: ${metodoNombre} (Esperando código...)`;
                    } else {
                        subtext.innerText = `Metodo: ${metodoNombre} (Código inválido)`;
                    }
                }
            }
        }

        const confirmCodigoComprobacion = document.getElementById('confirmCodigoComprobacion');
        if (confirmCodigoComprobacion) {
            confirmCodigoComprobacion.addEventListener('input', () => {
                actualizarEstadoPagoBadge();
            });
        }

        if (initialPayMethod) {
            setupCodigoComprobacion(initialPayMethod.id, initialPayMethod.nombre);
            actualizarEstadoPagoBadge();
        }

        // Fecha de Emisión automática en el input
        const confirmFechaEmision = document.getElementById('confirmFechaEmision');
        if (confirmFechaEmision) {
            const now = new Date();
            const day = String(now.getDate()).padStart(2, '0');
            const month = String(now.getMonth() + 1).padStart(2, '0');
            const year = now.getFullYear();
            const hours = String(now.getHours()).padStart(2, '0');
            const minutes = String(now.getMinutes()).padStart(2, '0');
            confirmFechaEmision.value = `${day}/${month}/${year} ${hours}:${minutes}`;
        }

        // Evento cambio de Cliente registrado
        if (confirmClienteSelect) {
            confirmClienteSelect.addEventListener('change', () => {
                const clientId = confirmClienteSelect.value;
                if (!clientId) {
                    if (confirmNroDocumento) confirmNroDocumento.value = '';
                    if (confirmTipoClienteDisplay) confirmTipoClienteDisplay.value = '';
                    if (confirmDireccion) confirmDireccion.value = '';
                    if (confirmCorreo) confirmCorreo.value = '';
                    if (confirmTelefono) confirmTelefono.value = '';
                    return;
                }

                fetch(`/api/clientes/${clientId}`)
                    .then(res => {
                        if (!res.ok) throw new Error('Error al obtener cliente');
                        return res.json();
                    })
                    .then(cliente => {
                        const doc = cliente.nroDocumento || '';
                        const name = cliente.nombre || cliente.razonsocial || '';

                        if (confirmNroDocumento) confirmNroDocumento.value = doc;
                        if (confirmTipoClienteDisplay) confirmTipoClienteDisplay.value = cliente.tipoClienteNombre || 'Sin tipo';

                        // Simulamos datos de contacto de forma consistente
                        const tel = "9" + (doc && doc.length >= 8 ? doc.slice(-8) : "87654321");
                        const email = name.toLowerCase().trim().replace(/\s+/g, ".") + "@gmail.com";
                        const address = "Jr. Charapita " + (doc || "");

                        if (confirmDireccion) confirmDireccion.value = address;
                        if (confirmCorreo) confirmCorreo.value = email;
                        if (confirmTelefono) confirmTelefono.value = tel;
                    })
                    .catch(err => {
                        console.error('Error fetching client details:', err);
                    });
            });
        }

        // --- BÚSQUEDA Y FILTRADO DE CLIENTES ---
        let clientesOriginales = [];
        if (confirmClienteSelect) {
            for (let i = 0; i < confirmClienteSelect.options.length; i++) {
                const opt = confirmClienteSelect.options[i];
                if (opt.value !== "") {
                    clientesOriginales.push({
                        value: opt.value,
                        text: opt.text,
                        doc: opt.getAttribute('data-doc') || '',
                        nombre: opt.getAttribute('data-nombre') || '',
                        tipo: opt.getAttribute('data-tipo') || ''
                    });
                }
            }
        }

        const buscarClienteInput = document.getElementById('buscarClienteInput');
        if (buscarClienteInput && confirmClienteSelect) {
            buscarClienteInput.addEventListener('input', () => {
                const query = buscarClienteInput.value.toLowerCase().trim();
                confirmClienteSelect.innerHTML = '<option value="">-- Seleccionar Cliente --</option>';

                clientesOriginales.forEach(c => {
                    const matchesDoc = c.doc.toLowerCase().includes(query);
                    const matchesNombre = c.nombre.toLowerCase().includes(query);
                    const matchesText = c.text.toLowerCase().includes(query);

                    if (query === '' || matchesDoc || matchesNombre || matchesText) {
                        const opt = document.createElement('option');
                        opt.value = c.value;
                        opt.text = c.text;
                        opt.setAttribute('data-doc', c.doc);
                        opt.setAttribute('data-nombre', c.nombre);
                        opt.setAttribute('data-tipo', c.tipo);
                        confirmClienteSelect.appendChild(opt);
                    }
                });

                confirmClienteSelect.value = '';
                confirmClienteSelect.dispatchEvent(new Event('change'));
            });
        }

        // --- MODAL DE REGISTRO RÁPIDO DE CLIENTE ---
        const btnNuevoCliente = document.getElementById('btnNuevoCliente');
        const modalNuevoCliente = document.getElementById('modalNuevoCliente');
        const btnCerrarModalCliente = document.getElementById('btnCerrarModalCliente');
        const btnCancelarModalCliente = document.getElementById('btnCancelarModalCliente');
        const btnGuardarModalCliente = document.getElementById('btnGuardarModalCliente');

        if (btnNuevoCliente && modalNuevoCliente) {
            btnNuevoCliente.addEventListener('click', (e) => {
                e.preventDefault();
                const searchVal = buscarClienteInput ? buscarClienteInput.value.trim() : '';
                const modalNroDoc = document.getElementById('modalNroDocumento');

                if (searchVal !== '') {
                    if (modalNroDoc) {
                        modalNroDoc.value = searchVal;
                        modalNroDoc.readOnly = true;
                    }
                    const modalTipoCliente = document.getElementById('modalTipoCliente');
                    if (modalTipoCliente) {
                        if (searchVal.length === 8) {
                            modalTipoCliente.value = "1"; // Persona Natural
                        } else if (searchVal.length === 11) {
                            modalTipoCliente.value = "2"; // Persona Jurídica
                        }
                    }
                } else {
                    if (modalNroDoc) {
                        modalNroDoc.value = '';
                        modalNroDoc.readOnly = false;
                    }
                }
                modalNuevoCliente.classList.add('active');
            });
        }

        if (modalNuevoCliente) {
            const cerrarModal = (e) => {
                if (e) e.preventDefault();
                modalNuevoCliente.classList.remove('active');
                const modalNroDoc = document.getElementById('modalNroDocumento');
                if (modalNroDoc) {
                    modalNroDoc.value = '';
                    modalNroDoc.readOnly = false;
                }
            };
            if (btnCerrarModalCliente) btnCerrarModalCliente.addEventListener('click', cerrarModal);
            if (btnCancelarModalCliente) btnCancelarModalCliente.addEventListener('click', cerrarModal);
        }

        if (btnGuardarModalCliente) {
            btnGuardarModalCliente.addEventListener('click', (e) => {
                e.preventDefault();

                const tipoClienteId = document.getElementById('modalTipoCliente').value;
                const nroDoc = document.getElementById('modalNroDocumento').value.trim();
                const name = document.getElementById('modalNombre').value.trim();

                if (!nroDoc || !name || !tipoClienteId) {
                    alert('Por favor, complete los campos obligatorios: Tipo de Cliente, DNI/RUC y Nombre.');
                    return;
                }

                const payload = {
                    nombre: name,
                    razonsocial: name,
                    nroDocumento: nroDoc,
                    idtipocliente: parseInt(tipoClienteId),
                    estado: true
                };

                btnGuardarModalCliente.disabled = true;
                btnGuardarModalCliente.innerText = 'Guardando...';

                fetch('/api/clientes', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                })
                    .then(res => {
                        if (!res.ok) {
                            return res.text().then(text => { throw new Error(text || 'Error al guardar cliente') });
                        }
                        return res.json();
                    })
                    .then(newClient => {
                        btnGuardarModalCliente.disabled = false;
                        btnGuardarModalCliente.innerText = 'Guardar Cliente';

                        // 1. Add to select options dynamically
                        const opt = document.createElement('option');
                        opt.value = newClient.idcliente;
                        opt.text = `${newClient.nombre || newClient.razonsocial} - Doc: ${newClient.nroDocumento}`;
                        opt.setAttribute('data-doc', newClient.nroDocumento);
                        opt.setAttribute('data-nombre', newClient.nombre || newClient.razonsocial);
                        opt.setAttribute('data-tipo', newClient.tipoClienteNombre || '');
                        confirmClienteSelect.appendChild(opt);

                        // 2. Add to cached searchable array
                        clientesOriginales.push({
                            value: String(newClient.idcliente),
                            text: opt.text,
                            doc: newClient.nroDocumento,
                            nombre: newClient.nombre || newClient.razonsocial,
                            tipo: newClient.tipoClienteNombre || ''
                        });

                        // 3. Auto select newly registered client and trigger detail loading
                        confirmClienteSelect.value = newClient.idcliente;
                        confirmClienteSelect.dispatchEvent(new Event('change'));

                        // 4. Clear search bar so the newly selected option is visible
                        if (buscarClienteInput) buscarClienteInput.value = '';

                        // 5. Hide modal and clear modal fields
                        modalNuevoCliente.classList.remove('active');
                        document.getElementById('modalNroDocumento').value = '';
                        document.getElementById('modalNombre').value = '';
                        document.getElementById('modalTelefono').value = '';
                        document.getElementById('modalCorreo').value = '';
                        document.getElementById('modalDireccion').value = '';

                        alert('Cliente registrado y seleccionado con éxito.');
                    })
                    .catch(err => {
                        btnGuardarModalCliente.disabled = false;
                        btnGuardarModalCliente.innerText = 'Guardar Cliente';
                        alert('Error al registrar cliente: ' + err.message);
                        console.error(err);
                    });
            });
        }

        // Toggles Cliente Ocasional / Cliente Registrado
        if (clientToggleBtnsCustom.length > 0) {
            clientToggleBtnsCustom.forEach(btn => {
                btn.addEventListener('click', () => {
                    const action = btn.innerText.toLowerCase();
                    if (action.includes('ocasional')) {
                        if (groupClienteSelect) groupClienteSelect.style.display = 'none';
                        if (confirmNroDocumento) confirmNroDocumento.value = '00000000';
                        if (confirmTipoClienteDisplay) confirmTipoClienteDisplay.value = 'Persona Natural';
                        if (confirmDireccion) confirmDireccion.value = 'Sin dirección';
                        if (confirmCorreo) confirmCorreo.value = 'ocasional@charapita.com';
                        if (confirmTelefono) confirmTelefono.value = '999999999';
                        if (confirmClienteSelect) confirmClienteSelect.value = '';
                    } else {
                        if (groupClienteSelect) groupClienteSelect.style.display = 'block';
                        if (confirmNroDocumento) confirmNroDocumento.value = '';
                        if (confirmTipoClienteDisplay) confirmTipoClienteDisplay.value = '';
                        if (confirmDireccion) confirmDireccion.value = '';
                        if (confirmCorreo) confirmCorreo.value = '';
                        if (confirmTelefono) confirmTelefono.value = '';
                        if (confirmClienteSelect) confirmClienteSelect.value = '';
                    }
                });
            });
        }

        // Función para cargar automáticamente correlativo sumando +1 al último emitido
        async function actualizarCorrelativo() {
            if (!confirmComprobanteSerie) return;
            const numeroInput = document.getElementById('confirmComprobanteNumero');
            if (!numeroInput) return;

            const serieVal = confirmComprobanteSerie.value.toUpperCase().trim();
            if (serieVal.length !== 4) {
                numeroInput.value = '----';
                return;
            }

            try {
                const response = await fetch('/api/ventas');
                if (!response.ok) throw new Error('Error al listar ventas');
                const ventas = await response.json();

                let maxCorrelativo = 0;
                ventas.forEach(v => {
                    if (v.nroPedido && v.nroPedido.toUpperCase().startsWith(serieVal + '-')) {
                        const parts = v.nroPedido.split('-');
                        if (parts.length === 2) {
                            const num = parseInt(parts[1], 10);
                            if (!isNaN(num) && num > maxCorrelativo) {
                                maxCorrelativo = num;
                            }
                        }
                    }
                });

                const nextCorrelativo = maxCorrelativo + 1;
                numeroInput.value = String(nextCorrelativo).padStart(6, '0');
            } catch (err) {
                console.error('Error al obtener correlativo:', err);
                numeroInput.value = '000001';
            }
        }

        // Sincronizar el select de series con la caja de tipo de comprobante
        document.querySelectorAll('.cv-doc-box[data-target="comprobante"]').forEach(box => {
            box.addEventListener('click', () => {
                const docId = box.getAttribute('data-id'); // '1' = Boleta, '2' = Factura
                let serieDefault = 'B001';
                if (docId === '2') {
                    serieDefault = 'F001';
                }

                if (confirmComprobanteSerie) {
                    confirmComprobanteSerie.value = serieDefault;
                    actualizarCorrelativo();
                }
            });
        });

        // Detectar cambios manuales en la Serie para recalcular el correlativo
        if (confirmComprobanteSerie) {
            confirmComprobanteSerie.addEventListener('input', () => {
                confirmComprobanteSerie.value = confirmComprobanteSerie.value.toUpperCase();
                actualizarCorrelativo();
            });
        }

        // Inicializar por defecto (por ejemplo, con Boleta B001)
        if (confirmComprobanteSerie) {
            const activeComprobante = document.querySelector('.cv-doc-box[data-target="comprobante"].active');
            const docId = activeComprobante ? activeComprobante.getAttribute('data-id') : '1';
            confirmComprobanteSerie.value = (docId === '2') ? 'F001' : 'B001';
            actualizarCorrelativo();
        }

        // ENVÍO/CONFIRMACIÓN FINAL DE VENTA
        if (btnConfirmarFinal) {
            btnConfirmarFinal.addEventListener('click', async (e) => {
                if (!window.cajaEstaAbierta) {
                    mostrarAlertaCajaCerrada();
                    return;
                }
                e.preventDefault();

                const confirmCart = JSON.parse(localStorage.getItem('pos_cart')) || [];
                if (confirmCart.length === 0) {
                    alert('El carrito de compras está vacío.');
                    return;
                }

                // Validar SUNAT: Ventas > S/ 700.00 requieren identificación (no ocasional)
                let totalVenta = 0;
                confirmCart.forEach(item => {
                    totalVenta += item.precio * item.cantidad;
                });

                const activeToggleBtn = document.querySelector('.cv-toggle-btn[data-target="cliente"].active');
                const isOcasional = activeToggleBtn && activeToggleBtn.innerText.toLowerCase().includes('ocasional');

                let isSelectedOcasional = false;
                if (confirmClienteSelect && confirmClienteSelect.value) {
                    const selectedOpt = confirmClienteSelect.options[confirmClienteSelect.selectedIndex];
                    if (selectedOpt) {
                        const doc = selectedOpt.getAttribute('data-doc') || '';
                        const nombre = selectedOpt.getAttribute('data-nombre') || '';
                        if (confirmClienteSelect.value === '8' || doc === '00000000' || nombre.toLowerCase().includes('ocasional')) {
                            isSelectedOcasional = true;
                        }
                    }
                }

                if (totalVenta > 700) {
                    if (isOcasional || isSelectedOcasional || !confirmClienteSelect || !confirmClienteSelect.value) {
                        alert('Por regulación de la SUNAT, las ventas que superan los S/ 700.00 requieren obligatoriamente la identificación del cliente (no se permite Cliente Ocasional). Por favor, seleccione o registre un cliente con su DNI o RUC.');
                        return;
                    }
                }

                // Determinamos cliente
                let idcliente = null;

                if (isOcasional) {
                    btnConfirmarFinal.disabled = true;
                    btnConfirmarFinal.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Registrando...';

                    const occasional = await getOrCreateOccasionalClient();
                    if (!occasional) {
                        alert('No se pudo establecer el cliente ocasional en el sistema.');
                        btnConfirmarFinal.disabled = false;
                        btnConfirmarFinal.innerHTML = '<i class="fa-solid fa-file-invoice"></i> Generar Comprobante';
                        return;
                    }
                    idcliente = occasional.idcliente;
                } else {
                    if (!confirmClienteSelect || !confirmClienteSelect.value) {
                        alert('Debe seleccionar un cliente registrado de la lista.');
                        return;
                    }
                    idcliente = parseInt(confirmClienteSelect.value);
                }

                // Obtener vendedor
                const vendedorVal = confirmVendedorSelect ? confirmVendedorSelect.value : '';
                if (!vendedorVal) {
                    alert('Debe seleccionar el Vendedor.');
                    btnConfirmarFinal.disabled = false;
                    btnConfirmarFinal.innerHTML = '<i class="fa-solid fa-file-invoice"></i> Generar Comprobante';
                    return;
                }
                const idusuario = parseInt(vendedorVal);

                // Obtener método pago
                const metodoVal = confirmMetodoSelect ? confirmMetodoSelect.value : '';
                if (!metodoVal) {
                    alert('Debe seleccionar el Método de Pago.');
                    btnConfirmarFinal.disabled = false;
                    btnConfirmarFinal.innerHTML = '<i class="fa-solid fa-file-invoice"></i> Generar Comprobante';
                    return;
                }
                const idmetodopago = parseInt(metodoVal);

                // Validar Código de Comprobación según el método de pago
                const confirmCodigoComprobacion = document.getElementById('confirmCodigoComprobacion');
                const codigoVal = confirmCodigoComprobacion ? confirmCodigoComprobacion.value.trim() : '';
                const metodoNombreLower = initialPayMethod.nombre.toLowerCase();

                if (idmetodopago !== 1 && !metodoNombreLower.includes('efectivo')) {
                    if (codigoVal === '') {
                        alert('Debe ingresar el código de comprobación / operación bancaria.');
                        btnConfirmarFinal.disabled = false;
                        btnConfirmarFinal.innerHTML = '<i class="fa-solid fa-file-invoice"></i> Generar Comprobante';
                        return;
                    }

                    // 2. YAPE / PLIN (Presencial) -> Numérico de exactamente 3 dígitos
                    if (idmetodopago === 2 || (metodoNombreLower.includes('yape') && metodoNombreLower.includes('plin')) || metodoNombreLower.includes('presencial')) {
                        if (!/^\d{3}$/.test(codigoVal)) {
                            alert('El Código de Verificación Dinámico debe ser un número de exactamente 3 dígitos.');
                            btnConfirmarFinal.disabled = false;
                            btnConfirmarFinal.innerHTML = '<i class="fa-solid fa-file-invoice"></i> Generar Comprobante';
                            return;
                        }
                    }
                    // 3. YAPE (E-commerce / Pasarela Web) -> Numérico de exactamente 6 dígitos
                    else if (idmetodopago === 5 || metodoNombreLower.includes('e-commerce') || metodoNombreLower.includes('pasarela')) {
                        if (!/^\d{6}$/.test(codigoVal)) {
                            alert('El Código de Aprobación de Compra debe ser un número de exactamente 6 dígitos.');
                            btnConfirmarFinal.disabled = false;
                            btnConfirmarFinal.innerHTML = '<i class="fa-solid fa-file-invoice"></i> Generar Comprobante';
                            return;
                        }
                    }
                    // 4. TARJETA DE CRÉDITO / DÉBITO (POS Físico) -> Alfanumérico, entre 4 y 8 caracteres
                    else if (idmetodopago === 4 || metodoNombreLower.includes('tarjeta') || metodoNombreLower.includes('pos') || metodoNombreLower.includes('niubiz') || metodoNombreLower.includes('izipay')) {
                        if (!/^[a-zA-Z0-9]{4,8}$/.test(codigoVal)) {
                            alert('El Número de Operación / Autorización debe ser alfanumérico y tener entre 4 y 8 caracteres.');
                            btnConfirmarFinal.disabled = false;
                            btnConfirmarFinal.innerHTML = '<i class="fa-solid fa-file-invoice"></i> Generar Comprobante';
                            return;
                        }
                    }
                    // 5. TRANSFERENCIA BANCARIA / DEPÓSITO -> Alfanumérico
                    else if (idmetodopago === 3 || metodoNombreLower.includes('transferencia') || metodoNombreLower.includes('depósito') || metodoNombreLower.includes('deposito')) {
                        if (!/^[a-zA-Z0-9]+$/.test(codigoVal)) {
                            alert('El Número de Operación Bancaria debe ser alfanumérico.');
                            btnConfirmarFinal.disabled = false;
                            btnConfirmarFinal.innerHTML = '<i class="fa-solid fa-file-invoice"></i> Generar Comprobante';
                            return;
                        }
                    }
                }

                // Obtener tipo comprobante
                const activeDocBox = document.querySelector('.cv-doc-box[data-target="comprobante"].active');
                if (!activeDocBox) {
                    alert('Debe seleccionar un Tipo de Comprobante.');
                    btnConfirmarFinal.disabled = false;
                    btnConfirmarFinal.innerHTML = '<i class="fa-solid fa-file-invoice"></i> Generar Comprobante';
                    return;
                }
                const idtipocomprobante = parseInt(activeDocBox.getAttribute('data-id'));

                // Validar Serie y Correlativo de Comprobante
                const serieVal = confirmComprobanteSerie ? confirmComprobanteSerie.value.toUpperCase().trim() : '';
                const numeroInput = document.getElementById('confirmComprobanteNumero');
                const numeroVal = numeroInput ? numeroInput.value.trim() : '';

                if (serieVal.length !== 4) {
                    alert('La serie de comprobante debe tener exactamente 4 caracteres (ej. B001 o F001).');
                    btnConfirmarFinal.disabled = false;
                    btnConfirmarFinal.innerHTML = '<i class="fa-solid fa-file-invoice"></i> Generar Comprobante';
                    return;
                }
                if (idtipocomprobante === 1 && !serieVal.startsWith('B')) {
                    alert('Para Boletas, la serie debe comenzar obligatoriamente con la letra "B".');
                    btnConfirmarFinal.disabled = false;
                    btnConfirmarFinal.innerHTML = '<i class="fa-solid fa-file-invoice"></i> Generar Comprobante';
                    return;
                }
                if (idtipocomprobante === 2 && !serieVal.startsWith('F')) {
                    alert('Para Facturas, la serie debe comenzar obligatoriamente con la letra "F".');
                    btnConfirmarFinal.disabled = false;
                    btnConfirmarFinal.innerHTML = '<i class="fa-solid fa-file-invoice"></i> Generar Comprobante';
                    return;
                }
                if (numeroVal === '' || numeroVal === '----' || numeroVal === 'Automatico') {
                    alert('No se pudo generar un número correlativo válido para la serie ingresada.');
                    btnConfirmarFinal.disabled = false;
                    btnConfirmarFinal.innerHTML = '<i class="fa-solid fa-file-invoice"></i> Generar Comprobante';
                    return;
                }

                const nroPedidoCompleto = `${serieVal}-${numeroVal}`;

                // Armamos detalles
                const detalles = confirmCart.map(item => ({
                    idproducto: item.id,
                    cantidad: item.cantidad,
                    precioU: item.precio,
                    importe: item.precio * item.cantidad
                }));

                const toggleIgvCheckbox = document.getElementById('toggleIgvCheckbox');
                const applyIgvVal = toggleIgvCheckbox ? toggleIgvCheckbox.checked : true;

                const payload = {
                    idcliente: idcliente,
                    idusuario: idusuario,
                    idtipocomprobante: idtipocomprobante,
                    idmetodopago: idmetodopago,
                    nroOperacion: codigoVal, // Enviar el código de comprobación en el campo nroOperacion
                    nroPedido: nroPedidoCompleto, // Enviar el número de comprobante completo
                    applyIgv: applyIgvVal,
                    detalles: detalles
                };

                btnConfirmarFinal.disabled = true;
                btnConfirmarFinal.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Registrando...';

                fetch('/api/ventas', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(payload)
                })
                    .then(res => {
                        if (!res.ok) {
                            return res.text().then(text => { throw new Error(text || 'Error al guardar la venta') });
                        }
                        return res.json();
                    })
                    .then(() => {
                        alert('¡Venta confirmada y registrada en el sistema con éxito!');
                        // Vaciar carrito local
                        localStorage.removeItem('pos_cart');
                        // Redirigir al historial de ventas
                        window.location.href = '/historial';
                    })
                    .catch(err => {
                        alert('Error al registrar la venta: ' + err.message);
                        console.error(err);
                        btnConfirmarFinal.disabled = false;
                        btnConfirmarFinal.innerHTML = '<i class="fa-solid fa-file-invoice"></i> Generar Comprobante';
                    });
            });
        }
    }

    // =========================================================
    // LÓGICA COMPLETA DE HISTORIAL DE VENTAS (historial/index.html)
    // =========================================================
    const btnBuscarHistorial = document.getElementById('btnBuscarHistorial');
    const btnLimpiarHistorial = document.getElementById('btnLimpiarHistorial');
    const histTableBody = document.getElementById('historialTableBody');

    function filterSales() {
        const desdeInput = document.getElementById('filterFechaDesde');
        const hastaInput = document.getElementById('filterFechaHasta');
        const tipoSelect = document.getElementById('filterTipoComprobante');
        const estadoSelect = document.getElementById('filterEstado');
        const searchInput = document.getElementById('filterSearchInput');

        if (!desdeInput || !hastaInput || !tipoSelect || !estadoSelect || !searchInput) return;

        const desdeVal = desdeInput.value; // YYYY-MM-DD
        const hastaVal = hastaInput.value; // YYYY-MM-DD
        const tipoVal = tipoSelect.value; // Todos / Boleta / Factura
        const estadoVal = estadoSelect.value; // Todas / Completada / Anulada
        const query = searchInput.value.toLowerCase().trim();

        const rows = document.querySelectorAll('#historialTableBody tr');
        let visibleCount = 0;

        rows.forEach(row => {
            const rowFecha = row.getAttribute('data-fecha'); // YYYY-MM-DD
            const rowComp = row.getAttribute('data-comprobante'); // Boleta / Factura
            const rowEst = row.getAttribute('data-estado'); // Completada / Anulada
            const rowSearch = (row.getAttribute('data-search') || '').toLowerCase();

            let matchesFecha = true;
            if (rowFecha) {
                if (desdeVal && rowFecha < desdeVal) matchesFecha = false;
                if (hastaVal && rowFecha > hastaVal) matchesFecha = false;
            }

            let matchesTipo = (tipoVal === 'Todos' || rowComp === tipoVal);
            let matchesEstado = (estadoVal === 'Todas' || rowEst === estadoVal);

            // Buscar robustamente tanto en data-search como en el texto de las celdas directamente
            const cellComprobante = row.cells[1] ? row.cells[1].innerText.toLowerCase() : '';
            const cellCliente = row.cells[2] ? row.cells[2].innerText.toLowerCase() : '';

            let matchesSearch = (query === '' ||
                rowSearch.includes(query) ||
                cellComprobante.includes(query) ||
                cellCliente.includes(query));

            if (matchesFecha && matchesTipo && matchesEstado && matchesSearch) {
                row.style.display = '';
                visibleCount++;
            } else {
                row.style.display = 'none';
            }
        });

        // Actualizar totalizadores en la cabecera
        const countSpan = document.getElementById('totalVentasCount');
        if (countSpan) countSpan.innerText = visibleCount;

        const infoSpan = document.getElementById('historialPageInfo');
        if (infoSpan) {
            infoSpan.innerText = `Mostrando ${visibleCount} de ${rows.length} ventas`;
        }
    }

    // Registrar listeners para filtrado reactivo al instante
    const searchInputCustom = document.getElementById('filterSearchInput');
    if (searchInputCustom) {
        searchInputCustom.addEventListener('input', filterSales);
    }
    const tipoSelectCustom = document.getElementById('filterTipoComprobante');
    if (tipoSelectCustom) {
        tipoSelectCustom.addEventListener('change', filterSales);
    }
    const estadoSelectCustom = document.getElementById('filterEstado');
    if (estadoSelectCustom) {
        estadoSelectCustom.addEventListener('change', filterSales);
    }
    const desdeInputCustom = document.getElementById('filterFechaDesde');
    if (desdeInputCustom) {
        desdeInputCustom.addEventListener('change', filterSales);
    }
    const hastaInputCustom = document.getElementById('filterFechaHasta');
    if (hastaInputCustom) {
        hastaInputCustom.addEventListener('change', filterSales);
    }

    if (btnBuscarHistorial) {
        btnBuscarHistorial.addEventListener('click', (e) => {
            e.preventDefault();
            filterSales();
        });
    }

    if (btnLimpiarHistorial) {
        btnLimpiarHistorial.addEventListener('click', (e) => {
            e.preventDefault();

            // Establecer fechas por defecto: desde hace un mes hasta hoy
            const now = new Date();
            const yyyy = now.getFullYear();
            const mm = String(now.getMonth() + 1).padStart(2, '0');
            const dd = String(now.getDate()).padStart(2, '0');
            const finStr = `${yyyy}-${mm}-${dd}`;

            const prevMonthDate = new Date();
            prevMonthDate.setMonth(prevMonthDate.getMonth() - 1);
            const pyyyy = prevMonthDate.getFullYear();
            const pmm = String(prevMonthDate.getMonth() + 1).padStart(2, '0');
            const pdd = String(prevMonthDate.getDate()).padStart(2, '0');
            const inicioStr = `${pyyyy}-${pmm}-${pdd}`;

            if (document.getElementById('filterFechaDesde')) document.getElementById('filterFechaDesde').value = inicioStr;
            if (document.getElementById('filterFechaHasta')) document.getElementById('filterFechaHasta').value = finStr;
            if (document.getElementById('filterTipoComprobante')) document.getElementById('filterTipoComprobante').value = 'Todos';
            if (document.getElementById('filterEstado')) document.getElementById('filterEstado').value = 'Todas';
            if (document.getElementById('filterSearchInput')) document.getElementById('filterSearchInput').value = '';

            filterSales();
        });
    }

    // LÓGICA DE DETALLE Y ANULACIÓN (AJAX)
    let currentViewedSale = null;
    const detailPanel = document.getElementById('histDetailPanel');
    const closeDetailBtn = document.getElementById('btnCloseDetail');
    const btnAnularVenta = document.getElementById('btnAnularVenta');


    // Función reutilizable: abre el drawer de detalle para una venta por su ID.
    // Usada tanto por el click handler de la tabla como por la detección de URL (?ventaId=).
    function abrirDetalleVentaPorId(id) {
        if (!detailPanel) return;

        const comprobEl = document.getElementById('detailComprobante');
        if (comprobEl) comprobEl.innerText = 'Cargando...';
        const detailTBody = document.getElementById('detailTableBody');
        if (detailTBody) detailTBody.innerHTML = '<tr><td colspan="4" style="text-align: center; padding: 15px;">Cargando productos...</td></tr>';
        detailPanel.classList.add('active');

        fetch(`/api/ventas/${id}`)
            .then(res => {
                if (!res.ok) throw new Error('Error al cargar la venta');
                return res.json();
            })
            .then(venta => {
                currentViewedSale = venta;
                detailPanel.setAttribute('data-id', venta.idventa);

                // Nro de operación
                const nroOperContainer = document.getElementById('detailNroOperacionContainer');
                const nroOperSpan = document.getElementById('detailNroOperacion');
                if (nroOperContainer && nroOperSpan) {
                    if (venta.nroOperacion && venta.nroOperacion.trim() !== '') {
                        nroOperSpan.innerText = venta.nroOperacion;
                        nroOperContainer.style.display = 'block';
                    } else {
                        nroOperSpan.innerText = '---';
                        nroOperContainer.style.display = 'none';
                    }
                }

                // Cabecera del drawer
                document.getElementById('detailComprobante').innerText = `${venta.tipoComprobanteDescripcion} ${venta.nroPedido}`;

                const dt = new Date(venta.fecha);
                const fStr = `${String(dt.getDate()).padStart(2, '0')}/${String(dt.getMonth() + 1).padStart(2, '0')}/${dt.getFullYear()}`;
                const hStr = `${String(dt.getHours()).padStart(2, '0')}:${String(dt.getMinutes()).padStart(2, '0')}`;
                document.getElementById('detailFecha').innerText = `${fStr} ${hStr}`;

                document.getElementById('detailVendedor').innerText = venta.usuarioNombre || 'Sistema';
                document.getElementById('detailCliente').innerText = venta.clienteNombre || 'Cliente Ocasional';

                const row = document.querySelector(`#historialTableBody tr[data-id="${id}"]`);
                let dniText = venta.clienteDni || '00000000';
                if (row) {
                    const searchAttr = row.getAttribute('data-search') || '';
                    const parts = searchAttr.split(' ');
                    if (parts.length >= 3) dniText = parts[2];
                }
                document.getElementById('detailDniRuc').innerText = dniText;

                // Método de pago badge
                const payName = venta.metodoPagoDescripcion || 'Efectivo';
                const badgePago = document.getElementById('detailPagoBadge');
                const textPago = document.getElementById('detailPago');
                if (textPago) textPago.innerText = payName;
                if (badgePago) {
                    badgePago.className = 'hist-badge';
                    const payLower = payName.toLowerCase();
                    if (payLower.includes('efectivo')) badgePago.classList.add('hist-bg-efectivo');
                    else if (payLower.includes('yape')) badgePago.classList.add('hist-bg-yape');
                    else badgePago.classList.add('hist-bg-plin');
                }

                // Tabla de productos
                let tableHtml = '';
                let total = 0;
                if (venta.detalles && venta.detalles.length > 0) {
                    venta.detalles.forEach(d => {
                        const sub = parseFloat(d.importe || '0');
                        total += sub;
                        const code = 'PROD' + String(d.idproducto).padStart(3, '0');
                        tableHtml += `
                            <tr>
                                <td>
                                    <div class="hist-prod-cell">
                                        <img src="/img/placeholder-prod.png" alt="img">
                                        <div>
                                             <span>${d.nombreProducto}</span><br>
                                             <small>(${code})</small>
                                        </div>
                                    </div>
                                </td>
                                <td>${d.cantidad} kg/und</td>
                                <td>S/ ${parseFloat(d.precioU || '0').toFixed(2)}</td>
                                <td>S/ ${sub.toFixed(2)}</td>
                            </tr>
                        `;
                    });
                } else {
                    tableHtml = '<tr><td colspan="4" style="text-align: center; color: #888;">No hay productos detallados.</td></tr>';
                }
                document.getElementById('detailTableBody').innerHTML = tableHtml;

                // Totales
                const applyIgv = (venta.applyIgv !== false);
                const subtotal = total;
                const igv = applyIgv ? subtotal * 0.18 : 0.00;
                const totalFinal = subtotal + igv;

                const label = document.getElementById('detailIgvLabel');
                if (label) label.innerText = applyIgv ? 'IGV (18%)' : 'IGV (0%) EXONERADO';

                document.getElementById('detailSubtotal').innerText = `S/ ${subtotal.toFixed(2)}`;
                document.getElementById('detailIgv').innerText = `S/ ${igv.toFixed(2)}`;
                document.getElementById('detailTotal').innerText = `S/ ${totalFinal.toFixed(2)}`;

                // Zona de anulación
                const dangerZone = document.getElementById('detailDangerZone');
                if (dangerZone) {
                    if (venta.estado === true) {
                        dangerZone.style.display = 'block';
                        document.getElementById('anularMotivo').value = '';
                        document.getElementById('anularObs').value = '';
                    } else {
                        dangerZone.style.display = 'none';
                    }
                }
            })
            .catch(err => {
                console.error('Error cargando detalle de venta:', err);
                if (comprobEl) comprobEl.innerText = 'Error al cargar';
                if (detailTBody) detailTBody.innerHTML = '<tr><td colspan="4" style="text-align:center;color:#d32f2f;padding:15px;">No se pudo cargar el detalle.</td></tr>';
            });
    }

    if (histTableBody && detailPanel && closeDetailBtn) {
        // Cerrar panel de detalle
        closeDetailBtn.addEventListener('click', () => {
            detailPanel.classList.remove('active');
            currentViewedSale = null;
        });

        // Delegar clic en los botones de la tabla
        histTableBody.addEventListener('click', (e) => {
            const btnView = e.target.closest('.hist-btn-view');
            if (btnView) {
                e.preventDefault();
                const id = btnView.getAttribute('data-id');
                if (id) {
                    abrirDetalleVentaPorId(id);
                }
                return;
            }

            const btnPrint = e.target.closest('.hist-btn-print');
            if (btnPrint) {
                e.preventDefault();
                const id = btnPrint.getAttribute('data-id');
                if (!id) return;

                btnPrint.disabled = true;
                const originalHtml = btnPrint.innerHTML;
                btnPrint.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i>';

                fetch(`/api/ventas/${id}`)
                    .then(res => {
                        if (!res.ok) throw new Error('Error al cargar la venta');
                        return res.json();
                    })
                    .then(venta => {
                        imprimirTicket(venta);
                    })
                    .catch(err => {
                        alert('No se pudo imprimir el comprobante: ' + err.message);
                        console.error(err);
                    })
                    .finally(() => {
                        btnPrint.disabled = false;
                        btnPrint.innerHTML = originalHtml;
                    });
                return;
            }

            const btnOptions = e.target.closest('.hist-btn-options');
            if (btnOptions) {
                e.preventDefault();
                const id = btnOptions.getAttribute('data-id');
                const estado = btnOptions.getAttribute('data-estado');
                if (id) {
                    mostrarMenuOpciones(btnOptions, id, estado);
                }
                return;
            }
        });

        // --- GESTIÓN DE MENÚ DE OPCIONES DROPDOWN ---
        const dropdownMenu = document.getElementById('historialDropdownMenu');

        function mostrarMenuOpciones(btn, id, estado) {
            if (!dropdownMenu) return;

            dropdownMenu.setAttribute('data-id', id);
            dropdownMenu.setAttribute('data-estado', estado);

            // Deshabilitar/Habilitar opción de anulación si ya está anulada
            const optAnular = document.getElementById('optAnularVenta');
            if (optAnular) {
                if (estado === 'Anulada') {
                    optAnular.classList.add('disabled');
                    optAnular.title = 'Esta venta ya se encuentra anulada';
                } else {
                    optAnular.classList.remove('disabled');
                    optAnular.title = '';
                }
            }

            // Mostrar el menú para calcular sus dimensiones correctas
            dropdownMenu.style.display = 'flex';
            dropdownMenu.offsetHeight; // Forzar reflow
            dropdownMenu.classList.add('active');

            // Posicionar usando getBoundingClientRect (fixed relative to viewport)
            const rect = btn.getBoundingClientRect();
            let dropdownTop = rect.bottom;
            let dropdownLeft = rect.right - dropdownMenu.offsetWidth;

            // Evitar que se salga por abajo
            if (dropdownTop + dropdownMenu.offsetHeight > window.innerHeight) {
                dropdownTop = rect.top - dropdownMenu.offsetHeight;
            }
            // Evitar que se salga por la izquierda
            if (dropdownLeft < 0) {
                dropdownLeft = rect.left;
            }

            dropdownMenu.style.top = `${dropdownTop}px`;
            dropdownMenu.style.left = `${dropdownLeft}px`;
        }

        // Event listener global para cerrar el dropdown al hacer clic fuera
        document.addEventListener('click', (e) => {
            if (dropdownMenu && dropdownMenu.classList.contains('active')) {
                const isClickInside = dropdownMenu.contains(e.target) || e.target.closest('.hist-btn-options');
                if (!isClickInside) {
                    dropdownMenu.classList.remove('active');
                    setTimeout(() => {
                        dropdownMenu.style.display = 'none';
                    }, 150);
                }
            }
        });

        // Configurar los listeners para las opciones del dropdown
        if (dropdownMenu) {
            const optVerDetalle = document.getElementById('optVerDetalle');
            const optImprimir = document.getElementById('optImprimir');
            const optDescargarPdf = document.getElementById('optDescargarPdf');
            const optEnviarWhatsapp = document.getElementById('optEnviarWhatsapp');
            const optAnularVenta = document.getElementById('optAnularVenta');

            const cerrarDropdown = () => {
                dropdownMenu.classList.remove('active');
                setTimeout(() => {
                    dropdownMenu.style.display = 'none';
                }, 150);
            };

            const obtenerVentaParaDropdown = (id, callback) => {
                fetch(`/api/ventas/${id}`)
                    .then(res => {
                        if (!res.ok) throw new Error('Error al cargar la venta');
                        return res.json();
                    })
                    .then(venta => callback(venta))
                    .catch(err => {
                        alert('No se pudo obtener la información de la venta: ' + err.message);
                        console.error(err);
                    });
            };

            if (optVerDetalle) {
                optVerDetalle.addEventListener('click', (e) => {
                    e.preventDefault();
                    const id = dropdownMenu.getAttribute('data-id');
                    if (!id) return;
                    cerrarDropdown();
                    abrirDetalleVentaPorId(id);
                });
            }

            if (optImprimir) {
                optImprimir.addEventListener('click', (e) => {
                    e.preventDefault();
                    const id = dropdownMenu.getAttribute('data-id');
                    if (!id) return;
                    cerrarDropdown();
                    obtenerVentaParaDropdown(id, (venta) => {
                        imprimirTicket(venta);
                    });
                });
            }

            if (optDescargarPdf) {
                optDescargarPdf.addEventListener('click', (e) => {
                    e.preventDefault();
                    const id = dropdownMenu.getAttribute('data-id');
                    if (!id) return;
                    cerrarDropdown();
                    obtenerVentaParaDropdown(id, (venta) => {
                        generarPDFComprobante(venta, true);
                    });
                });
            }

            if (optEnviarWhatsapp) {
                optEnviarWhatsapp.addEventListener('click', (e) => {
                    e.preventDefault();
                    const id = dropdownMenu.getAttribute('data-id');
                    if (!id) return;
                    cerrarDropdown();
                    obtenerVentaParaDropdown(id, (venta) => {
                        iniciarEnvioWhatsapp(venta);
                    });
                });
            }

            if (optAnularVenta) {
                optAnularVenta.addEventListener('click', (e) => {
                    e.preventDefault();
                    const id = dropdownMenu.getAttribute('data-id');
                    const estado = dropdownMenu.getAttribute('data-estado');
                    if (!id || estado === 'Anulada') return;
                    cerrarDropdown();

                    // Abrir el detalle e ir a la sección de anulación
                    abrirDetalleVentaPorId(id);

                    setTimeout(() => {
                        const dangerZone = document.getElementById('detailDangerZone');
                        if (dangerZone) {
                            dangerZone.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
                            const anularMotivo = document.getElementById('anularMotivo');
                            if (anularMotivo) {
                                setTimeout(() => anularMotivo.focus(), 400);
                            }
                        }
                    }, 500);
                });
            }
        }
    }

    // Acción de Anular Venta
    if (btnAnularVenta) {
        btnAnularVenta.addEventListener('click', (e) => {
            e.preventDefault();
            const id = detailPanel ? detailPanel.getAttribute('data-id') : '';
            if (!id) return;

            const motivo = document.getElementById('anularMotivo').value;
            if (!motivo) {
                alert('Por favor, seleccione el motivo de la anulación.');
                return;
            }

            if (!confirm('¿Está seguro de que desea anular esta venta? Esta acción devolverá los productos al inventario y cambiará el estado de la venta. Esta acción no se puede deshacer.')) {
                return;
            }

            btnAnularVenta.disabled = true;
            btnAnularVenta.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Anulando...';

            fetch(`/api/ventas/${id}/anular`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
                .then(res => {
                    if (!res.ok) {
                        return res.text().then(text => { throw new Error(text || 'Error al anular la venta') });
                    }
                    return res.text();
                })
                .then(() => {
                    alert('La venta ha sido anulada con éxito y el stock de los productos se ha actualizado.');
                    window.location.reload();
                })
                .catch(err => {
                    alert('Error al anular la venta: ' + err.message);
                    console.error(err);
                    btnAnularVenta.disabled = false;
                    btnAnularVenta.innerHTML = '<i class="fa-solid fa-ban"></i> Anular Venta';
                });
        });
    }

    // Funciones y Listeners de Acciones de Detalle de Historial
    function imprimirTicket(venta) {
        if (!venta) return;

        const printWindow = window.open('', '_blank', 'width=600,height=800');
        if (!printWindow) {
            alert('Por favor permita las ventanas emergentes (popups) para poder imprimir el comprobante.');
            return;
        }

        const dt = new Date(venta.fecha);
        const fStr = `${String(dt.getDate()).padStart(2, '0')}/${String(dt.getMonth() + 1).padStart(2, '0')}/${dt.getFullYear()}`;
        const hStr = `${String(dt.getHours()).padStart(2, '0')}:${String(dt.getMinutes()).padStart(2, '0')}`;

        let tableRows = '';
        let total = 0;
        if (venta.detalles && venta.detalles.length > 0) {
            venta.detalles.forEach(d => {
                const sub = parseFloat(d.importe || '0');
                total += sub;
                const code = 'PROD' + String(d.idproducto).padStart(3, '0');
                tableRows += `
                    <tr>
                        <td style="padding: 6px 0; font-size: 13px;">${d.nombreProducto}<br><small style="color: #666;">(${code})</small></td>
                        <td style="padding: 6px 0; text-align: center; font-size: 13px;">${d.cantidad}</td>
                        <td style="padding: 6px 0; text-align: right; font-size: 13px;">S/ ${parseFloat(d.precioU || '0').toFixed(2)}</td>
                        <td style="padding: 6px 0; text-align: right; font-size: 13px;">S/ ${sub.toFixed(2)}</td>
                    </tr>
                `;
            });
        }

        const applyIgv = (venta.applyIgv !== false);
        const subtotal = total;                          // precios netos
        const igv = applyIgv ? subtotal * 0.18 : 0;
        const totalFinalTicket = subtotal + igv;
        const igvLabel = applyIgv ? 'IGV (18%)' : 'IGV (0%) EXONERADO';

        printWindow.document.write(`
            <html>
            <head>
                <title>Comprobante ${venta.nroPedido}</title>
                <style>
                    body {
                        font-family: 'Courier New', Courier, monospace;
                        color: #000;
                        padding: 20px;
                        margin: 0;
                        max-width: 400px;
                        background: #fff;
                    }
                    .text-center { text-align: center; }
                    .header { margin-bottom: 20px; line-height: 1.4; }
                    .header h2 { margin: 0 0 5px 0; font-size: 18px; }
                    .header p { margin: 0; font-size: 12px; }
                    .divider { border-top: 1px dashed #000; margin: 10px 0; }
                    .info-table, .items-table { width: 100%; border-collapse: collapse; }
                    .info-table td { font-size: 12px; padding: 2px 0; }
                    .items-table th { border-bottom: 1px dashed #000; padding: 5px 0; font-size: 12px; text-align: left; }
                    .totals-table { width: 100%; margin-top: 15px; }
                    .totals-table td { padding: 3px 0; font-size: 13px; }
                    .footer { text-align: center; margin-top: 30px; font-size: 11px; }
                    .qr-code {
                        border: 1px solid #000;
                        width: 100px;
                        height: 100px;
                        margin: 15px auto;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        font-size: 10px;
                        font-family: sans-serif;
                    }
                </style>
            </head>
            <body>
                <div class="header text-center">
                    <h2>CARNES Y AHUMADOS CHARAPITA S.A.C.</h2>
                    <p>RUC: 20785641239</p>
                    <p>Av. Los Próceres 123, Lima - Perú</p>
                </div>
                <div class="divider"></div>
                <div class="text-center" style="font-weight: bold; font-size: 15px; margin: 10px 0;">
                    ${venta.tipoComprobanteDescripcion.toUpperCase()}<br>
                    ${venta.nroPedido}
                </div>
                <div class="divider"></div>
                <table class="info-table">
                    <tr>
                        <td><strong>FECHA:</strong> ${fStr} ${hStr}</td>
                    </tr>
                    <tr>
                        <td><strong>CLIENTE:</strong> ${venta.clienteNombre || 'Cliente Ocasional'}</td>
                    </tr>
                    <tr>
                        <td><strong>VENDEDOR:</strong> ${venta.usuarioNombre || 'Sistema'}</td>
                    </tr>
                    <tr>
                        <td><strong>METODO PAGO:</strong> ${venta.metodoPagoDescripcion || 'Efectivo'}</td>
                    </tr>
                    \${venta.nroOperacion ? \`<tr><td><strong>Nº OPERACION:</strong> \${venta.nroOperacion}</td></tr>\` : ''}
                </table>
                <div class="divider"></div>
                <table class="items-table">
                    <thead>
                        <tr>
                            <th>PRODUCTO</th>
                            <th style="text-align: center;">CANT</th>
                            <th style="text-align: right;">P.U.</th>
                            <th style="text-align: right;">SUB</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${tableRows}
                    </tbody>
                </table>
                <div class="divider"></div>
                <table class="totals-table">
                    <tr>
                        <td>OP. GRAVADA</td>
                        <td style="text-align: right;">S/ ${subtotal.toFixed(2)}</td>
                    </tr>
                    <tr>
                        <td>${igvLabel}</td>
                        <td style="text-align: right;">S/ ${igv.toFixed(2)}</td>
                    </tr>
                    <tr style="font-weight: bold; font-size: 15px;">
                        <td>TOTAL A PAGAR</td>
                        <td style="text-align: right;">S/ ${totalFinalTicket.toFixed(2)}</td>
                    </tr>
                </table>
                <div class="divider"></div>
                <div class="qr-code">
                    [ QR COMPROBANTE ]
                </div>
                <div class="footer">
                    <p>¡Gracias por su compra!</p>
                    <p>Representación impresa de la Boleta de Venta Electrónica.</p>
                </div>
                <script>
                    window.onload = function() {
                        window.print();
                    }
                </script>
            </body>
            </html>
        `);
        printWindow.document.close();
    }
    // Detectar parámetro ?ventaId= en la URL (navegación desde la página de clientes)
    // Si viene con ?ventaId=X, abre automáticamente el drawer de detalle de esa venta
    if (detailPanel) {
        const urlParams = new URLSearchParams(window.location.search);
        const ventaIdParam = urlParams.get('ventaId');
        if (ventaIdParam) {
            // Limpiar la URL del parámetro sin recargar la página
            window.history.replaceState({}, document.title, window.location.pathname);
            // Pequeño delay para asegurar que el DOM y las variables están inicializadas
            setTimeout(() => abrirDetalleVentaPorId(ventaIdParam), 350);
        }
    }

    // Acción de Imprimir
    const btnDetailPrint = document.getElementById('btnDetailPrint');
    if (btnDetailPrint) {
        btnDetailPrint.addEventListener('click', (e) => {
            e.preventDefault();
            imprimirTicket(currentViewedSale);
        });
    }

    // Acción de Ver PDF (Descargar Boleta/Factura A4)
    const btnDetailPdf = document.getElementById('btnDetailPdf');
    if (btnDetailPdf) {
        btnDetailPdf.addEventListener('click', (e) => {
            e.preventDefault();
            if (!currentViewedSale) return;

            btnDetailPdf.disabled = true;
            const originalText = btnDetailPdf.innerHTML;
            btnDetailPdf.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Generando PDF...';

            generarPDFComprobante(currentViewedSale, true)
                .finally(() => {
                    btnDetailPdf.disabled = false;
                    btnDetailPdf.innerHTML = originalText;
                });
        });
    }

    // Acción de Enviar a WhatsApp
    const btnDetailWhatsapp = document.getElementById('btnDetailWhatsapp');

    if (btnDetailWhatsapp) {
        btnDetailWhatsapp.addEventListener('click', (e) => {
            e.preventDefault();
            if (currentViewedSale) {
                iniciarEnvioWhatsapp(currentViewedSale);
            }
        });
    }

    function iniciarEnvioWhatsapp(venta) {
        if (!venta) return;
        const customPhoneModal = document.getElementById('customPhoneModal');
        const customPhoneInput = document.getElementById('customPhoneInput');
        const btnCancelPhoneModal = document.getElementById('btnCancelPhoneModal');
        const btnRejectPhoneModal = document.getElementById('btnRejectPhoneModal');
        const btnSubmitPhoneModal = document.getElementById('btnSubmitPhoneModal');

        let telefono = venta.clienteTelefono;
        const clienteId = venta.clienteId;

        // Si no hay teléfono registrado o es un cliente ocasional sin número
        if (!telefono || telefono.trim() === '' || telefono === 'null') {
            if (!customPhoneModal || !customPhoneInput) {
                alert('No se pudo abrir el formulario de registro de teléfono.');
                return;
            }

            // Limpiar y mostrar modal
            customPhoneInput.value = '';
            customPhoneModal.style.display = 'flex';
            customPhoneModal.offsetHeight;
            customPhoneModal.classList.add('active');
            customPhoneInput.focus();

            // Definir manejadores de eventos
            const hideModal = () => {
                customPhoneModal.classList.remove('active');
                setTimeout(() => {
                    customPhoneModal.style.display = 'none';
                }, 250);
            };

            const handleCancel = (ev) => {
                ev.preventDefault();
                hideModal();
                removeListeners();
            };

            const handleSubmit = async (ev) => {
                ev.preventDefault();
                const nuevoTel = customPhoneInput.value.trim();

                if (!nuevoTel || !/^\d{9}$/.test(nuevoTel)) {
                    alert('Número de teléfono inválido. Debe ingresar exactamente 9 dígitos numéricos.');
                    customPhoneInput.focus();
                    return;
                }

                btnSubmitPhoneModal.disabled = true;
                btnSubmitPhoneModal.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Guardando...';

                try {
                    const resGet = await fetch(`/api/clientes/${clienteId}`);
                    if (!resGet.ok) throw new Error('Error al obtener datos del cliente');
                    const clienteData = await resGet.json();

                    clienteData.telefono = nuevoTel;

                    const resPut = await fetch('/api/clientes', {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(clienteData)
                    });
                    if (!resPut.ok) throw new Error('Error al guardar el teléfono del cliente');

                    alert('Teléfono del cliente registrado exitosamente.');
                    telefono = nuevoTel;
                    venta.clienteTelefono = telefono; // Guardar localmente
                    if (currentViewedSale && currentViewedSale.idventa === venta.idventa) {
                        currentViewedSale.clienteTelefono = telefono;
                    }

                    hideModal();
                    removeListeners();

                    enviarWhatsappConVenta(venta, telefono);

                } catch (err) {
                    alert('Error al guardar teléfono: ' + err.message);
                } finally {
                    btnSubmitPhoneModal.disabled = false;
                    btnSubmitPhoneModal.innerHTML = 'Guardar y Enviar';
                }
            };

            const removeListeners = () => {
                btnCancelPhoneModal.removeEventListener('click', handleCancel);
                btnRejectPhoneModal.removeEventListener('click', handleCancel);
                btnSubmitPhoneModal.removeEventListener('click', handleSubmit);
            };

            btnCancelPhoneModal.addEventListener('click', handleCancel);
            btnRejectPhoneModal.addEventListener('click', handleCancel);
            btnSubmitPhoneModal.addEventListener('click', handleSubmit);

            return;
        }

        enviarWhatsappConVenta(venta, telefono);
    }

    function enviarWhatsappConVenta(venta, telefono) {
        if (!venta) return;

        const btnDetailWhatsapp = document.getElementById('btnDetailWhatsapp');
        let originalText = '';
        if (btnDetailWhatsapp) {
            btnDetailWhatsapp.disabled = true;
            originalText = btnDetailWhatsapp.innerHTML;
            btnDetailWhatsapp.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Preparando WhatsApp...';
        }

        // 1. Descargar el comprobante PDF A4 automáticamente
        generarPDFComprobante(venta, true)
            .then((docName) => {
                // 2. Redirigir a WhatsApp Web adjuntando el mensaje predefinido e informando de la descarga
                const totalVentaStr = venta.detalles.reduce((acc, d) => acc + parseFloat(d.importe || '0'), 0).toFixed(2);
                const isFactura = venta.tipoComprobanteDescripcion.toLowerCase().includes('factura');
                const tipoDesc = isFactura ? 'Factura' : 'Boleta';

                const encodedMsg = encodeURIComponent(
                    `¡Hola ${venta.clienteNombre || 'Cliente'}! Te adjuntamos los detalles de tu compra en Carnes y Ahumados Charapita SAC.\n\n` +
                    `📄 Comprobante: ${venta.tipoComprobanteDescripcion} ${venta.nroPedido}\n` +
                    `💰 Importe Total: S/ ${totalVentaStr}\n` +
                    `💳 Método de Pago: ${venta.metodoPagoDescripcion}\n\n` +
                    `*(Le hemos descargado el archivo PDF de su ${tipoDesc} en su dispositivo [${docName}] para que pueda adjuntarlo en este chat)*\n\n` +
                    `¡Muchas gracias por tu preferencia! 🥩🔥`
                );

                window.open(`https://api.whatsapp.com/send?phone=51${telefono}&text=${encodedMsg}`, '_blank');
            })
            .catch(err => {
                console.error('Error al generar PDF para WhatsApp:', err);
                alert('No se pudo descargar el PDF del comprobante automáticamente. Se procederá a enviar solo el mensaje.');

                // Fallback: enviar solo mensaje de texto si falla html2pdf
                const totalVentaStr = venta.detalles.reduce((acc, d) => acc + parseFloat(d.importe || '0'), 0).toFixed(2);
                const encodedMsg = encodeURIComponent(
                    `¡Hola ${venta.clienteNombre || 'Cliente'}! Te adjuntamos los detalles de tu compra en Carnes y Ahumados Charapita SAC.\n\n` +
                    `📄 Comprobante: ${venta.tipoComprobanteDescripcion} ${venta.nroPedido}\n` +
                    `💰 Importe Total: S/ ${totalVentaStr}\n` +
                    `💳 Método de Pago: ${venta.metodoPagoDescripcion}\n\n` +
                    `¡Muchas gracias por tu preferencia! 🥩🔥`
                );
                window.open(`https://api.whatsapp.com/send?phone=51${telefono}&text=${encodedMsg}`, '_blank');
            })
            .finally(() => {
                if (btnDetailWhatsapp) {
                    btnDetailWhatsapp.disabled = false;
                    btnDetailWhatsapp.innerHTML = originalText;
                }
            });
    }

    // Convertidor de número a letras en Español para boletas y facturas
    function numeroALetras(num) {
        const dec = Math.round((num - Math.floor(num)) * 100);
        const centavos = String(dec).padStart(2, '0') + '/100 SOLES';
        const entero = Math.floor(num);
        if (entero === 0) return 'SON: CERO CON ' + centavos;

        const unidades = ['', 'UN', 'DOS', 'TRES', 'CUATRO', 'CINCO', 'SEIS', 'SIETE', 'OCHO', 'NUEVE'];
        const decenas = ['', 'DIEZ', 'VEINTE', 'TREINTA', 'CUARENTA', 'CINCUENTA', 'SESENTA', 'SETENTA', 'OCHENTA', 'NOVENTA'];
        const especiales = {
            11: 'ONCE', 12: 'DOCE', 13: 'TRECE', 14: 'CATORCE', 15: 'QUINCE',
            16: 'DIECISEIS', 17: 'DIECISIETE', 18: 'DIECIOCHO', 19: 'DIECINUEVE',
            21: 'VEINTIUNO', 22: 'VEINTIDOS', 23: 'VEINTITRES', 24: 'VEINTICUATRO',
            25: 'VEINTICINCO', 26: 'VEINTISEIS', 27: 'VEINTISIETE', 28: 'VEINTIOCHO', 29: 'VEINTINUEVE'
        };
        const cientos = ['', 'CIENTO', 'DOSCIENTOS', 'TRESCIENTOS', 'CUATROCIENTOS', 'QUINIENTOS', 'SEISCIENTOS', 'SETECIENTOS', 'OCHOCIENTOS', 'NOVECIENTOS'];

        function convertirGrupo(n) {
            let res = '';
            const c = Math.floor(n / 100);
            const d = Math.floor((n % 100) / 10);
            const u = n % 10;

            if (c > 0) {
                if (c === 1 && d === 0 && u === 0) {
                    res += 'CIEN ';
                } else {
                    res += cientos[c] + ' ';
                }
            }

            const resto = n % 100;
            if (resto > 0) {
                if (especiales[resto]) {
                    res += especiales[resto] + ' ';
                } else {
                    if (d > 0) {
                        res += decenas[d];
                        if (u > 0) res += ' Y ' + unidades[u];
                        res += ' ';
                    } else if (u > 0) {
                        res += unidades[u] + ' ';
                    }
                }
            }
            return res;
        }

        let texto = '';
        const millones = Math.floor(entero / 1000000);
        const miles = Math.floor((entero % 1000000) / 1000);
        const restoEntero = entero % 1000;

        if (millones > 0) {
            if (millones === 1) texto += 'UN MILLON ';
            else texto += convertirGrupo(millones) + 'MILLONES ';
        }

        if (miles > 0) {
            if (miles === 1) texto += 'MIL ';
            else texto += convertirGrupo(miles) + 'MIL ';
        }

        if (restoEntero > 0) {
            texto += convertirGrupo(restoEntero);
        }

        return 'SON: ' + texto.trim() + ' CON ' + centavos;
    }

    // Generador de PDF A4 Boleta / Factura Electrónica
    function generarPDFComprobante(venta, autoDownload = true) {
        return new Promise((resolve, reject) => {
            if (!venta) {
                reject(new Error('Venta vacía'));
                return;
            }

            // Crear un contenedor temporal oculto en el DOM
            const container = document.createElement('div');
            container.style.position = 'absolute';
            container.style.left = '-9999px';
            container.style.top = '-9999px';
            container.style.width = '800px';
            container.style.backgroundColor = '#fff';
            document.body.appendChild(container);

            const dt = new Date(venta.fecha);
            const fStr = `${String(dt.getDate()).padStart(2, '0')}/${String(dt.getMonth() + 1).padStart(2, '0')}/${dt.getFullYear()}`;
            const hStr = `${String(dt.getHours()).padStart(2, '0')}:${String(dt.getMinutes()).padStart(2, '0')}`;

            let tableRows = '';
            let total = 0;
            if (venta.detalles && venta.detalles.length > 0) {
                venta.detalles.forEach(d => {
                    const sub = parseFloat(d.importe || '0');
                    total += sub;
                    const code = 'PROD' + String(d.idproducto).padStart(3, '0');
                    tableRows += `
                        <tr style="border-bottom: 1px solid #eee;">
                            <td style="padding: 10px; font-size: 13px;">${code}</td>
                            <td style="padding: 10px; font-size: 13px; font-weight: 500;">${d.nombreProducto}</td>
                            <td style="padding: 10px; font-size: 13px; text-align: center;">${d.cantidad}</td>
                            <td style="padding: 10px; font-size: 13px; text-align: center;">kg</td>
                            <td style="padding: 10px; font-size: 13px; text-align: right;">S/ ${parseFloat(d.precioU || '0').toFixed(2)}</td>
                            <td style="padding: 10px; font-size: 13px; text-align: right; font-weight: 500;">S/ ${sub.toFixed(2)}</td>
                        </tr>
                    `;
                });
            }

            const applyIgv = (venta.applyIgv !== false);
            const subtotal = total;                             // precios netos
            const igv = applyIgv ? subtotal * 0.18 : 0;
            const totalFinalPdf = subtotal + igv;
            const igvLabel = applyIgv ? 'I.G.V. 18%' : 'I.G.V. 0% (EXONERADO)';
            const textTotalLetras = numeroALetras(totalFinalPdf);

            const isFactura = venta.tipoComprobanteDescripcion.toLowerCase().includes('factura');
            const tituloComprobante = isFactura ? 'FACTURA ELECTRÓNICA' : 'BOLETA DE VENTA ELECTRÓNICA';

            // Generar HTML del Comprobante A4
            container.innerHTML = `
                <div style="padding: 40px; font-family: 'Roboto', 'Helvetica Neue', Helvetica, Arial, sans-serif; color: #333; line-height: 1.5; background: #fff;">
                    <!-- Cabecera -->
                    <table style="width: 100%; border-collapse: collapse; margin-bottom: 30px;">
                        <tr>
                            <td style="width: 60%; vertical-align: top;">
                                <div style="display: flex; align-items: center; gap: 15px; margin-bottom: 15px;">
                                    <div style="background-color: #8a1529; color: #fff; width: 50px; height: 50px; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-weight: bold; font-size: 24px;">C</div>
                                    <div>
                                        <h1 style="margin: 0; font-size: 20px; color: #8a1529; font-weight: 700; letter-spacing: 0.5px;">CARNES Y AHUMADOS CHARAPITA S.A.C.</h1>
                                        <p style="margin: 3px 0 0 0; font-size: 12px; color: #666; font-weight: 500;">Venta de carnes y ahumados de alta calidad</p>
                                    </div>
                                </div>
                                <div style="font-size: 12px; color: #555; line-height: 1.6;">
                                    <strong>Dirección:</strong> Av. Los Próceres 123, Lima - Perú<br>
                                    <strong>Teléfono:</strong> (01) 456-7890 | <strong>Email:</strong> contacto@charapita.pe<br>
                                    <strong>Web:</strong> www.charapita.pe
                                </div>
                            </td>
                            <td style="width: 40%; vertical-align: top; text-align: right;">
                                <div style="border: 2px solid #8a1529; border-radius: 8px; padding: 20px; text-align: center; background-color: #fbf6f6;">
                                    <h2 style="margin: 0 0 8px 0; font-size: 16px; color: #333; font-weight: bold; letter-spacing: 1px;">R.U.C. 20785641239</h2>
                                    <div style="background-color: #8a1529; color: white; padding: 8px; font-size: 13px; font-weight: bold; letter-spacing: 0.5px; border-radius: 4px; margin-bottom: 10px;">
                                        ${tituloComprobante}
                                    </div>
                                    <span style="font-size: 18px; font-weight: bold; color: #333; font-family: monospace;">${venta.nroPedido}</span>
                                </div>
                            </td>
                        </tr>
                    </table>

                    <hr style="border: none; border-top: 1px solid #e1e1e1; margin-bottom: 25px;">

                    <!-- Datos del Cliente / Comprobante -->
                    <table style="width: 100%; border-collapse: collapse; margin-bottom: 30px; font-size: 13px;">
                        <tr>
                            <td style="width: 50%; vertical-align: top; padding-right: 20px;">
                                <table style="width: 100%;">
                                    <tr>
                                        <td style="padding: 4px 0; color: #777; width: 30%;"><strong>SEÑOR(ES):</strong></td>
                                        <td style="padding: 4px 0; color: #333; font-weight: 500;">${venta.clienteNombre || 'Cliente Ocasional'}</td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 4px 0; color: #777;"><strong>DNI / RUC:</strong></td>
                                        <td style="padding: 4px 0; color: #333; font-weight: 500;" id="pdfDniRuc">00000000</td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 4px 0; color: #777;"><strong>DIRECCIÓN:</strong></td>
                                        <td style="padding: 4px 0; color: #333;" id="pdfDireccion">Lima, Perú</td>
                                    </tr>
                                </table>
                            </td>
                            <td style="width: 50%; vertical-align: top; padding-left: 20px; border-left: 1px solid #f0f0f0;">
                                <table style="width: 100%;">
                                    <tr>
                                        <td style="padding: 4px 0; color: #777; width: 40%;"><strong>FECHA EMISIÓN:</strong></td>
                                        <td style="padding: 4px 0; color: #333; font-weight: 500;">${fStr} ${hStr}</td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 4px 0; color: #777;"><strong>MONEDA:</strong></td>
                                        <td style="padding: 4px 0; color: #333; font-weight: 500;">SOLES (S/)</td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 4px 0; color: #777;"><strong>MÉTODO PAGO:</strong></td>
                                        <td style="padding: 4px 0; color: #333; font-weight: 500;">${venta.metodoPagoDescripcion || 'Efectivo'}</td>
                                    </tr>
                                    ${venta.nroOperacion ? `
                                    <tr>
                                        <td style="padding: 4px 0; color: #777;"><strong>NRO. OPERACIÓN:</strong></td>
                                        <td style="padding: 4px 0; color: #333; font-weight: bold; color: #8a1529;">${venta.nroOperacion}</td>
                                    </tr>` : ''}
                                </table>
                            </td>
                        </tr>
                    </table>

                    <!-- Tabla de Detalle -->
                    <table style="width: 100%; border-collapse: collapse; margin-bottom: 30px;">
                        <thead>
                            <tr style="background-color: #8a1529; color: white;">
                                <th style="padding: 10px; font-size: 12px; text-align: left; font-weight: bold; border-top-left-radius: 4px; border-bottom-left-radius: 4px;">CÓDIGO</th>
                                <th style="padding: 10px; font-size: 12px; text-align: left; font-weight: bold; width: 45%;">DESCRIPCIÓN</th>
                                <th style="padding: 10px; font-size: 12px; text-align: center; font-weight: bold;">CANTIDAD</th>
                                <th style="padding: 10px; font-size: 12px; text-align: center; font-weight: bold;">U.M.</th>
                                <th style="padding: 10px; font-size: 12px; text-align: right; font-weight: bold;">P. UNITARIO</th>
                                <th style="padding: 10px; font-size: 12px; text-align: right; font-weight: bold; border-top-right-radius: 4px; border-bottom-right-radius: 4px;">IMPORTE</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${tableRows}
                        </tbody>
                    </table>

                    <!-- Totales y Letras -->
                    <table style="width: 100%; border-collapse: collapse; margin-bottom: 40px; font-size: 13px;">
                        <tr>
                            <td style="width: 60%; vertical-align: top; padding-right: 20px;">
                                <div style="border: 1px solid #e1e1e1; border-radius: 6px; padding: 12px 15px; margin-bottom: 20px; background-color: #fafafa; font-size: 12px; font-weight: 500; color: #555;">
                                    ${textTotalLetras}
                                </div>
                                <div style="display: flex; gap: 15px; align-items: center;">
                                    <div style="border: 1px solid #ccc; padding: 6px; background: #fff; width: 90px; height: 90px; display: flex; align-items: center; justify-content: center; border-radius: 4px;">
                                        <svg width="80" height="80" viewBox="0 0 100 100" style="display: block;">
                                            <rect x="0" y="0" width="25" height="25" fill="#333"/>
                                            <rect x="5" y="5" width="15" height="15" fill="#fff"/>
                                            <rect x="8" y="8" width="9" height="9" fill="#333"/>
                                            
                                            <rect x="75" y="0" width="25" height="25" fill="#333"/>
                                            <rect x="80" y="5" width="15" height="15" fill="#fff"/>
                                            <rect x="83" y="8" width="9" height="9" fill="#333"/>
                                            
                                            <rect x="0" y="75" width="25" height="25" fill="#333"/>
                                            <rect x="5" y="80" width="15" height="15" fill="#fff"/>
                                            <rect x="83" y="83" width="9" height="9" fill="#333"/>
                                            
                                            <rect x="35" y="10" width="10" height="10" fill="#333"/>
                                            <rect x="55" y="5" width="10" height="15" fill="#333"/>
                                            <rect x="30" y="35" width="15" height="10" fill="#333"/>
                                            <rect x="50" y="30" width="20" height="10" fill="#333"/>
                                            <rect x="10" y="45" width="10" height="20" fill="#333"/>
                                            <rect x="35" y="55" width="15" height="15" fill="#333"/>
                                            <rect x="60" y="50" width="15" height="10" fill="#333"/>
                                            <rect x="80" y="35" width="10" height="25" fill="#333"/>
                                            <rect x="50" y="75" width="15" height="15" fill="#333"/>
                                            <rect x="75" y="70" width="20" height="20" fill="#333"/>
                                        </svg>
                                    </div>
                                    <div style="font-size: 11px; color: #777; line-height: 1.5;">
                                        Representación impresa de la ${isFactura ? 'Factura' : 'Boleta de Venta'} Electrónica.<br>
                                        Autorizado mediante Resolución de SUNAT.<br>
                                        Consulte su validez en: <strong>www.sunat.gob.pe</strong>
                                    </div>
                                </div>
                            </td>
                            <td style="width: 40%; vertical-align: top;">
                                <table style="width: 100%; border-collapse: collapse; font-size: 13px;">
                                    <tr style="border-bottom: 1px solid #f0f0f0;">
                                        <td style="padding: 8px 0; color: #666;">OP. GRAVADA</td>
                                        <td style="padding: 8px 0; text-align: right; font-weight: 500; color: #333;">S/ ${subtotal.toFixed(2)}</td>
                                    </tr>
                                    <tr style="border-bottom: 1px solid #f0f0f0;">
                                        <td style="padding: 8px 0; color: #666;">${igvLabel}</td>
                                        <td style="padding: 8px 0; text-align: right; font-weight: 500; color: #333;">S/ ${igv.toFixed(2)}</td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 10px 0; font-size: 15px; font-weight: bold; color: #8a1529;">TOTAL NETO</td>
                                        <td style="padding: 10px 0; font-size: 15px; font-weight: bold; text-align: right; color: #8a1529;">S/ ${totalFinalPdf.toFixed(2)}</td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>

                    <div style="text-align: center; font-size: 11px; color: #999; margin-top: 50px; border-top: 1px solid #eee; padding-top: 20px;">
                        ¡Muchas gracias por su preferencia! Carnes y Ahumados Charapita S.A.C.
                    </div>
                </div>
            `;

            // Extraer DNI y dirección de la interfaz
            const dniText = document.getElementById('detailDniRuc').innerText;
            const pdfDniRuc = container.querySelector('#pdfDniRuc');
            if (pdfDniRuc && dniText) pdfDniRuc.innerText = dniText;

            const pdfDireccion = container.querySelector('#pdfDireccion');
            if (pdfDireccion && isFactura) {
                pdfDireccion.innerText = 'Av. República de Panamá 345, San Isidro - Lima';
            }

            const docName = `${isFactura ? 'Factura' : 'Boleta'}_${venta.nroPedido}.pdf`;

            const opt = {
                margin: 0,
                filename: docName,
                image: { type: 'jpeg', quality: 0.98 },
                html2canvas: { scale: 2, useCORS: true },
                jsPDF: { unit: 'in', format: 'letter', orientation: 'portrait' }
            };

            // Ejecutar html2pdf
            if (autoDownload) {
                html2pdf().set(opt).from(container).save()
                    .then(() => {
                        document.body.removeChild(container);
                        resolve(docName);
                    })
                    .catch(err => {
                        document.body.removeChild(container);
                        reject(err);
                    });
            } else {
                html2pdf().set(opt).from(container).outputPdf('blob')
                    .then(blob => {
                        document.body.removeChild(container);
                        resolve({ blob, filename: docName });
                    })
                    .catch(err => {
                        document.body.removeChild(container);
                        reject(err);
                    });
            }
        });
    }

    // === LÓGICA DE ROLES Y PERMISOS (CONFIGURACIÓN) ===
    const btnGuardarPermisos = document.getElementById('btnGuardarPermisos');
    if (btnGuardarPermisos) {
        // 1. Toggle visual
        document.querySelectorAll('.permiso-toggle').forEach(icon => {
            icon.addEventListener('click', function() {
                if (this.classList.contains('fa-circle-check')) {
                    this.classList.replace('fa-circle-check', 'fa-circle-xmark');
                    this.classList.replace('conf-icon-check', 'conf-icon-cross');
                } else {
                    this.classList.replace('fa-circle-xmark', 'fa-circle-check');
                    this.classList.replace('conf-icon-cross', 'conf-icon-check');
                }
            });
        });

        // 2. Guardar cambios
        btnGuardarPermisos.addEventListener('click', async () => {
            const rows = document.querySelectorAll('.rol-permiso-row');
            let successCount = 0;
            let errorCount = 0;

            btnGuardarPermisos.disabled = true;
            btnGuardarPermisos.innerText = 'Guardando...';

            for (const row of rows) {
                const idrol = row.getAttribute('data-idrol');
                const payload = { idrol: parseInt(idrol) };
                
                row.querySelectorAll('.permiso-toggle').forEach(icon => {
                    const fieldName = icon.getAttribute('data-field');
                    const isChecked = icon.classList.contains('fa-circle-check');
                    payload[fieldName] = isChecked;
                });

                try {
                    const res = await fetch('/api/roles', {
                        method: 'PUT',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(payload)
                    });
                    if (res.ok) successCount++;
                    else errorCount++;
                } catch (e) {
                    errorCount++;
                }
            }

            btnGuardarPermisos.disabled = false;
            btnGuardarPermisos.innerText = 'Guardar Permisos';

            if (errorCount === 0) {
                alert('Todos los permisos se guardaron correctamente. Los usuarios verán los cambios en su próximo inicio de sesión o al recargar la página.');
            } else {
                alert(`Se guardaron ${successCount} roles, pero hubo ${errorCount} errores.`);
            }
        });
    }

    // === GESTIÓN DE USUARIOS ===
    const btnNuevoUsuario = document.getElementById('btnNuevoUsuario');
    const panelNuevoUsuario = document.getElementById('panelNuevoUsuario');
    const closeNuevoUsuario = document.getElementById('closeNuevoUsuario');
    const cancelNuevoUsuario = document.getElementById('cancelNuevoUsuario');
    const btnSubmitUser = document.getElementById('btnSubmitUser');

    // Elementos del form
    const formUserId = document.getElementById('formUserId');
    const formUserNombres = document.getElementById('formUserNombres');
    const formUserDni = document.getElementById('formUserDni');
    const formUserCorreo = document.getElementById('formUserCorreo');
    const formUserRol = document.getElementById('formUserRol');
    const formUserPasswordGroup = document.getElementById('formUserPasswordGroup');
    const confPasswordInput = document.getElementById('confPasswordInput');
    const confTogglePassword = document.getElementById('confTogglePassword');
    const panelNuevoUsuarioTitle = document.getElementById('panelNuevoUsuarioTitle');

    if (btnNuevoUsuario && panelNuevoUsuario) {
        // Abrir / Cerrar Panel
        const openPanel = () => {
            panelNuevoUsuario.classList.add('active');
            document.body.style.overflow = 'hidden';
        };
        const closePanel = () => {
            panelNuevoUsuario.classList.remove('active');
            document.body.style.overflow = '';
        };

        btnNuevoUsuario.addEventListener('click', () => {
            formUserId.value = '';
            formUserNombres.value = '';
            formUserDni.value = '';
            formUserCorreo.value = '';
            formUserRol.value = '';
            confPasswordInput.value = '';
            formUserPasswordGroup.style.display = 'block';
            panelNuevoUsuarioTitle.innerText = 'Nuevo Usuario';
            openPanel();
        });

        closeNuevoUsuario.addEventListener('click', closePanel);
        cancelNuevoUsuario.addEventListener('click', closePanel);

        // Toggle Password Visibility
        if (confTogglePassword && confPasswordInput) {
            confTogglePassword.addEventListener('click', function () {
                const type = confPasswordInput.getAttribute('type') === 'password' ? 'text' : 'password';
                confPasswordInput.setAttribute('type', type);
                this.classList.toggle('fa-eye-slash');
                this.classList.toggle('fa-eye');
            });
        }

        // Editar Usuario
        document.querySelectorAll('.btn-editar-usuario').forEach(btn => {
            btn.addEventListener('click', function () {
                const tr = this.closest('tr');
                formUserId.value = tr.getAttribute('data-id');
                const nombre = tr.getAttribute('data-nombre') || '';
                const apellido = tr.getAttribute('data-apellido') || '';
                formUserNombres.value = (nombre + ' ' + apellido).trim();
                formUserDni.value = tr.getAttribute('data-dni');
                formUserCorreo.value = tr.getAttribute('data-correo');
                formUserRol.value = tr.getAttribute('data-idrol');

                confPasswordInput.value = '';
                formUserPasswordGroup.style.display = 'none'; // No se edita la clave por aquí
                panelNuevoUsuarioTitle.innerText = 'Editar Usuario';
                openPanel();
            });
        });

        // Guardar Usuario (POST o PUT)
        btnSubmitUser.addEventListener('click', async () => {
            if (!formUserNombres.value || !formUserDni.value || !formUserCorreo.value || !formUserRol.value) {
                Swal.fire('Error', 'Por favor complete todos los campos obligatorios.', 'warning');
                return;
            }

            const isEdit = formUserId.value !== '';
            if (!isEdit && !confPasswordInput.value) {
                Swal.fire('Error', 'La contraseña inicial es obligatoria para nuevos usuarios.', 'warning');
                return;
            }

            // Split nombre y apellido
            const parts = formUserNombres.value.trim().split(' ');
            const nombre = parts[0];
            const apellido = parts.slice(1).join(' ');

            const payload = {
                nombre: nombre,
                apellido: apellido,
                dni: formUserDni.value,
                correo: formUserCorreo.value,
                rol: { idrol: parseInt(formUserRol.value) }
            };

            if (isEdit) {
                payload.idusuario = parseInt(formUserId.value);
            } else {
                payload.contrasena = confPasswordInput.value;
            }

            try {
                btnSubmitUser.disabled = true;
                btnSubmitUser.innerText = 'Guardando...';

                const res = await fetch('/api/usuarios', {
                    method: isEdit ? 'PUT' : 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                if (res.ok) {
                    await Swal.fire('Éxito', 'Usuario guardado correctamente.', 'success');
                    window.location.reload();
                } else {
                    const text = await res.text();
                    Swal.fire('Error', text, 'error');
                    btnSubmitUser.disabled = false;
                    btnSubmitUser.innerText = 'Guardar Usuario';
                }
            } catch (error) {
                Swal.fire('Error', 'Hubo un error de conexión.', 'error');
                btnSubmitUser.disabled = false;
                btnSubmitUser.innerText = 'Guardar Usuario';
            }
        });

        // Cambiar Estado (Toggle)
        document.querySelectorAll('.toggle-estado-usuario').forEach(toggle => {
            toggle.addEventListener('change', async function () {
                const tr = this.closest('tr');
                const id = tr.getAttribute('data-id');
                const nuevoEstado = this.checked ? 1 : 0;
                
                try {
                    const res = await fetch('/api/usuarios', {
                        method: 'PUT',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ idusuario: parseInt(id), estado: nuevoEstado })
                    });
                    
                    if (!res.ok) {
                        const text = await res.text();
                        Swal.fire('Error', text, 'error');
                        this.checked = !this.checked; // Revert
                    } else {
                        // Visual update
                        if (nuevoEstado === 0) {
                            tr.classList.add('conf-row-inactive');
                            tr.querySelector('.conf-avatar').classList.add('conf-avatar-gray');
                        } else {
                            tr.classList.remove('conf-row-inactive');
                            tr.querySelector('.conf-avatar').classList.remove('conf-avatar-gray');
                        }
                    }
                } catch (e) {
                    Swal.fire('Error', 'Hubo un error de conexión.', 'error');
                    this.checked = !this.checked; // Revert
                }
            });
        });

        // Cambiar Contraseña
        document.querySelectorAll('.btn-clave-usuario').forEach(btn => {
            btn.addEventListener('click', async function () {
                const tr = this.closest('tr');
                const id = tr.getAttribute('data-id');
                const nombreCompleto = tr.getAttribute('data-nombre') + ' ' + tr.getAttribute('data-apellido');

                const { value: nuevaClave } = await Swal.fire({
                    title: 'Cambiar Contraseña',
                    text: `Ingrese la nueva contraseña para: ${nombreCompleto}`,
                    input: 'password',
                    inputPlaceholder: 'Mínimo 6 caracteres',
                    showCancelButton: true,
                    confirmButtonText: 'Cambiar',
                    cancelButtonText: 'Cancelar',
                    inputValidator: (value) => {
                        if (!value || value.length < 6) {
                            return 'La contraseña debe tener al menos 6 caracteres'
                        }
                    }
                });

                if (nuevaClave) {
                    try {
                        const res = await fetch('/api/usuarios', {
                            method: 'PUT',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify({ idusuario: parseInt(id), contrasena: nuevaClave })
                        });
                        
                        if (res.ok) {
                            Swal.fire('Éxito', 'Contraseña actualizada correctamente.', 'success');
                        } else {
                            const text = await res.text();
                            Swal.fire('Error', text, 'error');
                        }
                    } catch (e) {
                        Swal.fire('Error', 'Hubo un error de conexión.', 'error');
                    }
                }
            });
        });

        // Búsqueda de Usuarios
        const searchInputUsuarios = document.getElementById('searchInputUsuarios');
        if (searchInputUsuarios) {
            searchInputUsuarios.addEventListener('keyup', function() {
                const filter = this.value.toLowerCase();
                // Select only the rows in the user table, not the roles table
                const rows = document.querySelectorAll('#tablaUsuarios tbody tr');
                rows.forEach(row => {
                    const text = row.innerText.toLowerCase();
                    if (text.includes(filter)) {
                        row.style.display = '';
                    } else {
                        row.style.display = 'none';
                    }
                });
            });
        }
    }
});