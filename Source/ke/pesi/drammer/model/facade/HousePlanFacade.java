/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.pesi.drammer.model.facade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import ke.pesi.drammer.model.dao.HousePlan;

/**
 *n
 * @author kelly
 */
@Stateless
public class HousePlanFacade extends AbstractFacade<HousePlan> {
    
    @PersistenceContext(unitName = "ramani-digitalPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HousePlanFacade() {
        super(HousePlan.class);
    }
    
}