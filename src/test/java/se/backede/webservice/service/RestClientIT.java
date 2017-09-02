/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.service;

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
import se.backede.webservice.service.methods.GetAllMethod;
import se.backede.webservice.service.methods.GetByIdMethod;
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
     * Test of loginAndGetHeaders method, of class RestClient.
     */
    @Test
    public void testLoginAndGetHeders() throws AuthorizationException {
        RestClient client = new RestClientImpl();
        Credentials credentials = new Credentials();
        credentials.setUsername("user");
        credentials.setPassword("user");
        client.loginAndGetHeaders(credentials);
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
        createObject.setPath("/registry");
        createObject.setRequestObject(entity);
        createObject.setResponseClass(TestEntity.class);

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
        getAllObject.setPath("/registry");
        getAllObject.setResponseClass(TestEntity.class);

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
        method.setPath("/registry");
        method.setResponseClass(TestEntity.class);
        method.setCredentials(credentials);

        Optional<TestEntity> updatedEntity = client.update(method);
    }

    /**
     * Test of getById method, of class RestClient.
     */
    @Test
    public void testGetById() throws AuthorizationException, InternalServerException {
        Optional<TestEntity> create = create();

        RestClientImpl client = new RestClientImpl();

        Credentials credentials = new Credentials();
        credentials.setUsername("user");
        credentials.setPassword("user");

        GetByIdMethod<TestEntity> getByIdMethod = new GetByIdMethod<>();
        getByIdMethod.setCredentials(credentials);
        getByIdMethod.setPath("/registry");
        getByIdMethod.setResponseClass(TestEntity.class);
        getByIdMethod.setId(create.get().getId());

        Optional<TestEntity> all = client.getById(getByIdMethod);

        log.error("Got Object: {}", all.get().toString());
    }

    /**
     * Test of delete method, of class RestClient.
     */
    @Test
    public void testDelete() {
        RestClient client = new RestClientImpl();
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
