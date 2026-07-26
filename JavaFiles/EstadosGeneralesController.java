package com.uisrael.pedidosweb.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uisrael.pedidosweb.modelo.dt.request.EstadosGeneralesRequestDto;
import com.uisrael.pedidosweb.modelo.dt.response.EstadosGeneralesResponseDto;
import com.uisrael.pedidosweb.services.IEstadosGeneralesService;

@Controller
@RequestMapping("/estadosgenerales")
public class EstadosGeneralesController {

    @Autowired
    private IEstadosGeneralesService servicioEstadosGenerales;

    @GetMapping
    public String leerpagina(Model model) {
        List<EstadosGeneralesResponseDto> resultadosBD = servicioEstadosGenerales.listarEstadosGenerales();
        model.addAttribute("listaestadosgenerales", resultadosBD);
        return "estadosGenerales/listarestadosgenerales";
    }

    @GetMapping("/nuevo")
    public String crearEstadoGeneral(Model model) {
        model.addAttribute("estadogeneral", new EstadosGeneralesRequestDto());
        return "estadosGenerales/crearestadogeneral";
    }

    @PostMapping("/guardar")
    public String guardarEstadoGeneral(@ModelAttribute EstadosGeneralesRequestDto estadoGeneral) {
        servicioEstadosGenerales.guardarEstadoGeneral(estadoGeneral);
        return "redirect:/estadosgenerales";
    }
}