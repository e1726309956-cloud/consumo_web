package com.uisrael.pedidosweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.uisrael.pedidosweb.presentacion.dto.RestablecerPasswordDto;
import com.uisrael.pedidosweb.services.IRecuperacionPasswordService;

@Controller
@RequestMapping("/auth")
public class RecuperacionPasswordController {

	private final IRecuperacionPasswordService recuperacionPasswordService;

	public RecuperacionPasswordController(IRecuperacionPasswordService recuperacionPasswordService) {
		this.recuperacionPasswordService = recuperacionPasswordService;
	}

	@GetMapping("/olvidar-contrasena")
	public String mostrarOlvidarContrasena() {
		return "auth/olvidar-contrasena";
	}

	@PostMapping("/olvidar-contrasena")
	public String solicitarRecuperacion(@RequestParam("correo") String correo, Model model) {

		model.addAttribute("correo", correo);

		if (correo == null || correo.isBlank()) {

			model.addAttribute("error", "Debe ingresar su correo electrónico");

			return "auth/olvidar-contrasena";
		}

		String respuesta = recuperacionPasswordService.solicitarRecuperacion(correo.trim());

		if (respuesta != null
				&& (respuesta.toLowerCase().contains("generó") || respuesta.toLowerCase().contains("genero")
						|| respuesta.toLowerCase().contains("enviado") || respuesta.toLowerCase().contains("enlace"))) {

			model.addAttribute("mensaje", "Se generó el enlace de recuperación correctamente");

		} else {

			model.addAttribute("error", respuesta != null ? respuesta : "No se pudo procesar la solicitud");
		}

		return "auth/olvidar-contrasena";
	}

	@GetMapping("/restablecer")
	public String mostrarRestablecerPassword(@RequestParam(value = "token", required = false) String token,
			Model model) {

		if (token == null || token.isBlank()) {
			model.addAttribute("tokenValido", false);
			model.addAttribute("error", "Token inválido.");
			return "auth/restablecer-contrasena";
		}

		boolean tokenValido = recuperacionPasswordService.validarToken(token);

		model.addAttribute("tokenValido", tokenValido);

		if (tokenValido) {
			RestablecerPasswordDto dto = new RestablecerPasswordDto();
			dto.setToken(token);
			model.addAttribute("restablecerPasswordDto", dto);
		} else {
			model.addAttribute("error", "El enlace expiró o ya fue utilizado.");
		}

		return "auth/restablecer-contrasena";
	}

	@PostMapping("/restablecer")
	public String restablecerPassword(RestablecerPasswordDto dto, Model model) {

		model.addAttribute("restablecerPasswordDto", dto);

		if (dto.getNuevaContrasena() == null || dto.getNuevaContrasena().length() < 6) {
			model.addAttribute("tokenValido", true);
			model.addAttribute("error", "La contraseña debe tener mínimo 6 caracteres.");
			return "auth/restablecer-contrasena";
		}

		if (!dto.getNuevaContrasena().equals(dto.getConfirmarContrasena())) {
			model.addAttribute("tokenValido", true);
			model.addAttribute("error", "Las contraseñas no coinciden.");
			return "auth/restablecer-contrasena";
		}

		String respuesta = recuperacionPasswordService.restablecerPassword(dto);

		if (respuesta != null && respuesta.toLowerCase().contains("actualiz")) {

			model.addAttribute("passwordActualizado", true);
			model.addAttribute("mensaje", respuesta);

		} else {

			model.addAttribute("tokenValido", true);
			model.addAttribute("error", respuesta);
		}

		return "auth/restablecer-contrasena";
	}
}