package Service;

import Exceptions.DataException;
import POJO.Flight;
import POJO.Forecast;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static Constants.Constants.JSON_PATCH;


public class DataUnpackingService {

    /**
     * Парсинг JSON.**/
    public void unpackingData(List<Flight> flights, Map<String, List<Forecast>> forecasts) {
        File file = new File(JSON_PATCH);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        try {
            JsonNode data = objectMapper.readTree(file);
            getFlights(flights, objectMapper, data);
            getForecasts(forecasts, objectMapper, data);
        } catch (IOException e) {
            throw new DataException("Не удалось прочитать JSON.");
        }
    }

    /**Парсинг погодных условий.**/
    private void getForecasts(Map<String, List<Forecast>> forecasts, ObjectMapper objectMapper, JsonNode data) throws JsonProcessingException {
        JsonNode forecastNode =  data.get("forecast");
        Iterator<String> city = forecastNode.fieldNames();
        for (JsonNode element : forecastNode) {
            List<Forecast> weather = new ArrayList<>();
            for (JsonNode wea: element) {
                Forecast forecast = objectMapper.treeToValue(wea, Forecast.class);
                weather.add(forecast);
            }
            forecasts.put(city.next(), weather);
        }
    }

    /**Парсинг перелётов.**/
    private void getFlights(List<Flight> flights, ObjectMapper objectMapper, JsonNode data) throws JsonProcessingException {
        JsonNode flightNode =  data.get("flights");
        for (JsonNode element : flightNode) {
            Flight flight = objectMapper.treeToValue(element, Flight.class);
            flights.add(flight);
        }
    }
}
