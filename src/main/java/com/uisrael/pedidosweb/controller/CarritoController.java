package com.uisrael.pedidosweb.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uisrael.pedidosweb.model.dto.carrito.CarritoItemDto;
import com.uisrael.pedidosweb.model.dto.request.DetallePedidoRequestDto;
import com.uisrael.pedidosweb.model.dto.request.PedidoRequestDto;
import com.uisrael.pedidosweb.model.dto.response.PedidoResponseDto;
import com.uisrael.pedidosweb.model.dto.response.ProductoResponseDto;
import com.uisrael.pedidosweb.services.IPedidoService;
import com.uisrael.pedidosweb.services.IProductoService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

	private final IProductoService productoService;
	private final IPedidoService pedidoService;

	public CarritoController(IProductoService productoService, IPedidoService pedidoService) {

		this.productoService = productoService;
		this.pedidoService = pedidoService;
	}

	@SuppressWarnings("unchecked")
	private List<CarritoItemDto> obtenerCarrito(HttpSession session) {

		List<CarritoItemDto> carrito = (List<CarritoItemDto>) session.getAttribute("carrito");

		if (carrito == null) {

			carrito = new ArrayList<>();

			session.setAttribute("carrito", carrito);
		}

		return carrito;
	}

	private boolean esCliente(HttpSession session) {

		String rol = (String) session.getAttribute("rolUsuario");

		return "CLIENTE".equalsIgnoreCase(rol);
	}

	private void actualizarCantidadCarrito(HttpSession session, List<CarritoItemDto> carrito) {

		int cantidadTotal = carrito.stream().mapToInt(CarritoItemDto::getCantidad).sum();

		session.setAttribute("cantidadCarrito", cantidadTotal);
	}

	@GetMapping
	public String verCarrito(HttpSession session, Model model) {

		if (!esCliente(session)) {
			return "redirect:/auth/login";
		}

		List<CarritoItemDto> carrito = obtenerCarrito(session);

		double total = carrito.stream().mapToDouble(CarritoItemDto::getSubtotal).sum();

		int cantidadTotal = carrito.stream().mapToInt(CarritoItemDto::getCantidad).sum();

		model.addAttribute("carrito", carrito);

		model.addAttribute("total", total);

		model.addAttribute("cantidadTotal", cantidadTotal);

		return "carrito/carrito";
	}

	@PostMapping("/agregar/{idProducto}")
	public String agregarProducto(@PathVariable int idProducto,

			@RequestParam(defaultValue = "1") int cantidad,

			HttpSession session, RedirectAttributes redirectAttributes) {

		if (!esCliente(session)) {
			return "redirect:/auth/login";
		}

		if (cantidad < 1) {
			cantidad = 1;
		}

		try {

			ProductoResponseDto producto = productoService.buscarPorId(idProducto);

			if (producto == null) {

				redirectAttributes.addFlashAttribute("error", "Producto no encontrado");

				return "redirect:/catalogo";
			}

			if (!producto.isDisponible()) {

				redirectAttributes.addFlashAttribute("error", "El producto no está disponible");

				return "redirect:/catalogo";
			}

			if (producto.getStock() <= 0) {

				redirectAttributes.addFlashAttribute("error", "El producto no tiene stock disponible");

				return "redirect:/catalogo";
			}

			List<CarritoItemDto> carrito = obtenerCarrito(session);

			CarritoItemDto existente = carrito.stream().filter(item -> item.getIdProducto() == idProducto).findFirst()
					.orElse(null);

			int cantidadActual = existente != null ? existente.getCantidad() : 0;

			if (cantidadActual + cantidad > producto.getStock()) {

				redirectAttributes.addFlashAttribute("error", "La cantidad supera el stock disponible");

				return "redirect:/catalogo";
			}

			if (existente != null) {

				existente.setCantidad(existente.getCantidad() + cantidad);

			} else {

				CarritoItemDto item = new CarritoItemDto();

				item.setIdProducto(producto.getIdProducto());

				item.setNombre(producto.getNombre());

				item.setImagenUrl(producto.getImagenUrl());

				item.setPrecio(producto.getPrecio());

				item.setCantidad(cantidad);

				carrito.add(item);
			}

			session.setAttribute("carrito", carrito);

			actualizarCantidadCarrito(session, carrito);

			redirectAttributes.addFlashAttribute("mensaje", "Producto agregado al carrito");

			return "redirect:/carrito";

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("error",
					e.getMessage() != null ? e.getMessage() : "No fue posible agregar el producto");

			return "redirect:/catalogo";
		}
	}

	@PostMapping("/actualizar/{idProducto}")
	public String actualizarCantidad(@PathVariable int idProducto, @RequestParam int cantidad, HttpSession session,
			RedirectAttributes redirectAttributes) {

		if (!esCliente(session)) {
			return "redirect:/auth/login";
		}

		List<CarritoItemDto> carrito = obtenerCarrito(session);

		if (cantidad <= 0) {

			carrito.removeIf(item -> item.getIdProducto() == idProducto);

		} else {

			ProductoResponseDto producto = productoService.buscarPorId(idProducto);

			if (producto == null) {

				redirectAttributes.addFlashAttribute("error", "Producto no encontrado");

				return "redirect:/carrito";
			}

			if (!producto.isDisponible()) {

				redirectAttributes.addFlashAttribute("error", "El producto ya no está disponible");

				return "redirect:/carrito";
			}

			if (cantidad > producto.getStock()) {

				redirectAttributes.addFlashAttribute("error", "La cantidad supera el stock disponible");

				return "redirect:/carrito";
			}

			carrito.stream().filter(item -> item.getIdProducto() == idProducto).findFirst()
					.ifPresent(item -> item.setCantidad(cantidad));
		}

		session.setAttribute("carrito", carrito);

		actualizarCantidadCarrito(session, carrito);

		return "redirect:/carrito";
	}

	@GetMapping("/eliminar/{idProducto}")
	public String eliminarProducto(@PathVariable int idProducto, HttpSession session) {

		if (!esCliente(session)) {
			return "redirect:/auth/login";
		}

		List<CarritoItemDto> carrito = obtenerCarrito(session);

		carrito.removeIf(item -> item.getIdProducto() == idProducto);

		session.setAttribute("carrito", carrito);

		actualizarCantidadCarrito(session, carrito);

		return "redirect:/carrito";
	}

	@GetMapping("/vaciar")
	public String vaciarCarrito(HttpSession session) {

		if (!esCliente(session)) {
			return "redirect:/auth/login";
		}

		session.removeAttribute("carrito");

		session.removeAttribute("cantidadCarrito");

		return "redirect:/carrito";
	}

	@PostMapping("/generar")
	public String generarPedido(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date fechaEntrega,

			@RequestParam String direccionEntrega,

			@RequestParam(value = "observacion", required = false) String observacion,

			@RequestParam(value = "comprobante", required = false) MultipartFile comprobante,

			HttpSession session, RedirectAttributes redirectAttributes) {

		if (!esCliente(session)) {
			return "redirect:/auth/login";
		}

		try {

			Integer idUsuario = (Integer) session.getAttribute("idUsuario");

			if (idUsuario == null) {
				return "redirect:/auth/login";
			}

			List<CarritoItemDto> carrito = obtenerCarrito(session);

			if (carrito.isEmpty()) {

				redirectAttributes.addFlashAttribute("error", "El carrito está vacío");

				return "redirect:/carrito";
			}

			if (fechaEntrega == null || fechaEntrega.before(new Date())) {

				redirectAttributes.addFlashAttribute("error", "Seleccione una fecha de entrega válida");

				return "redirect:/carrito";
			}

			if (direccionEntrega == null || direccionEntrega.trim().isEmpty()) {

				redirectAttributes.addFlashAttribute("error", "La dirección de entrega es obligatoria");

				return "redirect:/carrito";
			}

			PedidoRequestDto pedido = new PedidoRequestDto();

			pedido.setIdUsuario(idUsuario);

			pedido.setFechaEntrega(fechaEntrega);

			pedido.setDireccionEntrega(direccionEntrega.trim());

			pedido.setObservacion(observacion != null && !observacion.trim().isEmpty() ? observacion.trim() : null);

			List<DetallePedidoRequestDto> detalles = carrito.stream().map(item -> {

				DetallePedidoRequestDto detalle = new DetallePedidoRequestDto();

				detalle.setIdProducto(item.getIdProducto());

				detalle.setCantidad(item.getCantidad());

				return detalle;
			}).toList();

			pedido.setDetalles(detalles);
			
			
			PedidoResponseDto pedidoGenerado = pedidoService.guardarpedido(pedido);

			if (pedidoGenerado == null || pedidoGenerado.getIdPedido() <= 0) {

				throw new RuntimeException("No se obtuvo el número del pedido generado");
			}

			if (comprobante != null && !comprobante.isEmpty()) {

				validarComprobante(comprobante);

				pedidoService.subirComprobante(pedidoGenerado.getIdPedido(), comprobante, pedidoGenerado.getTotal());
			}

			session.removeAttribute("carrito");

			session.removeAttribute("cantidadCarrito");

			redirectAttributes.addFlashAttribute(
			        "pedidoConfirmado",
			        pedidoGenerado
			);

			redirectAttributes.addFlashAttribute(
			        "mensaje",
			        "Tu pedido fue generado correctamente"
			);

			return "redirect:/pedidos";

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("error",
					e.getMessage() != null && !e.getMessage().isBlank() ? e.getMessage()
							: "No fue posible generar el pedido");

			return "redirect:/carrito";
		}
	}

	private void validarComprobante(MultipartFile comprobante) {

		String tipo = comprobante.getContentType();

		boolean permitido = "image/jpeg".equalsIgnoreCase(tipo) || "image/png".equalsIgnoreCase(tipo)
				|| "image/webp".equalsIgnoreCase(tipo);

		if (!permitido) {

			throw new RuntimeException("El comprobante debe ser una imagen JPG, PNG o WEBP");
		}

		long limite = 5L * 1024L * 1024L;

		if (comprobante.getSize() > limite) {

			throw new RuntimeException("El comprobante no puede superar los 5 MB");
		}
	}
}