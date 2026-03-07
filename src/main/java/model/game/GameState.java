package model.game;

/**
 * All possible states of the game. GameManager changes the state and notifies the UI through GameEventListener.
 */
public enum GameState {
    /** Main menu screen. */
    MAIN_MENU,
    /** Character selection screen. */
    CHARACTER_SELECT,
    /** Map phase where players take turns moving. */
    MAP_PHASE,
    /** Turn-based battle phase. */
    BATTLE_PHASE,
    /** Game over; a winner has been determined. */
    GAME_OVER
}
