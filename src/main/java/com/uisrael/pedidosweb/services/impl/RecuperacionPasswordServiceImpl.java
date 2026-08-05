package com.uisrael.pedidosweb.services.impl;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.uisrael.pedidosweb.presentacion.dto.RestablecerPasswordDto;
import com.uisrael.pedidosweb.presentacion.dto.SolicitarRecuperacionDto;
import com.uisrael.pedidosweb.services.IRecuperacionPasswordService;

@Service
public class RecuperacionPasswordServiceImpl implements IRecuperacionPasswordService{

	private final WebClient webClient;

	public RecuperacionPasswordServiceImpl(WebClient.Builder webClientBuilder) {

		this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
	}

	@Override
	public String solicitarRecuperacion(String correo) {

		try {

			SolicitarRecuperacionDto dto = new SolicitarRecuperacionDto(correo);

			return webClient.post().uri("/api/usuario/recuperar-password").contentType(MediaType.APPLICATION_JSON)
					.bodyValue(dto).retrieve().bodyToMono(String.class).block();

		} catch (WebClientResponseException e) {

			if (e.getStatusCode().value() == 404) {
		        return "El correo ingresado no se encuentra registrado.";
		    }

		    if (e.getStatusCode().value() == 400) {
		        return "El correo ingresado no es válido.";
		    }

		    if (e.getStatusCode().value() >= 500) {
		        return "No se pudo procesar la solicitud. Verifique el correo e intente nuevamente.";
		    }

		    return "Ocurrió un error al solicitar la recuperación.";

		} catch (Exception e) {

			return "No se pudo conectar con el servidor";
		}
	}

	@Override
	public boolean validarToken(String token) {

		if (token == null || token.isBlank()) {

			return false;
		}

		try {

			Boolean valido = webClient.get().uri("/api/usuario/validar-token/{token}", token).retrieve()
					.toBodilessEntity().map(respuesta -> respuesta.getStatusCode().is2xxSuccessful())
					.onErrorReturn(false).block();

			return Boolean.TRUE.equals(valido);

		} catch (Exception e) {

			return false;
		}
	}

	@Override
	public String restablecerPassword(RestablecerPasswordDto dto) {

		try {

			return webClient.post().uri("/api/usuario/restablecer-password").contentType(MediaType.APPLICATION_JSON)
					.bodyValue(dto).retrieve().bodyToMono(String.class).block();

		} catch (WebClientResponseException e) {

			String respuesta = e.getResponseBodyAsString();

			if (respuesta == null || respuesta.isBlank()) {

				return "No se pudo restablecer la contraseña";
			}

			return respuesta;

		} catch (Exception e) {

			return "No se pudo conectar con el servidor";
		}
	}

}
