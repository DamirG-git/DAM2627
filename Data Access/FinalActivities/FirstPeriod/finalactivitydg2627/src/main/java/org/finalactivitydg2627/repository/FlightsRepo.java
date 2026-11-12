package org.finalactivitydg2627.repository;

import org.finalactivitydg2627.database.DBConnection;
import org.finalactivitydg2627.dto.FlightDTO;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FlightsRepo {

    private final DBConnection connection;

    public FlightsRepo() {
        connection = DBConnection.getInstance();
    }


    public Optional<List<FlightDTO>> getFlights(int origin, int destination, LocalDate date) throws SQLException {
        List<FlightDTO> flights = new ArrayList<>();
        try (var conn = connection.connect()) {
            conn.setAutoCommit(false);

            //RETURNS TABLE(flight_code varchar, total_seats integer, free_seats bigint
            var getFlightsQuery = "SELECT flight_code, total_seats, free_seats " +
                    "FROM dg2627_get_available_flights_on_date(?,?,?)";
            try (var stmt = conn.prepareStatement(getFlightsQuery)) {
                stmt.setInt(1, origin);
                stmt.setInt(2, destination);
                stmt.setDate(3, Date.valueOf(date));
                try (var result = stmt.executeQuery()) {
                    while (result.next()) {
                        flights.add(new FlightDTO(result.getString(1), result.getInt(2), result.getLong(3)));
                    }
                }
                conn.commit();
                return Optional.of(flights);
            } catch (SQLException e) {
                conn.rollback();
                flights.clear();
                return Optional.empty();
            }
        }
    }


}
