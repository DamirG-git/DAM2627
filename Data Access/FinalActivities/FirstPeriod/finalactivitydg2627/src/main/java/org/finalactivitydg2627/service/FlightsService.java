package org.finalactivitydg2627.service;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.dto.FlightDTO;
import org.finalactivitydg2627.repository.FlightsRepo;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class FlightsService {

    private final ApplicationLogger logger;
    private final FlightsRepo flightsRepo;

    public FlightsService() {
        logger = ApplicationLogger.getInstance();
        flightsRepo = new FlightsRepo();
    }

    public Optional<List<FlightDTO>> getFlights(int origin, int destination, LocalDate date) {
        try {
            return flightsRepo.getFlights(origin,destination,date);
        } catch (SQLException e) {
            logger.log("Error on fetching data:"+e.getMessage(), ApplicationLogger.LogLevel.ERROR);
            return Optional.empty();
        }
    }
}
