/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.service.client.restclient;

import java.util.Optional;
import java.util.Set;
import static javafx.scene.input.KeyCode.T;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.backede.webservice.constants.PathConstants;
import se.backede.webservice.exception.AuthorizationException;
import se.backede.webservice.exception.InternalServerException;
import static se.backede.webservice.service.client.restclient.GetFilteredListClient.log;
import se.backede.webservice.service.client.methods.IndexEntityMethod;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
public interface IndexEntityClient extends LoginClient {

    final Logger log = LoggerFactory.getLogger(IndexEntityClient.class);

    public default Optional<Boolean> indexEntity(IndexEntityMethod indexEntity) throws AuthorizationException, InternalServerException {
        try {

            Optional<Client> sslClient = getSslClient();
            String filteredListPath = getRootPath().concat(indexEntity.getService()).concat(PathConstants.PATH_INDEX);
            if (sslClient.isPresent()) {
                WebTarget target = sslClient.get().target(filteredListPath);
                Response response = target
                        .request()
                        .headers(getHeaders(indexEntity.getCredentials()))
                        .accept(MediaType.APPLICATION_JSON)
                        .get();

                switch (response.getStatus()) {
                    case 200:
                        Boolean entityResponse = response.readEntity(Boolean.class);
                        return Optional.ofNullable(entityResponse);
                    case 401:
                        throw new AuthorizationException("Not authorized, Got 401 from server");
                    case 500:
                        throw new InternalServerException("Got 500 from server");
                    default:
                        return Optional.empty();
                }
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            log.error("Error when authorizing ERROR: {}", e);
        }
        return Optional.of(Boolean.FALSE);
    }
}
