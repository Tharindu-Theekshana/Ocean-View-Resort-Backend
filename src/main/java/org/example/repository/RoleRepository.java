package org.example.repository;

import org.example.config.HibernateUtil;
import org.example.model.Role;
import org.example.model.RoleType;
import org.example.model.UserRole;
import org.hibernate.Session;

public class RoleRepository {
    public Role findByName(RoleType name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Role r WHERE r.name = :name", Role.class)
                    .setParameter("name", name)
                    .uniqueResult();
        }
    }

    public void saveUserRole(UserRole userRole) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(userRole);
            session.getTransaction().commit();
        }
    }

}
