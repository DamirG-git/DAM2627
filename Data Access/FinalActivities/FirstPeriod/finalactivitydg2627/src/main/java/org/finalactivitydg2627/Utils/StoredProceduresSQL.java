package org.finalactivitydg2627.Utils;

import org.finalactivitydg2627.database.DBConnection;

import java.sql.SQLException;
import java.util.logging.Logger;

public class StoredProceduresSQL {


    private static final Logger logger = Logger.getLogger(StoredProceduresSQL.class.getName());
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
            SELECT f.flight_code, dst.iata, c.name, p.total_seats, p.total_seats - COUNT(t.ticket_number)
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
            
            
            CREATE OR REPLACE FUNCTION dg2627_get_seats_available_for_booking(p_flightno varchar(10), p_date date)
                RETURNS BIGINT
            LANGUAGE sql AS
            $BODY$
            
            SELECT p.total_seats-COUNT(t.ticket_number) AS free_seats
            FROM flights f join planes p
                on f.plane = p.plane_id
            
                LEFT JOIN tickets t
                    ON f.flight_code = t.flight_code
                    and t.date_of_travel = p_date
                           and t.date_of_cancellation is NULL
            WHERE f.flight_code = p_flightno
            group by p.total_seats;
            $BODY$;
            
            
            CREATE OR REPLACE FUNCTION dg2627_user_has_ticket_on_same_date(p_passno varchar(10), p_date date)
                RETURNS BOOLEAN
            LANGUAGE sql AS
            $BODY$
            
            SELECT EXISTS(
                SELECT 1
                FROM tickets t
                WHERE t.passportno = p_passno
                AND t.date_of_travel = p_date
                AND t.date_of_cancellation is NULL
            )
            $BODY$;
            END;
            $$
            """;

    //var prepStIndexes = conn.prepareStatement(SQL_INDEXES) // Invalid
    // prepStIndexes.execute();
    /*private static final String SQL_INDEXES = """
            DO $$ BEGIN
            IF NOT EXISTS(
                SELECT 1
                FROM pg_indexes
                WHERE indexname = 'unique_passenger_travel_date'
            )THEN
                CREATE UNIQUE INDEX unique_passenger_travel_date
                ON tickets (passportno, date_of_travel)
                WHERE date_of_cancellation IS NULL;
            end if;
            end;
            $$
            """;
            */

    public void initialize() throws SQLException {
        try (var conn = connection.connect();
             var prepStFunctions = conn.prepareStatement(SQL_FUNCTIONS);
        ) {
            prepStFunctions.execute();
        }
    }


}
