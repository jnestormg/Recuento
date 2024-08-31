package com.inventarios.inventario.repositories;

import java.util.List;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inventarios.inventario.entities.Productos;

@Repository
public interface ProductosRepository extends JpaRepository<Productos, Long> {
    
     @Query("SELECT p FROM Productos p WHERE p.nombre LIKE %:nombre%")
    List<Productos> buscarPorNombre(@Param("nombre") String nombre);

    @Query("SELECT p.cantidad FROM Productos p WHERE p.id = :id")
    Integer cantidadProductos(@Param("id") Long id);

    @Query("SELECT p.id, p.nombre, SUM(v.cantidad) AS total FROM Ventas v JOIN v.productos p GROUP BY p.id ORDER BY total DESC")
    List<Object[]> top10ProductosMasVendidos(org.springframework.data.domain.Pageable page);
}
