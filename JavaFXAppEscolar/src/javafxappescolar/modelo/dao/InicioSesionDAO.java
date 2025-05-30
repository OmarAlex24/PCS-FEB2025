/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxappescolar.modelo.dao;

import javafxappescolar.modelo.ConexionBD;
import javafxappescolar.modelo.pojo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author omaralex
 */
public class InicioSesionDAO {

    public static Usuario verificarCredenciales(String username, String password) throws SQLException {
        Usuario usuarioSesion = null;

        Connection conexionBD = ConexionBD.abrirConexion();

        if (conexionBD != null) {
            String consultaSQL = "select * from usuario where username = ? and password = ?";

            PreparedStatement ps = conexionBD.prepareStatement(consultaSQL);

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                usuarioSesion = resultSetToUsuario(rs);
            }

        } else {
            throw new SQLException("Error: Sin conexion a la base de datos");
        }

        return usuarioSesion;
    }

    private static Usuario resultSetToUsuario(ResultSet resultSet) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(resultSet.getInt("idUsuario"));
        usuario.setNombre(resultSet.getString("nombre"));
        usuario.setApellidoPaterno(resultSet.getString("apellidoPaterno"));
        usuario.setApellidoMaterno(resultSet.getString("apellidoMaterno"));
        usuario.setUsername(resultSet.getString("username"));
        usuario.setPassword(resultSet.getString("password"));
        return usuario;
    }

}
