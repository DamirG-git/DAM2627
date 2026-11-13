package org.finalactivitydg2627.repository;

import org.finalactivitydg2627.database.DBConnection;
import org.finalactivitydg2627.dto.AirportDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AirportRepository {

    private final DBConnection connection;

    public AirportRepository() {
        this.connection = DBConnection.getInstance();
    }

    public List<AirportDTO> getOriginAirports() throws SQLException {
        var airportsDtoList = new ArrayList<AirportDTO>();
        var sqlOriginsQuery = """
                SELECT DISTINCT a.iata, a.name,c.name, f.flight_code
                FROM airports a
                    join flights f
                    on a.code = f.source
                    join cities c
                    on a.city = c.code
                ORDER BY a.iata, a.name, c.name, f.flight_code;
                """;
        try (var conn = connection.connect()) {
            conn.setAutoCommit(false);
            try (var ps = conn.prepareStatement(sqlOriginsQuery)) {
                try (var result = ps.executeQuery()) {
                    while (result.next()) {
                        airportsDtoList.add(new AirportDTO(result.getString(1), result.getString(2), result.getString(3),result.getString(4)));
                    }
                    return airportsDtoList;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }

    }

    public List<AirportDTO> getDestinationAirports(String code) throws SQLException {
        var airportsDtoList = new ArrayList<AirportDTO>();
        var sqlDestinationsQuery = """
        SELECT DISTINCT dest.name, dest.iata, c.name, f.flight_code
            FROM flights f
            JOIN airports asource
                ON f.source = asource.code
            JOIN airports dest
                ON f.destination = dest.code
            JOIN cities c
                ON dest.city = c.code
            WHERE asource.iata = ?
        ORDER BY dest.iata, dest.name, c.name, f.flight_code;
    """;
        try (var conn = connection.connect()) {
            try (var ps = conn.prepareStatement(sqlDestinationsQuery)) {
                ps.setString(1, code);
                try (var result = ps.executeQuery()) {
                    while (result.next()) {
                        airportsDtoList.add(
                                new AirportDTO(result.getString(1),
                                        result.getString(2),
                                        result.getString(3),
                                        result.getString(4)));
                    }
                    return airportsDtoList;
                }
            }
        }
    }
}

