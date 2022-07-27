package abd.game.character;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import abd.game.GameManager;
import abd.game.GameSetupLoader;
import abd.game.character.item.Candy;
import abd.game.character.item.GameItem;

public class PCharacter extends GameCharacter implements Playerable{
	private String job;
	private GameManager manager;
	
	private Integer xp;//경험치
	private Integer requiredXp;//요구경험치
	private Integer level;//플레이어 캐릭터 레벨
	
	private Map<String,CompCharacter> company;
	private Map<String,CompCharacter> removedCompany;
	private Map<String,CompCharacter> calledCompany;
	
	private Map<String,Object> actionResult;
	
	private Map<String,LinkedList<GameItem>> items;
	
	public PCharacter(String charCd, String charNm, String classCd, String hp, String att, String ac, String av) {
		// TODO Auto-generated constructor stub
		super(charCd, charNm,classCd, hp, att, ac, av);
		
		//플레이어는 기본 100의 정신력을 가짐
		this.maxMp = 100;
		this.currentMp = 100;
		
		removedCompany = new HashMap<String, CompCharacter>();
		calledCompany = new HashMap<String, CompCharacter>();
		actionResult = new HashMap<String, Object>();
		
		items = new HashMap<String, LinkedList<GameItem>>();
		
		//테스트용 코드
		LinkedList<GameItem> candy = new LinkedList<GameItem>();
		candy.add(new Candy());
		candy.add(new Candy());
		candy.add(new Candy());
		candy.add(new Candy());
		candy.add(new Candy());
		items.put("candy", candy);
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
	
	public void takeExp(NonPlayerable charact) throws Exception {
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
	public void setActionResult(Map<String,Object> actionResult) {
		this.actionResult.putAll(actionResult);
	}

	@Override
	public void act(NPCharacter other) {
		// TODO Auto-generated method stub
		setActionResult(action.act(other));
	}
	
	@Override
	public Integer getLevel() {
		// TODO Auto-generated method stub
		return level;
	}

	public void setLvlStatus(String level, String hp, String att, String ac, String av, String requiredXp) {
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
			this.ac = Integer.valueOf(ac);
			this.av = Integer.valueOf(av);
			this.requiredXp = Integer.valueOf(requiredXp);
			
			setAcAvPool();
		}catch(NullPointerException e) {
			throw e;
		}
	}

	public void setLvlStatus(Map<String, String> lvlData) {
		// TODO Auto-generated method stub
		setLvlStatus(lvlData.get("LEVEL"),lvlData.get("HP"),lvlData.get("ATT"),lvlData.get("AC"),lvlData.get("AV"),lvlData.get("REQD_XP"));
	}	

	@Override
	public Map<String, Object> getCharacterContext() {
		// TODO Auto-generated method stub
		//"{\"name\":\"김은우\",\"hp\":\"100\",\"att\":\"10\",\"xp\":\"0\",\"lev\":\"1\",\"reqdXp\":\"15\"}"
		Map<String,Object> pcContext = new HashMap<String, Object>();
		
		pcContext.put("name", getName());
		pcContext.put("maxHp", getHp().toString());
		pcContext.put("hp", getCurrentHp().toString());
		pcContext.put("maxMp", getMp().toString());
		pcContext.put("mp", getCurrentMp().toString());	
		pcContext.put("att", getAtt().toString());
		pcContext.put("ac", getAc().toString());
		pcContext.put("av", getAv().toString());
		pcContext.put("lev", level.toString());
		pcContext.put("xp", xp.toString());
		pcContext.put("reqdXp", requiredXp.toString());
		
		Map<String,Object> arCopy = new HashMap<String, Object>();
		arCopy.putAll(actionResult);
		pcContext.put("actionResult", arCopy);
		actionResult.clear();
		
		return pcContext;
	}

	public Map<String, Object> useItem(String item) {
		// TODO Auto-generated method stub
		Map<String,Object> resultMap = new HashMap<String, Object>();
		if("candy".equals(item)) {
			//setCurrentHp(getCurrentHp()+20);
			LinkedList<GameItem> candy = items.get(item);
			if(!candy.isEmpty()) {
				GameItem aCandy = candy.pop();
				aCandy.use(this);
				resultMap.put("selectResult", "무설탕 사탕을 먹고 체력을 20회복했다.");
			}else {
				resultMap.put("selectResult", "무설탕 사탕이 다 떨어졌다.");
			}
			resultMap.put("selectOption", "usedItem");
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

	public void createCompanyCharacters(GameSetupLoader loader) throws Exception {
		// TODO Auto-generated method stub
		company = new HashMap<String, CompCharacter>();
		
		//동료 정보 로드
		List<Map<String,String>> companyList = loader.getCompCharacter();
		
		for(Map<String,String> companyInfo:companyList) {
			String caracterCode = companyInfo.get("CHAR_CD");
			//List<Map<String,String>> characterLines = loader.getCharacterLine(companyInfo);
			List<Map<String,String>> characterLines = new ArrayList<Map<String,String>>();
			characterLines.add(companyInfo);
			CompCharacter companyCharacter = GameCharacterBuilder.getCompCharacterInstance(companyInfo,characterLines);
			companyCharacter.setPlayer(this);
			company.put(caracterCode, companyCharacter);
		}
	}
	
	public Map<String,Object> getCompContext(){
		Map<String,Object> compContext = new HashMap<String,Object>();
		for(String compKey:company.keySet()) {
			CompCharacter comp = company.get(compKey);
			compContext.put(comp.getName(),comp.getCharacterContext());
		}
		return compContext;
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

	@Override
	public void setJob(String job) {
		// TODO Auto-generated method stub
		this.job = job;
	}
	
	@Override
	public String getJob() {
		return job;
	}
//	public List<String> getCalledCompCharCd(){
//		Set<String> characterCodeSet = calledCompany.keySet();
//		return Arrays.asList(characterCodeSet.toArray(new String[characterCodeSet.size()])); 
//	}

	public void setCompany(String characterCode) {
		List<String> compCodeList = Arrays.asList(characterCode.split(","));
		// TODO Auto-generated method stub
		//받아서 삭제캐릭터 맵에 담거나
		Map<String,CompCharacter> companyCopy = new HashMap<String, CompCharacter>();
		companyCopy.putAll(company);
		
		for(String compKey:companyCopy.keySet()) {
			if(!compCodeList.contains(compKey)) {
				removedCompany.put(compKey, company.remove(compKey));
			}
		}
		
		Map<String,CompCharacter> removedCompanyCopy = new HashMap<String, CompCharacter>();
		removedCompanyCopy.putAll(removedCompany);
		//삭제 캐릭터 맵에서 꺼내서 컴퍼니에 담거나
		for(String compKey:removedCompanyCopy.keySet()) {
			if(compCodeList.contains(compKey)) {
				company.put(compKey, removedCompany.remove(compKey));
			}
		}
	}

	public void restoreCompany(String characterCode) {
		// TODO Auto-generated method stub
		List<String> compCodeList = Arrays.asList(characterCode.split(","));
		Map<String,CompCharacter> removedCompanyCopy = new HashMap<String, CompCharacter>();
		removedCompanyCopy.putAll(removedCompany);
		//삭제 캐릭터 맵에서 꺼내서 컴퍼니에 담거나
		for(String compKey:removedCompanyCopy.keySet()) {
			if(compCodeList.contains(compKey)) {
				company.put(compKey, removedCompany.remove(compKey));
			}
		}
	}

	public void increaseCompReliabl() {
		// TODO Auto-generated method stub
		for(String key:company.keySet()) {
			CompCharacter comp = company.get(key);
			comp.increaseReliabl(10);
		}
	}

	public void initCompActive() {
		// TODO Auto-generated method stub
		for(String key:company.keySet()) {
			CompCharacter comp = company.get(key);
			comp.setActive();
		}
	}
}
