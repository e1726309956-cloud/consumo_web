package com.uisrael.pedidosweb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	@GetMapping("/editar/{id}")
	public String editarPedido(@PathVariable int id, Model model) {
	    PedidoResponseDto pedido = serviciopedido.buscarPorId(id);
	    model.addAttribute("pedidos", pedido);
	    return "/pedidos/crearpedido"; // reutiliza el mismo formulario
	}

	@GetMapping("/eliminar/{id}")
	public String eliminarPedido(@PathVariable int id) {
	    serviciopedido.eliminarPedido(id);
	    return "redirect:/pedidos";
	}

	
	
}
