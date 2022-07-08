package abd.game;

import java.util.HashMap;
import java.util.Map;

public class Game {
	private GameInterface gameInterface;
	private GameDataLoader loader;
	
	public Game(GameInterface gameInterface) {
		// TODO Auto-generated constructor stub
		this.gameInterface = gameInterface;
	}
	
	public void setGameDataLoader(GameDataLoader loader) {
		this.loader = loader;
	}
	
	public Map<String,Object> run(Map<String,String> input) throws Exception {
		Map<String,Object> gameContext = null;
		String command = input.get("status");
		String order = input.get("action");
		
		//시작이면
		if("start".equals(command)) {
			gameInterface.createPlayerCharacter(loader, input.get("name"));
			gameContext= gameInterface.getGameContext();
			gameContext.put("status", "start");
			
		}else if("onGoing".equals(command)) {
			if("adventure".equals(order)) {
				//모험
				gameInterface.goAdventure(loader);
				gameContext= gameInterface.getGameContext();
				gameContext.put("status", "adventure");
				
			}else if("rest".equals(order)){
				//휴식
				gameInterface.talkWithHealer(loader);
				gameContext= gameInterface.getGameContext();
				//gameContext.put("status", "rest");
				
			}else if("battle".equals(order)){
				//대결
				gameInterface.takeActionToEachother(order);
				gameContext= gameInterface.getGameContext();
				
			}else if("runAway".equals(order)){
				//도망
				gameInterface.takeActionToEachother(order);
				gameContext= gameInterface.getGameContext();
				gameContext.put("status", "runAway");
			}else {
				gameContext = new HashMap<String, Object>();
				gameContext.put("status", "err");
			}
			
		}else if("clear".equals(command)){
			GameContainer.remove(this);
		}
		
		
		return gameContext;
	}
}
