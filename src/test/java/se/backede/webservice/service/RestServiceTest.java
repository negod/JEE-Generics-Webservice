/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.service;

import java.util.Optional;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import se.backede.generics.persistence.GenericDao;
import se.backede.generics.persistence.dto.GenericDto;
import se.backede.generics.persistence.entity.GenericEntity;
import se.backede.generics.persistence.mapper.BaseMapper;
import se.backede.generics.persistence.search.GenericFilter;
import se.backede.generics.persistence.update.ObjectUpdate;

import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import se.backede.generics.persistence.GenericDaoAbs;
import se.backede.webservice.service.mocks.RestServiceImpl;
import se.backede.webservice.service.mocks.dto.DtoMock;
import se.backede.webservice.service.mocks.dto.EntityMock;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
@RunWith(MockitoJUnitRunner.class)
public class RestServiceTest<E extends GenericEntity, D extends GenericDto> {

    @Mock
    private GenericDaoAbs dao;

    @Mock
    private BaseMapper mapper;

    @InjectMocks
    RestService instance = new RestServiceImpl();

    public RestServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        System.out.println("Setting up tests");
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getDao method, of class RestService.
     */
    @Test
    public void testGetDao() {
        System.out.println("getDao");
        GenericDao expResult = dao;
        GenericDao result = instance.getDao();
        assertEquals(expResult, result);
    }

    /**
     * Test of create method - happycase, of class RestService.
     */
    @Test
    public void testCreateHappyCase() {
        System.out.println("create - happycase");

        DtoMock dto = new DtoMock();
        dto.setName("name");

        EntityMock entity = new EntityMock();
        entity.setName("name");

        Mockito.when(instance.getDao().persistEntity(Mockito.any())).thenReturn(Optional.of(entity));

        Response result = instance.create(dto);
        Response expResult = Response.ok(dto, MediaType.APPLICATION_JSON).build();

        assertEquals(expResult.getStatus(), result.getStatus());
        assertEquals(expResult.hasEntity(), result.hasEntity());
        assertEquals(expResult.getMediaType(), result.getMediaType());

        assertEquals(expResult.getEntity().getClass().getName(), result.getEntity().getClass().getName());

        DtoMock name = (DtoMock) result.getEntity();

        assertEquals(dto.getName(), name.getName());
    }

    /**
     * Test of create method - happycase, of class RestService.
     */
    @Test
    public void testCreateDtoNull() {
        System.out.println("create - dto is null");

        Response result = instance.create(null);
        Response expResult = Response.status(Response.Status.BAD_REQUEST).build();

        assertEquals(expResult.getStatus(), result.getStatus());
        assertEquals(expResult.hasEntity(), result.hasEntity());
        assertEquals(expResult.getMediaType(), result.getMediaType());
    }

    /**
     * Test of getAll method, of class RestService.
     */
    @Test
    public void testGetAll() {
//        System.out.println("getAll");
//        RestService instance = new RestServiceImpl();
//        Response expResult = null;
//        Response result = instance.getAll();
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of update method, of class RestService.
     */
    @Test
    public void testUpdate() {
//        System.out.println("update");
//        String id = "";
//        Object dto = null;
//        RestService instance = new RestServiceImpl();
//        Response expResult = null;
//        Response result = instance.update(id, dto);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of updateObject method, of class RestService.
     */
    @Test
    public void testUpdateObject() {
//        System.out.println("updateObject");
//        String id = "";
//        ObjectUpdate update = null;
//        RestService instance = new RestServiceImpl();
//        Response expResult = null;
//        Response result = instance.updateObject(id, update);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of delete method, of class RestService.
     */
    @Test
    public void testDelete() {
//        System.out.println("delete");
//        String id = "";
//        RestService instance = new RestServiceImpl();
//        Response expResult = null;
//        Response result = instance.delete(id);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of getById method, of class RestService.
     */
    @Test
    public void testGetById() {
//        System.out.println("getById");
//        String id = "";
//        RestService instance = new RestServiceImpl();
//        Response expResult = null;
//        Response result = instance.getById(id);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of getFilteredList method, of class RestService.
     */
    @Test
    public void testGetFilteredList() {
//        System.out.println("getFilteredList");
//        GenericFilter filter = null;
//        RestService instance = new RestServiceImpl();
//        Response expResult = null;
//        Response result = instance.getFilteredList(filter);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of getSearchFields method, of class RestService.
     */
    @Test
    public void testGetSearchFields() {
//        System.out.println("getSearchFields");
//        RestService instance = new RestServiceImpl();
//        Response expResult = null;
//        Response result = instance.getSearchFields();
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of indexEntity method, of class RestService.
     */
    @Test
    public void testIndexEntity() {
//        System.out.println("indexEntity");
//        RestService instance = new RestServiceImpl();
//        Response expResult = null;
//        Response result = instance.indexEntity();
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

}
