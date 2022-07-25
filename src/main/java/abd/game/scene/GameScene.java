package abd.game.scene;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import abd.game.GameDataLoader;
import abd.game.GameManager;

public class GameScene {
	private String sceneCode;
	private String sceneName;
	
	private Map<String,GameEvent> events;
	private LinkedList<GameEventVO> eventInfo;
	private int idxOfEvents = 0;
	
	private boolean done;

	GameDataLoader loader;
	GameManager manager;
	
	public GameScene(Map<String, String> map, GameDataLoader loader, GameManager manager) throws Exception {
		// TODO Auto-generated constructor stub
		sceneCode = map.get("SCENE_CD");
		sceneName = map.get("SCENE_NM");
		
		this.loader = loader;
		this.manager = manager;
		
		events = new HashMap<String, GameEvent>();
		eventInfo = new LinkedList<GameEventVO>();
		done = false;
		
		List<Map<String,String>> eventInfoList = loader.getEventsOfScene(map);
		
		for(Map<String,String> eventInfo:eventInfoList) {
			GameEventVO eVo = new GameEventVO(eventInfo.get("EVENT_CD"), eventInfo.get("EVENT_SEQ"), eventInfo.get("E_TYPE"));
			this.eventInfo.add(eVo);
			//첫 번째 이벤트 만들기
			if(events.isEmpty()) {
				setEvent(eVo.getEventCode(), eVo.getEventSeq());	
			}
		}
	}
	
	public String getSceneCode() {
		return this.sceneCode;
	}
	
	public String getSceneName() {
		return this.sceneName;
	}
	
	private GameEvent setEvent(String eventCode, String eventSeq) throws Exception {
		GameEvent event = null;
		for(int i=0;i<eventInfo.size();i++) {
			GameEventVO eventVo = eventInfo.get(i);
			if(eventCode.equals(eventVo.getEventCode()) && eventSeq.equals(eventVo.getEventSeq())) {
				Map<String,String> eventInfo = new HashMap<String, String>();
				eventInfo.put("EVENT_CD", eventVo.getEventCode());
				eventInfo.put("EVENT_SEQ", eventVo.getEventSeq());
				eventInfo.put("E_TYPE", eventVo.getEventType());
				
				if("S".equals(eventInfo.get("E_TYPE"))) {
					event = new GameEvScript(eventInfo,loader, this, manager.getPlayer());
					
				}else if("J".equals(eventInfo.get("E_TYPE"))) {
					event = new GameEvJustHpn(eventInfo,loader,this,manager);
					
				}else if("P".equals(eventInfo.get("E_TYPE"))) {
					event = new GameEvPlay(eventInfo,loader,this,manager);
					
				}else if("B".equals(eventInfo.get("E_TYPE"))) {
					event = new GameEvBranch(eventInfo,loader,this,manager);
					manager.setBranch((GameEvBranch)event);
					
				}
				if(!events.isEmpty()) {
//					GameEventVO eVo = this.eventInfo.get(idxOfEvents);
//					GameEvent lastEvent = events.get(eVo.getEventCode());
//					lastEvent.setNextEvent(event);
				}
				events.put(eventVo.getEventCode()+eventVo.getEventSeq(), event);
				idxOfEvents = i;
				break;
			}
		}
		return event;
	}
	
	private GameEvent setNextEvent() throws Exception{
		GameEvent event = null;
		int idxOfNextEvent = idxOfEvents+1;
		if(this.eventInfo.size() > idxOfEvents) {
			GameEventVO currentEventInfo = this.eventInfo.get(idxOfNextEvent);
			String eventCode = currentEventInfo.getEventCode();
			String eventSeq = currentEventInfo.getEventSeq();
			
			event = setEvent(eventCode, eventSeq);
		}
		return event;
	}
	
	public GameEvent getEvent() throws Exception {
		// TODO Auto-generated method stub
		GameEventVO eVo = eventInfo.get(idxOfEvents);
		GameEvent currentEvent = events.get(eVo.getEventCode()+eVo.getEventSeq());
		return currentEvent;
	}
	
	public GameEvent getNextEvent() throws Exception {
		// TODO Auto-generated method stub
		GameEventVO eVo = eventInfo.get(idxOfEvents);
		
		System.out.println(eVo.getEventCode()+", "+eVo.getEventSeq());
		
		GameEvent currentEvent = events.get(eVo.getEventCode()+eVo.getEventSeq());
		currentEvent = setNextEvent();
		
		return currentEvent;
	}
	
	public GameEvent getEvent(String eventCode, String eventSeq) throws Exception {
		GameEvent selectedEvent = null;
		//이미 있는 이벤트를 가져올 경우
		for(int i=0; i<eventInfo.size();i++) {
			GameEventVO eVo = eventInfo.get(i);
			GameEvent event = events.get(eVo.getEventCode()+eVo.getEventSeq());
			if(event != null) {
				if(selectedEvent == null) {
					if(eventCode.equals(event.getEventCode()) && eventSeq.equals(event.getEventSeq())) {
						selectedEvent = event;
						selectedEvent.doneBack();
						
						//브랜치인 경우 매니저에 설정된 브랜치 변경
						if("B".equals(selectedEvent.getEventType())) {
							manager.setBranch((GameEvBranch)selectedEvent);
						}
						idxOfEvents = i;
					}
				}
				if(i >= idxOfEvents) {
					event.doneBack();
				}
			}
		}
		//위와 같은 작업을 했는데도 이벤트가 없는 경우
		if(selectedEvent == null) {
			//이벤트를 새로 등록
			selectedEvent = setEvent(eventCode, eventSeq);
		}
		return selectedEvent;
	}

	public boolean isDone() {
		// TODO Auto-generated method stub
		return done;
	}
	
	public void hasDone() {
		// TODO Auto-generated method stub
		done = true;
	}
	
	public void doneBack() {
		// TODO Auto-generated method stub
		done = false;
	}

	public void eventDone(String eventCode, String eventSeq) {
		// TODO Auto-generated method stub
		String lastEventCode = eventInfo.peekLast().getEventCode();
		String lastEventSeq = eventInfo.peekLast().getEventSeq();
		if(eventCode.equals(lastEventCode) && eventSeq.equals(lastEventSeq)) {
			hasDone();
		}
	}

}
