package com.charapita.sistema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard.html";
    }

    @GetMapping({"/dashboard", "/dashboard.html"})
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping({"/caja", "/caja/index", "/caja/index.html"})
    public String caja() {
        return "caja/index";
    }

    @GetMapping({"/cliente", "/cliente/index", "/cliente/index.html"})
    public String cliente() {
        return "cliente/index";
    }

    @GetMapping({"/configuracion", "/configuracion/index", "/configuracion/index.html"})
    public String configuracion() {
        return "configuracion/index";
    }

    @GetMapping({"/historial", "/historial/index", "/historial/index.html"})
    public String historial() {
        return "historial/index";
    }

    @GetMapping({"/nuevaventa", "/nuevaventa/index", "/nuevaventa/index.html"})
    public String nuevaventa() {
        return "nuevaventa/index";
    }

    @GetMapping({"/nuevaventa/confirmar", "/nuevaventa/confirmar.html"})
    public String confirmarVenta() {
        return "nuevaventa/confirmar";
    }

    @GetMapping({"/producto", "/producto/index", "/producto/index.html"})
    public String producto() {
        return "producto/index";
    }

    @GetMapping({"/reporte", "/reporte/index", "/reporte/index.html"})
    public String reporte() {
        return "reporte/index";
    }

    @GetMapping("/includes/{page}.html")
    public String getInclude(@PathVariable String page) {
        return "includes/" + page;
    }
}
