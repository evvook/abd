package abd.game.scene;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abd.game.GameDataLoader;
import abd.game.character.PCharacter;

public abstract class GameEvent {
	private String eventCode;
	private String eventSeq;
	private String eventType;
	private String eventChildType;
	
	private Map<String,String> eventTagBefore;
	private Map<String,String> eventTagAfter;
	
	private boolean done;
	private PCharacter player;
	
	private GameScene thisScene;
	
	public GameEvent(Map<String, String> eventInfo, GameDataLoader loader, GameScene scene, PCharacter player) throws Exception {
		// TODO Auto-generated constructor stub
		eventCode = eventInfo.get("EVENT_CD");
		eventSeq = eventInfo.get("EVENT_SEQ");
		eventType = eventInfo.get("E_TYPE");
		
		List<Map<String,String>> tagList = null;
		if(Integer.valueOf(eventInfo.get("CNT_TAG"))>0) {
			eventTagBefore = new HashMap<String,String>();
			eventTagAfter = new HashMap<String,String>();
			
			tagList = loader.getTagOfEvent(eventInfo);
			for(Map<String,String> tagInfo:tagList) {
				if("BEFORE".equals(tagInfo.get("TAG_TYPE"))) {
					eventTagBefore.put(tagInfo.get("TAG"), tagInfo.get("RESULT_OCCURRED"));
				}else if("AFTER".equals(tagInfo.get("TAG_TYPE"))) {
					eventTagAfter.put(tagInfo.get("TAG"), tagInfo.get("RESULT_OCCURRED"));
				}
			}
		}
		
		thisScene = scene;
		
		done = false;
		
		this.player = player;
	}
	
	protected GameScene getScene() {
		return thisScene;
	}
	
//	public void setNextEvent(GameEvent nextEvent) {
//		this.nextEvent = nextEvent;
//	}
//	
//	public GameEvent getNextEvent() {
//		return nextEvent;
//	}

	public String getEventCode() {
		return eventCode;
	}
	
	public String getEventSeq() {
		return eventSeq;
	}
	
	public String getEventType() {
		return eventType;
	}
	
	protected PCharacter getPlayer() {
		return player;
	}
	
	public Map<String,Object> somethingHappened() throws Exception{
		if(eventTagBefore != null) doBeforeWork();
		Map<String, Object> resultMap = happened();
		return resultMap;
	}
	
	public Map<String, Object> somethingHappened(Map<String, Object> input) throws Exception{
		if(eventTagBefore != null) doBeforeWork();
		Map<String, Object> resultMap = happened(input);
		return resultMap;
		
	}
	
	protected abstract Map<String,Object> happened() throws Exception;
	
	protected abstract Map<String, Object> happened(Map<String, Object> input) throws Exception;
	
	public boolean isDone() {
		return done;
	}
	
	public void hasDone() throws Exception {
		if(eventTagAfter != null) {
			Map<String,Object> resultMap = doAfterWork();
			if(!"TRUE".equals(resultMap.get("AFTER_OVER"))) {
				return;
			}
		}
		done = true;
		GameScene scene = getScene();
		scene.eventDone(getEventCode(),getEventSeq());
	}

	public void doneBack() {
		// TODO Auto-generated method stub
		done = false;
	}

	protected void setChildType(String eventChildType) {
		this.eventChildType = eventChildType;
	}
	
	public String getchildType() {
		return eventChildType;
	}
	
	private Map<String, Object> doAfterWork() throws Exception {
		// TODO Auto-generated method stub
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("AFTER_OVER", "TRUE");
		
		for(String tagName:eventTagAfter.keySet()) {
			String methodName = eventTagAfter.get(tagName);
			if(methodName != null && !"".equals(methodName)) {
				
				Object instance = thisScene.getEventCallBack();
				Class<?> clazz = instance.getClass();
				
				Method method = null;
				method = clazz.getDeclaredMethod(methodName);
				@SuppressWarnings("unchecked")
				Map<String,Object> callbackResult = (Map<String, Object>)method.invoke(instance);
				if(callbackResult!=null) {
					resultMap.putAll(callbackResult);
				}
			}
			
		}
		return resultMap;
	}
	
	private void doBeforeWork() throws Exception {
		// TODO Auto-generated method stub
		for(String tagName:eventTagBefore.keySet()) {
			String methodName = eventTagBefore.get(tagName);
			if(methodName != null && !"".equals(methodName)) {
				
				Object instance = thisScene.getEventCallBack();
				Class<?> clazz = instance.getClass();
				
				Method method = null;
				method = clazz.getDeclaredMethod(methodName);
				method.invoke(instance);
			}
			
		}
	}
}
