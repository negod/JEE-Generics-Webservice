/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.service.mocks.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import se.backede.generics.persistence.dto.GenericDto;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
@Getter
@Setter
@ToString
public class DtoMock extends GenericDto {

    private String name;

}
