package abd.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.core.script.Script;

import abd.game.character.GameCharacterBuilder;
import abd.game.character.PCharacter;
import abd.game.scene.GameEvPlay;
import abd.game.scene.GameEvent;
import abd.game.scene.GameScene;

public class GameManager implements GameInterface{
	private PCharacter player;
	private Map<String,Map<String,String>> lvlData;
	//씬
	private LinkedList<Map<String,String>> sceneInfoList;
	private LinkedList<Map<String,String>> pastSceneInfoList;
	private GameScene currentScene;
	//이벤트
	private GameEvent currentEvent;
	private Map<String,Object> eventContext;
	private List<String> eventScripts;
	
	//데이터 로드를 위한 객체(서비스)
	private GameDataLoader loader;
	
	//테스트용
	int dayCnt=1;
	
	boolean dayOut = false;
	boolean eventCut = false;
	
	GameStatus rpgBattle;
	GameStatus visualNovel;
	GameStatus currentStatus;
	
	public GameManager() {
		eventContext = new HashMap<String, Object>();
		sceneInfoList = new LinkedList<Map<String,String>>();
		pastSceneInfoList = new LinkedList<Map<String,String>>();
		
		lvlData = new HashMap<String, Map<String,String>>();
		eventScripts = new ArrayList<String>();
		
		rpgBattle = new GameStatusRpgBattle(this);
		visualNovel = new GameStatusVisualNovel(this);
		currentStatus = visualNovel;
	}
	
	@Override
	public void createPlayerCharacter(GameSetupLoader loader, String name) throws Exception {
		// TODO Auto-generated method stub
		//this.loader = loader;
		
		Map<String,String> paramMap = new HashMap<String, String>();
		//paramMap.put("LEVEL", "1");
		List<Map<String,String>> lvlDataList = loader.getPCharacterLevelInfo(paramMap);
		for(Map<String,String>lvlDataInfo:lvlDataList) {
			lvlData.put(lvlDataInfo.get("LEVEL"), lvlDataInfo);
		}
		Map<String,String> characterInfo = lvlData.get("1");
		
		player = GameCharacterBuilder.getPCharacterInstance(characterInfo, name);
		player.createCompanyCharacters(loader);
		player.setGameManager(this);
	}
	
	@Override
	public void setupScenesInfo(GameSetupLoader loader) throws Exception {
		// TODO Auto-generated method stub
		List<Map<String,String>> sceneInfos = loader.getGameSceneInfo();
		sceneInfoList.addAll(sceneInfos);
	}
	
	@Override
	public void startSceneLoad(GameDataLoader loader) throws Exception {
		// TODO Auto-generated method stub
		this.loader = loader;
		Map<String,String> sceneInfo = sceneInfoList.pollFirst();
		pastSceneInfoList.add(sceneInfo);
		currentScene = new GameScene(sceneInfo,loader,this);
		currentEvent = currentScene.getEvent();
	}

	@Override
	public Map<String, Object> getGameContext() {
		// TODO Auto-generated method stub
		Map<String, Object> context = new HashMap<String, Object>();
		Map<String, Object> pCharContext = player.getCharacterContext();
		if(currentEvent == null || !"B".equals(currentEvent.getchildType())){
			//이벤트가 존재하지 않거나 이벤트트의 하위타입이 전투가 아니면 플레이어 정보를 넣어준다.
			context.put("player", pCharContext);
		}
		context.putAll(eventContext);

		context.put("day", dayCnt);
		if(dayOut) {
			dayCnt++;
			dayOut = false;
		}
		
		eventContext.clear();
		eventScripts.clear();
		
		return context;
	}
	
	public void setScript(Map<String,Object> resultMap) {
		String script = (String)resultMap.remove("script");
		if(script != null) {
			setScript(script);
		}
	}
	
	public void setScript(String script) {
		String[] scripts = script.split("<br>");
		for(String s:scripts) {
			eventScripts.add(s);
		}
	}
	
	@Override
	public void goEvent() throws Exception {
		//파라미터 없는 goEvent는 이벤트 시작을 의미
		goEvent(null);
	}
	
	@Override
	public void goEvent(Map<String,Object> input) throws Exception {
		
		//씬이 끝난 경우
		if(currentScene.isDone()) {
			//다음 씬으로 이동
			String beforeSceneCode = currentScene.getSceneCode();
			Map<String,String> sceneInfo = sceneInfoList.pollFirst();
			pastSceneInfoList.add(sceneInfo);
			if(sceneInfo != null) {
				currentScene = new GameScene(sceneInfo,loader,this);
				currentEvent = currentScene.getEvent();
				
				//담아줌
				List<String> scripts = new ArrayList<String>();
				scripts.addAll(eventScripts);
				eventContext.put("scripts", scripts);
				
				//뭔가 클리어를 해줌
				eventContext.remove("select");
				eventContext.remove("input");
				eventContext.remove("battle");
				eventContext.remove("play");
				
				if(dayCnt == 7 || "S003".equals(beforeSceneCode)) {//7일째 이거나 인트로이면
					eventContext.put("sceneInfo", "intro");
				}
				eventContext.put("sceneStatus", "end");
			}else {
				//더이상 씬이 없는 경우
				//엔딩?
			}
		}
		//이벤트 연쇄를 끝냄
		else if(eventCut){
			eventContext.put("sceneStatus", "end");
			eventCut = false;
			//담아줌
			List<String> scripts = new ArrayList<String>();
			scripts.addAll(eventScripts);
			eventContext.put("scripts", scripts);
			
			if(dayCnt == 6) {
				//특정 조건이면 가로채서 다음씬을 시작시킴
				currentScene.hasDone();
				goEvent();
			}
		}
		//씬의 진행
		else {
			
			if(currentEvent.isDone()) {
				currentEvent = currentScene.getNextEvent();
			}
			//상태패턴 적용
			currentStatus.go(input);
		}//씬이 종료되거나 진행됨
	}

