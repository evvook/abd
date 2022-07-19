package abd.game.scene;

import java.util.Map;

import abd.game.GameDataLoader;
import abd.game.character.PCharacter;

public abstract class GameEvent {
	private String eventCode;
	private String eventSeq;
	private String eventType;
	private String eventChildType;
	
	private boolean done;
	private PCharacter player;
	
	private GameScene thisScene;
	
	public GameEvent(Map<String, String> eventInfo, GameDataLoader loader, GameScene scene, PCharacter player) {
		// TODO Auto-generated constructor stub
		eventCode = eventInfo.get("EVENT_CD");
		eventSeq = eventInfo.get("EVENT_SEQ");
		eventType = eventInfo.get("E_TYPE");
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
	
	public abstract Map<String,Object> happened() throws Exception;
	
	public abstract Map<String, Object> happened(Map<String, Object> input) throws Exception;
	
	public boolean isDone() {
		return done;
	}
	
	public void hasDone() {
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

}
