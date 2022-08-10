package abd.game;

import java.util.HashMap;
import java.util.Map;

public class GameContainer {
	public static Map<String,Game> gameContainer = new HashMap<String,Game>();
	
	public static void putGame(String key,Game controller) {
		System.out.println(key);
		gameContainer.put(key, controller);
	}
	public static Game getGame(String key) {
		System.out.println(key);
		return gameContainer.get(key);
	}
	public static boolean isExists(String key) {
		return gameContainer.containsKey(key);
	}
	public static boolean isExists(Game value) {
		return gameContainer.containsValue(value);
	}
	
	public static void remove(Game game) {
		// TODO Auto-generated method stub
		for(String key:gameContainer.keySet()) {
			if(game.equals(gameContainer.get(key))) {
				gameContainer.remove(key);
				System.out.println("컨트롤러 삭제. 컨트롤러 사이즈 : "+gameContainer.size());
				break;
			}
		}
	}
}
