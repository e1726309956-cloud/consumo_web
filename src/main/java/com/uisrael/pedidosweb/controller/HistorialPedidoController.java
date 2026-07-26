package com.uisrael.pedidosweb.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.uisrael.pedidosweb.modelo.dt.request.HistorialPedidoRequestDto;
import com.uisrael.pedidosweb.modelo.dt.response.HistorialPedidoResponseDto;
import com.uisrael.pedidosweb.services.IHistorialPedidoService;

@Controller
@RequestMapping("/historialpedidos")
public class HistorialPedidoController {

    @Autowired
    private IHistorialPedidoService servicioHistorial;

    @GetMapping
    public String listar(Model model) {
        List<HistorialPedidoResponseDto> lista = servicioHistorial.listarHistorial();
        model.addAttribute("listahistorialpedido", lista);
        return "/historialPedidos/listarhistorialpedidos";
    }

    @GetMapping("/nuevo")
    public String crear(Model model) {
        model.addAttribute("historialpedido", new HistorialPedidoRequestDto());
        return "/historialPedidos/crearhistorialpedido";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute HistorialPedidoRequestDto dto) {
        servicioHistorial.guardarHistorial(dto);
        return "redirect:/historialpedidos";
    }

    @GetMapping("/editar/{id}")
    public String editarHistorial(@PathVariable int id, Model model) {
        HistorialPedidoResponseDto historial = servicioHistorial.buscarPorId(id);
        model.addAttribute("historialpedido", historial);
        return "/historialPedidos/crearhistorialpedido";
    }
    

    @GetMapping("/eliminar/{id}")
    public String eliminarHistorial(@PathVariable int id) {
        servicioHistorial.eliminarHistorial(id);
        return "redirect:/historialpedidos";
    }
}
