package abd.game;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Game {
	private GameInterface gameInterface;
	private GameDataLoader loader;
	private static int MAX_SLEEP = 600;
	private int sleep_count;
	
	public Game(GameInterface gameInterface) {
		// TODO Auto-generated constructor stub
		this.gameInterface = gameInterface;
		
		sleep_count = 0;
		selfMonitoring();
	}
	
	public void setGameDataLoader(GameDataLoader loader) {
		this.loader = loader;
	}
	
	public Map<String,Object> run(Map<String,Object> input) throws Exception {
		sleep_count = 0;
		
		Map<String,Object> gameContext = null;
		String status = (String)input.get("status");
		String action = (String)input.get("action");
		@SuppressWarnings("unchecked")
		Map<String,Object> inputData = (Map<String,Object>)input.get("inputData");
		
		//시작이면
		if("start".equals(status)) {
			gameInterface.startSceneLoad(loader);
			if("intro".equals(action)) {
				gameInterface.goToIntro();
			}else {
				gameInterface.goEvent();
			}
			gameContext= gameInterface.getGameContext();
			gameContext.put("status", "onGoing");
			
		}else if("onGoing".equals(status)) {
			if("event".equals(action)) {
				
				if(inputData != null) {
					gameInterface.goEvent(inputData);
				}else {
					gameInterface.goEvent();
				}
				gameContext= gameInterface.getGameContext();
				gameContext.put("status", "onGoing");
			}
			
		}else if("clear".equals(status)){
			GameContainer.remove(this);
		}
		
		
		return gameContext;
	}

	public Map<String, Object> setup(GameSetupLoader setupLoader, Map<String, Object> input) throws Exception {
		sleep_count = 0;
		// TODO Auto-generated method stub
		Map<String,Object> gameContext = null;
		gameInterface.createPlayerCharacter(setupLoader, (String)input.get("name"));
		gameInterface.setupScenesInfo(setupLoader);
		gameContext= gameInterface.getGameContext();
		gameContext.put("status", (String)input.get("status"));
		gameContext.put("action", (String)input.get("action"));
		
		return gameContext;
	}

	public Map<String, Object> getCharStat() {
		sleep_count = 0;
		// TODO Auto-generated method stub
		return gameInterface.getCharStat();
	}
	
	public void selfMonitoring(){
		Game game = this;
		Timer timer = new Timer();
		TimerTask tTask = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				sleep_count++;
				if(sleep_count==MAX_SLEEP) {
					GameContainer.remove(game);
				}
			}
		};
		timer.schedule(tTask, 1000, 1000);
	}

	public void clear() {
		// TODO Auto-generated method stub
		gameInterface = null;
		loader = null;
	}
}
