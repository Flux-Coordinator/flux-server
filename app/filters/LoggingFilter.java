package filters;

import akka.stream.Materializer;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javax.inject.Inject;
import play.Logger;
import play.mvc.Filter;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;

public class LoggingFilter extends Filter {

    @Inject
    public LoggingFilter(Materializer mat) {
        super(mat);
    }

    @Override
    public CompletionStage<Result> apply(Function<RequestHeader, CompletionStage<Result>> nextFilter,
        RequestHeader requestHeader) {

        long startTime = System.nanoTime();
        return nextFilter.apply(requestHeader).thenApply(result -> {
            long endTime = System.nanoTime();
            long requestTime = endTime - startTime;
            requestTime = TimeUnit.MILLISECONDS.convert(requestTime, TimeUnit.NANOSECONDS);

            Logger.info("{} {} took {}ms and returned {}",
                requestHeader.method(), requestHeader.uri(), requestTime, result.status());

            return result.withHeader("Request-Time", "" + requestTime);
        });

    }
}
