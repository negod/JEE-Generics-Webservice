/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.service;

import com.negod.generics.persistence.search.GenericFilter;
import com.negod.generics.persistence.search.Pagination;
import com.negod.generics.persistence.update.ObjectUpdate;
import com.negod.generics.persistence.update.UpdateType;
import java.util.HashSet;
import se.backede.webservice.service.client.RestClient;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import se.backede.webservice.exception.AuthorizationException;
import se.backede.webservice.exception.InternalServerException;
import se.backede.webservice.security.Credentials;
import se.backede.webservice.service.client.methods.CreateMethod;
import se.backede.webservice.service.client.methods.DeleteMethod;
import se.backede.webservice.service.client.methods.GetAllMethod;
import se.backede.webservice.service.client.methods.GetByIdMethod;
import se.backede.webservice.service.client.methods.GetFilteredListMethod;
import se.backede.webservice.service.client.methods.GetSearchFieldsMethod;
import se.backede.webservice.service.client.methods.IndexEntityMethod;
import se.backede.webservice.service.client.methods.ObjectUpdateMethod;
import se.backede.webservice.service.client.methods.UpdateMethod;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
@Slf4j
public class RestClientIT {

    /**
     * Test of getRootPath method, of class RestClient.
     */
    @Test
    public void testGetUrl() {
        RestClient client = new RestClientImpl();
    }

    /**
     * Test of create method, of class RestClient.
     *
     * @throws se.backede.webservice.exception.AuthorizationException
     * @throws se.backede.webservice.exception.InternalServerException
     */
    @Test
    public void testCreate() throws AuthorizationException, InternalServerException {
        Optional<TestEntity> create = create();
    }

    public Optional<TestEntity> create() throws AuthorizationException, InternalServerException {
        RestClient client = new RestClientImpl();
        Credentials credentials = new Credentials();
        credentials.setUsername("user");
        credentials.setPassword("user");

        TestEntity entity = new TestEntity();
        entity.setOnline(Boolean.TRUE);
        entity.setServiceName(UUID.randomUUID().toString());
        entity.setUrl(UUID.randomUUID().toString());

        CreateMethod<TestEntity> createObject = new CreateMethod<>();
        createObject.setCredentials(credentials);
        createObject.setService("registry");
        createObject.setRequestObject(entity);

        return client.create(createObject);
    }

    /**
     * Test of getAll method, of class RestClient.
     *
     * @throws se.backede.webservice.exception.AuthorizationException
     * @throws se.backede.webservice.exception.InternalServerException
     */
    @Test
    public void testGetAll() throws AuthorizationException, InternalServerException {
        RestClient client = new RestClientImpl();
        Credentials credentials = new Credentials();
        credentials.setUsername("user");
        credentials.setPassword("user");

        GetAllMethod<TestEntity> getAllObject = new GetAllMethod<>();
        getAllObject.setCredentials(credentials);
        getAllObject.setService("registry");

        Optional<Set<TestEntity>> all = client.getAll(getAllObject);

        log.error("Size of array: {}", all.get().size());
    }

    /**
     * Test of update method, of class RestClient.
     *
     * @throws se.backede.webservice.exception.AuthorizationException
     * @throws se.backede.webservice.exception.InternalServerException
     */
    @Test
    public void testUpdate() throws AuthorizationException, InternalServerException {
        RestClient client = new RestClientImpl();

        Credentials credentials = new Credentials();
        credentials.setUsername("user");
        credentials.setPassword("user");

        Optional<TestEntity> create = create();
        String id = create.get().getId();
        create.get().setServiceName("UPDATED".concat(create.get().getServiceName()));

        UpdateMethod<TestEntity> method = new UpdateMethod();
        method.setRequestObject(create.get());
        method.setId(create.get().getId());
        method.setService("registry");
        method.setCredentials(credentials);

        Optional<TestEntity> updatedEntity = client.update(method);
        assert updatedEntity.isPresent();

    }

    /**
     * Test of getById method, of class RestClient.
     *
     * @throws se.backede.webservice.exception.AuthorizationException
     * @throws se.backede.webservice.exception.InternalServerException
     */
    @Test
    public void testGetById() throws AuthorizationException, InternalServerException {
        Optional<TestEntity> create = create();
        Optional<TestEntity> byId = getById(create.get().getId());
        assert byId.isPresent();
    }

