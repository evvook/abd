package abd.game.character.item;

import abd.game.character.GameCharacter;

public class GameItem {
	private Integer healPoint;
	private String code;
	private String name;
	private Integer size;
	
	public GameItem(String code, String name, Integer healPoint, Integer size) {
		this.code = code;
		this.name = name;
		this.healPoint = healPoint;
		this.size = size;
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
	
	public Integer getSize() {
		return size;
	}
	
	public void use(GameCharacter character) {
		character.increaseHp(healPoint);
	}
	
}
