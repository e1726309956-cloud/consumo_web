package com.uisrael.pedidosweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/detallepedido") //url
public class DetallePedido {

	@GetMapping
	public String leerpagina() {
		return "/detallePedidos/listardetallepedidos";//ruta fisica de la paguina 
	}
	//erik
}
