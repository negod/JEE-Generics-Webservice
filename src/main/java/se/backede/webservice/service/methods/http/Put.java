/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.service.methods.http;

import com.negod.generics.persistence.update.ObjectUpdate;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
public interface Put extends HttpsMethod{
    
    ObjectUpdate getObjectUpdate();
    
}
