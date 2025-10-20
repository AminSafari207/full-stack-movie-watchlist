package com.fsmw.config;

import com.fsmw.service.RolePermissionSeeder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppStartupListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        new RolePermissionSeeder(PersistenceUnit.MW).seed();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        JpaUtils.shutdown();
    }
}
