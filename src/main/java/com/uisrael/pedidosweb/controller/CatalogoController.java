package com.uisrael.pedidosweb.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.uisrael.pedidosweb.model.dto.response.ProductoResponseDto;
import com.uisrael.pedidosweb.services.ICategoriaService;
import com.uisrael.pedidosweb.services.IProductoService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/catalogo")
public class CatalogoController {

	private final IProductoService productoService;
	private final ICategoriaService categoriaService;

	public CatalogoController(IProductoService productoService, ICategoriaService categoriaService) {
		super();
		this.productoService = productoService;
		this.categoriaService = categoriaService;
	}

	@GetMapping
	public String listarCatalogo(

			@RequestParam(value = "categoria", required = false) Integer idCategoria,

			@RequestParam(value = "buscar", required = false) String buscar,

			Model model, HttpSession session) {

		String rol = (String) session.getAttribute("rolUsuario");

		if (rol == null) {
			return "redirect:/auth/login";
		}

		if (!"CLIENTE".equalsIgnoreCase(rol)) {
			return "redirect:/producto";
		}

		List<ProductoResponseDto> productos;

		if (idCategoria != null) {

			productos = productoService.buscarPorCategoria(idCategoria);

		} else {

			productos = productoService.listarProducto();
		}

		productos = productos.stream().filter(ProductoResponseDto::isDisponible)
				.filter(producto -> producto.getStock() > 0)
				.filter(producto -> producto.getCategoria() != null && producto.getCategoria().isEstado()).toList();

		if (buscar != null && !buscar.trim().isEmpty()) {

			String textoBusqueda = normalizarTexto(buscar);

			String[] palabras = textoBusqueda.split("\\s+");

			productos = productos.stream().filter(producto -> {

				String nombre = normalizarTexto(producto.getNombre());

				String descripcion = normalizarTexto(producto.getDescripcion());

				String categoria = "";

				if (producto.getCategoria() != null) {
					categoria = normalizarTexto(producto.getCategoria().getNombre());
				}

				String contenido = nombre + " " + descripcion + " " + categoria;

				for (String palabra : palabras) {

					if (!contenido.contains(palabra)) {
						return false;
					}
				}

				return true;
			}).toList();
		}

		model.addAttribute("listarProductos", productos);

		model.addAttribute("categorias", categoriaService.listarCategorias());

		model.addAttribute("categoriaSeleccionada", idCategoria);

		model.addAttribute("textoBusqueda", buscar);

		return "catalogo/catalogo";
	}

	@GetMapping("/detalle/{idProducto}")
	public String verDetalle(@PathVariable int idProducto, Model model, HttpSession session) {

		String rol = (String) session.getAttribute("rolUsuario");

		if (!"CLIENTE".equalsIgnoreCase(rol)) {
			return "redirect:/auth/login";
		}

		ProductoResponseDto producto = productoService.buscarPorId(idProducto);

		if (producto == null || !producto.isDisponible() || producto.getStock() <= 0) {

			return "redirect:/catalogo";
		}

		model.addAttribute("producto", producto);

		return "catalogo/detalleproducto";
	}

	private String normalizarTexto(String texto) {

		if (texto == null) {
			return "";
		}

		String textoNormalizado = java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD);

		return textoNormalizado.replaceAll("\\p{M}", "").toLowerCase().trim();
	}

}