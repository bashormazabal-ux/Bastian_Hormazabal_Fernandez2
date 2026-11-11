
package cl.duoc.viviendas.controller;

import cl.duoc.viviendas.model.Vivienda;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.Locale;

public class DetalleViviendaController {

    @FXML private Label lblTipo;
    @FXML private Label lblCodigo;
    @FXML private Label lblSuperficie;
    @FXML private Label lblHabitaciones;
    @FXML private Label lblCosto;

    public void mostrarVivienda(Vivienda vivienda) {

        if (vivienda == null) {
            return;
        }

        lblTipo.setText("Tipo: " + vivienda.getTipo());
        lblCodigo.setText("Código: " + vivienda.getCodigo());

        lblSuperficie.setText(
                "Superficie: " + vivienda.getSuperficie() + " m²"
        );

        lblHabitaciones.setText(
                "Habitaciones: " + vivienda.getHabitaciones()
        );

        NumberFormat formato = NumberFormat.getCurrencyInstance(
                Locale.forLanguageTag("es-CL")
        );

        lblCosto.setText(
                "Costo de arriendo: "
                        + formato.format(vivienda.calcularCostoArriendo())
        );
    }

    @FXML
    private void cerrarVentana() {
        Stage ventana = (Stage) lblTipo.getScene().getWindow();
        ventana.close();
    }
}
