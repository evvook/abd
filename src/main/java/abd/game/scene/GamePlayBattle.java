package abd.game.scene;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abd.game.Encounter;
import abd.game.GameDataLoader;
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
	private GamePlaySelect battleSelect;//공격한다, 동료에게 도움요청, 후퇴한다
	private GamePlaySelect pullBackSelect;//전투에 복귀한다, 아이템을 사용한다, 동료에게 도움을 요청, 장소에서 이탈한다
	
	public GamePlayBattle(Map<String, String> playInfo, GameDataLoader loader, PChacrater player) throws Exception {
		// TODO Auto-generated constructor stub
		this.loader = loader;
		this.player = player;
		
		List<Map<String,String>> battleList = loader.getBattleOfPlay(playInfo);
		Map<String,String> battleInfo = battleList.get(0);
		mapCode = battleInfo.get("MAP_CD");
		
		encounter = new Encounter();
		setEncounterdChracter(loader);
	}

	@Override
	public Map<String, Object> getElContext() {
		// TODO Auto-generated method stub
		Map<String, Object> context = new HashMap<String, Object>();
		Map<String, String> pCharContext = player.getCharacterContext();
		context.put("player", pCharContext);
		if(encounteredChracter != null) {
			Map<String, String> npCharContext = encounteredChracter.getCharacterContext();
			context.put("npc", npCharContext);
			if(encounteredChracter.equalsClass(GameCharacter.CLASS_HEALER)) {
				
			}else {
				if(!encounteredChracter.isAlive()) {
					//이기거나
					context.put("status", "win");
					encounteredChracter = null;
					encounter.clearCharacter();
					
				}else if(!player.isAlive()){
					//지거나
					context.put("status", "defeat");
					
				}else {
					//진행중
					context.put("status", "battle");
				}
			}
		}
		if(encounteredChracterline != null) {
			context.put("line", encounteredChracterline);
			context.put("status", "goAway");
			encounteredChracterline = null;
		}
			
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
				if(encounteredChracter.equalsClass(GameCharacter.CLASS_FIGHTER)) {
					player.takeFight(encounteredChracter);
					countTurn();
				}else if(encounteredChracter.equalsClass(GameCharacter.CLASS_HEALER)) {
					encounteredChracterline = encounteredChracter.getActionLine();
					
					player.takeCure(encounteredChracter);
					countTurn();
					encounteredChracter.setFreq(1);
					encounteredChracter = null;
					encounter.clearCharacter();
				}
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
	public void play(Map<String, Object> commandInfo) throws Exception {
		// TODO Auto-generated method stub
		String command = (String)commandInfo.get("command");
		takeActionToEachother(command);
	}	
}
