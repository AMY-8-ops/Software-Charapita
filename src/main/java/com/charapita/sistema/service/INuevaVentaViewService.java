package com.charapita.sistema.service;

import com.charapita.sistema.dto.NuevaVentaDTO;
import com.charapita.sistema.dto.ConfirmarVentaDTO;

public interface INuevaVentaViewService {
    NuevaVentaDTO getNuevaVentaData();
    ConfirmarVentaDTO getConfirmarVentaData();
}
