package actors.measurements;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import play.mvc.WebSocket;

import static actors.measurements.MeasurementActorProtocol.*;

public class MeasurementActor extends AbstractActor {
    private final ActorRef out;

    public static Props props(ActorRef out) {
        return Props.create(MeasurementActor.class, out);
    }

    public MeasurementActor(final ActorRef out) {
        this.out = out;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PutReading.class, putReading -> {
                    out.tell(putReading.reading, self());
                })
                .build();
    }
}
