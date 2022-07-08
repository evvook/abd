package abd.game.character.action;

import abd.game.character.GameCharacter;
import abd.game.character.NonPlayerable;

public class Fight implements Action {
	GameCharacter fihgter;
	public Fight(GameCharacter fihgter) {
		// TODO Auto-generated constructor stub
		this.fihgter = fihgter;
	}

	@Override
	public void act(GameCharacter other) {
		// TODO Auto-generated method stub
		//상대가 파이터면 데미지를 주고
		if(other.equalsClass(GameCharacter.CLASS_FIGHTER)) {
			other.decreaseHp(fihgter.getAtt());
		}
		//상대가 NonPlayable이면 반응 실행
		if(other.isAlive() && NonPlayerable.class.isInstance(other))  {
			NonPlayerable npc = (NonPlayerable)other;
			npc.react(fihgter);
		}
	}

	@Override
	public void react(GameCharacter other) {
		// TODO Auto-generated method stub
		other.decreaseHp(fihgter.getAtt());
	}
}
