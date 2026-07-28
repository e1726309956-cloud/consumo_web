package com.uisrael.pedidosweb.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uisrael.pedidosweb.model.dto.request.CambiarEstadoComprobanteRequestDto;
import com.uisrael.pedidosweb.model.dto.response.ComprobantePagoResponseDto;
import com.uisrael.pedidosweb.model.dto.response.EntregaResponseDto;
import com.uisrael.pedidosweb.model.dto.response.PedidoResponseDto;
import com.uisrael.pedidosweb.services.IComprobantePagoService;
import com.uisrael.pedidosweb.services.IEntregaService;
import com.uisrael.pedidosweb.services.IPedidoService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/entregas")
public class EntregaController {

	/*
	 * Estados exclusivos del módulo ENTREGA.
	 */
	private static final int ID_ENTREGA_PENDIENTE = 13;
	private static final int ID_ENTREGA_PROGRAMADA = 14;
	private static final int ID_ENTREGA_EN_RUTA = 15;
	private static final int ID_ENTREGA_ENTREGADA = 16;
	private static final int ID_ENTREGA_NO_ENTREGADA = 17;
	private static final int ID_ENTREGA_REPROGRAMADA = 18;
	private static final int ID_ENTREGA_CANCELADA = 19;

	private static final int ID_COMPROBANTE_APROBADO = 2;
	private static final int ID_COMPROBANTE_RECHAZADO = 3;

	private final IEntregaService entregaService;
	private final IPedidoService pedidoService;
	private final IComprobantePagoService comprobanteService;

	public EntregaController(IEntregaService entregaService, IPedidoService pedidoService,
			IComprobantePagoService comprobanteService) {
		super();
		this.entregaService = entregaService;
		this.pedidoService = pedidoService;
		this.comprobanteService = comprobanteService;
	}

	@GetMapping
	public String listar(Model model, HttpSession session, RedirectAttributes redirectAttributes) {

		String rol = (String) session.getAttribute("rolUsuario");

		if (!esVendedorOAdministrador(rol)) {
			return "redirect:/auth/login";
		}

		try {

			List<EntregaResponseDto> entregas = entregaService.listarTodos();

			model.addAttribute("listaEntregas", entregas);

			return "entregas/listarentregas";

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("error", mensajeExcepcion(e, "No fue posible consultar las entregas"));

			return "redirect:/pedidos";
		}
	}

	@GetMapping("/detalle/{idEntrega}")
	public String detalle(@PathVariable int idEntrega, Model model, HttpSession session,
			RedirectAttributes redirectAttributes) {

		String rol = (String) session.getAttribute("rolUsuario");

		if (!esVendedorOAdministrador(rol)) {
			return "redirect:/auth/login";
		}

		try {

			EntregaResponseDto entrega = entregaService.buscarPorId(idEntrega);

			if (entrega == null) {

				redirectAttributes.addFlashAttribute("error", "Entrega no encontrada");

				return "redirect:/entregas";
			}

			PedidoResponseDto pedido = pedidoService.buscarPorId(entrega.getIdPedido());

			if (pedido == null) {

				redirectAttributes.addFlashAttribute("error", "No se encontró el pedido asociado");

				return "redirect:/entregas";
			}

			List<ComprobantePagoResponseDto> comprobantes = comprobanteService.listarPorPedido(entrega.getIdPedido());

			if (comprobantes == null) {
				comprobantes = List.of();
			}

			/*
			 * Total registrado: excluimos los comprobantes rechazados.
			 */
			double totalRegistrado = comprobantes.stream().filter(c -> c.getMonto() != null)
					.filter(c -> c.getIdEstado() != ID_COMPROBANTE_RECHAZADO)
					.mapToDouble(ComprobantePagoResponseDto::getMonto).sum();

			/*
			 * Total aprobado: solamente comprobantes aprobados.
			 */
			double totalAprobado = comprobantes.stream().filter(c -> c.getIdEstado() == ID_COMPROBANTE_APROBADO)
					.filter(c -> c.getMonto() != null).mapToDouble(ComprobantePagoResponseDto::getMonto).sum();

			double totalPedido = pedido.getTotal() != null ? pedido.getTotal() : 0.0;

			double saldoPendiente = Math.max(totalPedido - totalAprobado, 0.0);

			boolean pagoCompleto = totalPedido > 0 && saldoPendiente <= 0.001;

			model.addAttribute("entrega", entrega);

			model.addAttribute("pedido", pedido);

			model.addAttribute("comprobantes", comprobantes);

			model.addAttribute("totalPedido", totalPedido);

			model.addAttribute("totalRegistrado", totalRegistrado);

			model.addAttribute("totalAprobado", totalAprobado);

			model.addAttribute("saldoPendiente", saldoPendiente);

			model.addAttribute("pagoCompleto", pagoCompleto);

			return "entregas/detalleentrega";

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("error", mensajeExcepcion(e, "No fue posible consultar la entrega"));

			return "redirect:/entregas";
		}
	}

