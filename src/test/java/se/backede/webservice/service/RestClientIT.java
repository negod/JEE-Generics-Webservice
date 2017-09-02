/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.service;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import se.backede.webservice.exception.AuthorizationException;
import se.backede.webservice.exception.InternalServerException;
import se.backede.webservice.security.Credentials;
import se.backede.webservice.service.methods.Create;

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
        RestClient client = new RestClientImpl();
        Credentials credentials = new Credentials();
        credentials.setUsername("user");
        credentials.setPassword("user");

        TestEntity entity = new TestEntity();
        entity.setOnline(Boolean.FALSE);
        entity.setServiceName("FROM CLIENT12");
        entity.setUrl("http://from_client12");

        Create<TestEntity> createObject = new Create<>();
        createObject.setCredentials(credentials);
        createObject.setPath("");
        createObject.setRequestObject(entity);
        createObject.setResponseClass(TestEntity.class);

        Optional<TestEntity> create = client.create(createObject);
        log.error(create.get().toString());
    }

    /**
     * Test of getAll method, of class RestClient.
     */
    @Test
    public void testGetAll() {
        RestClient client = new RestClientImpl();
    }

    /**
     * Test of update method, of class RestClient.
     */
    @Test
    public void testUpdate() {
        RestClient client = new RestClientImpl();
    }

    /**
     * Test of updateObject method, of class RestClient.
     */
    @Test
    public void testUpdateObject() {
        RestClient client = new RestClientImpl();
    }

    /**
     * Test of getById method, of class RestClient.
     */
    @Test
    public void testGetById() {
        RestClient client = new RestClientImpl();
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
