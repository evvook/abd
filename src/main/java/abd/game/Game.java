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
	
	public Map<String,Object> run(Map<String,Object> input) throws Exception {
		Map<String,Object> gameContext = null;
		String status = (String)input.get("status");
		String action = (String)input.get("action");
		@SuppressWarnings("unchecked")
		Map<String,Object> inputData = (Map<String,Object>)input.get("inputData");
		
		//시작이면
		if("start".equals(status)) {
			gameInterface.createPlayerCharacter(loader, (String)input.get("name"));
			gameInterface.startSceneLoad(loader);
			gameInterface.goEvent();
			gameContext= gameInterface.getGameContext();
			gameContext.put("status", "start");
			
		}else if("onGoing".equals(status)) {
			if("event".equals(action)) {
				
				if(inputData != null) {
					gameInterface.goEvent(inputData);
				}else {
					gameInterface.goEvent();
				}
				gameContext= gameInterface.getGameContext();
				//gameContext.put("status", "script");
			}
//			if("adventure".equals(order)) {
//				//모험
//				gameInterface.goAdventure(loader);
//				gameContext= gameInterface.getGameContext();
//				gameContext.put("status", "adventure");
//				
//			}else if("rest".equals(order)){
//				//휴식
//				gameInterface.talkWithHealer(loader);
//				gameContext= gameInterface.getGameContext();
//				//gameContext.put("status", "rest");
//				
//			}else if("battle".equals(order)){
//				//대결
//				gameInterface.takeActionToEachother(order);
//				gameContext= gameInterface.getGameContext();
//				
//			}else if("runAway".equals(order)){
//				//도망
//				gameInterface.takeActionToEachother(order);
//				gameContext= gameInterface.getGameContext();
//				gameContext.put("status", "runAway");
//			}else {
//				gameContext = new HashMap<String, Object>();
//				gameContext.put("status", "err");
//			}
			
		}else if("clear".equals(status)){
			GameContainer.remove(this);
		}
		
		
		return gameContext;
	}
}
