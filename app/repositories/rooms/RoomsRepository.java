package repositories.rooms;

import models.Room;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface RoomsRepository {
    CompletionStage<List<Room>> getRooms(final int limit);

    CompletionStage<Room> getRoomById(final long roomId);

    CompletionStage<Long> addRoom(final Room room);

    void removeRoom(final long roomId);
}
