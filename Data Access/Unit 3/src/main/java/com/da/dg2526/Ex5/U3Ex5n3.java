package com.da.dg2526.Ex5;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class U3Ex5n3 {

    // this is from 4.1 to 5.3
    public U3Ex5n3() {
        try {
            int debugValue = 3;
            switch (debugValue) {
                case 1:
                    callDb();
                    break;
                case 2:
                    addLanguages();
                    break;
                case 3:
                    addHours();
                    break;

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void callDb() {

        try (var con = getConnection(); var statement = con.createStatement();) {

            String SQLsentence = "SELECT * FROM subjects ORDER BY code";
            ResultSet rs = statement.executeQuery(SQLsentence);
            System.out.println("Code" + "\t" + "Name" + "\t" + "Year");
            System.out.println("------------------------------------------");
            while (rs.next()) {
                System.out.println(rs.getString(1) + "\t " + rs.getString(2) + "\t " + rs.getString(3));
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }


    }

    public void addLanguages() throws ClassNotFoundException, SQLException {
        try (var con = getConnection(); var statement = con.createStatement();) {

            String SQLsentence = "INSERT INTO subjects (name,year) VALUES('MARKUP LANGUAGES', 1)";
            var updated = statement.executeUpdate(SQLsentence);
            System.out.println("Rows affected: " + updated);
        }
    }

    public void addHours() throws ClassNotFoundException, SQLException {
        try (var con = getConnection(); var statement = con.createStatement();) {
            var SQLsentence = "ALTER TABLE subjects ADD COLUMN hours INTEGER";
            var updated = statement.executeUpdate(SQLsentence);
            System.out.println("Columns affected: " + updated);
        }
    }


    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        String url = "jdbc:postgresql://localhost:5432/VTInstitute";
        String user = "postgres";
        String password = "damir";
        return DriverManager.getConnection(url, user, password);
    }
}
