package com.uisrael.pedidosweb.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uisrael.pedidosweb.model.dto.request.CategoriaRequestDto;
import com.uisrael.pedidosweb.model.dto.response.CategoriaResponseDto;
import com.uisrael.pedidosweb.services.ICategoriaService;

@Controller
@RequestMapping("/categoria")
public class CategoriaController {

    private final ICategoriaService servicioCategoria;

    public CategoriaController(ICategoriaService servicioCategoria) {
        this.servicioCategoria = servicioCategoria;
    }

    @GetMapping
    public String listarCategorias(Model model) {

        List<CategoriaResponseDto> categorias =
                servicioCategoria.listarCategorias();

        model.addAttribute("listarCategorias", categorias);

        return "categorias/listarcategorias";
    }
    
    @GetMapping("/nuevo")
    public String nuevaCategoria(Model model) {

        CategoriaRequestDto categoria = new CategoriaRequestDto();
        categoria.setEstado(true);

        model.addAttribute("categoria", categoria);

        return "categorias/crearcategorias";
    }

    @PostMapping("/guardar")
    public String guardarCategoria(
            @ModelAttribute("categoria") CategoriaRequestDto categoria) {


        if (categoria.getIdCategoria() > 0) {

            servicioCategoria.actualizarCategoria(categoria);

        } else {

            servicioCategoria.guardarCategoria(categoria);
        }

        return "redirect:/categoria";
    }
    
    @GetMapping("/inactivar/{idCategoria}")
    public String inactivarCategoria(
            @PathVariable int idCategoria) {

        servicioCategoria.inactivarCategoria(idCategoria);

        return "redirect:/categoria";
    }

    @GetMapping("/activar/{idCategoria}")
    public String activarCategoria(
            @PathVariable int idCategoria) {

        servicioCategoria.activarCategoria(idCategoria);

        return "redirect:/categoria";
    }
    
    @GetMapping("/editar/{idCategoria}")
    public String editarCategoria(
            @PathVariable int idCategoria,
            Model model) {

        CategoriaResponseDto categoriaApi =
                servicioCategoria.buscarPorId(idCategoria);

        CategoriaRequestDto categoria =
                new CategoriaRequestDto();

        categoria.setIdCategoria(
                categoriaApi.getIdCategoria()
        );

        categoria.setNombre(
                categoriaApi.getNombre()
        );

        categoria.setDescripcion(
                categoriaApi.getDescripcion()
        );

        categoria.setEstado(
                categoriaApi.isEstado()
        );

        model.addAttribute(
                "categoria",
                categoria
        );

        return "categorias/crearcategorias";
    }

}