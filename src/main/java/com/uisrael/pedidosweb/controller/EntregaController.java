package com.uisrael.pedidosweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uisrael.pedidosweb.model.dto.request.EntregaRequestDto;
import com.uisrael.pedidosweb.services.IEntregaService;
import com.uisrael.pedidosweb.services.IEstadosGeneralesService;
import com.uisrael.pedidosweb.services.IPedidoService;

@Controller
@RequestMapping("/entregas")
public class EntregaController {

    private final IEntregaService entregaService;
    private final IPedidoService pedidoService;
    private final IEstadosGeneralesService estadosGeneralesService;

    public EntregaController(IEntregaService entregaService, 
                             IPedidoService pedidoService, 
                             IEstadosGeneralesService estadosGeneralesService) {
        this.entregaService = entregaService;
        this.pedidoService = pedidoService;
        this.estadosGeneralesService = estadosGeneralesService;
    }

    @GetMapping
    public String listarEntregas(Model model) {
        model.addAttribute("listaentregas", entregaService.listarEntregas());
        return "entregas/listarentregas";
    }

    @GetMapping("/crear")
    public String crearEntrega(Model model) {
        model.addAttribute("entrega", new EntregaRequestDto());
        model.addAttribute("listapedidos", pedidoService.listarpedido());
        model.addAttribute("listaestados", estadosGeneralesService.listarEstadosGenerales());
        return "entregas/crearentrega";
    }

    @PostMapping("/guardar")
    public String guardarEntrega(@ModelAttribute("entrega") EntregaRequestDto entregaDto) {
        entregaService.guardarEntrega(entregaDto);
        return "redirect:/entregas";
    }
}