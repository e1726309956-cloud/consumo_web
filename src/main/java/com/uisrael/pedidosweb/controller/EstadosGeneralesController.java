package com.uisrael.pedidosweb.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uisrael.pedidosweb.modelo.dt.request.EstadosGeneralesRequestDto;
import com.uisrael.pedidosweb.modelo.dt.response.EstadosGeneralesResponseDto;
import com.uisrael.pedidosweb.services.IEstadosGeneralesService;

@Controller
@RequestMapping({"/estadosGenerales", "/estadosgenerales"})
public class EstadosGeneralesController {

    private final IEstadosGeneralesService estadosGeneralesService;

    public EstadosGeneralesController(IEstadosGeneralesService estadosGeneralesService) {
        this.estadosGeneralesService = estadosGeneralesService;
    }

    @GetMapping
    public String listarEstadosGenerales(Model model) {
        try {
            List<EstadosGeneralesResponseDto> lista = estadosGeneralesService.listarEstadosGenerales();
            model.addAttribute("listaestados", lista != null ? lista : Collections.emptyList());
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("listaestados", Collections.emptyList());
            model.addAttribute("mensajeError", "Error al obtener la lista de estados.");
        }
        return "estadosGenerales/listarestadosgenerales";
    }

    @GetMapping({"/crear", "/nuevo"})
    public String crearEstadoGeneral(Model model) {
        model.addAttribute("estadoGeneral", new EstadosGeneralesRequestDto());
        return "estadosGenerales/crearestadogeneral";
    }

    @PostMapping("/guardar")
    public String guardarEstadoGeneral(@ModelAttribute("estadoGeneral") EstadosGeneralesRequestDto estadoGeneralDto, RedirectAttributes redirectAttributes) {
        try {
            estadosGeneralesService.guardarEstadoGeneral(estadoGeneralDto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Estado procesado exitosamente.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensajeError", "Error al guardar en el backend.");
        }
        return "redirect:/estadosGenerales";
    }

    @GetMapping("/editar/{id}")
    public String editarEstadoGeneral(@PathVariable("id") int id, Model model) {
        try {
            EstadosGeneralesResponseDto response = estadosGeneralesService.obtenerPorId(id);
            
            EstadosGeneralesRequestDto request = new EstadosGeneralesRequestDto();
            request.setIdEstado(response.getIdEstado());
            request.setNombreEstado(response.getNombreEstado());
            request.setDescripcion(response.getDescripcion());
            request.setEstado(response.isEstado());

            model.addAttribute("estadoGeneral", request);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("estadoGeneral", new EstadosGeneralesRequestDto());
        }
        return "estadosGenerales/crearestadogeneral";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarEstadoGeneral(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        try {
            estadosGeneralesService.eliminarEstadoGeneral(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Estado eliminado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensajeError", "No se puede eliminar el estado porque está en uso en la base de datos.");
        }
        return "redirect:/estadosGenerales";
    }
}