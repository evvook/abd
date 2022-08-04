package abd.game.character.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abd.game.character.GameCharacter;
import abd.game.character.NPCharacter;

public class Fight implements Action {
	GameCharacter fighter;
	
	public Fight(GameCharacter fighter) {
		// TODO Auto-generated constructor stub
		this.fighter = fighter;
	}

	@Override
	public Map<String,Object> act(NPCharacter enermy) {
		// TODO Auto-generated method stub
		Map<String,Object> resultMap = new HashMap<String, Object>();
		
		Map<String,Object> playerResult = attack(enermy, 2);
		resultMap.put("playerResult", playerResult);
		
		//상대가 NonPlayable이면 반응 실행
		if(enermy.isAlive())  {
			resultMap.put("enermyResult",enermy.react(fighter));
		}
		
		return resultMap;
	}

	@Override
	public Map<String,Object> react(GameCharacter other) {
		return attack(other, 1);
	}
	
	private Map<String,Object> attack(GameCharacter character, int cnt){
		Map<String,Object> resultMap = new HashMap<String, Object>();
		
		for(Integer i = 0; i<cnt; i++) {
			
			List<String> acPool = fighter.getAcPool();
			int acIdx = (int) (Math.random() * acPool.size());
			String acRslt = acPool.get(acIdx);
			if("SUCCESS".equals(acRslt)) {//명중
				
				List<String> avPool = character.getAvPool();
				int avIdx = (int) (Math.random() * avPool.size());
				String avRslt = avPool.get(avIdx);
				if("FAIL".equals(avRslt)) {//회피실패=공격성공
					character.decreaseHp(fighter.getAtt());
					resultMap.put("ATTACK"+ String.valueOf(i+1), acRslt);
					resultMap.put("DMG"+ String.valueOf(i+1), fighter.getAtt());
				}else {//회피
					resultMap.put("ATTACK" + String.valueOf(i+1), avRslt);
					resultMap.put("DMG" + String.valueOf(i+1), 0);
				}
			}else {//빗나감
				resultMap.put("ATTACK" + String.valueOf(i+1), acRslt);
				resultMap.put("DMG" + String.valueOf(i+1), 0);
			}
		}
		
		return resultMap;
	}
}
