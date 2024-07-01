package ru.izebit.db;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Tables {
    public static final String PLAYERS_STATS_DDL = """
            CREATE TABLE IF NOT EXISTS players_stats (
                  player_id     INT,
                  season_id     INT,
                  game_count    INT DEFAULT 1,
                  points        INT DEFAULT 0,
                  rebounds      INT DEFAULT 0,
                  assists       INT DEFAULT 0,
                  steals        INT DEFAULT 0,
                  blocks        INT DEFAULT 0,
                  fouls         INT DEFAULT 0,
                  turnovers     INT DEFAULT 0,
                  minutesPlayed INT DEFAULT 0,
                  PRIMARY KEY (player_id, season_id)
            )
            """;

    public static final String TEAM_STATS_DDL = """
            CREATE TABLE IF NOT EXISTS team_stats (
                  team_id       INT,
                  season_id     INT,
                  game_count    INT DEFAULT 1,
                  points        INT DEFAULT 0,
                  rebounds      INT DEFAULT 0,
                  assists       INT DEFAULT 0,
                  steals        INT DEFAULT 0,
                  blocks        INT DEFAULT 0,
                  fouls         INT DEFAULT 0,
                  turnovers     INT DEFAULT 0,
                  minutesPlayed INT DEFAULT 0,
                  PRIMARY KEY (team_id, season_id)
            )
            """;
}
