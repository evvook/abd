package abd.game.character.action;

import abd.game.character.CompCharacter;
import abd.game.character.GameCharacter;

public class HealHp implements SpecialAction {
	private CompCharacter sCharacter;
	
	public HealHp(CompCharacter newCharacter) {
		// TODO Auto-generated constructor stub
		sCharacter = newCharacter;
	}

	@Override
	public void react(GameCharacter other) {
		// TODO Auto-generated method stub
		other.increaseHp(sCharacter.getSpAbl1());
	}

}
