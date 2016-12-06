package ex07;

import ex06.*;

// グラフィックスウィンドウアプリケーションクラス
public class GameApp2 extends GameApp {
	Breadcrumbs bread;											// パンくずリスト

	public static void main(String[] args) {
		launch(args);
	}

	public void initGame() {
		bread = new Breadcrumbs(15, this);			// 長さを与えてパンくずリスト作成
		super.initGame();
	}

	public GameElem makeElem(int typ) {			// キャラクタ生成
		switch(typ) {
			case GameElem.PLAYER:	return new BreadPlayer(this, bread);	// 新たなプレイヤークラス
			case GameElem.ALIEN: 	return new BreadAlien(this, bread);	// 新たな敵クラス
			default: return super.makeElem(typ);												// 他は同じ生成法
		}
	}
}

// パンくず機能を追加したBreadPlayerクラス
class BreadPlayer extends Player {
	Breadcrumbs bread;

	public BreadPlayer(GameApp app, Breadcrumbs bread) {
		super(app);
		this.bread = bread;
	}

	public void move() {
		super.move();
		if (reached) bread.drop(r,c);		// ユニットを移動したらパンくず落とす
  }
}

// パンくず機能を追加したBreadAlienクラス
class BreadAlien extends Alien {
	Breadcrumbs bread;

	public BreadAlien(GameApp app, Breadcrumbs bread) {
		super(app);
		this.bread = bread;
	}

	public void nextMove() {
		nextMoveBread();
	}

	public void nextMoveBread() {					// パンくず探索による移動
		if (reached) {												// 別ユニットにぴったり到達した場合
			int tryDir = bread.trail(r, c, dir);
			if (tryDir != -1) {										// パンくず発見
				nextDir = tryDir;
			} else {														// ランダム方向転換
				nextDir = Math.random() < 0.005 ?
						(dir + (int)(1 + Math.random() * 3)) % 4 : dir;
			}
		} else {
			nextDir = dir;
		}
	}
}
