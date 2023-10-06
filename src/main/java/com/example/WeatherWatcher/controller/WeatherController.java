package com.example.WeatherWatcher.controller;

import com.example.WeatherWatcher.model.Weather;
import com.example.WeatherWatcher.service.RepositoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.List;

@Controller
public class WeatherController {
    RepositoryService repositoryService;

    public WeatherController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    private ObjectMapper objectMapper;
    private Calendar calendar = Calendar.getInstance();
    private final String apiKey = "NfJtCrGev4K61MSy6IbZ6uhUb9qwJLAq";
    private final String getLocationKey = "http://dataservice.accuweather.com/locations/v1/cities/autocomplete?apikey=NfJtCrGev4K61MSy6IbZ6uhUb9qwJLAq&q=";
    private final String getLocationWeather = "http://dataservice.accuweather.com/currentconditions/v1/%s?apikey=NfJtCrGev4K61MSy6IbZ6uhUb9qwJLAq&details=true";

    private RestTemplate restTemplate;

    {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @GetMapping("/")
    public String home(Model model) {
        try {
            JsonNode keyStr = objectMapper.readTree(restTemplate.getForEntity(getLocationKey.concat("Yerevan"), String.class).getBody()).get(0);
            String keyValue = keyStr.get("Key").textValue();
            JsonNode weather = objectMapper.readTree(restTemplate.getForEntity(getLocationWeather.formatted(keyValue), String.class).getBody()).get(0);
            String weatherDescription = weather.get("WeatherText").textValue();
            String measureValue = weather.get("Temperature").get("Metric").get("Value").asText().concat("°");
            model.addAttribute("weather_description", measureValue.concat(" ").concat(weatherDescription));
            model.addAttribute("current_weather", "Yerevan, Armenia ");

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return "startWindow";
    }


    @PostMapping("/weather")
    public String processUserInput(@RequestBody String userInput, Model model) {
        setModelValues(userInput.substring(5), model);
        return "weatherInfo";
    }

    private void setModelValues(String userInput, Model model) {
        try {
            JsonNode keyStr = objectMapper.readTree(restTemplate.getForEntity(getLocationKey.concat(userInput), String.class).getBody()).get(0);
            String keyValue = keyStr.get("Key").textValue();
            JsonNode weather = objectMapper.readTree(restTemplate.getForEntity(getLocationWeather.formatted(keyValue), String.class).getBody()).get(0);
            String weatherDescription = weather.get("WeatherText").textValue();
            String localizedName = keyStr.get("LocalizedName").textValue().concat(", ").concat(keyStr.get("Country").get("LocalizedName").textValue());
            String measureValue = weather.get("Temperature").get("Metric").get("Value").asText().concat("°");
            model.addAttribute("weather_description", weatherDescription);
            model.addAttribute("location", localizedName);
            model.addAttribute("measure_value", measureValue);
            repositoryService.saveLog(new Weather(localizedName, weatherDescription, calendar.getTime().toString(), measureValue));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(fixedRate = 30 * 60000)
    public void scheduleFixedRateTask() {
        restTemplate.getForEntity("http://localhost:9090/", String.class);
    }


    @GetMapping("/error")
    public void getError() {
        System.out.println();
    }

    @GetMapping("/show_weather_history")
    public String showHistoryLog(Model model) {
        List<Weather> weatherList = repositoryService.listHistoryLog();
        model.addAttribute("weatherList", weatherList);
        return "weatherHistoryWindow";
    }


}
