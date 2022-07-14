package abd.game.scene;

import java.util.Iterator;
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
	
	private LinkedList<GameEvent> events;
	int idxOfEvents = 0;
	
	public GameScene(Map<String, String> map, GameDataLoader loader, GameManager manager) throws Exception {
		// TODO Auto-generated constructor stub
		sceneCode = map.get("SCENE_CD");
		sceneName = map.get("SCENE_NM");
		
		events = new LinkedList<GameEvent>();
		
		List<Map<String,String>> eventInfoList = loader.getEventsOfScene(map);
		
		for(Map<String,String> eventInfo:eventInfoList) {
			if("S".equals(eventInfo.get("E_TYPE"))) {
				GameEvent event = new GameEvScript(eventInfo,loader,manager.getPlayer());
				if(!events.isEmpty())
					events.peekLast().setNextEvent(event);
				events.add(event);
				
			}else if("P".equals(eventInfo.get("E_TYPE"))) {
				GameEvent event = new GameEvPlay(eventInfo,loader,manager);
				if(!events.isEmpty())
					events.peekLast().setNextEvent(event);
				events.add(event);
				
			}
		}
	}
	
	public String getSceneCode() {
		return this.sceneCode;
	}

	public GameEvent getEvent() {
		// TODO Auto-generated method stub
		return events.get(idxOfEvents++);
	}
	
	public GameEvent getEvent(String eventCode, String eventSeq) {
		GameEvent selectedEvent = null;
		for(int i=0; i<events.size();i++) {
			GameEvent event = events.get(i);
			if(selectedEvent == null)
				if(eventCode.equals(event.getEventCode()) && eventSeq.equals(event.getEventSeq())) {
					selectedEvent = event;
					selectedEvent.doneBack();
					idxOfEvents = i+1;
			}
			
			if(i >= idxOfEvents) {
				event.doneBack();
			}
		}
		return selectedEvent;
	}

}
