package com.uisrael.pedidosweb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uisrael.pedidosweb.modelo.dt.request.HistorialPedidoRequestDto;
import com.uisrael.pedidosweb.modelo.dt.response.HistorialPedidoResponseDto;
import com.uisrael.pedidosweb.services.IHistorialPedidoService;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/historialpedidos") //url
public class HistorialPedidoController {
	
	@Autowired
	private IHistorialPedidoService servicioHistorialPedido;

	@GetMapping
	public String leerpagina(Model model) {
		List<HistorialPedidoResponseDto> resultadosBD=servicioHistorialPedido.listarhistorialpedido();
		model.addAttribute("listahistorialpedido",resultadosBD);
		return "/historialPedidos/listarhistorialpedidos";//ruta fisica de la paguina 
	}
	
	@GetMapping("/nuevo")
	public String crearHistorialPedido(Model model) {
		model.addAttribute("historialpedido", new HistorialPedidoRequestDto());
	return "/grupos/crearhistorialpedido";
	}
	
	
	@PostMapping("/guardar")
	public String  guardarGrupo(@ModelAttribute HistorialPedidoRequestDto historialpedido ) {
		servicioHistorialPedido.guardarhistorialpedido(historialpedido);
		return "redirect:/historialpedidos";
	}
	
}
