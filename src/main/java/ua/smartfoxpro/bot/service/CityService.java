package ua.smartfoxpro.bot.service;

import ua.smartfoxpro.bot.pojo.City;
import ua.smartfoxpro.bot.repository.CityRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CityService {
    private static final Logger logger = LogManager.getLogger(CityService.class);

    @Autowired
    CityRepository cityRepository;

    public List<City> getAllCities() {
        List<City> cities = new ArrayList<>(cityRepository.findAll());
        return cities;
    }

    public City getCityById(int cityId) {
        City city = cityRepository.findById((long) cityId).orElseThrow();
        return city;
    }

    public void createNewCity(City newCity) {
        cityRepository.save(newCity);
    }

    public void updateCity(int id,City newCity) {
            City city = new City();
            city.setCityId((long) id);
            city.setName(newCity.getName());
            city.setText(newCity.getText());
            cityRepository.save(city);
    }

    public boolean isExists(int id){
        return cityRepository.existsById((long) id);
    }

    public void deleteCity(int id){
        cityRepository.deleteById((long) id);
    }

    public Optional<City> findByCityName(String name) {
        Optional<City> city = cityRepository.findByNameIgnoreCase(name);
        return city;
    }
}
