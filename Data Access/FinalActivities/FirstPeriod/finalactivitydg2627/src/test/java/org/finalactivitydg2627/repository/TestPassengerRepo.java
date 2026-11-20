package org.finalactivitydg2627.repository;

import org.finalactivitydg2627.AbstractTest;
import org.finalactivitydg2627.database.DBConnection;
import org.finalactivitydg2627.model.Passengers;
import org.finalactivitydg2627.utils.ApplicationLogger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class TestPassengerRepo  extends AbstractTest {

    private PassengerRepo passengerRepo;


    private static final Passengers test1 = new Passengers("TEST001", "John", "Smith",
            "Test Street 1", "111111111");
    private static final Passengers test2 = new Passengers("TEST002", "Jane", "Doe",
            "Test Street 2", "222222222");

    @BeforeAll
    static void verifyTestDatabase() throws SQLException {
        try (var conn = DBConnection.getInstance().connect()) {
            assertEquals("flights2627_test", conn.getCatalog());
        }
    }

    @BeforeEach
    void setUp() {
        var connection = DBConnection.getInstance();
        passengerRepo = new PassengerRepo(connection, logger);
    }

    @Test
    void addPassengers_success() throws SQLException {
        var passengers = List.of(test1, test2);
        try {
            passengerRepo.addPassengers(passengers);

            try (var conn = DBConnection.getInstance().connect();
                 var stmt = conn.prepareStatement(
                         "SELECT COUNT(*) FROM passengers WHERE passportno IN (?, ?)")) {

                stmt.setString(1, "TEST001");
                stmt.setString(2, "TEST002");

                try (var rs = stmt.executeQuery()) {
                    assertTrue(rs.next());
                    assertEquals(2, rs.getInt(1));
                }
            }
        } finally {
            try (var conn = DBConnection.getInstance().connect();
                 var stmt = conn.prepareStatement(
                         "DELETE FROM passengers WHERE passportno IN (?, ?)")) {

                stmt.setString(1, "TEST001");
                stmt.setString(2, "TEST002");
                stmt.executeUpdate();
            }
        }
    }

    @Test
    void addPassengers_fail_duplicatedKey() throws SQLException {
        var passengers = List.of(test1, test1);
        assertThrows(SQLException.class, () -> passengerRepo.addPassengers(passengers));
        try (var conn = DBConnection.getInstance().connect()) {
            try (var stmt = conn.prepareStatement("SELECT 1 FROM passengers p where p.passportno = ?")) {
                stmt.setString(1, "TEST001");
                try (var rs = stmt.executeQuery()) {
                    assertFalse(rs.next());
                }
            }
        }
    }

    @Test
    void addPassenger_fail_duplicateKeyNotChangingValue() throws SQLException {
        var passengers = List.of(test1, test2, new Passengers("X9324666", "TEST", "TEST", "TEST", "TEST"));
        assertThrows(SQLException.class, () -> passengerRepo.addPassengers(passengers));
        try (var conn = DBConnection.getInstance().connect()) {
            try (var stmt = conn.prepareStatement("SELECT * FROM passengers p where p.passportno = ?")) {
                stmt.setString(1, "X9324666");
                try (var rs = stmt.executeQuery()) {
                    assertTrue(rs.next());
                    assertEquals("X9324666", rs.getString(1));
                    assertNotEquals("TEST", rs.getString(2));
                    assertNotEquals("TEST", rs.getString(3));
                }
            }
        }
    }
}

