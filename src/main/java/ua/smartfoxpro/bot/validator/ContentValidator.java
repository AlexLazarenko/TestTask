package ua.smartfoxpro.bot.validator;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Component
public class ContentValidator {

    private static final String CONTENT_VALIDATOR_REGEX = "[a-zA-Z]+";

    public boolean isStringMatches(String string) {
        Pattern pattern = Pattern.compile(CONTENT_VALIDATOR_REGEX);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }
}
