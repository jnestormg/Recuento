package com.inventarios.inventario.services;

import java.util.*;
import com.inventarios.inventario.entities.Ventas;

public interface IVentasService {

    public List<Ventas> mostrarVentas();

    public Ventas buscarPorId(Long id);

    public Ventas guardarVentas(Ventas ventas);

    public void eliminarVenta(Long id);
    
}
