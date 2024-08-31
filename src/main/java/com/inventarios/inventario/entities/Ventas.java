package com.inventarios.inventario.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ventas {

    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha_salida;

    private Integer cantidad;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Productos productos;
    
}
