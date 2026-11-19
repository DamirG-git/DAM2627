package org.finalactivitydg2627.service;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.dto.FlightDTO;
import org.finalactivitydg2627.repository.AbstractRepo;
import org.finalactivitydg2627.repository.FlightsRepo;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class FlightsService extends AbstractService {

    private final FlightsRepo flightsRepo;



    public FlightsService() {
        flightsRepo = new FlightsRepo();
    }

    public FlightsService(ApplicationLogger logger, FlightsRepo flightsRepo) {
        super(logger);
        this.flightsRepo = flightsRepo;
    }

    public AbstractRepo.RepoResultValue<List<FlightDTO>, FlightsRepo.AirportResult> getFlights(String origin, String destination, LocalDate date) throws SQLException {
            return flightsRepo.getFlights(origin,destination,date);
    }
}
