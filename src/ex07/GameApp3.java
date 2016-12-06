package ex07;

import java.util.ArrayList;
import java.util.List;

import ex06.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;

// グラフィックスウィンドウアプリケーションクラス
public class GameApp3 extends GameApp2 {
	AStarAlien astarAlien = null;

	public static void main(String[] args) {
		launch(args);
	}

	public void initGame() {
		super.initGame();
		if (astarAlien != null) astarAlien.initAStar(this);
	}

	public GameElem makeElem(int typ) {					// キャラクタ生成
		switch (typ) {
			case GameElem.PLAYER:	return new Player(this);
			case GameElem.ALIEN:
				if (astarAlien == null) {											// 追跡NPCを1つにするため
					astarAlien = new AStarAlien(this, bread);		// このNPCを追跡者にする
					astarAlien.s.setStroke(Color.BLACK);				// 枠線を強調
					return astarAlien;
				} else {
					return new Alien(this);										// 他は普通のNPC
				}
			default: return super.makeElem(typ);						// 他は同じ生成法
		}
	}
}

// Routeキャラクタクラス
class Route extends GameElem {
	public static final int ROUTE	 = -2;					// 追跡ルート

	List<java.lang.Double> pos = new ArrayList<java.lang.Double>();
	boolean visible = true;
	Polyline p = new Polyline();								// Polylineでルート表現する

	public Route(GameApp app) {
		super(app);
		p.setStrokeWidth(2);
		setShape(p);
		typ = ROUTE;
	}

	public void draw() {
		p.setVisible(visible);
		if (pos != null) {							// ルートが変更されたならPolylineを再セット
			p.getPoints().clear();				// Polylineをクリアして座標を再設定する
			p.getPoints().addAll(pos);
			pos = null;
		}
	}
}

// パンくず，A*を追加したAStarAlienクラス
class AStarAlien extends BreadAlien {
	AStar astar;														// A*エンジン
	Player targetPlayer = null;							// 追跡対象

	public AStarAlien(GameApp app, Breadcrumbs bread) {
		super(app, bread);
		astar = new AStar(app);							// A*エンジン作成
	}

	public void initAStar(GameApp app) {
		for (GameElem e : app.elems) {
			if (e.typ == GameElem.PLAYER) targetPlayer = (Player)e;	// 追跡対象設定
		}
	}

	int getDir(int nextR, int nextC) {					// 行と列から方向を得る
		int dy = nextR - r;
		int dx = nextC - c;
		for (int i = 0, n = dirOffset.length; i < n; i++) {
			if (dirOffset[i][0] == dx && dirOffset[i][1] == dy) return i;
		}
		return -1;
	}

	public void nextMove() {
		nextMoveAStar();
	}

	void nextMoveAStar() {								// A*アルゴリズムによる移動
		if (reached) {												// 別ユニットに移動するとき再探索
			nextDir = -1;
			AStarUnit unit = astar.search(r, c, targetPlayer);	// 最適ルート探索
			if (unit != null) {										// 最適ルート発見
				nextDir = getDir(unit.r, unit.c);
			}
			if (nextDir == -1) {								// ランダム方向転換
				nextDir = Math.random() < 0.005 ?
									 (dir + (int)(1 + Math.random() * 3)) % 4 : dir;
			}
		} else {
			nextDir = dir;
		}
	}
}
