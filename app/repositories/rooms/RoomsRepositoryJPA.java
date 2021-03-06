package repositories.rooms;

import models.Measurement;
import models.Project;
import models.Room;
import play.db.jpa.JPAApi;
import repositories.DatabaseExecutionContext;
import repositories.exceptions.AlreadyExistsException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Subgraph;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static repositories.utils.JpaHelper.flushAndClear;
import static repositories.utils.JpaHelper.wrap;

@Singleton
public class RoomsRepositoryJPA implements RoomsRepository {

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public RoomsRepositoryJPA(final JPAApi jpaApi, final DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public CompletableFuture<Set<Room>> getRooms(final int limit) {
        return CompletableFuture
                .supplyAsync(() -> wrap(jpaApi, entityManager -> getRooms(entityManager, limit)), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Room> getRoomById(final long roomId) {
        return CompletableFuture
                .supplyAsync(() -> wrap(jpaApi, entityManager -> getRoomById(entityManager, roomId)), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Set<Room>> getRoomsByIds(final List<Long> roomIds) {
        return CompletableFuture
                .supplyAsync(() -> wrap(jpaApi, em -> getRoomsByIds(em, roomIds)), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Set<Room>> getRoomsByName(final List<String> roomNames) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, em -> getRoomsByName(em, roomNames)), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Long> addRoom(final long projectId, final Room room) {
        return CompletableFuture
                .supplyAsync(() -> {
                    final Room persistedRoom = wrap(jpaApi, entityManager -> addRoom(entityManager, projectId, room));
                    return persistedRoom.getRoomId();
                }, databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Void> addRooms(final List<Room> rooms) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, em -> {
            addRooms(em, rooms);
            return null;
        }), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Void> removeRoom(long roomId) {
        return CompletableFuture.runAsync(() -> wrap(jpaApi, entityManager -> {
            removeRoom(entityManager, roomId);
            return null;
        }), databaseExecutionContext);
    }

    private Set<Room> getRooms(final EntityManager em, final int limit) {
        final TypedQuery<Room> query = em.createQuery("SELECT r FROM Room r", Room.class);
        if(limit > 0) {
            query.setMaxResults(limit);
        }
        return new HashSet<>(query.getResultList());
    }

    private Room getRoomById(final EntityManager em, final long roomId) {
        return em.find(Room.class, roomId);
    }

    private Set<Room> getRoomsByIds(final EntityManager em, final List<Long> roomIds) {
        final TypedQuery<Room> typedQuery = em.createQuery("SELECT r FROM Room r WHERE r.roomId in (:roomIds)", Room.class);
        typedQuery.setParameter("roomIds", roomIds);
        return new HashSet<>(typedQuery.getResultList());
    }

    private long countRoomsByName(final EntityManager em, final String roomName) {
        final TypedQuery<Long> typedQuery = em.createQuery("SELECT COUNT(r) FROM Room r WHERE r.name LIKE (:roomName)", Long.class);
        typedQuery.setParameter("roomName", roomName);
        return typedQuery.getSingleResult();
    }

    private Set<Room> getRoomsByName(final EntityManager em, final List<String> roomNames) {
        final TypedQuery<Room> typedQuery = em.createQuery("SELECT r FROM Room r WHERE r.name in (:roomNames)", Room.class);
        typedQuery.setParameter("roomNames", roomNames);
        return new HashSet<>(typedQuery.getResultList());
    }

    private Room addRoom(final EntityManager em, final long projectId, final Room room) {
        final Project projectRef = em.getReference(Project.class, projectId);
        room.setProject(projectRef);

        if(room.getRoomId() != null) {
            final EntityGraph graph = em.createEntityGraph(Measurement.class);
            graph.addSubgraph("readings");

            final TypedQuery<Measurement> roomTypedQuery = em.createQuery("SELECT m FROM Measurement m WHERE m.room.roomId = :roomId", Measurement.class);
            roomTypedQuery.setHint("javax.persistence.loadgraph", graph);
            roomTypedQuery.setParameter("roomId", room.getRoomId());
            final HashSet<Measurement> measurements = new HashSet<>(roomTypedQuery.getResultList());
            room.setMeasurements(measurements);
        }

        return em.merge(room);
    }

    private void addRooms(final EntityManager em, final List<Room> rooms) {
        rooms.forEach(em::merge);
    }

    private void removeRoom(final EntityManager em, final long roomId) {
        Room room = em.find(Room.class, roomId);
        room.setProject(null);
        flushAndClear(em);
        room = em.getReference(Room.class, roomId);
        em.remove(room);
    }
}
