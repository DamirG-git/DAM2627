package com.da.dg2526;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Utility {

    private static final String driverName ="org.postgresql.Driver";
    private static final String user = "postgres";
    private static final String password = "damir";


    public static Connection getConnectionVTInstitute() throws ClassNotFoundException, SQLException {
        Class.forName(driverName);
        String url = "jdbc:postgresql://localhost:5432/VTInstitute";
        return DriverManager.getConnection(url, user, password);
    }

    public static Connection getConnectionEmployees() throws ClassNotFoundException, SQLException {
        Class.forName(driverName);
        String url = "jdbc:postgresql://localhost:5432/employees";
        return DriverManager.getConnection(url, user, password);
    }

    public static Connection getConnectionArt() throws ClassNotFoundException, SQLException {
        Class.forName(driverName);
        String url = "jdbc:postgresql://localhost:5432/Art";
        return DriverManager.getConnection(url, user, password);
    }

}
