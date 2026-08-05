package com.uisrael.pedidosweb.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.uisrael.pedidosweb.model.dto.request.ProductoRequestDto;
import com.uisrael.pedidosweb.model.dto.response.CategoriaResponseDto;
import com.uisrael.pedidosweb.model.dto.response.ProductoResponseDto;
import com.uisrael.pedidosweb.services.ICategoriaService;
import com.uisrael.pedidosweb.services.IPrecioProductoService;
import com.uisrael.pedidosweb.services.IProductoService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/producto")
public class ProductoController {

	private final IProductoService servicioProducto;
	private final ICategoriaService servicioCategoria;
	private final IPrecioProductoService precioProductoService;

	public ProductoController(IProductoService servicioProducto, ICategoriaService servicioCategoria,
			IPrecioProductoService precioProductoService) {
		super();
		this.servicioProducto = servicioProducto;
		this.servicioCategoria = servicioCategoria;
		this.precioProductoService = precioProductoService;
	}

	@GetMapping
	public String leerPagina(Model model, HttpSession session) {

		if (!esAdministrador(session)) {
			return redireccionSegunSesion(session);
		}

		List<ProductoResponseDto> productos = servicioProducto.listarProducto();

		model.addAttribute("listarProductos", productos);

		return "productos/listarproductos";
	}

	@GetMapping("/nuevo")
	public String nuevoProducto(Model model, HttpSession session) {

		if (!esAdministrador(session)) {
			return redireccionSegunSesion(session);
		}

		ProductoRequestDto producto = new ProductoRequestDto();

		List<CategoriaResponseDto> categorias = servicioCategoria.listarActivas();

		model.addAttribute("producto", producto);

		model.addAttribute("categorias", categorias);

		return "productos/nuevosproductos";
	}

	@PostMapping("/guardar")
	public String guardarProducto(@ModelAttribute("producto") ProductoRequestDto producto,

			@RequestParam(value = "imagen", required = false) MultipartFile imagen,

			HttpSession session) {

		if (!esAdministrador(session)) {
			return redireccionSegunSesion(session);
		}

		if (producto.getIdProducto() > 0) {

			servicioProducto.actualizarProducto(producto, imagen);

		} else {

			servicioProducto.guardarProducto(producto, imagen);
		}

		return "redirect:/producto";
	}

	@GetMapping("/inactivar/{idProducto}")
	public String inactivarProducto(@PathVariable int idProducto, HttpSession session) {

		if (!esAdministrador(session)) {
			return redireccionSegunSesion(session);
		}

		servicioProducto.inactivarProducto(idProducto);

		return "redirect:/producto";
	}

	@GetMapping("/activar/{idProducto}")
	public String activarProducto(@PathVariable int idProducto, HttpSession session) {

		if (!esAdministrador(session)) {
			return redireccionSegunSesion(session);
		}

		servicioProducto.activarProducto(idProducto);

		return "redirect:/producto";
	}

	@GetMapping("/editar/{idProducto}")
	public String editarProducto(@PathVariable int idProducto, Model model, HttpSession session) {

		if (!esAdministrador(session)) {
			return redireccionSegunSesion(session);
		}

		ProductoResponseDto productoApi = servicioProducto.buscarPorId(idProducto);

		ProductoRequestDto producto = new ProductoRequestDto();

		producto.setIdProducto(productoApi.getIdProducto());

		producto.setNombre(productoApi.getNombre());

		producto.setDescripcion(productoApi.getDescripcion());

		producto.setPrecio(productoApi.getPrecio());

		producto.setStock(productoApi.getStock());

		producto.setImagenUrl(productoApi.getImagenUrl());

		producto.setDisponible(productoApi.isDisponible());

		producto.setFechaCreacion(productoApi.getFechaCreacion());

		if (productoApi.getCategoria() != null) {

			producto.setCategoria(productoApi.getCategoria().getIdCategoria());
		}

		model.addAttribute("producto", producto);

		model.addAttribute("categorias", servicioCategoria.listarActivas());

		return "productos/nuevosproductos";
	}

	private boolean esAdministrador(HttpSession session) {

		String rol = (String) session.getAttribute("rolUsuario");

		return "ADMINISTRADOR".equalsIgnoreCase(rol);
	}

	private String redireccionSegunSesion(HttpSession session) {

		String rol = (String) session.getAttribute("rolUsuario");

		if (rol == null) {
			return "redirect:/auth/login";
		}

		return "redirect:/catalogo";
	}
}