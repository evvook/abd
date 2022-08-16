package abd.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	
	public GameManager() {
		eventContext = new HashMap<String, Object>();
		sceneInfoList = new LinkedList<Map<String,String>>();
		lvlData = new HashMap<String, Map<String,String>>();
		eventScripts = new ArrayList<String>();
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
		currentScene = new GameScene(sceneInfoList.pollFirst(),loader,this);
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
	
	private void setScript(Map<String,Object> resultMap) {
		String script = (String)resultMap.remove("script");
		if(script != null) {
			setScript(script);
		}
	}
	
	private void setScript(String script) {
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
			Map<String,String> sceneInfo = sceneInfoList.pollFirst();
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
				
				if(dayCnt == 7) {
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
			
			Map<String,Object> resultContext = currentEvent.happened(input);
			eventContext.putAll(resultContext);
			
			//현재 이벤트에서 처리가 한 번 되고나서 후처리
			//플레이가 아니면
			if(!"P".equals(currentEvent.getEventType())) {

				//직전 이벤트가 플레이고 셀렉트일때
				if("select".equals(eventContext.get("play"))) {
					//Map<String,String> rSelect = (Map<String,String>)eventContext.get("select");
					if("afterSelect".equals(eventContext.get("status"))) {
						//플레이 정보는 삭제하고
						eventContext.remove("play");
						eventContext.remove("status");
						//스크립트 담아주고
						setScript(eventContext);
						goEvent();
						
					}
				//스크립트가 있다면 담아주고 이벤트 호출
				}else if(eventContext.get("script") != null) {
					setScript(eventContext);
					goEvent();
				}
				
			}//플레이 타입이 아닌 경우 이벤트 연쇄 발생
			//플레이 타입이면
			//이벤트 연쇄(재귀)인 경우 몇몇 케이스를 제외하고 이벤트 연쇄(재귀)를 멈춘다
			else if("P".equals(currentEvent.getEventType())) {
				
				//케이스 #0(이벤트 연쇄 시작지점인 경우)
				//전투 종료이면 종료 이벤트로 이동한다.
				if("endBattle".equals(eventContext.get("play"))) {
					@SuppressWarnings("unchecked")
					Map<String,Object> battleContext = (Map<String,Object>)eventContext.get("battle");
					if(battleContext.get("script") != null) {
						setScript(battleContext);
					}
//					Map<String,String> battleNextEventInfo = pBtl.getNextEventInfo();
//					eventContext = goSpecificEvent(battleNextEventInfo.get("EVENT_CD"), battleNextEventInfo.get("EVENT_SEQ"));
					GameEvPlay evPlay = (GameEvPlay)currentEvent;
					Map<String,String> battleNextEventInfo = evPlay.getBattlesNextEventInfo();
					currentEvent.hasDone();
					currentEvent = currentScene.getEvent(battleNextEventInfo.get("EVENT_CD"), battleNextEventInfo.get("EVENT_SEQ"));
					eventContext = currentEvent.happened();
					
					setScript(eventContext);
					goEvent();
				}else {
					//케이스 #1
					//인풋이고 에프터인풋이면 리절트 텍스트 담아주고 이벤트 호출
					if("input".equals(eventContext.get("play"))) {
						@SuppressWarnings("unchecked")
						Map<String,String> rInput = (Map<String,String>)eventContext.get("input");
						if("afterInput".equals(rInput.get("status"))) {
							eventContext.remove("input");
							setScript(rInput.get("resultTxt"));
							goEvent();
							
						}
					}
					//케이스 #2
					//셀렉트이고 애프터셀렉트 이면 스크립트 담아주고 플레이 정보 제거
					else if("select".equals(eventContext.get("play"))) {
						if("afterSelect".equals(eventContext.get("status"))) {
							eventContext.remove("play");
							eventContext.remove("status");
							setScript(eventContext);
							goEvent();
							
						}
					}
					//케이스 #3
					//스크립트가 있다면 담아주고 이벤트 호출	
					else if(eventContext.get("script") != null){
						setScript(eventContext);
						goEvent();
					}
					
					//전투 중 발생한 스크립트 담아줌. 이벤트 호출하지 않음
					if("battle".equals(eventContext.get("play"))) {
						@SuppressWarnings("unchecked")
						Map<String,Object> battleContext = (Map<String,Object>)eventContext.get("battle");
						if(battleContext.get("script") != null) {
							setScript(battleContext);
						}
					}
					
					//스크립트 담아주고 이벤트 더이상 호출하지 않음
					List<String> scripts = new ArrayList<String>();
					scripts.addAll(eventScripts);
					eventContext.put("scripts", scripts);
					
				}//플레이 타입에서 이벤트 연쇄를 진행하거나 끊어냄
			}//플레이 타입이 아니거나 플레이 타입임
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
}
