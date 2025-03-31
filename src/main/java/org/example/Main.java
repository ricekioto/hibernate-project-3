package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.example.config.MySessionFactory;
import org.example.dao.CityDAO;
import org.example.dao.CountryDAO;
import org.example.domain.City;
import org.example.domain.Country;
import org.example.domain.CountryLanguage;
import org.example.redis.CityCountry;
import org.example.redis.Language;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class Main {
    private static SessionFactory sessionFactory = MySessionFactory.getSessionFactory();
    private final RedisClient redisClient;

    private final ObjectMapper mapper;

    private final CityDAO cityDAO;
    private final CountryDAO countryDAO;

    public Main() {
        this.redisClient = prepareRedisClient();
        this.mapper = new ObjectMapper();
        this.cityDAO = new CityDAO(sessionFactory);
        this.countryDAO = new CountryDAO(sessionFactory);
    }

    public static void main(String[] args) {
        Main main = new Main();
        List<City> allCities = main.fetchData(main);
//        main.pushToRedis();
        main.shutdown();
    }

    private RedisClient prepareRedisClient() {
        RedisClient redisClient = RedisClient.create(RedisURI.create("localhost", 6379));
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            System.out.println("\nConnected to Redis\n");
        }
        return redisClient;
    }

    private void pushToRedis(List<CityCountry> data) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (CityCountry cityCountry : data) {
                try {
                    sync.set(String.valueOf(cityCountry.getId()), mapper.writeValueAsString(cityCountry));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void shutdown() {
        if (nonNull(sessionFactory)) {
            sessionFactory.close();
        }
        if (nonNull(redisClient)) {
            redisClient.shutdown();
        }
    }

    private List<City> fetchData(Main main) {
        try (Session session = main.sessionFactory.getCurrentSession()) {
            List<City> allCities = new ArrayList<>();
            session.beginTransaction();
            List<Country> countries = main.countryDAO.getAll();

            int totalCount = main.cityDAO.getTotalCount();
            int step = 500;
            for (int i = 0; i < totalCount; i += step) {
                allCities.addAll(main.cityDAO.getItems(i, step));
            }
            session.getTransaction().commit();
            return allCities;
        }
    }

    private List<CityCountry> transformData(List<City> cities) {
        return cities.stream()
                .map(city -> {
                    Country country = city.getCountry();
                    CityCountry res = CityCountry.builder()
                            .id(city.getId())
                            .name(city.getName())
                            .population(city.getPopulation())
                            .district(city.getDistrict())
                            .alternativeCountryCode(country.getCode2())
                            .continent(country.getContinent())
                            .countryCode(country.getCode())
                            .countryName(country.getLocalName())
                            .countryPopulation(country.getPopulation())
                            .countrySurfaceArea(country.getSurfaceArea())
                            .countryRegion(country.getRegion())
                            .build();

                    Set<CountryLanguage> countryLanguages = country.getLanguages();
                    Set<Language> languages = countryLanguages.stream()
                            .map(cl -> {
                                Language language = Language.builder()
                                        .language(cl.getLanguage())
                                        .isOfficial(cl.getIsOfficial())
                                        .percentage(cl.getPercentage())
                                        .build();
                                return language;
                            }).collect(Collectors.toSet());
                    res.setLanguages(languages);

                    return res;

                }).collect(Collectors.toList());
    }

}