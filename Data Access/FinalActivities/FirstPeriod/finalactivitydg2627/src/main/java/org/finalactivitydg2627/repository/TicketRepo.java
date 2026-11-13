package org.finalactivitydg2627.repository;

import org.finalactivitydg2627.database.DBConnection;

import java.sql.SQLException;
import java.time.LocalDate;

public class TicketRepo {


    private final DBConnection connection;
    public TicketRepo() {
        connection = DBConnection.getInstance();
    }




    private final String sqlTicketOnSameDate = """
            SELECT EXISTS(
                    SELECT 1 from tickets t
                             where t.passportno = ?
                             and t.date_of_travel = ?
                             and t.date_of_cancellation is NULL
                );
            """;
    public void buyTicket(String passno, String flight_code, LocalDate date) throws SQLException {
        try(var conn = connection.connect(); var pstTicketOnSameDate = conn.prepareStatement(sqlTicketOnSameDate)){




        }
    }
}
