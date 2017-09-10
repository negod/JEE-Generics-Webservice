/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.service.client.restclient;

import java.util.Optional;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.backede.webservice.exception.CreateNotPossibleException;
import se.backede.webservice.exception.AuthorizationException;
import se.backede.webservice.exception.InternalServerException;
import se.backede.webservice.service.client.methods.CreateMethod;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 * @param <T>
 */
public interface CreateClient<T> extends LoginClient {

    final Logger log = LoggerFactory.getLogger(CreateClient.class);

    public Class<T> getEntityClass();

    public default Optional<T> create(CreateMethod create) throws AuthorizationException, InternalServerException, CreateNotPossibleException {
        try {

            Optional<Client> sslClient = getSslClient();
            String createPath = getRootPath().concat(create.getService());
            if (sslClient.isPresent()) {
                WebTarget target = sslClient.get().target(createPath);
                Entity<T> data = Entity.entity((T) create.getRequestObject(), MediaType.APPLICATION_JSON_TYPE);
                Response response = target
                        .request()
                        .headers(getHeaders(create.getCredentials()))
                        .accept(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(create.getRequestObject(), MediaType.APPLICATION_JSON));

                switch (response.getStatus()) {
                    case 200:
                        T entityResponse = (T) response.readEntity(getEntityClass());
                        return Optional.ofNullable(entityResponse);
                    case 401:
                        throw new AuthorizationException("Not authorized, Got 401 from server");
                    case 500:
                        throw new InternalServerException("Got 500 from server");
                    case 304:
                        throw new CreateNotPossibleException("Update not possible, probably constraint violation, Got 304 from server");
                    default:
                        return Optional.empty();
                }
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            log.error("Error when creating ERROR: {}", e);
        }
        return Optional.empty();
    }

}
