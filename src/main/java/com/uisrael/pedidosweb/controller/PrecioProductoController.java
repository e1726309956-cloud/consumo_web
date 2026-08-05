package com.uisrael.pedidosweb.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uisrael.pedidosweb.model.dto.response.PrecioProductoResponseDto;
import com.uisrael.pedidosweb.model.dto.response.ProductoResponseDto;
import com.uisrael.pedidosweb.services.IPrecioProductoService;
import com.uisrael.pedidosweb.services.IProductoService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/precios")
public class PrecioProductoController {

	private final IPrecioProductoService precioService;
	private final IProductoService productoService;

	public PrecioProductoController(IPrecioProductoService precioService, IProductoService productoService) {

		this.precioService = precioService;
		this.productoService = productoService;
	}

	@GetMapping("/producto/{idProducto}")
	public String historial(@PathVariable int idProducto, Model model, HttpSession session,
			RedirectAttributes redirectAttributes) {

		String rol = (String) session.getAttribute("rolUsuario");

		if (!"ADMINISTRADOR".equalsIgnoreCase(rol)) {
			return "redirect:/auth/login";
		}

		try {

			ProductoResponseDto producto = productoService.buscarPorId(idProducto);

			List<PrecioProductoResponseDto> historial = precioService.listarHistorial(idProducto);

			PrecioProductoResponseDto precioActivo = null;

			try {

				precioActivo = precioService.obtenerActivo(idProducto);

			} catch (Exception e) {

				/*
				 * Puede ocurrir cuando el producto todavía no tiene un precio activo
				 * registrado.
				 */
				precioActivo = null;
			}

			model.addAttribute("producto", producto);

			model.addAttribute("historialPrecios", historial != null ? historial : List.of());

			model.addAttribute("precioActivo", precioActivo);

			return "precios/historialprecios";

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("error",
					e.getMessage() != null && !e.getMessage().isBlank() ? e.getMessage()
							: "No fue posible consultar la gestión de precios");

			return "redirect:/producto";
		}
	}

	@PostMapping("/producto/{idProducto}")
	public String registrar(@PathVariable int idProducto, @RequestParam Double precio,
			RedirectAttributes redirectAttributes, HttpSession session) {

		String rol = (String) session.getAttribute("rolUsuario");

		if (!"ADMINISTRADOR".equalsIgnoreCase(rol)) {
			return "redirect:/auth/login";
		}

		try {

			precioService.registrarPrecio(idProducto, precio);

			redirectAttributes.addFlashAttribute("mensaje", "Precio actualizado correctamente");

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("error",
					e.getMessage() != null ? e.getMessage() : "No fue posible actualizar el precio");
		}

		return "redirect:/precios/producto/" + idProducto;
	}
}