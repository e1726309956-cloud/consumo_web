package com.uisrael.pedidosweb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uisrael.pedidosweb.modelo.dt.request.DetallePedidoRequestDto;
import com.uisrael.pedidosweb.modelo.dt.response.DetallePedidoResponseDto;
import com.uisrael.pedidosweb.services.IDetallePedidoService;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/detallepedidos") //url
public class DetallePedidoController {
	
	@Autowired
	private IDetallePedidoService servicioDetallePedido;

	@GetMapping
	public String leerpagina(Model model) {
		List<DetallePedidoResponseDto> resultadosBD=servicioDetallePedido.listardetallepedido();
		model.addAttribute("listadetallepedido",resultadosBD);
		return "/detallePedidos/listardetallepedidos";//ruta fisica de la paguina 
	}
	
	@GetMapping("/nuevo")
	public String crearDetallePedido(Model model) {
		model.addAttribute("detallepedido", new DetallePedidoRequestDto());
	return "/detallepedido/creardetallepedido";
	}
	
	
	@PostMapping("/guardar")
	public String  guardarDetallePedido(@ModelAttribute DetallePedidoRequestDto detallepedido ) {
		servicioDetallePedido.guardarDetallePedido(detallepedido);
		return "redirect:/detallepedidos";
	}
	
}
