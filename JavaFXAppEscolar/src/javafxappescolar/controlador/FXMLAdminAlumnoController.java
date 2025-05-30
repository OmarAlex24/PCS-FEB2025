/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxappescolar.controlador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxappescolar.JavaFXAppEscolar;
import javafxappescolar.interfaz.INotificacion;
import javafxappescolar.modelo.dao.AlumnoDAO;
import javafxappescolar.modelo.pojo.Alumno;
import javafxappescolar.utilidades.Utilidad;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author OmarAlex
 */
public class FXMLAdminAlumnoController implements Initializable, INotificacion {

    @FXML
    private TextField tfBuscar;
    @FXML
    private TableColumn colMatricula;
    @FXML
    private TableColumn colApPaterno;
    @FXML
    private TableColumn colApMaterno;
    @FXML
    private TableColumn colNombre;
    @FXML
    private TableColumn colFacultad;
    @FXML
    private TableColumn colCarrera;
    @FXML
    private TableView<Alumno> tableViewAlumnos;

    private ObservableList<Alumno> alumnos;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();
    }

    private void configurarColumnas() {
        colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        colApPaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoPaterno"));
        colApMaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoMaterno"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colFacultad.setCellValueFactory(new PropertyValueFactory<>("nombreFacultad"));
        colCarrera.setCellValueFactory(new PropertyValueFactory<>("nombreCarrera"));
    }

    private void cargarDatos() {
        try {
            alumnos = FXCollections.observableArrayList(AlumnoDAO.obtenerAlumnos());
        } catch (Exception e) {
            Utilidad.crearAlerta(Alert.AlertType.ERROR, "Error al cargar",
                    "Lo sentimos por el momento no se puede mostrar la informacion de los alumnos, "
                            + "por favor intenta mas tarde.");

            cerrarVentana();
        }
        tableViewAlumnos.setItems(alumnos);
    }

    private void cerrarVentana() {
        ((Stage) tfBuscar.getScene().getWindow()).close();
    }

    private void irFormularioAlumno(boolean esEdicion, Alumno alumnoEdicion) {
        try {
            Stage escenarioFormulario = new Stage();
            FXMLLoader loader = new FXMLLoader(JavaFXAppEscolar.class.getResource("vista/FXMLAgregarAlumno.fxml"));
            Parent vista = loader.load();

            FXMLAgregarAlumnoController controlador = loader.getController();

            controlador.inicializarInformacion(esEdicion, alumnoEdicion, this);

            Scene escena = new Scene(vista);

            escenarioFormulario.setScene(escena);
            escenarioFormulario.setTitle("Agregar alumno");
            escenarioFormulario.initModality(Modality.APPLICATION_MODAL);
            escenarioFormulario.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void btnClicAgregar(ActionEvent event) {
        irFormularioAlumno(false, null);
    }

    @FXML
    private void btnClicModificar(ActionEvent event) {
        Alumno alumnoEdicion = tableViewAlumnos.getSelectionModel().getSelectedItem();

        if (alumnoEdicion != null) {
            irFormularioAlumno(true, alumnoEdicion);
        } else {
            Utilidad.crearAlerta(Alert.AlertType.WARNING, "Selecciona un alumno",
                    "Por favor selecciona un alumno de la lista para modificar.");
        }
    }

    @FXML
    private void btnClicEliminar(ActionEvent event) {

    }

    @Override
    public void operacionExitosa(String tipo, String nombreAlumno) {
        System.out.println("Operacion: " + tipo + ", con el alumno(a): " + nombreAlumno);
        cargarDatos();
    }
}
