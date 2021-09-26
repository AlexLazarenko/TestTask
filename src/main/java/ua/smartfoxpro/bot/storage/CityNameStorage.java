package ua.smartfoxpro.bot.storage;

import ua.smartfoxpro.bot.pojo.City;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CityNameStorage {

    private static CityNameStorage instance;
    private final List<String> systemAnswerList = new ArrayList<>();
    private final List<String> allNames = new ArrayList<>();
    private final List<String> userAnswerList = new ArrayList<>();


    private CityNameStorage() {
    }

    public static CityNameStorage getInstance() {
        if (instance == null) {
            instance = new CityNameStorage();
        }
        return instance;
    }

    public void addSystemAnswer(String name) {
        systemAnswerList.add(name);
        if (allNames.contains(name)) {
            allNames.remove(name);
        }
    }

    public void addUserAnswer(String name) {
        userAnswerList.add(name);
        if (allNames.contains(name)) {
            allNames.remove(name);
        }
    }

    public String getLastSystemWord() {
        return systemAnswerList.get(systemAnswerList.size() - 1);
    }

    public boolean findCity(String name) {
        return systemAnswerList.contains(name)||userAnswerList.contains(name);
    }

    public void fillAllNames(List<City> cities) {
        for (City city : cities) {
            allNames.add(city.getName());
        }
    }

    public String getRandomElement() {
        int randomElement = (int) (Math.random() * ((allNames.size())));
        return allNames.get(randomElement);
    }

    public Optional<String> getNextSystemWord(String symbol) {
        Optional<String> nextWord = Optional.empty();
        for (String word : allNames) {
            if (word.substring(0, 1).equalsIgnoreCase(symbol)) {
                nextWord = Optional.of(word);
            }
        }
        return nextWord;
    }
    public void clearStorage(){
        allNames.clear();
        systemAnswerList.clear();
        userAnswerList.clear();
    }
}

