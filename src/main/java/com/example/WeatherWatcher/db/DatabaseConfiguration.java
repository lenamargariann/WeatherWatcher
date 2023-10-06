package com.example.WeatherWatcher.db;

import com.example.WeatherWatcher.model.Weather;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Component
public class DatabaseConfiguration {
    private String DATA_SOURCE_URL = "jdbc:mysql://127.0.0.1:3306/";
    private final String DB_CREATION_ST = "CREATE DATABASE IF NOT EXISTS weather_history;";
    private final String TABLE_CREATION_ST = "CREATE TABLE IF NOT EXISTS history (" + "ID INT AUTO_INCREMENT," + " name VARCHAR(255)," + " time VARCHAR(255)," + " value VARCHAR(255)," + " description TEXT," + " PRIMARY KEY (ID)" + ");";
    private static final String INSERT_SQL = "INSERT INTO history (name, time, value, description) VALUES (?, ?, ?, ?)";
    private final String LIST_SQL = "SELECT * FROM history;";
    private final String GET_HISTORY_BY_ID = "SELECT * FROM weather_history WHERE ID = ?;";

    {
        createDatabase();
    }

    @Bean
    private DataSource dataSource() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(DATA_SOURCE_URL);
        dataSource.setUsername("lenamargariann");
        dataSource.setPassword("Dinozavr-123");
        return dataSource;
    }

    private void createDatabase() {
        try (Connection connection = dataSource().getConnection()) {
            PreparedStatement statement1 = connection.prepareStatement(DB_CREATION_ST);
            statement1.execute();
            DATA_SOURCE_URL += "weather_history";
            createHistoryTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createHistoryTable() {
        try (Connection connection = dataSource().getConnection()) {
            PreparedStatement statement2 = connection.prepareStatement(TABLE_CREATION_ST);
            statement2.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean saveHistoryLog(Weather weather) {
        try (Connection connection = dataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(INSERT_SQL);
            statement.setString(1, weather.getName());
            statement.setString(2, weather.getTime());
            statement.setString(3, weather.getValue());
            statement.setString(4, weather.getDescription());
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Weather> getWeatherHistory() {
        List<Weather> list = new ArrayList<>();
        try (Connection connection = dataSource().getConnection()) {
            PreparedStatement st = connection.prepareStatement(LIST_SQL);
            ResultSet set = st.executeQuery();
            while (set.next()) {
                Weather weather = new Weather();
                weather.setId(Long.parseLong(set.getString("id")));
                weather.setName(set.getString("name"));
                weather.setDescription(set.getString("description"));
                weather.setTime(set.getString("time"));
                weather.setValue(set.getString("value"));
                list.add(weather);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
