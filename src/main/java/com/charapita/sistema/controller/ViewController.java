package com.charapita.sistema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.charapita.sistema.dto.*;
import com.charapita.sistema.service.*;

@Controller
public class ViewController {

    private final IDashboardService dashboardService;
    private final ICajaViewService cajaViewService;
    private final IClienteViewService clienteViewService;
    private final IConfiguracionViewService configuracionViewService;
    private final IHistorialViewService historialViewService;
    private final INuevaVentaViewService nuevaVentaViewService;
    private final IProductoViewService productoViewService;
    private final IReporteViewService reporteViewService;

    public ViewController(IDashboardService dashboardService,
                          ICajaViewService cajaViewService,
                          IClienteViewService clienteViewService,
                          IConfiguracionViewService configuracionViewService,
                          IHistorialViewService historialViewService,
                          INuevaVentaViewService nuevaVentaViewService,
                          IProductoViewService productoViewService,
                          IReporteViewService reporteViewService) {
        this.dashboardService = dashboardService;
        this.cajaViewService = cajaViewService;
        this.clienteViewService = clienteViewService;
        this.configuracionViewService = configuracionViewService;
        this.historialViewService = historialViewService;
        this.nuevaVentaViewService = nuevaVentaViewService;
        this.productoViewService = productoViewService;
        this.reporteViewService = reporteViewService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard.html";
    }

    @GetMapping({ "/dashboard", "/dashboard.html" })
    public String dashboard(Model model) {
        DashboardResponseDTO data = dashboardService.getDashboardData();
        model.addAttribute("datos", data);
        return "dashboard";
    }

    @GetMapping({ "/caja", "/caja/index", "/caja/index.html" })
    public String caja(Model model) {
        CajaDashboardDTO data = cajaViewService.getCajaDashboardData();
        model.addAttribute("datos", data);
        return "caja/index";
    }

    @GetMapping({ "/cliente", "/cliente/index", "/cliente/index.html" })
    public String cliente(Model model) {
        ClienteDashboardDTO data = clienteViewService.getClienteDashboardData();
        model.addAttribute("datos", data);
        return "cliente/index";
    }

    @GetMapping({ "/configuracion", "/configuracion/index", "/configuracion/index.html" })
    public String configuracion(Model model) {
        ConfiguracionDashboardDTO data = configuracionViewService.getConfiguracionDashboardData();
        model.addAttribute("datos", data);
        return "configuracion/index";
    }

    @GetMapping({ "/historial", "/historial/index", "/historial/index.html" })
    public String historial(Model model) {
        HistorialDashboardDTO data = historialViewService.getHistorialDashboardData();
        model.addAttribute("datos", data);
        return "historial/index";
    }

    @GetMapping({ "/nuevaventa", "/nuevaventa/index", "/nuevaventa/index.html" })
    public String nuevaventa(Model model) {
        NuevaVentaDTO data = nuevaVentaViewService.getNuevaVentaData();
        model.addAttribute("datos", data);
        return "nuevaventa/index";
    }

    @GetMapping({ "/nuevaventa/confirmar", "/nuevaventa/confirmar.html" })
    public String confirmarVenta(Model model) {
        ConfirmarVentaDTO data = nuevaVentaViewService.getConfirmarVentaData();
        model.addAttribute("datos", data);
        return "nuevaventa/confirmar";
    }

    @GetMapping({ "/producto", "/producto/index", "/producto/index.html" })
    public String producto(Model model) {
        ProductoDashboardDTO data = productoViewService.getProductoDashboardData();
        model.addAttribute("datos", data);
        return "producto/index";
    }

    @GetMapping({ "/reporte", "/reporte/index", "/reporte/index.html" })
    public String reporte(Model model) {
        ReporteDashboardDTO data = reporteViewService.getReporteDashboardData();
        model.addAttribute("datos", data);
        return "reporte/index";
    }

    @GetMapping("/includes/{page}.html")
    public String getInclude(@PathVariable String page) {
        return "includes/" + page;
    }
}
