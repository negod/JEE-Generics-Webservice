package se.backede.webservice.service;

import com.negod.generics.persistence.GenericDao;
import com.negod.generics.persistence.entity.GenericEntity;
import com.negod.generics.persistence.search.GenericFilter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 * @param <T>
 */
@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
public interface RestService<T extends GenericEntity> {

    public GenericDao getDao();

    /**
     *
     * @param entity The entity to persist
     * @return The created entity
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @summary Persists an entity to database
     */
    @Path("/")
    @POST()
    default Response create(T entity) {
        try {
            Optional<T> createdEntity = getDao().persist(entity);
            if (createdEntity.isPresent()) {
                return Response.ok(createdEntity.get(), MediaType.APPLICATION_JSON).build();
            } else {
                return Response.serverError().build();
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    /**
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @return Returns all entities
     * @summary Returns all entitites
     */
    @Path("/")
    @GET()
    default Response getAll() {
        try {
            Optional<List<T>> entityList = getDao().getAll();
            if (entityList.isPresent()) {
                return Response.ok(entityList.get(), MediaType.APPLICATION_JSON).build();
            } else {
                return Response.noContent().build();
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    /**
     *
     * @param id
     * @param entity the entity to update
     * @return The created entity
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @summary Updates an entity
     */
    @Path("/")
    @PUT()
    default Response update(String id, T entity) {
        try {
            Optional<T> updatedEntity = getDao().update(entity);
            if (updatedEntity.isPresent()) {
                return Response.ok(updatedEntity.get(), MediaType.APPLICATION_JSON).build();
            } else {
                return Response.serverError().build();
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    /**
     * @param id the external id of the entity to delete
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @summary Deletes an entity
     */
    @Path("/")
    @DELETE
    default void delete(String id) {
        try {
            getDao().delete(id);
        } catch (Exception e) {
        }
    }

    /**
     * @param id The external id of the entity
     * @return the requested entity
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @summary Gets the entity by its id
     */
    @Path("/{id}")
    @GET
    default Response getById(@PathParam("id") String id) {
        try {
            Optional<T> entity = getDao().getById(id);
            if (entity.isPresent()) {
                return Response.ok(entity.get(), MediaType.APPLICATION_JSON).build();
            } else {
                return Response.noContent().build();
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    /**
     * @param filter The filter for the search
     * @return the filtered list
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @summary Get all entitites with a fixed listsize
     */
    @Path("/filter")
    @POST
    default Response getFilteredList(GenericFilter filter) {
        try {
            Optional<List<T>> responseList = getDao().getAll(filter);
            if (responseList.isPresent()) {
                List<T> entityList = responseList.get();
                return Response.ok(entityList, MediaType.APPLICATION_JSON).build();
            } else {
                return Response.noContent().build();
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    /**
     * @return The entitys searchfields
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @summary Gets all searchable fieldnames
     */
    @Path("/searchFields")
    @GET
    default Response getSearchFields() {
        Set<String> searchFields = getDao().getSearchFields();
        return Response.ok(searchFields, MediaType.APPLICATION_JSON).build();
    }

    @Path("/index")
    @POST
    default Response indexEntity() {
        return Response.ok(getDao().indexEntity(), MediaType.WILDCARD_TYPE).build();
    }

}
