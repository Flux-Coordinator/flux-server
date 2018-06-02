package repositories.utils;

import models.Measurement;
import play.db.jpa.JPAApi;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JpaHelper {
    private JpaHelper() { }

    public static <T> T wrap(final JPAApi jpaApi, Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }




    public static void flushAndClear(final EntityManager em) {
        em.flush();
        em.clear();
    }
}
