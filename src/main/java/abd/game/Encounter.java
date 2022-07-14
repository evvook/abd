package abd.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import abd.game.character.CountTurnObserver;
import abd.game.character.GameCharacterBuilder;
import abd.game.character.NPCharacter;

//플레이어와 캐릭터를 만나게 하는 객체
public class Encounter {
	private Map<String,NPCharacter> pickedCharacterPool;
	private List<CountTurnObserver> pickedCharacters;
	private NPCharacter character;
	private boolean poolEmpty;
	
	public Encounter() {
		pickedCharacterPool = new HashMap<String, NPCharacter>();
		pickedCharacters = new ArrayList<CountTurnObserver>();
		poolEmpty = false;
	}
	
	public void encounterChracter(List<Map<String, String>> characterList, GameDataLoader loader) throws Exception {
		pickRandomChracter(characterList, loader);
		character.setEncounter(true);
	}
	
	public void encounterChracter(String mapCode, GameDataLoader loader) throws Exception {
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("MAP_CD", mapCode);
		
		List<Map<String,String>> characterList = loader.getCharactersInMap(paramMap);
		pickRandomChracter(characterList, loader);
		character.setEncounter(true);
	}
	
	public NPCharacter getEncounterCharacter() {
		return character;
	}
	
	private void pickRandomChracter(List<Map<String, String>> characterList, GameDataLoader loader) throws Exception {
		// TODO Auto-generated method stub
		String code = null;
		//랜덤으로 캐릭터 뽑는 내용
		//뽑힌 캐릭터 코드로 캐릭터를 찾거나 생성한다.
		
		//2. 캐릭터 코드 리스트를 반복하며 풀 생성(빈도수에 맞게 추가로 코드 add)
		List<String> charPool = new ArrayList<String>();
		for(Map<String,String> codeMap:characterList) {
			int freq = 0;
			code = codeMap.get("CHAR_CD");
			if(pickedCharacterPool.containsKey(code)) {
				NPCharacter pickedChar = pickedCharacterPool.get(code);
				if(pickedChar.isAlive()) {
					freq = pickedChar.getFreq();
				}
			}else {
				freq = Integer.valueOf(codeMap.get("FREQ"));
			}
			
			if(freq>0) {
				//출현빈도에 따라 풀에 포함
				for(int i=0; i<freq; i++) {
					charPool.add(code);
				}
			}
		}
		//3. 리스트 크기에서 랜덤하게 인덱스 뽑음
		System.out.println("pool :"+charPool);
		if(charPool.size() > 0) {
			int idx = (int) (Math.random() * charPool.size());
			//4. 인덱스에 해당하는 캐릭터 생성
			code = charPool.get(idx);
			
			if(pickedCharacterPool.containsKey(code)) {
				character = pickedCharacterPool.get(code);
			}else {
				Map<String,String> paramMap = new HashMap<String, String>();
				paramMap.put("CHAR_CD", code);
				List<Map<String,String>> npcList = loader.getNPCharacterInfo(paramMap);
				List<Map<String,String>> characterLines = loader.getCharacterLine(paramMap);
				character = GameCharacterBuilder.getNPCharacterInstance(npcList.get(0), characterLines);
				pickedCharacterPool.put(code, character);
				pickedCharacters.add(character);
			}
		}else {
			poolEmpty = true;
		}
	}

	public void clearCharacter() {
		// TODO Auto-generated method stub
		character.setEncounter(false);
		character = null;
	}

	public NPCharacter encounterHealer(List<Map<String, String>> healerList, GameDataLoader loader) throws Exception {
		// TODO Auto-generated method stub
		//힐러 중 랜덤으로 뽑아서 셋팅
		
		//2. 캐릭터 코드 리스트를 반복하며 풀 생성(빈도수에 맞게 추가로 코드 add)
		List<String> charPool = new ArrayList<String>();
		for(Map<String,String> codeMap:healerList) {
			int freq = Integer.valueOf(codeMap.get("FREQ"));
			for(int i=0; i<freq; i++) {
				charPool.add(codeMap.get("CHAR_CD"));
			}
		}
		//3. 리스트 크기에서 랜덤하게 인덱스 뽑음
		int idx = (int) (Math.random() * charPool.size());
		//4. 인덱스에 해당하는 캐릭터 생성
		String code = charPool.get(idx);
		Map<String,String> paramMap = new HashMap<String, String>();
		paramMap.put("CHAR_CD", code);
		List<Map<String,String>> npcList = loader.getNPCharacterInfo(paramMap);
		List<Map<String,String>> characterLines = loader.getCharacterLine(paramMap);
		return GameCharacterBuilder.getNPCharacterInstance(npcList.get(0), characterLines);
	}

	public void countTurn() {
		// TODO Auto-generated method stub
		Iterator<CountTurnObserver> oi = pickedCharacters.iterator();
		while(oi.hasNext()){
			oi.next().notified();
		}
	}

	public boolean isPoolEmpty() {
		// TODO Auto-generated method stub
		return poolEmpty;
	}
}
