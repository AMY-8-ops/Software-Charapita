package com.charapita.sistema.service.jpa;

import com.charapita.sistema.dto.ConfiguracionDashboardDTO;
import com.charapita.sistema.entity.Rol;
import com.charapita.sistema.entity.Usuario;
import com.charapita.sistema.repository.PermisoRepository;
import com.charapita.sistema.repository.RolRepository;
import com.charapita.sistema.repository.UsuarioRepository;
import com.charapita.sistema.service.IConfiguracionViewService;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
public class ConfiguracionViewServiceImpl implements IConfiguracionViewService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;

    public ConfiguracionViewServiceImpl(UsuarioRepository usuarioRepository,
                                        RolRepository rolRepository,
                                        PermisoRepository permisoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
    }

    @Override
    public ConfiguracionDashboardDTO getConfiguracionDashboardData() {
        ConfiguracionDashboardDTO dto = new ConfiguracionDashboardDTO();

        List<Usuario> listUsuarios = usuarioRepository.findAll().stream()
                .filter(u -> !Boolean.TRUE.equals(u.getEliminado()))
                .toList();

        long usuariosActivos = listUsuarios.stream()
                .filter(u -> u.getEstado() != null && u.getEstado() == 1)
                .count();

        long totalUsuarios = listUsuarios.size();
        long totalRoles = rolRepository.count();
        long totalPermisos = permisoRepository.count();

        Usuario ultimoAccesoUser = listUsuarios.stream()
                .filter(u -> u.getUltimoAcceso() != null)
                .max(Comparator.comparing(Usuario::getUltimoAcceso))
                .orElse(null);

        String ultimoUsuario = "Ninguno";
        String fechaUltimoAcceso = "Sin accesos";
        if (ultimoAccesoUser != null) {
            ultimoUsuario = ultimoAccesoUser.getNombre() + " " + ultimoAccesoUser.getApellido();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            fechaUltimoAcceso = ultimoAccesoUser.getUltimoAcceso().format(formatter);
        }

        List<Rol> listRoles = rolRepository.findAll().stream()
                .filter(r -> Boolean.TRUE.equals(r.getEstado()))
                .toList();

        dto.setUsuarios(listUsuarios);
        dto.setUsuariosActivos(usuariosActivos);
        dto.setTotalUsuarios(totalUsuarios);
        dto.setTotalRoles(totalRoles);
        dto.setTotalPermisos(totalPermisos);
        dto.setUltimoUsuario(ultimoUsuario);
        dto.setFechaUltimoAcceso(fechaUltimoAcceso);
        dto.setRoles(listRoles);

        return dto;
    }
}