	/*
	 * Programar entrega.
	 */
	@PostMapping("/{idEntrega}/programar")
	public String programar(@PathVariable int idEntrega,

			@RequestParam(value = "observacion", required = false) String observacion,

			HttpSession session, RedirectAttributes redirectAttributes) {

		return cambiarEstado(idEntrega, ID_ENTREGA_PROGRAMADA, observacion, "Entrega programada correctamente", session,
				redirectAttributes);
	}

	/*
	 * Marcar entrega en ruta.
	 */
	@PostMapping("/{idEntrega}/en-ruta")
	public String enRuta(@PathVariable int idEntrega,

			@RequestParam(value = "observacion", required = false) String observacion,

			HttpSession session, RedirectAttributes redirectAttributes) {

		return cambiarEstado(idEntrega, ID_ENTREGA_EN_RUTA, observacion, "La entrega fue marcada como en ruta", session,
				redirectAttributes);
	}

	/*
	 * Reprogramar entrega.
	 */
	@PostMapping("/{idEntrega}/reprogramar")
	public String reprogramar(@PathVariable int idEntrega,

			@RequestParam("observacion") String observacion,

			HttpSession session, RedirectAttributes redirectAttributes) {

		if (observacion == null || observacion.isBlank()) {

			redirectAttributes.addFlashAttribute("error", "Debe indicar el motivo de la reprogramación");

			return "redirect:/entregas/detalle/" + idEntrega;
		}

		return cambiarEstado(idEntrega, ID_ENTREGA_REPROGRAMADA, observacion, "La entrega fue reprogramada", session,
				redirectAttributes);
	}

	/*
	 * Marcar como no entregada.
	 */
	@PostMapping("/{idEntrega}/no-entregada")
	public String noEntregada(@PathVariable int idEntrega,

			@RequestParam("observacion") String observacion,

			HttpSession session, RedirectAttributes redirectAttributes) {

		if (observacion == null || observacion.isBlank()) {

			redirectAttributes.addFlashAttribute("error", "Debe indicar por qué no se pudo realizar la entrega");

			return "redirect:/entregas/detalle/" + idEntrega;
		}

		return cambiarEstado(idEntrega, ID_ENTREGA_NO_ENTREGADA, observacion,
				"La entrega fue marcada como no entregada", session, redirectAttributes);
	}

	/*
	 * Cancelar entrega.
	 */
	@PostMapping("/{idEntrega}/cancelar")
	public String cancelar(@PathVariable int idEntrega,

			@RequestParam("observacion") String observacion,

			HttpSession session, RedirectAttributes redirectAttributes) {

		if (observacion == null || observacion.isBlank()) {

			redirectAttributes.addFlashAttribute("error", "Debe indicar el motivo de la cancelación");

			return "redirect:/entregas/detalle/" + idEntrega;
		}

		return cambiarEstado(idEntrega, ID_ENTREGA_CANCELADA, observacion, "La entrega fue cancelada", session,
				redirectAttributes);
	}

	private String cambiarEstado(int idEntrega, int idEstado, String observacion, String mensajeExito,
			HttpSession session, RedirectAttributes redirectAttributes) {

		try {

			String rol = (String) session.getAttribute("rolUsuario");

			if (!esVendedorOAdministrador(rol)) {

				redirectAttributes.addFlashAttribute("error",
						"No está autorizado para cambiar el estado de la entrega");

				return "redirect:/entregas";
			}

			entregaService.cambiarEstado(idEntrega, idEstado, observacion != null ? observacion.trim() : null);

			redirectAttributes.addFlashAttribute("mensaje", mensajeExito);

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("error",
					mensajeExcepcion(e, "No fue posible cambiar el estado de la entrega"));
		}

		return "redirect:/entregas/detalle/" + idEntrega;
	}

	private boolean esVendedorOAdministrador(String rol) {

		return "ADMINISTRADOR".equalsIgnoreCase(rol) || "VENDEDOR".equalsIgnoreCase(rol);
	}

	private String mensajeExcepcion(Exception e, String mensajeDefecto) {

		return e.getMessage() != null && !e.getMessage().isBlank() ? e.getMessage() : mensajeDefecto;
	}

