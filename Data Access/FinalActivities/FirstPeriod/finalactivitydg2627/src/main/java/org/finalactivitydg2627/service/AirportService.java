package org.finalactivitydg2627.service;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.dto.AirportDTO;
import org.finalactivitydg2627.repository.AirportRepo;

import java.sql.SQLException;
import java.util.List;

public class AirportService {

    private final ApplicationLogger logger;
    private final AirportRepo airportRepo;

    public AirportService() {
        logger = ApplicationLogger.getInstance();
        airportRepo = new AirportRepo();
    }

    public AirportService(ApplicationLogger logger, AirportRepo airportRepo) {
        this.logger = logger;
        this.airportRepo = airportRepo;
    }

    public List<AirportDTO> getOriginAirports() throws SQLException {
        logger.log("Getting all airports that function as origin in flights...", ApplicationLogger.LogLevel.INFO);
        return airportRepo.getOriginAirports();

    }

    public List<AirportDTO> getDestinationAirports(String code) throws SQLException {
        logger.log("Getting all airports that are destinations for this origin: " + code, ApplicationLogger.LogLevel.INFO);
        return airportRepo.getDestinationAirports(code);


    }
}
