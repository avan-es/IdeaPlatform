import POJO.Flight;
import POJO.Forecast;
import Service.ScheduleService;
import Service.DataUnpackingService;

import java.util.*;

public class Application {

    public static void main(String[] args) {
        DataUnpackingService dataUnpackingService = new DataUnpackingService();
        ScheduleService scheduleService = new ScheduleService();
        List<Flight> flights = new ArrayList<>();
        Map<String, List<Forecast>> forecasts = new HashMap<>();

        dataUnpackingService.unpackingData(flights, forecasts);
        scheduleService.printSchedule(flights, forecasts);
    }

}