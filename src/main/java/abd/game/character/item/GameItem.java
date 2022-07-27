package abd.game.character.item;

import abd.game.character.GameCharacter;

public class GameItem {
	private Integer healPoint;
	public void use(GameCharacter character) {
		character.increaseHp(healPoint);
	}
	
	protected void setHealPoint(int healPoint) {
		this.healPoint = healPoint;
	}
}