	public PCharacter getPlayer() {
		// TODO Auto-generated method stub
		return player;
	}
	
	@Override
	public Map<String, Object> getCharStat() {
		// TODO Auto-generated method stub
		Map<String,Object> charaterStatus = new HashMap<String, Object>();
		charaterStatus.put("charaterStatus", player.getCompContext());
		return charaterStatus;
	}
	
	
	public void currentEventDone() {
		// TODO Auto-generated method stub
		currentEvent.hasDone();
	}

	public void setNextEvent() throws Exception {
		// TODO Auto-generated method stub
		currentEvent = currentScene.getNextEvent();
	}
	
	public void setEvent(String eventCode, String eventSeq) throws Exception {
		// TODO Auto-generated method stub
		currentEvent = currentScene.getEvent(eventCode, eventSeq);
	}
	
	public Map<String, Object> currnetEventHappened() throws Exception {
		// TODO Auto-generated method stub
		return currentEvent.happened();
	}

	public Map<String, Object> currnetEventHappened(Map<String, Object> paramMap) throws Exception {
		// TODO Auto-generated method stub
		return currentEvent.happened(paramMap);
	}
	

	public void setDayOut() {
		// TODO Auto-generated method stub
		dayOut = true;
	}	
	
	public void setEventCut() {
		// TODO Auto-generated method stub
		eventCut = true;
	}
	
	public void playerLevelUp() throws Exception {
		Map<String,String> lvlDataInfo = lvlData.get(String.valueOf(player.getLevel()+1));
		player.setLvlStatus(lvlDataInfo);
	}

	//게임 스태이터스에서 사용하는 메서드들
	public Object getCurrentEventChildType() {
		// TODO Auto-generated method stub
		return currentEvent.getchildType();
	}

	public void switchCurrentStatus() {
		// TODO Auto-generated method stub
		if(currentStatus.equals(rpgBattle)) {
			currentStatus = visualNovel;
		}else if(currentStatus.equals(visualNovel)) {
			currentStatus = rpgBattle;
		}
	}

	public void playCurrentStatus(Map<String, Object> input) throws Exception {
		// TODO Auto-generated method stub
		currentStatus.go(input);
	}	
	
	public void setEventContext(Map<String,Object> resultContext) {
		eventContext.putAll(resultContext);
	}

	public Object getCurrentEventType() {
		// TODO Auto-generated method stub
		return currentEvent.getEventType();
	}

	public List<String> getEventScripts() {
		// TODO Auto-generated method stub
		return eventScripts;
	}

	public Object getPlayInEventContext() {
		// TODO Auto-generated method stub
		return eventContext.get("play");
	}
	
	public Object getBattleInEventContext() {
		// TODO Auto-generated method stub
		return eventContext.get("battle");
	}
	
	public Object getStatusInEventContext() {
		// TODO Auto-generated method stub
		return eventContext.get("status");
	}
	public Object getScriptInEventContext() {
		// TODO Auto-generated method stub
		return eventContext.get("script");
	}

	public Object getInputInEventContext() {
		// TODO Auto-generated method stub
		return eventContext.get("input");
	}	

	public void putEventContext(String key, Object context) {
		// TODO Auto-generated method stub
		eventContext.put(key, context);
	}

	public void setScriptEventContext() {
		// TODO Auto-generated method stub
		setScript(eventContext);
	}

	public void removeEventContext(List<String> removeKeys) {
		// TODO Auto-generated method stub
		for(String key:removeKeys) {
			eventContext.remove(key);
		}
	}

	public void goBattleNextEvent() throws Exception {
		// TODO Auto-generated method stub
		GameEvPlay evPlay = (GameEvPlay)currentEvent;
		Map<String,String> battleNextEventInfo = evPlay.getBattlesNextEventInfo();
		currentEvent.hasDone();
		currentEvent = currentScene.getEvent(battleNextEventInfo.get("EVENT_CD"), battleNextEventInfo.get("EVENT_SEQ"));
		eventContext = currentEvent.happened();
	}

	public void goBackIntro() throws Exception {
		// TODO Auto-generated method stub
		eventScripts.clear();
		eventContext.clear();
		
		LinkedList<Map<String,String>>pastSceneInfoListCopy = new LinkedList<Map<String,String>>();
		pastSceneInfoListCopy.addAll(pastSceneInfoList);
		
		for(Map<String,String> sceneInfo :pastSceneInfoList) {
			if("S003".equals(sceneInfo.get("SCENE_CD"))) {//인트로 씬을 찾아 시작 씬으로 삼는다.
				sceneInfoList.addAll(0, pastSceneInfoListCopy);
				startSceneLoad(loader);
				break;
			}else {
				pastSceneInfoListCopy.pollFirst();
			}
		}
	}
	
	public void goToIntro() throws Exception {
		// TODO Auto-generated method stub
		
		LinkedList<Map<String,String>>sceneInfoListCopy = new LinkedList<Map<String,String>>();
		sceneInfoListCopy.addAll(sceneInfoList);
		
		for(Map<String,String> sceneInfo :sceneInfoListCopy) {
			if("S003".equals(sceneInfo.get("SCENE_CD"))) {//인트로 씬을 찾아 시작 씬으로 삼는다.
				startSceneLoad(loader);
				break;
			}else {
				pastSceneInfoList.add(sceneInfoList.pollFirst());
			}
		}
	}
}
