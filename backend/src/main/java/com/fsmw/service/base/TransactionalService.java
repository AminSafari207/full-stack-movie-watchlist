package com.fsmw.service.base;

import com.fsmw.utils.JpaUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.function.Function;

import java.util.Objects;

public abstract class TransactionalService {
    @FunctionalInterface
    public interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;
    }

    @FunctionalInterface
    public interface ThrowingConsumer<T> {
        void accept(T t) throws Exception;
    }

    public void executeTransactionVoid(ThrowingConsumer<EntityManager> consumer) {
        Objects.requireNonNull(consumer, "consumer must not be null");
        executeVoid(consumer);
    }

    public <R> R executeTransaction(Function<EntityManager, R> function) {
        Objects.requireNonNull(function, "function must not be null");
        return executeWithReturn(function::apply);
    }

    public <R> R executeTransactionThrowing(ThrowingFunction<EntityManager, R> throwingFunction) {
        Objects.requireNonNull(throwingFunction, "throwingFunction must not be null");
        return executeWithReturn(throwingFunction);
    }

    private <R> R executeWithReturn(ThrowingFunction<EntityManager, R> action) {
        try (EntityManager em = JpaUtils.getEm()) {
            EntityTransaction tx = em.getTransaction();

            try {
                tx.begin();
                R result = action.apply(em);
                tx.commit();
                return result;
            } catch (Exception e) {
                rollbackQuietly(tx);
                handleAndRethrow(e);
                return null;
            }
        }
    }

    private void executeVoid(ThrowingConsumer<EntityManager> action) {
        try (EntityManager em = JpaUtils.getEm()) {
            EntityTransaction tx = em.getTransaction();

            try {
                tx.begin();
                action.accept(em);
                tx.commit();
            } catch (Exception e) {
                rollbackQuietly(tx);
                handleAndRethrow(e);
            }
        }
    }

    private void rollbackQuietly(EntityTransaction tx) {
        if (tx != null && tx.isActive()) {
            try {
                tx.rollback();
            } catch (Exception rbEx) {
                System.err.println("Rollback failed: " + rbEx.getMessage());
                rbEx.printStackTrace(System.err);
            }
        }
    }

    private void handleAndRethrow(Exception e) {
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }

        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        }

        throw new RuntimeException("Transaction failed.", e);
    }
}
