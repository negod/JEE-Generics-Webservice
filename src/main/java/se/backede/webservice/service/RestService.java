package se.backede.webservice.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import se.backede.generics.persistence.GenericDao;
import se.backede.generics.persistence.entity.GenericEntity;
import se.backede.generics.persistence.mapper.BaseMapper;
import se.backede.generics.persistence.search.GenericFilter;
import se.backede.generics.persistence.update.ObjectUpdate;
import se.backede.webservice.constants.PathConstants;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 * @param <D> The dto object
 * @param <E> The Entity object
 */
@Api
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public abstract class RestService<D, E extends GenericEntity> {

    final Logger log = LoggerFactory.getLogger(RestService.class);

    public abstract GenericDao<E> getDao();

    public abstract BaseMapper<D, E> getMapper();

    /**
     *
     * @param entity
     * @return
     */
    @POST
    @Path(PathConstants.PATH_BASE_PATH)
    @ApiOperation(value = "create", notes = "Returns the persisted object")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful persisting the entity", response = Response.class),
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response create(@ApiParam(value = "The Object to create", required = true) D dto) {
        log.debug("Creating {} with values {} [ RESTLAYER ]", getDao().getClassName(), dto.toString());
        try {

            Optional<E> mapFromDtoToEntity = getMapper().mapFromDtoToEntity(dto);

            if (mapFromDtoToEntity.isPresent()) {

                Optional<E> createdEntity = getDao().persist(mapFromDtoToEntity.get());
                if (createdEntity.isPresent()) {

                    Optional<D> mapFromEntityToDto = getMapper().mapFromEntityToDto(createdEntity.get());

                    if (mapFromEntityToDto.isPresent()) {

                        return Response.ok(mapFromEntityToDto.get(), MediaType.APPLICATION_JSON).build();
                    }
                } else {
                    return Response.serverError().build();
                }

            }
            return Response.serverError().build();

        } catch (Exception ex) {
            log.error("ConstraintViolation when creating {} with values {} [ RESTLAYER ] Error: {}", getDao().getClassName(), dto.toString(), ex);
            return Response.status(Response.Status.NOT_MODIFIED).build();
        }
    }

    /**
     *
     * @return
     */
    @GET
    @Path(PathConstants.PATH_BASE_PATH)
    @ApiOperation(value = "getAll", notes = "Returns a list of Objects")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful retrieval of the entity", response = Response.class, responseContainer = "Set"),
        @ApiResponse(code = 204, message = "No entity found"),
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response getAll() {
        log.debug("Getting all of type {} [ RESTLAYER ] " + getDao().getClassName());
        try {

            Optional<Set<E>> entityList = getDao().getAll();

            if (entityList.isPresent()) {

                Optional<Set<D>> mapToDtoList = getMapper().mapToDtoSet(entityList.get());

                if (mapToDtoList.isPresent()) {
                    return Response.ok(mapToDtoList.get(), MediaType.APPLICATION_JSON).build();
                } else {
                    return Response.noContent().build();
                }

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
     * @param dto
     * @return
     */
    @PUT
    @Path(PathConstants.PATH_GET_BY_ID)
    @ApiOperation(value = "update", notes = "Update the owning object")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful update of the entity", response = Response.class),
        @ApiResponse(code = 204, message = "No entity found for the provided id"),
        @ApiResponse(code = 500, message = "Internal server error")})
    public Response update(
            @ApiParam(value = "The id of the Object to update", required = true) @PathParam("id") String id,
            @ApiParam(value = "The object data to update", required = true) D dto) {
        try {
            log.debug("Updating {} with values {} [ RESTLAYER ]", getDao().getClassName(), dto.toString());

            if (Optional.ofNullable(id).isPresent()) {

                Optional<E> entity = getMapper().mapFromDtoToEntity(dto);
                if (entity.isPresent()) {
                    entity.get().setId(id);
                    Optional<E> updatedEntity = getDao().update(entity.get());

                    if (updatedEntity.isPresent()) {
                        return Response.ok(updatedEntity.get(), MediaType.APPLICATION_JSON).build();
                    } else {
                        return Response.noContent().build();
                    }

                } else {
                    return Response.serverError().build();
                }

            } else {
                return Response.ok("ID not present in request [ RESTLAYER ]", MediaType.APPLICATION_JSON).build();
            }
        } catch (Exception e) {
            log.error("Error when updating {} with values {} [ RESTLAYER ] ErrorMessage: {}", getDao().getClassName(), dto.toString(), e);
            return Response.serverError().build();
        }
    }

    /**
     *
     * @param id
     * @param update
     * @return
     */
    @PUT
    @Path(PathConstants.PATH_UPDATE)
    @ApiOperation(value = "updateObject", notes = "Add or Delete subobjects to owning object")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful ADD or DELETE of the sub entity", response = Response.class),
        @ApiResponse(code = 204, message = "No entity found to update"),
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response updateObject(
            @ApiParam(value = "The id of the Object to update", required = true) @PathParam("id") String id,
            @ApiParam(value = "Information on the object to ADD, DELETE", required = true) ObjectUpdate update) {
        log.debug("Updating {} with values {} [ RESTLAYER ]", getDao().getClassName(), update.toString());
        try {

            Optional<E> updatedEntity = getDao().update(id, update);

            if (updatedEntity.isPresent()) {
                Optional<D> mapFromEntityToDto = getMapper().mapFromEntityToDto(updatedEntity.get());
                if (mapFromEntityToDto.isPresent()) {
                    return Response.ok(mapFromEntityToDto.get(), MediaType.APPLICATION_JSON).build();
                } else {
                    return Response.serverError().build();
                }
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
    @DELETE
    @Path(PathConstants.PATH_GET_BY_ID)
    @ApiOperation(value = "delete", notes = "deletes an object by its id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful deletion of the entity", response = Response.class),
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response delete(
            @ApiParam(value = "The id of the Object to delete", required = true) @PathParam("id") String id) {
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
    @GET
    @Path(PathConstants.PATH_GET_BY_ID)
    @ApiOperation(value = "get", notes = "Gets an object by its id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful retrieval of the entity", response = Response.class),
        @ApiResponse(code = 204, message = "No entity found"),
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response getById(
            @ApiParam(value = "The id of the Object to get", required = true) @PathParam("id") String id) {
        log.debug("Getting {} by id: {} [ RESTLAYER ]", getDao().getClassName(), id);
        try {
            Optional<E> entity = getDao().getById(id);
            if (entity.isPresent()) {

                Optional<D> mapFromEntityToDto = getMapper().mapFromEntityToDto(entity.get());
                if (mapFromEntityToDto.isPresent()) {
                    return Response.ok(mapFromEntityToDto.get(), MediaType.APPLICATION_JSON).build();
                } else {
                    return Response.serverError().build();
                }
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
    @POST
    @Path(PathConstants.PATH_FILTER)
    @ApiOperation(value = "getFilteredList", notes = "Get a filtered list of the owning object")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful retrieval of the filteref entity list", response = Response.class, responseContainer = "Set"),
        @ApiResponse(code = 204, message = "No entities found for the query"),
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response getFilteredList(
            @ApiParam(value = "The filter to use when querying", required = true) GenericFilter filter) {
        log.debug("Getting all {} with filter {} [ RESTLAYER ] ", getDao().getClassName(), filter.toString());
        Optional<Set<E>> responseList = getDao().search(filter);
        if (responseList.isPresent()) {

            Optional<Set<D>> mapToDtoSet = getMapper().mapToDtoSet(responseList.get());
            if (mapToDtoSet.isPresent()) {
                return Response.ok(mapToDtoSet.get(), MediaType.APPLICATION_JSON).build();
            }
            return Response.serverError().build();
        } else {
            return Response.noContent().build();
        }
    }

    /**
     *
     * @return
     */
    @GET
    @Path(PathConstants.PATH_SEARCH_FIELDS)
    @ApiOperation(value = "getSearchFields", notes = "Get all searchable fields for the owning object")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful retrieval of the filteref entity list", response = String.class, responseContainer = "Set"),
        @ApiResponse(code = 204, message = "No searchfields found"),
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response getSearchFields() {
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
    @GET
    @Path(PathConstants.PATH_INDEX)
    @ApiOperation(value = "indexEntity", notes = "Indexes the owning object ( Reindexes Lucene )")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful retrieval of the filteref entity list", response = Boolean.class),
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response indexEntity() {
        try {
            log.debug("Indexing entity {} [ RESTLAYER ]", getDao().getClassName());
            return Response.ok(getDao().indexEntity(), MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            log.error("Error building index for type: {} [ RESTLAYER ] ErrorMessage: {}", getDao().getClassName(), e);
            return Response.serverError().build();
        }
    }

}
