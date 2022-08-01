package abd.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import abd.game.character.CompCharacter;
import abd.game.character.GameCharacterBuilder;
import abd.game.character.PCharacter;
import abd.game.character.item.Candy;
import abd.game.character.item.ChocoBar;
import abd.game.character.item.NoFreeSpaceException;
import abd.game.scene.GameEvBranch;
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
	private List<String> eventScripts;
	
	//데이터 로드를 위한 객체(서비스)
	private GameDataLoader loader;
	
	//전투 상황을 통제하기 위한 배틀 객체
	private GamePlayBattle pBtl;
	
	//테스트용
	int dayCnt=1;
	boolean dayOut = false;
	
	boolean sceneEnd = false;
	private GameEvBranch evBranch;
	
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
			Map<String,String> sceneInfo = sceneInfoList.pollFirst();
			if(sceneInfo != null) {
				currentScene = new GameScene(sceneInfo,loader,this);
				currentEvent = currentScene.getEvent();
				pBtl = null;
				
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
		//씬이 끝난 것으로 가정하는 경우(강제로 이벤트 연쇄를 끊어낼 때)
		else if(sceneEnd){
			eventContext.put("sceneStatus", "end");
			sceneEnd = false;
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
					Map<String,String> battleNextEventInfo = pBtl.getNextEventInfo();
					eventContext = goSpecificEvent(battleNextEventInfo.get("EVENT_CD"), battleNextEventInfo.get("EVENT_SEQ"));
					
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
	
	public void setBattle(GamePlayBattle pBtl) {
		// TODO Auto-generated method stub
		this.pBtl = pBtl;
	}
	
	public void setBranch(GameEvBranch evBranch) {
		// TODO Auto-generated method stub
		this.evBranch = evBranch;
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
		currentEvent.hasDone();
		currentEvent = currentScene.getEvent(eventCode, eventSeq);
		return currentEvent.happened();
	}
	
	//특정 이벤트 시퀀스로 이동한다.
	public Map<String,Object> goSpecificEvent(String eventCode, String eventSeq, String param) throws Exception {
		currentEvent.hasDone();
		currentEvent = currentScene.getEvent(eventCode, eventSeq);
		Map<String,Object> paramMap = new HashMap<String, Object>();
		paramMap.put("param", param);
		Map<String,Object> resultMap = currentEvent.happened(paramMap);
		return resultMap;
	}
	
	//특정 이벤트 시퀀스를 셋팅한다.
	public void setSpecificEvent(String eventCode, String eventSeq) throws Exception {
		currentEvent.hasDone();
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
	//사용할 아이템이 있는지 체크
	public Map<String,Object> useItemCheckBattle() throws Exception{
		Map<String,Object> resultMap = new HashMap<String, Object>();
		//플레이어가 아이템이 하나라도 있는지 체크
		//아무 아이템이라도 있다면
		if(player.hasAnyItem()) {
			//아이템 선택지를 가져온다
			Map<String,Object> itemSelectContext = pBtl.getItemSelectContext();
			resultMap.putAll(itemSelectContext);
		}else {
			//없다면
			Map<String,Object> noItemContext = pBtl.getNoItemContext();
			resultMap.putAll(noItemContext);
		}
		return resultMap;
	}
	//아이템 사용
	public Map<String,Object> useItemBattle(String item) throws Exception{
		Map<String,Object> resultMap = new HashMap<String, Object>();
		resultMap.putAll(pBtl.useItem(item));
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
		player.decreaseMp(10);
		
		dayOut = true;
		
		return resultMap;
	}
	
	public Map<String,Object> dayEnd(String eventCode, String eventSeq) throws Exception{
		Map<String,Object> resultMap = new HashMap<String, Object>();
		player.decreaseMp(10);
		
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
		
		dayOut = true;
		sceneEnd = true;
		
		//특정 이벤트 설정
		currentEvent = currentScene.getEvent(eventCode, eventSeq);
	}
	
	public void mentalCare(String eventCode, String eventSeq) throws Exception {
		//특정 이벤트 설정
		player.increaseMp(25);
		
		dayOut = true;
		sceneEnd = true;
		
		currentEvent = currentScene.getEvent(eventCode, eventSeq);
	}
	
	public Map<String,Object> setJob(String job, String resultTxt){
		Map<String,Object> resultMap = new HashMap<String, Object>();
		setJob(job);
		resultMap.put("resultTxt",resultTxt.replace("%job%", job));
		return resultMap;
		
	}
	
	public void setJob(String job){
		player.setJob(job);
	}
	
	public Map<String,Object> sayHello(String hello, String resultTxt){
		Map<String,Object> resultMap = new HashMap<String, Object>();
		resultTxt = resultTxt.replace("%name%", player.getName());
		resultTxt = resultTxt.replace("%hello%", hello);
		resultMap.put("resultTxt",resultTxt);
		return resultMap;
		
	}
	
	public void setComp(String characterCode) {
		player.setCompany(characterCode);
	}
	
	public Map<String,Object> checkSupplyB4() throws Exception {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		//하나도 없는 경우
		if(player.getItems().isEmpty()) {
			//캐릭터에 아이템을 셋팅한다
			//supplyItemsB4();
			Map<String,Object> eventResult = evBranch.goYEvent();
			String script = (String)eventResult.get("script");
			eventResult.put("script",script);
			resultMap.putAll(eventResult);
		}
		//있는 경우
		else {
			Map<String,Object> eventResult = evBranch.goNEvent();
			String script = (String)eventResult.get("script");
			eventResult.put("script",script);
			resultMap.putAll(eventResult);
		}
		evBranch.setOutEvent();
		return resultMap;
	}
	
	public Map<String,Object> supplyItemsB4() throws Exception {
		try {
			player.setItem(Candy.class,5);
			player.setItem(ChocoBar.class,3);
			return null;
		}catch(NoFreeSpaceException e) {
			Map<String,Object> resultMap = new HashMap<>();
			resultMap.put("script", e.toString());
			return resultMap;
		}
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
	
	public Map<String,Object> checkBattleEnd(String eventCodeY, String eventSeqY, String eventCodeN, String eventSeqN) throws Exception {
		Map<String,Object> resultMap = new HashMap<String, Object>();
		if(pBtl != null && pBtl.isDone()) {
			resultMap.putAll(goSpecificEvent(eventCodeY, eventSeqY));
		}else {
			resultMap.putAll(goSpecificEvent(eventCodeN, eventSeqN));
		}
		return resultMap;
	}
	
	public Map<String,Object> checkBattleEnd() throws Exception {
		Map<String,Object> resultMap = new HashMap<String, Object>();
		if(pBtl != null && pBtl.isDone()) {
			resultMap.putAll(evBranch.goYEvent());
		}else {
			resultMap.putAll(evBranch.goNEvent());
		}
		return resultMap;
	}
	
	public Map<String,Object> checkSinArmed(String eventCodeY, String eventSeqY, String eventCodeN, String eventSeqN, String outEventCode, String outEventSeq, String characterCode) throws Exception{
		Map<String,Object> resultMap = new HashMap<String, Object>();
		CompCharacter sin = player.getCompany("C05");
		//신해량이 있는 경우
		//신해량이 무장을 하지 않았고, 캐릭터코드가 신해량이 아닌경우
		if(sin != null && !sin.isArmed() && !characterCode.equals(sin.getCode())) {
			Map<String,Object> eventResult = goSpecificEvent(eventCodeY, eventSeqY,sin.getCode());
			String script = (String)eventResult.get("script");
			script = script.replace("%compName%", sin.getName());
			eventResult.put("script",script);
			resultMap.putAll(eventResult);
		}else {
			//신해량이 무장을 이미 했거나, 캐릭터코드가 신해량인 경우
			Map<String,Object> eventResult = goSpecificEvent(eventCodeN, eventSeqN,characterCode);
			String script = (String)eventResult.get("script");
			if("ME".equals(characterCode)) {
				script = script.replace("%compName%", player.getName());
			}else {
				script = script.replace("%compName%", player.getCompany(characterCode).getName());
			}
			eventResult.put("script",script);
			resultMap.putAll(eventResult);
		}
		setSpecificEvent(outEventCode, outEventSeq);
		return resultMap;
	}
	
	public Map<String,Object> checkSinArmed(String characterCode) throws Exception{
		Map<String,Object> resultMap = new HashMap<String, Object>();
		CompCharacter sin = player.getCompany("C05");
		//신해량이 있는 경우
		//신해량이 무장을 하지 않았고, 캐릭터코드가 신해량이 아닌경우
		if(sin != null && !sin.isArmed() && !characterCode.equals(sin.getCode())) {
			Map<String,Object> eventResult = evBranch.goYEvent(sin.getCode());
			String script = (String)eventResult.get("script");
			script = script.replace("%compName%", sin.getName());
			eventResult.put("script",script);
			resultMap.putAll(eventResult);
		}else {
			//신해량이 무장을 이미 했거나, 캐릭터코드가 신해량인 경우
			Map<String,Object> eventResult = evBranch.goNEvent(characterCode);
			String script = (String)eventResult.get("script");
			if("ME".equals(characterCode)) {
				script = script.replace("%compName%", player.getName());
			}else {
				script = script.replace("%compName%", player.getCompany(characterCode).getName());
			}
			eventResult.put("script",script);
			resultMap.putAll(eventResult);
		}
		evBranch.setOutEvent();
		return resultMap;
	}
	
	public void takeGun(String characterCode) {
		if("ME".equals(characterCode)) {
			player.setArmed();
		}else {
			CompCharacter comp = player.getCompany(characterCode);
			comp.setArmed();
		}
	}
	
	public void giveGunTo(String characterCode) throws Exception{
		if("ME".equals(characterCode)) {
			takeGun(characterCode);
		}else {
			CompCharacter comp = player.getCompany(characterCode);
			comp.setArmed();
			comp.increaseReliabl(10);
		}
	}
}
