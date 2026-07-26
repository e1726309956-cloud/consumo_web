package com.uisrael.pedidosweb.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.uisrael.pedidosweb.modelo.dt.request.DetallePedidoRequestDto;
import com.uisrael.pedidosweb.modelo.dt.response.DetallePedidoResponseDto;
import com.uisrael.pedidosweb.services.IDetallePedidoService;

@Controller
@RequestMapping("/detallepedidos")
public class DetallePedidoController {

    @Autowired
    private IDetallePedidoService servicioDetalle;

    @GetMapping
    public String listar(Model model) {
        List<DetallePedidoResponseDto> lista = servicioDetalle.listarDetallePedido();
        model.addAttribute("listadetallepedido", lista);
        return "/detallePedidos/listardetallepedidos";
    }

    @GetMapping("/nuevo")
    public String crear(Model model) {
        model.addAttribute("detallepedido", new DetallePedidoRequestDto());
        return "/detallePedidos/creardetallepedido";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute DetallePedidoRequestDto dto) {
        servicioDetalle.guardarDetallePedido(dto);
        return "redirect:/detallepedidos";
    }

    @GetMapping("/editar/{id}")
    public String editarDetalle(@PathVariable int id, Model model) {
        DetallePedidoResponseDto detalle = servicioDetalle.buscarPorId(id);
        model.addAttribute("detallepedido", detalle);
        return "/detallePedidos/creardetallepedido";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarDetalle(@PathVariable int id) {
        servicioDetalle.eliminarDetallePedido(id);
        return "redirect:/detallepedidos";
    }
}
