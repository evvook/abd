package abd.service;

import java.util.Map;

import abd.game.Game;

public interface SetupService {

	void setGame(Game game);
	Map<String, Object> setup(Map<String, Object> paramMap) throws Exception;

}
