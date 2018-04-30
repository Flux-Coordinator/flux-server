package actors.measurements;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import models.Reading;
import play.mvc.WebSocket;

import static actors.measurements.MeasurementActorProtocol.*;

public class MeasurementActor extends AbstractActor {
    public static ActorRef out;

    public static Props props(ActorRef out) {
        return Props.create(MeasurementActor.class, out);
    }

    public MeasurementActor(final ActorRef out) {
        MeasurementActor.out = out;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Reading.class, putReading -> {
                    out.tell(putReading, self());
                })
                .build();
    }
}
