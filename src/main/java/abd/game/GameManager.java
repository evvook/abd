package abd.game;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import abd.game.character.CompCharacter;
import abd.game.character.GameCharacterBuilder;
import abd.game.character.PCharacter;
import abd.game.scene.GameEvent;
import abd.game.scene.GamePlayBattle;
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
	
	//데이터 로드를 위한 객체(서비스)
	private GameDataLoader loader;
	
	//전투 상황을 통제하기 위한 배틀 객체
	private GamePlayBattle pBtl;
	
	//테스트용
	int dayCnt=1;
	
	public GameManager() {
		eventContext = new HashMap<String, Object>();
		sceneInfoList = new LinkedList<Map<String,String>>();
		lvlData = new HashMap<String, Map<String,String>>();
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
		Map<String, String> pCharContext = player.getCharacterContext();
		if(currentEvent == null || !"B".equals(currentEvent.getchildType())){
			//이벤트가 존재하지 않거나 이벤트트의 하위타입이 전투가 아니면 플레이어 정보를 넣어준다.
			context.put("player", pCharContext);
		}
		context.putAll(eventContext);
		
		return context;
	}
	
	@Override
	public void goEvent() throws Exception {
		goEvent(null);
	}
	
	@Override
	public void goEvent(Map<String,Object> input) throws Exception {
		if(input == null) {
			if(currentEvent.isDone()) {
				currentEvent = currentScene.getNextEvent();
				eventContext = currentEvent.happened();
			}else {
				eventContext = currentEvent.happened();
			}
		}else {
			if(currentEvent.isDone()) {
				currentEvent = currentScene.getNextEvent();
				eventContext = currentEvent.happened(input);
			}else {
				eventContext = currentEvent.happened(input);
			}
		}
		//전투 종료이면
		if("endBattle".equals(eventContext.get("play"))) {
			Map<String,String> battleNextEventInfo = pBtl.getNextEventInfo();
			eventContext = goSpecificEvent(battleNextEventInfo.get("EVENT_CD"), battleNextEventInfo.get("EVENT_SEQ"));
		}
		
		if(currentScene.isDone()) {
			//다음 씬으로 이동
			Map<String,String> sceneInfo = sceneInfoList.pollFirst();
			if(sceneInfo != null) {
				currentScene = new GameScene(sceneInfo,loader,this);
				currentEvent = currentScene.getEvent();
				pBtl = null;
			}else {
				//더이상 씬이 없는 경우
				//엔딩?
			}
		}
	}

	public PCharacter getPlayer() {
		// TODO Auto-generated method stub
		return player;
	}
	
	public void setBattle(GamePlayBattle pBtl) {
		// TODO Auto-generated method stub
		this.pBtl = pBtl;
	}
	
	@Override
	public Map<String, Object> getCharStat() {
		// TODO Auto-generated method stub
		Map<String,Object> charaterStatus = new HashMap<String, Object>();
		charaterStatus.put("charaterStatus", player.getCompContext());
		return charaterStatus;
	}
	
	///////////////////////////////////////////////////////////
	//이하, 리플렉션으로 실행하는 셀렉트 선택의 결과 메서드들

	
	//이벤트를 종료하고 다음 이벤트를 실행한다.
	public Map<String,Object> goNextEvent() throws Exception {
		currentEvent.hasDone();
		currentEvent = currentScene.getNextEvent();
		return currentEvent.happened();
	}
	
	//레벨업한다(테스트용)
	public void playerLevelUp() throws Exception {
		Map<String,String> lvlDataInfo = lvlData.get(String.valueOf(player.getLevel()+1));
		player.setLvlStatus(lvlDataInfo);
	}
	
	//특정 이벤트 시퀀스로 이동한다.
	public Map<String,Object> goSpecificEvent(String eventCode, String eventSeq) throws Exception {
		currentEvent = currentScene.getEvent(eventCode, eventSeq);
		return currentEvent.happened();
	}
	
	//특정 이벤트 시퀀스를 셋팅한다.
	public void setSpecificEvent(String eventCode, String eventSeq) throws Exception {
		currentEvent = currentScene.getEvent(eventCode, eventSeq);
	}
	
	/********************************전투선택설정****************************************/
	//전투 => 전투실행(일반전투선택)
	//대타출동 => 대타요청선택
	//후퇴 => 재정비선택
	//재정비도움받기 => 도움받기선택
	public Map<String,Object> playBattle(String command) throws Exception {
		Map<String,Object> resultMap = pBtl.play(command);
		return resultMap;
	}
	/********************************전투선택설정****************************************/

	
	/********************************도움요청선택****************************************/
	//도움요청=대타출동
	public Map<String,Object> callForHelp(String characterCode) throws Exception{
		return pBtl.callForHelp(characterCode);
	}
	/********************************도움요청선택****************************************/
	
	/********************************도움 공통****************************************/
	//전투 중 선택지를 취소하고 이전 상태로 돌아간다.
	public Map<String,Object> cancelBattleSelect() throws Exception{
		Map<String,Object> resultMap = pBtl.cancelSelect();
		return resultMap;
	}
	/********************************도움 공통****************************************/
	
	/********************************재정비선택****************************************/
	//아이템 사용
	public Map<String,Object> useItem(String item) throws Exception{
		Map<String,Object> resultMap = new HashMap<String, Object>();
		resultMap.putAll(pBtl.getBattle());
		resultMap.putAll(player.useItem(item));
		return resultMap;
	}
	
	//도움받기=체력/정신력 회복
	public Map<String,Object> getHelp(String characterCode,String ability) throws Exception{
		return pBtl.getHelp(characterCode,ability);
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
	/********************************재정비선택****************************************/
	
	public Map<String,Object> dayEnd(){
		Map<String,Object> resultMap = new HashMap<String, Object>();
		player.decreaseMp(player.getCurrentMp()-10);
		//resultMap.putAll(player.getCharacterContext());
		
		dayCnt++;
		return resultMap;
	}
	
	public Map<String,Object> dayEnd(String eventCode, String eventSeq) throws Exception{
		Map<String,Object> resultMap = new HashMap<String, Object>();
		player.decreaseMp(player.getCurrentMp()-10);
		//resultMap.putAll(player.getCharacterContext());
		
		dayCnt++;
		if(dayCnt >= 4) {
			//특정 조건이면 가로채서 다음씬을 시작시킴
			currentScene.hasDone();
		}
		
		//특정 이벤트 설정
		currentEvent = currentScene.getEvent(eventCode, eventSeq);
		return resultMap;
	}
	
	public void meet(String eventCode,String eventSeq, String characterCode, String reliabl) throws Exception {
		meet(eventCode, eventSeq, characterCode, reliabl, null);
	}
	
	public void meet(String eventCode,String eventSeq, String characterCode, String reliabl, String mp) throws Exception {
		String[] compCodes = characterCode.split(",");
		for(String compCode:compCodes) {
			CompCharacter comp = player.getCompany(compCode);
			comp.increaseReliabl(Integer.valueOf(reliabl));
			if(mp != null) {
				player.increaseMp(Integer.valueOf(mp));
			}
		}
		//특정 이벤트 설정
		currentEvent = currentScene.getEvent(eventCode, eventSeq);
	}
	
	public void mentalCare(String eventCode, String eventSeq) throws Exception {
		//특정 이벤트 설정
		player.increaseMp(10);
		currentEvent = currentScene.getEvent(eventCode, eventSeq);
	}
	
	public Map<String,Object> setJob(String job, String resultTxt){
		Map<String,Object> resultMap = new HashMap<String, Object>();
		player.setJob(job);
		resultMap.put("resultTxt",resultTxt.replace("%job%", job));
		return resultMap;
		
	}
	
	public void setJob(String job){
		player.setJob(job);
	}
	
	public void setComp(String characterCode) {
		player.setCompany(characterCode);
	}
	
	public void supplyItems(String eventCode, String eventSeq) throws Exception {
		setSpecificEvent(eventCode, eventSeq);
	}
	
	public void getOnElve(String eventCode, String eventSeq, String characterCode, String mp) throws Exception{
		//박무현, 유금이 동료로 합류
		player.restoreCompany(characterCode);
		
		//정신력 감소
		String[] compCodes = characterCode.split(",");
		for(String compCode:compCodes) {
			CompCharacter comp = player.getCompany(compCode);
			if(mp != null) {
				comp.decreaseMp(Integer.valueOf(mp));
			}
		}
		player.decreaseMp(Integer.valueOf(mp));
		
		//이벤트 세팅
		setSpecificEvent(eventCode, eventSeq);
	}
	
	public Map<String,Object> checkBattle(String eventCodeY, String eventSeqY, String eventCodeN, String eventSeqN) throws Exception {
		Map<String,Object> resultMap = new HashMap<String, Object>();
		if(pBtl != null && pBtl.isDone()) {
			resultMap.putAll(goSpecificEvent(eventCodeY, eventSeqY));
		}else {
			resultMap.putAll(goSpecificEvent(eventCodeN, eventSeqN));
		}
		return resultMap;
	}
}
