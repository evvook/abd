package abd.game.character;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import abd.game.GameDataLoader;
import abd.game.GameManager;

public class PCharacter extends GameCharacter implements Playerable{
	private String job;
	private GameManager manager;
	
	private Integer xp;//경험치
	private Integer requiredXp;//요구경험치
	private Integer level;//플레이어 캐릭터 레벨
	
	private Map<String,CompCharacter> company;
	private Map<String,CompCharacter> calledCompany;
	
	public PCharacter(String charCd, String charNm, String classCd) {
		// TODO Auto-generated constructor stub
		super(charCd, charNm,classCd);
		
		//플레이어는 기본 100의 정신력을 가짐
		this.maxMp = 100;
		this.currentMp = 100;
		
		calledCompany = new HashMap<String, CompCharacter>();
	}
	
	public void setGameManager(GameManager manager) {
		this.manager = manager;
	}
	
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
		pcContext.put("maxMp", getMp().toString());
		pcContext.put("mp", getCurrentMp().toString());	
		pcContext.put("att", getAtt().toString());
		pcContext.put("lev", level.toString());
		pcContext.put("xp", xp.toString());
		pcContext.put("reqdXp", requiredXp.toString());
		
		return pcContext;
	}

	public Map<String, Object> useItem(String item) {
		// TODO Auto-generated method stub
		Map<String,Object> resultMap = new HashMap<String, Object>();
		if("candy".equals(item)) {
			setCurrentHp(getCurrentHp()+20);
			resultMap.put("statusDetail", "usedItem");
			resultMap.put("player", getCharacterContext());
		}
		return resultMap;
	}

	public CompCharacter getCompany(String characterCode) {
		// TODO Auto-generated method stub
		//호출된 동료는 별도로 담아둔다. 응답간격을 조절하기 위함
		calledCompany.put(characterCode, company.get(characterCode));
		return company.get(characterCode);
	}

	public void createCompanyCharacters(GameDataLoader loader) throws Exception {
		// TODO Auto-generated method stub
		company = new HashMap<String, CompCharacter>();
		
		//동료 정보 로드
		List<Map<String,String>> companyList = loader.getCompCharacter();
		
		for(Map<String,String> companyInfo:companyList) {
			String caracterCode = companyInfo.get("CHAR_CD");
			List<Map<String,String>> characterLines = loader.getCharacterLine(companyInfo);
			CompCharacter companyCharacter = GameCharacterBuilder.getCompCharacterInstance(companyInfo,characterLines);
			company.put(caracterCode, companyCharacter);
		}
	}

	public void countTurn() {
		// TODO Auto-generated method stub
		Map<String,CompCharacter> calledCompCopy = new HashMap<String, CompCharacter>();
		calledCompCopy.putAll(calledCompany);
		
		for(String key:calledCompCopy.keySet()) {
			CompCharacter comp = calledCompCopy.get(key);
			comp.countTurn();
			if(comp.isActive()) {
				calledCompany.remove(key);
			}
		}
	}
	
//	public List<String> getCalledCompCharCd(){
//		Set<String> characterCodeSet = calledCompany.keySet();
//		return Arrays.asList(characterCodeSet.toArray(new String[characterCodeSet.size()])); 
//	}
}
