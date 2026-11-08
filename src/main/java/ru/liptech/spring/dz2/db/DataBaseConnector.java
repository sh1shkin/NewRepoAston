package ru.liptech.spring.dz2.db;

import org.hibernate.*;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.liptech.spring.dz2.model.User;


public final class DataBaseConnector {
    private static SessionFactory session = buildSession();
    public static SessionFactory buildSession() {
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().build();
        return new MetadataSources(serviceRegistry).addAnnotatedClass(User.class).buildMetadata().buildSessionFactory();
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
