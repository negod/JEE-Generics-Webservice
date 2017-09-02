/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.service.methods;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
@FunctionalInterface
public interface Update<T> {

    public T update(T entity);
}
