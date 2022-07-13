package abd.game.scene;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abd.game.GameDataLoader;
import abd.game.GameManager;
import abd.game.character.PChacrater;

public class GameEvPlay extends GameEvent {
	private String playCode;
	private String playType;
	private GamePlayElement pEl;
	
	private GameDataLoader loader;
	private GameManager manager;

	public GameEvPlay(Map<String, String> eventInfo, GameDataLoader loader, GameManager manager) throws Exception {
		super(eventInfo, loader, manager.getPlayer());
		// TODO Auto-generated constructor stub
		this.loader = loader;
		this.manager = manager;
		
		//플레이는 하나씩만 존재
		List<Map<String,String>> playList = loader.getPlayOfEvent(eventInfo);
		Map<String,String> playInfo = playList.get(0);
		
		playCode = playInfo.get("PLAY_CD");
		playType = playInfo.get("P_TYPE");
		
		if("S".equals(playType)) {
			pEl = new GamePlaySelect(playInfo,loader);
			
		}else if("B".equals(playType)){
			pEl = new GamePlayBattle(playInfo,loader,manager.getPlayer());
		}
	}

	@Override
	public Map<String, Object> happened() {
		// TODO Auto-generated method stub
		Map<String,Object> resultMap = new HashMap<String, Object>();
		if("S".equals(playType)) {
			//선택지를 선택회수만큼 선택하면 이벤트 종료
			resultMap.put("status", "select");
			resultMap.put("selectCode", pEl.getCode());
			resultMap.put("select", pEl.getElContext());
		}else if("B".equals(playType)) {
			//적을 모두 쓰러트리면 이벤트 종료
			resultMap.put("status", "startBattle");
			resultMap.put("battleCode", pEl.getCode());
			resultMap.put("battle", pEl.getElContext());
		}
		return resultMap;
	}

	@Override
	public Map<String, Object> happened(Map<String, Object> input) throws Exception {
		// TODO Auto-generated method stub
		Map<String,Object> resultMap = new HashMap<String, Object>();
		if("S".equals(playType)) {
			@SuppressWarnings("unchecked")
			Map<String,String> selectedInfo = (Map<String,String>) input.get("selected");
			
			List<Map<String,String>> resultList = loader.getSeletionResult(selectedInfo);
			Map<String,String> result = resultList.get(0);
			//가져온 데이터로 객체 실행(리플렉션 사용?)
			//다듬을 필요 있음
			String methodName = result.get("RESULT_OCCURRED");
			if(methodName != null && !"".equals(methodName)) {
				Object instance = manager;
				Class<?> clazz = instance.getClass();
				Method method = clazz.getDeclaredMethod(methodName);
				Map<String,Object> resultContext = (Map<String,Object>)method.invoke(instance);
				resultMap.putAll(resultContext);
			}
		}else if("B".equals(playType)) {
			pEl.play(input);
			
			resultMap.put("status", "battle");
			resultMap.put("battleCode", pEl.getCode());
			resultMap.put("battle", pEl.getElContext());
		}
		return resultMap;
	}
}
