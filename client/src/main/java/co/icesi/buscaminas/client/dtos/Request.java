package co.icesi.buscaminas.client.dtos;

import java.util.LinkedHashMap;
import java.util.Map;

public class Request {
    public String action;
    public Map<String, String> data;

    public Request() {
        this.data = new LinkedHashMap<>();
    }

    public Request(String action) {
        this.action = action;
        this.data = new LinkedHashMap<>();
    }

    public Request with(String key, Object value) {
        data.put(key, String.valueOf(value));
        return this;
    }
}
