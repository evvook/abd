package abd.game.character.item;

import abd.game.character.GameCharacter;

public class GameItem {
	private Integer healPoint;
	private String code;
	private String name;
	
	public GameItem(String code, String name, Integer healPoint) {
		this.code = code;
		this.name = name;
		this.healPoint = healPoint;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	public String getHealPoint() {
		return healPoint.toString();
	}
	
	public void use(GameCharacter character) {
		character.increaseHp(healPoint);
	}
	
}
