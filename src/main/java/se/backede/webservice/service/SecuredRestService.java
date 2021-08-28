package se.backede.webservice.service;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.backede.generics.persistence.GenericDaoAbs;
import se.backede.generics.persistence.dto.GenericDto;
import se.backede.generics.persistence.search.GenericFilter;
import se.backede.generics.persistence.update.ObjectUpdate;
import se.backede.webservice.constants.PathConstants;
import se.backede.webservice.security.Secured;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 * @param <T>
 */
//@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface SecuredRestService<T extends GenericDto> {

    final Logger log = LoggerFactory.getLogger(SecuredRestService.class);

    public GenericDaoAbs getDao();

    /**
     *
     * @param entity
     * @return
     */
    @Secured
    @POST
    @Path(PathConstants.PATH_BASE_PATH)
    default Response create(T entity) {
        try {
//            Optional<T> createdEntity = getDao().persist(entity);
//            if (createdEntity.isPresent()) {
//                return Response.ok(createdEntity.get(), MediaType.APPLICATION_JSON).build();
//            } else {
//                return Response.serverError().build();
//            }
        } catch (Exception ex) {
            log.error("ConstraintViolation when creating {} with values {} [ RESTLAYER ] Error: {}", getDao().getClassName(), entity.toString(), ex);
            return Response.status(Response.Status.NOT_MODIFIED).build();
        }
        return Response.serverError().build();
    }

    /**
     *
     * @return
     */
    @Secured
    @GET
    @Path(PathConstants.PATH_BASE_PATH)
    default Response getAll() {
        log.debug("Getting all of type {} [ RESTLAYER ] " + getDao().getClassName());
        try {
            Optional<List<T>> entityList = getDao().getAll();
            if (entityList.isPresent()) {
                return Response.ok(entityList.get(), MediaType.APPLICATION_JSON).build();
            } else {
                return Response.noContent().build();
            }
        } catch (Exception e) {
            log.error("Error when getting all of type {} [ RESTLAYER ] ErrorMessage: {}", getDao().getClassName(), e);
            return Response.serverError().build();
        }
    }

    /**
     *
     * @param id
     * @param entity
     * @return
     */
    @Secured
    @PUT
    @Path(PathConstants.PATH_GET_BY_ID)
    default Response update(@PathParam("id") String id, T entity) {
        try {
            log.debug("Updating {} with values {} [ RESTLAYER ]", getDao().getClassName(), entity.toString());
//            if (Optional.ofNullable(id).isPresent()) {
//                entity.setId(id);
//                Optional<T> updatedEntity = getDao().update(entity);
//                if (updatedEntity.isPresent()) {
//                    return Response.ok(updatedEntity.get(), MediaType.APPLICATION_JSON).build();
//                } else {
//                    return Response.noContent().build();
//                }
//            } else {
//                return Response.ok("ID not present in request [ RESTLAYER ]", MediaType.APPLICATION_JSON).build();
//            }
        } catch (Exception e) {
            log.error("Error when updating {} with values {} [ RESTLAYER ] ErrorMessage: {}", getDao().getClassName(), entity.toString(), e);
            return Response.serverError().build();
        }
        return Response.serverError().build();
    }

    /**
     *
     * @param id
     * @param update
     * @return
     */
    @Secured
    @PUT
    @Path(PathConstants.PATH_UPDATE)
    default Response updateObject(@PathParam("id") String id, ObjectUpdate update) {
        log.debug("Updating {} with values {} [ RESTLAYER ]", getDao().getClassName(), update.toString());
        try {
            Optional<T> updatedEntity = getDao().update(id, update);
            if (updatedEntity.isPresent()) {
                return Response.ok(updatedEntity.get(), MediaType.APPLICATION_JSON).build();
            } else {
                log.debug(" Error when Updating {} with values {} [ RESTLAYER ]", getDao().getClassName(), update.toString());
                return Response.noContent().build();
            }
        } catch (Exception e) {
            log.error(" Error when Updating {} with values {} [ RESTLAYER ] ErrorMessage: {}", getDao().getClassName(), update.toString());
            return Response.serverError().build();
        }
    }

    /**
     *
     * @param id
     * @return
     */
    @Secured
    @DELETE
    @Path(PathConstants.PATH_GET_BY_ID)
    default Response delete(@PathParam("id") String id) {
        log.debug("Deleting {} with ID {} [ RESTLAYER ]", getDao().getClassName(), id);
        try {
            if (getDao().delete(id).isPresent()) {
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
     *
     * @param id
     * @return
     */
    @Secured
    @GET
    @Path(PathConstants.PATH_GET_BY_ID)
    default Response getById(@PathParam("id") String id) {
        log.debug("Getting {} by id: {} [ RESTLAYER ]", getDao().getClassName(), id);
        try {
            Optional<T> entity = getDao().getById(id);
            if (entity.isPresent()) {
                return Response.ok(entity.get(), MediaType.APPLICATION_JSON).build();
            } else {
                return Response.noContent().build();
            }
        } catch (Exception e) {
            log.error("Error when getting {} by id: {} [ RESTLAYER ] ErrorMessage: {}", getDao().getClassName(), id, e);
            return Response.serverError().build();
        }
    }

    /**
     *
     * @param filter
     * @return
     */
    @Secured
    @POST
    @Path(PathConstants.PATH_FILTER)
    default Response getFilteredList(GenericFilter filter) {
        log.debug("Getting all {} with filter {} [ RESTLAYER ] ", getDao().getClassName(), filter.toString());
        Optional<Set<T>> responseList = getDao().search(filter);
        if (responseList.isPresent()) {
            Set<T> entityList = responseList.get();
            return Response.ok(entityList, MediaType.APPLICATION_JSON).build();
        } else {
            return Response.noContent().build();
        }
    }

    /**
     *
     * @return
     */
    @Secured
    @GET
    @Path(PathConstants.PATH_SEARCH_FIELDS)
    default Response getSearchFields() {
        try {
            log.debug("Getting all search fields for {} [ RESTLAYER ] ", getDao().getClassName());
            Set<String> searchFields = getDao().getSearchFields();
            if (searchFields.isEmpty()) {
                return Response.noContent().build();
            } else {
                return Response.ok(searchFields, MediaType.APPLICATION_JSON).build();
            }
        } catch (Exception e) {
            log.error("Error when getting searchfields for type: {} [ RESTLAYER ] ErrorMessage: {}", getDao().getClassName(), e);
            return Response.serverError().build();
        }
    }

    /**
     *
     * @return
     */
    @Secured
    @GET
    @Path(PathConstants.PATH_INDEX)
    default Response indexEntity() {
        try {
            log.debug("Indexing entity {} [ RESTLAYER ]", getDao().getClassName());
            return Response.ok(getDao().indexEntity(), MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            log.error("Error building index for type: {} [ RESTLAYER ] ErrorMessage: {}", getDao().getClassName(), e);
            return Response.serverError().build();
        }
    }

}
