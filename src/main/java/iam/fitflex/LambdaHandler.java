package iam.fitflex;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
This class serves as a proxy that takes the inbound request payload from Lambda (it can be a REST request from API Gateway)
and converts it to something Spring understands, and passes it onto Spring. The same happens on the way back out, when generating a response.
Take a look at the handler variable as it confirms the explanation above:
SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
 */
@Profile("lambda")
public class LambdaHandler implements RequestStreamHandler {

    private static final SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    static {
        try {
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(Application.class);
        } catch (ContainerInitializationException e) {
            // if we fail here. We re-throw the exception to force another cold start
            e.printStackTrace();
            throw new RuntimeException("Could not initialize Spring Boot application", e);
        }
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        handler.proxyStream(inputStream, outputStream, context);
    }
}
