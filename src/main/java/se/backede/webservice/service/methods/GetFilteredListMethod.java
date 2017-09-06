/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.service.methods;

import com.negod.generics.persistence.search.GenericFilter;
import lombok.Data;
import se.backede.webservice.service.methods.https.HttpsMethod;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 * @param <T>
 */
@Data
public class GetFilteredListMethod<String> extends HttpsMethod {

    GenericFilter filter;
}
