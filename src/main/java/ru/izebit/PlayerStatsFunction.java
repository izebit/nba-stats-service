package ru.izebit;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import lombok.SneakyThrows;
import ru.izebit.events.StatResponseEvent;

import java.util.Map;

public class PlayerStatsFunction extends AbstractDatabaseFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final String SQL = """
                   SELECT
                       player_id,
                       season_id,
                       points,
                       rebounds,
                       assists,
                       steals,
                       blocks,
                       fouls,
                       turnovers,
                       minutesPlayed,
                       game_count
                   FROM players_stats
                   WHERE player_id = ? and season_id = ?
            """;

    @Override
    @SneakyThrows
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        var seasonId = Long.parseLong(request.getPathParameters().get("season_id"));
        var playerId = Long.parseLong(request.getPathParameters().get("player_id"));
        var teamId = Long.parseLong(request.getPathParameters().get("team_id"));

        var event = JDBC_TEMPLATE.query(SQL, ps -> {
            ps.setLong(1, playerId);
            ps.setLong(2, seasonId);
        }, rs -> {
            if (!rs.next())
                return null;

            var e = new StatResponseEvent();
            e.setTeamId(teamId);
            e.setSeasonId(seasonId);
            e.setPlayerId(playerId);
            double gameCount = rs.getInt("game_count");
            e.setPoints(rs.getInt("points") / gameCount);
            e.setRebounds(rs.getInt("rebounds") / gameCount);
            e.setAssists(rs.getInt("assists") / gameCount);
            e.setSteals(rs.getInt("steals") / gameCount);
            e.setBlocks(rs.getInt("blocks") / gameCount);
            e.setFouls(rs.getInt("fouls") / gameCount);
            e.setTurnovers(rs.getInt("turnovers") / gameCount);
            e.setMinutesPlayed(rs.getInt("minutesPlayed") / gameCount);
            return e;
        });

        if (event == null)
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(404)
                    .withBody("not found");

        return new APIGatewayProxyResponseEvent()
                .withBody(OBJECT_MAPPER.writeValueAsString(event))
                .withStatusCode(200)
                .withHeaders(Map.of("Content-Type", "application/json"));
    }
}

