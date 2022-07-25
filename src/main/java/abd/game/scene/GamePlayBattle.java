package abd.game.scene;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abd.game.Encounter;
import abd.game.GameDataLoader;
import abd.game.GameManager;
import abd.game.character.CompCharacter;
import abd.game.character.NPCharacter;
import abd.game.character.PCharacter;

public class GamePlayBattle implements GamePlayElement {
	private String battleCode;
	private String mapCode;
	private String nextEventCode;
	private String nextEventSeq;
	
	private GameDataLoader loader;
	
	private PCharacter player;
	private CompCharacter company;
	
	private NPCharacter encounteredChracter;
	private Encounter encounter;
	private String encounteredChracterline;
	
	//전투 에서 선택 가능한 선택지
	private Map<String,String> playCodes;
	private Map<String,String> selectCodes;
	private GamePlaySelect battleSelect;
	private GamePlaySelect battleHelpSelect;
	private GamePlaySelect pullBackSelect;
	private GamePlaySelect pullBackHelpSelect;
	
	//현재 상태
	private GamePlaySelect currentSelect;
	private GamePlaySelect beforeSelect;
	
	private String currentSelectName;
	private String beforeSelectName;
	
	private boolean onGoing;
	
	public GamePlayBattle(Map<String, String> playInfo, GameDataLoader loader, PCharacter player) throws Exception {
		// TODO Auto-generated constructor stub
		this.loader = loader;
		this.player = player;
		
		List<Map<String,String>> battleList = loader.getBattleOfPlay(playInfo);
		Map<String,String> battle = battleList.get(0);
		battleCode = battle.get("BATTLE_CD");
		mapCode = battle.get("MAP_CD");
		nextEventCode = battle.get("NEXT_EVENT_CD");
		nextEventSeq = battle.get("NEXT_EVENT_SEQ");
		
		playCodes = new HashMap<String, String>();
		selectCodes = new HashMap<String, String>();
		//공격한다, 동료에게 도움요청, 후퇴한다
		for(Map<String,String>battleInfo:battleList) {
			playCodes.put(battleInfo.get("SELECT_ALIAS"), battleInfo.get("PLAY_CD"));
			selectCodes.put(battleInfo.get("SELECT_ALIAS"), battleInfo.get("SELECT_CD"));
		}
		
		//기본전투 선택지만 생성 및 로딩
		Map<String,String> battleSelectInfo = new HashMap<String,String>();
		battleSelectInfo.put("PLAY_CD", playCodes.get("commonBattle"));
		battleSelect = new GamePlaySelect(battleSelectInfo,loader);
		
		//전투 준비
		encounter = new Encounter();
		initBattle();
		player.initCompActive();
	}

	@Override
	public Map<String, Object> getElContext() throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> context = new HashMap<String, Object>();
		if(encounteredChracter != null) {
			Map<String, Object> npCharContext = encounteredChracter.getCharacterContext();
			if(!encounteredChracter.isAlive()) {
				//이기거나
				//다음 상대 셋팅
				setEncounterdChracter(loader);
				if(isDone()) {
					onGoing = false;
				}else {
					context.put("npc", encounteredChracter.getCharacterContext());
					context.put("battleResult", "win");
					context.put("depeatedNpc", npCharContext);
					
					player.increaseCompReliabl();
				}
				
			}else if(!player.isAlive()){
				//지거나
				context.put("battleResult", "defeat");
				//사망 씬으로 이동할 수 있는 무언가 필요
			}else {
				context.put("npc", npCharContext);
			}
			
			//전투상태에 따라 데이터 설정
			if(onGoing){
				//진행중
				context.put("process", "onGoing");
			}else {
				if(isDone()) {
					//다음 이벤트로 이동할 수 있는 무언가 필요
					context.put("process", "end");
				}else {
					context.put("process", "start");
					currentSelectName = "commonBattle";
					context.put("selectName", currentSelectName);
					context.put("selectNext", currentSelectName);
					onGoing = true;
				}
			}
		}
		
		Map<String, Object> pCharContext = player.getCharacterContext();
		context.put("player", pCharContext);
		if(company != null) {
			context.put("company", company.getCharacterContext());
			//정보만 담고 비워준다.
			company = null;
		}		
		