	@PostMapping("/{idEntrega}/comprobante")
	public String subirComprobanteFinal(@PathVariable int idEntrega,

			@RequestParam("archivo") MultipartFile archivo,

			@RequestParam("monto") Double monto,

			@RequestParam(value = "observacion", required = false) String observacion,

			HttpSession session, RedirectAttributes redirectAttributes) {

		try {

			String rol = (String) session.getAttribute("rolUsuario");

			if (!esVendedorOAdministrador(rol)) {

				redirectAttributes.addFlashAttribute("error", "No está autorizado para registrar pagos");

				return "redirect:/entregas";
			}

			EntregaResponseDto entrega = entregaService.buscarPorId(idEntrega);

			if (entrega == null) {
				throw new RuntimeException("Entrega no encontrada");
			}

			PedidoResponseDto pedido = pedidoService.buscarPorId(entrega.getIdPedido());

			List<ComprobantePagoResponseDto> comprobantes = comprobanteService.listarPorPedido(entrega.getIdPedido());

			if (comprobantes == null) {
				comprobantes = List.of();
			}

			double totalAprobado = comprobantes.stream().filter(c -> c.getIdEstado() == ID_COMPROBANTE_APROBADO)
					.filter(c -> c.getMonto() != null).mapToDouble(ComprobantePagoResponseDto::getMonto).sum();

			double totalPedido = pedido.getTotal() != null ? pedido.getTotal() : 0.0;

			double saldoPendiente = Math.max(totalPedido - totalAprobado, 0.0);

			if (saldoPendiente <= 0.001) {

				redirectAttributes.addFlashAttribute("error", "El pedido ya se encuentra pagado completamente");

				return "redirect:/entregas/detalle/" + idEntrega;
			}

			if (archivo == null || archivo.isEmpty()) {

				redirectAttributes.addFlashAttribute("error", "Debe seleccionar el comprobante");

				return "redirect:/entregas/detalle/" + idEntrega;
			}

			if (monto == null || monto <= 0) {

				redirectAttributes.addFlashAttribute("error", "Ingrese un monto válido");

				return "redirect:/entregas/detalle/" + idEntrega;
			}

			if (monto > saldoPendiente + 0.001) {

				redirectAttributes.addFlashAttribute("error",
						"El monto no puede superar el saldo pendiente de $" + String.format("%.2f", saldoPendiente));

				return "redirect:/entregas/detalle/" + idEntrega;
			}

			validarArchivo(archivo);

			comprobanteService.subirComprobante(entrega.getIdPedido(), archivo, "PAGO_FINAL", monto, observacion);

			redirectAttributes.addFlashAttribute("mensaje",
					"Comprobante del pago restante registrado. " + "Debe aprobarse antes de finalizar la entrega.");

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("error",
					mensajeExcepcion(e, "No fue posible registrar el comprobante"));
		}

		return "redirect:/entregas/detalle/" + idEntrega;
	}

	private void validarArchivo(MultipartFile archivo) {

		String tipo = archivo.getContentType();

		boolean permitido = "image/jpeg".equalsIgnoreCase(tipo) || "image/png".equalsIgnoreCase(tipo)
				|| "image/webp".equalsIgnoreCase(tipo);

		if (!permitido) {

			throw new RuntimeException("El archivo debe ser JPG, PNG o WEBP");
		}

		long limite = 5L * 1024L * 1024L;

		if (archivo.getSize() > limite) {

			throw new RuntimeException("La imagen no puede superar los 5 MB");
		}
	}

	@PostMapping("/{idEntrega}/comprobante/{idComprobante}/aprobar")
	public String aprobarComprobanteFinal(@PathVariable int idEntrega, @PathVariable int idComprobante,
			HttpSession session, RedirectAttributes redirectAttributes) {

		try {

			String rol = (String) session.getAttribute("rolUsuario");

			if (!esVendedorOAdministrador(rol)) {

				redirectAttributes.addFlashAttribute("error", "No está autorizado para aprobar pagos");

				return "redirect:/entregas";
			}

			CambiarEstadoComprobanteRequestDto request = new CambiarEstadoComprobanteRequestDto();

			request.setIdEstado(ID_COMPROBANTE_APROBADO);

			request.setObservacion("Pago final aprobado durante la gestión de entrega");

			comprobanteService.cambiarEstado(idComprobante, request);

			redirectAttributes.addFlashAttribute("mensaje", "Comprobante aprobado correctamente");

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("error", mensajeExcepcion(e, "No fue posible aprobar el comprobante"));
		}

		return "redirect:/entregas/detalle/" + idEntrega;
	}

	@PostMapping("/{idEntrega}/finalizar")
	public String finalizarEntrega(@PathVariable int idEntrega,

			@RequestParam("evidencia") MultipartFile evidencia,

			@RequestParam("recibidoPor") String recibidoPor,

			@RequestParam(value = "observacion", required = false) String observacion,

			HttpSession session, RedirectAttributes redirectAttributes) {

		try {

			String rol = (String) session.getAttribute("rolUsuario");

			if (!esVendedorOAdministrador(rol)) {

				redirectAttributes.addFlashAttribute("error", "No está autorizado para finalizar entregas");

				return "redirect:/entregas";
			}

			if (recibidoPor == null || recibidoPor.isBlank()) {

				redirectAttributes.addFlashAttribute("error", "Debe indicar quién recibió el pedido");

				return "redirect:/entregas/detalle/" + idEntrega;
			}

			if (evidencia == null || evidencia.isEmpty()) {

				redirectAttributes.addFlashAttribute("error", "Debe adjuntar la evidencia de entrega");

				return "redirect:/entregas/detalle/" + idEntrega;
			}

			validarArchivo(evidencia);

			entregaService.finalizarEntrega(idEntrega, evidencia, recibidoPor.trim(), observacion);

			redirectAttributes.addFlashAttribute("mensaje", "Entrega finalizada correctamente");

			return "redirect:/entregas";

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("error", mensajeExcepcion(e, "No fue posible finalizar la entrega"));

			return "redirect:/entregas/detalle/" + idEntrega;
		}
	}
}