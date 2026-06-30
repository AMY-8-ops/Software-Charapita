package com.charapita.sistema.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.charapita.sistema.entity.Rol;
import com.charapita.sistema.repository.RolRepository;

import java.util.List;

@Configuration
public class RolInitializer {

    @Bean
    public CommandLineRunner initRoles(RolRepository rolRepository) {
        return args -> {
            List<Rol> roles = (List<Rol>) rolRepository.findAll();
            for (Rol rol : roles) {
                boolean changed = false;

                // Si alguno de los campos es null, forzamos a actualizar
                if (rol.getModNuevaVenta() == null) {
                    rol.setModNuevaVenta(false);
                    rol.setModClientes(false);
                    rol.setModProductos(false);
                    rol.setModVentasHistorial(false);
                    rol.setModReportes(false);
                    rol.setModCaja(false);
                    rol.setModConfiguracion(false);
                    changed = true;
                }

                // Asignar según el ID del rol como indicó el usuario
                Integer id = rol.getIdrol();
                if (changed) {
                    if (id == 1) { // Admin: Módulo 1, 3, 4
                        rol.setModNuevaVenta(true);
                        rol.setModCaja(true);
                        rol.setModVentasHistorial(true);
                        rol.setModClientes(true);
                        rol.setModReportes(true);
                        rol.setModConfiguracion(true);
                        // Also usually Admin has everything
                        rol.setModProductos(true);
                    } else if (id == 2) { // Vendedor: Módulo 1, 3
                        rol.setModNuevaVenta(true);
                        rol.setModCaja(true);
                        rol.setModVentasHistorial(true);
                        rol.setModClientes(true);
                    } else if (id == 3) { // Cajero: Módulo 1
                        rol.setModNuevaVenta(true);
                        rol.setModCaja(true);
                        rol.setModVentasHistorial(true);
                    } else if (id == 4) { // Almacenero: Módulo 2
                        rol.setModProductos(true);
                    } else if (id == 5) { // Contador o similar: Módulo 4 (solo reportes)
                        rol.setModReportes(true);
                    }
                    rolRepository.save(rol);
                    System.out.println("Inicializados permisos para el Rol: " + rol.getNombre());
                }
            }
        };
    }
}
