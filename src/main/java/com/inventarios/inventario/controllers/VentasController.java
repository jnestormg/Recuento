package com.inventarios.inventario.controllers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inventarios.inventario.entities.Productos;
import com.inventarios.inventario.entities.Ventas;
import com.inventarios.inventario.services.ComprasService;
import com.inventarios.inventario.services.ProductosService;
import com.inventarios.inventario.services.VentasService;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.view.ViewScoped;
import lombok.Data;

@Data
@Component
@ViewScoped
public class VentasController {

  @Autowired
  private VentasService ventasService;

  @Autowired
  ProductosService productosService;

  private List<Ventas> listaVentas;

  private List<Ventas> listaVentasBusqueda;

  private Productos productoSeleccionado;

  private Ventas venta;

  private String nombreProducto;

  private Integer cantidadTemporal;

  private static final Logger logger = LoggerFactory.getLogger(indexController.class);

  @PostConstruct
  public void init() {
    mostrarVentas();
  }

  public void mostrarVentas() {
    this.listaVentas = ventasService.mostrarVentas();
  }

  /**
   * Metodo para crear nuevos objetos de producto y ventas
   */
  public void nuevo(ActionEvent event) {
    this.productoSeleccionado = new Productos();
    this.venta = new Ventas();
    this.listaVentas = null;
    logger.info("se crea nuevo producto" + this.productoSeleccionado);

  }

  /**
   * Metodo para guardar las ventas realizadas
   */
  public void guardar() {
    // guarda un nuevo producto
    logger.info("prodcucto a guardar" + this.productoSeleccionado);
    if (this.productoSeleccionado.getId() == null) {
      /*
       * this.productoSeleccionado.setCantidad(this.venta.getCantidad());
       * this.productosService.guardarProductos(this.productoSeleccionado);
       * 
       * this.venta.setFecha_salida(venta.getFecha_salida());
       * this.venta.setProductos(productoSeleccionado); // guarda en una nueva venta
       * el producto
       * this.ventasService.guardarVentas(venta);
       */

      FacesContext.getCurrentInstance().addMessage(null,
       new FacesMessage("Necesitas seleccionar un producto"));
    } else {
      // agrega una nueva venta apartir de un producto selecccionado
      if (this.venta.getId() == null) {
        try {
            if(this.venta.getProductos().getCantidad()>0 && 
            this.venta.getProductos().getCantidad()>=this.venta.getCantidad()){

            Integer cantidad = this.productoSeleccionado.getCantidad();

            Integer totalGuardar = 0;
            totalGuardar = cantidad - this.venta.getCantidad();
            this.productoSeleccionado.setCantidad(totalGuardar);

            this.productosService.guardarProductos(this.productoSeleccionado);
            this.ventasService.guardarVentas(this.venta);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("venta guardada"));
            }else{
              FacesContext.getCurrentInstance().addMessage(null,
              new FacesMessage("No hay existencias suficientes del producto: "+this.productoSeleccionado.getNombre()));
            }
         
        } catch (Exception e) {
          logger.info("Error: " + e);
        }
      } else {
        // actualiza la venta desde la datatable
        try {
          Integer cantidad = this.productoSeleccionado.getCantidad();

          Integer cantidadGuardar = (cantidad + cantidadTemporal) - this.venta.getCantidad();
         

          this.productoSeleccionado.setCantidad(cantidadGuardar);
          this.productosService.guardarProductos(this.productoSeleccionado);
          this.ventasService.guardarVentas(this.venta);
          FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("venta modificada"));

        } catch (Exception e) {
          logger.info("Error :" + e);
        }
      }

    }

    this.productoSeleccionado = null;
    mostrarVentas();

  }

  /**
   * Permite cancelar la venta del producto
   */
  public void cancelar() {
    this.productoSeleccionado = null;
    mostrarVentas();
  }

  /**
   * Este metodo regresa una lista con los
   * nombres de los productos agregados para
   * ser usada por el autocomplete al buscar un producto.
   * 
   * @return Devuelve la lista de nombres de productos
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
   * @param nombre Nombre el producto a buscar
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

      this.venta.setProductos(productoSeleccionado);

    }

  }

  /**
   * Metodo para actuializar las ventas realizadas
   * 
   * @param compraSeleccionada Compra que sera actualizada
   */
  public void actualizarVenta(Ventas ventaSeleccionada) {

    Productos p = productosService.buscarProductosPorId(ventaSeleccionada.getProductos().getId());

    cantidadTemporal = ventaSeleccionada.getCantidad();

    this.productoSeleccionado = p;
    this.venta = ventaSeleccionada;
    this.listaVentas = null;

  }

  /**
   * Obtiene la suma de cantidad de un determinado producto
   * 
   * @param id
   * @return
   */
  public Long CantidadProductosPorId(Long id) {
    return ventasService.CantidadProductosPorId(id);
  }

  /**
   * Este metodo resta la cantidad que de la compra
   * eliminada y la regresa al inventario.
   * 
   * @param compraAEliminar Es la Compra que será eliminada
   */
  public void eliminaVenta(Ventas ventaEliminar) {

    Productos product = productosService.buscarProductosPorId(ventaEliminar.getProductos().getId());

    Integer cantidad = product.getCantidad();

    Integer cantidadEliminar = ventaEliminar.getCantidad();

    Integer cantidadTotal = cantidad + cantidadEliminar;

    product.setCantidad(cantidadTotal);

    productosService.guardarProductos(product);

    ventasService.eliminarVenta(ventaEliminar.getId());
    mostrarVentas();

  }
}
