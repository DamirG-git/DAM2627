package org.finalactivitydg2627.service;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.dto.AirportDTO;
import org.finalactivitydg2627.repository.AirportRepository;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class AirportService {

    private final ApplicationLogger logger;
    private final AirportRepository airportRepository;

    public AirportService() {
        logger = ApplicationLogger.getInstance();
        airportRepository = new AirportRepository();
    }

    public List<AirportDTO> getOriginAirports() {
        logger.log("Getting all airports that function as origin in flights...", ApplicationLogger.LogLevel.INFO);
        try {
            return airportRepository.getOriginAirports();
        } catch (SQLException e) {
            logger.log("Error obtaining airports: " + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
            return Collections.emptyList();
        }
    }
}
