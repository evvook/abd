package abd.game.character;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abd.game.character.action.SpecialAction;

public class CompCharacter extends GameCharacter {
	//활성화(선택가능) 상태여부
	private boolean active;
	
	//특수능력이 있을 수 있음
	private Integer spAbl1;
	private Integer spAbl2;
	
	private SpecialAction healHp;
	private SpecialAction healMp;
	
	//도움간격
	private Integer maxReplTerm;
	private Integer minReplTerm;
	private Integer currentReplTerm;
	private Integer termCnt;
	
	private Integer currentReliabl;
	private Integer maxReliabl;
	
	private PCharacter player;
	
	//대사 관련
	private Map<String,List<String>> lines;//조우시 대사(encounter), 액션시 대사(action), 사망시 대사(defeat), 도망시(runAway) 대사
	private String line = "NO_TEXT";
	
	public CompCharacter(String charCd, String charNm, String classCd, String hp, String att, String ac, String av) {
		super(charCd, charNm, classCd, hp, att, ac, av);
		// TODO Auto-generated constructor stub
		//기본은 활성화 상태
		active = true;
		termCnt = 0;
		
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
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive() {
		active = true;
	}
	
	public void setInactive() {
		active = false;
	}
	
	public Integer getSpAbl1() {
		return spAbl1;
	}
	
	public Integer getSpAbl2() {
		return spAbl2;
	}
	
	public void setPlayer(PCharacter player) {
		this.player = player;
	}

	@Override
	public Map<String, Object> getCharacterContext() {
		// TODO Auto-generated method stub
		Map<String,Object> pcContext = new HashMap<String, Object>();
		
		pcContext.put("name", getName());
		pcContext.put("maxHp", getHp().toString());
		pcContext.put("hp", getCurrentHp().toString());
		pcContext.put("maxMp", getMp().toString());
		pcContext.put("mp", getCurrentMp().toString());
		pcContext.put("att", getAtt().toString());
		pcContext.put("currentReliabl", currentReliabl.toString());
		if(active) {
			pcContext.put("active", "도움 가능");
			pcContext.put("line", getLine());
			//전투 진행한 동료는 비활성화 된다.
			setInactive();
			//턴이 지나야 활성화 됨
			++termCnt;
		}else {
			pcContext.put("active", "도움 불가능");
			pcContext.put("line", getLine());
		}
		
		return pcContext;
	}

	public void act(NPCharacter encounteredChracter) throws Exception {
		// TODO Auto-generated method stub
		//전투
		if(active) {
			player.setActionResult(this.action.act(encounteredChracter));
			if(this.isAlive()) {
				if(!encounteredChracter.isAlive()) {
					player.takeExp(encounteredChracter);
				}
			}
		}
	}
	
	public void reactSpAbl1(PCharacter player) {
		healHp.react(player);
	}
	
	public void reactSpAbl2(PCharacter player) {
		healMp.react(player);
	}
	
	//mp,spabl1,apabl2,
	public void setStatus(String mp, String minReplTerm, String maxReplTerm, String spAbl1, String spAbl2, String minReliabl, String maxReliabl) {
		// TODO Auto-generated method stub
		this.maxMp = Integer.valueOf(mp);
		this.currentMp = Integer.valueOf(mp);
		this.minReplTerm = Integer.valueOf(minReplTerm);
		this.maxReplTerm = Integer.valueOf(maxReplTerm);
		this.currentReplTerm = Integer.valueOf(maxReplTerm);
//		this.minReliab = Integer.valueOf(minReliab);
		this.maxReliabl = Integer.valueOf(maxReliabl);
		this.currentReliabl = Integer.valueOf(minReliabl);
		
		//특수능력치가 있다면, 설정
		if("".equals(spAbl1) || spAbl1 == null) {
			this.spAbl1 = 0;
		}else {
			this.spAbl1 = Integer.valueOf(spAbl1);
		}
		
		if("".equals(spAbl2) || spAbl2 == null) {
			this.spAbl2 = 0;
		}else {
			this.spAbl2 = Integer.valueOf(spAbl2);
		}
	}

	public void setSpecialAction1(SpecialAction healHp) {
		// TODO Auto-generated method stub
		this.healHp = healHp;
	}

	public void setSpecialAction2(SpecialAction healMp) {
		// TODO Auto-generated method stub
		this.healMp = healMp;
	}

	
	public void setLines(List<Map<String, String>> lines) {
		// TODO Auto-generated method stub
		if(lines != null) {
			for(Map<String,String> map : lines) {
				List<String> lineList = this.lines.get(map.get("TYPE"));
				lineList.add(map.get("LINE_TXT"));
			}
			
			List<String> eList = this.lines.get("E");
			List<String> aList = this.lines.get("A");
			if(eList.size()>0) {
				this.line = eList.get(0);
			}else if(aList.size()>0) {
				this.line = aList.get(0);
			}
		}
	}
	public String getLine() {
		// TODO Auto-generated method stub
		if(active) {
			return "\""+line+"\"";
		}else {
			return getName()+"이(가) 당신을 도울 수 있는 상황이 아닙니다.";
		}
	}
	
	public String getActionLine() {
		// TODO Auto-generated method stub
		return lines.get("A").get(0);
	}

	public void countTurn() {
		// TODO Auto-generated method stub
		++termCnt;
		if(currentReplTerm <= termCnt) {
			setActive();
			termCnt = 0;
		}
	}

	public void increaseReliabl(Integer reliabl) {
		// TODO Auto-generated method stub
		currentReliabl = currentReliabl + reliabl;
		if(currentReliabl > maxReliabl) {
			currentReliabl = maxReliabl;
		}
	}
}
