package abd.service;

import java.util.Map;

import abd.game.Game;

public interface PlayService {

	void setGame(Game game);
	Map<String, Object> play(Map<String, Object> paramMap) throws Exception;

}
