package com.uisrael.pedidosweb.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uisrael.pedidosweb.model.dto.request.ComprobantePagoRequestDto;
import com.uisrael.pedidosweb.model.dto.response.ComprobantePagoResponseDto;
import com.uisrael.pedidosweb.services.IComprobantePagoService;



@Controller
// Acepta tanto /comprobantespago como /comprobantesPago para evitar errores de tipeo
@RequestMapping({"/comprobantespago", "/comprobantesPago"}) 
public class ComprobantePagoController {

    @Autowired
    private IComprobantePagoService servicioComprobantePago;

    @GetMapping
    public String leerPagina(Model model) {
        List<ComprobantePagoResponseDto> resultadosBD = servicioComprobantePago.listarComprobantesPago();
        model.addAttribute("listacomprobantespago", resultadosBD);
        return "comprobantesPago/listarcomprobantespago";
    }

    @GetMapping("/nuevo")
    public String crearComprobantePago(Model model) {
        model.addAttribute("comprobantepago", new ComprobantePagoRequestDto());
        return "comprobantesPago/crearcomprobantepago";
    }

    @PostMapping("/guardar")
    public String guardarComprobantePago(@ModelAttribute ComprobantePagoRequestDto comprobantePago) {
        servicioComprobantePago.guardarComprobantePago(comprobantePago);
        return "redirect:/comprobantespago";
    }
    
}