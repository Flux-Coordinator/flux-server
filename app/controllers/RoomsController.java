package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Room;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import repositories.rooms.RoomsRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

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
}