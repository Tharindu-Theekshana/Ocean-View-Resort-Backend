package org.example.repository;

import org.example.utils.HibernateUtil;
import org.example.model.Reservation;
import org.hibernate.Session;

import java.util.List;

public class ReservationRepository {

    public Reservation save(Reservation reservation) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(reservation);
            session.getTransaction().commit();
            return reservation;
        }
    }

    public List<Reservation> findByUserDetailId(Long userDetailId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Reservation r WHERE r.userDetail.id = :userDetailId", Reservation.class)
                    .setParameter("userDetailId", userDetailId)
                    .list();
        }
    }

    public Reservation findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Reservation.class, id);
        }
    }

    public List<Reservation> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Reservation", Reservation.class).list();
        }
    }
}
