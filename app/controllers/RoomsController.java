package controllers;

import authentication.JWTAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
import models.Room;
import play.Logger;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import repositories.rooms.RoomsRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletionStage;

@Security.Authenticated(value = JWTAuthenticator.class)
@Singleton
public class RoomsController extends Controller {
    private final HttpExecutionContext httpExecutionContext;
    private final RoomsRepository roomsRepository;

    @Inject
    public RoomsController(final HttpExecutionContext httpExecutionContext, final RoomsRepository roomsRepository) {
        this.httpExecutionContext = httpExecutionContext;
        this.roomsRepository = roomsRepository;
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> addRoom(final long projectId) {
        final JsonNode jsonNode = request().body().asJson();
        final Room room = Json.fromJson(jsonNode, Room.class);
        return roomsRepository.addRoom(projectId, room)
                .thenApplyAsync(roomId -> created(Json.toJson(roomId)), httpExecutionContext.current());
    }

    public CompletionStage<Result> removeRoom(final long roomId) {
        return roomsRepository.removeRoom(roomId)
                .thenApplyAsync(aVoid -> ok(""), httpExecutionContext.current())
                .exceptionally(throwable -> {
                    Logger.error("Error removing room with room ID: " + roomId, throwable);
                    return badRequest("Der Raum konnte nicht gelöscht werden (Raum ID: " + roomId + ").");
                });
    }

    public CompletionStage<Result> getRooms(final int limit) {
        return roomsRepository.getRooms(limit).thenApplyAsync(rooms -> ok(Json.toJson(rooms)), httpExecutionContext.current());
    }

    public CompletionStage<Result> getRoom(final long roomId) {
        return roomsRepository.getRoomById(roomId).thenApplyAsync(room -> ok(Json.toJson(room)), httpExecutionContext.current());
    }
}
