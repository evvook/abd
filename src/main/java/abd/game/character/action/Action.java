package abd.game.character.action;

import abd.game.character.GameCharacter;

public interface Action {
	public void act(GameCharacter other);
	public void react(GameCharacter other);
}
