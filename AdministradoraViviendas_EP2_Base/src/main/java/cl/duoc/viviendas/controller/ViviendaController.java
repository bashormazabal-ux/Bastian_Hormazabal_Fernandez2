
package cl.duoc.viviendas.controller;

import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Vivienda;
import cl.duoc.viviendas.repository.ViviendaRepository;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class ViviendaController {

    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtSuperficie;
    @FXML private TextField txtHabitaciones;
    @FXML private TextField txtPiso;
    @FXML private TextField txtBuscar;

    @FXML private CheckBox chkGastoComun;
    @FXML private CheckBox chkEstacionamiento;
    @FXML private CheckBox chkPatio;

    @FXML private TableView<Vivienda> tablaViviendas;
    @FXML private TableColumn<Vivienda, String> colTipo;
    @FXML private TableColumn<Vivienda, String> colCodigo;
    @FXML private TableColumn<Vivienda, Double> colSuperficie;
    @FXML private TableColumn<Vivienda, Integer> colHabitaciones;
    @FXML private TableColumn<Vivienda, String> colCosto;

    @FXML private Label lblMensaje;

    private final ObservableList<Vivienda> viviendas =
            FXCollections.observableArrayList();

    private final FilteredList<Vivienda> viviendasFiltradas =
            new FilteredList<>(viviendas, vivienda -> true);

    private ViviendaRepository repository;

    @FXML
    public void initialize() {

        cmbTipo.setItems(
                FXCollections.observableArrayList("Casa", "Departamento")
        );

        colTipo.setCellValueFactory(
                new PropertyValueFactory<>("tipo")
        );

        colCodigo.setCellValueFactory(
                new PropertyValueFactory<>("codigo")
        );

        colSuperficie.setCellValueFactory(
                new PropertyValueFactory<>("superficie")
        );

        colHabitaciones.setCellValueFactory(
                new PropertyValueFactory<>("habitaciones")
        );

        colCosto.setCellValueFactory(datos -> {

            double costo =
                    datos.getValue().calcularCostoArriendo();

            NumberFormat formato =
                    NumberFormat.getCurrencyInstance(
                            Locale.forLanguageTag("es-CL")
                    );

            return new SimpleStringProperty(
                    formato.format(costo)
            );
        });

        tablaViviendas.setItems(viviendasFiltradas);

        cmbTipo.valueProperty().addListener(
                (observable, anterior, nuevo) -> {

                    boolean departamento =
                            "Departamento".equals(nuevo);

                    txtPiso.setDisable(!departamento);
                    chkGastoComun.setDisable(!departamento);
                    chkEstacionamiento.setDisable(!departamento);
                    chkPatio.setDisable(departamento);
                }
        );

        txtPiso.setDisable(true);
        chkGastoComun.setDisable(true);
        chkEstacionamiento.setDisable(true);
        chkPatio.setDisable(true);

        if (txtBuscar != null) {

            txtBuscar.textProperty().addListener(
                    (observable, anterior, nuevo) ->
                            filtrarViviendas(nuevo)
            );
        }

        try {

            repository = new ViviendaRepository();

            viviendas.setAll(
                    repository.listar()
            );

        } catch (RuntimeException e) {

            mostrarMensaje(
                    "Error al cargar viviendas: "
                            + e.getMessage()
            );
        }
    }

    private void filtrarViviendas(String texto) {

        String busqueda = texto == null
                ? ""
                : texto.trim().toLowerCase(Locale.ROOT);

        viviendasFiltradas.setPredicate(vivienda ->
                busqueda.isEmpty()
                        || vivienda.getCodigo()
                        .toLowerCase(Locale.ROOT)
                        .contains(busqueda)
        );
    }

    @FXML
    private void registrarVivienda() {

        if (repository == null) {

            mostrarMensaje(
                    "No se pudo iniciar el repositorio."
            );

            return;
        }

        try {

            String tipo = cmbTipo.getValue();

            if (tipo == null) {

                mostrarMensaje(
                        "Debe seleccionar un tipo de vivienda."
                );

                return;
            }

            String codigo =
                    txtCodigo.getText().trim();

            double superficie =
                    Double.parseDouble(
                            txtSuperficie.getText().trim()
                    );

            int habitaciones =
                    Integer.parseInt(
                            txtHabitaciones.getText().trim()
                    );

            Vivienda nuevaVivienda;

            if ("Casa".equals(tipo)) {

                nuevaVivienda = new Casa(
                        codigo,
                        superficie,
                        habitaciones,
                        chkPatio.isSelected()
                );

            } else {

                int piso =
                        Integer.parseInt(
                                txtPiso.getText().trim()
                        );

                Departamento departamento =
                        new Departamento(
                                codigo,
                                superficie,
                                habitaciones,
                                piso,
                                chkGastoComun.isSelected()
                        );

                if (chkEstacionamiento.isSelected()) {
                    departamento.asignarEstacionamiento();
                }

                nuevaVivienda = departamento;
            }

            repository.registrar(nuevaVivienda);

            viviendas.setAll(
                    repository.listar()
            );

            mostrarMensaje(
                    "Vivienda registrada y guardada correctamente."
            );

            limpiarFormulario();

        } catch (NumberFormatException e) {

            mostrarMensaje(
                    "Revise los campos numéricos."
            );

        } catch (IllegalArgumentException e) {

            mostrarMensaje(
                    e.getMessage()
            );

        } catch (RuntimeException e) {

            mostrarMensaje(
                    "No se pudo guardar: "
                            + e.getMessage()
            );
        }
    }

    @FXML
    private void eliminarVivienda() {

        if (repository == null) {

            mostrarMensaje(
                    "No se pudo iniciar el repositorio."
            );

            return;
        }

        Vivienda seleccionada =
                tablaViviendas
                        .getSelectionModel()
                        .getSelectedItem();

        if (seleccionada == null) {

            mostrarMensaje(
                    "Debe seleccionar una vivienda de la tabla para eliminarla."
            );

            return;
        }

        try {

            repository.eliminar(seleccionada);

            viviendas.setAll(
                    repository.listar()
            );

            mostrarMensaje(
                    "Vivienda eliminada y cambios guardados."
            );

        } catch (RuntimeException e) {

            mostrarMensaje(
                    "No se pudo eliminar: "
                            + e.getMessage()
            );
        }
    }

    @FXML
    private void verDetalles() {

        Vivienda seleccionada =
                tablaViviendas
                        .getSelectionModel()
                        .getSelectedItem();

        if (seleccionada == null) {

            mostrarMensaje(
                    "Seleccione una vivienda de la tabla para ver sus detalles."
            );

            return;
        }

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/cl/duoc/viviendas/view/detalle-vivienda.fxml"
                    )
            );

            Parent contenido = loader.load();

            DetalleViviendaController controladorDetalle =
                    loader.getController();

            controladorDetalle.mostrarVivienda(
                    seleccionada
            );

            Stage ventanaDetalle = new Stage();

            ventanaDetalle.setTitle(
                    "Detalle de vivienda - "
                            + seleccionada.getCodigo()
            );

            ventanaDetalle.setScene(
                    new Scene(contenido)
            );

            ventanaDetalle.initModality(
                    Modality.WINDOW_MODAL
            );

            ventanaDetalle.initOwner(
                    tablaViviendas.getScene().getWindow()
            );

            ventanaDetalle.setResizable(false);

            ventanaDetalle.showAndWait();

        } catch (IOException | RuntimeException e) {

            mostrarMensaje(
                    "No se pudo abrir la ventana de detalles: "
                            + e.getMessage()
            );
        }
    }

    private void mostrarMensaje(String mensaje) {

        lblMensaje.setText(mensaje);
    }

    private void limpiarFormulario() {

        txtCodigo.clear();
        txtSuperficie.clear();
        txtHabitaciones.clear();
        txtPiso.clear();

        chkPatio.setSelected(false);
        chkGastoComun.setSelected(false);
        chkEstacionamiento.setSelected(false);
    }
}
