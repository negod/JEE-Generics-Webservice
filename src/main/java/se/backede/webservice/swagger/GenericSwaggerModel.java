/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.swagger;

import io.swagger.models.AbstractModel;
import io.swagger.models.ExternalDocs;
import io.swagger.models.properties.Property;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import lombok.extern.slf4j.Slf4j;
import se.backede.generics.persistence.entity.GenericEntity;
import se.backede.generics.persistence.exception.DaoException;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
@Slf4j
public class GenericSwaggerModel<T extends GenericEntity> extends AbstractModel {

    private Map<String, Property> properties = new HashMap<>();
    private Map<String, Object> vendorExtensions = new HashMap<>();
    private T example;
    private ExternalDocs docs;
    private String reference = "reference";
    private String description = "description";

    Class<T> entityClass;

    public GenericSwaggerModel(Class<T> entityClass) {
        try {
            this.entityClass = entityClass;
            properties = extractFields(entityClass);
        } catch (DaoException ex) {
            log.error("Error when extracting searchFields {} [ DatabaseLayer ]", ex);
        }
    }

    private HashMap<String, Property> extractFields(Class<T> entityClass) throws DaoException {
        log.trace("Extracting searchfields ( SWAGGER ) for entity class {} [ RestLayer ] method:extractFields", entityClass.getSimpleName());
        try {
            String fieldAnnotation = XmlElement.class.getName();
            HashMap<String, Property> fields = new HashMap<>();
            Field[] declaredFields = entityClass.getDeclaredFields();
            for (Field field : declaredFields) {
                Annotation[] annotations = field.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType().getName().equals(fieldAnnotation)) {
                        fields.put(field.getName(), PropertyCreator.getInstance().getProperty(field));
                    }
                }
            }
            return fields;
        } catch (IllegalArgumentException ex) {
            log.error("Error when extracting searchFields {} [ RestLayer ]", ex);
            throw new DaoException("Error whgen extracting serachFields {}", ex);
        }
    }

    @Override
    public Map<String, Property> getProperties() {
        return properties;
    }

    @Override
    public void setProperties(Map<String, Property> arg0) {
        properties = arg0;
    }

    @Override
    public Object getExample() {
        return example;
    }

    @Override
    public void setExample(Object arg0) {
        example = (T) arg0;
    }

    @Override
    public ExternalDocs getExternalDocs() {
        return docs;
    }

    @Override
    public String getReference() {
        return reference;
    }

    @Override
    public void setReference(String arg0) {
        reference = arg0;
    }

    @Override
    public Map<String, Object> getVendorExtensions() {
        return vendorExtensions;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String arg0) {
        description = arg0;
    }
}
