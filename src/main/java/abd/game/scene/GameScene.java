package abd.game.scene;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import abd.game.GameDataLoader;
import abd.game.GameManager;
import abd.game.character.PChacrater;

public class GameScene {
	private String sceneCode;
	private String sceneName;
	
	private Queue<GameEvent> events;
	
	public GameScene(Map<String, String> map, GameDataLoader loader, GameManager manager) throws Exception {
		// TODO Auto-generated constructor stub
		sceneCode = map.get("SCENE_CD");
		sceneName = map.get("SCENE_NM");
		
		events = new LinkedList<GameEvent>();
		
		List<Map<String,String>> eventInfoList = loader.getEventsOfScene(map);
		
		for(Map<String,String> eventInfo:eventInfoList) {
			if("S".equals(eventInfo.get("E_TYPE"))) {
				events.add(new GameEvScript(eventInfo,loader,manager.getPlayer()));
				
			}else if("P".equals(eventInfo.get("E_TYPE"))) {
				events.add(new GameEvPlay(eventInfo,loader,manager));
				
			}
		}
	}
	
	public String getSceneCode() {
		return this.sceneCode;
	}

	public GameEvent getEvent() {
		// TODO Auto-generated method stub
		return events.poll();
	}

}
