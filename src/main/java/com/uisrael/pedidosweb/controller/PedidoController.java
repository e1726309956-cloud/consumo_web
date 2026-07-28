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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uisrael.pedidosweb.model.dto.request.CambiarEstadoComprobanteRequestDto;
import com.uisrael.pedidosweb.model.dto.request.CambiarEstadoPedidoRequestDto;
import com.uisrael.pedidosweb.model.dto.request.PedidoRequestDto;
import com.uisrael.pedidosweb.model.dto.response.ComprobantePagoResponseDto;
import com.uisrael.pedidosweb.model.dto.response.PedidoResponseDto;
import com.uisrael.pedidosweb.services.IComprobantePagoService;
import com.uisrael.pedidosweb.services.IPedidoService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

	/*
	 * Ajusta estos ID según tu tabla estados_generales.
	 */
	private static final int ID_PEDIDO_PENDIENTE = 1;
	private static final int ID_PEDIDO_CONFIRMADO = 6;
	private static final int ID_PEDIDO_CANCELADO = 1;

	/*
	 * Según los estados que mostraste: 2 = APROBADO / COMPROBANTE 3 = RECHAZADO /
	 * COMPROBANTE 4 = PENDIENTE / COMPROBANTE
	 */
	private static final int ID_COMPROBANTE_APROBADO = 2;
	private static final int ID_COMPROBANTE_RECHAZADO = 3;
	private static final int ID_COMPROBANTE_PENDIENTE = 4;

	private final IPedidoService servicioPedido;
	private final IComprobantePagoService comprobanteService;

	public PedidoController(IPedidoService servicioPedido, IComprobantePagoService comprobanteService) {

		this.servicioPedido = servicioPedido;
		this.comprobanteService = comprobanteService;
	}

	/*
	 * Listar pedidos. El administrador ve todos. El cliente ve únicamente los
	 * propios.
	 */
	@GetMapping
	public String leerPagina(Model model, HttpSession session, RedirectAttributes redirectAttributes) {

		String rol = (String) session.getAttribute("rolUsuario");

		Integer idUsuario = (Integer) session.getAttribute("idUsuario");

		if (rol == null || idUsuario == null) {
			return "redirect:/auth/login";
		}

		try {

			List<PedidoResponseDto> resultados;

			if ("ADMINISTRADOR".equalsIgnoreCase(rol) || "VENDEDOR".equalsIgnoreCase(rol)) {

				resultados = servicioPedido.listarpedido();

			} else if ("CLIENTE".equalsIgnoreCase(rol)) {

				resultados = servicioPedido.listarPorUsuario(idUsuario);

			} else {

				return "redirect:/auth/login";
			}

			model.addAttribute("listapedido", resultados);

			return "pedidos/listarpedidos";

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("error",
					e.getMessage() != null ? e.getMessage() : "No fue posible consultar los pedidos");

			return "redirect:/catalogo";
		}
	}

	@GetMapping("/nuevo")
	public String crearPedido(Model model) {

		model.addAttribute("pedidos", new PedidoRequestDto());

		return "pedidos/crearpedido";
	}

	@PostMapping("/guardar")
	public String guardarPedido(@ModelAttribute PedidoRequestDto pedido) {

		servicioPedido.guardarpedido(pedido);

		return "redirect:/pedidos";
	}

	/*
	 * Detalle del pedido y resumen de pagos.
	 */
	@GetMapping("/detalle/{idPedido}")
	public String verDetalle(
	        @PathVariable int idPedido,
	        Model model,
	        HttpSession session,
	        RedirectAttributes redirectAttributes) {

	    try {

	        PedidoResponseDto pedido =
	                servicioPedido.buscarPorId(idPedido);

	        if (pedido == null) {

	            redirectAttributes.addFlashAttribute(
	                    "error",
	                    "Pedido no encontrado"
	            );

	            return "redirect:/pedidos";
	        }

	        String rol =
	                (String) session.getAttribute(
	                        "rolUsuario"
	                );

	        Integer idUsuario =
	                (Integer) session.getAttribute(
	                        "idUsuario"
	                );

	        /*
	         * El cliente solo puede consultar
	         * los pedidos que le pertenecen.
	         */
	        if ("CLIENTE".equalsIgnoreCase(rol)
	                && (
	                    idUsuario == null
	                    || pedido.getIdUsuario() != idUsuario
	                )) {

	            redirectAttributes.addFlashAttribute(
	                    "error",
	                    "No está autorizado para consultar este pedido"
	            );

	            return "redirect:/pedidos";
	        }

	        /*
	         * Consultamos todos los comprobantes.
	         */
	        List<ComprobantePagoResponseDto> comprobantes =
	                comprobanteService.listarPorPedido(
	                        idPedido
	                );

	        if (comprobantes == null) {
	            comprobantes = List.of();
	        }

	        /*
	         * Total registrado.
	         */
	        double totalRegistrado =
	                comprobantes.stream()
	                        .filter(c -> c.getMonto() != null)
	                        .mapToDouble(
	                                ComprobantePagoResponseDto::getMonto
	                        )
	                        .sum();

	        /*
	         * Total aprobado.
	         */
	        double totalAprobado =
	                comprobantes.stream()
	                        .filter(c ->
	                                c.getIdEstado()
	                                        == ID_COMPROBANTE_APROBADO
	                        )
	                        .filter(c -> c.getMonto() != null)
	                        .mapToDouble(
	                                ComprobantePagoResponseDto::getMonto
	                        )
	                        .sum();

	        double totalPedido =
	                pedido.getTotal() != null
	                        ? pedido.getTotal()
	                        : 0.0;

	        double abonoRequerido =
	                totalPedido * 0.50;

	        double saldoPendiente =
	                Math.max(
	                        totalPedido - totalAprobado,
	                        0.0
	                );

	        boolean cumpleAbono =
	                totalPedido > 0
	                && totalAprobado >= abonoRequerido;

	        /*
	         * Faltaba esta variable.
	         */
	        boolean pagoCompleto =
	                totalPedido > 0
	                && saldoPendiente <= 0.001;

	        String nombreEstado =
	                pedido.getNombreEstado() != null
	                        ? pedido.getNombreEstado().trim()
	                        : "";

	        boolean pedidoCerrado =
	                "CANCELADO".equalsIgnoreCase(
	                        nombreEstado
	                )
	                || "FINALIZADO".equalsIgnoreCase(
	                        nombreEstado
	                );

	        model.addAttribute(
	                "pedido",
	                pedido
	        );

	        model.addAttribute(
	                "comprobantes",
	                comprobantes
	        );

	        model.addAttribute(
	                "totalPedido",
	                totalPedido
	        );

	        model.addAttribute(
	                "totalRegistrado",
	                totalRegistrado
	        );

	        model.addAttribute(
	                "totalAprobado",
	                totalAprobado
	        );

	        model.addAttribute(
	                "saldoPendiente",
	                saldoPendiente
	        );

	        model.addAttribute(
	                "abonoRequerido",
	                abonoRequerido
	        );

	        model.addAttribute(
	                "cumpleAbono",
	                cumpleAbono
	        );

	        model.addAttribute(
	                "pagoCompleto",
	                pagoCompleto
	        );

	        model.addAttribute(
	                "pedidoCerrado",
	                pedidoCerrado
	        );

	        return "pedidos/detallepedido";

	    } catch (Exception e) {

	        e.printStackTrace();

	        redirectAttributes.addFlashAttribute(
	                "error",
	                e.getMessage() != null
	                        && !e.getMessage().isBlank()
	                                ? e.getMessage()
	                                : "No fue posible consultar el pedido"
	        );

	        return "redirect:/pedidos";
	    }
	}
	/*
	 * Subir un nuevo comprobante. Cada carga debe insertar un registro nuevo.
	 */
	@PostMapping("/{idPedido}/comprobante")
	public String subirComprobante(@PathVariable int idPedido,

			@RequestParam("archivo") MultipartFile archivo,

			@RequestParam("monto") Double monto,

			@RequestParam(value = "tipoPago", defaultValue = "ABONO_INICIAL") String tipoPago,

			@RequestParam(value = "observacion", required = false) String observacion,

			HttpSession session, RedirectAttributes redirectAttributes) {

		try {

			String rol = (String) session.getAttribute("rolUsuario");

			boolean autorizado = "ADMINISTRADOR".equalsIgnoreCase(rol) || "VENDEDOR".equalsIgnoreCase(rol)
					|| "CLIENTE".equalsIgnoreCase(rol);

			if (!autorizado) {

				redirectAttributes.addFlashAttribute("error", "No está autorizado para subir comprobantes");

				return "redirect:/pedidos/detalle/" + idPedido;
			}

			if (archivo == null || archivo.isEmpty()) {

				redirectAttributes.addFlashAttribute("error", "Seleccione una imagen");

				return "redirect:/pedidos/detalle/" + idPedido;
			}

			if (monto == null || monto <= 0) {

				redirectAttributes.addFlashAttribute("error", "Ingrese un monto válido");

				return "redirect:/pedidos/detalle/" + idPedido;
			}

			validarArchivo(archivo);

			comprobanteService.subirComprobante(idPedido, archivo, tipoPago, monto, observacion);

			redirectAttributes.addFlashAttribute("mensaje", "Comprobante guardado correctamente");

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("error",
					e.getMessage() != null && !e.getMessage().isBlank() ? e.getMessage()
							: "No fue posible guardar el comprobante");
		}

		return "redirect:/pedidos/detalle/" + idPedido;
	}

	/*
	 * Marcar pedido como pendiente.
	 */
	@PostMapping("/{idPedido}/marcar-pendiente")
	public String marcarPendiente(@PathVariable int idPedido,

			@RequestParam("observacion") String observacion,

			HttpSession session, RedirectAttributes redirectAttributes) {

		try {

			String rol = (String) session.getAttribute("rolUsuario");

			Integer idUsuario = (Integer) session.getAttribute("idUsuario");

			boolean autorizado = "ADMINISTRADOR".equalsIgnoreCase(rol) || "VENDEDOR".equalsIgnoreCase(rol);

			if (!autorizado || idUsuario == null) {

				redirectAttributes.addFlashAttribute("error", "No está autorizado para cambiar el estado");

				return "redirect:/pedidos";
			}

			if (observacion == null || observacion.isBlank()) {

				redirectAttributes.addFlashAttribute("error", "Debe indicar qué información falta");

				return "redirect:/pedidos/detalle/" + idPedido;
			}

			CambiarEstadoPedidoRequestDto request = new CambiarEstadoPedidoRequestDto();

			request.setIdEstado(ID_PEDIDO_PENDIENTE);

			request.setIdUsuario(idUsuario);

			request.setObservacion(observacion.trim());

			servicioPedido.cambiarEstado(idPedido, request);

			redirectAttributes.addFlashAttribute("mensaje", "El pedido fue marcado como pendiente");

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("error",
					e.getMessage() != null && !e.getMessage().isBlank() ? e.getMessage()
							: "No fue posible cambiar el estado");
		}

		return "redirect:/pedidos/detalle/" + idPedido;
	}

	/*
	 * Cancelar pedido.
	 */
	/*
	 * Cancelar pedido.
	 */
	@PostMapping("/{idPedido}/cancelar")
	public String cancelarPedido(@PathVariable int idPedido,

			@RequestParam("observacion") String observacion,

			HttpSession session, RedirectAttributes redirectAttributes) {

		try {

			String rol = (String) session.getAttribute("rolUsuario");

			Integer idUsuario = (Integer) session.getAttribute("idUsuario");

			boolean autorizado = "ADMINISTRADOR".equalsIgnoreCase(rol) || "VENDEDOR".equalsIgnoreCase(rol);

			if (!autorizado || idUsuario == null) {

				redirectAttributes.addFlashAttribute("error", "No está autorizado para cancelar pedidos");

				return "redirect:/pedidos";
			}

			if (observacion == null || observacion.isBlank()) {

				redirectAttributes.addFlashAttribute("error", "Debe ingresar el motivo de la cancelación");

				return "redirect:/pedidos/detalle/" + idPedido;
			}

			PedidoResponseDto pedido = servicioPedido.buscarPorId(idPedido);

			if (pedido == null) {

				redirectAttributes.addFlashAttribute("error", "Pedido no encontrado");

				return "redirect:/pedidos";
			}

			String estadoActual = pedido.getNombreEstado() != null ? pedido.getNombreEstado().trim() : "";

			if ("CANCELADO".equalsIgnoreCase(estadoActual)) {

				redirectAttributes.addFlashAttribute("error", "El pedido ya se encuentra cancelado");

				return "redirect:/pedidos/detalle/" + idPedido;
			}

			if ("CONFIRMADO".equalsIgnoreCase(estadoActual)) {

				redirectAttributes.addFlashAttribute("error", "No se puede cancelar un pedido confirmado");

				return "redirect:/pedidos/detalle/" + idPedido;
			}

			CambiarEstadoPedidoRequestDto request = new CambiarEstadoPedidoRequestDto();

			request.setIdEstado(ID_PEDIDO_CANCELADO);

			request.setIdUsuario(idUsuario);

			request.setObservacion(observacion.trim());

			PedidoResponseDto pedidoCancelado = servicioPedido.cambiarEstado(idPedido, request);

			if (pedidoCancelado == null || pedidoCancelado.getIdEstado() != ID_PEDIDO_CANCELADO) {

				throw new RuntimeException("No fue posible actualizar el estado del pedido");
			}

			redirectAttributes.addFlashAttribute("mensaje", "El pedido fue cancelado correctamente");

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("error",
					e.getMessage() != null && !e.getMessage().isBlank() ? e.getMessage()
							: "No fue posible cancelar el pedido");
		}

		return "redirect:/pedidos/detalle/" + idPedido;
	}

	/*
	 * Validación básica del comprobante.
	 */
	private void validarArchivo(MultipartFile archivo) {

		String tipo = archivo.getContentType();

		boolean permitido = "image/jpeg".equalsIgnoreCase(tipo) || "image/png".equalsIgnoreCase(tipo)
				|| "image/webp".equalsIgnoreCase(tipo);

		if (!permitido) {

			throw new RuntimeException("El comprobante debe ser JPG, PNG o WEBP");
		}

		long limite = 5L * 1024L * 1024L;

		if (archivo.getSize() > limite) {

			throw new RuntimeException("La imagen no puede superar los 5 MB");
		}
	}

	@PostMapping("/{idPedido}/comprobante/{idComprobante}/aprobar")
	public String aprobarComprobante(@PathVariable int idPedido, @PathVariable int idComprobante,

			@RequestParam(value = "observacion", required = false) String observacion,

			HttpSession session, RedirectAttributes redirectAttributes) {

		try {

			String rol = (String) session.getAttribute("rolUsuario");

			if (!"ADMINISTRADOR".equalsIgnoreCase(rol) && !"VENDEDOR".equalsIgnoreCase(rol)) {

				redirectAttributes.addFlashAttribute("error", "No está autorizado para aprobar comprobantes");

				return "redirect:/pedidos/detalle/" + idPedido;
			}

			CambiarEstadoComprobanteRequestDto request = new CambiarEstadoComprobanteRequestDto();

			request.setIdEstado(2);

			request.setObservacion(observacion != null ? observacion.trim() : "Comprobante aprobado");

			comprobanteService.cambiarEstado(idComprobante, request);

			redirectAttributes.addFlashAttribute("mensaje", "Comprobante aprobado correctamente");

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("error",
					e.getMessage() != null ? e.getMessage() : "No fue posible aprobar el comprobante");
		}

		return "redirect:/pedidos/detalle/" + idPedido;
	}

	@PostMapping("/{idPedido}/comprobante/{idComprobante}/rechazar")
	public String rechazarComprobante(@PathVariable int idPedido, @PathVariable int idComprobante,

			@RequestParam("observacion") String observacion,

			HttpSession session, RedirectAttributes redirectAttributes) {

		try {

			String rol = (String) session.getAttribute("rolUsuario");

			if (!"ADMINISTRADOR".equalsIgnoreCase(rol) && !"VENDEDOR".equalsIgnoreCase(rol)) {

				redirectAttributes.addFlashAttribute("error", "No está autorizado para rechazar comprobantes");

				return "redirect:/pedidos/detalle/" + idPedido;
			}

			if (observacion == null || observacion.isBlank()) {

				redirectAttributes.addFlashAttribute("error", "Debe indicar el motivo del rechazo");

				return "redirect:/pedidos/detalle/" + idPedido;
			}

			CambiarEstadoComprobanteRequestDto request = new CambiarEstadoComprobanteRequestDto();

			request.setIdEstado(3);

			request.setObservacion(observacion.trim());

			comprobanteService.cambiarEstado(idComprobante, request);

			redirectAttributes.addFlashAttribute("mensaje", "Comprobante rechazado");

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("error",
					e.getMessage() != null ? e.getMessage() : "No fue posible rechazar el comprobante");
		}

		return "redirect:/pedidos/detalle/" + idPedido;
	}

	@PostMapping("/{idPedido}/aprobar")
	public String aprobarPedido(@PathVariable int idPedido, HttpSession session,
			RedirectAttributes redirectAttributes) {

		try {

			String rol = (String) session.getAttribute("rolUsuario");

			Integer idUsuario = (Integer) session.getAttribute("idUsuario");

			boolean autorizado = "ADMINISTRADOR".equalsIgnoreCase(rol) || "VENDEDOR".equalsIgnoreCase(rol);

			if (!autorizado || idUsuario == null) {

				redirectAttributes.addFlashAttribute("error", "No está autorizado para aprobar pedidos");

				return "redirect:/pedidos";
			}

			PedidoResponseDto pedido = servicioPedido.buscarPorId(idPedido);

			if (pedido == null) {

				redirectAttributes.addFlashAttribute("error", "Pedido no encontrado");

				return "redirect:/pedidos";
			}

			String estadoActual = pedido.getNombreEstado() != null ? pedido.getNombreEstado().trim() : "";

			if ("CONFIRMADO".equalsIgnoreCase(estadoActual)) {

				redirectAttributes.addFlashAttribute("advertencia", "El pedido ya se encuentra confirmado");

				return "redirect:/pedidos/detalle/" + idPedido;
			}

			if ("CANCELADO".equalsIgnoreCase(estadoActual)) {

				redirectAttributes.addFlashAttribute("error", "No se puede aprobar un pedido cancelado");

				return "redirect:/pedidos/detalle/" + idPedido;
			}

			List<ComprobantePagoResponseDto> comprobantes = comprobanteService.listarPorPedido(idPedido);

			if (comprobantes == null) {
				comprobantes = List.of();
			}

			double totalAprobado = comprobantes.stream().filter(c -> c.getIdEstado() == ID_COMPROBANTE_APROBADO)
					.filter(c -> c.getMonto() != null).mapToDouble(ComprobantePagoResponseDto::getMonto).sum();

			double totalPedido = pedido.getTotal() != null ? pedido.getTotal() : 0.0;

			double abonoRequerido = totalPedido * 0.50;

			if (totalAprobado < abonoRequerido) {

				redirectAttributes.addFlashAttribute("error",
						"No se puede aprobar el pedido. " + "Debe existir al menos un 50% aprobado. "
								+ "Abono requerido: $" + String.format("%.2f", abonoRequerido));

				return "redirect:/pedidos/detalle/" + idPedido;
			}

			CambiarEstadoPedidoRequestDto request = new CambiarEstadoPedidoRequestDto();

			request.setIdEstado(ID_PEDIDO_CONFIRMADO);

			request.setIdUsuario(idUsuario);

			request.setObservacion("Pedido confirmado con el abono mínimo aprobado");

			PedidoResponseDto actualizado = servicioPedido.cambiarEstado(idPedido, request);

			if (actualizado == null || actualizado.getIdEstado() != ID_PEDIDO_CONFIRMADO) {

				throw new RuntimeException("La API no confirmó el cambio de estado");
			}

			redirectAttributes.addFlashAttribute("mensaje",
					"Pedido aprobado correctamente. " + "La entrega fue generada.");

		} catch (Exception e) {

			e.printStackTrace();

			redirectAttributes.addFlashAttribute("error",
					e.getMessage() != null && !e.getMessage().isBlank() ? e.getMessage()
							: "No fue posible aprobar el pedido");
		}

		return "redirect:/pedidos/detalle/" + idPedido;
	}
}