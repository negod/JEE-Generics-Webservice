/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.registry;

import com.negod.generics.persistence.entity.GenericEntity;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
@Data
@XmlRootElement
public class RegistryEntity extends GenericEntity {

    public RegistryEntity() {
    }
    
    @XmlElement
    private String serviceName;
    @XmlElement
    private String url;
    @XmlElement
    private Boolean online;

}
