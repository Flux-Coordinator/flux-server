package repositories.utils;

import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import java.util.function.Function;

public class Helper {
    private Helper() { }

    public static <T> T wrap(final JPAApi jpaApi, Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
