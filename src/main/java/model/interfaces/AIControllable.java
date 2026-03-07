package model.interfaces;

import model.battle.BattleAction;
import model.entity.Entity;

/**
 * Interface for entities that can be controlled by AI.
 * Goblin implements this interface so it can decide actions without waiting for player input.
 */
public interface AIControllable {

    /**
     * Decides what action to take this turn.
     *
     * @param target The opposing entity (used as a reference for decision-making)
     * @return The BattleAction chosen by the AI
     */
    BattleAction decideAction(Entity target);
}
