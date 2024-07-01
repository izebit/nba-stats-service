package ru.izebit.events;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LogRequestEvent {
    private long seasonId;
    private long teamId;
    private Long playerId;

    private int points;
    private int rebounds;
    private int assists;
    private int steals;
    private int blocks;
    private int fouls;
    private int turnovers;
    private int minutesPlayed;
}
