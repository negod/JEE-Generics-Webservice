/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.service.client.restclient;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.Optional;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.backede.webservice.constants.PathConstants;
import se.backede.webservice.exception.AuthorizationException;
import se.backede.webservice.security.Credentials;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
public interface LoginClient extends SSLClient {

    final Logger log = LoggerFactory.getLogger(LoginClient.class);

    public String getRootPath();

    public default Optional<String> loginAndGetAuthToken(Credentials credentials) throws AuthorizationException {
        Optional<Client> sslClient = getSslClient();
        if (sslClient.isPresent()) {
            try {

                String loginPath = getRootPath().concat(PathConstants.PATH_LOGIN.replace("/", ""));
                WebTarget target = sslClient.get().target(loginPath);
                Response response = target.request().post(Entity.entity(credentials, MediaType.APPLICATION_JSON));

                if (response.getStatus() == 200) {
                    String header = (String) response.getMetadata().get(HttpHeaders.AUTHORIZATION).get(0);
                    return Optional.ofNullable(header);
                } else {
                    throw new AuthorizationException("Failed to authorize");
                }

            } catch (IllegalArgumentException | NullPointerException e) {
                log.error("Error when authorizing ERROR: {}", e);
            } catch (AuthorizationException ex) {
                log.error("Error when authorizing URL: {} , ERROR: {}", getRootPath(), ex);
            }
        }
        return Optional.empty();
    }

    public default MultivaluedMap<String, Object> getHeaders(Credentials credentials) throws AuthorizationException {
        Optional<String> authToken = loginAndGetAuthToken(credentials);
        MultivaluedMap headers = new MultivaluedMapImpl();
        if (authToken.isPresent()) {
            headers.add(HttpHeaders.AUTHORIZATION, authToken.get());
        }
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        return headers;
    }

}
