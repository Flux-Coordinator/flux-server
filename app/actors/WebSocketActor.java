package actors;

import actors.messages.GoodbyeMessage;
import actors.messages.IntroductionMessage;
import actors.messages.ReadingsMessage;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import models.Reading;
import play.libs.Json;

public class WebSocketActor extends AbstractActor {
    // HACK: ELU Had to make the Actor Ref public static, until I find a way to find the actor inside the actorsystem (and tell him something).
    private final ActorRef out;
    private final ActorRef readingsActor;

    public static Props props(final ActorRef out, final ActorRef readingsActor) {
        return Props.create(WebSocketActor.class, out, readingsActor);
    }

    public WebSocketActor(final ActorRef out, final ActorRef readingsActor) {
        this.out = out;
        this.readingsActor = readingsActor;
        this.readingsActor.tell(new IntroductionMessage(), self());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(ReadingsMessage.class, msg -> {
                out.tell(Json.toJson(msg.getReadings()), self());
            })
            .build();
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        this.readingsActor.tell(new GoodbyeMessage(), self());
    }
}
