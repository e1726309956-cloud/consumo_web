package com.uisrael.pedidosweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/historialpedido") //url
public class HistorialPedido {

	@GetMapping
	public String leerpagina() {
		return "/historialPedidos/listarhistorialpedidos";//ruta fisica de la paguina 
	}
	//erik
}
