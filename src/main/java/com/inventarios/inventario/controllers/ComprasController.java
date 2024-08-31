package com.inventarios.inventario.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inventarios.inventario.entities.Compras;
import com.inventarios.inventario.entities.Productos;
import com.inventarios.inventario.services.ComprasService;
import com.inventarios.inventario.services.ProductosService;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Data;
 

@Component
@Data
@ViewScoped
@Named(value = "comprasController")
public class ComprasController {

  @Autowired
  ComprasService comprasService;

  @Autowired
  ProductosService productosService;

  private List<Compras> listaCompras;

  private List<Compras> listaComprasBusqueda;

  private Productos productoSeleccionado;

  private Compras compra;

  private String nombreProducto;

  private static Integer cantidadTemporal;

  private static final Logger logger = LoggerFactory.getLogger(indexController.class);
  
  @PostConstruct
  public void init() {
    mostrarCompras();
  }



  /**
   * Metodo para mostrar en la tabla todas las compras realizadas
   */
  public void mostrarCompras() {
    this.listaCompras = comprasService.mostrarCompras();
  }

  /**
   * Metodo para crear nuevos objetos de producto
   */
  public void nuevo(ActionEvent event) {
    this.productoSeleccionado = new Productos();
    this.compra = new Compras();
    this.listaCompras = null;
    logger.info("se crea nuevo producto" + this.productoSeleccionado);

  }

  /**
   * Metodo para guardar las compras realizadas
   * considerando si es una nueva venta, si ha sido buscada o selecionada
   * átraves de la tabla.
   */
  public void guardar() {
    // guarda un nuevo producto
    logger.info("producto a guardar" + this.productoSeleccionado);
    if (this.productoSeleccionado.getId() == null) {

      if (this.compra.getCantidad() > 0) {

        if(this.productoSeleccionado.getPrecio()>0){
        this.productoSeleccionado.setCantidad(this.compra.getCantidad());
        this.productosService.guardarProductos(this.productoSeleccionado);

        this.compra.setFecha_ingreso(compra.getFecha_ingreso());
        this.compra.setProductos(productoSeleccionado); // guarda en una nueva compra el producto
        this.comprasService.guardarCompras(compra);

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto agregado"));
        }else{
          FacesContext.getCurrentInstance().addMessage(null,
          new FacesMessage("El precio debe ser mayor a cero"));
        }
      } else {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage("Cantidad debe ser mayor a cero"));
      }

    } else {
      // agrega una nueva venta apartir de un producto selecccionado
      if (this.compra.getId() == null) {
        try {

          if (this.compra.getCantidad()>0) {
            Integer cantidad = this.productoSeleccionado.getCantidad();

            if (this.productoSeleccionado.getPrecio() > 0) {

              cantidad += this.compra.getCantidad();
              this.productoSeleccionado.setCantidad(cantidad);

              this.productosService.guardarProductos(this.productoSeleccionado);
              this.comprasService.guardarCompras(this.compra);
              FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Compra guardada"));
            } else {
              FacesContext.getCurrentInstance().addMessage(null,
                  new FacesMessage("El precio debe ser mayor a cero"));
            }
          } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Cantidad debe ser mayor a cero"));
          }
        } catch (Exception e) {
          logger.info("Error: " + e);
        }
      } else {
        // actualiza la compra desde la datatable
        try {

          if (this.compra.getCantidad()>0) {
            if (this.productoSeleccionado.getPrecio()>0) {
              
            
          
          Integer cantidad = this.productoSeleccionado.getCantidad();
          Integer total = 0;
          // se calcula la cantidad para guardar la modificacion con la nueva cantidad
          // modificada
          total = (cantidad - cantidadTemporal) + this.compra.getCantidad();
          this.productoSeleccionado.setCantidad(total);

          this.productosService.guardarProductos(this.productoSeleccionado);
          this.comprasService.guardarCompras(this.compra);
          FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Compra modificada"));

            }else{
              FacesContext.getCurrentInstance().addMessage(null,
              new FacesMessage("El precio debe ser mayor a cero"));
            }
          }else{
            FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage("Cantidad debe ser mayor a cero"));
          }
        } catch (Exception e) {
          logger.info("Error :" + e);
        }
      }

    }

    this.productoSeleccionado = null;
    mostrarCompras();

  }

  /**
   * metodo para cancelar una compra
   */
  public void cancelar() {
    this.productoSeleccionado = null;
    mostrarCompras();
  }

  /**
   * Este metodo regresa una lista con los
   * nombres de los productos agregados para
   * ser usada por el autocomplete al buscar un producto.
   * 
   * @return
   */
  public List<String> nombresProductos() {

    List<Productos> products = productosService.mostrarProductos();
    List<String> nombresProductos = new ArrayList<>();
    for (Productos p : products) {
      nombresProductos.add(p.getNombre());
    }

    return nombresProductos;
  }

  /**
   * Este metodo permite poner los datos de un producto que
   * ha sido seleccionado por un item del autocomplete
   * 
   * @param nombre
   */
  public void setNombres(String nombre) {

    List<Productos> product = productosService.buscarPorNombre(nombre);

    int sizeProductos = product.size() - 1;

    for (int i = 0; i <= sizeProductos; i++) {
      this.productoSeleccionado.setCantidad(product.get(sizeProductos).getCantidad());
      this.productoSeleccionado.setNombre(product.get(sizeProductos).getNombre());
      this.productoSeleccionado.setId(product.get(sizeProductos).getId());
      this.productoSeleccionado.setPrecio(product.get(sizeProductos).getPrecio());
      this.productoSeleccionado.setDescripcion(product.get(sizeProductos).getDescripcion());

      this.compra.setProductos(productoSeleccionado);

    }

  }

  /**
   * Metodo para actualizar las compras realizadas atraves de la tabla
   * 
   * @param compraSeleccionada recibe la compra cuando es seleccionada la compra
   */
  public void actualizarCompra(Compras compraSeleccionada) {

    Productos p = productosService.buscarProductosPorId(compraSeleccionada.getProductos().getId());

    this.productoSeleccionado = p;
    this.compra = compraSeleccionada;

    cantidadTemporal = compraSeleccionada.getCantidad();

    this.listaCompras = null;

  }

  /**
   * Obtiene la suma de cantidad de un determinado producto
   * 
   * @param id
   * @return
   */
  public Long CantidadProductosPorId(Long id) {
    return comprasService.CantidadProductosPorId(id);
  }

  /**
   * Este metodo resta la cantidad que de la compra eliminada y la regresa al
   * inventario.
   * 
   * @param compraAEliminar
   */
  public void eliminarCompra(Compras compraAEliminar) {

    Productos product = productosService.buscarProductosPorId(compraAEliminar.getProductos().getId());

    Integer cantidad = product.getCantidad();

    Integer cantidadEliminar = compraAEliminar.getCantidad();

    Integer cantidadTotal = cantidad - cantidadEliminar;

    product.setCantidad(cantidadTotal);

    productosService.guardarProductos(product);

    comprasService.eliminarCompra(compraAEliminar.getId());
    mostrarCompras();
  }

}
