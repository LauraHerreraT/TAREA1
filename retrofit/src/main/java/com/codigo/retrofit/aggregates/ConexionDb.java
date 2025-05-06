package com.codigo.retrofit.aggregates;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDb {

    private static ConexionDb conexionDb;

    private ConexionDb() {
    }
    public static ConexionDb obtenerInstancia() {
        if (conexionDb == null) {
            conexionDb = new ConexionDb();
        }
        return conexionDb;
    }

    public Connection getConnection() {
        try {
            String url = "jdbc:postgresql://localhost:5432/TAREA1";
            String user = "postgres";
            String pass = "password";
            return DriverManager.getConnection(url, user, pass);
        }
        catch (SQLException e)
        {
            System.out.println("Error en la conexión: " + e.getMessage());
            throw new RuntimeException("No se pudo establecer la conexión", e);
        }
    }
}
