package abd.game.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import abd.game.GameDataLoader;
import abd.game.GameManager;

public class GameScene {
	private String sceneCode;
	private String sceneName;
	
	private LinkedList<GameEvent> events;
	private List<GameEventVO> eventInfo;
	int idxOfEvents = 0;

	GameDataLoader loader;
	GameManager manager;
	
	public GameScene(Map<String, String> map, GameDataLoader loader, GameManager manager) throws Exception {
		// TODO Auto-generated constructor stub
		sceneCode = map.get("SCENE_CD");
		sceneName = map.get("SCENE_NM");
		
		this.loader = loader;
		this.manager = manager;
		
		events = new LinkedList<GameEvent>();
		eventInfo = new ArrayList<GameEventVO>();
		
		List<Map<String,String>> eventInfoList = loader.getEventsOfScene(map);
		
		for(Map<String,String> eventInfo:eventInfoList) {
			GameEventVO eVo = new GameEventVO(eventInfo.get("EVENT_CD"), eventInfo.get("EVENT_SEQ"), eventInfo.get("E_TYPE"));
			this.eventInfo.add(eVo);
			//첫 번째 이벤트 만들기
			if(events.isEmpty()) {
				if("S".equals(eventInfo.get("E_TYPE"))) {
					GameEvent event = new GameEvScript(eventInfo,loader,manager.getPlayer());
					events.add(event);
					
				}else if("P".equals(eventInfo.get("E_TYPE"))) {
					GameEvent event = new GameEvPlay(eventInfo,loader,manager);
					events.add(event);
				}	
			}
		}
		setNextEvent();
	}
	
	public String getSceneCode() {
		return this.sceneCode;
	}

	public void setNextEvent() throws Exception{
		int idxOfNextEvent = idxOfEvents+1;
		if(this.eventInfo.size() > idxOfEvents) {
			GameEventVO currentEventInfo = this.eventInfo.get(idxOfNextEvent);
			
			Map<String,String> eventInfo = new HashMap<String, String>();
			eventInfo.put("EVENT_CD", currentEventInfo.getEventCode());
			eventInfo.put("EVENT_SEQ", currentEventInfo.getEventSeq());
			eventInfo.put("E_TYPE", currentEventInfo.getEventType());
			
			if("S".equals(eventInfo.get("E_TYPE"))) {
				GameEvent event = new GameEvScript(eventInfo,loader,manager.getPlayer());
				events.peekLast().setNextEvent(event);
				events.add(event);
				
			}else if("P".equals(eventInfo.get("E_TYPE"))) {
				GameEvent event = new GameEvPlay(eventInfo,loader,manager);
				events.peekLast().setNextEvent(event);
				events.add(event);
				
			}
		}
	}
	
	public GameEvent getEvent() throws Exception {
		// TODO Auto-generated method stub
		GameEvent currentEvent = events.get(idxOfEvents);
		
		//다음 이벤트가 셋팅되지 않은 경우 셋팅해둔다
		if(currentEvent.getNextEvent() == null) {
			setNextEvent();
		}
		
		//다음 이벤트 호출을 대비하여 인덱스 증가
		idxOfEvents++;
		return currentEvent;
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
