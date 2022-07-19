package abd.game.scene;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abd.game.GameDataLoader;
import abd.game.GameManager;

public class GameEvPlay extends GameEvent {
	private String playCode;
	private String playType;
	private GamePlayElement pEl;
	GamePlayBattle pBtl;
	
//	private GameDataLoader loader;
	private GameManager manager;

	public GameEvPlay(Map<String, String> eventInfo, GameDataLoader loader, GameScene scene, GameManager manager) throws Exception {
		super(eventInfo, loader, scene, manager.getPlayer());
		// TODO Auto-generated constructor stub
//		this.loader = loader;
		this.manager = manager;
		
		//플레이는 하나씩만 존재
		List<Map<String,String>> playList = loader.getPlayOfEvent(eventInfo);
		Map<String,String> playInfo = playList.get(0);
		
		playCode = playInfo.get("PLAY_CD");
		playType = playInfo.get("P_TYPE");
		setChildType(playType);
		
		if("S".equals(playType)) {
			pEl = new GamePlaySelect(playInfo,loader);
			
		}else if("I".equals(playType)){
			pEl = new GamePlayInput(playInfo,loader);
			
		}else if("B".equals(playType)){
			pBtl = new GamePlayBattle(playInfo,loader,manager.getPlayer());
			manager.setBattle(pBtl);
			pEl = pBtl;
		}
	}
	
	public String getPlayType() {
		return playType;
	}

	@Override
	public Map<String, Object> happened() throws Exception {
		// TODO Auto-generated method stub
		Map<String,Object> resultMap = new HashMap<String, Object>();
		//이벤트 플레이인 걸 명시해준다.
		resultMap.put("event", "play");
		
		if("S".equals(playType)) {
			//선택지를 선택회수만큼 선택하면 이벤트 종료
			//플레이 선택임을 명시해준다.
			resultMap.put("play", "select");
			resultMap.put("selectCode", pEl.getCode());
			resultMap.put("select", pEl.getElContext());
			
		}else if("I".equals(playType)) {
			//플레이 입력임을 명시해준다.
			resultMap.put("play", "input");
			
			resultMap.put("inputCode", pEl.getCode());
			resultMap.put("input", pEl.getElContext());
		}else if("B".equals(playType)) {
			//적을 모두 쓰러트리면 이벤트 종료
			//동일한 레이어에서 리턴값을 설정하는 것이 바람직하다 즉 배틀도 여기서 스테이터스->이벤트를 설정하는 것이 좋아보임
			//플레이 전투시작임을 명시해준다.
			resultMap.put("play", "battle");
			resultMap.put("battle", pEl.getElContext());
			resultMap.put("battleCode", pEl.getCode());
			
		}
		return resultMap;
	}

	@Override
	public Map<String, Object> happened(Map<String, Object> input) throws Exception {
		// TODO Auto-generated method stub
		Map<String,Object> resultMap = new HashMap<String, Object>();
		//이벤트 플레이인 걸 명시해준다.
		resultMap.put("event", "play");
		
		//실행하고 결과를 받는다.
		Map<String,Object> playContext = null;
		
		if("S".equals(playType)) {
			//셀렉트 결과는 이벤트 등이 될 수 있으므로 그냥 통째로 받는다.
			playContext = pEl.play(input,manager);
			resultMap.putAll(playContext);
			
		}else if("I".equals(playType)) {
			//입력임을 명시해준다.
			playContext = pEl.play(input,manager);
			resultMap.put("play", "input");
			resultMap.put("input", playContext);
			hasDone();
		}else if("B".equals(playType)) {
			//선택의 결과를 가져온다
			playContext = pEl.play(input,manager);
			//선택의 결과에서 프로세스를 가지고 전투 종료인지 전투 진행인지 체크
			if("end".equals(playContext.get("process"))) {
				resultMap.put("play", "endBattle");
				hasDone();
			}else {
				resultMap.put("play", "battle");
			}
			resultMap.put("battle", playContext);
			
		}
		return resultMap;
	}
}
