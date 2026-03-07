package model.item;

import model.entity.Entity;
import java.util.Random;

/**
 * Reward granted after defeating a Goblin; randomly boosts one of 3 stats for the winner.
 * The boost is permanent (base values are also adjusted) and is not reset after battle.
 */
public class TomeItem extends Item {

    /** Shared random instance used to select which stat to boost. */
    private static final Random RANDOM = new Random();

    /** HP bonus applied when MAX_HP is rolled. */
    private static final int BONUS_AMOUNT_HP = 10;

    /** Attack bonus applied when ATTACK is rolled. */
    private static final int BONUS_AMOUNT_ATK = 3;

    /** Defense bonus applied when DEFENSE is rolled. */
    private static final int BONUS_AMOUNT_DEF = 2;

    /** Stat that was randomly selected when this object was created. */
    private final StatType boostedStat;

    /**
     * Creates a TomeItem and immediately randomly selects the stat to boost.
     */
    public TomeItem() {
        super("Ancient Tome");
        StatType[] stats = StatType.values();
        this.boostedStat = stats[RANDOM.nextInt(stats.length)];
    }

    /**
     * Applies the randomly chosen stat boost to the entity and updates base values as well,
     * making the boost permanent and not subject to battle resets.
     *
     * @param target Entity to receive the stat boost
     */
    @Override
    public void apply(Entity target) {
        switch (boostedStat) {
            case MAX_HP:
                target.setMaxHp(target.getMaxHp() + BONUS_AMOUNT_HP);
                target.setCurrentHp(target.getCurrentHp() + BONUS_AMOUNT_HP);
                break;
            case ATTACK:
                target.resetAttackPower();
                target.setAttackPower(target.getAttackPower() + BONUS_AMOUNT_ATK);
                target.setBaseAttackPower(target.getAttackPower());
                break;
            case DEFENSE:
                target.resetDefense();
                target.setDefense(target.getDefense() + BONUS_AMOUNT_DEF);
                target.setBaseDefense(target.getDefense());
                break;
        }
    }

    /**
     * Returns a description of the stat boost for display in the UI.
     *
     * @return Description of the boosted stat
     */
    @Override
    public String getEffectDescription() {
        switch (boostedStat) {
            case MAX_HP:  return "Max HP +" + BONUS_AMOUNT_HP;
            case ATTACK:  return "ATK +"    + BONUS_AMOUNT_ATK;
            case DEFENSE: return "DEF +"    + BONUS_AMOUNT_DEF;
            default:      return "";
        }
    }

    /**
     * Returns the stat type that was randomly selected.
     *
     * @return The StatType to be boosted
     */
    public StatType getBoostedStat() { return boostedStat; }
}
