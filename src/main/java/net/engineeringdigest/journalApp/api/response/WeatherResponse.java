package net.engineeringdigest.journalApp.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WeatherResponse {

    private Current current;


    @Setter
    @Getter
    public class Current{
        @JsonProperty("observation_time")
        private String observationTime;
        private int temperature;
        @JsonProperty("weather_code")
        private int weatherCode;
        @JsonProperty("weather_icons")
        private List<String> weatherIcons;
        @JsonProperty("weather_descriptions")
        private List<String> weatherDescriptions;

        private int feelslike;

    }

}



