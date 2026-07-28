package com.uisrael.pedidosweb.controller;

import java.util.List;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uisrael.pedidosweb.model.dto.request.PedidoRequestDto;
import com.uisrael.pedidosweb.model.dto.response.ComprobantePagoResponseDto;
import com.uisrael.pedidosweb.model.dto.response.PedidoResponseDto;
import com.uisrael.pedidosweb.services.IComprobantePagoService;
import com.uisrael.pedidosweb.services.IPedidoService;

import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/pedidos") // url
public class PedidoController {

	private IPedidoService serviciopedido;
	private final IComprobantePagoService comprobanteService;

	public PedidoController(IPedidoService serviciopedido, IComprobantePagoService comprobanteService) {
		super();
		this.serviciopedido = serviciopedido;
		this.comprobanteService = comprobanteService;
	}

	@GetMapping
	public String leerPagina(Model model, HttpSession session, RedirectAttributes redirectAttributes) {

		String rol = (String) session.getAttribute("rolUsuario");

		Integer idUsuario = (Integer) session.getAttribute("idUsuario");

		if (rol == null || idUsuario == null) {
			return "redirect:/auth/login";
		}

		try {

			List<PedidoResponseDto> resultados;

			if ("ADMINISTRADOR".equalsIgnoreCase(rol)) {

				resultados = serviciopedido.listarpedido();

			} else if ("CLIENTE".equalsIgnoreCase(rol)) {

				resultados = serviciopedido.listarPorUsuario(idUsuario);

			} else {

				return "redirect:/auth/login";
			}

			model.addAttribute("listapedido", resultados);

			return "pedidos/listarpedidos";

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("error", "No fue posible consultar los pedidos");

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
		serviciopedido.guardarpedido(pedido);
		return "redirect:/pedidos";
	}

	@GetMapping("/detalle/{idPedido}")
	public String verDetalle(@PathVariable int idPedido, Model model, HttpSession session,
			RedirectAttributes redirectAttributes) {

		try {

			PedidoResponseDto pedido = serviciopedido.buscarPorId(idPedido);

			if (pedido == null) {

				redirectAttributes.addFlashAttribute("error", "Pedido no encontrado");

				return "redirect:/pedidos";
			}

			String rol = (String) session.getAttribute("rolUsuario");

			Integer idUsuario = (Integer) session.getAttribute("idUsuario");

			if ("CLIENTE".equalsIgnoreCase(rol) && (idUsuario == null || pedido.getIdUsuario() != idUsuario)) {

				redirectAttributes.addFlashAttribute("error", "No está autorizado para consultar este pedido");

				return "redirect:/pedidos";
			}

			ComprobantePagoResponseDto comprobante = comprobanteService.buscarPorPedido(idPedido);

			model.addAttribute("pedido", pedido);

			model.addAttribute("comprobante", comprobante);

			return "pedidos/detallepedido";

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("error",
					e.getMessage() != null ? e.getMessage() : "No fue posible consultar el pedido");

			return "redirect:/pedidos";
		}
	}
	
	@PostMapping("/{idPedido}/comprobante")
	public String subirComprobante(
	        @PathVariable int idPedido,

	        @RequestParam("archivo")
	        MultipartFile archivo,

	        @RequestParam("monto")
	        Double monto,

	        @RequestParam(
	                value = "tipoPago",
	                defaultValue = "ABONO_INICIAL"
	        )
	        String tipoPago,

	        @RequestParam(
	                value = "observacion",
	                required = false
	        )
	        String observacion,

	        HttpSession session,
	        RedirectAttributes redirectAttributes) {

	    try {

	        String rol =
	                (String) session.getAttribute(
	                        "rolUsuario"
	                );

	        boolean autorizado =
	                "ADMINISTRADOR".equalsIgnoreCase(rol)
	                || "CLIENTE".equalsIgnoreCase(rol);

	        if (!autorizado) {

	            redirectAttributes.addFlashAttribute(
	                    "error",
	                    "No está autorizado para subir comprobantes"
	            );

	            return "redirect:/pedidos/detalle/"
	                    + idPedido;
	        }

	        if (archivo == null || archivo.isEmpty()) {

	            redirectAttributes.addFlashAttribute(
	                    "error",
	                    "Seleccione una imagen"
	            );

	            return "redirect:/pedidos/detalle/"
	                    + idPedido;
	        }

	        if (monto == null || monto <= 0) {

	            redirectAttributes.addFlashAttribute(
	                    "error",
	                    "Ingrese un monto válido"
	            );

	            return "redirect:/pedidos/detalle/"
	                    + idPedido;
	        }

	        validarArchivo(
	                archivo
	        );

	        comprobanteService.subirComprobante(
	                idPedido,
	                archivo,
	                tipoPago,
	                monto,
	                observacion
	        );

	        redirectAttributes.addFlashAttribute(
	                "mensaje",
	                "Comprobante guardado correctamente"
	        );

	    } catch (Exception e) {

	        redirectAttributes.addFlashAttribute(
	                "error",
	                e.getMessage() != null
	                        ? e.getMessage()
	                        : "No fue posible guardar el comprobante"
	        );
	    }

	    return "redirect:/pedidos/detalle/"
	            + idPedido;
	}
	
	private void validarArchivo(
	        MultipartFile archivo) {

	    String tipo =
	            archivo.getContentType();

	    boolean permitido =
	            "image/jpeg".equalsIgnoreCase(tipo)
	            || "image/png".equalsIgnoreCase(tipo)
	            || "image/webp".equalsIgnoreCase(tipo);

	    if (!permitido) {

	        throw new RuntimeException(
	                "El comprobante debe ser JPG, PNG o WEBP"
	        );
	    }

	    long limite =
	            5L * 1024L * 1024L;

	    if (archivo.getSize() > limite) {

	        throw new RuntimeException(
	                "La imagen no puede superar los 5 MB"
	        );
	    }
	}

}
