package org.example.repository;

import org.example.config.HibernateUtil;
import org.example.model.Room;
import org.hibernate.Session;

public class RoomRepository {

    public Room save(Room room) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(room);
            session.getTransaction().commit();
            return room;
        }
    }

    public Room findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Room.class, id);
        }
    }

}
