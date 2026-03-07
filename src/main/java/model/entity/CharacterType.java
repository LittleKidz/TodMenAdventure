package model.entity;

/**
 * Enum of selectable characters that also acts as a factory for the correct Player subclass.
 * To add a new character, simply add an entry here.
 */
public enum CharacterType {

    /** Swordsman with high HP and good defense. Skill attacks twice. */
    KNIGHT {
        @Override
        public Player createPlayer() { return new KnightPlayer(); }
    },

    /** Archer with the highest ATK. Skill pierces defense. */
    ARCHER {
        @Override
        public Player createPlayer() { return new ArcherPlayer(); }
    },

    /** Cleric with a 5-slot inventory. Skill restores HP. */
    REBORN {
        @Override
        public Player createPlayer() { return new RebornPlayer(); }
    },

    /** Alien with magic attacks that bypass defense; can move diagonally. */
    ALIEN {
        @Override
        public Player createPlayer() { return new AlienPlayer(); }
    };

    /**
     * Creates the Player subclass corresponding to this CharacterType.
     *
     * @return A new Player instance
     */
    public abstract Player createPlayer();
}
