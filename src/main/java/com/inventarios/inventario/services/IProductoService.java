package com.inventarios.inventario.services;

import java.util.*;
import com.inventarios.inventario.entities.Productos;


public interface IProductoService {
    
    public List<Productos> mostrarProductos();

    public Productos buscarProductosPorId(Long id);

    public Productos guardarProductos(Productos productos);

    public void eliminarProductos(Long id);
    
}
