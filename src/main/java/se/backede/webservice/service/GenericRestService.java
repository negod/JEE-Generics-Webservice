package se.backede.webservice.service;

import com.negod.generics.persistence.GenericDao;
import com.negod.generics.persistence.entity.GenericEntity;
import com.negod.generics.persistence.search.GenericFilter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Joakim Johansson ( joakimjohansson@outlook.com )
 * @param <T>
 * @exclude
 *
 */
@Slf4j
public abstract class GenericRestService<T extends GenericEntity> implements RestService<T> {

    public abstract GenericDao getDao();

    /**
     * Creates an entity
     *
     * @param entity
     * @return
     */
    public Response create(T entity) {
        log.debug("Creating {} with values {}", getDao().getClassName(), entity.toString());
        try {
            Optional<T> createdEntity = getDao().persist(entity);
            if (createdEntity.isPresent()) {
                return Response.ok(createdEntity.get(), MediaType.APPLICATION_JSON).build();
            } else {
                return Response.serverError().build();
            }
        } catch (Exception e) {
            log.error("Error when creating {} with values {}", getDao().getClassName(), entity.toString(), e);
            return Response.serverError().build();
        }
    }

    public Response getAll() {
        try {
            log.debug("Getting all of type {}" + getDao().getClassName());
            Optional<List<T>> entityList = getDao().getAll();
            if (entityList.isPresent()) {
                return Response.ok(entityList.get(), MediaType.APPLICATION_JSON).build();
            } else {
                return Response.noContent().build();
            }
        } catch (Exception e) {
            log.debug("Error when getting all of type {}", getDao().getClassName());
            return Response.serverError().build();
        }
    }

    /**
     * Updates an entity
     *
     * @param entity
     * @return
     */
    public Response update(String id, T entity) {
        log.debug("Updating {} with values {}", getDao().getClassName(), entity.toString());
        try {
            Optional<T> updatedEntity = getDao().update(entity);
            if (updatedEntity.isPresent()) {
                return Response.ok(updatedEntity.get(), MediaType.APPLICATION_JSON).build();
            } else {
                return Response.serverError().build();
            }
        } catch (Exception e) {
            log.error("Error when updating {} with values {}", getDao().getClassName(), entity.toString(), e);
            return Response.serverError().build();
        }
    }

    /**
     * Deletes an entity
     *
     * @param id
     */
    public void delete(String id) {
        log.debug("Deleting {} with ID {}", getDao().getClassName(), id);
        try {
            getDao().delete(id);
        } catch (Exception e) {
            log.error("Error when deleting {} with id {} ", getDao().getClassName(), id, e);
        }
    }

    /**
     * Gets an entity by the external id.
     *
     * @param id
     * @return
     */
    public Response getById(String id) {
        try {
            log.debug("Getting {} by id: {}", getDao().getClassName(), id);
            Optional<T> entity = getDao().getById(id);
            if (entity.isPresent()) {
                return Response.ok(entity.get(), MediaType.APPLICATION_JSON).build();
            } else {
                return Response.noContent().build();
            }
        } catch (Exception e) {
            log.debug("Error when getting {} by id: {}", getDao().getClassName(), id);
            return Response.serverError().build();
        }
    }

    /**
     * Gets a list of the entity with filtration
     *
     * @param filter
     * @return
     */
    public Response getFilteredList(GenericFilter filter) {
        try {
            log.debug("Getting all {} with filter {}", getDao().getClassName(), filter.toString());
            Optional<List<T>> responseList = getDao().getAll(filter);
            if (responseList.isPresent()) {
                List<T> entityList = responseList.get();
                return Response.ok(entityList, MediaType.APPLICATION_JSON).build();
            } else {
                return Response.noContent().build();
            }
        } catch (Exception e) {
            log.error("Error when getting filtered list {}", e);
            return Response.serverError().build();
        }
    }

    /**
     * Gets all searchfields available in the entity
     *
     * @return
     */
    public Response getSearchFields() {
        log.debug("Getting all search fields for {} ", getDao().getClassName());
        Set<String> searchFields = getDao().getSearchFields();
        return Response.ok(searchFields, MediaType.APPLICATION_JSON).build();
    }

    /**
     * Creates a Lucene index for the entity
     *
     * @return
     */
    public Response indexEntity() {
        log.debug("Indexing entity {} ", getDao().getClassName());
        return Response.ok(getDao().indexEntity(), MediaType.WILDCARD_TYPE).build();
    }

}
