package abd.game.character;

import java.util.List;
import java.util.Map;

import abd.game.character.action.Fight;
import abd.game.character.action.HealHp;
import abd.game.character.action.HealMp;

public class GameCharacterBuilder {

	public static NPCharacter getNPCharacterInstance(Map<String, String> charInfo, List<Map<String,String>> charLineList) throws Exception {
		
		NPCharacter newCharacter = new NPCharacter(charInfo.get("CHAR_CD"),charInfo.get("CHAR_NM"),charInfo.get("CLASS_CD"),charInfo.get("HP"),charInfo.get("ATT"),charInfo.get("AC"),charInfo.get("AV"));
		newCharacter.setStatus(charInfo.get("XP"),charInfo.get("FREQ"));
		
		newCharacter.setAction(new Fight(newCharacter));
		
		newCharacter.setLines(charLineList);
		
		
		return newCharacter;
	}
	
	public static PCharacter getPCharacterInstance(Map<String, String> charInfo, String character_name) throws Exception {
		
		PCharacter newCharacter = new PCharacter(charInfo.get("CHAR_CD"),character_name,charInfo.get("CLASS_CD"),charInfo.get("HP"),charInfo.get("ATT"),charInfo.get("AC"),charInfo.get("AV"));
		newCharacter.setLvlStatus(charInfo);
		newCharacter.setAction(new Fight(newCharacter));
		
		return newCharacter;
	}
	
	public static CompCharacter getCompCharacterInstance(Map<String, String> charInfo, List<Map<String, String>> characterLines) throws Exception {

		CompCharacter newCharacter = new CompCharacter(charInfo.get("CHAR_CD"),charInfo.get("CHAR_NM"),charInfo.get("CLASS_CD"),charInfo.get("HP"),charInfo.get("ATT"),charInfo.get("AC"),charInfo.get("AV"));
		newCharacter.setStatus(charInfo.get("MP"),charInfo.get("MIN_REPL_TERM"),charInfo.get("MAX_REPL_TERM"),charInfo.get("SP_ABL1"),charInfo.get("SP_ABL2"),charInfo.get("MIN_RELIABL"),charInfo.get("MAX_RELIABL"));
		
		if(GameCharacter.CLASS_FIGHTER.equals(charInfo.get("CLASS_CD"))) {
			newCharacter.setAction(new Fight(newCharacter));
			
		}else if(GameCharacter.CLASS_SPECAIL.equals(charInfo.get("CLASS_CD"))) {
			newCharacter.setAction(new Fight(newCharacter));
			
			//특수능력이 있다면 설정
			if(charInfo.get("SP_ABL1") != null && !"".equals(charInfo.get("SP_ABL1"))) {
				newCharacter.setSpecialAction1(new HealHp(newCharacter));
			}
			if(charInfo.get("SP_ABL2") != null && !"".equals(charInfo.get("SP_ABL2"))) {
				newCharacter.setSpecialAction2(new HealMp(newCharacter));
			}
		}
		newCharacter.setLines(characterLines);
		
		return newCharacter;
	}
}
