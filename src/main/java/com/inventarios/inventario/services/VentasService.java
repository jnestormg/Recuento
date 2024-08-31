package com.inventarios.inventario.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.inventarios.inventario.entities.Ventas;
import com.inventarios.inventario.repositories.VentasRepository;

import jakarta.transaction.Transactional;

@Service
public class VentasService implements IVentasService{

    @Autowired
    private VentasRepository repository;

    
    @Override
    public List<Ventas> mostrarVentas() {
        return repository.findAll(Sort.by(Sort.Direction.DESC,"id"));
    }

    
    @Override
    public Ventas buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public Ventas guardarVentas(Ventas ventas) {
        return repository.save(ventas);
    }

    @Transactional
    @Override
    public void eliminarVenta(Long id) {
        repository.deleteById(id);
    }

    
    public Long CantidadProductosPorId(Long id){
        return repository.sumaCantidadProducto(id);
    }
    
    @Transactional
    public void deleteByProductoId(Long productoId){
        repository.deleteByProductoId(productoId);
    }
    
}
