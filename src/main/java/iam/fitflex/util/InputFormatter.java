package iam.fitflex.util;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class InputFormatter {
    private static final String SPACE_REGEX = "\\s+";

    public String replaceSpacesWithHyphens (String input){
        // Replace spaces with hyphens and ensure only one hyphen between words
        return input.replaceAll(SPACE_REGEX, "-");
       }
}