		if(encounteredChracterline != null) {
			//context.put("line", encounteredChracterline);
			//context.put("status", "goAway");
			encounteredChracterline = null;
		}
		//전투 선택지 설정
		context.put("select", currentSelect.getElContext());
		context.put("selectCode", currentSelect.getCode());
			
		return context;
	}

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return battleCode;
	}
	
	public void setEncounterdChracter(GameDataLoader loader) throws Exception {
		//조우 발생
		encounter.encounterChracter(mapCode,loader);
		//누구랑 조우했는지 가져옴
		this.encounteredChracter = encounter.getEncounterCharacter();
	}
	
	public void takeActionToEachother(String actionCommand) throws Exception {
	// TODO Auto-generated method stub
		if(encounteredChracter != null) {
			if("runAway".equals(actionCommand)) {
				//도망이면
				encounteredChracterline = encounteredChracter.getLine();
				
				countTurn();
				player.runAwayFrom(encounteredChracter);
				encounteredChracter = null;
				encounter.clearCharacter();
				
			}else if("battle".equals(actionCommand)) {
				player.takeFight(encounteredChracter);
				countTurn();
			}
		}else {
			encounter.clearCharacter();
		}
	}

	public void countTurn() {
		// TODO Auto-generated method stub
		encounter.countTurn();
		player.countTurn();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> play(Map<String, Object> input, GameManager manager) throws Exception {
		Map<String,Object> resultMap = new HashMap<String, Object>();
		Map<String,String> selectInfo =(Map<String,String>)input.get("selected");
		
		Map<String,Object> selectContext = null;
		
		for(String selectAlias:selectCodes.keySet()) {
			String selectCode = selectCodes.get(selectAlias);
			if(selectCode.equals(selectInfo.get("SELECT_CD"))) {
				//일반전투선택
				//선택지명 기본셋팅
				currentSelectName = selectAlias;
				resultMap.put("selectName", currentSelectName);
				resultMap.put("selectNext", currentSelectName);
				
				if("commonBattle".equals(currentSelectName)) {
					//일반전투선택
					selectContext = battleSelect.play(input,manager);
					
				}else if("battleHelp".equals(currentSelectName)) {
					//도움요청선택
					selectContext = battleHelpSelect.play(input,manager);
					
				}else if("pullBack".equals(currentSelectName)) {
					//재정비선택
					selectContext = pullBackSelect.play(input,manager);
					
				}else if("pullBackHelp".equals(currentSelectName)) {
					//도움받기선택
					selectContext = pullBackHelpSelect.play(input,manager);
					
				}
				//전투선택 결과 설정
				resultMap.putAll(selectContext);
				break;
			}
		}
		return resultMap;
	}
	
	//gameManager에서 호출함
	//특정 선택지 설정
	public Map<String, Object> play(String command) throws Exception {
		// TODO Auto-generated method stub
		Map<String,Object> battleContext = new HashMap<String, Object>();
		beforeSelect = currentSelect;
		beforeSelectName = currentSelectName;
		
		if("battle".equals(command)) {
			//전투진행
			takeActionToEachother(command);
			//화면으로 전달할 정보 설정
			currentSelect = battleSelect;
			currentSelectName = "commonBattle";
			battleContext.put("selectName", currentSelectName);
			battleContext.put("selectNext", currentSelectName);
			battleContext.put("battleCode", getCode());
		}else if("battleHelp".equals(command)) {
			//화면이 전환되면서 선택지가 뜬다.
			//화면으로 전달할 정보 설정
			if(battleHelpSelect == null) {
				Map<String,String> selectInfo = new HashMap<String, String>();
				selectInfo.put("PLAY_CD", playCodes.get(command));
				battleHelpSelect = new GamePlaySelect(selectInfo,loader);
			}
			currentSelect = battleHelpSelect;
			currentSelectName = "battleHelp";
			//선택지명 설정
			battleContext.put("selectName", currentSelectName);
			battleContext.put("selectNext", currentSelectName);
			battleContext.put("battleCode", getCode());
		}else if("pullBack".equals(command)) {
			//화면이 전환되면서 선택지가 뜬다.
			//화면으로 전달할 정보 설정
			//없으면 로딩
			if(pullBackSelect == null) {
				Map<String,String> selectInfo = new HashMap<String, String>();
				selectInfo.put("PLAY_CD", playCodes.get(command));
				pullBackSelect = new GamePlaySelect(selectInfo,loader);
			}
			currentSelect = pullBackSelect;
			currentSelectName = "pullBack";
			//선택지명 설정
			battleContext.put("selectName", currentSelectName);
			battleContext.put("selectNext", currentSelectName);
			battleContext.put("battleCode", getCode());
		}else if("pullBackHelp".equals(command)) {
			//화면이 전환되면서 선택지가 뜬다.
			//화면으로 전달할 정보 설정
			//없으면 로딩
			if(pullBackHelpSelect == null) {
				Map<String,String> selectInfo = new HashMap<String, String>();
				selectInfo.put("PLAY_CD", playCodes.get(command));
				pullBackHelpSelect = new GamePlaySelect(selectInfo,loader);
			}
			currentSelect = pullBackHelpSelect;
			currentSelectName = "pullBackHelp";
			//선택지명 설정
			battleContext.put("selectName", currentSelectName);
			battleContext.put("selectNext", currentSelectName);
			battleContext.put("battleCode", getCode());
		}
		//여기서 스테이터스가 다 덮어씌워짐. 수정할 필요 있을까?
		battleContext.putAll(getElContext());
		
		return battleContext;
	}
	
	public Map<String, Object> cancelSelect() throws Exception{
		Map<String,Object> battleContext = new HashMap<String, Object>();
		currentSelect = beforeSelect;
		currentSelectName = beforeSelectName;
		
		battleContext.put("selectOption", "cancelSelect");
		battleContext.put("selectNext", currentSelectName);
		battleContext.put("battleCode", getCode());
		battleContext.putAll(getElContext());
		return battleContext;
	}

	public Map<String, Object> goBattle() throws Exception {
		// TODO Auto-generated method stub
		initBattle();
		Map<String,Object> battleContext = getBattle();
		return battleContext;
	}
	
	public Map<String, Object> getBattle() throws Exception {
		// TODO Auto-generated method stub
		Map<String,Object> battleContext = new HashMap<String, Object>();
		battleContext.put("battleCode", getCode());
		battleContext.putAll(getElContext());
		return battleContext;
	}

	public void initBattle() throws Exception {
		// TODO Auto-generated method stub
		//전투 선택지 초기화
		currentSelect = battleSelect;
		//조우 초기화
		setEncounterdChracter(loader);
		
		onGoing = false;
	}

	public Map<String, Object> callForHelp(String characterCode) throws Exception {
		// TODO Auto-generated method stub
		company = player.getCompany(characterCode);
		company.act(encounteredChracter);
		
		countTurn();
		
		//일반배틀선택 선택지 보여줌
		currentSelect = beforeSelect;
		currentSelectName = beforeSelectName;
		//동료의 싸움 결과 보여줌
		
		//후처리
		Map<String,Object> battleContext = new HashMap<String, Object>();
		battleContext.put("selectOption", "battleWithCompany");
		battleContext.put("selectNext", currentSelectName);
		battleContext.put("battleCode", getCode());
		battleContext.putAll(getElContext());
		return battleContext;
	}

	public Map<String, Object> getHelp(String characterCode, String ability) throws Exception {
		// TODO Auto-generated method stub
		company = player.getCompany(characterCode);
		if("SA1".equals(ability)) {
			company.reactSpAbl1(player);
		}else if("SA2".equals(ability)) {
			company.reactSpAbl2(player);
		}
		countTurn();
		
		//후처리
		Map<String,Object> battleContext = new HashMap<String, Object>();
		currentSelect = beforeSelect;
		currentSelectName = beforeSelectName;
		
		battleContext.put("selectOption", "getHelpFromCompany");
		battleContext.put("selectNext", currentSelectName);
		battleContext.put("battleCode", getCode());
		battleContext.putAll(getElContext());
		return battleContext;
	}

	public boolean isDone() {
		// TODO Auto-generated method stub
		return encounter.isPoolEmpty();
	}
	
	public Map<String,String> getNextEventInfo(){
		Map<String,String> nextEventInfo = new HashMap<String, String>();
		nextEventInfo.put("EVENT_CD", nextEventCode);
		nextEventInfo.put("EVENT_SEQ", nextEventSeq);
		return nextEventInfo;
	}
}
