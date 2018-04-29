package actors.measurements;

import akka.actor.AbstractActor;
import akka.actor.Props;
import play.mvc.WebSocket;

import static actors.measurements.MeasurementActorProtocol.*;

public class MeasurementActor extends AbstractActor {
    public static Props getProps(final WebSocket socket) {
        return Props.create(MeasurementActor.class, () -> new MeasurementActor(socket));
    }

    private final WebSocket webSocket;

    public MeasurementActor(final WebSocket socket) {
        this.webSocket = socket;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PutReading.class, putReading -> {
                })
                .build();
    }
}
