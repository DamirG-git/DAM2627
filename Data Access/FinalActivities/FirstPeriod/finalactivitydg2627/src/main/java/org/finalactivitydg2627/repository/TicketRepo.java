package org.finalactivitydg2627.repository;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.database.DBConnection;

import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

public class TicketRepo extends AbstractRepo {


    public TicketRepo() {
    }

    public TicketRepo(DBConnection connection, ApplicationLogger logger) {
        super(connection, logger);
    }

    private static final String SQL_PASSENGER_EXISTS = " SELECT 1 FROM passengers p where p.passportno = ?";

    private static final String SQL_TICKET_ON_SAME_DATE = """
            SELECT EXISTS(
                    SELECT 1 from tickets t
                             where t.passportno = ?
                             and t.date_of_travel = ?
                             and t.date_of_cancellation is NULL
                );
            """;
    private static final String SQL_TICKET_ADD_NEW_TICKET = """
                    INSERT INTO tickets (passportno, date_of_travel,date_of_booking, date_of_cancellation, flight_code, price)
                    values (?, ?, now(),NULL, ?, ?)
            """;
    private static final String SQL_FLIGHT_HAS_FREE_SEATS = """
                    SELECT p.total_seats-COUNT(t.ticket_number) AS free_seats
                    FROM flights f join planes p
                        on f.plane = p.plane_id
            
                        LEFT JOIN tickets t
                            ON f.flight_code = t.flight_code
                            and t.date_of_travel = ?
                                   and t.date_of_cancellation is NULL
                    WHERE f.flight_code = ?
                    group by p.total_seats;
            """;

    public enum TicketPurchaseResult {
        SUCCESS,
        PASSENGER_ALREADY_HAS_TICKET_ON_SAME_DATE,
        NO_SEATS_AVAILABLE,
        FLIGHT_NOT_FOUND,
        FAILED_TO_CREATE_NEW_TICKET,
        PASSENGER_DOES_NOT_EXIST
    }


    public TicketPurchaseResult buyTicket(String passno, String flightCode, LocalDate date) throws SQLException {
        try (var conn = connection.connect()) {
            conn.setAutoCommit(false);
            try (var stmt = conn.prepareStatement(SQL_PASSENGER_EXISTS)) {
                stmt.setString(1, passno);
                try (var rs = stmt.executeQuery()) {
                    if (!rs.next()) return TicketPurchaseResult.PASSENGER_DOES_NOT_EXIST;
                }
            }

            try (var pstTicketOnSameDate = conn.prepareStatement(SQL_TICKET_ON_SAME_DATE);
            ) {

                pstTicketOnSameDate.setString(1, passno);
                pstTicketOnSameDate.setObject(2, date);

                try (var hasTicketOnSameDate = pstTicketOnSameDate.executeQuery();
                     var pstFlightHasFreeSeats = conn.prepareStatement(SQL_FLIGHT_HAS_FREE_SEATS);) {
                    if (hasTicketOnSameDate.next() && hasTicketOnSameDate.getBoolean(1)) {
                        conn.rollback();
                        return TicketPurchaseResult.PASSENGER_ALREADY_HAS_TICKET_ON_SAME_DATE;
                    }
                    pstFlightHasFreeSeats.setObject(1, date);
                    pstFlightHasFreeSeats.setString(2, flightCode);

                    try (var stmtFreeSeats = pstFlightHasFreeSeats.executeQuery();
                         var pstTicketAddNewTicket = conn.prepareStatement(SQL_TICKET_ADD_NEW_TICKET);) {
                        if (stmtFreeSeats.next()) {
                            if (stmtFreeSeats.getLong(1) <= 0) {
                                conn.rollback();
                                return TicketPurchaseResult.NO_SEATS_AVAILABLE;
                            }
                        } else {
                            conn.rollback();
                            return TicketPurchaseResult.FLIGHT_NOT_FOUND;
                        }

                        pstTicketAddNewTicket.setString(1, passno);
                        pstTicketAddNewTicket.setObject(2, date);
                        pstTicketAddNewTicket.setString(3, flightCode);
                        pstTicketAddNewTicket.setNull(4, Types.NUMERIC);

                        var rows = pstTicketAddNewTicket.executeUpdate();
                        if (rows > 0) {
                            conn.commit();
                            return TicketPurchaseResult.SUCCESS;
                        }
                        conn.rollback();
                        return TicketPurchaseResult.FAILED_TO_CREATE_NEW_TICKET;
                    }


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
