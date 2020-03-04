/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import se.backede.generics.persistence.search.GenericFilter;
import se.backede.webservice.exception.CreateNotPossibleException;
import se.backede.webservice.exception.AuthorizationException;
import se.backede.webservice.exception.InternalServerException;
import se.backede.webservice.exception.ServiceNotMatchException;
import se.backede.webservice.properties.WsProperties;
import se.backede.webservice.security.Credentials;
import se.backede.webservice.service.client.methods.CreateMethod;
import se.backede.webservice.service.client.methods.GetFilteredListMethod;
import se.backede.webservice.service.client.methods.UpdateMethod;
import se.backede.webservice.service.client.restclient.CreateClient;
import se.backede.webservice.service.client.restclient.GetFilteredListClient;
import se.backede.webservice.service.client.restclient.UpdateClient;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
@Slf4j
@Named
@Data
public abstract class ServiceRegistry implements CreateClient<RegistryEntity>, UpdateClient<RegistryEntity>, GetFilteredListClient<RegistryEntity> {

    private Optional<RegistryEntity> registryEntity = Optional.empty();

    @Inject
    WsProperties props;

    public abstract String getServiceName();

    public abstract String getRegistryVersion();

    @Override
    public String getRootPath() {
        return props.getRootPath().replace(props.getPathReplacement(), "registry".concat("-").concat(getRegistryVersion()));
    }

    @Override
    public Class getEntityClass() {
        return RegistryEntity.class;
    }

    @Override
    public String getKeyStorePath() {
        return props.getKeyStorePath();
    }

    @Override
    public String getCertPath() {
        return props.getCertPath();
    }

    public Optional<RegistryEntity> setOnline() {
        return updateOnlineStatus(Boolean.TRUE);
    }

    public Optional<RegistryEntity> setOffline() {
        return updateOnlineStatus(Boolean.FALSE);
    }

    private Optional<RegistryEntity> updateOnlineStatus(Boolean online) {
        try {
            UpdateMethod update = new UpdateMethod();
            update.setService("registry");
            update.setId(getRegistryEntity().get().getId());
            update.setCredentials(getCredentials());
            getRegistryEntity().get().setOnline(online);
            update.setRequestObject(getRegistryEntity().get());
            return update(update);
        } catch (AuthorizationException | InternalServerException ex) {
            log.error("Error when updating online status for service: {} Error {}", getServiceName(), ex);
        }
        return Optional.empty();
    }

    private Optional<RegistryEntity> isServiceRegistered() throws AuthorizationException, InternalServerException {
        if (!getRegistryEntity().isPresent()) {
            GetFilteredListMethod getFilteredList = new GetFilteredListMethod();
            GenericFilter filter = new GenericFilter();
            filter.setGlobalSearchWord(getServiceName());
            filter.setSearchFields(new HashSet<>(Arrays.asList("serviceName")));

            getFilteredList.setCredentials(getCredentials());
            getFilteredList.setService("registry");
            getFilteredList.setFilter(filter);

            Optional<Set<RegistryEntity>> filteredList = getFilteredList(getFilteredList);
            if (filteredList.isPresent()) {
                if (filteredList.get().size() == 1) {
                    return Optional.ofNullable(new ArrayList<>(filteredList.get()).get(0));
                }
            }
        }
        log.info("Service {} not registered", getServiceName());
        return Optional.empty();
    }

    private Optional<RegistryEntity> registerAsNewService() throws AuthorizationException, InternalServerException, CreateNotPossibleException {
        RegistryEntity entity = new RegistryEntity();
        entity.setServiceName(getServiceName());
        entity.setUrl(props.getRootPath().replace(props.getPathReplacement(), getServiceName()));
        entity.setOnline(Boolean.TRUE);

        CreateMethod method = new CreateMethod();
        method.setCredentials(getCredentials());
        method.setRequestObject(entity);
        method.setService("registry");
        return create(method);
    }

    public Optional<RegistryEntity> register() throws CreateNotPossibleException, ServiceNotMatchException {
        log.debug("Registering Service {} to REGISTRY-URL: {}", getServiceName(), getRootPath());
        try {

            //Check if the service is already registered
            Optional<RegistryEntity> serviceRegistered = isServiceRegistered();
            if (serviceRegistered.isPresent()) {
                if (serviceRegistered.get().getServiceName().equals(getServiceName())) {
                    setRegistryEntity(serviceRegistered);
                } else {
                    log.error("The service retrieved: {} does not match the requesting service {}!", serviceRegistered.get().getServiceName(), getServiceName());
                    throw new ServiceNotMatchException("The service retrieved does not match the requesting service ");
                }
                return setOnline();
            } else {
                return registerAsNewService();
            }
        } catch (AuthorizationException | InternalServerException ex) {
            log.error("Error when registering service: {} Error {}", getServiceName(), ex.getMessage());
        }
        return Optional.empty();
    }

    public Credentials getCredentials() {
        Credentials credentials = new Credentials();
        credentials.setPassword(props.getUser());
        credentials.setUsername(props.getPassword());
        return credentials;
    }

}
