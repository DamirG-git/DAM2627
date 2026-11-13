package org.finalactivitydg2627.repository;

import org.finalactivitydg2627.database.DBConnection;
import org.finalactivitydg2627.dto.AirportDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AirportRepository {

    private final DBConnection connection;

    public AirportRepository() {
        this.connection = DBConnection.getInstance();
    }

    public List<AirportDTO> getOriginAirports() throws SQLException {
        var airportsDtoList = new ArrayList<AirportDTO>();
        var sqlOriginsQuery = """
                SELECT DISTINCT a.name, a.code, f.flight_code
                FROM airports a
                    join flights f
                    on a.code = f.source
                ORDER BY a.code;
                """;
        try (var conn = connection.connect()) {
            conn.setAutoCommit(false);
            try (var ps = conn.prepareStatement(sqlOriginsQuery)) {
                try (var result = ps.executeQuery()) {
                    while (result.next()) {
                        airportsDtoList.add(new AirportDTO(result.getString(1), result.getInt(2), result.getString(3)));
                    }
                    return airportsDtoList;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }

    }

    public List<AirportDTO> getDestinationAirports(int code) throws SQLException {
        var airportsDtoList = new ArrayList<AirportDTO>();
        var sqlDestinationsQuery = """
        SELECT DISTINCT a.name, a.code, f.flight_code
            FROM flights f
            JOIN airports a
                ON f.destination = a.code
            WHERE f.source = ?
        ORDER BY a.code;
        """;
        try (var conn = connection.connect()) {
            conn.setAutoCommit(false);
            try (var ps = conn.prepareStatement(sqlDestinationsQuery)) {
                ps.setInt(1, code);
                try (var result = ps.executeQuery()) {
                    while (result.next()) {
                        airportsDtoList.add(new AirportDTO(result.getString(1), result.getInt(2), result.getString(3)));
                    }
                    return airportsDtoList;
                }
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }
                throw e;
            }
        }
    }
}

