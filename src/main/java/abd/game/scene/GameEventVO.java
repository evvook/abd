package abd.game.scene;

public class GameEventVO {
	private String eventCode;
	private String eventSeq;
	private String eventType;
	
	public GameEventVO(String eventCode, String eventSeq, String eventType) {
		this.eventCode = eventCode;
		this.eventSeq = eventSeq;
		this.eventType = eventType;
	}
	
	public String getEventCode() {
		return eventCode;
	}
	public String getEventSeq() {
		return eventSeq;
	}
	public String getEventType() {
		return eventType;
	}
}
