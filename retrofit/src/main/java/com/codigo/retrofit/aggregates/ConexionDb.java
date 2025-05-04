package com.codigo.retrofit.aggregates;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDb {

    private static ConexionDb conexionDb;
    private static Connection connection;

    private ConexionDb() {
        try {
            String url = "jdbc:postgresql://localhost:5432/Tarea";
            String user = "postgres";
            String pass = "bryan12barp";

            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Conexión a la base de datos Tarea exitosa");
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
        }
    }

    public static ConexionDb obtenerInstancia() {
        if (conexionDb == null) {
            conexionDb = new ConexionDb();
        }
        return conexionDb;
    }

    public Connection getConnection() {
        return connection;
    }
}