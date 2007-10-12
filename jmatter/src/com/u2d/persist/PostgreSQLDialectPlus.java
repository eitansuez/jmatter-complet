package com.u2d.persist;

import java.sql.Types;

import org.hibernate.Hibernate;

public class PostgreSQLDialectPlus extends org.hibernate.dialect.PostgreSQLDialect {
    public PostgreSQLDialectPlus() {
        super();
        registerColumnType(Types.DECIMAL, "number($p,$s)");
        registerHibernateType(Types.DECIMAL, "big_decimal");
    }
}
