package abd.game.character;

public interface Playerable {
	
	public void act(GameCharacter other);
	
	public void takeFight(NPCharacter npc) throws Exception;
	public void takeCure(NPCharacter npc);
	public void runAwayFrom(NPCharacter npc);
	
	public Integer getLevel();
	
	public void setLvlStatus(String level, String hp, String att, String requiredXp);
	
	public void levelUp() throws Exception;

	public void setJob(String job);

	public String getJob();
}
