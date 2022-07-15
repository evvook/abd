package abd.game.scene;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import abd.game.GameDataLoader;
import abd.game.character.PCharacter;

public class GameEvScript extends GameEvent {
	private LinkedList<String> scripts;
	private int idxOfScript = 0;

	public GameEvScript(Map<String, String> eventInfo, GameDataLoader loader, PCharacter player) throws Exception {
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
		String currentScript = scripts.get(idxOfScript++);
		resultMap.put("script", currentScript);
		if(currentScript.equals(scripts.getLast())) {
			//마지막 스크립트가 실행되면
			hasDone();
		}
		return resultMap;
	}

	@Override
	public Map<String, Object> happened(Map<String, Object> input) {
		// TODO Auto-generated method stub
		return null;
	}

	public void doneBack() {
		// TODO Auto-generated method stub
		super.doneBack();
		idxOfScript = 0;
	}
}
