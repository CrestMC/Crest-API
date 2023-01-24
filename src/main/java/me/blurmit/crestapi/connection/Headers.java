package me.blurmit.crestapi.connection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Headers {

    public static boolean hasHeader(String name, Collection<String> collection) {
        return collection.stream().anyMatch(header -> {
            header = header.trim();
            return (header.startsWith(name + ": ") || header.startsWith(name + ":"));
        });
    }

    public static String getValue(String name, Collection<String> collection) {
        return collection.stream()
                .filter(header -> header.startsWith(name + ": ") || header.startsWith(name + ":"))
                .findFirst()
                .orElse("")
                .split(":")[1]
                .trim();
    }

    public static String getValue(String header) {
        if (!(header.startsWith(header + ": ") || header.startsWith(header + ":"))) {
            return "";
        }

        try {
            return header.split(":")[1].trim();
        } catch (ArrayIndexOutOfBoundsException e) {
            return "";
        }
    }

    public static String getArgument(String name, Collection<String> collection) {
        String headerData = collection.stream()
                .filter(header -> header.trim().contains(name + "="))
                .findFirst()
                .orElse(name + "=unknown");

        return getArgument(name, headerData);
    }

    public static String getArgument(String name, String header) {
        if (name.contains("=")) {
            name = name.replace("=", "-");
        }

        if (name.contains("&")) {
            name = name.replace("&", "|");
        }

        Map<String, String> argumentMap = new HashMap<>();
        String[] arguments = header.split("&");

        for (String argument : arguments) {
            if (argument.contains(name + "=")) {
                String key = argument.split("=")[0];
                String value = argument.replaceFirst(key + "=", "");

                if (value.equals("")) {
                    value = "unknown";
                }

                argumentMap.put(key, value);
            }
        }

        return argumentMap.get(name);
    }

}
