/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.service.mocks;

import se.backede.generics.persistence.GenericDaoAbs;
import se.backede.generics.persistence.dto.GenericDto;
import se.backede.generics.persistence.entity.GenericEntity;
import se.backede.generics.persistence.mapper.BaseMapper;
import se.backede.generics.persistence.mapper.DtoEntityBaseMapper;
import se.backede.webservice.service.RestService;
import se.backede.webservice.service.mocks.dto.DtoMock;
import se.backede.webservice.service.mocks.dto.EntityMock;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
public class RestServiceImpl extends RestService<DtoMock, EntityMock> {

    DtoEntityBaseMapper<DtoMock, EntityMock> mapper = new DtoEntityBaseMapper(DtoMock.class, EntityMock.class);

    GenericDaoAbs dao;

    @Override
    public GenericDaoAbs getDao() {
        return dao;
    }

    @Override
    public DtoEntityBaseMapper<DtoMock, EntityMock> getMapper() {
        return mapper;
    }

}
