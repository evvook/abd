package abd.game.character;

import java.util.Map;

import abd.game.character.action.Action;

public abstract class GameCharacter{
	public static final String CLASS_FIGHTER = "F";
	public static final String CLASS_SPECAIL = "S";
	
	//기본정보
	private String code;
	private String name;
	protected String classCd;
	//클래스에 다른 행동 연관정보
	protected Action action;
	protected boolean alive;
	//스테이터스 정보
	protected Integer maxHp;
	protected Integer currentHp;
	protected Integer maxMp;
	protected Integer currentMp;
	protected Integer att;
	
	//무장여부
	protected boolean armed = false;
	//무장할 경우 증가하는 추가 공격치
	protected Integer extraAtt = 0;
	
	public GameCharacter(String charCd, String charNm, String classCd) {
		// TODO Auto-generated constructor stub
		this.code = charCd;
		this.name = charNm;
		this.classCd = classCd;
		this.alive = true;
	}
	
	
	public String getCode() {
		// TODO Auto-generated method stub
		return code;
	}
	
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	

	public boolean isAlive() {
		return alive;
	}
	
	public Integer getAtt() {
		if(armed) {
			return att+30;
		}else {
			return att;
		}
	}
	public Integer getHp() {
		return maxHp;
	}
	
	public Integer getMp() {
		return maxMp;
	}
	
	public void setCurrentHp(int hp) {
		if(alive && currentHp !=null) {
			if(hp > maxHp) {
				currentHp = maxHp;
			}else {
				currentHp = hp;
			}
		}
	}
	
	public Integer getCurrentHp() {
		return currentHp;
	}
	
	public Integer getCurrentMp() {
		// TODO Auto-generated method stub
		return currentMp;
	}
	
	public void setAction(Action action) {
		// TODO Auto-generated method stub
		this.action = action;
	}	
	
	public boolean equalsClass(String classCd) {
		// TODO Auto-generated method stub
		return this.classCd.equals(classCd);
	}

	public void increaseHp(Integer att) {
		// TODO Auto-generated method stub
		currentHp = currentHp + att;
		if(currentHp > maxHp) {
			currentHp = maxHp;
		}
	}

	public void decreaseHp(Integer att) {
		// TODO Auto-generated method stub
		currentHp = currentHp - att;
		if(currentHp < 0) {
			currentHp = 0;
		}
		if(currentHp == 0) {
			alive = false;
		}
	}
	
	public void increaseMp(Integer att) {
		// TODO Auto-generated method stub
		currentMp = currentMp + att;
		if(currentMp > maxMp) {
			currentMp = maxMp;
		}
	}
	
	public void decreaseMp(Integer att) {
		// TODO Auto-generated method stub
		currentMp = currentMp - att;
		if(currentMp < 0) {
			currentMp = 0;
		}
		if(currentMp == 0) {
			//명중 감소, 회피 감소
		}
	}

	public abstract Map<String, String> getCharacterContext();
}
