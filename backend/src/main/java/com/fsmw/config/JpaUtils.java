package com.fsmw.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JpaUtils {
    private static final PersistenceUnit DEFAULT_UNIT = PersistenceUnit.MW;
    private static volatile EntityManagerFactory emf;

    private JpaUtils() {
        throw new IllegalArgumentException("'JpaUtils' cannot be instantiated.");
    }

    public static EntityManagerFactory getEmf(PersistenceUnit unit) {
        if (emf == null) {
            synchronized (JpaUtils.class) {
                if (emf == null) {
                    emf = Persistence.createEntityManagerFactory(unit.getUnitName());
                }
            }
        }

        return emf;
    }

    public static EntityManagerFactory getDefaultEmf() {
        return getEmf(DEFAULT_UNIT);
    }

    public static EntityManager getEm(PersistenceUnit unit) {
        return getEmf(unit).createEntityManager();
    }

    public static EntityManager getEm() {
        return getDefaultEmf().createEntityManager();
    }


    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
