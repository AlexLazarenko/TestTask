package ua.smartfoxpro.bot.controller;

import ua.smartfoxpro.bot.pojo.City;
import ua.smartfoxpro.bot.service.CityService;
import ua.smartfoxpro.bot.storage.CityNameStorage;
import ua.smartfoxpro.bot.validator.ContentValidator;
import ua.smartfoxpro.bot.validator.SymbolValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CityGameController {

    private static final Logger logger = LogManager.getLogger(CityGameController.class);


    CityNameStorage storage = CityNameStorage.getInstance();
    @Autowired
    CityService service;
    @Autowired
    SymbolValidator symbolValidator;
    @Autowired
    ContentValidator contentValidator;

    @GetMapping("/begin")
    public ResponseEntity<?> offerFirstWord() {
        List<City> cities = service.getAllCities();
        storage.fillAllNames(cities);
        String firstWord = storage.getRandomElement();
        storage.addSystemAnswer(firstWord);
        return new ResponseEntity<>(firstWord, HttpStatus.OK);
    }

    @GetMapping("/next")
    public ResponseEntity<?> getNextWord(@RequestParam("word") String word) {
        ResponseEntity<?> responseEntity;
        if (word != null && !word.isBlank() && contentValidator.isStringMatches(word)) {
            if (!storage.findCity(word)) {
                String lastSystemWord = storage.getLastSystemWord();
                if (symbolValidator.validate(lastSystemWord, word)) {
                    storage.addUserAnswer(word);
                    String lastUserWordSymbol = word.substring(word.length() - 1);
                    Optional<String> nextWord = storage.getNextSystemWord(lastUserWordSymbol);
                    if (nextWord.isPresent()) {
                        responseEntity = new ResponseEntity<>(nextWord, HttpStatus.OK);
                        storage.addSystemAnswer(nextWord.get());
                    } else {
                        responseEntity = new ResponseEntity<>("You win!", HttpStatus.OK);
                    }
                } else {
                    responseEntity = new ResponseEntity<>("City name must starts with " +
                            lastSystemWord.substring(lastSystemWord.length() - 1), HttpStatus.NOT_ACCEPTABLE);
                }
            } else {
                responseEntity = new ResponseEntity<>("This city is already been!Please insert another!", HttpStatus.NOT_ACCEPTABLE);
            }
        } else {
            responseEntity = new ResponseEntity<>("Please, insert a correct data!", HttpStatus.NOT_ACCEPTABLE);
        }
        return responseEntity;
    }

    @PostMapping("/end")
    public ResponseEntity<?> endGame() {
        storage.clearStorage();
        String message = "Спасибо за игру";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
