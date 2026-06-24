package com.charapita.sistema.service.jpa;

import com.charapita.sistema.dto.ClienteDashboardDTO;
import com.charapita.sistema.dto.ClienteInfoDTO;
import com.charapita.sistema.entity.Cliente;
import com.charapita.sistema.entity.DetalleVenta;
import com.charapita.sistema.entity.TipoCliente;
import com.charapita.sistema.entity.Venta;
import com.charapita.sistema.repository.ClienteRepository;
import com.charapita.sistema.repository.DetalleVentaRepository;
import com.charapita.sistema.repository.TipoClienteRepository;
import com.charapita.sistema.repository.VentaRepository;
import com.charapita.sistema.service.IClienteViewService;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
public class ClienteViewServiceImpl implements IClienteViewService {

    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final TipoClienteRepository tipoClienteRepository;

    public ClienteViewServiceImpl(ClienteRepository clienteRepository,
                                  VentaRepository ventaRepository,
                                  DetalleVentaRepository detalleVentaRepository,
                                  TipoClienteRepository tipoClienteRepository) {
        this.clienteRepository = clienteRepository;
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.tipoClienteRepository = tipoClienteRepository;
    }

    @Override
    public ClienteDashboardDTO getClienteDashboardData() {
        ClienteDashboardDTO dto = new ClienteDashboardDTO();

        List<Cliente> listClientes = clienteRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getEstado()))
                .toList();

        List<Venta> activeSales = ventaRepository.findAll().stream()
                .filter(v -> Boolean.TRUE.equals(v.getEstado()))
                .toList();

        List<DetalleVenta> details = detalleVentaRepository.findAll();

        List<ClienteInfoDTO> clienteInfos = listClientes.stream()
                .map(c -> {
                    List<Venta> clientSales = activeSales.stream()
                            .filter(v -> v.getCliente() != null && v.getCliente()
                                    .getIdcliente().equals(c.getIdcliente()))
                            .toList();

                    java.math.BigDecimal total = clientSales.stream()
                            .flatMap(v -> details.stream()
                                    .filter(d -> d.getVenta().getIdventa()
                                            .equals(v.getIdventa())))
                            .map(d -> d.getImporte() != null ? d.getImporte()
                                    : java.math.BigDecimal.ZERO)
                            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

                    String lastPurchase = clientSales.stream()
                            .map(Venta::getFecha)
                            .filter(java.util.Objects::nonNull)
                            .max(Comparator.naturalOrder())
                            .map(f -> f.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                            .orElse("Sin compras");

                    String doc = c.getNroDocumento();
                    String tel = "9" + (doc != null && doc.length() >= 8
                            ? doc.substring(doc.length() - 8)
                            : "87654321");
                    String cleanName = c.getNombre() != null ? c.getNombre() : c.getRazonsocial();
                    String email = cleanName.toLowerCase().trim().replace(" ", ".") + "@gmail.com";

                    return new ClienteInfoDTO(c, tel, email, lastPurchase, total);
                })
                .toList();

        long totalClientes = listClientes.size();
        long clientesNuevos = totalClientes;

        ClienteInfoDTO best = clienteInfos.stream()
                .max(Comparator.comparing(ClienteInfoDTO::getTotalConsumido))
                .orElse(null);

        String mejorClienteName = "Ninguno";
        java.math.BigDecimal mejorClienteTotal = java.math.BigDecimal.ZERO;
        if (best != null && best.getTotalConsumido().compareTo(java.math.BigDecimal.ZERO) > 0) {
            mejorClienteName = best.getCliente().getNombre() != null ? best.getCliente().getNombre()
                    : best.getCliente().getRazonsocial();
            mejorClienteTotal = best.getTotalConsumido();
        }

        List<Long> intervals = new java.util.ArrayList<>();
        java.util.Map<Integer, List<Venta>> salesByClient = activeSales.stream()
                .filter(v -> v.getCliente() != null)
                .collect(java.util.stream.Collectors.groupingBy(v -> v.getCliente().getIdcliente()));

        for (List<Venta> clientSales : salesByClient.values()) {
            if (clientSales.size() > 1) {
                List<java.time.LocalDateTime> dates = clientSales.stream()
                        .map(Venta::getFecha)
                        .filter(java.util.Objects::nonNull)
                        .sorted()
                        .toList();
                for (int i = 0; i < dates.size() - 1; i++) {
                    long days = java.time.temporal.ChronoUnit.DAYS.between(dates.get(i),
                            dates.get(i + 1));
                    intervals.add(days);
                }
            }
        }
        double avgFrecuencia = intervals.stream().mapToLong(Long::longValue).average().orElse(15.0);

        List<TipoCliente> tipos = tipoClienteRepository.findAll().stream()
                .filter(tc -> Boolean.TRUE.equals(tc.getEstado()))
                .toList();

        dto.setClientes(clienteInfos);
        dto.setTipos(tipos);
        dto.setTotalClientes(totalClientes);
        dto.setClientesNuevos(clientesNuevos);
        dto.setMejorCliente(mejorClienteName);
        dto.setTotalMejorCliente(mejorClienteTotal);
        dto.setFrecuenciaPromedio(Math.round(avgFrecuencia));

        return dto;
    }
}
