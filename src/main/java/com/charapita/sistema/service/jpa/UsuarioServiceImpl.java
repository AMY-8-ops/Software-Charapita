package com.charapita.sistema.service.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.charapita.sistema.dto.UsuarioResponseDTO;
import com.charapita.sistema.entity.Rol;
import com.charapita.sistema.entity.Usuario;
import com.charapita.sistema.repository.RolRepository;
import com.charapita.sistema.repository.UsuarioRepository;
import com.charapita.sistema.repository.MovimientoCajaRepository;
import com.charapita.sistema.service.IUsuarioService;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, RolRepository rolRepository, MovimientoCajaRepository movimientoCajaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.movimientoCajaRepository = movimientoCajaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .filter(u -> !u.getEliminado()) // Filtramos los borrados
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Usuario guardar(Usuario usuario) {
        // Validaciones de negocio preventivas
        if (usuarioRepository.existsByDni(usuario.getDni())) {
            throw new IllegalArgumentException("El DNI ya se encuentra registrado.");
        }

        // --- INICIO: VALIDACIÓN DEFENSIVA DEL ROL ---
        if (usuario.getRol() == null || usuario.getRol().getIdrol() == null) {
            throw new IllegalArgumentException("Error: El campo 'idrol' es obligatorio para crear un usuario.");
        }

        Integer idRolAsignado = usuario.getRol().getIdrol();
        Rol rolBD = rolRepository.findById(idRolAsignado)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Error: El Rol con ID [" + idRolAsignado + "] no existe. Registra el rol primero."));

        if (rolBD.getEstado() != null && !rolBD.getEstado()) {
            throw new IllegalArgumentException(
                    "Error: El Rol con ID [" + idRolAsignado + "] se encuentra inactivo. No se puede asignar.");
        }
        // --- FIN: VALIDACIÓN DEFENSIVA DEL ROL ---

        usuario.setEstado(1); // 1 = Activo
        usuario.setEliminado(false);
        // Aquí a futuro deberías aplicar:
        // usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario actualizar(Integer id, Usuario usuarioRecibido) {
        // 1. Buscamos el usuario existente
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // 2. Actualización inteligente (Patching)
        if (usuarioRecibido.getNombre() != null)
            existente.setNombre(usuarioRecibido.getNombre());
        if (usuarioRecibido.getApellido() != null)
            existente.setApellido(usuarioRecibido.getApellido());
        if (usuarioRecibido.getDireccion() != null)
            existente.setDireccion(usuarioRecibido.getDireccion());
        if (usuarioRecibido.getCorreo() != null)
            existente.setCorreo(usuarioRecibido.getCorreo());
        if (usuarioRecibido.getEstado() != null)
            existente.setEstado(usuarioRecibido.getEstado());
        if (usuarioRecibido.getContrasena() != null && !usuarioRecibido.getContrasena().trim().isEmpty())
            existente.setContrasena(usuarioRecibido.getContrasena());

        // 3. Validación especial para el DNI: Si lo quiere cambiar, verificamos que no
        // lo tenga otro
        if (usuarioRecibido.getDni() != null && !usuarioRecibido.getDni().equals(existente.getDni())) {
            if (usuarioRepository.existsByDni(usuarioRecibido.getDni())) {
                throw new IllegalArgumentException("Error: El nuevo DNI ya se encuentra registrado por otro usuario.");
            }
            existente.setDni(usuarioRecibido.getDni());
        }

        // 4. Validación defensiva para el cambio de Rol
        if (usuarioRecibido.getRol() != null && usuarioRecibido.getRol().getIdrol() != null) {
            Integer idRolAsignado = usuarioRecibido.getRol().getIdrol();
            Rol rolBD = rolRepository.findById(idRolAsignado)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Error: El Rol con ID [" + idRolAsignado + "] no existe."));

            if (rolBD.getEstado() != null && !rolBD.getEstado()) {
                throw new IllegalArgumentException(
                        "Error: El Rol con ID [" + idRolAsignado + "] se encuentra inactivo. No se puede asignar.");
            }
            existente.setRol(rolBD); // Asignamos el rol validado
        }

        // Guardamos los cambios
        return usuarioRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminarLogico(Integer id) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        existente.setEliminado(true);
        existente.setEstado(0);
        usuarioRepository.save(existente);
    }

    private UsuarioResponseDTO convertirADTO(Usuario u) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setIdusuario(u.getIdusuario());
        dto.setNombreCompleto(u.getNombre() + " " + u.getApellido());
        dto.setCorreo(u.getCorreo());
        dto.setRol(u.getRol().getNombre());
        dto.setIdrol(u.getRol().getIdrol());

        com.charapita.sistema.dto.RolPermisosDTO permisos = new com.charapita.sistema.dto.RolPermisosDTO();
        permisos.setModNuevaVenta(u.getRol().getModNuevaVenta() != null ? u.getRol().getModNuevaVenta() : false);
        permisos.setModClientes(u.getRol().getModClientes() != null ? u.getRol().getModClientes() : false);
        permisos.setModProductos(u.getRol().getModProductos() != null ? u.getRol().getModProductos() : false);
        permisos.setModVentasHistorial(u.getRol().getModVentasHistorial() != null ? u.getRol().getModVentasHistorial() : false);
        permisos.setModReportes(u.getRol().getModReportes() != null ? u.getRol().getModReportes() : false);
        permisos.setModCaja(u.getRol().getModCaja() != null ? u.getRol().getModCaja() : false);
        permisos.setModConfiguracion(u.getRol().getModConfiguracion() != null ? u.getRol().getModConfiguracion() : false);
        
        dto.setPermisos(permisos);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO login(String correo, String contrasena) {
        // 1. Buscamos el usuario por su correo
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("Correo o contraseña incorrectos."));

        // 2. Verificamos que el usuario no esté eliminado o inactivo
        if (usuario.getEliminado() != null && usuario.getEliminado() ||
                usuario.getEstado() != null && usuario.getEstado() == 0) {
            throw new IllegalArgumentException("El usuario se encuentra inactivo o ha sido eliminado.");
        }

        // 3. Comparamos las contraseñas en TEXTO PLANO
        if (!usuario.getContrasena().equals(contrasena)) {
            throw new IllegalArgumentException("Correo o contraseña incorrectos.");
        }

        // 4. Actualizamos la fecha de último acceso
        usuario.setUltimoAcceso(java.time.LocalDateTime.now());
        usuarioRepository.save(usuario);

        // 5. Devolvemos el DTO
        return convertirADTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public void logout(Integer idusuario) {
        movimientoCajaRepository.findByUsuario_IdusuarioAndFhCierreIsNull(idusuario)
                .ifPresent(m -> {
                    throw new IllegalArgumentException("No se puede cerrar sesión porque tiene una caja abierta.");
                });
    }
}