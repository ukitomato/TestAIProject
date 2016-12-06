package ex07;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ex06.*;

// A*用ユニット情報クラス
class AStarUnit {
	int r, c;
	int open = 0;							// オープン状態
	int movement = 0;				// スタートからの移動数
	int distance = 0;					// ゴールまでの距離
	int totalCost = 0;					// コスト
	AStarUnit from = null;

	public AStarUnit(int r, int c) {
		this.r = r; this.c = c;
	}
	
	void calcCost(int targetR, int targetC) {					// コスト計算
		movement = Math.abs(r - from.r) + Math.abs(c - from.c) + from.movement;
		distance = Math.abs(r - targetR) + Math.abs(c - targetC);
		totalCost = movement + distance;
	}
}

// A*アルゴリズムエンジンクラス
public class AStar {
	GameApp app;
	int mapR, mapC;														// マップサイズ
	AStarUnit[][] aMap;
	int[][] AStarDir = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
	LinkedList<AStarUnit> openList = new LinkedList<AStarUnit>();	// オープンリスト
	Route routeLine;														// ルートのPolyline用キャラクタ

	public AStar(GameApp app) {
		this.app = app;
		routeLine = new Route(app);
		app.elems.add(routeLine);
		mapR = GameMap.r;
		mapC = GameMap.c;
		aMap = new AStarUnit[mapR][mapC]; 
		for (int i = 0; i < mapR; i++) {
			for (int j = 0; j < mapC; j++) aMap[i][j] = new AStarUnit(i, j);
		}
	}

	AStarUnit search(int r, int c, Player target){
		// Polyline用のDouble型のリスト用意
		List<java.lang.Double> a = new ArrayList<java.lang.Double>();	
		if (!searchRoute(r, c, target.r, target.c)) {			// 最適解探索
			routeLine.pos = a;
			return null;
		}
		LinkedList<AStarUnit> route = new LinkedList<AStarUnit>();
		AStarUnit unit = aMap[target.r][target.c];			// 目的地点
		while (!(unit.r == r && unit.c == c)) {				// 開始地点まで遡る
			route.addFirst(unit);											// 最適ルートに追加
			unit = unit.from;
		}
		for (AStarUnit u : route) {
			a.add(u.c * app.uw + app.uw * 0.5);				// x座標
			a.add(u.r * app.uh + app.uh * 0.5);					// y座標
		}
		routeLine.pos = a.subList(0, a.size() - 1);			// Polyline用座標に設定
		return route.getFirst();											// 最適ルートの1歩目を返す
	}

	boolean movable(int r, int c) {								// 移動可能か調べる
		if (app.map[r][c] == GameElem.WALL) return false;
		for (GameElem e : app.elems) {
			if (e.typ == GameElem.ALIEN && e.r == r && e.c == c) return false;
		}
		return true;
	}

	AStarUnit minByCost() {
		AStarUnit m = null;
		for (AStarUnit u : openList) if (m == null || m.totalCost > u.totalCost) m = u;
		return m;
	}
	
	boolean searchRoute(int startR, int startC, int targetR, int targetC) {
		for (AStarUnit[] uu : aMap) {
			for (AStarUnit u : uu) u.open = 0;					// 未オープンにしておく
		}
		AStarUnit unit = aMap[startR][startC];				// 開始ユニット
		unit.open = 1;														// オープンにする
		openList.clear();
		openList.add(unit);												// オープンリストに入れる
		while (!openList.isEmpty()) {
			AStarUnit minUnit = minByCost()	;		 		// 最小コストのものを選択
			if (minUnit.r == targetR && minUnit.c == targetC) return true;
			minUnit.open = -1;											// クローズドにする
			openList.remove(minUnit);								// オープンリストから削除
			for (int[] dir : AStarDir) {									// 周囲のユニットを調べる
				int r = minUnit.r + dir[0];
				int c = minUnit.c + dir[1];
				if (r >= 0 && c >= 0 && r < mapR && c < mapC) {
					AStarUnit around = aMap[r][c];
					if (around.open == 0 && movable(r, c)) {
						around.open = 1;									// オープンにする
						openList.addFirst(around);					// オープンリストに追加
						around.from = minUnit;						// どこから来たか記憶
						around.calcCost(targetR, targetC);		// コスト計算
					}
				}
			}
		}
		return false;
	}
}
