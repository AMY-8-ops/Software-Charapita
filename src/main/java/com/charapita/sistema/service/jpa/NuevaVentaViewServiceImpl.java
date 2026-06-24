package com.charapita.sistema.service.jpa;

import com.charapita.sistema.dto.ConfirmarVentaDTO;
import com.charapita.sistema.dto.NuevaVentaDTO;
import com.charapita.sistema.entity.Categoria;
import com.charapita.sistema.entity.Cliente;
import com.charapita.sistema.entity.Inventario;
import com.charapita.sistema.entity.MetodoPago;
import com.charapita.sistema.entity.TipoComprobante;
import com.charapita.sistema.entity.Usuario;
import com.charapita.sistema.repository.CategoriaRepository;
import com.charapita.sistema.repository.ClienteRepository;
import com.charapita.sistema.repository.InventarioRepository;
import com.charapita.sistema.repository.MetodoPagoRepository;
import com.charapita.sistema.repository.TipoComprobanteRepository;
import com.charapita.sistema.repository.UsuarioRepository;
import com.charapita.sistema.service.INuevaVentaViewService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NuevaVentaViewServiceImpl implements INuevaVentaViewService {

    private final InventarioRepository inventarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoComprobanteRepository tipoComprobanteRepository;

    public NuevaVentaViewServiceImpl(InventarioRepository inventarioRepository,
                                     CategoriaRepository categoriaRepository,
                                     MetodoPagoRepository metodoPagoRepository,
                                     ClienteRepository clienteRepository,
                                     UsuarioRepository usuarioRepository,
                                     TipoComprobanteRepository tipoComprobanteRepository) {
        this.inventarioRepository = inventarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.tipoComprobanteRepository = tipoComprobanteRepository;
    }

    @Override
    public NuevaVentaDTO getNuevaVentaData() {
        NuevaVentaDTO dto = new NuevaVentaDTO();

        List<Inventario> inventarios = inventarioRepository.findAll().stream()
                .filter(i -> Boolean.TRUE.equals(i.getEstado())
                        && Boolean.TRUE.equals(i.getProducto().getEstado()))
                .toList();

        List<Categoria> categorias = categoriaRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getEstado()))
                .toList();

        List<MetodoPago> metodos = metodoPagoRepository.findAll().stream()
                .filter(mp -> Boolean.TRUE.equals(mp.getEstado()))
                .toList();

        dto.setInventarios(inventarios);
        dto.setCategorias(categorias);
        dto.setMetodos(metodos);

        return dto;
    }

    @Override
    public ConfirmarVentaDTO getConfirmarVentaData() {
        ConfirmarVentaDTO dto = new ConfirmarVentaDTO();

        List<Cliente> clientes = clienteRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getEstado()))
                .toList();

        List<Usuario> usuarios = usuarioRepository.findAll().stream()
                .filter(u -> !Boolean.TRUE.equals(u.getEliminado()))
                .filter(u -> u.getEstado() != null && u.getEstado() == 1)
                .toList();

        List<TipoComprobante> comprobantes = tipoComprobanteRepository.findAll().stream()
                .filter(tc -> Boolean.TRUE.equals(tc.getEstado()))
                .toList();

        List<MetodoPago> metodos = metodoPagoRepository.findAll().stream()
                .filter(mp -> Boolean.TRUE.equals(mp.getEstado()))
                .toList();

        dto.setClientes(clientes);
        dto.setUsuarios(usuarios);
        dto.setComprobantes(comprobantes);
        dto.setMetodos(metodos);

        return dto;
    }
}
