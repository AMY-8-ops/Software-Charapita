package com.charapita.sistema.service.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.charapita.sistema.entity.Rol;
import com.charapita.sistema.repository.RolRepository;
import com.charapita.sistema.service.IRolService;

@Service
public class RolServiceImpl implements IRolService {

    private final RolRepository rolRepository;

    public RolServiceImpl(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    @Transactional
    public Rol guardar(Rol rol) {
        rol.setEstado(true); 
        return rolRepository.save(rol);
    }

    @Override
    @Transactional
    public Rol actualizar(Integer id, Rol rolRecibido) {
        Rol existente = rolRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));

        if (rolRecibido.getNombre() != null) {
            existente.setNombre(rolRecibido.getNombre());
        }

        if (rolRecibido.getEstado() != null) {
            existente.setEstado(rolRecibido.getEstado());
        }

        if (rolRecibido.getModNuevaVenta() != null) existente.setModNuevaVenta(rolRecibido.getModNuevaVenta());
        if (rolRecibido.getModClientes() != null) existente.setModClientes(rolRecibido.getModClientes());
        if (rolRecibido.getModProductos() != null) existente.setModProductos(rolRecibido.getModProductos());
        if (rolRecibido.getModVentasHistorial() != null) existente.setModVentasHistorial(rolRecibido.getModVentasHistorial());
        if (rolRecibido.getModReportes() != null) existente.setModReportes(rolRecibido.getModReportes());
        if (rolRecibido.getModCaja() != null) existente.setModCaja(rolRecibido.getModCaja());
        if (rolRecibido.getModConfiguracion() != null) existente.setModConfiguracion(rolRecibido.getModConfiguracion());

        return rolRepository.save(existente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rol> listarTodos() { 
        return rolRepository.findAll(); 
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rol> buscarPorId(Integer id) { 
        return rolRepository.findById(id); 
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Rol existente = rolRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
        existente.setEstado(false);
        rolRepository.save(existente);
    }
}