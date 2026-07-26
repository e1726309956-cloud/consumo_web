package com.uisrael.pedidosweb.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uisrael.pedidosweb.modelo.dt.request.EntregaRequestDto;
import com.uisrael.pedidosweb.modelo.dt.response.EntregaResponseDto;
import com.uisrael.pedidosweb.services.IEntregaService;

@Controller
@RequestMapping("/entregas")
public class EntregaController {

    @Autowired
    private IEntregaService servicioEntrega;

    @GetMapping
    public String leerPagina(Model model) {
        List<EntregaResponseDto> resultadosBD = servicioEntrega.listarEntregas();
        model.addAttribute("listaentregas", resultadosBD);
        return "entregas/listarentregas";
    }

    @GetMapping("/nuevo")
    public String crearEntrega(Model model) {
        model.addAttribute("entrega", new EntregaRequestDto());
        return "entregas/crearentrega";
    }

    @PostMapping("/guardar")
    public String guardarEntrega(@ModelAttribute EntregaRequestDto entrega) {
        servicioEntrega.guardarEntrega(entrega);
        return "redirect:/entregas";
    }
}