package javafxappescolar.modelo.dao;

import javafxappescolar.modelo.ConexionBD;
import javafxappescolar.modelo.pojo.Alumno;
import javafxappescolar.modelo.pojo.ResultadoOperacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AlumnoDAO {

    public static ArrayList<Alumno> obtenerAlumnos() throws SQLException {
        ArrayList<Alumno> alumnos = new ArrayList<>();

        Connection conexionBD = ConexionBD.abrirConexion();

        if (conexionBD != null) {
            String consultaSQL = "SELECT idAlumno, matricula, apellidoPaterno, apellidoMaterno, alumno.nombre, fechaNacimiento, alumno.idCarrera, carrera.nombre as carrera, facultad.idFacultad, facultad.nombre as facultad, email " +
                    "FROM alumno " +
                    "INNER JOIN carrera ON alumno.idCarrera = carrera.idCarrera " +
                    "INNER JOIN facultad ON carrera.idFacultad = facultad.idFacultad";

            PreparedStatement ps = conexionBD.prepareStatement(consultaSQL);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                alumnos.add(resultSetToAlumno(rs));
            }

            ps.close();
            rs.close();
            conexionBD.close();

        } else {
            throw new SQLException("Error: Sin conexion a la base de datos");
        }
        return alumnos;
    }

    public static ResultadoOperacion registrarAlumno() throws SQLException {

        ResultadoOperacion resultado = new ResultadoOperacion();
        Connection conexionBD = ConexionBD.abrirConexion();

        if (conexionBD != null) {
            String consultaSQL = "INSERT INTO alumno (matricula, apellidoPaterno, apellidoMaterno, nombre, fechaNacimiento, idCarrera) VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = conexionBD.prepareStatement(consultaSQL);

            ps.setString(1, "123456");
            ps.setString(2, "Paterno");
            ps.setString(3, "Materno");
            ps.setString(4, "Nombre");
            ps.setString(5, "2000-01-01");
            ps.setInt(6, 1);


            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                resultado.setError(false);
                resultado.setMensaje("Alumno registrado correctamente");
            } else {
                resultado.setError(true);
                resultado.setMensaje("Error al registrar el alumno");
            }

            ps.close();
            conexionBD.close();

        } else {
            throw new SQLException("Error: Sin conexion a la base de datos");
        }
        return resultado;

    }

    public static ResultadoOperacion registrarAlumno(Alumno alumno) throws SQLException {
        ResultadoOperacion resultado = new ResultadoOperacion();
        Connection conexionBD = ConexionBD.abrirConexion();
        if (conexionBD != null) {
            String consultaSQL = "INSERT INTO alumno (nombre, apellidoPaterno, apellidoMaterno, matricula, email, idCarrera, fechaNacimiento, foto) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = conexionBD.prepareStatement(consultaSQL);
            ps.setString(1, alumno.getNombre());
            ps.setString(2, alumno.getApellidoPaterno());
            ps.setString(3, alumno.getApellidoMaterno());
            ps.setString(4, alumno.getMatricula());
            ps.setString(5, alumno.getEmail());
            ps.setInt(6, alumno.getIdCarrera());
            ps.setString(7, alumno.getFechaNacimiento());
            ps.setBytes(8, alumno.getFoto());

            int filasActualizadas = ps.executeUpdate();

            if (filasActualizadas == 1) {
                resultado.setError(false);
                resultado.setMensaje("Alumno registrado correctamente");
            } else {
                resultado.setError(true);
                resultado.setMensaje("Lo sentimos no se pudo registrar el alumno, intente más tarde");
            }

            ps.close();
            conexionBD.close();

        } else {
            throw new SQLException("Error: Sin conexion a la base de datos");

        }
        return resultado;
    }

    public static byte[] obtenerFotoAlumno(int idAlumno) throws SQLException {
        byte[] foto = null;
        Connection conexionBD = ConexionBD.abrirConexion();

        if (conexionBD != null) {
            String consultaSQL = "SELECT foto FROM alumno WHERE idAlumno = ?";
            PreparedStatement ps = conexionBD.prepareStatement(consultaSQL);
            ps.setInt(1, idAlumno);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                foto = (byte[]) rs.getObject("foto");
            }

            ps.close();
            rs.close();
            conexionBD.close();
        } else {
            throw new SQLException("Error: Sin conexion a la base de datos");
        }
        return foto;
    }

    public static boolean verificarExistenciaMatricula(String matricula) throws SQLException {
        boolean existe = false;
        Connection conexionBD = ConexionBD.abrirConexion();

        if (conexionBD != null) {
            String consultaSQL = "SELECT COUNT(*) FROM alumno WHERE matricula = ?";
            PreparedStatement ps = conexionBD.prepareStatement(consultaSQL);
            ps.setString(1, matricula);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                existe = rs.getInt(1) > 0;
            }

            ps.close();
            rs.close();
            conexionBD.close();
        } else {
            throw new SQLException("Error: Sin conexion a la base de datos");
        }
        return existe;
    }

    public static ResultadoOperacion editarAlumno(Alumno alumno) throws SQLException {
        ResultadoOperacion resultado = new ResultadoOperacion();
        Connection conexionBD = ConexionBD.abrirConexion();

        if (conexionBD != null) {
            String consultaSQL = "UPDATE alumno SET nombre = ?, apellidoPaterno = ?, apellidoMaterno = ?, email = ?, idCarrera = ?, fechaNacimiento = ?, foto = ? WHERE idAlumno = ?";
            PreparedStatement ps = conexionBD.prepareStatement(consultaSQL);
            ps.setString(1, alumno.getNombre());
            ps.setString(2, alumno.getApellidoPaterno());
            ps.setString(3, alumno.getApellidoMaterno());
            ps.setString(4, alumno.getEmail());
            ps.setInt(5, alumno.getIdCarrera());
            ps.setString(6, alumno.getFechaNacimiento());
            ps.setBytes(7, alumno.getFoto());
            ps.setInt(8, alumno.getIdAlumno());

            int filasActualizadas = ps.executeUpdate();

            if (filasActualizadas == 1) {
                resultado.setError(false);
                resultado.setMensaje("Alumno editado correctamente");
            } else {
                resultado.setError(true);
                resultado.setMensaje("Lo sentimos no se pudo editar el alumno, intente más tarde");
            }

            ps.close();
            conexionBD.close();
        } else {
            throw new SQLException("Error: Sin conexion a la base de datos");
        }
        return resultado;
    }

    public static ResultadoOperacion eliminarAlumno(int idAlumno) throws SQLException {
        ResultadoOperacion resultado = new ResultadoOperacion();
        Connection conexionBD = ConexionBD.abrirConexion();

        if (conexionBD != null) {
            String consultaSQL = "DELETE FROM alumno WHERE idAlumno = ?";
            PreparedStatement ps = conexionBD.prepareStatement(consultaSQL);
            ps.setInt(1, idAlumno);

            int filasEliminadas = ps.executeUpdate();

            if (filasEliminadas == 1) {
                resultado.setError(false);
                resultado.setMensaje("Alumno eliminado correctamente");
            } else {
                resultado.setError(true);
                resultado.setMensaje("Lo sentimos no se pudo eliminar el alumno, intente más tarde");
            }

            ps.close();
            conexionBD.close();
        } else {
            throw new SQLException("Error: Sin conexion a la base de datos");
        }
        return resultado;
    }

    private static Alumno resultSetToAlumno(ResultSet resultSet) throws SQLException {
        Alumno alumno = new Alumno();
        alumno.setIdAlumno(resultSet.getInt("idAlumno"));
        alumno.setEmail(resultSet.getString("email"));
        alumno.setMatricula(resultSet.getString("matricula"));
        alumno.setApellidoPaterno(resultSet.getString("apellidoPaterno"));
        alumno.setApellidoMaterno(resultSet.getString("apellidoMaterno"));
        alumno.setNombre(resultSet.getString("nombre"));
        alumno.setFechaNacimiento(resultSet.getString("fechaNacimiento"));
        alumno.setIdCarrera(resultSet.getInt("idCarrera"));
        alumno.setNombreCarrera(resultSet.getString("carrera"));
        alumno.setIdFacultad(resultSet.getInt("idFacultad"));
        alumno.setNombreFacultad(resultSet.getString("facultad"));
        return alumno;
    }
}
