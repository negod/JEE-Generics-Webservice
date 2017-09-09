/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.registry;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import se.backede.webservice.constants.PathConstants;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
@Stateless
public class HealthCheck {

    @GET
    @Path(PathConstants.PATH_ONLINE)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response isOnline() {
        return Response.ok().build();
    }

}
