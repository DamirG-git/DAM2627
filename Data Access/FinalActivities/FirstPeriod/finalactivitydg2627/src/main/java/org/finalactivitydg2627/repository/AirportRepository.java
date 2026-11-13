package org.finalactivitydg2627.repository;

import org.finalactivitydg2627.database.DBConnection;
import org.finalactivitydg2627.dto.AirportDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AirportRepository {

    DBConnection connection;

    public AirportRepository() {
        this.connection = DBConnection.getInstance();
    }

    public List<AirportDTO> getOriginAirports() throws SQLException {
        var airportsDtoList = new ArrayList<AirportDTO>();
        var sqlOriginsQuery = """
                SELECT DISTINCT a.name, a.code, f.flight_code FROM airports a join flights f on a.code = f.source ORDER BY a.code;
                """;
        try (var conn = connection.connect()){
            conn.setAutoCommit(false);
            try(var psGetOrigins = conn.prepareStatement(sqlOriginsQuery)){
                try(var result = psGetOrigins.executeQuery()){
                    while (result.next()) {
                        airportsDtoList.add(new AirportDTO(result.getString(1), result.getInt(2), result.getString(3)));
                    }
                    return airportsDtoList;
                }
            }catch (SQLException e){
                conn.rollback();
                return Collections.emptyList();
            }

        }

    }

}
