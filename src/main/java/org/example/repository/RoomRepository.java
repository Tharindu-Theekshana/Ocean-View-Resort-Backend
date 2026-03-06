package org.example.repository;

import org.example.config.HibernateUtil;
import org.example.model.Room;
import org.example.model.RoomType;
import org.hibernate.Session;

import java.util.List;

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

    public List<Room> findByType(RoomType type) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Room r WHERE r.type = :type", Room.class)
                    .setParameter("type", type)
                    .list();
        }
    }

    public List<Room> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Room", Room.class).list();
        }
    }

}
