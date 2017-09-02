/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.security;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
@Data
@XmlRootElement
public class Credentials implements Serializable {

    public Credentials() {
    }
    
    private String username;
    private String password;

}
