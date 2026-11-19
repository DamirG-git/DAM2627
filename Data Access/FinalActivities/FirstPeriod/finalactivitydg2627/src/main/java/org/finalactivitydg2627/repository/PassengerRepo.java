package org.finalactivitydg2627.repository;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.database.DBConnection;
import org.finalactivitydg2627.model.Passengers;

import java.sql.SQLException;
import java.util.List;

public class PassengerRepo extends AbstractRepo {

    public PassengerRepo() {}
    public PassengerRepo(DBConnection connection, ApplicationLogger logger) {
        super(connection,logger);
    }

    public Integer addPassengers(List<Passengers> input) throws SQLException {
        if (input == null || input.isEmpty()) {
            return null;
        }
        try (var conn = connection.connect();
             var psAddPassenger = conn.prepareStatement("INSERT INTO passengers(passportno,firstname,lastname,address,phone) values(?,?,?,?,?)")) {
            conn.setAutoCommit(false);
            try {
                for (Passengers p : input) {
                    psAddPassenger.setString(1, p.getPassno());
                    psAddPassenger.setString(2, p.getFirstName());
                    psAddPassenger.setString(3, p.getLastName());
                    psAddPassenger.setString(4, p.getAddress());
                    psAddPassenger.setString(5, p.getPhone());
                    psAddPassenger.addBatch();
                }
                var execution = psAddPassenger.executeBatch();
                conn.commit();
                return execution.length;
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
