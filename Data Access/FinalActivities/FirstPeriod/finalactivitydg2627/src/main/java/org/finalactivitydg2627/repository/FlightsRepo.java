package org.finalactivitydg2627.repository;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.dto.FlightDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FlightsRepo extends AbstractRepo {
    private static final String GET_FLIGHTS_FUNC = """
            SELECT flight_code, airport_iata ,city_name ,total_seats, free_seats
            FROM dg2627_get_available_flights_on_date(?,?,?)
            """;
    private static final String DEBUG_GETTING_FLIGHTS = "Trying to get flight from %s -> %s on %s";

    public enum AirportResult {
        NO_ORIGIN_AIRPORT_FOUND,
        NO_DESTINATION_AIRPORT_FOUND,
        NO_ORIGIN_OR_DESTINATION_FOUND,
        SUCCESS,
    }

    public RepoResultValue<List<FlightDTO>, AirportResult> getFlights(String origin, String destination, LocalDate date) throws SQLException {
        List<FlightDTO> flights = new ArrayList<>();
        try (var conn = connection.connect()) {
            //RETURNS TABLE(flight_code varchar, total_seats integer, free_seats bigint
            logger.log(String.format(
                            DEBUG_GETTING_FLIGHTS, origin, destination, date.toString()),
                    ApplicationLogger.LogLevel.DEBUG);

            var exists = validateAirports(conn, origin, destination);
            if (exists == AirportResult.SUCCESS) {
                try (var stmt = conn.prepareStatement(GET_FLIGHTS_FUNC)) {
                    stmt.setString(1, origin);
                    stmt.setString(2, destination);
                    stmt.setObject(3, date);
                    try (var result = stmt.executeQuery()) {
                        while (result.next()) {
                            flights.add(
                                    new FlightDTO(
                                            result.getString(1),
                                            result.getString(2),
                                            result.getString(3),
                                            result.getInt(4),
                                            result.getLong(5)));
                        }
                    }
                    return new RepoResultValue<>(flights, AirportResult.SUCCESS);
                }
            }
            return new RepoResultValue<>(null, exists);
        }
    }


    private AirportResult validateAirports(Connection conn, String origin, String destination) throws SQLException {


        var foundOrigin = true;
        var foundDestination = true;
        try (var stsm = conn.prepareStatement("SELECT 1 FROM airports a where a.iata = ?")) {
            stsm.setString(1, origin);
            try (var rs = stsm.executeQuery()) {
                if (!rs.next()) foundOrigin = false;

            }
            stsm.setString(1, destination);
            try (var rs = stsm.executeQuery()) {
                if (!rs.next()) foundDestination = false;

            }

            if (!foundOrigin && !foundDestination)
                return AirportResult.NO_ORIGIN_OR_DESTINATION_FOUND;

            if (!foundOrigin)
                return AirportResult.NO_ORIGIN_AIRPORT_FOUND;

            if (!foundDestination)
                return AirportResult.NO_DESTINATION_AIRPORT_FOUND;

            return AirportResult.SUCCESS;
        }


    }
}
