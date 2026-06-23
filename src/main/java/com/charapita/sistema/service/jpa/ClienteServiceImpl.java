package com.charapita.sistema.service.jpa;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.charapita.sistema.dto.ClienteDTO;
import com.charapita.sistema.entity.Cliente;
import com.charapita.sistema.entity.TipoCliente;
import com.charapita.sistema.repository.ClienteRepository;
import com.charapita.sistema.repository.TipoClienteRepository;
import com.charapita.sistema.service.IClienteService;

@Service
public class ClienteServiceImpl implements IClienteService {

    private final ClienteRepository clienteRepository;
    private final TipoClienteRepository tipoClienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository, TipoClienteRepository tipoClienteRepository) {
        this.clienteRepository = clienteRepository;
        this.tipoClienteRepository = tipoClienteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> listarTodos() {
        return clienteRepository.findAll().stream()
                .filter(cliente -> cliente.getEstado() != null && cliente.getEstado())
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO buscarPorId(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        return convertirADTO(cliente);
    }

    @Override
    @Transactional
    public ClienteDTO guardar(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        mapearDatosAEntidad(cliente, dto);
        cliente.setEstado(true); // Activo por defecto
        
        Cliente guardado = clienteRepository.save(cliente);
        return convertirADTO(guardado);
    }

    @Override
    @Transactional
    public ClienteDTO actualizar(Integer id, ClienteDTO dto) {
        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        
        mapearDatosAEntidad(existente, dto);
        existente.setEstado(dto.getEstado());
        
        Cliente actualizado = clienteRepository.save(existente);
        return convertirADTO(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        
        if (existente.getEstado() != null && !existente.getEstado()) {
            throw new IllegalArgumentException("El cliente ya se encuentra inactivo/eliminado.");
        }

        existente.setEstado(false);
        clienteRepository.save(existente);
    }

    // --- Métodos Privados de Conversión ---
    
    private ClienteDTO convertirADTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setIdcliente(cliente.getIdcliente());
        dto.setRazonsocial(cliente.getRazonsocial());
        dto.setNombre(cliente.getNombre());
        dto.setNroDocumento(cliente.getNroDocumento());
        dto.setEstado(cliente.getEstado());
        dto.setIdtipocliente(cliente.getTipoCliente().getIdtipocliente());
        dto.setTipoClienteNombre(cliente.getTipoCliente().getNombre());
        return dto;
    }

    private void mapearDatosAEntidad(Cliente cliente, ClienteDTO dto) {
        cliente.setRazonsocial(dto.getRazonsocial());
        cliente.setNombre(dto.getNombre());
        cliente.setNroDocumento(dto.getNroDocumento());
        
        TipoCliente tipo = tipoClienteRepository.findById(dto.getIdtipocliente())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de cliente inválido"));
        cliente.setTipoCliente(tipo);
    }
}