package javafxappescolar.controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxappescolar.JavaFXAppEscolar;
import javafxappescolar.modelo.ConexionBD;
import javafxappescolar.modelo.pojo.Usuario;
import javafxappescolar.utilidades.Utilidad;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static javafxappescolar.modelo.dao.InicioSesionDAO.verificarCredenciales;
import static javafxappescolar.utilidades.Utilidad.crearAlerta;

/**
 * FXML Controller class
 *
 * @author migue
 */
public class FXMLinicioSesionController implements Initializable {

    @FXML
    private TextField tfUsuario;
    @FXML
    private PasswordField tfPassword;
    @FXML
    private Label lbErrorUsuario;
    @FXML
    private Label lbErrorPassword;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Connection conexionBD = ConexionBD.abrirConexion();
        if (conexionBD != null) {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Conexion Base de datos");
            alerta.setContentText("La conexion con la base de datos se ha realizado correctamente");
            alerta.show();
        }
        // TODO
    }

    @FXML
    private void btnClicVerificar(ActionEvent event) {
        String username = tfUsuario.getText();
        String password = tfPassword.getText();

        if (validarCampos(username, password)) {
            validarCredenciales(username, password);
        }
    }

    private boolean validarCampos(String username, String passowrd) {
        lbErrorPassword.setText("");
        lbErrorUsuario.setText("");

        boolean camposValidos = true;
        if (username.isEmpty()) {
            lbErrorUsuario.setText("Usuario obligatorio");
            camposValidos = false;
        }
        if (passowrd.isEmpty()) {
            lbErrorPassword.setText("Contraseña obligatoria");
            camposValidos = false;
        }
        return camposValidos;
    }

    private void validarCredenciales(String username, String password) {
        //TODO flujo normal y alterno 1

        try {
            Usuario usuario = verificarCredenciales(tfUsuario.getText(), tfPassword.getText());

            if (usuario != null) {
                crearAlerta(Alert.AlertType.INFORMATION,
                        "Credenciales correctas",
                        "Bienvenido(a) " + usuario.toString() + " a la aplicacion");

                irPantallaPrincipal(usuario);
            } else {
                crearAlerta(Alert.AlertType.WARNING,
                        "Credenciales incorrectas",
                        "Verifique sus credenciales");
            }
        } catch (SQLException e) {

            crearAlerta(Alert.AlertType.ERROR,
                    "Problema de conexion",
                    e.getMessage());

            throw new RuntimeException(e);
        }

    }

    private void irPantallaPrincipal(Usuario usuario) {
        Stage escenarioBase = Utilidad.getEscenarioComponente(tfPassword);

        try {
            FXMLLoader loader = new FXMLLoader(JavaFXAppEscolar.class.getResource("vista/FXMLPrincipal.fxml"));
            Parent vista = loader.load();

            FXMLPrincipalController controlador = loader.getController();
            controlador.setUsuarioSesion(usuario);

            Scene escenaPrincipal = new Scene(vista);
            escenarioBase.setScene(escenaPrincipal);
            escenarioBase.setTitle("Home");
            escenarioBase.show();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }


}

