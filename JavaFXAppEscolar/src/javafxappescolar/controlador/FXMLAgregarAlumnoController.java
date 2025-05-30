/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxappescolar.controlador;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafxappescolar.dominio.AlumnoDM;
import javafxappescolar.interfaz.INotificacion;
import javafxappescolar.modelo.dao.AlumnoDAO;
import javafxappescolar.modelo.dao.CatalogoDAO;
import javafxappescolar.modelo.pojo.Alumno;
import javafxappescolar.modelo.pojo.Carrera;
import javafxappescolar.modelo.pojo.Facultad;
import javafxappescolar.modelo.pojo.ResultadoOperacion;
import javafxappescolar.utilidades.Utilidad;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLAgregarAlumnoController implements Initializable {

    @javafx.fxml.FXML
    private TextField tfNombres;
    @javafx.fxml.FXML
    private TextField tfMatricula;
    @javafx.fxml.FXML
    private TextField tfApPaterno;
    @javafx.fxml.FXML
    private DatePicker dpFechaNacimiento;
    @javafx.fxml.FXML
    private TextField tfEmail;
    @javafx.fxml.FXML
    private TextField tfApMaterno;
    @javafx.fxml.FXML
    private Label lbErrorApPaterno;
    @javafx.fxml.FXML
    private Label lbErrorEmail;
    @javafx.fxml.FXML
    private Label lbErrorApMaterno;
    @javafx.fxml.FXML
    private Label lbErrorFechaNacimiento;
    @javafx.fxml.FXML
    private Label lbErrorNombre;
    @javafx.fxml.FXML
    private Label lbErrorMatricula;
    @javafx.fxml.FXML
    private ComboBox cbFacultad;
    @javafx.fxml.FXML
    private ComboBox cbCarrera;

    ObservableList<Facultad> facultades;
    ObservableList<Carrera> carreras;
    File archivoFoto;

    INotificacion observador;
    Alumno alumnoEdicion;
    boolean esEdicion;

    @javafx.fxml.FXML
    private ImageView ivFotoAlumno;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarFacultades();
        seleccionarFacultad();
    }

    public void inicializarInformacion(boolean esEdicion, Alumno alumnoEdicion, INotificacion observador) {
        this.observador = observador;
        this.alumnoEdicion = alumnoEdicion;
        this.esEdicion = esEdicion;

        if (esEdicion) {
            cargarInformacionEdicion();
        }
    }

    private void cargarInformacionEdicion() {
        tfMatricula.setText(alumnoEdicion.getMatricula());
        tfMatricula.setDisable(true);

        tfNombres.setText(alumnoEdicion.getNombre());
        tfApPaterno.setText(alumnoEdicion.getApellidoPaterno());
        tfApMaterno.setText(alumnoEdicion.getApellidoMaterno());
        tfEmail.setText(alumnoEdicion.getEmail());

        if (alumnoEdicion.getFechaNacimiento() != null) {
            dpFechaNacimiento.setValue(LocalDate.parse(alumnoEdicion.getFechaNacimiento()));
        }
        dpFechaNacimiento.setDisable(true);

        cbFacultad.getSelectionModel().select(obtenerPosicionFacultad(alumnoEdicion.getIdFacultad()));
        cbCarrera.getSelectionModel().select(obtenerPosicionCarrera(alumnoEdicion.getIdCarrera()));

        try {
            byte[] foto = AlumnoDAO.obtenerFotoAlumno(alumnoEdicion.getIdAlumno());
            alumnoEdicion.setFoto(foto);
            ByteArrayInputStream input = new ByteArrayInputStream(foto);
            Image image = new Image(input);
            ivFotoAlumno.setImage(image);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            Utilidad.crearAlerta(Alert.AlertType.WARNING, "Foto no disponible",
                    "No se encontró una foto para este alumno, por favor seleccione una nueva.");
        }


    }

    private void cargarFacultades() {
        try {
            facultades = FXCollections.observableArrayList();
            List<Facultad> facultadesDAO = CatalogoDAO.obtenerFacultades();
            facultades.addAll(facultadesDAO);
            cbFacultad.setItems(facultades);
        } catch (SQLException e) {
            Utilidad.crearAlerta(Alert.AlertType.ERROR, "Error al cargar",
                    "Lo sentimos por el momento no se puede mostrar"
                            + "la información, por favor inténtelo más tarde.");
        }
    }

    private void cargarCarreras(int idFacultad) {
        try {
            carreras = FXCollections.observableArrayList();
            List<Carrera> carreraDAO = CatalogoDAO.obtenerCarrerasPorFacultad(idFacultad);
            carreras.addAll(carreraDAO);
            cbCarrera.setItems(carreras);
        } catch (SQLException e) {
            Utilidad.crearAlerta(Alert.AlertType.ERROR, "Error al cargar",
                    "Lo sentimos por el momento no se puede mostrar"
                            + "la información, por favor inténtelo más tarde.");
        }
    }

    private void seleccionarFacultad() {
        cbFacultad.valueProperty().addListener(new ChangeListener<Facultad>() {
            @Override
            public void changed(ObservableValue<? extends Facultad> observableValue, Facultad oldFacultad,
                                Facultad newFacultad) {
                if (newFacultad != null) {
                    cargarCarreras(newFacultad.getIdFacultad());
                }
            }
        });
    }

    @javafx.fxml.FXML
    public void clicAgregar(ActionEvent actionEvent) {
        if (validarCampos()) {
            try {
                if (!esEdicion) {
                    ResultadoOperacion resultado = AlumnoDM.verificarEstadoMatricula(tfMatricula.getText());
                    Alumno alumno = obtenerAlumnoNuevo();
                    guardarAlumno(alumno);
                } else {
                    Alumno alumno = obtenerAlumnoEdicion();
                    modificarAlumno(alumno);
                }


            } catch (IOException e) {
                Utilidad.crearAlerta(Alert.AlertType.ERROR, "Error en foto",
                        "Ocurrio un error al intentar guardar la foto, agregue otra foto");
            }

        }
    }

    @javafx.fxml.FXML
    public void clicCancelar(ActionEvent actionEvent) {
        if (!alumnoEdicion.getApellidoMaterno().equals(tfApMaterno.getText()) ||
                !alumnoEdicion.getApellidoPaterno().equals(tfApPaterno.getText()) ||
                !alumnoEdicion.getNombre().equals(tfNombres.getText()) ||
                !alumnoEdicion.getEmail().equals(tfEmail.getText())) {

            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Cancelar, datos sin guardar");
            confirmacion.setContentText("¿Está seguro de que desea cancelar?, tienes datos sin guardar.");

            if (!confirmacion.showAndWait().get().getButtonData().isCancelButton()) {
                Utilidad.getEscenarioComponente(tfNombres).close();
                return;
            }
        }
        Utilidad.getEscenarioComponente(tfNombres).close();
    }

    @javafx.fxml.FXML
    public void btnSeleccionarFoto(ActionEvent actionEvent) {
        mostrarDialogoSeleccionarFoto();
    }

    private void mostrarDialogoSeleccionarFoto() {
        FileChooser dialogoSeleccion = new FileChooser();
        dialogoSeleccion.setTitle("Seleccionar foto");

        dialogoSeleccion.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("Imagenes", "*.png", "*.jpg", "*.jpeg"));

        archivoFoto = dialogoSeleccion.showOpenDialog(Utilidad.getEscenarioComponente(tfNombres));
        if (archivoFoto != null) {
            mostrarFotoPerfil(archivoFoto);
        } else {
            Utilidad.crearAlerta(Alert.AlertType.WARNING, "Seleccionar foto", "No se seleccionó ninguna foto");
        }

    }

    private void mostrarFotoPerfil(File fotoPerfil) {
        try {
            BufferedImage bufferedImage = ImageIO.read(fotoPerfil);
            Image imagen = SwingFXUtils.toFXImage(bufferedImage, null);

            ivFotoAlumno.setImage(imagen);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private boolean validarCampos() {

        boolean camposValidos = true;

        String nombre = tfNombres.getText();
        String matricula = tfMatricula.getText();
        String apPaterno = tfApPaterno.getText();
        String apMaterno = tfApMaterno.getText();
        String email = tfEmail.getText();
        String fechaNacimiento = dpFechaNacimiento.getValue().toString();

        if (nombre.isEmpty()) {
            lbErrorNombre.setText("Campo requerido");
            camposValidos = false;
        } else {
            lbErrorNombre.setText("");
        }

        if (matricula.isEmpty()) {
            lbErrorMatricula.setText("Campo requerido");
            camposValidos = false;
        } else {
            lbErrorMatricula.setText("");
        }

        if (apPaterno.isEmpty()) {
            lbErrorApPaterno.setText("Campo requerido");
            camposValidos = false;
        } else {
            lbErrorApPaterno.setText("");
        }

        if (apMaterno.isEmpty()) {
            lbErrorApMaterno.setText("Campo requerido");
            camposValidos = false;
        } else {
            lbErrorApMaterno.setText("");
        }

        if (email.isEmpty()) {
            lbErrorEmail.setText("Campo requerido");
            camposValidos = false;
        } else {
            lbErrorEmail.setText("");
        }

        return camposValidos;
    }

    private Alumno obtenerAlumnoNuevo() throws IOException {
        Alumno alumno = new Alumno();

        alumno.setNombre(tfNombres.getText());
        alumno.setMatricula(tfMatricula.getText());
        alumno.setApellidoPaterno(tfApPaterno.getText());
        alumno.setApellidoMaterno(tfApMaterno.getText());
        alumno.setEmail(tfEmail.getText());
        alumno.setFechaNacimiento(dpFechaNacimiento.getValue().toString());
        alumno.setIdCarrera(((Carrera) cbCarrera.getSelectionModel().getSelectedItem()).getIdCarrera());
        alumno.setFoto(Files.readAllBytes(archivoFoto.toPath()));

        return alumno;
    }

    private Alumno obtenerAlumnoEdicion() throws IOException {
        Alumno alumno = new Alumno();

        alumno.setNombre(tfNombres.getText());
        alumno.setMatricula(tfMatricula.getText());
        alumno.setApellidoPaterno(tfApPaterno.getText());
        alumno.setApellidoMaterno(tfApMaterno.getText());
        alumno.setEmail(tfEmail.getText());
        alumno.setFechaNacimiento(dpFechaNacimiento.getValue().toString());
        alumno.setIdCarrera(((Carrera) cbCarrera.getSelectionModel().getSelectedItem()).getIdCarrera());

        if (archivoFoto != null) {
            alumno.setFoto(Files.readAllBytes(archivoFoto.toPath()));
        } else {
            alumno.setFoto(alumnoEdicion.getFoto());
        }

        return alumno;
    }

    private void guardarAlumno(Alumno alumno) {
        try {
            ResultadoOperacion resultado = AlumnoDAO.registrarAlumno(alumno);
            if (!resultado.isError()) {
                Utilidad.crearAlerta(Alert.AlertType.INFORMATION, "Éxito", "El alumno se ha guardado correctamente");
                Utilidad.getEscenarioComponente(tfNombres).close();
                observador.operacionExitosa("Registro", alumno.getNombre());
            } else {
                Utilidad.crearAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar el alumno");
            }
        } catch (SQLException e) {
            Utilidad.crearAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar el alumno: " + e.getMessage());
        }
    }

    private void modificarAlumno(Alumno alumno) {
        try {
            ResultadoOperacion resultado = AlumnoDAO.editarAlumno(alumno);
            if (!resultado.isError()) {
                Utilidad.crearAlerta(Alert.AlertType.INFORMATION, "Alumno(a) modificado", "El alumno(a) " + alumno.getNombre() + " se ha modificado correctamente");
                Utilidad.getEscenarioComponente(tfNombres).close();
                observador.operacionExitosa("Modificar", alumno.getNombre());
            } else {
                Utilidad.crearAlerta(Alert.AlertType.ERROR, "Error", "No se pudo modificar el alumno");
            }
        } catch (SQLException e) {
            Utilidad.crearAlerta(Alert.AlertType.ERROR, "Error", "Error al modificar el alumno: " + e.getMessage());
        }
    }

    private int obtenerPosicionFacultad(int idFacultad) {
        for (int i = 0; i < facultades.size(); i++) {
            if (facultades.get(i).getIdFacultad() == idFacultad) {
                return i;
            }
        }
        return 0;
    }

    private int obtenerPosicionCarrera(int idCarrera) {
        for (int i = 0; i < carreras.size(); i++) {
            if (carreras.get(i).getIdCarrera() == idCarrera) {
                return i;
            }
        }
        return 0;
    }

}
