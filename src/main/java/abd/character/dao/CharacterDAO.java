package abd.character.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("characterDAO")
public class CharacterDAO {

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;
	
	private String Namespace = "abd.character.characterMapper";
	
	public List<Map<String,String>> selecCharacter(Map<String,String> paramMap) throws Exception{
		Map<String,String> paramMapCopy = new HashMap<String, String>();
		paramMapCopy.putAll(paramMap);
		
		for(String key:paramMapCopy.keySet()) {
			paramMap.remove(key,"");
		}
		return sqlSessionTemplate.selectList(Namespace+".selectCharacter",paramMap);
	}
	
	public List<Map<String,String>> selecPCharacter(Map<String,String> paramMap) throws Exception{
		Map<String,String> paramMapCopy = new HashMap<String, String>();
		paramMapCopy.putAll(paramMap);
		
		for(String key:paramMapCopy.keySet()) {
			paramMap.remove(key,"");
		}
		return sqlSessionTemplate.selectList(Namespace+".selectPCharacter",paramMap);
	}
	
	public List<Map<String,String>> selecNPCharacter(Map<String,String> paramMap) throws Exception{
		Map<String,String> paramMapCopy = new HashMap<String, String>();
		paramMapCopy.putAll(paramMap);
		
		for(String key:paramMapCopy.keySet()) {
			paramMap.remove(key,"");
		}
		return sqlSessionTemplate.selectList(Namespace+".selectNPCharacter",paramMap);
	}
	
	public List<Map<String,String>> selecCharacterLine(Map<String,String> paramMap) throws Exception{
		Map<String,String> paramMapCopy = new HashMap<String, String>();
		paramMapCopy.putAll(paramMap);
		
		for(String key:paramMapCopy.keySet()) {
			paramMap.remove(key,"");
		}
		return sqlSessionTemplate.selectList(Namespace+".selectCharacterLine",paramMap);
	}
	
	public List<Map<String,String>> selectCharacterInMap(Map<String,String> paramMap) throws Exception{
		Map<String,String> paramMapCopy = new HashMap<String, String>();
		paramMapCopy.putAll(paramMap);
		
		for(String key:paramMapCopy.keySet()) {
			paramMap.remove(key,"");
		}
		return sqlSessionTemplate.selectList(Namespace+".selectCharacterInMap",paramMap);
	}
}
