package repositories.rooms;

import models.Room;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface RoomsRepository {
    CompletableFuture<Set<Room>> getRooms(final int limit);

    CompletableFuture<Room> getRoomById(final long roomId);

    CompletableFuture<Long> addRoom(final long projectId, final Room room);

    CompletableFuture<Void> removeRoom(final long roomId);
}
