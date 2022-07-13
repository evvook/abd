package abd.game.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import abd.game.GameDataLoader;

public class GamePlaySelect implements GamePlayElement{
	private String selectCode;
	private Integer numOfSelect;
	private String selectHead;
	private List<Map<String,String>> options;
	
	public GamePlaySelect(Map<String, String> playInfo, GameDataLoader loader) throws Exception {
		// TODO Auto-generated constructor stub
		List<Map<String,String>> selectList = loader.getSelectOfPlay(playInfo);
		Map<String,String> selectInfo = selectList.get(0);
		
		selectCode = selectInfo.get("SELECT_CD");
		numOfSelect = Integer.valueOf(selectInfo.get("NUM_OF_SELECT"));
		selectHead = selectInfo.get("SELECT_HEAD");
		
		options = new ArrayList<Map<String,String>>();
		List<Map<String,String>> optionList = loader.getOptionsOfSelect(selectInfo);
		Iterator<Map<String,String>> mi = optionList.iterator();
		while(mi.hasNext()) {
			Map<String,String> optionInfo = mi.next();
			Map<String,String> optionMap = new HashMap<String, String>();
			optionMap.put(optionInfo.get("OPTION_SEQ"), optionInfo.get("OPTION_TXT"));
			options.add(optionMap);
		}
	}

	@Override
	public Map<String, Object> getElContext() {
		// TODO Auto-generated method stub
		Map<String,Object> resultMap = new HashMap<String, Object>();
		resultMap.put("selectHead",selectHead);
		resultMap.put("selectOptions",options);
		resultMap.put("numOfSelect",numOfSelect);
		
		return resultMap;
	}

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return selectCode;
	}

	@Override
	public void play(Map<String, Object> commandInfo) {
		// TODO Auto-generated method stub
		
	}

}
