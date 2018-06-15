package com.aconno.acnsensa.data.converter;

import com.aconno.acnsensa.domain.model.readings.Reading;

import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataStringConverter {

    private HashMap<String, String> map;

    private Pattern pattern = Pattern.compile("\\$\\s*(\\w+)");

    public DataStringConverter(String userDataString) {
        parseDataString(userDataString);
    }

    private void parseDataString(String everything) {
        map = new HashMap<>();

        Scanner scanner = new Scanner(everything);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                if (!matcher.group(1).equalsIgnoreCase("ts")
                        && !matcher.group(1).equalsIgnoreCase("name")) {
                    map.put(matcher.group(1), line);
                }
            }
        }
        scanner.close();
    }

//    public String convert(Reading data) {
//        String dataString = map.get(data.getSensorType().toString().toLowerCase());
//        return dataString.replace("$" + data.getSensorType().toString().toLowerCase(), data.value)
//                .replace("$ts", String.valueOf(System.currentTimeMillis()))
//                .replace("$name", data.name);
//    }
}
