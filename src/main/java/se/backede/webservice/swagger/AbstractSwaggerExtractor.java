/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.swagger;

import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.parser.SwaggerParser;
import io.swagger.parser.util.SwaggerDeserializationResult;
import io.swagger.util.Json;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.ejb.EJB;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import se.backede.webservice.service.EntityRegistrySingleton;
import se.backede.webservice.service.RestService;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
@Slf4j
public abstract class AbstractSwaggerExtractor {

    public abstract Set<Class<?>> getServiceClasses();

    @EJB
    EntityRegistrySingleton entityRegistry;

    Optional<String> SWAGGER_JSON = Optional.empty();

    public Optional<String> getSwagger() throws IOException, InstantiationException, IllegalAccessException {
        log.trace("Getting Manupulated Swagger json");

        if (SWAGGER_JSON.isPresent()) {
            return SWAGGER_JSON;
        }

        Optional<Swagger> swagger = getSwaggerFromJson();

        if (swagger.isPresent()) {

            entityRegistry.getRegisteredEntities().forEach((Class entry) -> {
                swagger.get().getDefinitions().put(entry.getSimpleName(), new GenericSwaggerModel(entry));
            });

        } else {
            return Optional.empty();
        }

        getServiceClasses().forEach((Class clazz) -> {
            try {
                if (clazz.newInstance() instanceof RestService) {
                    Optional<String> basePath = PropertyCreator.getInstance().getBasePath(clazz);

                    Optional<Class<?>> classTypeForRestService = getClassTypeOfrestService(clazz);
                    if (classTypeForRestService.isPresent() && basePath.isPresent()) {

                        Optional<Path> path = Optional.ofNullable(swagger.get().getPath(basePath.get()));

                        if (path.isPresent()) {

                            log.trace("Handling basepath {}", basePath);

                            handleRequest(Optional.ofNullable(path.get().getPost()), classTypeForRestService.get(), true);
                            handleRequest(Optional.ofNullable(path.get().getGet()), classTypeForRestService.get(), false);

                            Optional<Path> getByIdPath = Optional.ofNullable(swagger.get().getPath(basePath + "/{id}"));
                            if (getByIdPath.isPresent()) {
                                handleRequest(Optional.ofNullable(getByIdPath.get().getGet()), classTypeForRestService.get(), false);
                                handleRequest(Optional.ofNullable(getByIdPath.get().getPut()), classTypeForRestService.get(), false);
                                handleRequest(Optional.ofNullable(getByIdPath.get().getDelete()), classTypeForRestService.get(), false);
                            }

                            Optional<Path> filterPath = Optional.ofNullable(swagger.get().getPath(basePath + "/filter"));
                            if (filterPath.isPresent()) {
                                handleRequest(Optional.ofNullable(filterPath.get().getPost()), classTypeForRestService.get(), false);
                            }

                            Optional<Path> searchFieldPath = Optional.ofNullable(swagger.get().getPath(basePath + "/search/fields"));
                            if (searchFieldPath.isPresent()) {
                                handleRequest(Optional.ofNullable(searchFieldPath.get().getGet()), new HashSet<String>().getClass(), false);
                            }

                            Optional<Path> objectUpdatePath = Optional.ofNullable(swagger.get().getPath(basePath + "/update/{id}"));
                            if (objectUpdatePath.isPresent()) {
                                handleRequest(Optional.ofNullable(objectUpdatePath.get().getPut()), classTypeForRestService.get(), false);
                            }
                        }
                    }
                }
            } catch (InstantiationException | IllegalAccessException ex) {
                log.error("Error when handling paths ErrorMessage {}", ex);
            }
        });

        SWAGGER_JSON = Optional.ofNullable(Json.pretty(swagger.get()));
        return SWAGGER_JSON;
    }

    private Optional<Swagger> getSwaggerFromJson() {
        log.trace("Getting swagger from JSON file from resources , method:getSwaggerFromJson");
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream input = classLoader.getResourceAsStream("swagger.json");
            String swaggerFile = swaggerFile = IOUtils.toString(input, Charset.defaultCharset());
            SwaggerDeserializationResult readWithInfo = new SwaggerParser().readWithInfo(swaggerFile);
            Swagger swagger = readWithInfo.getSwagger();
            return Optional.ofNullable(swagger);
        } catch (IOException ex) {
            log.error("Error when extracting Swagger Parser from Json file, ErrorMessage: {}", ex);
        }
        return Optional.empty();
    }

    private Optional<Class<?>> getClassTypeOfrestService(Class restService) {
        log.trace("Getting the classtype that is used for the webservice, method:getClassTypeOfrestService");
        Type[] genericInterfaces = restService.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                Type[] genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
                for (Type genericType : genericTypes) {
                    return Optional.ofNullable((Class) genericType);
                }
            }
        }
        return Optional.empty();
    }

    private void handleRequest(Optional<Operation> operation, Class<?> clazz, Boolean addInParam) {
        if (operation.isPresent()) {
            log.trace("Setting params for Operation {} for classType {}, method:handleRequest", operation.get().getOperationId(), clazz.getSimpleName());
            Optional<Map<String, Response>> responses = Optional.ofNullable(operation.get().getResponses());
            if (responses.isPresent()) {
                setSchemaResponses(responses.get(), clazz);
            }
            if (addInParam) {
                if (operation.get().getParameters().size() == 1) {
                    try {
                        BodyParameter get = (BodyParameter) operation.get().getParameters().get(0);
                        get.setSchema(new GenericSwaggerModel(clazz));
                    } catch (ClassCastException e) {
                        log.error("Error when trying to cast param to BodyParam method:handleRequest Error:{} ", e);
                    }
                }
            }
        }
    }

    private void setSchemaResponses(Map<String, Response> responses, Class<?> clazz) {
        responses.entrySet().forEach((Map.Entry<String, Response> entry) -> {
            switch (entry.getKey()) {
                case "200":
                    entry.getValue().setSchema(PropertyCreator.getInstance().getRefProperty(clazz));
                    break;
                case "204":
                    entry.getValue().setSchema(null);
                    break;
                case "500":
                    entry.getValue().setSchema(null);
                    break;
                default:
                    break;
            }
        });
    }

}
