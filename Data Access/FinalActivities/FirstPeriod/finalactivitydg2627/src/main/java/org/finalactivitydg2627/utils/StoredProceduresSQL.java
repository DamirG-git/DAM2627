package org.finalactivitydg2627.utils;

import org.finalactivitydg2627.database.DBConnection;

import java.sql.SQLException;

public class StoredProceduresSQL {

    private static final DBConnection connection = DBConnection.getInstance();


    private static final String SQL_FUNCTIONS = """
            DO $$ BEGIN
            
            DROP FUNCTION IF EXISTS dg2627_get_available_flights_on_date(int, int, date);
            
            CREATE OR REPLACE FUNCTION dg2627_get_available_flights_on_date(p_source varchar(3),p_destination varchar(3),p_date date
            )
            RETURNS TABLE(
                flight_code varchar,
                airport_iata varchar,
                city_name varchar,
                total_seats integer,
                free_seats bigint
            )
            LANGUAGE sql AS
            $BODY$
            SELECT f.flight_code, dst.iata, c.name, p.total_seats, COUNT(t.ticket_number)
            FROM flights f
            LEFT JOIN tickets t
                ON f.flight_code = t.flight_code
                AND t.date_of_travel = p_date
                AND t.date_of_cancellation IS NULL
            
            JOIN planes p
                ON f.plane = p.plane_id
            
            JOIN airports src
                ON f.source = src.code
            
            JOIN airports dst
                ON f.destination = dst.code
            
            JOIN cities c
                ON dst.city = c.code
            
            WHERE src.iata = p_source
                AND dst.iata = p_destination
            
            GROUP BY f.flight_code, dst.iata, c.name, p.total_seats;
            $BODY$;
            END;
            $$
            """;
    public void initialize() throws SQLException {
        try (var conn = connection.connect();
             var prepStFunctions = conn.prepareStatement(SQL_FUNCTIONS);
        ) {
            prepStFunctions.execute();
        }
    }


}
