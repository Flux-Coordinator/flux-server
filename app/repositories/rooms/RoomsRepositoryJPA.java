package repositories.rooms;

import models.Project;
import models.Room;
import play.db.jpa.JPAApi;
import repositories.DatabaseExecutionContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static repositories.utils.Helper.wrap;

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
    public CompletionStage<Set<Room>> getRooms(final int limit) {
        return CompletableFuture
                .supplyAsync(() -> wrap(jpaApi, entityManager -> getRooms(entityManager, limit)), databaseExecutionContext);
    }

    @Override
    public CompletionStage<Room> getRoomById(final long roomId) {
        return CompletableFuture
                .supplyAsync(() -> wrap(jpaApi, entityManager -> getRoomById(entityManager, roomId)), databaseExecutionContext);
    }

    @Override
    public CompletionStage<Long> addRoom(final long projectId, final Room room) {
        return CompletableFuture
                .supplyAsync(() -> {
                    final Room persistedRoom = wrap(jpaApi, entityManager -> addRoom(entityManager, projectId, room));
                    return persistedRoom.getRoomId();
                }, databaseExecutionContext);
    }

    @Override
    public void removeRoom(long roomId) {
        CompletableFuture.runAsync(() -> wrap(jpaApi, entityManager -> {
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

    private Room addRoom(final EntityManager em, final long projectId, final Room room) {
        final Project projectRef = em.getReference(Project.class, projectId);
        room.setProject(projectRef);
        em.persist(room);
        return room;
    }

    private void removeRoom(final EntityManager em, final long roomId) {
        final Room foundRoomReference = em.getReference(Room.class, roomId);
        em.remove(foundRoomReference);
    }
}
