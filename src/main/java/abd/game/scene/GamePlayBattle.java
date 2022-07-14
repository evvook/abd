package abd.game.scene;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abd.game.Encounter;
import abd.game.GameDataLoader;
import abd.game.GameManager;
import abd.game.character.GameCharacter;
import abd.game.character.NPCharacter;
import abd.game.character.PChacrater;

public class GamePlayBattle implements GamePlayElement {
	private String battleCode;
	private String mapCode;
	
	private GameDataLoader loader;
	
	private PChacrater player;
	private NPCharacter encounteredChracter;
	private Encounter encounter;
	private String encounteredChracterline;
	
	//선택지를 두개 가질 수 있다?
	private GamePlaySelect battleSelect;
	private GamePlaySelect battleHelpSelect;
	private GamePlaySelect pullBackSelect;
	private GamePlaySelect pullBackHelpSelect;
	
	private GamePlaySelect currentSelect;
	private String currentStatus;
	private GamePlaySelect beforeSelect;
	private String beforeStatus;
	
	private boolean battle;
	
	public GamePlayBattle(Map<String, String> playInfo, GameDataLoader loader, PChacrater player) throws Exception {
		// TODO Auto-generated constructor stub
		this.loader = loader;
		this.player = player;
		
		List<Map<String,String>> battleList = loader.getBattleOfPlay(playInfo);
		Map<String,String> battleInfo = battleList.get(0);
		battleCode = battleInfo.get("BATTLE_CD");
		mapCode = battleInfo.get("MAP_CD");
		
		//공격한다, 동료에게 도움요청, 후퇴한다
		Map<String,String> battleSelectInfo = new HashMap<String,String>();
		battleSelectInfo.put("PLAY_CD", "PB01");
		battleSelect = new GamePlaySelect(battleSelectInfo,loader);
		
		battleSelectInfo.put("PLAY_CD", "PB02");
		battleHelpSelect = new GamePlaySelect(battleSelectInfo,loader);
		
		//아이템을 사용한다, 동료에게 도움을 요청, 전투에 복귀한다, 장소에서 이탈한다
		battleSelectInfo.put("PLAY_CD", "PB03");
		pullBackSelect = new GamePlaySelect(battleSelectInfo,loader);
		
		battleSelectInfo.put("PLAY_CD", "PB04");
		pullBackHelpSelect = new GamePlaySelect(battleSelectInfo,loader);
		
		//전투 준비
		encounter = new Encounter();
		initBattle();
		
	}

	@Override
	public Map<String, Object> getElContext() throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> context = new HashMap<String, Object>();
		Map<String, String> pCharContext = player.getCharacterContext();
		context.put("player", pCharContext);
		if(encounteredChracter != null) {
			Map<String, String> npCharContext = encounteredChracter.getCharacterContext();
			if(!encounteredChracter.isAlive()) {
				//이기거나
				//다음 상대 셋팅
				if(encounter.isPoolEmpty()) {
					//다음 이벤트로 이동할 수 있는 무언가 필요
					context.put("status", "endBattle");
					
				}else {
					context.put("status", "win");
					setEncounterdChracter(loader);
					context.put("npc", npCharContext);
				}
				
			}else if(!player.isAlive()){
				//지거나
				context.put("status", "defeat");
				//사망 씬으로 이동할 수 있는 무언가 필요
				
			}else {
				//진행중
				context.put("status", "battle");
				context.put("npc", npCharContext);
			}
		}
		if(encounteredChracterline != null) {
			context.put("line", encounteredChracterline);
			context.put("status", "goAway");
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

	private void countTurn() {
		// TODO Auto-generated method stub
		encounter.countTurn();
	}

	@Override
	public Map<String, Object> playSelect(Map<String, String> selectedInfo, GameManager manager) throws Exception {
		Map<String,Object> resultMap = new HashMap<String, Object>();
		
		Map<String,Object> selectContext = null;
		if("BSL01".equals(selectedInfo.get("SELECT_CD"))) {
			selectContext = battleSelect.playSelect(selectedInfo,manager);
			
		}else if("BSL02".equals(selectedInfo.get("SELECT_CD"))) {
			selectContext = battleHelpSelect.playSelect(selectedInfo,manager);
			
		}else if("BSL03".equals(selectedInfo.get("SELECT_CD"))) {
			selectContext = pullBackSelect.playSelect(selectedInfo,manager);
			
		}else if("BSL04".equals(selectedInfo.get("SELECT_CD"))) {
			selectContext = pullBackHelpSelect.playSelect(selectedInfo,manager);
			
		}
		resultMap.putAll(selectContext);
		
		return resultMap;
	}
	
	public Map<String, Object> play(String command) throws Exception {
		// TODO Auto-generated method stub
		Map<String,Object> battleContext = new HashMap<String, Object>();
		beforeSelect = currentSelect;
		beforeStatus = currentStatus;
		
		if("battle".equals(command)) {
			//전투진행
			takeActionToEachother(command);
			//화면으로 전달할 정보 설정
			currentSelect = battleSelect;
			currentStatus ="battle";
			battleContext.put("status", currentStatus);
			battleContext.put("battleCode", getCode());
		}else if("battleHelp".equals(command)) {
			//화면이 전환되면서 선택지가 뜬다.
			//화면으로 전달할 정보 설정
			currentSelect = battleHelpSelect;
			currentStatus ="battleHelp";
			battleContext.put("status", currentStatus);
			battleContext.put("battleCode", getCode());
		}else if("pullBack".equals(command)) {
			//화면이 전환되면서 선택지가 뜬다.
			//화면으로 전달할 정보 설정
			currentSelect = pullBackSelect;
			currentStatus ="pullBack";
			battleContext.put("status", currentStatus);
			battleContext.put("battleCode", getCode());
		}else if("pullBackHelp".equals(command)) {
			//화면이 전환되면서 선택지가 뜬다.
			//화면으로 전달할 정보 설정
			currentSelect = pullBackHelpSelect;
			currentStatus ="pullBackHelp";
			battleContext.put("status", currentStatus);
			battleContext.put("battleCode", getCode());
		}
		battleContext.putAll(getElContext());
		
		return battleContext;
	}
	
	public Map<String, Object> cancelSelect() throws Exception{
		Map<String,Object> battleContext = new HashMap<String, Object>();
		currentSelect = beforeSelect;
		currentStatus = beforeStatus;
		battleContext.put("status", currentStatus);
		battleContext.put("battleCode", getCode());
		battleContext.putAll(getElContext());
		return battleContext;
	}

	public Map<String, Object> goBattle() throws Exception {
		// TODO Auto-generated method stub
		Map<String,Object> battleContext = new HashMap<String, Object>();
		currentSelect = battleSelect;
		battleContext.put("status", currentStatus);
		battleContext.put("battleCode", getCode());
		battleContext.putAll(getElContext());
		return battleContext;
	}

	public void initBattle() throws Exception {
		// TODO Auto-generated method stub
		//전투 선택지 초기화
		currentSelect = battleSelect;
		//
		currentStatus = "battle";
		//조우 초기화
		setEncounterdChracter(loader);
	}
}
