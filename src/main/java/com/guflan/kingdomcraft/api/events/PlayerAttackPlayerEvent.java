package com.guflan.kingdomcraft.api.events;

import com.guflan.kingdomcraft.api.entity.Player;

public class PlayerAttackPlayerEvent {

    private final Player player;
    private final Player attacker;

    private Result result = Result.NEUTRAL;

    public PlayerAttackPlayerEvent(Player player, Player attacker) {
        this.player = player;
        this.attacker = attacker;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getAttacker() {
        return attacker;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public enum Result {
        ALLOW, DENY, NEUTRAL;
    }
}
