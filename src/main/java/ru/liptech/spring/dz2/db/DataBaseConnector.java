package ru.liptech.spring.dz2.db;

import org.hibernate.*;

import org.hibernate.cfg.Configuration;
import ru.liptech.spring.dz2.model.User;


public final class DataBaseConnector {
    private static SessionFactory session = buildSession();
    public static SessionFactory buildSession() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(User.class);
        return configuration.buildSessionFactory();
    }

    public static SessionFactory getSessionFactory() {
        return session;
    }

    public static void closeSession() {
        if(session != null) {
            session.close();
        }
    }

}
