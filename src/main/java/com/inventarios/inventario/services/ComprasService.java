package com.inventarios.inventario.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.inventarios.inventario.entities.Compras;
import com.inventarios.inventario.entities.Productos;
import com.inventarios.inventario.repositories.ComprasRepository;

import jakarta.transaction.Transactional;

@Service
public class ComprasService implements IComprasService {


    @Autowired
    private ComprasRepository repository;

    @Override
    public List<Compras> mostrarCompras() {
        return repository.findAll(Sort.by(Sort.Direction.DESC,"id"));
    }

    @Override
    public Compras buscarPorId(Long id) {
       return repository.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public Compras guardarCompras(Compras compras) {
       return repository.save(compras);
    }

    @Transactional
    @Override
    public void eliminarCompra(Long id) {
        repository.deleteById(id);
    }

    public List<Compras> buscarPorIdProducto(Long id){
      return  repository.buscarPorIdProducto(id);
    }

    public Long CantidadProductosPorId(Long id){
        return repository.sumaCantidadProducto(id);
    }
    
    @Transactional
    public void deleteByProductoId(Long productoId){
        repository.deleteByProductoId(productoId);
    }
  
    
}
