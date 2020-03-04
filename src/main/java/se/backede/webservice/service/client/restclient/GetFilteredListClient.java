/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.service.client.restclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.backede.webservice.constants.PathConstants;
import se.backede.webservice.exception.AuthorizationException;
import se.backede.webservice.exception.InternalServerException;
import se.backede.webservice.service.client.methods.GetFilteredListMethod;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
public interface GetFilteredListClient<T> extends LoginClient {

    final Logger log = LoggerFactory.getLogger(GetFilteredListClient.class);

    public Class<T> getEntityClass();

    public default Optional<Set<T>> getFilteredList(GetFilteredListMethod filteredList) throws AuthorizationException, InternalServerException {
        try {

            Optional<Client> sslClient = getSslClient();
            String filteredListPath = getRootPath().concat(filteredList.getService()).concat(PathConstants.PATH_FILTER);
            if (sslClient.isPresent()) {
                WebTarget target = sslClient.get().target(filteredListPath);
                Response response = target
                        .request()
                        .headers(getHeaders(filteredList.getCredentials()))
                        .accept(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(filteredList.getFilter(), MediaType.APPLICATION_JSON));

                switch (response.getStatus()) {
                    case 200:
                        String responseData = response.readEntity(String.class);
                        ObjectMapper mapper = new ObjectMapper();
                        CollectionType javaType = mapper.getTypeFactory().constructCollectionType(List.class, getEntityClass());
                        List<T> asList = (List<T>) mapper.readValue(responseData, javaType);
                        return Optional.ofNullable(new HashSet<>(asList));
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
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(GetFilteredListClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Optional.empty();
    }

}
