package ru.liptech.spring.dz2.controller;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.liptech.spring.dz2.dao.UserDao;
import ru.liptech.spring.dz2.db.DataBaseConnector;
import ru.liptech.spring.dz2.model.User;

import java.util.List;

public class UserDaoController implements UserDao {


    @Override
    public User create(User user) {
        Transaction transaction = null;
        try (Session session = DataBaseConnector.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
        } catch (Exception ex) {
            if(transaction!=null && transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println(ex.getMessage());
        }
        return user;
    }

    @Override
    public User update(User user) {
        Transaction transaction = null;
        try(Session session = DataBaseConnector.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User updateUser = session.merge(user);
            transaction.commit();
            return updateUser;
        } catch (Exception ex) {
            if(transaction!=null && transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println(ex.getMessage());
        }
        return null;
    }

    @Override
    public List<User> readUsers() {
        try(Session session = DataBaseConnector.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }catch (HibernateException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    @Override
    public boolean delete(User user) {
        Transaction transaction = null;
        try (Session session = DataBaseConnector.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            User userDel = session.get(User.class, user.getId());
            if (userDel == null) {
                transaction.rollback();
                return false;
            }
            session.remove(userDel);
            transaction.commit();
            return true;
        } catch (Exception ex) {
            if(transaction!=null && transaction.isActive()) {
                transaction.rollback();
            }
        }
        return false;
    }
}
