/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.service.client;

import se.backede.webservice.service.client.restclient.DeleteClient;
import se.backede.webservice.service.client.restclient.GetByIdClient;
import se.backede.webservice.service.client.restclient.IndexEntityClient;
import se.backede.webservice.service.client.restclient.GetAllClient;
import se.backede.webservice.service.client.restclient.UpdateClient;
import se.backede.webservice.service.client.restclient.CreateClient;
import se.backede.webservice.service.client.restclient.GetSearchFieldsClient;
import se.backede.webservice.service.client.restclient.LoginClient;
import se.backede.webservice.service.client.restclient.GetFilteredListClient;
import se.backede.webservice.service.client.restclient.UpdateObjectClient;
import com.negod.generics.persistence.search.GenericFilter;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import se.backede.webservice.properties.ApplicationProperty;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 * @param <T>
 */
@Slf4j
@Data
public abstract class RestClient<T> implements
        LoginClient,
        CreateClient<T>,
        UpdateClient<T>,
        GetAllClient<T>,
        GetByIdClient<T>,
        UpdateObjectClient<T>,
        DeleteClient,
        GetFilteredListClient<T>,
        GetSearchFieldsClient,
        IndexEntityClient {

    @Inject
    @ApplicationProperty(name = "keystore.path")
    private String keyStorePath;

    @Inject
    @ApplicationProperty(name = "cert.path")
    private String certPath;

    @Override
    public String getKeyStorePath() {
        return keyStorePath;
    }

    @Override
    public String getCertPath() {
        return certPath;
    }

    @Override
    public abstract String getRootPath();

}
