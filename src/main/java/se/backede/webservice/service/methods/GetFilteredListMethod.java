/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.service.methods;

import com.negod.generics.persistence.search.GenericFilter;
import java.util.Set;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 * @param <T>
 */
public interface GetFilteredListMethod<T> {

    public Set<T> getFilteredList(GenericFilter filter);
}
