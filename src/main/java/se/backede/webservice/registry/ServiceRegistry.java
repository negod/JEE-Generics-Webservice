/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.registry;

import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.extern.slf4j.Slf4j;
import se.backede.webservice.exception.AuthorizationException;
import se.backede.webservice.exception.InternalServerException;
import se.backede.webservice.properties.ApplicationProperty;
import se.backede.webservice.security.Credentials;
import se.backede.webservice.service.client.methods.CreateMethod;
import se.backede.webservice.service.client.restclient.CreateClient;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
@Slf4j
@Named
public abstract class ServiceRegistry implements CreateClient<RegistryEntity> {
    
    @Inject
    @ApplicationProperty(name = "keystore.path")
    private String keyStorePath;
    
    @Inject
    @ApplicationProperty(name = "cert.path")
    private String certPath;
    
    @Inject
    @ApplicationProperty(name = "path.replacement")
    private String pathReplacement;
    
    @Inject
    @ApplicationProperty(name = "service.user")
    private String user;
    
    @Inject
    @ApplicationProperty(name = "service.pass")
    private String password;
    
    @Inject
    @ApplicationProperty(name = "server.rootpath")
    private String rootPath;
    
    public abstract String getServiceName();
    
    public abstract String getRegistryVersion();
    
    @Override
    public String getRootPath() {
        return rootPath.replace(pathReplacement, "registry".concat("-").concat(getRegistryVersion()));
    }
    
    @Override
    public Class getEntityClass() {
        return RegistryEntity.class;
    }
    
    @Override
    public String getKeyStorePath() {
        return keyStorePath;
    }
    
    @Override
    public String getCertPath() {
        return certPath;
    }
    
    public Optional<RegistryEntity> register() {
        log.debug("Registering Service {} to REGISTRY-URL: {}", getServiceName(), getRootPath());
        try {
            RegistryEntity entity = new RegistryEntity();
            entity.setServiceName(getServiceName());
            entity.setUrl(rootPath.replace(pathReplacement, getServiceName()));
            entity.setOnline(Boolean.TRUE);
            
            CreateMethod method = new CreateMethod();
            method.setCredentials(getCredentials());
            method.setRequestObject(entity);
            method.setService("registry");
            return create(method);
        } catch (AuthorizationException | InternalServerException ex) {
            log.error("Error when registering service: {} Error {}", getServiceName(), ex);
        }
        return Optional.empty();
    }
    
    public Credentials getCredentials() {
        Credentials credentials = new Credentials();
        credentials.setPassword(password);
        credentials.setUsername(user);
        return credentials;
    }
    
}
