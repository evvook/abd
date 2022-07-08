package abd.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import abd.service.CharacterService;

@Controller
public class DatabaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(DatabaseController.class);
	
	@Resource
	private CharacterService service;
	
	@ResponseBody
	@RequestMapping(value="/getCharacterName", method=RequestMethod.POST)
	public List<Map<String,String>> getCharacterName(@RequestBody Map<String,Object> param) throws Exception{
		logger.info("db");
		@SuppressWarnings("unchecked")
		Map<String,String> paramMap = (Map<String,String>)param.get("inputs");
		
		List<Map<String,String>> characterList = service.selectCharacter(paramMap);
		return characterList;
	}
}
