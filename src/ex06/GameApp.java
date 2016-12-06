package ex06;

import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

// グラフィックスウィンドウアプリケーションクラス
public class GameApp extends Application {
	public ArrayList<GameElem> elems = new ArrayList<GameElem>();	// キャラクタ配列
	public int[][] map;											// マップ情報
	public int uw = 40, uh = 40;							// ユニット幅と高さ
	Pane pane = new Pane();
	public ObservableList<Node> shapes = pane.getChildren();		// 図形の集まり
	int w, h;														// ゲーム画面の幅と高さ
	boolean active = true;									// 移動スレッド稼働フラグ
	KeyCode keyCode = null;								// キーコード

	public static void main(String[] args) {
		launch(args);
	}

	public void initGame() {							// ゲーム初期化処理
		map = GameMap.makeMap();
		elems.addAll(makeElems(map));
	}

	// キャラクタ配列生成
	public ArrayList<GameElem> makeElems(int[][] m) {
		ArrayList<GameElem> elems = new ArrayList<GameElem>();
		for (int r = 0; r < m.length; r++) {
			for (int c = 0; c < m[r].length; c++) {
				if (m[r][c] != GameElem.ROAD) {
					GameElem e = makeElem(m[r][c]);
					e.typ = m[r][c];
					e.setPos(c * uw, r * uh);
					if (e.typ == GameElem.WALL) {
						e.draw();							// 壁を描画（図形位置設定）
					} else {
						elems.add(e);					// キャラクタ配列へ追加
					}
				}
			}
		}
		return elems;
	}

	public GameElem makeElem(int typ) {		// キャラクタ生成
		switch (typ) {
			case GameElem.WALL:		return new Wall(this);
			case GameElem.PLAYER:	return new Player(this);
			case GameElem.ALIEN:		return new Alien(this);
			default:				return null;
		}
	}

	public void start(Stage stage) {				// グラフィックス開始処理
		double scale = 1.0;
		h = GameMap.r * uh;
		w = GameMap.c * uw;
		Scene scene = new Scene(pane, w*scale, h*scale);
		pane.getTransforms().add(new javafx.scene.transform.Scale(scale, scale));

		stage.setScene(scene);
		stage.show();
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {	// キー押下時の処理
			public void handle(KeyEvent e) {	keyCode = e.getCode(); }
		});
		scene.setOnKeyReleased(new EventHandler<KeyEvent>() {	// キー解放時の処理
			public void handle(KeyEvent e) {	keyCode = null; }
		});

		initGame();													// ゲーム初期化処理

		new Thread() {											// 移動計算スレッド
			public void run() {
			  	while (active) {									// 稼働フラグが真の間ループ
					for (GameElem e : elems) {
						if (e.typ >= GameElem.PLAYER) e.move();		// 移動
					}
					try {
						Thread.sleep(8);							// 速度調整
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
			 	}
			}
		}.start();

		new AnimationTimer() {							// 描画タイマー処理
			public void handle(long now) {
				for (GameElem e : elems) {				// キャラクタの描画
					if (!(e.typ==GameElem.ROAD || e.typ==GameElem.WALL)) e.draw();
				}
		  }
		}.start();
	}

	public void stop() { active = false; }			// 終了時の処理

	public boolean isWall(int x, int y) {			// 壁にぶつかるか？
	  	int r1 = y / uh;
	  	int c1 = x / uw;
	  	int r2 = (y + uh - 1) / uh;
	  	int c2 = (x + uw - 1) / uw;
	  	return map[r1][c1] == GameElem.WALL || map[r1][c2] == GameElem.WALL ||
	  				map[r2][c1] == GameElem.WALL || map[r2][c2] == GameElem.WALL;
	}

	public int getCollision(GameElem me, int x, int y) {		// 衝突検出
		for (GameElem other : elems) {
			if (other.typ > 1 && other != me) {
				if (Math.abs(other.x - x) < uw && Math.abs(other.y - y) < uh) {
					return other.typ;
				}
			}
		}
		return -1;
	}
}