    public Optional<TestEntity> getById(String id) throws AuthorizationException, InternalServerException {
        RestClientImpl client = new RestClientImpl();

        Credentials credentials = new Credentials();
        credentials.setUsername("user");
        credentials.setPassword("user");

        GetByIdMethod<TestEntity> getByIdMethod = new GetByIdMethod<>();
        getByIdMethod.setCredentials(credentials);
        getByIdMethod.setService("registry");
        getByIdMethod.setId(id);

        return client.getById(getByIdMethod);
    }

    @Test
    public void testUpdateObject() throws AuthorizationException, InternalServerException {
        RestClient client = new RestClientImpl();

        Credentials credentials = new Credentials();
        credentials.setUsername("user");
        credentials.setPassword("user");

        Optional<TestEntity> create = create();

        ObjectUpdate update = new ObjectUpdate();
        update.setObject("registry");
        update.setObjectId(create.get().getId());
        update.setType(UpdateType.ADD);

        ObjectUpdateMethod method = new ObjectUpdateMethod();
        method.setCredentials(credentials);
        method.setService("registry");
        method.setId(create.get().getId());
        method.setUpdateType(update);

        Optional<TestEntity> update1 = client.update(method);
        assert update1.isPresent();
        assert update1.get().getId().equals(create.get().getId());
    }

    /**
     * Test of delete method, of class RestClient.
     *
     * @throws se.backede.webservice.exception.AuthorizationException
     * @throws se.backede.webservice.exception.InternalServerException
     */
    @Test
    public void testDelete() throws AuthorizationException, InternalServerException {
        Credentials credentials = new Credentials();
        credentials.setUsername("user");
        credentials.setPassword("user");

        Optional<TestEntity> create = create();

        RestClient client = new RestClientImpl();
        DeleteMethod delete = new DeleteMethod();
        delete.setCredentials(credentials);
        delete.setService("registry");
        delete.setId(create.get().getId());

        client.delete(delete);

        Optional<TestEntity> byId = getById(create.get().getId());
        assert byId.isPresent() == false;

    }

    /**
     * Test of getFilteredList method, of class RestClient.
     *
     * @throws se.backede.webservice.exception.AuthorizationException
     * @throws se.backede.webservice.exception.InternalServerException
     */
    @Test
    public void testGetFilteredList() throws AuthorizationException, InternalServerException {
        RestClient client = new RestClientImpl();

        Optional<TestEntity> create = create();

        Credentials credentials = new Credentials();
        credentials.setUsername("user");
        credentials.setPassword("user");

        GenericFilter filter = new GenericFilter();

        Pagination pagination = new Pagination();
        pagination.setListSize(10);
        pagination.setPage(1);

        filter.setGlobalSearchWord("true");
        filter.setPagination(pagination);

        Set<String> searchFields = new HashSet<>();
        searchFields.add("online");
        filter.setSearchFields(searchFields);

        GetFilteredListMethod method = new GetFilteredListMethod();
        method.setCredentials(credentials);
        method.setService("registry");
        method.setFilter(filter);

        Optional<Set<TestEntity>> filteredList = client.getFilteredList(method);

        assert filteredList.get().size() > 1;
    }

    /**
     * Test of geteSearchFields method, of class RestClient.
     *
     * @throws se.backede.webservice.exception.AuthorizationException
     * @throws se.backede.webservice.exception.InternalServerException
     */
    @Test
    public void testGetSearchFields() throws AuthorizationException, InternalServerException {
        RestClient client = new RestClientImpl();

        Credentials credentials = new Credentials();
        credentials.setUsername("user");
        credentials.setPassword("user");

        GetSearchFieldsMethod method = new GetSearchFieldsMethod();
        method.setCredentials(credentials);
        method.setService("registry");

        Optional<Set<String>> searchFields = client.getSearchFields(method);

        assert searchFields.isPresent();
        assert searchFields.get().size() > 1;
    }

    /**
     * Test of indexEntity method, of class RestClient.
     *
     * @throws se.backede.webservice.exception.AuthorizationException
     * @throws se.backede.webservice.exception.InternalServerException
     */
    @Test
    public void testIndexEntity() throws AuthorizationException, InternalServerException {
        RestClient client = new RestClientImpl();

        Credentials credentials = new Credentials();
        credentials.setUsername("user");
        credentials.setPassword("user");

        IndexEntityMethod method = new IndexEntityMethod();
        method.setCredentials(credentials);
        method.setService("registry");

        Optional<Boolean> indexEntity = client.indexEntity(method);

        assert indexEntity.isPresent();
        assert indexEntity.get() == Boolean.TRUE;
    }

}
