package ru.izebit;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import ru.izebit.events.LogRequestEvent;

public class LogFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String SNS_TOPIC_ARN = System.getenv("SNS_TOPIC_ARN");
    private static final AmazonSNS SNS_CLIENT = AmazonSNSClientBuilder.defaultClient();

    @Override
    @SneakyThrows
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        context.getLogger().log("Received request: " + event.getBody());

        var seasonId = event.getPathParameters().get("season_id");
        var teamId = event.getPathParameters().get("team_id");
        var playerId = event.getPathParameters().get("player_id");

        var logEvent = OBJECT_MAPPER.readValue(event.getBody(), LogRequestEvent.class);
        logEvent.setSeasonId(Long.parseLong(seasonId));
        logEvent.setTeamId(Long.parseLong(teamId));
        logEvent.setPlayerId(Long.parseLong(playerId));

        var publishRequest = new PublishRequest()
                .withTopicArn(SNS_TOPIC_ARN)
                .withMessage(OBJECT_MAPPER.writeValueAsString(logEvent));

        var result = SNS_CLIENT.publish(publishRequest);
        context.getLogger().log("Message published to SNS topic: " + result.getMessageId());

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody("Message published to SNS topic");

    }
}

