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
			pBtl = new GamePlayBattle(playInfo,loader,manager.getPlayer());
			manager.setBattle(pBtl);
			pEl = pBtl;
		}
	}

	@Override
	public Map<String, Object> happened() throws Exception {
		// TODO Auto-generated method stub
		Map<String,Object> resultMap = new HashMap<String, Object>();
		if("S".equals(playType)) {
			//선택지를 선택회수만큼 선택하면 이벤트 종료
			resultMap.put("status", "select");
			resultMap.put("selectCode", pEl.getCode());
			resultMap.put("select", pEl.getElContext());
		}else if("B".equals(playType)) {
			//적을 모두 쓰러트리면 이벤트 종료
			resultMap.put("battle", pEl.getElContext());
			resultMap.put("battleCode", pEl.getCode());
			//스테이터스 설정
			String battleStatus = (String)pEl.getElContext().get("status");
			if("endBattle".equals(battleStatus)) {
				resultMap.put("status", battleStatus);
			}else {
				resultMap.put("status", "startBattle");
			}
		}
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> happened(Map<String, Object> input) throws Exception {
		// TODO Auto-generated method stub
		Map<String,Object> resultMap = new HashMap<String, Object>();
		if("S".equals(playType)) {
			
			Map<String,String> selectInfo = (Map<String,String>)input.get("selected");
			Map<String,Object> selectContext = pEl.playSelect(selectInfo,manager);
			resultMap.putAll(selectContext);
			
		}else if("B".equals(playType)) {
			
			Map<String,String> selectInfo = (Map<String,String>)input.get("selected");
			Map<String,Object> battleContext = pEl.playSelect(selectInfo,manager);
			resultMap.putAll(battleContext);
			
			if("battleEnd".equals(battleContext.get("status"))) {
				hasDone();
			}
		}
		return resultMap;
	}
}
