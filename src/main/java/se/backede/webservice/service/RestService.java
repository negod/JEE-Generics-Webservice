package se.backede.webservice.service;

import com.negod.generics.persistence.GenericDao;
import com.negod.generics.persistence.entity.GenericEntity;
import com.negod.generics.persistence.exception.DaoException;
import com.negod.generics.persistence.search.GenericFilter;
import com.negod.generics.persistence.update.ObjectUpdate;
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
import lombok.extern.slf4j.Slf4j;

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
        } catch (DaoException e) {
            return Response.serverError().build();
        }
    }

    /**
     * @return Returns all entities
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
        } catch (DaoException e) {
            return Response.serverError().build();
        }
    }

    /**
     *
     * @param id
     * @param entity the entity to update
     * @return The created entity
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
        } catch (DaoException e) {
            return Response.serverError().build();
        }
    }

    /**
     *
     * @param id
     * @param update
     * @return The created entity
     */
    @Path("update/{id}")
    @PUT()
    default Response updateObject(@PathParam("id") String id, ObjectUpdate update) {
        try {
            Optional<T> updatedEntity = getDao().updateObject(id, update);
            if (updatedEntity.isPresent()) {
                return Response.ok(updatedEntity.get(), MediaType.APPLICATION_JSON).build();
            } else {
                return Response.serverError().build();
            }
        } catch (DaoException e) {
            return Response.serverError().build();
        }
    }

    /**
     * @param id the external id of the entity to delete
     * @return
     */
    @Path("/")
    @DELETE
    default Response delete(String id) {
        try {
            if (getDao().delete(id)) {
                return Response.ok().build();
            }
            return Response.serverError().build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    /**
     * @param id The external id of the entity
     * @return the requested entity
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
        } catch (DaoException e) {
            return Response.serverError().build();
        }
    }

    /**
     * @param filter The filter for the search
     * @return the filtered list
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
        } catch (DaoException e) {
            return Response.serverError().build();
        }
    }

    /**
     * @return The entitys searchfields
     */
    @Path("/searchFields")
    @GET
    default Response getSearchFields() {
        Set<String> searchFields = getDao().getSearchFields();
        return Response.ok(searchFields, MediaType.APPLICATION_JSON).build();
    }

    /**
     *
     * @return The ok if the index went ok
     */
    @Path("/index")
    @POST
    default Response indexEntity() {
        return Response.ok(getDao().indexEntity(), MediaType.APPLICATION_JSON).build();
    }

}
