/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.service.client.methods;

import com.negod.generics.persistence.update.ObjectUpdate;
import lombok.Data;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
@Data
public class ObjectUpdateMethod extends UpdateMethod {

    
    private ObjectUpdate updateType;
}
