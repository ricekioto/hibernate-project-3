package org.example;

import org.example.config.MySessionFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import static java.util.Objects.nonNull;

public class Main {
    private static SessionFactory sessionFactory = MySessionFactory.getSessionFactory();

    public static void main(String[] args) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            System.out.println("work");

            session.getTransaction().commit();
        }
    }

}