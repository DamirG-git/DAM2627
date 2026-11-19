package org.finalactivitydg2627.controller;

import org.finalactivitydg2627.dto.FlightDTO;
import org.finalactivitydg2627.model.Flights;
import org.finalactivitydg2627.service.FlightsService;
import org.finalactivitydg2627.utils.ApplicationLogger;
import org.finalactivitydg2627.utils.ApplicationValidator;
import org.finalactivitydg2627.utils.MessageFormatter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;

public class FlightsController extends AbstractController {

    private final FlightsService flightsService;

    public FlightsController(ApplicationLogger logger) {
        super(logger);
        flightsService = new FlightsService(logger);
    }

    public FlightsController(ApplicationLogger logger, FlightsService flightsService) {
        super(logger);
        this.flightsService = flightsService;
    }

    public static final String INFO_VALIDATING_FLIGHT_INPUT = "Validating input values: ";
    public static final String INFO_FETCHING_FLIGHTS = "Attempting to get flights!";
    public static final String INFO_FLIGHTS_RETRIEVED = "Successfully retrieved flights!\n";

    public static final String WARN_INPUT_VALIDATION_FAILED = "Input validation failed: \n";
    public static final String WARN_ORIGIN_AIRPORT_NOT_FOUND = "No origin airport found with IATA: ";
    public static final String WARN_DESTINATION_AIRPORT_NOT_FOUND = "No destination airport found with IATA: ";

    public static final String WARN_ORIGIN_OR_DESTINATION_NOT_FOUND_FORMAT_ORIGIN_DEST = "No origin IATA (%s) or destination IATA (%s) have been found in the database.";
    public static final String WARN_NO_FLIGHTS_FOUND_FORMAT_FORMAT_ORIGIN_DEST_DATE = "No flights found with these parameters: origin (%s), destination (%s), date (%s).";

    public static final String ERROR_FETCHING_FLIGHTS = "Error on fetching data: ";

    public void displayFlights(String[] args) {
        logger.log(INFO_VALIDATING_FLIGHT_INPUT + Arrays.toString(args), ApplicationLogger.LogLevel.INFO);
        var flight = new Flights();

        var origin = flight.setCodeSourceAirport(args[1]);
        var destination = flight.setCodeDestinationAirport(args[2]);
        var date = flight.setTravelDate(args[3]);


        var errors = MessageFormatter.multipleErrorsFormatter(origin, destination, date);
        if (!errors.isEmpty()) {
            logger.log(WARN_INPUT_VALIDATION_FAILED + errors, ApplicationLogger.LogLevel.WARNING);
            return;
        }
        logger.log(INFO_FETCHING_FLIGHTS, ApplicationLogger.LogLevel.INFO);
        fetchFlights(flight, destination, origin, date);


    }

    private void fetchFlights(Flights flight, ApplicationValidator.ResultValue<String> destination, ApplicationValidator.ResultValue<String> origin, ApplicationValidator.ResultValue<LocalDate> date) {
        try {
            var flightsRepoResult = flightsService.getFlights(flight.getCodeSourceAirport(),
                    flight.getCodeDestinationAirport(),
                    flight.getTravelDate());
            var code = flightsRepoResult.codeEnum();
            switch (code) {
                case NO_DESTINATION_AIRPORT_FOUND:
                    logger.log(WARN_DESTINATION_AIRPORT_NOT_FOUND + destination.result(), ApplicationLogger.LogLevel.WARNING);
                    return;
                case NO_ORIGIN_AIRPORT_FOUND:
                    logger.log(WARN_ORIGIN_AIRPORT_NOT_FOUND + origin.result(), ApplicationLogger.LogLevel.WARNING);
                    return;
                case NO_ORIGIN_OR_DESTINATION_FOUND:
                    logger.log(String.format(WARN_ORIGIN_OR_DESTINATION_NOT_FOUND_FORMAT_ORIGIN_DEST, origin.result(), destination.result()), ApplicationLogger.LogLevel.WARNING);
                    return;
                case SUCCESS:
                    if (flightsRepoResult.value().isEmpty()) {
                        logger.log(
                                String.format(
                                        WARN_NO_FLIGHTS_FOUND_FORMAT_FORMAT_ORIGIN_DEST_DATE, origin.result(), destination.result(), date.result()),
                                ApplicationLogger.LogLevel.WARNING);
                        return;
                    }
                    var flights = flightsRepoResult.value();
                    var stringBuilder = new StringBuilder();
                    stringBuilder.append(INFO_FLIGHTS_RETRIEVED);
                    for (var flightDto : flights) {
                        appendFlightMessage(flightDto, stringBuilder);
                    }
                    logger.log(stringBuilder.toString(), ApplicationLogger.LogLevel.INFO);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + code);
            }
        } catch (SQLException e) {
            logger.log(ERROR_FETCHING_FLIGHTS + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
        }
    }

    private void appendFlightMessage(FlightDTO flight, StringBuilder stringBuilder) {
        stringBuilder.append("  - ").append("Flight: ")
                .append(flight.getFlightCode()).append(" from airport ")
                .append(flight.getIataCode()).append(" in ")
                .append("(").append(flight.getCityName()).append(")")
                .append(" has ").append(flight.getFreeSeats()).append(" available seats out of ").append(flight.getTotalSeats()).append(" seats.\n");
    }
}
