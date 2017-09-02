/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.service;

import com.negod.generics.persistence.search.GenericFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import se.backede.webservice.exception.AuthorizationException;
import se.backede.webservice.exception.InternalServerException;
import se.backede.webservice.properties.ApplicationProperty;
import se.backede.webservice.security.Credentials;
import se.backede.webservice.service.methods.CreateMethod;
import se.backede.webservice.service.methods.GetByIdMethod;
import se.backede.webservice.service.methods.UpdateMethod;
import se.backede.webservice.service.methods.http.Get;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 * @param <T>
 */
@Slf4j
@Data
public abstract class RestClient<T> implements SSLClient {
    
    @Inject
    @ApplicationProperty(name = "keystore.path")
    private String keyStorePath;
    
    private Optional<String> authHeader;
    
    @Override
    public String getKeyStorePath() {
        return keyStorePath;
    }
    
    public abstract String getRootPath();
    
    public void loginAndGetHeaders(Credentials credentials) throws AuthorizationException {
        getSslClient().ifPresent((Client client) -> {
            try {
                
                WebTarget target = client.target(getRootPath() + "/auth/login");
                Response response = target.request().post(Entity.entity(credentials, MediaType.APPLICATION_JSON));
                
                if (response.getStatus() == 200) {
                    String header = (String) response.getMetadata().get(HttpHeaders.AUTHORIZATION).get(0);
                    setAuthHeader(Optional.ofNullable(header));
                } else {
                    throw new AuthorizationException("Failed to authorize");
                }
                
            } catch (IllegalArgumentException | NullPointerException e) {
                log.error("Error when authorizing ERROR: {}", e);
            } catch (AuthorizationException ex) {
                log.error("Error when authorizing URL: {} , ERROR: {}", getRootPath(), ex);
            }
        });
    }
    
    private MultivaluedMap<String, Object> getHeaders(Credentials credentials) throws AuthorizationException {
        loginAndGetHeaders(credentials);
        MultivaluedMap headers = new MultivaluedMapImpl();
        if (authHeader.isPresent()) {
            headers.add(HttpHeaders.AUTHORIZATION, authHeader.get());
        }
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        return headers;
    }
    
    public Optional<T> create(CreateMethod post) throws AuthorizationException, InternalServerException {
        try {
            
            Optional<Client> sslClient = getSslClient();
            
            if (sslClient.isPresent()) {
                WebTarget target = sslClient.get().target(getRootPath().concat(post.getPath()));
                Entity<T> data = Entity.entity((T) post.getRequestObject(), MediaType.APPLICATION_JSON_TYPE);
                Response response = target
                        .request()
                        .headers(getHeaders(post.getCredentials()))
                        .accept(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(post.getRequestObject(), MediaType.APPLICATION_JSON));
                
                switch (response.getStatus()) {
                    case 200:
                        T entityResponse = (T) response.readEntity(post.getResponseClass());
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
    
    public Optional<T> update(UpdateMethod update) throws AuthorizationException, InternalServerException {
        try {
            
            Optional<Client> sslClient = getSslClient();
            
            if (sslClient.isPresent()) {
                WebTarget target = sslClient.get().target(getRootPath().concat(update.getPath()).concat("/").concat(update.getId()));
                Entity<T> data = Entity.entity((T) update.getRequestObject(), MediaType.APPLICATION_JSON_TYPE);
                Response response = target
                        .request()
                        .headers(getHeaders(update.getCredentials()))
                        .accept(MediaType.APPLICATION_JSON)
                        .put(Entity.entity(update.getRequestObject(), MediaType.APPLICATION_JSON));
                
                switch (response.getStatus()) {
                    case 200:
                        T entityResponse = (T) response.readEntity(update.getResponseClass());
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
    
    public Optional<Set<T>> getAll(Get getAll) throws AuthorizationException, InternalServerException {
        try {
            
            Optional<Client> sslClient = getSslClient();
            
            if (sslClient.isPresent()) {
                WebTarget target = sslClient.get().target(getRootPath().concat(getAll.getPath()));
                Response response = target
                        .request()
                        .headers(getHeaders(getAll.getCredentials()))
                        .accept(MediaType.APPLICATION_JSON)
                        .get();
                
                switch (response.getStatus()) {
                    case 200:
                        Set<T> entityResponse = (Set<T>) response.readEntity(new GenericType<Set<T>>() {
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
    
    public Optional<T> getById(GetByIdMethod getById) throws AuthorizationException, InternalServerException {
        try {
            
            Optional<Client> sslClient = getSslClient();
            
            if (sslClient.isPresent()) {
                WebTarget target = sslClient.get().target(getRootPath().concat(getById.getPath()).concat("/").concat(getById.getId()));
                Response response = target
                        .request()
                        .headers(getHeaders(getById.getCredentials()))
                        .accept(MediaType.APPLICATION_JSON)
                        .get();
                
                switch (response.getStatus()) {
                    case 200:
                        T entityResponse = (T) response.readEntity(getById.getResponseClass());
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

    void delete(String id) {
    }
    
    Set<T> getFilteredList(GenericFilter filter) {
        return null;
    }
    
    Set<String> geteSearchFields() {
        return null;
    }
    
    Boolean indexEntity() {
        return null;
    }
    
}
