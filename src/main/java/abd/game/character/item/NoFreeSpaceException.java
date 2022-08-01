package abd.game.character.item;

public class NoFreeSpaceException extends Exception {
	public String toString() {
		return "가방에 물건을 담을 공간이 부족합니다.";
	}
}
