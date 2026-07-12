package com.uisrael.pedidosweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pedido") //url
public class PedidoController {
	@GetMapping
	public String leerpagina() {
		return "/pedidos/listarpedidos";//ruta fisica de la paguina 
	}
	//erik
}
