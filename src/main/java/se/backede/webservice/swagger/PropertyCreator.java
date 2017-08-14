/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.negod.generics.persistence.entity.GenericEntity;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.DateProperty;
import io.swagger.models.properties.DoubleProperty;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import io.swagger.models.properties.LongProperty;
import io.swagger.models.properties.RefProperty;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
@Slf4j
public class PropertyCreator<T extends GenericEntity> {

    private static final PropertyCreator INSTANCE = new PropertyCreator();

    protected PropertyCreator() {
    }

    final public static PropertyCreator getInstance() {
        return INSTANCE;
    }

    public Property getRefProperty(Class<T> clazz) {
        RefProperty property = new RefProperty();
        property.set$ref(clazz.getSimpleName());
        return property;
    }

    public Property getProperty(Field field) {
        log.trace("Getting property for field {} Method:getProperty", field.getName());

        Class<?> clazz = field.getType();

        Property retProperty = new ObjectProperty();

        if (clazz.equals(String.class)) {
            retProperty = new StringProperty();
        } else if (clazz.equals(Integer.class)) {
            retProperty = new IntegerProperty();
        } else if (clazz.equals(Long.class)) {
            retProperty = new LongProperty();
        } else if (clazz.equals(Double.class)) {
            retProperty = new DoubleProperty();
        } else if (clazz.equals(Date.class)) {
            retProperty = new DateProperty();
        } else if (clazz.equals(Set.class) || clazz.equals(List.class)) {
            ArrayProperty arrayProperty = new ArrayProperty();
            ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
            Class<?> entityClass = (Class<?>) stringListType.getActualTypeArguments()[0];
            if (entityClass.getSuperclass() == GenericEntity.class) {
                RefProperty refProp = new RefProperty();
                refProp.set$ref(entityClass.getSimpleName());
                arrayProperty.setItems(refProp);
            } else if (entityClass.equals(String.class)) {
                StringProperty stringProp = new StringProperty();
                arrayProperty.setItems(stringProp);
            }
            retProperty.setName(field.getName());
            return arrayProperty;
        }

        if (clazz.getSuperclass() == GenericEntity.class) {
            Class<T> entityClass = (Class<T>) clazz;
            RefProperty refProp = new RefProperty();
            refProp.set$ref(entityClass.getSimpleName());
            refProp.setName(field.getName());
            return refProp;
        }

        retProperty.setName(field.getName());
        return retProperty;

    }

    public String createExampleFromObject(Class<T> object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(object.newInstance());
        } catch (InstantiationException | IllegalAccessException | JsonProcessingException ex) {
            Logger.getLogger(PropertyCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public Optional<String> getBasePath(Class<?> clazz) {
        log.trace("Extracting basePath ( SWAGGER ) for entity class {} [ RestLayer ] method:getBasePath", clazz.getSimpleName());
        try {
            String fieldAnnotation = Path.class.getName();
            for (Annotation annotation : clazz.getAnnotations()) {
                if (annotation.annotationType().getName().equals(fieldAnnotation)) {
                    Path extracted = (Path) annotation;
                    return Optional.ofNullable(extracted.value());
                }
            }
        } catch (IllegalArgumentException ex) {
            log.error("Error when extracting searchFields {} [ RestLayer ]", ex);
        }
        return Optional.empty();
    }

}
