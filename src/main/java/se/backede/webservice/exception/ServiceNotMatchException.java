/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.exception;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
public class ServiceNotMatchException extends Exception {

    public ServiceNotMatchException(String message) {
        super(message);
    }

    public ServiceNotMatchException(String message, Throwable cause) {
        super(message, cause);
    }

}
