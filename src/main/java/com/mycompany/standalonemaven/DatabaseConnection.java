package com.mycompany.standalonemaven;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private static final String DATABASE_URL = "jdbc:sqlite:database2.db";
    private static final int MAX_CONNECTIONS = 5;

    private static DatabaseConnection instance;
    private final List<Connection> connections;

    private DatabaseConnection() {
        connections = new ArrayList<>();
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        if (connections.isEmpty()) {
            createConnections();
        }
        return connections.remove(0);
    }

    private void createConnections() {
        try {
            Class.forName("org.sqlite.JDBC");
            for (int i = 0; i < MAX_CONNECTIONS; i++) {
                Connection connection = DriverManager.getConnection(DATABASE_URL);
                connections.add(connection);
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void releaseConnection(Connection connection) {
        connections.add(connection);
    }

    public void closeAllConnections() {
        for (Connection connection : connections) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        connections.clear();
    }
}

