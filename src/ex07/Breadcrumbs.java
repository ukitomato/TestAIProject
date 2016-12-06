package ex07;

import ex06.*;

import java.util.LinkedList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

// 位置情報クラス
class Pos {
	int r, c;
	public Pos(int r, int c) {
		this.r = r; this.c = c;
	}

	public boolean equals(Pos p) {
		return this.r == p.r && this.c == p.c;
	}
}

// パンくず拾いクラス
public class Breadcrumbs {
	int len;
	GameApp app;
	Bread[] bread;
	int[][] map;																			// パンくずが置かれたマップ
	LinkedList<Pos> pList = new LinkedList<Pos>();		// パンくずの位置リスト
	int[][] dirOffset = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1} };
	int[] tryPlan = { 0, 1, 3, 2 };												// 前，右，左，後の順で調べる

	public Breadcrumbs(int len, GameApp app) {
		this.len = len;
		this.app = app;
		bread = new Bread[len];
		for (int i = 0; i < len; i++) bread[i] = new Bread(app);
		map = new int[GameMap.r][GameMap.c];					// パンくずマップ作成（行，列）
		for (int i = 0; i < len; i++) app.elems.add(bread[i]);		// 描画対象キャラクタに追加
	}

	void drop(int r, int c) {											// パンくずを落とす
		if (pList.size() > 0) {
			Pos last  = pList.getLast();
			if (r == last.r && c == last.c) return;			// 移動していない
		}
		if (pList.size() >= len) {										// パンくず長さを超えたら
			Pos pos = pList.removeFirst();						// 古い位置を取り出して
			map[pos.r][pos.c] = 0;									// マップから消す
		}
		Pos p = new Pos(r, c);
		if (map[r][c] != 0) {
			int idx = -1;
			for (int i = 0; i < pList.size(); i++) {
				if (pList.get(i).equals(p)) idx = i;
			}
			if (idx != -1) pList.remove(idx);					// 既にあればまず消す
		}
		pList.addLast(p);												// 新たな位置をリストに追加
		map[r][c] = 1;													// マップに置く
		plotBread();
	}

	void plotBread() {													// グラフィックス要素位置設定
		int i = 0;
		for (Pos p : pList) {
			int x = (int)((p.c + 0.5) * app.uw - 2);
			int y = (int)((p.r + 0.5) * app.uh - 2);
			bread[i++].setPos(x, y);
		}
	}

	int trail(int r, int c, int dir) {									// パンくずの方向を探す
		for (int i = 0; i < 3; i++) {
			int tryDir = (dir + tryPlan[i]) % 4;
			int[] d = dirOffset[tryDir];
			if (map[r+d[1]][c+d[0]] == 1) return tryDir;	// 見つかった
		}
		return -1;															// 見つからなかった
	}
}

// パンくずキャラクタクラス
class Bread extends GameElem {
	public static final int BREAD		= -1;				// パンくず

	public Bread(GameApp app) {
		super(app);
		Rectangle s = new Rectangle(4, 4);
		s.setFill(Color.CORNFLOWERBLUE);
		setShape(s);
		typ = BREAD;
		setPos(-100, -100);											// 見えない場所に置いておく
	}
}
