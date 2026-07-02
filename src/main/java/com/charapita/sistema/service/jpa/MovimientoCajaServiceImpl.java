package com.charapita.sistema.service.jpa;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.charapita.sistema.dto.MovimientoCajaRequestDTO;
import com.charapita.sistema.dto.MovimientoCajaResponseDTO;
import com.charapita.sistema.entity.Caja;
import com.charapita.sistema.entity.MovimientoCaja;
import com.charapita.sistema.entity.Usuario;
import com.charapita.sistema.repository.CajaRepository;
import com.charapita.sistema.repository.MovimientoCajaRepository;
import com.charapita.sistema.repository.UsuarioRepository;
import com.charapita.sistema.service.IAnomaliaService;
import com.charapita.sistema.service.IMovimientoCajaService;

@Service
public class MovimientoCajaServiceImpl implements IMovimientoCajaService {

    private final MovimientoCajaRepository movimientoRepository;
    private final CajaRepository cajaRepository;
    private final UsuarioRepository usuarioRepository;
    private final IAnomaliaService anomaliaService;

    public MovimientoCajaServiceImpl(MovimientoCajaRepository movimientoRepository, CajaRepository cajaRepository,
            UsuarioRepository usuarioRepository, IAnomaliaService anomaliaService) {
        this.movimientoRepository = movimientoRepository;
        this.cajaRepository = cajaRepository;
        this.usuarioRepository = usuarioRepository;
        this.anomaliaService = anomaliaService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoCajaResponseDTO> listarHistorial() {
        return movimientoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MovimientoCajaResponseDTO abrirCaja(MovimientoCajaRequestDTO dto) {
        Caja caja = cajaRepository.findById(dto.getIdcaja())
                .orElseThrow(() -> new IllegalArgumentException("Caja no encontrada"));
        Usuario usuario = usuarioRepository.findById(dto.getIdusuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        MovimientoCaja movimiento = new MovimientoCaja();
        movimiento.setFhApertura(LocalDateTime.now());
        movimiento.setMontoinicial(dto.getMontoinicial());
        movimiento.setCaja(caja);
        movimiento.setUsuario(usuario);
        // El monto final, cierre y diferencia se quedan en nulo hasta cerrar el turno

        MovimientoCaja guardado = movimientoRepository.save(movimiento);
        return convertirADTO(guardado);
    }

    @Override
    @Transactional
    public MovimientoCajaResponseDTO cerrarCaja(Integer idmovimiento, BigDecimal montofinal) {
        MovimientoCaja movimiento = movimientoRepository.findById(idmovimiento)
                .orElseThrow(() -> new IllegalArgumentException("Movimiento de caja no encontrado"));

        if (movimiento.getFhCierre() != null) {
            throw new IllegalStateException("Esta caja ya fue cerrada anteriormente");
        }

        movimiento.setFhCierre(LocalDateTime.now());
        movimiento.setMontofinal(montofinal);

        // Lógica: Diferencia = Monto Final declarado - Monto Inicial (En un sistema
        // real se suma lo vendido)
        BigDecimal diferencia = montofinal.subtract(movimiento.getMontoinicial());
        movimiento.setDiferencia(diferencia);

        MovimientoCaja guardado = movimientoRepository.save(movimiento);

        // Evaluar anomalía de forma asíncrona o sincrona
        anomaliaService.evaluarMovimiento(guardado);

        return convertirADTO(guardado);
    }

    private MovimientoCajaResponseDTO convertirADTO(MovimientoCaja m) {
        MovimientoCajaResponseDTO dto = new MovimientoCajaResponseDTO();
        dto.setIdmovimiento(m.getIdmovimiento());
        dto.setFhApertura(m.getFhApertura());
        dto.setFhCierre(m.getFhCierre());
        dto.setMontoinicial(m.getMontoinicial());
        dto.setMontofinal(m.getMontofinal());
        dto.setDiferencia(m.getDiferencia());
        dto.setNombreCaja(m.getCaja().getNombre());
        dto.setNombreCajero(m.getUsuario().getNombre() + " " + m.getUsuario().getApellido());
        return dto;
    }
}