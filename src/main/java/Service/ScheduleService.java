package Service;

import Exceptions.DataException;
import POJO.Flight;
import POJO.Forecast;

import java.util.List;
import java.util.Map;

import static Constants.Constants.*;

public class ScheduleService {

    /**
     * Печать расписания с информацией о возможности вылета. **/
    public void printSchedule(List<Flight> flights, Map<String, List<Forecast>> forecasts) {
        StringBuilder result = new StringBuilder();
        for (Flight flight : flights) {
            String number = flight.getNo();
            String from = flight.getFrom();
            String to = flight.getTo();
            Short departure = flight.getDeparture();
            Short duration = flight.getDuration();
            int arriver = calculateArriverTime(from, to, departure, duration);

            result.append(number).append(" | ").append(from).append(" -> ").append(to).append(" | ");

            if (arriver >= DAY_START && arriver <= DAY_END) {
                if (isFlightPossible(forecasts, from, to, departure, arriver)) {
                    result.append(BY_TIME);
                } else {
                    result.append(CANCELED);
                }
                System.out.println(result.toString().toUpperCase());
            } else {
                throw new DataException("Ошибка во времени прилета.");
            }
            result.delete(0, result.length());
        }
    }


    /**Возможно ли выполнить перелет.**/
    private boolean isFlightPossible(
            Map<String, List<Forecast>> forecasts, String from, String to, Short departure, int arriver) {
        Forecast fromForecast = forecasts.get(from).get(departure);
        Forecast toForecast = forecasts.get(to).get(arriver);
        if (isWindy(fromForecast) || isWindy(toForecast) || isVisibility(fromForecast) || isVisibility(toForecast)) {
            return false;
        } else {
            return true;
        }
    }

    /**Проверка скорости ветра.**/
    private boolean isWindy(Forecast forecast) {
        return forecast.getWind() > 30;
    }

    /**
     * Проверка видимости.**/
    private boolean isVisibility(Forecast forecast) {
        return forecast.getVisibility() < 200;
    }

    /**
     * Метод вычисляет МЕСТНОЕ время прилета, опираясь на город вылета и город назначения.
     * Возвращает время прилета в городе назначения. В случае отсутствия города вернет -1.
    **/
    private static int calculateArriverTime(String from, String to, Short departure, Short duration) {
        int arriver = departure + duration;
        if (from.equals("moscow")) {
            arriver = arriver - MOSCOW_TIMEZONE;
            switch (to) {
                case "omsk": return arriver  + OMSK_TIMEZONE;
                case "novosibirsk": return arriver  + NOVOSIBIRSK_TIMEZONE;
                default: return -1;
            }
        } else if (from.equals("omsk")) {
            arriver = arriver - OMSK_TIMEZONE;
            switch (to) {
                case "moscow": return arriver  + MOSCOW_TIMEZONE;
                case "novosibirsk": return arriver  + NOVOSIBIRSK_TIMEZONE;
                default: return -1;
            }
        } else if (from.equals("novosibirsk")) {
            arriver = arriver - NOVOSIBIRSK_TIMEZONE;
            switch (to) {
                case "moscow": return arriver  + MOSCOW_TIMEZONE;
                case "omsk": return arriver  + OMSK_TIMEZONE;
                default: return -1;
            }
        } else {
            return -1;
        }
    }

}
