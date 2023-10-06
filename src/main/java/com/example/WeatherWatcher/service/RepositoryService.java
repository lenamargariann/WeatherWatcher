package com.example.WeatherWatcher.service;

import com.example.WeatherWatcher.db.DatabaseConfiguration;
import com.example.WeatherWatcher.model.Weather;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class RepositoryService {
    DatabaseConfiguration databaseConfiguration;

    public RepositoryService(DatabaseConfiguration databaseConfiguration) {
        this.databaseConfiguration = databaseConfiguration;
    }
    public List<Weather> listHistoryLog()
    {
        return databaseConfiguration.getWeatherHistory();
    }
    public void saveLog(Weather weather){
        databaseConfiguration.saveHistoryLog(weather);
    }
 }
