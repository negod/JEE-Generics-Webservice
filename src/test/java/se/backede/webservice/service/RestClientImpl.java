/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.service;

import se.backede.webservice.service.client.RestClient;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
public class RestClientImpl extends RestClient<TestEntity> {

    @Override
    public String getRootPath() {
        return "https://192.168.2.140:8443/registry-1.0-SNAPSHOT/rest/";
    }

    @Override
    public String getKeyStorePath() {
        return "C:\\Programmering\\ssl\\backede.se.jks";
    }

    @Override
    public String getCertPath() {
        return "C:\\Programmering\\ssl\\backede.se.crt";
    }

    @Override
    public Class<TestEntity> getEntityClass() {
        return TestEntity.class;
    }

}
