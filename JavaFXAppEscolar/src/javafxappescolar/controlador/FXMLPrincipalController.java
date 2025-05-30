package javafxappescolar.controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxappescolar.JavaFXAppEscolar;
import javafxappescolar.modelo.pojo.Usuario;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLPrincipalController implements Initializable {

    private Usuario usuarioSesion;
    @FXML
    private Label lbNombre;
    @FXML
    private Label lbUsuario;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //TODO
    }

    public void setUsuarioSesion(Usuario usuarioSesion) {
        this.usuarioSesion = usuarioSesion;
        cargarInformacionUsuario();
    }

    private void cargarInformacionUsuario() {
        if (usuarioSesion != null) {
            lbNombre.setText(usuarioSesion.toString());
            lbUsuario.setText(usuarioSesion.getUsername());
        }
    }

    @FXML
    public void btnClicCerrarSesion(ActionEvent actionEvent) {
        try {
            FXMLLoader cargador = new FXMLLoader(JavaFXAppEscolar.class.getResource("vista/FXMLInicioSesion.fxml"));
            Parent vistaInicioSesion = cargador.load();

            Scene escenaInicio = new Scene(vistaInicioSesion);
            Stage escenarioActual = (Stage) lbNombre.getScene().getWindow();

            escenarioActual.setScene(escenaInicio);
            escenarioActual.setTitle("Inicio de Sesión");
            escenarioActual.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void clicBtnAdminAlumno(ActionEvent actionEvent) {
        Stage escenarioAdmin = new Stage();

        try {
            Parent vista = FXMLLoader.load(JavaFXAppEscolar.class.getResource("vista/FXMLAdminAlumno.fxml"));
            Scene escena = new Scene(vista);
            escenarioAdmin.setScene(escena);
            escenarioAdmin.setTitle("Administrador de Alumnos");
            escenarioAdmin.initModality(Modality.APPLICATION_MODAL);
            escenarioAdmin.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
