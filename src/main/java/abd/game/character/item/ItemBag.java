package abd.game.character.item;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

public class ItemBag {
	private Map<String,LinkedList<GameItem>> items;
	private Integer bagSize;
	private Integer freeSpace;
	
	public ItemBag() {
		items = new HashMap<String, LinkedList<GameItem>>();
		bagSize = 100;
		freeSpace = 100;
	}
	
	public void push(GameItem item) throws NoFreeSpaceException{
		if(freeSpace - item.getSize()<0) {
			throw new NoFreeSpaceException();
		}else {
			freeSpace = freeSpace - item.getSize();
			String itemCode = item.getCode();
			LinkedList<GameItem> itemList = items.get(itemCode);
			if(itemList == null) {
				itemList = new LinkedList<GameItem>();
			}
			itemList.add(item);
			items.put(itemCode, itemList);
		}
	}
	
	public GameItem popItem(String itemCode) {
		LinkedList<GameItem> itemList = items.get(itemCode);
		try {
			//맵에 없으면 예외처리
			if(itemList == null) {
				throw new NoSuchElementException();
			}
			GameItem item = itemList.pop();//pop에 실패하면 예외(NoSuchElementException)
			freeSpace = freeSpace + item.getSize();
			return item;
		}catch (NoSuchElementException e) {
			return null;
		}
	}
	
	public GameItem peekItem(String itemCode) {
		LinkedList<GameItem> itemList = items.get(itemCode);
		return itemList.peek();
	}
	
	public Map<String,LinkedList<GameItem>> getItems(){
		return items;
	}
	
	public boolean isEmpty() {
		return bagSize == freeSpace;
	}
}
