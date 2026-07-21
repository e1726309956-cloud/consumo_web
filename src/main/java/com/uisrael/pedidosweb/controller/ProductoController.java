package com.uisrael.pedidosweb.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uisrael.pedidosweb.model.dto.request.ProductoRequestDto;
import com.uisrael.pedidosweb.model.dto.response.CategoriaResponseDto;
import com.uisrael.pedidosweb.model.dto.response.ProductoResponseDto;
import com.uisrael.pedidosweb.services.ICategoriaService;
import com.uisrael.pedidosweb.services.IProductoService;

@Controller
@RequestMapping("/producto")
public class ProductoController {

    private final IProductoService servicioProducto;
    private final ICategoriaService servicioCategoria;

    

    public ProductoController(IProductoService servicioProducto, ICategoriaService servicioCategoria) {
		super();
		this.servicioProducto = servicioProducto;
		this.servicioCategoria = servicioCategoria;
	}

	@GetMapping
    public String leerPagina(Model model) {

        List<ProductoResponseDto> resultadoDB =
                servicioProducto.listarProducto();

        model.addAttribute("listarProductos", resultadoDB);

        return "productos/listarproductos";
    }

    @GetMapping("/nuevo")
    public String nuevoProducto(Model model) {

        ProductoRequestDto producto = new ProductoRequestDto();
        List<CategoriaResponseDto> categorias = servicioCategoria.listarCategorias();

        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categorias);

        return "productos/nuevosproductos";
    }

    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute ProductoRequestDto producto) {
        servicioProducto.guardarProducto(producto);
        return "redirect:/producto";
    }
}