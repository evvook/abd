package abd.game.scene;

import java.util.Map;

import abd.game.GameDataLoader;
import abd.game.character.PCharacter;

public abstract class GameEvent {
	private String eventCode;
	private String eventSeq;
	private boolean done;
	private PCharacter player;
	
	private GameEvent nextEvent;
	
	public GameEvent(Map<String, String> eventInfo, GameDataLoader loader,PCharacter player) {
		// TODO Auto-generated constructor stub
		eventCode = eventInfo.get("EVENT_CD");
		eventSeq = eventInfo.get("EVENT_SEQ");
		
		done = false;
		
		this.player = player;
	}
	
	public void setNextEvent(GameEvent nextEvent) {
		this.nextEvent = nextEvent;
	}
	
	public GameEvent getNextEvent() {
		return nextEvent;
	}

	public String getEventCode() {
		return eventCode;
	}
	
	public String getEventSeq() {
		return eventSeq;
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
	}

	public void doneBack() {
		// TODO Auto-generated method stub
		done = false;
	}

}
