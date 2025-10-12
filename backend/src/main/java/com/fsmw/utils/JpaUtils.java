package com.fsmw.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JpaUtils {
    private static final String PERSISTENCE_UNIT_NAME = "mw_db";
    private static volatile EntityManagerFactory emf;

    private JpaUtils() {
        throw new IllegalArgumentException("'JpaUtils' cannot be instantiated.");
    }

    private static EntityManagerFactory getEmf() {
        if (emf == null) {
            synchronized (JpaUtils.class) {
                if (emf == null) {
                    emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
                }
            }
        }

        return emf;
    }

    public static EntityManager getEm() {
        return getEmf().createEntityManager();
    }

    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
