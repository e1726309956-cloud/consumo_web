package com.uisrael.pedidosweb.model.dto.carrito;

import lombok.Data;

@Data
public class CarritoItemDto {

    private int idProducto;

    private String nombre;

    private String imagenUrl;

    private Double precio;

    private int cantidad;

    public Double getSubtotal() {

        if (precio == null) {
            return 0.0;
        }

        return precio * cantidad;
    }
}