package abd.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abd.game.character.GameCharacter;
import abd.game.character.GameCharacterBuilder;
import abd.game.character.NPCharacter;
import abd.game.character.PChacrater;
import abd.game.scene.GameEvent;
import abd.game.scene.GameScene;

public class GameManager implements GameInterface{
	private PChacrater player;
	//씬
	private GameScene currentScene;
	//이벤트
	private GameEvent currentEvent;
	private Map<String,Object> eventContext;
	
	private GameDataLoader loader;
	
	public GameManager() {
		eventContext = new HashMap<String, Object>();
	}
	
	@Override
	public void createPlayerCharacter(GameDataLoader loader, String name) throws Exception {
		// TODO Auto-generated method stub
		this.loader = loader;
		
		Map<String,String> paramMap = new HashMap<String, String>();
		paramMap.put("LEVEL", "1");
		Map<String,String> characterInfo = loader.getPChacterInfo(paramMap).get(0);
		
		player = GameCharacterBuilder.getPCharacterInstance(characterInfo, name);
		player.setGameManager(this);
	}
	
	
//	public void setEncounterdChracter(List<Map<String, String>> characterList,GameDataLoader loader) throws Exception {
//		//조우 발생
//		encounter.encounterChracter(characterList,loader);
//		//누구랑 조우했는지 가져옴
//		this.encounteredChracter = encounter.getEncounterCharacter();
//	}
//	public GameCharacter getEncounterdChracter() {
//		// TODO Auto-generated method stub
//		return encounteredChracter;
//	}
//	
//	@Override
//	public boolean isEncountered() {
//		// TODO Auto-generated method stub
//		return encounteredChracter != null;
//	}
//	@Override
//	public void takeActionToEachother(String actionCommand) throws Exception {
//		// TODO Auto-generated method stub
//		if(encounteredChracter != null) {
//			if("runAway".equals(actionCommand)) {
//				//도망이면
//				encounteredChracterline = encounteredChracter.getLine();
//				
//				countTurn();
//				player.runAwayFrom(encounteredChracter);
//				encounteredChracter = null;
//				encounter.clearCharacter();
//				
//			}else if("battle".equals(actionCommand)) {
//				if(encounteredChracter.equalsClass(GameCharacter.CLASS_FIGHTER)) {
//					player.takeFight(encounteredChracter);
//					countTurn();
//				}else if(encounteredChracter.equalsClass(GameCharacter.CLASS_HEALER)) {
//					encounteredChracterline = encounteredChracter.getActionLine();
//					
//					player.takeCure(encounteredChracter);
//					countTurn();
//					encounteredChracter.setFreq(1);
//					encounteredChracter = null;
//					encounter.clearCharacter();
//				}
//			}
//		}else {
//			encounter.clearCharacter();
//		}
//	}
//
//	private void countTurn() {
//		// TODO Auto-generated method stub
//		encounter.countTurn();
//	}
//	@Override
//	public void talkWithHealer(GameDataLoader loader) throws Exception {
//		// TODO Auto-generated method stub
//		Map<String,String> paramMap = new HashMap<String,String>();
//		paramMap.put("MAP_CD", "M01");
//		paramMap.put("CLASS_CD", "H");
//		List<Map<String,String>> healerList = loader.getCharactersInMap(paramMap);
//		
//		NPCharacter healer = encounter.encounterHealer(healerList, loader);
//		healer.react(player);
//		encounteredChracterline = healer.getActionLine();
//		countTurn();
//		healer.setFreq(1);
//	}
//	@Override
//	public void goAdventure(GameDataLoader loader) throws Exception {
//		// TODO Auto-generated method stub
//		Map<String,String> paramMap = new HashMap<String,String>();
//		paramMap.put("MAP_CD", "M01");
//		List<Map<String,String>> characterList = loader.getCharactersInMap(paramMap);
//		setEncounterdChracter(characterList, loader);
//	}
//	@Override
//	public Map<String, Object> getGameContext() {
//		// TODO Auto-generated method stub
//		Map<String, Object> context = new HashMap<String, Object>();
//		Map<String, String> pCharContext = player.getCharacterContext();
//		context.put("player", pCharContext);
//		if(encounteredChracter != null) {
//			Map<String, String> npCharContext = encounteredChracter.getCharacterContext();
//			context.put("npc", npCharContext);
//			if(encounteredChracter.equalsClass(GameCharacter.CLASS_HEALER)) {
//				
//			}else {
//				if(!encounteredChracter.isAlive()) {
//					//이기거나
//					context.put("status", "win");
//					encounteredChracter = null;
//					encounter.clearCharacter();
//					
//				}else if(!player.isAlive()){
//					//지거나
//					context.put("status", "defeat");
//					
//				}else {
//					//진행중
//					context.put("status", "battle");
//				}
//			}
//		}
//		if(encounteredChracterline != null) {
//			context.put("line", encounteredChracterline);
//			context.put("status", "goAway");
//			encounteredChracterline = null;
//		}
//			
//		return context;
//	}

	@Override
	public void startSceneLoad(GameDataLoader loader) throws Exception {
		// TODO Auto-generated method stub
		List<Map<String,String>> startSceneInfoList = loader.getStartSceneInfo(new HashMap<String, String>());
		currentScene = new GameScene(startSceneInfoList.get(0),loader,this);
		
		currentEvent = currentScene.getEvent();
	}

	@Override
	public Map<String, Object> getGameContext() {
		// TODO Auto-generated method stub
		Map<String, Object> context = new HashMap<String, Object>();
		Map<String, String> pCharContext = player.getCharacterContext();
		context.put("player", pCharContext);
		context.putAll(eventContext);
		
		//eventContext.clear();
		
		return context;
	}
	
	@Override
	public void goEvent() {
		if(currentEvent.isDone()) {
			currentEvent = currentScene.getEvent();
			eventContext = currentEvent.happened();
		}else {
			eventContext = currentEvent.happened();
		}
	}
	
	@Override
	public void goEvent(Map<String,Object> input) throws Exception {
		if(currentEvent.isDone()) {
			currentEvent = currentScene.getEvent();
			eventContext = currentEvent.happened(input);
		}else {
			eventContext = currentEvent.happened(input);
		}
	}

	public PChacrater getPlayer() {
		// TODO Auto-generated method stub
		return player;
	}
	
	//리플렉션으로 실행하는 셀렉트 선택의 결과 메서드들
	public Map<String,Object> goNextEvent() {
		currentEvent.hasDone();
		currentEvent = currentScene.getEvent();
		return currentEvent.happened();
	}
	
	public Map<String,Object> playerLevelUp() throws Exception {
		//player.levelUp();
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("LEVEL", String.valueOf(player.getLevel()+1));
		List<Map<String,String>> lvlDataList = loader.getPChacterInfo(paramMap);
		Map<String,String> lvlData = lvlDataList.get(0);
		player.setLvlStatus(lvlData);
		
		return new HashMap<String, Object>();
	}
}
