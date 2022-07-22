package abd.game.character;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NPCharacter extends GameCharacter implements NonPlayerable,CountTurnObserver{

	private Integer xp;//쓰러트릴 시 주는 경험치
	private Integer maxFreq;
	private Integer currentFreq;
	//대사 관련
	private Map<String,List<String>> lines;//조우시 대사(encounter), 액션시 대사(action), 사망시 대사(defeat), 도망시(runAway) 대사
	private String line = "NO_TEXT";
	
	private Integer turnCount;
	private boolean encounter;
	
	public NPCharacter(String charCd, String charNm, String classCd) {
		// TODO Auto-generated constructor stub
		super(charCd, charNm, classCd);
		this.turnCount = 0;
		
		this.armed = true;
		
		this.lines = new HashMap<String, List<String>>();
		List<String> eLines = new ArrayList<String>();
		List<String> aLines = new ArrayList<String>();
		List<String> dLines = new ArrayList<String>();
		List<String> rLines = new ArrayList<String>();
		this.lines.put("E", eLines);
		this.lines.put("A", aLines);
		this.lines.put("D", dLines);
		this.lines.put("R", rLines);
	}

	public void setStatus(String hp, String att, String xp, String freq) {
		// TODO Auto-generated method stub
		try {
			this.att = Integer.valueOf(att);
			this.maxFreq = Integer.valueOf(freq);
			this.currentFreq = Integer.valueOf(freq);
			
			if("".equals(hp) || hp == null) {
				this.maxHp = 0;
				this.currentHp = 0;
			}else {
				this.maxHp = Integer.valueOf(hp);
				this.currentHp = Integer.valueOf(hp);
			}
			
			if("".equals(xp) || xp == null) {
				this.xp = 0;
			}else {
				this.xp = Integer.valueOf(xp);
			}
		}catch(NullPointerException e) {
			throw e;
		}
	}	
	
	public void setEncounter(boolean encounter) {
		this.encounter = encounter;
	}
	
	@Override
	public void react(GameCharacter other) {
		// TODO Auto-generated method stub
		action.react(other);
	}

	public Integer getXp() {
		// TODO Auto-generated method stub
		return xp;
	}
	
	public int getFreq() {
		// TODO Auto-generated method stub
		return currentFreq;
	}
	
	public void setFreq(int freq) {
		if(freq > maxFreq) {
			this.currentFreq = maxFreq;
		}else {
			this.currentFreq = freq;
		}
	}

	@Override
	public void setLines(List<Map<String, String>> lines) {
		// TODO Auto-generated method stub
		if(lines != null) {
			for(Map<String,String> map : lines) {
				List<String> lineList = this.lines.get(map.get("TYPE"));
				lineList.add(map.get("LINE_TXT"));
			}
			
			List<String> eList = this.lines.get("E");
			if(eList.size()>0) {
				this.line = eList.get(0);
			}
		}
	}

	@Override
	public String getLine() {
		// TODO Auto-generated method stub
		return line;
	}
	
	public String getActionLine() {
		// TODO Auto-generated method stub
		return lines.get("A").get(0);
	}

	public void decreaseHp(Integer att) {
		// TODO Auto-generated method stub
		currentHp = currentHp - att;
		if(currentHp < 0) {
			currentHp = 0;
		}
		if(currentHp == 0) {
			alive = false;
			currentFreq = 0;
		}
	}
	
	protected void revive() {
		alive = true;
		currentHp = maxHp;
	}	
	
	@Override
	public void notified() {
		// TODO Auto-generated method stub
		if(!encounter) {
			turnCount++;
			//캐릭터가 죽은 경우 10턴 뒤에 부활함
			if(!isAlive() && turnCount >= 10) {
				//부활하는 캐릭터는 빈도수 초기화 및 턴 카운트 초기화
				//revive();
				//currentFreq = maxFreq;
				//turnCount = 0;
			}else {
				//살아있는 캐릭터의 경우
				if(turnCount%3 == 0) {
					//캐릭터는 매 3턴마다 체력을 10 회복함
					setCurrentHp(currentHp+10);
					if(currentHp == maxHp) {
						//최대 체력이 됐을 때 턴 카운트 초기화
						turnCount = 0;
					}
				}else if(turnCount%5 == 0) {
					//캐릭터는 매 5턴마다 빈도가 증가함
					setFreq(currentFreq+1);
				}
			}
		}
	}

	@Override
	public Map<String, String> getCharacterContext() {
		// TODO Auto-generated method stub
		//"{\"name\":\"김은우\",\"hp\":\"100\",\"att\":\"10\",\"xp\":\"0\",\"lev\":\"1\",\"reqdXp\":\"15\"}"
		Map<String,String> npcContext = new HashMap<String, String>();
		
		npcContext.put("name", getName());
		npcContext.put("maxHp", getHp().toString());
		npcContext.put("hp", getCurrentHp().toString());
		npcContext.put("att", getAtt().toString());
		npcContext.put("xp", xp.toString());
		npcContext.put("line", line);
		
		return npcContext;
	}
}
