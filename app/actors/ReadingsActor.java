package actors;

import actors.messages.GoodbyeMessage;
import actors.messages.IntroductionMessage;
import actors.messages.ReadingsMessage;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.HashSet;
import java.util.Set;

/**
 * This actor handles new incoming readings.
 */
public class ReadingsActor extends AbstractActor {
    private Set<ActorRef> websocketActors;

    public static Props getProps() {
        return Props.create(ReadingsActor.class);
    }

    public ReadingsActor() {
        this.websocketActors = new HashSet<>();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(IntroductionMessage.class, introductionMessage -> {
                    final ActorRef sender = getSender();
                    this.websocketActors.add(sender);
                })
                .match(GoodbyeMessage.class, goodbyeMessage -> {
                    final ActorRef sender = getSender();
                    this.websocketActors.remove(sender);
                })
                .match(ReadingsMessage.class, readingsMessage -> {
                    this.websocketActors.forEach(actorRef -> {
                        actorRef.tell(readingsMessage, self());
                    });
                })
                .build();
    }
}
