package com.inventarios.inventario.services;

import java.util.*;
import com.inventarios.inventario.entities.Compras;


public interface IComprasService {
    
    public List<Compras> mostrarCompras();

    public Compras buscarPorId(Long id);

    public Compras guardarCompras(Compras compras);

    public void eliminarCompra(Long id);
    
}
