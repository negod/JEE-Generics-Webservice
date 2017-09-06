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
import se.backede.webservice.service.methods.GetSearchFieldsMethod;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 * @param <T>
 */
public interface GetSearchFieldsClient extends LoginClient {

    final Logger log = LoggerFactory.getLogger(GetSearchFieldsClient.class);

    public default Optional<Set<String>> getSearchFields(GetSearchFieldsMethod getSearchFields) throws AuthorizationException, InternalServerException {
        try {

            Optional<Client> sslClient = getSslClient();

            if (sslClient.isPresent()) {
                String getSearchFildsPath = getRootPath().concat(getSearchFields.getService()).concat(PathConstants.PATH_SEARCH_FIELDS);
                WebTarget target = sslClient.get().target(getSearchFildsPath);
                Response response = target
                        .request()
                        .headers(getHeaders(getSearchFields.getCredentials()))
                        .accept(MediaType.APPLICATION_JSON)
                        .get();

                switch (response.getStatus()) {
                    case 200:
                        Set<String> entityResponse = (Set<String>) response.readEntity(new GenericType<Set<String>>() {
                        });
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
