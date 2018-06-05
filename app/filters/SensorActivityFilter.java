package filters;

import akka.stream.Materializer;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.Date;
import javax.inject.Inject;
import play.mvc.Filter;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;

public class SensorActivityFilter extends Filter {

    private static final String SENSOR_DEVICE_HEADER = "X-Flux-Sensor";
    public static Date lastActivity = null;

    @Inject
    public SensorActivityFilter(Materializer mat) {
        super(mat);
    }

    @Override
    public CompletionStage<Result> apply(Function<RequestHeader, CompletionStage<Result>> nextFilter,
        RequestHeader requestHeader) {
        if (requestHeader.hasHeader(SENSOR_DEVICE_HEADER)) {
            lastActivity = new Date();
        }
        return nextFilter.apply(requestHeader);
    }
}