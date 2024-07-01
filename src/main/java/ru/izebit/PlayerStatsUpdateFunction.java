package ru.izebit;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import lombok.SneakyThrows;
import ru.izebit.db.Tables;
import ru.izebit.events.LogRequestEvent;

public class PlayerStatsUpdateFunction extends AbstractDatabaseFunction implements RequestHandler<SNSEvent, Void> {
    private static final String SQL = """
            INSERT INTO players_stats (
                player_id,
                season_id,
                points,
                rebounds,
                assists,
                steals,
                blocks,
                fouls,
                turnovers,
                minutesPlayed
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                  game_count = game_count + 1,
                  points = points + VALUES(points),
                  rebounds = rebounds + VALUES(rebounds),
                  assists = assists + VALUES(assists),
                  steals = steals + VALUES(steals),
                  blocks = blocks + VALUES(blocks),
                  points = points + VALUES(points),
                  fouls = fouls + VALUES(fouls),
                  turnovers = turnovers + VALUES(turnovers),
                  minutesPlayed = minutesPlayed + VALUES(minutesPlayed)
            """;

    public PlayerStatsUpdateFunction() {
        super(Tables.PLAYERS_STATS_DDL);
    }

    @Override
    @SneakyThrows
    public Void handleRequest(SNSEvent event, Context context) {
        for (SNSEvent.SNSRecord r : event.getRecords()) {
            var snsMessage = r.getSNS().getMessage();
            var message = OBJECT_MAPPER.readValue(snsMessage, LogRequestEvent.class);
            JDBC_TEMPLATE.update(SQL,
                    message.getPlayerId(),
                    message.getSeasonId(),
                    message.getPoints(),
                    message.getRebounds(),
                    message.getAssists(),
                    message.getSteals(),
                    message.getBlocks(),
                    message.getFouls(),
                    message.getTurnovers(),
                    message.getMinutesPlayed());
        }
        return null;
    }
}