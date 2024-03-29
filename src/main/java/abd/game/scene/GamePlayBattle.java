package abd.game.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import abd.game.GameDataLoader;
import abd.game.character.CompCharacter;
import abd.game.character.NPCharacter;
import abd.game.character.PCharacter;
import abd.game.character.item.GameItem;
import abd.game.scene.battle.Encounter;

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

	private GamePlaySelect useItemSelect;
	
	//현재 상태
	private GamePlaySelect currentSelect;
	private GamePlaySelect beforeSelect;
	
	private String currentSelectName;
	private String beforeSelectName;
	
	private boolean onGoing;
	
	private StringBuilder scripts;
	
	private static String fightScriptTemplate = "%pResult% %pDmg%의 데미지를 주었다. %eResult% %eDmg%의 데미지를 받았다.<br>";
	private static String fightWihCompScriptTemplate = "%pResult% %compName%이(가) %pDmg%의 데미지를 주었다. %eResult% %compName%이(가) %eDmg%의 데미지를 받았다.<br>";
	private static String fightResultPlayerScriptTemplate = "%aResult1% %aResult2%";
	private static String fightResultEnermyScriptTemplate = "%aResult1%";

	private boolean isPlayerLevelUp;
	
	private Integer initHp;
	private Integer initLvl;
	
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
		scripts = new StringBuilder();
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
		initHp = player.getCurrentHp();
		initLvl = player.getLevel();
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
					
					//game.monster.name+"을/를 이겨 "+game.monster.xp+" 경험치를 얻었다. ";
					scripts.append(npCharContext.get("name")+"을(를) 이겨"+npCharContext.get("xp")+" 경험치를 얻었다.<br>");
					if(isPlayerLevelUp) {
						scripts.append("레벨업! 레벨 "+player.getLevel()+"<br>");
						isPlayerLevelUp = false;
					}
				}
				
			}else if(!player.isAlive()){
				//지거나
				context.put("battleResult", "defeat");
				onGoing = false;
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
					
					scripts.append(npCharContext.get("name")+"을(를) 이겨"+npCharContext.get("xp")+" 경험치를 얻었다.<br>");
					if(isPlayerLevelUp) {
						scripts.append("레벨업! 레벨 "+player.getLevel()+"<br>");
						isPlayerLevelUp = false;
					}
					scripts.append("모든 적을 쓰러트렸다.<br>");
					//전투가 승리로 끝나면 모든 동료 신뢰도 10증가
					player.increaseCompReliabl();
				}else if("defeat".equals(context.get("battleResult"))){
					context.put("process", "end");
				}else{
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
		
		context.put("script", scripts.toString());
		scripts.delete(0, scripts.length());
			
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
				if(initLvl < player.getLevel()) {
					isPlayerLevelUp = true;
					initHp = player.getCurrentHp();
					initLvl = player.getLevel();
				}
				scripts.append(makeBattleScript(player.getActionResult(), new String(fightScriptTemplate)));
				
				countTurn();
			}
		}else {
			encounter.clearCharacter();
		}
	}
	
	@SuppressWarnings("unchecked")
	private String makeBattleScript(Map<String,Object> aResult, String template) {
//		Map<String,Object> aResult = player.getActionResult();
		Map<String,Object> playerResult = (Map<String,Object>)aResult.get("playerResult");
		if(playerResult == null) {
			return "";
		}
		
		String pResultCode1 = (String)playerResult.get("ATTACK1");
		String pResultCode2 = (String)playerResult.get("ATTACK2");
		Integer pDmg = (Integer)playerResult.get("DMG1")+(Integer)playerResult.get("DMG2");
		
		Map<String,Object> enermyResult = (Map<String,Object>)aResult.get("enermyResult");
		String eResultCode1 = "";
		Integer eDmg = 0;
		if(enermyResult != null) {
			eResultCode1 = (String)enermyResult.get("ATTACK1");
			eDmg = (Integer)enermyResult.get("DMG1");
		}
		//"%aResult1%!, %aResult2%!";
		String copyFightResultPlayerScriptTemplate = new String(fightResultPlayerScriptTemplate);
		copyFightResultPlayerScriptTemplate = copyFightResultPlayerScriptTemplate.replace("%aResult1%", getPlayerResult(pResultCode1));
		copyFightResultPlayerScriptTemplate = copyFightResultPlayerScriptTemplate.replace("%aResult2%", getPlayerResult(pResultCode2));
		template = template.replace("%pResult%", copyFightResultPlayerScriptTemplate);
		
		String copyFightResultEnermyScriptTemplate = new String(fightResultEnermyScriptTemplate);
		copyFightResultEnermyScriptTemplate = copyFightResultEnermyScriptTemplate.replace("%aResult1%", getEnermyResult(eResultCode1));
		template = template.replace("%eResult%", copyFightResultEnermyScriptTemplate);
		template = template.replace("%pDmg%", pDmg.toString());
		template = template.replace("%eDmg%", eDmg.toString());
		
		if(company != null) {
			template = template.replace("%compName%", company.getName());
		}
		
		return template;
	}
	
	private String getPlayerResult(String code) {
		String result = null;
		if("SUCCESS".equals(code)) {
			result = "공격 성공!";
		}else if("MISSED".equals(code)) {
			result = "빗나감!";
		}else if("AVOID".equals(code)) {
			result = "적 회피!";
		}
		return result;
	}
	
	private String getEnermyResult(String code) {
		String result = "";
		if("SUCCESS".equals(code)) {
			result = "회피 실패!";
		}else if("MISSED".equals(code)) {
			result = "빗나감!";
		}else if("AVOID".equals(code)) {
			result = "회피 성공!";
		}
		return result;
	}

	public void countTurn() {
		// TODO Auto-generated method stub
		encounter.countTurn();
		player.countTurn();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> play(Map<String, Object> input, GameEventCallback callback) throws Exception {
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
					selectContext = battleSelect.play(input,callback);
					
				}else if("battleHelp".equals(currentSelectName)) {
					//도움요청선택
					selectContext = battleHelpSelect.play(input,callback);
					
				}else if("pullBack".equals(currentSelectName)) {
					//재정비선택
					selectContext = pullBackSelect.play(input,callback);
					
				}else if("pullBackHelp".equals(currentSelectName)) {
					//도움받기선택
					selectContext = pullBackHelpSelect.play(input,callback);
					
				}else if("useItem".equals(currentSelectName)) {
					selectContext = useItemSelect.play(input,callback);
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
		}else if("useItem".equals(command)) {
			//화면이 전환되면서 선택지가 뜬다.
			//화면으로 전달할 정보 설정
			currentSelect = useItemSelect;
			currentSelectName = "useItem";
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
		
		player.initCompActive();
		
		onGoing = false;
	}

	public Map<String, Object> callForHelp(String characterCode) throws Exception {
		// TODO Auto-generated method stub
		company = player.callCompany(characterCode);
		//대사 스크립트로 설정
		scripts.append(company.getLine()+"<br>");
		if(company.isActive()) {
			//전투 수행
			company.act(encounteredChracter);
			if(initLvl < player.getLevel()) {
				isPlayerLevelUp = true;
				initHp = player.getCurrentHp();
				initLvl = player.getLevel();
			}
			//전투결과 스크립트로 설정
			scripts.append(makeBattleScript(player.getActionResult(), new String(fightWihCompScriptTemplate)));
			//턴 지남
			countTurn();
		}
		
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
		company = player.callCompany(characterCode);
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
	
	public Map<String, Object> useItem(String item) throws Exception {
		// TODO Auto-generated method stub
		Map<String,Object> resultMap = player.useItem(item);
		countTurn();
		
		//후처리
		Map<String,Object> battleContext = new HashMap<String, Object>();
		battleContext.putAll(resultMap);
		
		currentSelect = beforeSelect;
		currentSelectName = beforeSelectName;
		
		battleContext.put("selectOption", "usedItem");
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

	public Map<String, Object> getItemSelectContext() throws Exception {
		// TODO Auto-generated method stub
		useItemSelect = new GamePlaySelect();
		
		Map<String,Object> selectInfo = new HashMap<String,Object>();
		selectInfo.put("SELECT_CD", "USE_ITEM_SELECT");
		selectInfo.put("SELECT_ALIAS", "useItem");
		selectInfo.put("NUM_OF_SELECT", -1);
		selectInfo.put("SELECT_HEAD", "무엇을 사용할까?");
		
		List<Map<String,String>> optionList = new ArrayList<Map<String,String>>();
		
		Map<String,LinkedList<GameItem>> items = player.getItems();
		Integer seq = 1;
		for(String itmeKey:items.keySet()) {
			List<GameItem> itemList = items.get(itmeKey);
			if(!itemList.isEmpty()) {
				GameItem item = itemList.get(0);
				Map<String,String> option = new HashMap<String,String>();
				option.put("OPTION_SEQ", String.valueOf(seq++));
				option.put("OPTION_TXT", item.getName()+"을(를) 먹는다.");
				option.put("RESULT_OCCURRED", "useItemBattle#"+item.getCode());
				optionList.add(option);
			}
		}
		
		
		Map<String,String> optionLast = new HashMap<String,String>();
		optionLast.put("OPTION_SEQ", "99");
		optionLast.put("OPTION_TXT", "취소");
		optionLast.put("RESULT_OCCURRED", "cancelBattleSelect");
		optionList.add(optionLast);
		
		selectInfo.put("OPTION_LIST", optionList);
		
		useItemSelect.setSelect(selectInfo);
		selectCodes.put((String)selectInfo.get("SELECT_ALIAS"),(String)selectInfo.get("SELECT_CD"));
			
		return play("useItem");
	}

	public Map<String, Object> getNoItemContext() throws Exception {
		// TODO Auto-generated method stub
		Map<String,Object> battleContext = new HashMap<String, Object>();
		battleContext.put("selectResult","보유한 아이템이 없습니다.");
		battleContext.put("selectOption", "usedItem");
		battleContext.put("battleCode", getCode());
		battleContext.putAll(getElContext());
		return battleContext;
	}

	public Integer getInitHp() {
		// TODO Auto-generated method stub
		return initHp;
	}
}
