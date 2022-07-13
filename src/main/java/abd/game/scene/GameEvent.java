package abd.game.scene;

import java.util.Map;

import abd.game.GameDataLoader;
import abd.game.character.PChacrater;

public abstract class GameEvent {
	private String eventCode;
	private String eventSeq;
	private boolean done;
	private PChacrater player;
	
	public GameEvent(Map<String, String> eventInfo, GameDataLoader loader,PChacrater player) {
		// TODO Auto-generated constructor stub
		eventCode = eventInfo.get("EVENT_CD");
		eventSeq = eventInfo.get("EVENT_SEQ");
		
		done = false;
		
		this.player = player;
	}

	public String getEventCode() {
		return eventCode;
	}
	
	public String getEventSeq() {
		return eventSeq;
	}
	
	protected PChacrater getPlayer() {
		return player;
	}
	
	public abstract Map<String,Object> happened();
	
	public abstract Map<String, Object> happened(Map<String, Object> input) throws Exception;
	
	public boolean isDone() {
		return done;
	}
	
	public void hasDone() {
		done = true;
	}

}
