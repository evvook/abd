package abd.game.character.action;

import abd.game.character.CompCharacter;
import abd.game.character.GameCharacter;

public class HealMp implements SpecialAction {
	private CompCharacter sCharacter;
	
	public HealMp(CompCharacter newCharacter) {
		// TODO Auto-generated constructor stub
		sCharacter = newCharacter;
	}

	@Override
	public void react(GameCharacter other) {
		// TODO Auto-generated method stub
		other.increaseMp(other.getCurrentMp()+sCharacter.getSpAbl2());
	}

}
