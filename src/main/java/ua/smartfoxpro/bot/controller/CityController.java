package ua.smartfoxpro.bot.controller;

import ua.smartfoxpro.bot.pojo.City;
import ua.smartfoxpro.bot.service.CityService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CityController {

    private static final Logger logger = LogManager.getLogger(CityController.class);

    @Autowired
    CityService service;

    @GetMapping("/city")
    public ResponseEntity<?> getCities() {
        List<City> cities = service.getAllCities();
        return new ResponseEntity<>(cities, HttpStatus.OK);
    }

    @GetMapping("/city/{cityId}")
    public ResponseEntity<?> getCity(@PathVariable("cityId") int cityId) {
        ResponseEntity<?> responseEntity;
        if (service.isExists(cityId)) {
            City city = service.getCityById(cityId);
            responseEntity = new ResponseEntity<>(city, HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>("City with id=" + cityId + " not exists", HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    @PostMapping("/city")
    public ResponseEntity<?> createNewCity(@RequestBody City newCity) {
        ResponseEntity<?> responseEntity;
        Optional<City> city = service.findByCityName(newCity.getName());
        if (city.isPresent()) {
            responseEntity = new ResponseEntity<>("This city is already exists", HttpStatus.BAD_REQUEST);
        } else {
            if (newCity.getName() == null || newCity.getText() == null ||
                    newCity.getName().isBlank() || newCity.getText().isBlank()) {
                responseEntity = new ResponseEntity<>("Please, insert a correct data!", HttpStatus.BAD_REQUEST);
            } else {
                service.createNewCity(newCity);
                responseEntity = new ResponseEntity<>(newCity, HttpStatus.OK);
                logger.info("New city: " + newCity);
            }
        }
        return responseEntity;
    }

    @PutMapping("/city/{id}")
    public ResponseEntity<?> updateCity(@PathVariable int id,
                                        @RequestBody City city) {
        ResponseEntity<?> responseEntity;
        if (service.isExists(id)) {
            if (city.getName() != null && !city.getName().isBlank() &&
                    city.getText() != null && !city.getText().isBlank()) {
                service.updateCity(id, city);
                responseEntity = new ResponseEntity<>(city, HttpStatus.OK);
                logger.info("Update city ID=" + id + " " + city);
            } else {
                responseEntity = new ResponseEntity<>("Please, insert a correct data!", HttpStatus.BAD_REQUEST);
            }
        } else responseEntity = new ResponseEntity<>("City with id=" + id + " not exists", HttpStatus.NOT_FOUND);
        return responseEntity;
    }

    @DeleteMapping("/city/{id}")
    public ResponseEntity<?> deleteCity(@PathVariable int id) {
        ResponseEntity<?> responseEntity;
        if (service.isExists(id)) {
            service.deleteCity(id);
            responseEntity = new ResponseEntity<>("City deleted!", HttpStatus.OK);
            logger.info("Delete city ID=" + id);
        } else responseEntity = new ResponseEntity<>("City with id=" + id + " not exists!", HttpStatus.NOT_FOUND);
        return responseEntity;
    }
}
