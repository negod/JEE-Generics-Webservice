/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.security;

import se.backede.webservice.exception.AuthorizationException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import javax.ws.rs.core.UriInfo;
import se.backede.webservice.constants.PathConstants;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
@Stateless
public abstract class Authentication {

    abstract public AuthorizationDao getDao();

    @Context
    UriInfo uriInfo;

    @EJB
    KeyProvider keyProvider;

    @POST
    @Path(PathConstants.PATH_LOGIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authenticate(Credentials credentials) {
        try {
            if (getDao().authorize(credentials)) {
                return Response.ok().header(AUTHORIZATION, SecurityConstants.BEARER_SCHEME + keyProvider.generateJWT(credentials.getUsername(), uriInfo)).build();
            }
        } catch (AuthorizationException ex) {
            return Response.status(UNAUTHORIZED).build();
        }
        return Response.status(UNAUTHORIZED).build();
    }

}
