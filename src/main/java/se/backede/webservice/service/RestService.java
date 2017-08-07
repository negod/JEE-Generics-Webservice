package se.backede.webservice.service;

import com.negod.generics.persistence.GenericDao;
import com.negod.generics.persistence.entity.GenericEntity;
import com.negod.generics.persistence.exception.DaoException;
import com.negod.generics.persistence.search.GenericFilter;
import com.negod.generics.persistence.update.ObjectUpdate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.ejb.Stateless;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 * @param <T>
 */
@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface RestService<T extends GenericEntity> {

    final Logger log = LoggerFactory.getLogger(RestService.class);

    public GenericDao getDao();

    /**
     *
     * @param entity The entity to persist
     * @return The created entity
     */
    @Path("/")
    @POST
    default Response create(T entity) {
        log.debug("Creating {} with values {} [ RESTLAYER ]", getDao().getClassName(), entity.toString());
        try {
            Optional<T> createdEntity = getDao().persist(entity);
            if (createdEntity.isPresent()) {
                return Response.ok(createdEntity.get(), MediaType.APPLICATION_JSON).build();
            } else {
                return Response.serverError().build();
            }
        } catch (DaoException e) {
            log.error("Error when creating {} with values {} [ RESTLAYER ] ErrorMessage: {}", getDao().getClassName(), entity.toString(), e);
            return Response.serverError().build();
        }
    }

    /**
     * @return Returns all entities
     */
    @Path("/")
    @GET
    default Response getAll() {
        log.debug("Getting all of type {} [ RESTLAYER ] " + getDao().getClassName());
        try {
            Optional<List<T>> entityList = getDao().getAll();
            if (entityList.isPresent()) {
                return Response.ok(entityList.get(), MediaType.APPLICATION_JSON).build();
            } else {
                return Response.noContent().build();
            }
        } catch (DaoException e) {
            log.debug("Error when getting all of type {} [ RESTLAYER ] ErrorMessage: {}", getDao().getClassName(), e);
            return Response.serverError().build();
        }
    }

    /**
     *
     * @param id
     * @param entity the entity to update
     * @return The created entity
     */
    @Path("/{id}")
    @PUT
    default Response update(@PathParam("id") String id, T entity) {
        log.debug("Updating {} with values {} [ RESTLAYER ]", getDao().getClassName(), entity.toString());
        try {
            if (Optional.ofNullable(id).isPresent()) {
                entity.setId(id);
                Optional<T> updatedEntity = getDao().update(entity);
                if (updatedEntity.isPresent()) {
                    return Response.ok(updatedEntity.get(), MediaType.APPLICATION_JSON).build();
                } else {
                    return Response.serverError().build();
                }
            } else {
                return Response.ok("ID not present in request [ RESTLAYER ]", MediaType.APPLICATION_JSON).build();
            }
        } catch (DaoException e) {
            log.error("Error when updating {} with values {} [ RESTLAYER ] ErrorMessage: {}", getDao().getClassName(), entity.toString(), e);
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
    @PUT
    default Response updateObject(@PathParam("id") String id, ObjectUpdate update) {
        log.debug("Updating {} with values {} [ RESTLAYER ]", getDao().getClassName(), update.toString());
        try {
            Optional<T> updatedEntity = getDao().update(id, update);
            if (updatedEntity.isPresent()) {
                return Response.ok(updatedEntity.get(), MediaType.APPLICATION_JSON).build();
            } else {
                log.debug(" Error when Updating {} with values {} [ RESTLAYER ]", getDao().getClassName(), update.toString());
                return Response.serverError().build();
            }
        } catch (DaoException e) {
            log.debug(" Error when Updating {} with values {} [ RESTLAYER ] ErrorMessage: {}", getDao().getClassName(), update.toString());
            return Response.serverError().build();
        }
    }

    /**
     * @param id the external id of the entity to delete
     * @return
     */
    @Path("/{id}")
    @DELETE
    default Response delete(@PathParam("id") String id) {
        log.debug("Deleting {} with ID {} [ RESTLAYER ]", getDao().getClassName(), id);
        try {
            if (getDao().delete(id)) {
                return Response.ok().build();
            }
            log.error("Error when deleting {} with id {} [ RESTLAYER ]", getDao().getClassName(), id);
            return Response.serverError().build();
        } catch (Exception e) {
            log.error("Error when deleting {} with id {} [ RESTLAYER ] ErrorMessage: {}", getDao().getClassName(), id, e);
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
        log.debug("Getting {} by id: {} [ RESTLAYER ]", getDao().getClassName(), id);
        try {
            Optional<T> entity = getDao().getById(id);
            if (entity.isPresent()) {
                return Response.ok(entity.get(), MediaType.APPLICATION_JSON).build();
            } else {
                return Response.noContent().build();
            }
        } catch (DaoException e) {
            log.debug("Error when getting {} by id: {} [ RESTLAYER ] ErrorMessage: {}", getDao().getClassName(), id, e);
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
        log.debug("Getting all {} with filter {} [ RESTLAYER ] ", getDao().getClassName(), filter.toString());
        try {
            Optional<List<T>> responseList = getDao().getAll(filter);
            if (responseList.isPresent()) {
                List<T> entityList = responseList.get();
                return Response.ok(entityList, MediaType.APPLICATION_JSON).build();
            } else {
                return Response.noContent().build();
            }
        } catch (DaoException e) {
            log.debug("Error when getting filtered list {} with values: {} [ RESTLAYER ] ErrorMessage: {}", getDao().getClassName(), filter.toString(), e);
            return Response.serverError().build();
        }
    }

    /**
     * @return The entitys searchfields
     */
    @Path("/search/fields")
    @GET
    default Response getSearchFields() {
        log.debug("Getting all search fields for {} [ RESTLAYER ] ", getDao().getClassName());
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
        log.debug("Indexing entity {} [ RESTLAYER ]", getDao().getClassName());
        return Response.ok(getDao().indexEntity(), MediaType.APPLICATION_JSON).build();
    }

}
