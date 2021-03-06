/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.registry;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import se.backede.generics.persistence.entity.EntityRegistry;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
@Singleton
@Startup
public class EntityRegistrySingleton extends EntityRegistry {

    @PersistenceContext
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @PostConstruct
    public void init() {
        super.registerEnties();
        super.registerSearchFields();
        super.registerSearchFieldCaches();
    }

}
