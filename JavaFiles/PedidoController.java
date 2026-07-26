package com.uisrael.pedidosweb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uisrael.pedidosweb.modelo.dt.request.PedidoRequestDto;
import com.uisrael.pedidosweb.modelo.dt.response.PedidoResponseDto;
import com.uisrael.pedidosweb.services.IPedidoService;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/pedidos") //url
public class PedidoController {
	@Autowired
	private IPedidoService serviciopedido;
	
	@GetMapping
	public String leerpagina(Model model) {
		List<PedidoResponseDto> resultadosBD=serviciopedido.listarpedido();
		model.addAttribute("listapedido",resultadosBD);
		return "/pedidos/listarpedidos";//ruta fisica de la paguina 
	}
	
	@GetMapping("/nuevo")
	public String crearPedido(Model model) {
		model.addAttribute("pedidos", new PedidoRequestDto());
	return "/pedidos/crearpedido";
	}
	
	
	@PostMapping("/guardar")
	public String  guardarPedido(@ModelAttribute PedidoRequestDto pedido ) {
		serviciopedido.guardarpedido(pedido);
		return "redirect:/pedidos";
	}
	
	
}
