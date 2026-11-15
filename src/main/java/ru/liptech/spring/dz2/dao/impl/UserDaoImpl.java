package ru.liptech.spring.dz2.dao.impl;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import ru.liptech.spring.dz2.config.HibernateConfig;
import ru.liptech.spring.dz2.dao.UserDao;
import ru.liptech.spring.dz2.entity.User;

import java.util.List;

public class UserDaoImpl implements UserDao {

    private final SessionFactory sessionFactory;

    public UserDaoImpl() {
        this.sessionFactory = HibernateConfig.getSessionFactory();
    }

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User create(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            try {
                session.persist(user);
                transaction.commit();
                return user;
            } catch (Exception ex) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public User update(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            try {
                User updateUser = session.merge(user);
                transaction.commit();
                return updateUser;
            } catch (Exception ex) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public List<User> readUsers() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User", User.class).list();
        } catch (HibernateException ex) {
            return null;
        }
    }

    @Override
    public boolean delete(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            try {
                User userDel = session.get(User.class, user.getId());
                if (userDel == null) {
                    if (transaction.isActive()) {
                        transaction.rollback();
                    }
                    return false;
                }
                session.remove(userDel);
                transaction.commit();
                return true;
            } catch (Exception ex) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                return false;
            }
        }
    }
}