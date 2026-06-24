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
        
        // Cargar datos dinámicos desde atributos data de Thymeleaf
        const barLabelsStr = barCanvas.getAttribute('data-labels') || '';
        const barValoresStr = barCanvas.getAttribute('data-valores') || '';
        
        const barLabels = barLabelsStr ? barLabelsStr.split(',') : ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'];
        const barData = barValoresStr ? barValoresStr.split(',').map(Number) : [0, 0, 0, 0, 0, 0, 0];

        new Chart(ctxBar, {
            type: 'bar',
            data: {
                labels: barLabels,
                datasets: [{
                    label: 'Ventas (S/)',
                    data: barData,
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
        
        // Cargar datos dinámicos desde atributos data de Thymeleaf
        const pieLabelsStr = pieCanvas.getAttribute('data-labels') || '';
        const pieValoresStr = pieCanvas.getAttribute('data-valores') || '';
        
        const pieLabels = pieLabelsStr ? pieLabelsStr.split(',') : ['Sin categorías'];
        const pieData = pieValoresStr ? pieValoresStr.split(',').map(Number) : [100];
        
        // Generar colores agradables según la cantidad de categorías
        const predefinedColors = ['#8a1529', '#f39c12', '#27ae60', '#2980b9', '#8e44ad', '#1abc9c', '#d35400', '#2c3e50'];
        const backgroundColors = pieLabels.map((_, i) => predefinedColors[i % predefinedColors.length]);

        new Chart(ctxPie, {
            type: 'doughnut',
            data: {
                labels: pieLabels,
                datasets: [{
                    data: pieData,
                    backgroundColor: backgroundColors,
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
                        
                        // Mock fields matching the table representation
                        if (formTelefono) {
                            formTelefono.value = '9' + (data.nroDocumento && data.nroDocumento.length >= 8 ? data.nroDocumento.substring(data.nroDocumento.length - 8) : '87654321');
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
                                            <td><a href="/historial" class="cli-link">Ver detalle</a></td>
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
                                prefList.innerHTML = data.productosPreferidos.map((p, idx) => `
                                    <div class="cli-pref-item">
                                        <span class="cli-pref-num">${idx + 1}</span>
                                        <div class="cli-heart-icon" style="width:30px; height:30px; font-size:0.9rem; background:#fff; border:1px solid #f0f0f0;"><i class="fa-solid fa-basket-shopping" style="color:#a85867;"></i></div>
                                        <div class="cli-pref-info">
                                            <strong>${p.nombre}</strong>
                                            <small>Promedio: ${p.promedioCantidad} ${p.unidad} por compra</small>
                                        </div>
                                    </div>
                                `).join('');
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
    // LÓGICA VISTA: REPORTES (rep-)
    // =========================================

    let repChartInstance = null;

    // 1. Interacción de Pestañas (Tabs)
    const repTabs = document.querySelectorAll('.rep-tab');
    if (repTabs.length > 0) {
        repTabs.forEach((tab, index) => {
            tab.addEventListener('click', (e) => {
                // Quita la clase active de todas
                repTabs.forEach(t => t.classList.remove('active'));
                // Añade active a la clickeada
                e.currentTarget.classList.add('active');
                
                // Mostrar/ocultar secciones basadas en la pestaña
                const secVentas = document.getElementById('repVentasSection');
                const secMermas = document.getElementById('repMermasSection');
                const secInventario = document.getElementById('repInventarioSection');
                
                if (secVentas && secMermas && secInventario) {
                    if (index === 0) {
                        secVentas.style.display = 'block';
                        secMermas.style.display = 'none';
                        secInventario.style.display = 'none';
                    } else if (index === 1) {
                        secVentas.style.display = 'none';
                        secMermas.style.display = 'block';
                        secInventario.style.display = 'none';
                    } else if (index === 2) {
                        secVentas.style.display = 'none';
                        secMermas.style.display = 'none';
                        secInventario.style.display = 'block';
                    }
                }
            });
        });
    }

    // 2. Gráfico "Ventas vs Mermas"
    const repCanvas = document.getElementById('repBarChart');
    if (repCanvas) {
        const ctxRep = repCanvas.getContext('2d');
        const initialVentas = parseFloat(repCanvas.getAttribute('data-ventas')) || 0;
        const initialMermas = parseFloat(repCanvas.getAttribute('data-mermas')) || 0;
        repChartInstance = new Chart(ctxRep, {
            type: 'bar',
            data: {
                labels: ['Ventas', 'Mermas'],
                datasets: [{
                    label: 'Monto (S/)',
                    data: [initialVentas, initialMermas],
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
                                if (value >= 1000) {
                                    return (value / 1000) + 'K'; // Formato "10K", "8K"
                                }
                                return 'S/ ' + value;
                            }
                        }
                    }
                }
            }
        });
    }

    // 3. Lógica de Filtrado Local (Ventas e Inventario)
    function applyReportFilters() {
        const desdeInput = document.getElementById('repFechaDesde');
        const hastaInput = document.getElementById('repFechaHasta');
        const cajaSelect = document.getElementById('repCajaFilter');
        const vendedorSelect = document.getElementById('repVendedorFilter');
        const categoriaSelect = document.getElementById('repCategoriaFilter');

        if (!desdeInput || !hastaInput || !cajaSelect || !vendedorSelect || !categoriaSelect) return;

        const desdeVal = desdeInput.value; // YYYY-MM-DD
        const hastaVal = hastaInput.value; // YYYY-MM-DD
        const cajaVal = cajaSelect.value;
        const vendedorVal = vendedorSelect.value;
        const categoriaVal = categoriaSelect.value;

        // 3.1. Filtrar Ventas
        const salesRows = document.querySelectorAll('#repVentasTableBody tr');
        let visibleSalesCount = 0;
        let visibleSalesTotal = 0;

        salesRows.forEach(row => {
            const rowFecha = row.getAttribute('data-fecha'); // YYYY-MM-DD
            const rowCaja = row.getAttribute('data-caja');
            const rowVendedor = row.getAttribute('data-vendedor');
            const rowCategorias = row.getAttribute('data-categorias') || '';

            let matchesFecha = true;
            if (rowFecha) {
                if (desdeVal && rowFecha < desdeVal) matchesFecha = false;
                if (hastaVal && rowFecha > hastaVal) matchesFecha = false;
            }

            const matchesCaja = (cajaVal === 'Todas' || rowCaja === cajaVal);
            const matchesVendedor = (vendedorVal === 'Todos' || rowVendedor === vendedorVal);
            
            let matchesCategoria = true;
            if (categoriaVal !== 'Todas') {
                const rowCatsList = rowCategorias.split(',').map(s => s.trim());
                matchesCategoria = rowCatsList.includes(categoriaVal);
            }

            if (matchesFecha && matchesCaja && matchesVendedor && matchesCategoria) {
                row.style.display = '';
                visibleSalesCount++;
                
                // Extraer total de la sexta columna (índice 5)
                const totalTd = row.children[5];
                if (totalTd) {
                    const totalText = totalTd.textContent.replace('S/', '').replace(/,/g, '').trim();
                    const totalVal = parseFloat(totalText) || 0;
                    
                    // Solo sumar si la venta está completada (no anulada)
                    const isCompletada = !row.children[1].classList.contains('rep-text-muted');
                    if (isCompletada) {
                        visibleSalesTotal += totalVal;
                    }
                }
            } else {
                row.style.display = 'none';
            }
        });

        const infoLabel = document.getElementById('repVentasInfoLabel');
        if (infoLabel) {
            infoLabel.innerText = `Mostrando ${visibleSalesCount} de ${salesRows.length} ventas`;
        }

        // 3.2. Filtrar Inventario
        const invRows = document.querySelectorAll('#repInventoryTableBody tr');
        let totalInvProducts = 0;
        let totalInvStock = 0;
        let totalInvVal = 0;

        invRows.forEach(row => {
            const rowCategoria = row.getAttribute('data-categoria');
            const matchesCategoria = (categoriaVal === 'Todas' || rowCategoria === categoriaVal);

            if (matchesCategoria) {
                row.style.display = '';
                
                const prodVal = parseInt(row.children[1].textContent.trim()) || 0;
                const stockVal = parseFloat(row.children[2].textContent.replace(/,/g, '').trim()) || 0;
                const valVal = parseFloat(row.children[3].textContent.replace(/,/g, '').trim()) || 0;

                totalInvProducts += prodVal;
                totalInvStock += stockVal;
                totalInvVal += valVal;
            } else {
                row.style.display = 'none';
            }
        });

        const footerProducts = document.getElementById('repInvTotalProducts');
        const footerStock = document.getElementById('repInvTotalStock');
        const footerVal = document.getElementById('repInvTotalVal');

        if (footerProducts) footerProducts.textContent = totalInvProducts;
        if (footerStock) footerStock.textContent = totalInvStock.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
        if (footerVal) footerVal.textContent = totalInvVal.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

        // 3.3. Actualizar Gráfico
        let mermasTotal = 0;
        const mermasTotalCell = document.querySelector('#repMermasSection tfoot tr.rep-total-row td:last-child');
        if (mermasTotalCell) {
            mermasTotal = parseFloat(mermasTotalCell.textContent.replace(/,/g, '').trim()) || 0;
        } else if (repCanvas) {
            mermasTotal = parseFloat(repCanvas.getAttribute('data-mermas')) || 0;
        }

        if (repChartInstance) {
            repChartInstance.data.datasets[0].data = [visibleSalesTotal, mermasTotal];
            repChartInstance.update();
        }

        // 3.4. Actualizar Alerta de Porcentaje de Mermas
        const alertSpan = document.querySelector('#repMermasSection .rep-alert-red span');
        if (alertSpan) {
            let mermaPct = 0;
            if (visibleSalesTotal > 0) {
                mermaPct = (mermasTotal / visibleSalesTotal) * 100;
                mermaPct = Math.round(mermaPct * 100) / 100;
            }
            alertSpan.innerHTML = `<i class="fa-solid fa-circle-exclamation"></i> Las mermas representan el <strong>${mermaPct.toFixed(2)}%</strong> de las ventas totales.`;
        }
    }

    function clearReportFilters() {
        const desdeInput = document.getElementById('repFechaDesde');
        const hastaInput = document.getElementById('repFechaHasta');
        const cajaSelect = document.getElementById('repCajaFilter');
        const vendedorSelect = document.getElementById('repVendedorFilter');
        const categoriaSelect = document.getElementById('repCategoriaFilter');

        if (desdeInput && hastaInput) {
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

            desdeInput.value = inicioStr;
            hastaInput.value = finStr;
        }

        if (cajaSelect) cajaSelect.value = 'Todas';
        if (vendedorSelect) vendedorSelect.value = 'Todos';
        if (categoriaSelect) categoriaSelect.value = 'Todas';

        applyReportFilters();
    }

    const repBtnBuscar = document.getElementById('repBtnBuscar');
    if (repBtnBuscar) {
        repBtnBuscar.addEventListener('click', (e) => {
            e.preventDefault();
            applyReportFilters();
        });
    }

    const repBtnLimpiar = document.getElementById('repBtnLimpiar');
    if (repBtnLimpiar) {
        repBtnLimpiar.addEventListener('click', (e) => {
            e.preventDefault();
            clearReportFilters();
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
        btnConfirmarApertura.addEventListener('click', (e) => {
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

            const payload = {
                idcaja: parseInt(idcaja),
                idusuario: parseInt(idusuario),
                montoinicial: montoinicial,
                observaciones: observaciones
            };

            fetch('/api/movimientoscaja/abrir', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            })
            .then(res => {
                if (!res.ok) {
                    return res.text().then(text => { throw new Error(text || 'Error al abrir caja') });
                }
                return res.json();
            })
            .then(() => {
                window.location.reload();
            })
            .catch(err => {
                alert('Error al abrir la caja: ' + err.message);
                console.error(err);
            });
        });
    }

    // 3. Realizar Cierre de Caja
    if (btnConfirmarCierre) {
        btnConfirmarCierre.addEventListener('click', (e) => {
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

            const payload = {
                montofinal: montofinal
            };

            fetch(`/api/movimientoscaja/cerrar/${idmovimiento}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            })
            .then(res => {
                if (!res.ok) {
                    return res.text().then(text => { throw new Error(text || 'Error al cerrar caja') });
                }
                return res.json();
            })
            .then(() => {
                window.location.reload();
            })
            .catch(err => {
                alert('Error al cerrar la caja: ' + err.message);
                console.error(err);
            });
        });
    }

    // =========================================================
    // LÓGICA COMPLETA DE NUEVA VENTA Y CONFIRMAR VENTA
    // =========================================================
    
    // --- ESTADO GLOBAL POS ---
    let carrito = JSON.parse(localStorage.getItem('pos_cart')) || [];
    let selectedPayMethod = JSON.parse(localStorage.getItem('pos_pay_method')) || null;

    // Inicializar método de pago por defecto si no existe o no tiene estructura
    if (!selectedPayMethod || typeof selectedPayMethod !== 'object' || !selectedPayMethod.id) {
        // Buscamos el elemento .pos-pay-method que esté activo en el DOM al iniciar
        const activePayElem = document.querySelector('.pos-pay-method.active');
        if (activePayElem) {
            selectedPayMethod = {
                id: parseInt(activePayElem.getAttribute('data-id')),
                nombre: activePayElem.getAttribute('data-nombre')
            };
        } else {
            selectedPayMethod = { id: 1, nombre: 'Efectivo' };
        }
        localStorage.setItem('pos_pay_method', JSON.stringify(selectedPayMethod));
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

        // Cálculos estándar en Perú (Total incluye IGV)
        const subtotal = total / 1.18;
        const igv = total - subtotal;

        if (cartSubtotal) cartSubtotal.innerText = `S/ ${subtotal.toFixed(2)}`;
        if (cartIgv) cartIgv.innerText = `S/ ${igv.toFixed(2)}`;
        if (cartTotal) cartTotal.innerText = `S/ ${total.toFixed(2)}`;
    }

    // inicializar carrito si estamos en POS
    if (cartTableBody) {
        renderCart();

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

    // CLIC EN TARJETAS DE PRODUCTO
    document.querySelectorAll('.btn-pos-add-to-cart').forEach(card => {
        card.addEventListener('click', () => {
            const id = parseInt(card.getAttribute('data-id'));
            const nombre = card.getAttribute('data-nombre');
            const codigo = card.getAttribute('data-codigo');
            const precio = parseFloat(card.getAttribute('data-precio'));
            const stockMax = parseInt(card.getAttribute('data-stock'));
            const unidad = card.getAttribute('data-unidad');

            if (stockMax <= 0) {
                alert('Este producto está agotado (sin stock).');
                return;
            }

            const existing = carrito.find(i => i.id === id);
            if (existing) {
                if (existing.cantidad < stockMax) {
                    existing.cantidad++;
                } else {
                    alert(`No se puede agregar más. Stock máximo: ${stockMax}`);
                    return;
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

    // SELECCIÓN DE MÉTODO DE PAGO EN EL GRID DE POS
    const posPaymentGrid = document.getElementById('posPaymentGrid');
    if (posPaymentGrid) {
        // Resaltar método activo guardado
        posPaymentGrid.querySelectorAll('.pos-pay-method').forEach(item => {
            const payId = parseInt(item.getAttribute('data-id'));
            if (payId === selectedPayMethod.id) {
                item.classList.add('active');
            } else {
                item.classList.remove('active');
            }
        });

        posPaymentGrid.addEventListener('click', (e) => {
            const item = e.target.closest('.pos-pay-method');
            if (item) {
                posPaymentGrid.querySelectorAll('.pos-pay-method').forEach(i => i.classList.remove('active'));
                item.classList.add('active');
                selectedPayMethod = {
                    id: parseInt(item.getAttribute('data-id')),
                    nombre: item.getAttribute('data-nombre')
                };
                savePayMethodToStorage();
            }
        });
    }

    // VALIDACIÓN AL CLICKEAR "PAGAR / CONFIRMAR VENTA"
    if (btnConfirmarVenta) {
        btnConfirmarVenta.addEventListener('click', (e) => {
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
        const subtotal = total / 1.18;
        const igv = total - subtotal;

        document.getElementById('confirmSubtotal').innerText = `S/ ${subtotal.toFixed(2)}`;
        document.getElementById('confirmIgv').innerText = `S/ ${igv.toFixed(2)}`;
        document.getElementById('confirmTotal').innerText = `S/ ${total.toFixed(2)}`;

        // Inicializar Método de Pago de acuerdo al POS
        const initialPayMethod = JSON.parse(localStorage.getItem('pos_pay_method')) || { id: 1, nombre: 'Efectivo' };
        if (confirmMetodoSelect) {
            confirmMetodoSelect.value = initialPayMethod.id;
        }

        const badgeMethod = document.getElementById('confirmPaymentMethodBadge');
        const badgeAmount = document.getElementById('confirmPaymentAmountBadge');
        if (badgeMethod) badgeMethod.innerText = `Metodo: ${initialPayMethod.nombre}`;
        if (badgeAmount) badgeAmount.innerText = `S/ ${total.toFixed(2)}`;

        // Sincronizar el badge de pago recibido si cambia el select
        if (confirmMetodoSelect) {
            confirmMetodoSelect.addEventListener('change', () => {
                const selectedOpt = confirmMetodoSelect.options[confirmMetodoSelect.selectedIndex];
                if (badgeMethod && selectedOpt) {
                    badgeMethod.innerText = `Metodo: ${selectedOpt.text}`;
                }
            });
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

        // Toggles Cliente Ocasional / Cliente Registrado
        if (clientToggleBtnsCustom.length > 0) {
            clientToggleBtnsCustom.forEach(btn => {
                btn.addEventListener('click', () => {
                    const action = btn.innerText.toLowerCase();
                    if (action.includes('ocasional')) {
                        if (groupClienteSelect) groupClienteSelect.style.display = 'none';
                        if (confirmNroDocumento) confirmNroDocumento.value = '00000000';
                        if (confirmDireccion) confirmDireccion.value = 'Sin dirección';
                        if (confirmCorreo) confirmCorreo.value = 'ocasional@charapita.com';
                        if (confirmTelefono) confirmTelefono.value = '999999999';
                        if (confirmClienteSelect) confirmClienteSelect.value = '';
                    } else {
                        if (groupClienteSelect) groupClienteSelect.style.display = 'block';
                        if (confirmNroDocumento) confirmNroDocumento.value = '';
                        if (confirmDireccion) confirmDireccion.value = '';
                        if (confirmCorreo) confirmCorreo.value = '';
                        if (confirmTelefono) confirmTelefono.value = '';
                        if (confirmClienteSelect) confirmClienteSelect.value = '';
                    }
                });
            });
        }

        // Sincronizar el select de series con la caja de tipo de comprobante
        document.querySelectorAll('.cv-doc-box[data-target="comprobante"]').forEach(box => {
            box.addEventListener('click', () => {
                const serie = box.getAttribute('data-serie') || (box.getAttribute('data-id') === '1' ? 'B001' : 'F001');
                if (confirmComprobanteSerie) {
                    confirmComprobanteSerie.value = serie;
                }
            });
        });

        // ENVÍO/CONFIRMACIÓN FINAL DE VENTA
        if (btnConfirmarFinal) {
            btnConfirmarFinal.addEventListener('click', async (e) => {
                e.preventDefault();

                const confirmCart = JSON.parse(localStorage.getItem('pos_cart')) || [];
                if (confirmCart.length === 0) {
                    alert('El carrito de compras está vacío.');
                    return;
                }

                // Determinamos cliente
                let idcliente = null;
                const activeToggleBtn = document.querySelector('.cv-toggle-btn[data-target="cliente"].active');
                const isOcasional = activeToggleBtn && activeToggleBtn.innerText.toLowerCase().includes('ocasional');

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

                // Obtener tipo comprobante
                const activeDocBox = document.querySelector('.cv-doc-box[data-target="comprobante"].active');
                if (!activeDocBox) {
                    alert('Debe seleccionar un Tipo de Comprobante.');
                    btnConfirmarFinal.disabled = false;
                    btnConfirmarFinal.innerHTML = '<i class="fa-solid fa-file-invoice"></i> Generar Comprobante';
                    return;
                }
                const idtipocomprobante = parseInt(activeDocBox.getAttribute('data-id'));

                // Armamos detalles
                const detalles = confirmCart.map(item => ({
                    idproducto: item.id,
                    cantidad: item.cantidad,
                    precioU: item.precio,
                    importe: item.precio * item.cantidad
                }));

                const payload = {
                    idcliente: idcliente,
                    idusuario: idusuario,
                    idtipocomprobante: idtipocomprobante,
                    idmetodopago: idmetodopago,
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
            const rowSearch = row.getAttribute('data-search') || '';

            let matchesFecha = true;
            if (rowFecha) {
                if (desdeVal && rowFecha < desdeVal) matchesFecha = false;
                if (hastaVal && rowFecha > hastaVal) matchesFecha = false;
            }

            let matchesTipo = (tipoVal === 'Todos' || rowComp === tipoVal);
            let matchesEstado = (estadoVal === 'Todas' || rowEst === estadoVal);
            let matchesSearch = (query === '' || rowSearch.includes(query));

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

            document.getElementById('filterFechaDesde').value = inicioStr;
            document.getElementById('filterFechaHasta').value = finStr;
            document.getElementById('filterTipoComprobante').value = 'Todos';
            document.getElementById('filterEstado').value = 'Todas';
            document.getElementById('filterSearchInput').value = '';

            filterSales();
        });
    }

    // LÓGICA DE DETALLE Y ANULACIÓN (AJAX)
    const detailPanel = document.getElementById('histDetailPanel');
    const closeDetailBtn = document.getElementById('btnCloseDetail');
    const btnAnularVenta = document.getElementById('btnAnularVenta');

    if (histTableBody && detailPanel && closeDetailBtn) {
        // Cerrar panel de detalle
        closeDetailBtn.addEventListener('click', () => {
            detailPanel.classList.remove('active');
        });

        // Delegar clic en los botones de la tabla
        histTableBody.addEventListener('click', (e) => {
            const btnView = e.target.closest('.hist-btn-view');
            if (btnView) {
                e.preventDefault();
                const id = btnView.getAttribute('data-id');
                if (!id) return;

                // Valores temporales de carga
                document.getElementById('detailComprobante').innerText = 'Cargando...';
                document.getElementById('detailTableBody').innerHTML = '<tr><td colspan="4" style="text-align: center; padding: 15px;">Cargando productos...</td></tr>';
                detailPanel.classList.add('active');

                fetch(`/api/ventas/${id}`)
                    .then(res => {
                        if (!res.ok) throw new Error('Error al cargar la venta');
                        return res.json();
                    })
                    .then(venta => {
                        detailPanel.setAttribute('data-id', venta.idventa);

                        // Llenar campos de cabecera
                        document.getElementById('detailComprobante').innerText = `${venta.tipoComprobanteDescripcion} ${venta.nroPedido}`;
                        
                        const dt = new Date(venta.fecha);
                        const fStr = `${String(dt.getDate()).padStart(2, '0')}/${String(dt.getMonth() + 1).padStart(2, '0')}/${dt.getFullYear()}`;
                        const hStr = `${String(dt.getHours()).padStart(2, '0')}:${String(dt.getMinutes()).padStart(2, '0')}`;
                        document.getElementById('detailFecha').innerText = `${fStr} ${hStr}`;
                        
                        document.getElementById('detailVendedor').innerText = venta.usuarioNombre || 'Sistema';
                        document.getElementById('detailCliente').innerText = venta.clienteNombre || 'Cliente Ocasional';
                        
                        // Extraer DNI/RUC de la fila correspondiente de la tabla principal
                        const row = document.querySelector(`#historialTableBody tr[data-id="${id}"]`);
                        let dniText = '00000000';
                        if (row) {
                            const searchAttr = row.getAttribute('data-search') || '';
                            const parts = searchAttr.split(' ');
                            if (parts.length >= 3) {
                                dniText = parts[2];
                            }
                        }
                        document.getElementById('detailDniRuc').innerText = dniText;

                        // Método de Pago Badge
                        const payName = venta.metodoPagoDescripcion || 'Efectivo';
                        const badgePago = document.getElementById('detailPagoBadge');
                        const textPago = document.getElementById('detailPago');
                        if (textPago) textPago.innerText = payName;

                        if (badgePago) {
                            badgePago.className = 'hist-badge';
                            const payLower = payName.toLowerCase();
                            if (payLower.includes('efectivo')) {
                                badgePago.classList.add('hist-bg-efectivo');
                            } else if (payLower.includes('yape')) {
                                badgePago.classList.add('hist-bg-yape');
                            } else {
                                badgePago.classList.add('hist-bg-plin');
                            }
                        }

                        // Llenar tabla de detalles
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

                        // Totales del detalle
                        const subtotal = total / 1.18;
                        const igv = total - subtotal;

                        document.getElementById('detailSubtotal').innerText = `S/ ${subtotal.toFixed(2)}`;
                        document.getElementById('detailIgv').innerText = `S/ ${igv.toFixed(2)}`;
                        document.getElementById('detailTotal').innerText = `S/ ${total.toFixed(2)}`;

                        // Gestión de la Zona de Peligro (Anulación)
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
                        alert('No se pudo cargar la información de la venta: ' + err.message);
                        console.error(err);
                        detailPanel.classList.remove('active');
                    });
            }
        });
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
});