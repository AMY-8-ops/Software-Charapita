package com.charapita.sistema.service;

import java.util.List;
import java.util.Optional;

import com.charapita.sistema.dto.UsuarioResponseDTO;
import com.charapita.sistema.entity.Usuario;

public interface IUsuarioService {

    List<UsuarioResponseDTO> listarTodos();

    Optional<Usuario> buscarPorCorreo(String correo);

    Usuario guardar(Usuario usuario);

    Usuario actualizar(Integer id, Usuario usuario);

    void eliminarLogico(Integer id);

    UsuarioResponseDTO login(String correo, String contrasena);

    void logout(Integer idusuario);
}