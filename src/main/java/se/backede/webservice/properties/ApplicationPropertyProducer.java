/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.properties;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
@Dependent
@Slf4j
public class ApplicationPropertyProducer {
    
    @Inject
    private PropertyFileResolver fileResolver;
    
    @Produces
    @ApplicationProperty(name = "")
    public String getPropertyAsString(InjectionPoint injectionPoint) {
        
        String propertyName = injectionPoint.getAnnotated().getAnnotation(ApplicationProperty.class).name();
        log.debug("Retrieving propery {}", propertyName);
        String value = fileResolver.getProperty(propertyName);
        
        if (value == null || propertyName.trim().length() == 0) {
            throw new IllegalArgumentException("No property found with name " + value);
        }
        return value;
    }
    
    @Produces
    @ApplicationProperty(name = "")
    public Integer getPropertyAsInteger(InjectionPoint injectionPoint) {
        
        String value = getPropertyAsString(injectionPoint);
        return value == null ? null : Integer.valueOf(value);
    }
}
