package utils.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import play.libs.Json;

public class JacksonCustomObjectMapper {
    public JacksonCustomObjectMapper() {
        final ObjectMapper mapper = Json.newDefaultMapper();
        mapper.registerModule(new Hibernate5Module());

        Json.setObjectMapper(mapper);
    }
}
