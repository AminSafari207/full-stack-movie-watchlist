package com.fsmw.config;

import lombok.Getter;

@Getter
public enum PersistenceUnit {
    MW("mw_db"),
    TEST("h2_test");

    private final String unitName;

    PersistenceUnit(String unitName) {
        this.unitName = unitName;
    }
}