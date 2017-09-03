/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.service;

import com.negod.generics.persistence.update.ObjectUpdate;
import com.negod.generics.persistence.update.UpdateType;
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
import se.backede.webservice.service.methods.CreateMethod;
import se.backede.webservice.service.methods.DeleteMethod;
import se.backede.webservice.service.methods.GetAllMethod;
import se.backede.webservice.service.methods.GetByIdMethod;
import se.backede.webservice.service.methods.ObjectUpdateMethod;
import se.backede.webservice.service.methods.UpdateMethod;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
@Slf4j
public class RestClientIT {

    public RestClientIT() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getRootPath method, of class RestClient.
     */
    @Test
    public void testGetUrl() {
        RestClient client = new RestClientImpl();
    }

    /**
     * Test of create method, of class RestClient.
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
    }

    /**
     * Test of getById method, of class RestClient.
     */
    @Test
    public void testGetById() throws AuthorizationException, InternalServerException {
        Optional<TestEntity> create = create();
        getById(create.get().getId());
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

        client.update(method);
    }

    /**
     * Test of delete method, of class RestClient.
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
     */
    @Test
    public void testGetFilteredList() {
        RestClient client = new RestClientImpl();
    }

    /**
     * Test of geteSearchFields method, of class RestClient.
     */
    @Test
    public void testGeteSearchFields() {
        RestClient client = new RestClientImpl();
    }

    /**
     * Test of indexEntity method, of class RestClient.
     */
    @Test
    public void testIndexEntity() {
        RestClient client = new RestClientImpl();
    }

}
