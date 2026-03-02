package org.example.repository;

import org.example.config.HibernateUtil;
import org.example.model.User;
import org.example.model.UserDetail;
import org.hibernate.Session;

public class UserDetailRepository {

    public UserDetail findByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM UserDetail ud WHERE ud.user.id = :userId", UserDetail.class)
                    .setParameter("userId", userId)
                    .uniqueResult();
        }
    }
    public UserDetail save(UserDetail userDetail) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(userDetail);
            session.getTransaction().commit();
            return userDetail;
        }
    }


}
