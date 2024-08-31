package com.inventarios.inventario.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inventarios.inventario.entities.Ventas;

@Repository
public interface VentasRepository extends JpaRepository<Ventas, Long> {

    @Query("SELECT SUM(c.cantidad) FROM Compras c WHERE c.productos.id = :id_producto")
    Long sumaCantidadProducto(@Param("id_producto") Long id_producto);

    @Modifying
    @Query("DELETE FROM Ventas v WHERE v.productos.id = :id_producto")
    void deleteByProductoId(@Param("id_producto") Long id_producto);

}
