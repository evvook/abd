package abd.game.character;

import java.util.HashMap;
import java.util.Map;

import abd.game.GameManager;

public class PChacrater extends GameCharacter implements Playerable{
	private String job;
	private GameManager manager;
	
	public PChacrater(String charCd, String charNm, String classCd) {
		// TODO Auto-generated constructor stub
		super(charCd, charNm,classCd);
	}
	
	public void setGameManager(GameManager manager) {
		this.manager = manager;
	}

	private Integer xp;//경험치
	private Integer requiredXp;//요구경험치
	private Integer level;//플레이어 캐릭터 레벨
	
	public void takeFight(NPCharacter fighter) throws Exception {
		//GameCharacter fighter = gm.getEncounterdChracter();
		act(fighter);
		if(this.isAlive()) {
			if(!fighter.isAlive()) {
				//상대방이 죽은 경우
				//경험치를 획득
				takeExp(fighter);
			}
			
		}else {
			//플레이어가 죽은 경우
		}
	}
	
	public void takeCure(NPCharacter healer) {
		//GameCharacter healer = gm.getEncounterdChracter();
		healer.react(this);
	}
	
	@Override
	public void runAwayFrom(NPCharacter npc) {
		// TODO Auto-generated method stub
		npc.setFreq(1);
	}	
	
	private void takeExp(NonPlayerable charact) throws Exception {
		// TODO Auto-generated method stub
		xp += charact.getXp();
		if(isLevelUp()) {
			levelUp();
		}
	}

	private boolean isLevelUp() {
		// TODO Auto-generated method stub
		return xp >= requiredXp;
	}
	
	@Override
	public void levelUp() throws Exception {
		manager.playerLevelUp();
	}
//	public void levelUp() throws Exception {
//		// TODO Auto-generated method stub
//		
//		Map<String,String> paramMap = new HashMap<String,String>();
//		paramMap.put("LEVEL", String.valueOf(getLevel()+1));
//		List<Map<String,String>> lvlDataList = loader.getPChacterInfo(paramMap);
//		Map<String,String> lvlData = lvlDataList.get(0);
//		
//		setLvlStatus(lvlData.get("LEVEL"),lvlData.get("HP"),lvlData.get("ATT"),lvlData.get("REQD_XP"));
//	}

	@Override
	public void act(GameCharacter other) {
		// TODO Auto-generated method stub
		action.act(other);
	}
	
	@Override
	public Integer getLevel() {
		// TODO Auto-generated method stub
		return level;
	}

	public void setLvlStatus(String level, String hp, String att, String requiredXp) {
		// TODO Auto-generated method stub
		try {
			if(this.xp != null && this.xp != 0) {
				this.xp = this.xp - this.requiredXp;
			}else {
				this.xp = 0;
			}
			
			this.level = Integer.valueOf(level);
			this.maxHp = Integer.valueOf(hp);
			this.currentHp = Integer.valueOf(hp);
			this.att = Integer.valueOf(att);
			this.requiredXp = Integer.valueOf(requiredXp);
		}catch(NullPointerException e) {
			throw e;
		}
	}

	public void setLvlStatus(Map<String, String> lvlData) {
		// TODO Auto-generated method stub
		setLvlStatus(lvlData.get("LEVEL"),lvlData.get("HP"),lvlData.get("ATT"),lvlData.get("REQD_XP"));
	}	

	@Override
	public Map<String, String> getCharacterContext() {
		// TODO Auto-generated method stub
		//"{\"name\":\"김은우\",\"hp\":\"100\",\"att\":\"10\",\"xp\":\"0\",\"lev\":\"1\",\"reqdXp\":\"15\"}"
		Map<String,String> pcContext = new HashMap<String, String>();
		
		pcContext.put("name", getName());
		pcContext.put("maxHp", getHp().toString());
		pcContext.put("hp", getCurrentHp().toString());
		pcContext.put("att", getAtt().toString());
		pcContext.put("lev", level.toString());
		pcContext.put("xp", xp.toString());
		pcContext.put("reqdXp", requiredXp.toString());
		
		return pcContext;
	}
}
