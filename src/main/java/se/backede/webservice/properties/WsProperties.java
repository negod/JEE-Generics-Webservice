/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.properties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Data;
import se.backede.webservice.constants.PropertyConstants;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
@Named
@Data
@ApplicationScoped
public class WsProperties {

    @Inject
    @ApplicationProperty(name = PropertyConstants.KEY_STORE_PATH)
    private String keyStorePath;

    @Inject
    @ApplicationProperty(name = PropertyConstants.CERT_PATH)
    private String certPath;

    @Inject
    @ApplicationProperty(name = PropertyConstants.PATH_STRING_REPLACEMENT)
    private String pathReplacement;

    @Inject
    @ApplicationProperty(name = PropertyConstants.SERVICE_USER)
    private String user;

    @Inject
    @ApplicationProperty(name = PropertyConstants.SERVICE_PASS)
    private String password;

    @Inject
    @ApplicationProperty(name = PropertyConstants.SERVER_ROOT_PATH)
    private String rootPath;

}
