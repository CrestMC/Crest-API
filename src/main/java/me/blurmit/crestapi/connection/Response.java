package me.blurmit.crestapi.connection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public enum Response {

    OK() {
        @SafeVarargs
        @Override
        public final String build(Map<String, Object>... data) {
            data[0].put("code", code());
            data[0].put("success", true);

            return message() + getDefaultHeaders() + getJsonBuilder().create().toJson(data, HashMap[].class);
        }

        @Override
        public String message() {
            return "HTTP/2 200 Ok\n";
        }

        @Override
        public int code() {
            return 200;
        }
    },
    BAD_REQUEST() {
        @SafeVarargs
        @Override
        public final String build(Map<String, Object>... data) {
            data[0].put("code", code());
            data[0].put("success", false);

            return message() + getDefaultHeaders() + getJsonBuilder().create().toJson(data, HashMap[].class);
        }

        @Override
        public String message() {
            return "HTTP/2 400 Bad Request\n";
        }

        @Override
        public int code() {
            return 400;
        }
    },
    UNAUTHORIZED() {
        @SafeVarargs
        @Override
        public final String build(Map<String, Object>... data) {
            data[0].put("code", code());
            data[0].put("success", false);

            return message() + getDefaultHeaders() + getJsonBuilder().create().toJson(data, HashMap[].class);
        }

        @Override
        public String message() {
            return "HTTP/2 403 Unauthorized\n";
        }

        @Override
        public int code() {
            return 403;
        }
    },
    RATELIMIT() {
        @SafeVarargs
        @Override
        public final String build(Map<String, Object>... data) {
            data[0].put("code", code());
            data[0].put("success", false);

            return message() + getDefaultHeaders() + getJsonBuilder().create().toJson(data, HashMap[].class);
        }

        @Override
        public String message() {
            return "HTTP/2 429 Too Many Requests\n";
        }

        @Override
        public int code() {
            return 429;
        }
    };

    private final GsonBuilder jsonBuilder;
    private final String defaultHeaders;

    Response() {
        jsonBuilder = new Gson().newBuilder();
        defaultHeaders = "Content-Type: application/json" + "\n" +
                        "Cache-Control: no-store" + "\n" +
                        "\n\n";
    }

    public String getDefaultHeaders() {
        return defaultHeaders;
    }

    public GsonBuilder getJsonBuilder() {
        return jsonBuilder;
    }

    public abstract String build(Map<String, Object>... data);
    public abstract String message();
    public abstract int code();

}
