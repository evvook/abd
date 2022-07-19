package abd.service;

import java.util.Map;

import abd.game.Game;

public interface CharStatService {

	void setGame(Game game);
	Map<String, Object> getStat();

}
