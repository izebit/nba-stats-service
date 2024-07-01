package ru.izebit.events;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StatResponseEvent {
    private long seasonId;
    private long teamId;
    private long playerId;

    private double points;
    private double rebounds;
    private double assists;
    private double steals;
    private double blocks;
    private double fouls;
    private double turnovers;
    private double minutesPlayed;
}
