package abd.game.character.action;

import abd.game.character.GameCharacter;

public class Cure implements Action {
	GameCharacter healer;
	public Cure(GameCharacter healer) {
		// TODO Auto-generated constructor stub
		this.healer = healer;
	}

	@Override
	public void act(GameCharacter other) {
		// TODO Auto-generated method stub
		//풀피로 만듬
		other.setCurrentHp(other.getHp());
	}

	@Override
	public void react(GameCharacter other) {
		// TODO Auto-generated method stub
		//힐러의 능력치 만큼 체력 회복
		other.increaseHp(healer.getAtt());
	}

}
