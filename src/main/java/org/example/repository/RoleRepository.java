package org.example.repository;

import org.example.config.HibernateUtil;
import org.example.model.Role;
import org.example.model.RoleType;
import org.hibernate.Session;

public class RoleRepository {
    public Role findByName(Session session,RoleType name) {
            return session.createQuery(
                            "FROM Role r WHERE r.name = :name", Role.class)
                    .setParameter("name", name)
                    .uniqueResult();
    }

    public void save(Role role) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(role);
            session.getTransaction().commit();
        }
    }

}
