package abd.game.character;

import java.util.List;
import java.util.Map;

public interface NonPlayerable {

	public Integer getXp();
	
	public int getFreq();
	
	public void react(GameCharacter other);	
	
	public String getLine();
	
	public void setLines(List<Map<String,String>> lines);
}
