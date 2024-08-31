package com.inventarios.inventario.controllers;

import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inventarios.inventario.services.ComprasService;
import com.inventarios.inventario.services.ProductosService;
import com.inventarios.inventario.services.VentasService;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Data;
import com.inventarios.inventario.entities.*;

import java.util.ArrayList;
import java.util.List;

@Component
@ViewScoped
@Data
@Named
public class indexController {

  @Autowired
  ProductosService productosService;

  private static final Logger logger = LoggerFactory.getLogger(indexController.class);

  private List<Productos> listaProductos;

  private List<Productos> listaProductosBusqueda;

  private Productos productoSeleccionado;

  @Autowired
  private ComprasService comprasService;
  @Autowired
  private VentasService ventasService;

  private PieChartModel pieChartModel;


  @PostConstruct
  public void init() {
    mostrarProductos();
    mensaje();
    graficar();
  }

  

  /**
   * Permite mostra la lista de productos
   * en la tabla del index
   */
  public void mostrarProductos() {
    this.listaProductos = productosService.mostrarProductos();
  }

  /**
   * crear nuevo producto
   * 
   * @param event
   */
  public void nuevo(ActionEvent event) {
    this.productoSeleccionado = new Productos();
    System.out.println("nuevo......................");
    logger.info("se crea nuevo producto" + this.productoSeleccionado);

    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto nuevo"));

  }

  /**
   * Crear mensaje de alerta
   */

  public void mensaje() {
    FacesContext.getCurrentInstance().addMessage(null,
        new FacesMessage(FacesMessage.SEVERITY_INFO, "Info Message", "mesanje"));
  }

  /**
   * Guardar nuevo producto
   */

  public void guardar() {
    logger.info("prodcucto a guardar" + this.productoSeleccionado);
    if (this.productoSeleccionado.getId() == null) {
      this.productosService.guardarProductos(this.productoSeleccionado);
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto agregado"));
    } else {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto no agregado"));

    }

  }

  /**
   * Este metodo permite eliminar las relaciones con las
   * tablas de compras y ventas; para eliminar un producto
   * 
   * @param products
   */
  public void eliminarProducto(Productos products) {

    ventasService.deleteByProductoId(products.getId());
    comprasService.deleteByProductoId(products.getId());
    productosService.eliminarProductos(products.getId());
    mostrarProductos();

  }

  /**
   * Permite realizar una grafica de pie de los 
   * productos más vendidos 
   */
  public void graficar() {

    //obtiene la lista de nombres y la suma de la cantidad vendida
    List<Object[]> object = productosService.topProducto();

    pieChartModel = new PieChartModel();
    ChartData data = new ChartData();

    PieChartDataSet dataset = new PieChartDataSet();
    List<Number> values = new ArrayList<>();
    List<String> labels = new ArrayList<>();

    for (Object[] result : object) {
      String nombreproducto = (String) result[1];
      labels.add(nombreproducto);
      Long cantidadProducto = (Long) result[2];
      values.add(cantidadProducto);
    }

    dataset.setData(values);

    List<String> bgColors = new ArrayList<>();
    bgColors.add("rgb(0,255,13)");
    bgColors.add("rgb(54,162,235)");
    // bgColors.add("rgb(250, 20,0)");

    data.addChartDataSet(dataset);

    data.setLabels(labels);

    pieChartModel.setData(data);
  }

}
