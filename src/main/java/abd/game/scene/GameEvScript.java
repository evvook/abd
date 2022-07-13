package abd.game.scene;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import abd.game.GameDataLoader;
import abd.game.character.PChacrater;

public class GameEvScript extends GameEvent {
	private Queue<String> scripts;

	public GameEvScript(Map<String, String> eventInfo, GameDataLoader loader, PChacrater player) throws Exception {
		super(eventInfo, loader, player);
		// TODO Auto-generated constructor stub
		List<Map<String,String>> scriptList = loader.getScriptsOfEvent(eventInfo);
		
		scripts = new LinkedList<String>();
		
		for(Map<String,String> script:scriptList) {
			scripts.add(script.get("SCRIPT_TXT"));
		}
	}

	@Override
	public Map<String, Object> happened() {
		// TODO Auto-generated method stub
		Map<String,Object> resultMap = new HashMap<String, Object>();
		resultMap.put("status", "script");
		resultMap.put("script", scripts.poll());
		if(scripts.isEmpty()) {
			//스크립트가 전부 소진되면 이벤트 종료
			hasDone();
		}
		return resultMap;
	}

	@Override
	public Map<String, Object> happened(Map<String, Object> input) {
		// TODO Auto-generated method stub
		return null;
	}

}
