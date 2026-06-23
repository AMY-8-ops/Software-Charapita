package com.charapita.sistema.service.jpa;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.charapita.sistema.entity.Accion;
import com.charapita.sistema.entity.Permiso;
import com.charapita.sistema.entity.Recurso;
import com.charapita.sistema.repository.AccionRepository;
import com.charapita.sistema.repository.PermisoRepository;
import com.charapita.sistema.repository.RecursoRepository;
import com.charapita.sistema.service.IPermisoService;

@Service
public class PermisoServiceImpl implements IPermisoService {

    private final PermisoRepository permisoRepository;
    // 1. AGREGAMOS LOS REPOSITORIOS PADRE
    private final AccionRepository accionRepository;
    private final RecursoRepository recursoRepository;

    public PermisoServiceImpl(PermisoRepository permisoRepository, AccionRepository accionRepository, RecursoRepository recursoRepository) {
        this.permisoRepository = permisoRepository;
        this.accionRepository = accionRepository;
        this.recursoRepository = recursoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permiso> listarTodos() {
        return permisoRepository.findAll().stream()
                .filter(permiso -> permiso.getEstado() != null && permiso.getEstado())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Permiso> buscarPorId(Integer id) {
        return permisoRepository.findById(id);
    }

    @Override
    @Transactional
    public Permiso guardar(Permiso permiso) {
        // 2. VALIDAMOS LA ACCIÓN
        if (permiso.getAccion() == null || permiso.getAccion().getIdaccion() == null) {
            throw new IllegalArgumentException("Error: El campo 'idaccion' es obligatorio.");
        }
        Integer idAccion = permiso.getAccion().getIdaccion();
        Accion accionBD = accionRepository.findById(idAccion)
                .orElseThrow(() -> new IllegalArgumentException("Error: La Acción con ID [" + idAccion + "] no existe."));
        if (accionBD.getEstado() != null && !accionBD.getEstado()) {
            throw new IllegalArgumentException("Error: La Acción con ID [" + idAccion + "] está inactiva.");
        }

        // 3. VALIDAMOS EL RECURSO
        if (permiso.getRecurso() == null || permiso.getRecurso().getIdrecurso() == null) {
            throw new IllegalArgumentException("Error: El campo 'idrecurso' es obligatorio.");
        }
        Integer idRecurso = permiso.getRecurso().getIdrecurso();
        Recurso recursoBD = recursoRepository.findById(idRecurso)
                .orElseThrow(() -> new IllegalArgumentException("Error: El Recurso con ID [" + idRecurso + "] no existe."));
        if (recursoBD.getEstado() != null && !recursoBD.getEstado()) {
            throw new IllegalArgumentException("Error: El Recurso con ID [" + idRecurso + "] está inactivo.");
        }

        permiso.setEstado(true);
        return permisoRepository.save(permiso);
    }

    @Override
    @Transactional
    public Permiso actualizar(Integer id, Permiso permisoRecibido) {
        Permiso existente = permisoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Permiso no encontrado"));

        if (permisoRecibido.getDescripcion() != null) {
            existente.setDescripcion(permisoRecibido.getDescripcion());
        }

        // VALIDACIÓN EN EL PUT PARA LA ACCIÓN
        if (permisoRecibido.getAccion() != null && permisoRecibido.getAccion().getIdaccion() != null) {
            Integer idAccion = permisoRecibido.getAccion().getIdaccion();
            Accion accionBD = accionRepository.findById(idAccion)
                    .orElseThrow(() -> new IllegalArgumentException("Error: La Acción con ID [" + idAccion + "] no existe."));
            if (accionBD.getEstado() != null && !accionBD.getEstado()) {
                throw new IllegalArgumentException("Error: La Acción con ID [" + idAccion + "] está inactiva.");
            }
            existente.setAccion(permisoRecibido.getAccion());
        }

        // VALIDACIÓN EN EL PUT PARA EL RECURSO
        if (permisoRecibido.getRecurso() != null && permisoRecibido.getRecurso().getIdrecurso() != null) {
            Integer idRecurso = permisoRecibido.getRecurso().getIdrecurso();
            Recurso recursoBD = recursoRepository.findById(idRecurso)
                    .orElseThrow(() -> new IllegalArgumentException("Error: El Recurso con ID [" + idRecurso + "] no existe."));
            if (recursoBD.getEstado() != null && !recursoBD.getEstado()) {
                throw new IllegalArgumentException("Error: El Recurso con ID [" + idRecurso + "] está inactivo.");
            }
            existente.setRecurso(permisoRecibido.getRecurso());
        }

        if (permisoRecibido.getEstado() != null) {
            existente.setEstado(permisoRecibido.getEstado());
        }

        return permisoRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Permiso existente = permisoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Permiso no encontrado"));
        existente.setEstado(false);
        permisoRepository.save(existente);
    }
}