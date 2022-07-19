package abd.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import abd.game.Game;
import abd.game.GameContainer;
import abd.service.CharStatService;


@Controller
public class GameCharStatController {

	@Resource
	private CharStatService service;
	
	private static final Logger logger = LoggerFactory.getLogger(GameCharStatController.class);
	
	@CrossOrigin
	@ResponseBody
	@RequestMapping(value="/gameCharStat", method= {RequestMethod.POST,RequestMethod.OPTIONS})
	public Map<String,Object> play(@RequestBody Map<String,Object> param, HttpServletRequest request) throws Exception{
		logger.info("characterStatus");
//		@SuppressWarnings("unchecked")
		Map<String,Object> resultMap = new HashMap<String, Object>();
		
		//세션별로 구분하기위해 세션id를 받아서 게임 정보와 함께 컨테이너에 보관한다.
		String key = (String)param.get("gameToken");
		HttpSession session = request.getSession();
		if(key == null) {
			key = session.getId();
		}

		Game game = null;
		if(GameContainer.isExists(key)) {
			game = GameContainer.getGame(key);
		}else {
			throw new Exception("게임이 존재하지 않습니다.");
		}
		service.setGame(game);
		resultMap = service.getStat();
		resultMap.put("gameToken", key);
		
		return resultMap;
	}
}
