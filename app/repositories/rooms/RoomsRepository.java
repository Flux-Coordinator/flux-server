package repositories.rooms;

import models.Room;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface RoomsRepository {
    CompletableFuture<Set<Room>> getRooms(final int limit);

    CompletableFuture<Room> getRoomById(final long roomId);

    CompletableFuture<Set<Room>> getRoomsByIds(final List<Long> roomIds);

    CompletableFuture<Set<Room>> getRoomsByName(final List<String> roomNames);

    CompletableFuture<Long> addRoom(final long projectId, final Room room);

    CompletableFuture<Void> removeRoom(final long roomId);

    CompletableFuture<Void> addRooms(final List<Room> rooms);
}
