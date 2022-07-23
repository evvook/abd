package abd.game.character.action;

import java.util.Map;

import abd.game.character.GameCharacter;
import abd.game.character.NPCharacter;

public interface Action {
	public Map<String,Object> act(NPCharacter other);
	public Map<String,Object> react(GameCharacter other);
}
