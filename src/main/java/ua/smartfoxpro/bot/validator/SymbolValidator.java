package ua.smartfoxpro.bot.validator;

import org.springframework.stereotype.Component;

@Component
public class SymbolValidator {
    public boolean validate(String lastWord, String word) {
        String lastSystemWordSymbol = lastWord.substring(lastWord.length() - 1);
        String firstWordSymbol = word.substring(0, 1);
        return firstWordSymbol.equalsIgnoreCase(lastSystemWordSymbol);
    }
}
