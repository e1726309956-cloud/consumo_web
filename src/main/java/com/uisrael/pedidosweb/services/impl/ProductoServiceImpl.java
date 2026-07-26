package com.uisrael.pedidosweb.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.uisrael.pedidosweb.model.dto.request.ProductoRequestDto;
import com.uisrael.pedidosweb.model.dto.response.ProductoResponseDto;
import com.uisrael.pedidosweb.services.IProductoService;

@Service
public class ProductoServiceImpl implements IProductoService {

	private static final Path CARPETA_IMAGENES = Paths.get("C:/pedidosweb/imagenes/productos");

	private static final String RUTA_WEB_IMAGENES = "/imagenes/productos/";

	private final WebClient webClient;

	public ProductoServiceImpl(WebClient webClient) {
		this.webClient = webClient;
	}

	@Override
	public List<ProductoResponseDto> listarProducto() {

		return webClient.get().uri("/producto").retrieve().bodyToFlux(ProductoResponseDto.class).collectList().block();
	}

	@Override
	public void guardarProducto(ProductoRequestDto producto, MultipartFile imagen) {

		if (imagen == null || imagen.isEmpty()) {
			throw new RuntimeException("Debe seleccionar una imagen");
		}

		String rutaImagen = guardarImagen(imagen);

		producto.setImagenUrl(rutaImagen);
		producto.setDisponible(true);
		producto.setFechaCreacion(new java.util.Date());

		webClient.post().uri("/producto").bodyValue(producto).retrieve().toBodilessEntity().block();
	}

	@Override
	public void actualizarProducto(ProductoRequestDto producto, MultipartFile imagen) {

		/*
		 * Si selecciona otra imagen, se guarda y se reemplaza imagenUrl. Si no
		 * selecciona una nueva, se conserva la ruta anterior recibida desde el campo
		 * oculto.
		 */
		if (imagen != null && !imagen.isEmpty()) {

			String rutaAnterior = producto.getImagenUrl();

			String nuevaRuta = guardarImagen(imagen);

			producto.setImagenUrl(nuevaRuta);

			eliminarImagenAnterior(rutaAnterior);
		}

		webClient.put().uri("/producto/id/{idProducto}", producto.getIdProducto()).bodyValue(producto).retrieve()
				.toBodilessEntity().block();
	}

	@Override
	public void inactivarProducto(int idProducto) {

		webClient.delete().uri("/producto/{idProducto}", idProducto).retrieve().toBodilessEntity().block();
	}

	@Override
	public void activarProducto(int idProducto) {

		webClient.put().uri("/producto/activar/{idProducto}", idProducto).retrieve().toBodilessEntity().block();
	}

	@Override
	public ProductoResponseDto buscarPorId(int idProducto) {

		return webClient.get().uri("/producto/id/{idProducto}", idProducto).retrieve()
				.bodyToMono(ProductoResponseDto.class).block();
	}

	private String guardarImagen(MultipartFile imagen) {

		validarImagen(imagen);

		try {

			Files.createDirectories(CARPETA_IMAGENES);

			String nombreOriginal = imagen.getOriginalFilename();

			String extension = obtenerExtension(nombreOriginal);

			String nombreArchivo = UUID.randomUUID() + extension;

			Path destino = CARPETA_IMAGENES.resolve(nombreArchivo);

			Files.copy(imagen.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

			return RUTA_WEB_IMAGENES + nombreArchivo;

		} catch (IOException e) {

			throw new RuntimeException("No se pudo guardar la imagen", e);
		}
	}

	private void validarImagen(MultipartFile imagen) {

		if (imagen == null || imagen.isEmpty()) {
			throw new RuntimeException("Debe seleccionar una imagen");
		}

		String contentType = imagen.getContentType();

		boolean tipoPermitido = "image/jpeg".equals(contentType) || "image/png".equals(contentType)
				|| "image/webp".equals(contentType);

		if (!tipoPermitido) {
			throw new RuntimeException("Solo se permiten imágenes JPG, JPEG, PNG o WEBP");
		}

		String extension = obtenerExtension(imagen.getOriginalFilename());

		boolean extensionPermitida = extension.equals(".jpg") || extension.equals(".jpeg") || extension.equals(".png")
				|| extension.equals(".webp");

		if (!extensionPermitida) {
			throw new RuntimeException("La extensión del archivo no es válida");
		}

		long tamanioMaximo = 5L * 1024 * 1024;

		if (imagen.getSize() > tamanioMaximo) {
			throw new RuntimeException("La imagen no puede superar los 5 MB");
		}
	}

	private String obtenerExtension(String nombreArchivo) {

		if (nombreArchivo == null || !nombreArchivo.contains(".")) {

			throw new RuntimeException("El archivo no tiene una extensión válida");
		}

		return nombreArchivo.substring(nombreArchivo.lastIndexOf(".")).toLowerCase();
	}

	private void eliminarImagenAnterior(String rutaAnterior) {

		if (rutaAnterior == null || rutaAnterior.isBlank() || rutaAnterior.contains("sin-imagen")) {

			return;
		}

		try {

			String nombreAnterior = Paths.get(rutaAnterior).getFileName().toString();

			Path archivoAnterior = CARPETA_IMAGENES.resolve(nombreAnterior);

			Files.deleteIfExists(archivoAnterior);

		} catch (IOException e) {


			System.out.println("No se pudo eliminar la imagen anterior: " + e.getMessage());
		}
	}

	@Override
	public List<ProductoResponseDto> buscarPorCategoria(int idCategoria) {

		return webClient.get()

				.uri("/producto/categoria/{idCategoria}", idCategoria)

				.retrieve()

				.bodyToFlux(ProductoResponseDto.class)

				.collectList()

				.block();
	}
}