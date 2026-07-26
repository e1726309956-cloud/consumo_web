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
import com.uisrael.pedidosweb.services.IEstadosGeneralesService; // Ajusta el nombre según tu interfaz de servicio

@Controller
@RequestMapping({"/estadosGenerales", "/estadosgenerales"})
public class EstadosGeneralesController {

    @Autowired
    private IEstadosGeneralesService servicioEstadosGenerales;

    @GetMapping
    public String leerPagina(Model model) {
        List<EstadosGeneralesResponseDto> resultadosBD = servicioEstadosGenerales.listarEstadosGenerales();
        model.addAttribute("listaEstados", resultadosBD);
        return "estadosGenerales/listarestadosgenerales";
    }

    @GetMapping("/nuevo")
    public String crearEstado(Model model) {
        model.addAttribute("estadoGeneral", new EstadosGeneralesRequestDto());
        return "estadosGenerales/crearestadogeneral";
    }

    @PostMapping("/guardar")
    public String guardarEstado(@ModelAttribute("estadoGeneral") EstadosGeneralesRequestDto estado) {
        servicioEstadosGenerales.guardarEstadoGeneral(estado);
        return "redirect:/estadosGenerales";
    }
}