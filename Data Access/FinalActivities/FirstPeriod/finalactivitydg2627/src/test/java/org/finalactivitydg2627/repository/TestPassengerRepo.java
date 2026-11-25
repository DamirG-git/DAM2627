package org.finalactivitydg2627.repository;

import org.finalactivitydg2627.AbstractTest;
import org.finalactivitydg2627.database.DBConnection;
import org.finalactivitydg2627.model.Passengers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestPassengerRepo  extends AbstractTest {

    private PassengerRepo passengerRepo;
    private final TestData testData = new TestData();

    static class TestData{


        public final String passport1 = "TEST001";
        public final String name1 = "John";
        public final String surname1 = "Smith";
        public final String address1 = "Test Street 1";
        public final String phone1 = "111111111";

        public final String passport2 = "TEST002";
        public final String name2 = "Jane";
        public final String surname2 = "Doe";
        public final String address2 = "Test Street 2";
        public final String phone2 = "222222222";

        public final Passengers passenger1 = new Passengers(
                passport1, name1, surname1, address1, phone1
        );

        public final Passengers passenger2 = new Passengers(
                passport2, name2, surname2, address2, phone2
        );

    }

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
        var pass1 = testData.passenger1;
        var pass2 = testData.passenger2;

        var passport1 = testData.passport1;
        var passport2 = testData.passport2;

        var passengers = List.of(pass1, pass2);
        try {
            passengerRepo.addPassengers(passengers);

            try (var conn = DBConnection.getInstance().connect();
                 var stmt = conn.prepareStatement(
                         "SELECT COUNT(*) FROM passengers WHERE passportno IN (?, ?)")) {

                stmt.setString(1, passport1);
                stmt.setString(2, passport2);

                try (var rs = stmt.executeQuery()) {
                    assertTrue(rs.next());
                    assertEquals(2, rs.getInt(1));
                }
            }
        } finally {
            try (var conn = DBConnection.getInstance().connect();
                 var stmt = conn.prepareStatement(
                         "DELETE FROM passengers WHERE passportno IN (?, ?)")) {

                stmt.setString(1, passport1);
                stmt.setString(2, passport2);
                stmt.executeUpdate();
            }
        }
    }

    @Test
    void addPassengers_fail_duplicatedKey() throws SQLException {
        var pass1 = testData.passenger1;

        var passport1 = testData.passport1;

        var passengers = List.of(pass1, pass1);
        assertThrows(SQLException.class, () -> passengerRepo.addPassengers(passengers));
        try (var conn = DBConnection.getInstance().connect()) {
            try (var stmt = conn.prepareStatement("SELECT 1 FROM passengers p where p.passportno = ?")) {
                stmt.setString(1, passport1);
                try (var rs = stmt.executeQuery()) {
                    assertFalse(rs.next());
                }
            }
        }
    }

    @Test
    void addPassenger_fail_duplicateKeyNotChangingValue() throws SQLException {

        var pass1 = testData.passenger1;
        var pass2 = testData.passenger2;

        var passport1 = testData.passport1;
        var testInput = "TEST";

        var passengersStartInput = List.of(pass1);
        var result = passengerRepo.addPassengers(passengersStartInput);
        assertTrue(result>0);

        var passengers = List.of(pass2, pass2, new Passengers(passport1, testInput, testInput, testInput, testInput));
        assertThrows(SQLException.class, () -> passengerRepo.addPassengers(passengers));


        try (var conn = DBConnection.getInstance().connect()) {
            try (var stmt = conn.prepareStatement("SELECT * FROM passengers p where p.passportno = ?")) {
                stmt.setString(1, passport1);
                try (var rs = stmt.executeQuery()) {
                    assertTrue(rs.next());
                    assertEquals(passport1, rs.getString(1));
                    assertNotEquals(testInput, rs.getString(2));
                    assertNotEquals(testInput, rs.getString(3));
                }
            }
            finally {
                try (var stmt = conn.prepareStatement(
                             "DELETE FROM passengers WHERE passportno = ?")) {
                    stmt.setString(1, passport1);
                    stmt.executeUpdate();
                }
            }

        }
    }
}

