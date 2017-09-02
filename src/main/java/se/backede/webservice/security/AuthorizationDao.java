/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.security;

import se.backede.webservice.exception.AuthorizationException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
@LocalBean
@Stateless
public interface AuthorizationDao {

    public Boolean authorize(Credentials creds) throws AuthorizationException;

}
