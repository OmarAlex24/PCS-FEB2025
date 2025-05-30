package javafxappescolar.utilidades;

import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.stage.Stage;

public class Utilidad {

    public static void crearAlerta(Alert.AlertType tipoAlerta, String titulo, String contenido) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    public static Stage getEscenarioComponente(Control componente) {
        return (Stage) componente.getScene().getWindow();
    }

}
