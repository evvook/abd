package abd.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import abd.game.Game;

@Service("charStatSerive")
public class CharStatServiceImpl implements CharStatService {
	
	private Game game;
	@Override
	public void setGame(Game game) {
		// TODO Auto-generated method stub
		this.game = game;
	}

	@Override
	public Map<String, Object> getStat() {
		// TODO Auto-generated method stub
		return game.getCharStat();
	}

}
