package abd.game.scene;

import java.util.HashMap;
import java.util.Map;

import abd.game.GameManager;
import abd.game.character.CompCharacter;
import abd.game.character.PCharacter;
import abd.game.character.item.Candy;
import abd.game.character.item.ChocoBar;
import abd.game.character.item.NoFreeSpaceException;

public class GameEventCallback {
	PCharacter player;
	
	GameManager manager;
	
	GamePlayBattle pBtl;
	GameEvBranch evBranch;
	
	public GameEventCallback(GameManager manager) {
		this.manager = manager;
		this.player = manager.getPlayer();
	}
	
	public void setPlayBattle(GamePlayBattle pBtl) {
		this.pBtl = pBtl;
	}
	
	public void setEventBranch(GameEvBranch evBranch) {
		this.evBranch = evBranch;
	}
	
	//이하, 리플렉션으로 실행하는 셀렉트 선택의 결과 메서드들
	
	//이벤트를 종료하고 다음 이벤트를 실행한다.
	public Map<String,Object> goNextEvent() throws Exception {
		manager.currentEventDone();
		manager.setNextEvent();
		return manager.currnetEventHappened();
	}
	
	//특정 이벤트 시퀀스로 이동한다.
	public Map<String,Object> goSpecificEvent(String eventCode, String eventSeq) throws Exception {
		manager.currentEventDone();
		manager.setEvent(eventCode, eventSeq);
		return manager.currnetEventHappened();
	}
	
	//특정 이벤트 시퀀스로 이동한다.
	public Map<String,Object> goSpecificEvent(String eventCode, String eventSeq, String param) throws Exception {
		manager.currentEventDone();
		manager.setEvent(eventCode, eventSeq);
		Map<String,Object> paramMap = new HashMap<String, Object>();
		paramMap.put("param", param);
		Map<String,Object> resultMap = manager.currnetEventHappened(paramMap);
		return resultMap;
	}
	
	//특정 이벤트 시퀀스를 셋팅한다.
	public void setSpecificEvent(String eventCode, String eventSeq) throws Exception {
		manager.currentEventDone();
		manager.setEvent(eventCode, eventSeq);
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
		
		manager.setDayOut();
		
		return resultMap;
	}
	
	public Map<String,Object> dayEnd(String eventCode, String eventSeq) throws Exception{
		Map<String,Object> resultMap = new HashMap<String, Object>();
		player.decreaseMp(10);
		
		//특정 이벤트 설정
		manager.setEvent(eventCode, eventSeq);
		
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
		manager.setDayOut();
		
		//특정 이벤트 설정
		manager.setEvent(eventCode, eventSeq);
	}
	
	public void mentalCare(String eventCode, String eventSeq) throws Exception {
		//특정 이벤트 설정
		player.increaseMp(25);
		manager.setDayOut();
		
		manager.setEvent(eventCode, eventSeq);
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
	
	public Map<String,Object> checkBattleEnd() throws Exception {
		Map<String,Object> resultMap = new HashMap<String, Object>();
		if(pBtl != null && pBtl.isDone()) {
			resultMap.putAll(evBranch.goYEvent());
		}else {
			resultMap.putAll(evBranch.goNEvent());
		}
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
	
	public void cut() {
		manager.setEventCut();
	}
	
	public Map<String,Object> mpOfbattleResult(String resultTxt){
		Map<String,Object> resultMap = new HashMap<String, Object>();
		Integer iHp = pBtl.getInitHp();
		Integer cHp = player.getCurrentHp();
		Integer dHp = iHp - cHp;
		Integer hRates = Math.round((dHp*100/iHp));
		Integer dMp = (player.getMp()*(hRates))/100;
		player.decreaseMp(dMp);
		resultTxt = resultTxt.replace("%minus_hp%", dHp.toString());
		resultTxt = resultTxt.replace("%minus_mp%", dMp.toString());
		resultMap.put("script", resultTxt);
		return resultMap;
	}
	
	public void setIntro() {
		Map<String,Object> contextMap = new HashMap<String,Object>();
		contextMap.put("sceneInfo", "intro");
		manager.setEventContext(contextMap);
	}
	
	public Map<String,Object> notEnd(){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("AFTER_OVER", "FALSE");
		return resultMap;
	}
}
