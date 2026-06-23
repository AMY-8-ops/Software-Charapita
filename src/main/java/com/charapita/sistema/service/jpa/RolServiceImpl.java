package com.charapita.sistema.service.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.charapita.sistema.entity.Permiso;
import com.charapita.sistema.entity.Rol; // <-- Asegúrate de importar la entidad Permiso
import com.charapita.sistema.repository.PermisoRepository;
import com.charapita.sistema.repository.RolRepository;
import com.charapita.sistema.service.IRolService;

@Service
public class RolServiceImpl implements IRolService {

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;

    public RolServiceImpl(RolRepository rolRepository, PermisoRepository permisoRepository) {
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
    }

    @Override
    @Transactional
    public Rol guardar(Rol rol) {
        // 1. Validar que no envíen el objeto permiso vacío o sin ID
        if (rol.getPermiso() == null || rol.getPermiso().getIdpermiso() == null) {
            throw new IllegalArgumentException("Error: El campo 'idpermiso' es obligatorio para crear un rol.");
        }

        Integer idPermisoAsignado = rol.getPermiso().getIdpermiso();
        
        // 2. Buscar el permiso en la base de datos de manera explícita
        Permiso permisoBD = permisoRepository.findById(idPermisoAsignado)
                .orElseThrow(() -> new IllegalArgumentException("Error: El permiso con ID [" + idPermisoAsignado + "] no existe. Registra el permiso primero."));

        // 3. NUEVA VALIDACIÓN: Validar si el permiso existe pero está desactivado/eliminado lógicamente
        if (permisoBD.getEstado() != null && !permisoBD.getEstado()) {
            throw new IllegalArgumentException("Error: El permiso con ID [" + idPermisoAsignado + "] se encuentra inactivo o eliminado. No se puede asignar a un nuevo rol.");
        }

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
        
        // Si intentan cambiar el permiso en el PUT, también aplicamos la lógica defensiva
        if (rolRecibido.getPermiso() != null && rolRecibido.getPermiso().getIdpermiso() != null) {
            Integer idPermisoAsignado = rolRecibido.getPermiso().getIdpermiso();
            
            // Buscamos el permiso que intentan asignar en la actualización
            Permiso permisoBD = permisoRepository.findById(idPermisoAsignado)
                    .orElseThrow(() -> new IllegalArgumentException("Error: El permiso con ID [" + idPermisoAsignado + "] no existe."));

            // Validamos que el nuevo permiso a enlazar no esté desactivado
            if (permisoBD.getEstado() != null && !permisoBD.getEstado()) {
                throw new IllegalArgumentException("Error: El permiso con ID [" + idPermisoAsignado + "] se encuentra inactivo o eliminado. No se puede asignar.");
            }
            
            existente.setPermiso(rolRecibido.getPermiso());
        }

        if (rolRecibido.getEstado() != null) {
            existente.setEstado(rolRecibido.getEstado());
        }

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