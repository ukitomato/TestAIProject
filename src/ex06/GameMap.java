package ex06;

// ゲームマップオブジェクト
public class GameMap {
	static int map[][];
	static String mark = "＿＃〇＠";			// マーク（壁，道，プレイヤー，NPC）
	static String[] mapData = {
		"＃＃＃＃＃＃＃＃＃＃＃＃",
		"＃＠＿＿＿＿＃＃＿＿＠＃",
		"＃＿＃＿＃＿＿＿＿＃＿＃",
		"＃＿＃＿＃＃＿＃＃＃＿＃",
		"＃＿＿＿＿＃＿＃＿＿＿＃",
		"＃＃＿＿＿＿＿＃＿＃＃＃",
		"＃＿＿＿＃＿＿＿＿＿＿＃",
		"＃＿＃＿＃＃＃＿＃＃＿＃",
		"＃＿＃＿＿＿＿＿＿＃＿＃",
		"＃＿＃＃＿＃＿＃＿＃＿＃",
		"＃＠＿＿＿＃＿＿＿＿〇＃",
		"＃＃＃＃＃＃＃＃＃＃＃＃"
	};
	public static int r = mapData.length, c = mapData[0].length();		// 行列サイズ

	static public int[][] makeMap() {
		map = new int[r][c];
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {
				map[i][j] = mark.indexOf(mapData[i].charAt(j));
			}
		}
		return map;
	}
}
