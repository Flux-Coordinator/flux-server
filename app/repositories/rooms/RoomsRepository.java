package repositories.rooms;

import models.Room;

import java.util.Set;
import java.util.concurrent.CompletionStage;

public interface RoomsRepository {
    CompletionStage<Set<Room>> getRooms(final int limit);

    CompletionStage<Room> getRoomById(final long roomId);

    CompletionStage<Long> addRoom(final long projectId, final Room room);

    void removeRoom(final long roomId);
}
