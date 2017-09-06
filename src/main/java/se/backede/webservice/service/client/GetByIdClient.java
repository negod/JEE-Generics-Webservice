/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.service.client;

import java.util.Optional;
import java.util.Set;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.backede.webservice.constants.PathConstants;
import se.backede.webservice.exception.AuthorizationException;
import se.backede.webservice.exception.InternalServerException;
import se.backede.webservice.service.methods.GetByIdMethod;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
public interface GetByIdClient<T> extends LoginClient {

    final Logger log = LoggerFactory.getLogger(GetByIdClient.class);

    public Class<T> getEntityClass();

    public default Optional<T> getById(GetByIdMethod getById) throws AuthorizationException, InternalServerException {
        try {
            Optional<Client> sslClient = getSslClient();

            if (sslClient.isPresent()) {
                String getByIdPath = getRootPath().concat(getById.getService()).concat(PathConstants.PATH_GET_BY_ID);
                String getByIdPathWithId = getByIdPath.replace(PathConstants.ID_IDENTIFIER, getById.getId());
                WebTarget target = sslClient.get().target(getByIdPathWithId);
                Response response = target
                        .request()
                        .headers(getHeaders(getById.getCredentials()))
                        .accept(MediaType.APPLICATION_JSON)
                        .get();

                switch (response.getStatus()) {
                    case 200:
                        T entityResponse = (T) response.readEntity(getEntityClass());
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
        return Optional.empty();
    }

}
