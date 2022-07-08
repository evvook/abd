package abd.game;

import java.util.Map;

public interface GameInterface {
	public void createPlayerCharacter(GameDataLoader loader, String name) throws Exception;
	
	public boolean isEncountered();
	
	public void takeActionToEachother(String actionCommand) throws Exception;
	
	public Map<String, Object> getGameContext();
	
	public void talkWithHealer(GameDataLoader loader) throws Exception;
	
	public void goAdventure(GameDataLoader loader) throws Exception;
}
