package model.battle;

import model.entity.Entity;

/**
 * Aggregate result of one turn, containing ActionResults for both sides
 * as well as information on whether the battle has ended and who won or lost.
 */
public class BattleResult {

    /** Action result for entityA. */
    private ActionResult resultA;

    /** Action result for entityB. */
    private ActionResult resultB;

    /** Whether the battle is over (at least one side has reached 0 HP). */
    private boolean battleOver;

    /** The winner ({@code null} in case of a draw). */
    private Entity winner;

    /** The loser ({@code null} in case of a draw). */
    private Entity loser;

    /**
     * Summarises what happened this turn, including both sides' results,
     * and announces the winner if the battle is over.
     *
     * @return A summary of this turn
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        if (resultA != null) sb.append(resultA.getDescription()).append("\n");
        if (resultB != null) sb.append(resultB.getDescription()).append("\n");
        if (battleOver && winner != null) {
            sb.append("─────────────────────\n");
            sb.append(winner.getName()).append(" ชนะ!");
        }
        return sb.toString();
    }

    // ─── Getters / Setters ───────────────────────────────────────

    /** @return Action result for entityA. */
    public ActionResult getResultA()        { return resultA; }

    /** @param r Action result for entityA. */
    public void setResultA(ActionResult r)  { this.resultA = r; }

    /** @return Action result for entityB. */
    public ActionResult getResultB()        { return resultB; }

    /** @param r Action result for entityB. */
    public void setResultB(ActionResult r)  { this.resultB = r; }

    /** @return {@code true} if the battle is over */
    public boolean isBattleOver()           { return battleOver; }

    /** @param b {@code true} to mark the battle as over */
    public void setBattleOver(boolean b)    { this.battleOver = b; }

    /** @return The winner, or {@code null} for a draw */
    public Entity getWinner()               { return winner; }

    /** @param w The winning entity */
    public void setWinner(Entity w)         { this.winner = w; }

    /** @return The loser, or {@code null} for a draw */
    public Entity getLoser()                { return loser; }

    /** @param l The losing entity */
    public void setLoser(Entity l)          { this.loser = l; }
}
