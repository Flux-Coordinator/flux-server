package actors.measurements;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import models.Reading;

public class MeasurementActor extends AbstractActor {
    // HACK: ELU Had to make the Actor Ref public static, until I find a way to find the actor inside the actorsystem (and tell him something).
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

    @Override
    public void postStop() throws Exception {
        super.postStop();
        out = null;
    }
}
