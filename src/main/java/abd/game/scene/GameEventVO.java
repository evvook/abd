package abd.game.scene;

public class GameEventVO {
	private String eventCode;
	private String eventSeq;
	private String eventType;
	private String eventTagCnt;
	
	public GameEventVO(String eventCode, String eventSeq) {
		this.eventCode = eventCode;
		this.eventSeq = eventSeq;
	}
	
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public void setEventTagCnt(String eventTagCnt) {
		this.eventTagCnt = eventTagCnt;
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
	public String getEventTagCnt() {
		return eventTagCnt;
	}
}
