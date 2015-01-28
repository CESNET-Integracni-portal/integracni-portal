package cz.cvut.fel.integracniportal.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pstrnad on 14.11.2014.
 */
public class JsonRestTemplate extends RestTemplate {

    HttpHeaders headers = new HttpHeaders();
    Map<String, String> params = new HashMap<String, String>();

    public void addBasicAuthHeader(String username, String password) {
        StringBuilder authenticationSB = new StringBuilder();
        authenticationSB.append(username);
        authenticationSB.append(":");
        authenticationSB.append(password);
        String encodedAuth = new String(Base64.encode(authenticationSB.toString().getBytes()));
        authenticationSB = new StringBuilder("Basic ");
        authenticationSB.append(encodedAuth);

        headers.add("Authorization", authenticationSB.toString());
    }

    public void addHeader(String name, String value) {
        headers.add(name, value);
    }

    public void addParameter(String name, String value) {
        params.put(name, value);
    }

    public <T> ResponseEntity<T> post(String url, Class<T> responseType) {
        headers.add("Content-Type", "application/json");
        HttpEntity<Map<String, String>> request = new HttpEntity<Map<String, String>>(params, headers);
        return postForEntity(url, request, responseType);
    }


}
