/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.service.mocks.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import se.backede.generics.persistence.entity.GenericEntity;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
@Data
@XmlRootElement
@Getter
@Setter
public class EntityMock extends GenericEntity {

    @XmlElement
    private String name;

}
