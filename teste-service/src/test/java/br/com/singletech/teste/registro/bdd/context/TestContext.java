package br.com.singletech.teste.registro.bdd.context;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ScenarioScope
public class TestContext {

    private String endpointBase;
    private String requestBody;
    private Integer responseStatus;
    private String responseBody;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, Object> data = new HashMap<>();

    public String getEndpointBase() {
        return endpointBase;
    }

    public void setEndpointBase(String endpointBase) {
        this.endpointBase = endpointBase;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public Integer getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(Integer responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }

    public void putData(String key, Object value) {
        this.data.put(key, value);
    }

    public Object getData(String key) {
        return this.data.get(key);
    }

    public void clear() {
        endpointBase = null;
        requestBody = null;
        responseStatus = null;
        responseBody = null;
        headers.clear();
        data.clear();
    }
}
