package com.uisrael.pedidosweb.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.uisrael.pedidosweb.model.dto.carrito.CarritoItemDto;
import com.uisrael.pedidosweb.model.dto.response.ProductoResponseDto;
import com.uisrael.pedidosweb.services.IProductoService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final IProductoService productoService;

    public CarritoController(IProductoService productoService) {
        this.productoService = productoService;
    }

    @SuppressWarnings("unchecked")
    private List<CarritoItemDto> obtenerCarrito(HttpSession session) {

        List<CarritoItemDto> carrito =
                (List<CarritoItemDto>) session.getAttribute("carrito");

        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute("carrito", carrito);
        }

        return carrito;
    }

    @GetMapping
    public String verCarrito(
            HttpSession session,
            Model model) {

        List<CarritoItemDto> carrito =
                obtenerCarrito(session);

        double total = carrito.stream()
                .mapToDouble(CarritoItemDto::getSubtotal)
                .sum();

        int cantidadTotal = carrito.stream()
                .mapToInt(CarritoItemDto::getCantidad)
                .sum();

        model.addAttribute("carrito", carrito);
        model.addAttribute("total", total);
        model.addAttribute("cantidadTotal", cantidadTotal);

        return "carrito/carrito";
    }

    @PostMapping("/agregar/{idProducto}")
    public String agregarProducto(
            @PathVariable int idProducto,
            @RequestParam(defaultValue = "1") int cantidad,
            HttpSession session) {

        if (cantidad < 1) {
            cantidad = 1;
        }

        ProductoResponseDto producto =
                productoService.buscarPorId(idProducto);

        if (producto == null) {
            throw new RuntimeException("Producto no encontrado");
        }

        if (!producto.isDisponible()) {
            throw new RuntimeException(
                    "El producto no está disponible"
            );
        }

        List<CarritoItemDto> carrito =
                obtenerCarrito(session);

        CarritoItemDto existente = carrito.stream()
                .filter(item ->
                        item.getIdProducto() == idProducto
                )
                .findFirst()
                .orElse(null);

        if (existente != null) {

            existente.setCantidad(
                    existente.getCantidad() + cantidad
            );

        } else {

            CarritoItemDto item =
                    new CarritoItemDto();

            item.setIdProducto(
                    producto.getIdProducto()
            );

            item.setNombre(
                    producto.getNombre()
            );

            item.setImagenUrl(
                    producto.getImagenUrl()
            );

            item.setPrecio(
                    producto.getPrecio()
            );

            item.setCantidad(cantidad);

            carrito.add(item);
        }

        session.setAttribute("carrito", carrito);

        return "redirect:/carrito";
    }

    @PostMapping("/actualizar/{idProducto}")
    public String actualizarCantidad(
            @PathVariable int idProducto,
            @RequestParam int cantidad,
            HttpSession session) {

        List<CarritoItemDto> carrito =
                obtenerCarrito(session);

        if (cantidad <= 0) {

            carrito.removeIf(item ->
                    item.getIdProducto() == idProducto
            );

        } else {

            carrito.stream()
                    .filter(item ->
                            item.getIdProducto() == idProducto
                    )
                    .findFirst()
                    .ifPresent(item ->
                            item.setCantidad(cantidad)
                    );
        }

        session.setAttribute("carrito", carrito);

        return "redirect:/carrito";
    }

    @GetMapping("/eliminar/{idProducto}")
    public String eliminarProducto(
            @PathVariable int idProducto,
            HttpSession session) {

        List<CarritoItemDto> carrito =
                obtenerCarrito(session);

        carrito.removeIf(item ->
                item.getIdProducto() == idProducto
        );

        session.setAttribute("carrito", carrito);

        return "redirect:/carrito";
    }

    @GetMapping("/vaciar")
    public String vaciarCarrito(
            HttpSession session) {

        session.removeAttribute("carrito");

        return "redirect:/carrito";
    }
}