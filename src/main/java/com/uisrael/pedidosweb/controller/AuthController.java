package com.uisrael.pedidosweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uisrael.pedidosweb.model.dto.request.LoginRequestDto;
import com.uisrael.pedidosweb.model.dto.request.UsuarioRequestDto;
import com.uisrael.pedidosweb.model.dto.response.UsuarioResponseDto;
import com.uisrael.pedidosweb.services.IUsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {

	private final IUsuarioService usuarioService;

	public AuthController(IUsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	// Este método muestra el formulario
	@GetMapping("/login")
	public String mostrarLogin(Model model) {

		model.addAttribute("login", new LoginRequestDto());

		return "auth/login";
	}

	@PostMapping("/login")
	public String iniciarSesion(@ModelAttribute("login") LoginRequestDto login, HttpSession session,
			RedirectAttributes redirectAttributes) {

		try {

			UsuarioResponseDto usuario = usuarioService.iniciarSesion(login);

			if (usuario == null) {

				redirectAttributes.addFlashAttribute("error", "No fue posible iniciar sesión");

				return "redirect:/auth/login";
			}

			if (!"true".equalsIgnoreCase(usuario.getEstado())) {

				redirectAttributes.addFlashAttribute("error", "El usuario se encuentra inactivo");

				return "redirect:/auth/login";
			}

			if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {

				redirectAttributes.addFlashAttribute("error", "El usuario no tiene un perfil asignado");

				return "redirect:/auth/login";
			}

			String rol = usuario.getRoles().get(0).getNombre();

			session.setAttribute("usuarioSesion", usuario);

			session.setAttribute("idUsuario", usuario.getIdUsuario());

			session.setAttribute("nombreUsuario", usuario.getNombre());

			session.setAttribute("rolUsuario", rol);

			session.setAttribute("correoUsuario", usuario.getCorreo());

			if ("ADMINISTRADOR".equalsIgnoreCase(rol)) {

				redirectAttributes.addFlashAttribute("mensaje", "Bienvenido al panel administrativo");

				return "redirect:/producto";
			}

			if ("CLIENTE".equalsIgnoreCase(rol)) {

				redirectAttributes.addFlashAttribute("mensaje", "Bienvenido al catálogo");

				return "redirect:/catalogo";
			}

			session.invalidate();

			redirectAttributes.addFlashAttribute("error", "El perfil del usuario no está autorizado");

			return "redirect:/auth/login";

		} catch (WebClientResponseException e) {

			redirectAttributes.addFlashAttribute("error", "Correo o contraseña incorrectos");

			return "redirect:/auth/login";

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("error", "No fue posible iniciar sesión");

			return "redirect:/auth/login";
		}
	}

	@GetMapping("/logout")
	public String cerrarSesion(HttpSession session, RedirectAttributes redirectAttributes) {

		session.invalidate();

		redirectAttributes.addFlashAttribute("mensaje", "Sesión cerrada correctamente");

		return "redirect:/auth/login";
	}

	@GetMapping("/registro")
	public String mostrarRegistro(Model model) {

		model.addAttribute("usuario", new UsuarioRequestDto());

		return "auth/registro";
	}

	@PostMapping("/registro")
	public String registrarUsuario(@ModelAttribute("usuario") UsuarioRequestDto usuario,

			RedirectAttributes redirectAttributes) {

		try {

			usuarioService.registrarUsuario(usuario);

			redirectAttributes.addFlashAttribute("mensaje",
					"Registro realizado correctamente. Ya puedes iniciar sesión.");

			return "redirect:/auth/login";

		} catch (WebClientResponseException e) {

			String mensaje = "No fue posible registrar el usuario";

			if (e.getResponseBodyAsString().contains("correo")) {

				mensaje = "El correo ya se encuentra registrado";
			}

			if (e.getResponseBodyAsString().contains("cédula")) {

				mensaje = "La cédula ya se encuentra registrada";
			}

			redirectAttributes.addFlashAttribute("error", mensaje);

			return "redirect:/auth/registro";

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("error", "No fue posible realizar el registro");

			return "redirect:/auth/registro";
		}
	}

}