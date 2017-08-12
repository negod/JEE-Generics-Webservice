package se.backede.webservice.service;

import com.negod.generics.persistence.GenericDao;
import com.negod.generics.persistence.entity.GenericEntity;
import com.negod.generics.persistence.exception.DaoException;
import com.negod.generics.persistence.search.GenericFilter;
import com.negod.generics.persistence.update.ObjectUpdate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api
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
    @POST
    @Path("/")
    @ApiOperation(value = "create", notes = "Returns the persisted object", response = Response.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful persisting the entity", response = Response.class)
        ,
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    default Response create(@ApiParam(value = "The Object to create", required = true) T entity) {
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
    @GET
    @Path("/")
    @ApiOperation(value = "getAll", notes = "Returns a list of Objects", response = Response.class, responseContainer = "List")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful retrieval of the entity", response = Response.class)
        ,
        @ApiResponse(code = 204, message = "No entity found", response = Response.class)
        ,
        @ApiResponse(code = 500, message = "Internal server error")}
    )
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
            log.error("Error when getting all of type {} [ RESTLAYER ] ErrorMessage: {}", getDao().getClassName(), e);
            return Response.serverError().build();
        }
    }

    /**
     *
     * @param id
     * @param entity the entity to update
     * @return The created entity
     */
    @PUT
    @Path("/{id}")
    @ApiOperation(value = "update", notes = "Update the owning object", response = Response.class, responseContainer = "List")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful update of the entity", response = Response.class)
        ,
        @ApiResponse(code = 500, message = "Internal server error")})
    default Response update(
            @ApiParam(value = "The id of the Object to update", required = true) @PathParam("id") String id,
            @ApiParam(value = "The object data to update", required = true) T entity) {
        try {
            log.debug("Updating {} with values {} [ RESTLAYER ]", getDao().getClassName(), entity.toString());
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
    @PUT
    @Path("update/{id}")
    @ApiOperation(value = "updateObject", notes = "Add or Delete subobjects to owning object", response = Response.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful ADD or DELETE of the sub entity", response = Response.class)
        ,
        @ApiResponse(code = 204, message = "No entity found to update", response = Response.class)
        ,
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    default Response updateObject(
            @ApiParam(value = "The id of the Object to update", required = true) @PathParam("id") String id,
            @ApiParam(value = "Information on the object to ADD, DELETE", required = true) ObjectUpdate update) {
        log.debug("Updating {} with values {} [ RESTLAYER ]", getDao().getClassName(), update.toString());
        try {
            Optional<T> updatedEntity = getDao().update(id, update);
            if (updatedEntity.isPresent()) {
                return Response.ok(updatedEntity.get(), MediaType.APPLICATION_JSON).build();
            } else {
                log.debug(" Error when Updating {} with values {} [ RESTLAYER ]", getDao().getClassName(), update.toString());
                return Response.noContent().build();
            }
        } catch (DaoException e) {
            log.error(" Error when Updating {} with values {} [ RESTLAYER ] ErrorMessage: {}", getDao().getClassName(), update.toString());
            return Response.serverError().build();
        }
    }

    /**
     * @param id the external id of the entity to delete
     * @return
     */
    @DELETE
    @Path("/{id}")
    @ApiOperation(value = "delete", notes = "deletes an object by its id", response = Response.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful deletion of the entity", response = Response.class)
        ,
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    default Response delete(
            @ApiParam(value = "The id of the Object to delete", required = true) @PathParam("id") String id) {
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
    @GET
    @Path("/{id}")
    @ApiOperation(value = "get", notes = "Gets an object by its id", response = Response.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful retrieval of the entity", response = Response.class)
        ,
        @ApiResponse(code = 204, message = "No entity found", response = Response.class)
        ,
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    default Response getById(
            @ApiParam(value = "The id of the Object to get", required = true) @PathParam("id") String id) {
        log.debug("Getting {} by id: {} [ RESTLAYER ]", getDao().getClassName(), id);
        try {
            Optional<T> entity = getDao().getById(id);
            if (entity.isPresent()) {
                return Response.ok(entity.get(), MediaType.APPLICATION_JSON).build();
            } else {
                return Response.noContent().build();
            }
        } catch (DaoException e) {
            log.error("Error when getting {} by id: {} [ RESTLAYER ] ErrorMessage: {}", getDao().getClassName(), id, e);
            return Response.serverError().build();
        }
    }

    /**
     * @param filter The filter for the search
     * @return the filtered list
     */
    @POST
    @Path("/filter")
    @ApiOperation(value = "getFilteredList", notes = "Get a filtered list of the owning object", response = Response.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful retrieval of the filteref entity list", response = Response.class)
        ,
        @ApiResponse(code = 204, message = "No entities found for the query", response = Response.class)
        ,
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    default Response getFilteredList(
            @ApiParam(value = "The filter to use when querying", required = true) GenericFilter filter) {
        log.debug("Getting all {} with filter {} [ RESTLAYER ] ", getDao().getClassName(), filter.toString());
        try {
            Optional<List<T>> responseList = getDao().search(filter);
            if (responseList.isPresent()) {
                List<T> entityList = responseList.get();
                return Response.ok(entityList, MediaType.APPLICATION_JSON).build();
            } else {
                return Response.noContent().build();
            }
        } catch (DaoException e) {
            log.error("Error when getting filtered list {} with values: {} [ RESTLAYER ] ErrorMessage: {}", getDao().getClassName(), filter.toString(), e);
            return Response.serverError().build();
        }
    }

    /**
     * @return The entitys searchfields
     */
    @GET
    @Path("/search/fields")
    @ApiOperation(value = "getSearchFields", notes = "Get all searchable fields for the owning object", response = Response.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful retrieval of the filteref entity list", response = Response.class)
        ,
        @ApiResponse(code = 204, message = "No searchfields found", response = Response.class)
        ,
        @ApiResponse(code = 500, message = "Internal server error")}
    )
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
     * @return The ok if the index went ok
     */
    @POST
    @Path("/index")
    @ApiOperation(value = "indexEntity", notes = "Indexes the owning object ( Reindexes Lucene )", response = Response.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful retrieval of the filteref entity list", response = Response.class)
        ,
        @ApiResponse(code = 500, message = "Internal server error")}
    )
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
