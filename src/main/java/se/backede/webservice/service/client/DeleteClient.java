/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.service.client;

import java.util.Optional;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.backede.webservice.constants.PathConstants;
import se.backede.webservice.exception.AuthorizationException;
import se.backede.webservice.exception.InternalServerException;
import se.backede.webservice.service.methods.DeleteMethod;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
public interface DeleteClient<T> extends LoginClient {

    final Logger log = LoggerFactory.getLogger(GetAllClient.class);

    public default Optional<Boolean> delete(DeleteMethod delete) throws AuthorizationException, InternalServerException {
        try {
            Optional<Client> sslClient = getSslClient();

            if (sslClient.isPresent()) {
                String deletePath = getRootPath().concat(delete.getService()).concat(PathConstants.PATH_GET_BY_ID);
                String deletePathWithId = deletePath.replace(PathConstants.ID_IDENTIFIER, delete.getId());
                WebTarget target = sslClient.get().target(deletePathWithId);
                Response response = target
                        .request()
                        .headers(getHeaders(delete.getCredentials()))
                        .accept(MediaType.APPLICATION_JSON)
                        .delete();

                switch (response.getStatus()) {
                    case 200:
                        return Optional.of(Boolean.TRUE);
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
