package ru.izebit;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.izebit.db.JdbcTemplateFactory;
import ru.izebit.db.Tables;
import ru.izebit.events.StatResponseEvent;

import java.util.Map;

public class TeamStatsFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    private static final String DB_USERNAME = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_DATABASE = System.getenv("DB_DATABASE");
    private static final JdbcTemplate jdbcTemplate = JdbcTemplateFactory.create(DB_URL, DB_DATABASE, DB_USERNAME, DB_PASSWORD);

    static {
        jdbcTemplate.execute(Tables.TEAM_STATS_DDL);
    }

    private static final String SQL = """
                   SELECT
                       team_id,
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
                   FROM team_stats
                   WHERE team_id = ? and season_id = ?
            """;

    @Override
    @SneakyThrows
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        var seasonId = Long.parseLong(request.getPathParameters().get("season_id"));
        var teamId = Long.parseLong(request.getPathParameters().get("team_id"));

        var event = jdbcTemplate.query(SQL, ps -> {
            ps.setLong(1, teamId);
            ps.setLong(2, seasonId);
        }, rs -> {
            if (!rs.next())
                return null;

            var e = new StatResponseEvent();
            e.setTeamId(teamId);
            e.setSeasonId(seasonId);
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
                    .withStatusCode(400)
                    .withBody("not found");

        return new APIGatewayProxyResponseEvent()
                .withBody(OBJECT_MAPPER.writeValueAsString(event))
                .withStatusCode(200)
                .withHeaders(Map.of("Content-Type", "application/json"));
    }
}

