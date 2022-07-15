package abd.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abd.game.character.CompCharacter;
import abd.game.character.GameCharacterBuilder;
import abd.game.character.NPCharacter;
import abd.game.character.PCharacter;
import abd.game.scene.GameEvent;
import abd.game.scene.GamePlayBattle;
import abd.game.scene.GameScene;

public class GameManager implements GameInterface{
	private PCharacter player;
	//씬
	private GameScene currentScene;
	//이벤트
	private GameEvent currentEvent;
	private Map<String,Object> eventContext;
	
	//데이터 로드를 위한 객체(서비스)
	private GameDataLoader loader;
	
	//전투 상황을 통제하기 위한 배틀 객체
	private GamePlayBattle pBtl;
	
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
		player.createCompanyCharacters(loader);
		player.setGameManager(this);
	}
	
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
	public void goEvent() throws Exception {
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

	public PCharacter getPlayer() {
		// TODO Auto-generated method stub
		return player;
	}
	
	public void setBattle(GamePlayBattle pEl) {
		// TODO Auto-generated method stub
		this.pBtl = pEl;
	}
	
	
	///////////////////////////////////////////////////////////
	//이하, 리플렉션으로 실행하는 셀렉트 선택의 결과 메서드들

	
	//이벤트를 종료하고 다음 이벤트를 실행한다.
	public Map<String,Object> goNextEvent() throws Exception {
		currentEvent.hasDone();
		currentEvent = currentScene.getEvent();
		return currentEvent.happened();
	}
	
	//레벨업한다(테스트용)
	public void playerLevelUp() throws Exception {
		//player.levelUp();
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("LEVEL", String.valueOf(player.getLevel()+1));
		List<Map<String,String>> lvlDataList = loader.getPChacterInfo(paramMap);
		Map<String,String> lvlData = lvlDataList.get(0);
		player.setLvlStatus(lvlData);
	}
	
	//특정 이벤트 시퀀스로 이동한다.
	public Map<String,Object> goSpecificEvent(String eventCode, String eventSeq) throws Exception {
		currentEvent = currentScene.getEvent(eventCode, eventSeq);
		return currentEvent.happened();
	}
	
	//전투를 수행한다
	public Map<String,Object> playBattle(String command) throws Exception {
		Map<String,Object> resultMap = pBtl.play(command);
		return resultMap;
	}
	
	//전투 중 선택지를 취소하고 이전 상태로 돌아간다.
	public Map<String,Object> cancelBattleSelect() throws Exception{
		Map<String,Object> resultMap = pBtl.cancelSelect();
		return resultMap;
	}
	
	//전투에 돌입한다.
	public Map<String,Object> goBattle() throws Exception{
		Map<String,Object> resultMap = pBtl.goBattle();
		return resultMap;
	}
	
	//전투를 중단하고 특정 이벤트 시퀀스로 이동한다.
	public Map<String,Object> awayBattle(String eventCode, String eventSeq) throws Exception {
		pBtl.takeActionToEachother("runAway");
		pBtl.initBattle();
		return goSpecificEvent(eventCode,eventSeq);
	}
	
	//도움요청=대타출동
	public Map<String,Object> callForHelp(String characterCode) throws Exception{
		return pBtl.callForHelp(characterCode);
	}
	
	//도움받기=체력/정신력 회복
	public Map<String,Object> getHelp(String characterCode,String ability) throws Exception{
		return pBtl.getHelp(characterCode,ability);
	}
	
	//아이템 사용
	public Map<String,Object> useItem(String item) throws Exception{
		Map<String,Object> resultMap = new HashMap<String, Object>();
		resultMap.putAll(pBtl.getBattle());
		resultMap.putAll(player.useItem(item));
		return resultMap;
	}
}
