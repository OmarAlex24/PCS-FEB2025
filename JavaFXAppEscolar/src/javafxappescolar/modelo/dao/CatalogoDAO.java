package javafxappescolar.modelo.dao;

import javafxappescolar.modelo.ConexionBD;
import javafxappescolar.modelo.pojo.Carrera;
import javafxappescolar.modelo.pojo.Facultad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CatalogoDAO {

    //TODO
    public static ArrayList<Facultad> obtenerFacultades() throws SQLException {
        ArrayList<Facultad> facultades = new ArrayList<>();

        Connection conexionBD = ConexionBD.abrirConexion();

        if (conexionBD != null) {
            String consultaSQL = "select facultad.idFacultad, facultad.nombre " +
                    "from facultad;";

            PreparedStatement ps = conexionBD.prepareStatement(consultaSQL);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Facultad facultad = new Facultad();
                facultad.setResultSet(rs);
                facultades.add(facultad);
            }

            ps.close();
            rs.close();
            conexionBD.close();

        } else {
            throw new SQLException("Error: Sin conexion a la base de datos");
        }
        return facultades;
    }

    //TODO
    public static ArrayList<Carrera> obtenerCarrerasPorFacultad(int idFacultad) throws SQLException {
        ArrayList<Carrera> carreras = new ArrayList<>();

        Connection conexionBD = ConexionBD.abrirConexion();

        if (conexionBD != null) {
            String consultaSQL = "select carrera.idCarrera, carrera.nombre, carrera.codigo, carrera.idFacultad " +
                    "from carrera " +
                    "inner join facultad on carrera.idFacultad = facultad.idFacultad " +
                    "where facultad.idFacultad = ?;";

            PreparedStatement ps = conexionBD.prepareStatement(consultaSQL);

            ps.setInt(1, idFacultad);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Carrera carrera = new Carrera();
                carrera.setResultSet(rs);
                carreras.add(carrera);
            }

            ps.close();
            rs.close();
            conexionBD.close();

        } else {
            throw new SQLException("Error: Sin conexion a la base de datos");
        }
        return carreras;
    }
}
