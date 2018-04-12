package helpers;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.Result;

public class Helpers {
    public static <T> T convertFromJSON(final Result result, Class<T> clazz) {
        final JsonNode node = Json.parse(play.test.Helpers.contentAsString(result));
        return Json.fromJson(node, clazz);
    }
}
