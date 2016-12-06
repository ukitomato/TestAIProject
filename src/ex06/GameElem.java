package ex06;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.paint.Color;

// キャラクター基本要素クラス
public  class GameElem  {
	public GameApp app;
	public int typ, dir, x, y, r, c;						// 種類，方向，座標，行，列
	public boolean reached = false;				// ユニットにぴったり到達したか
	Shape shape = null;									// グラフィックス要素

	public static final int ROAD		= 0;		// 通路
	public static final int WALL			= 1;		// 壁
	public static final int PLAYER		= 2;		// プレイヤー
	public static final int ALIEN		= 3;		// 敵

	public GameElem(GameApp app) {
		this.app = app;
	}

	public void setShape(Shape shp) {
		shape = shp;
		app.shapes.add(shape);						// 図形追加
	}

	public void setPos(int x1, int y1) {			// 位置更新
		x = x1; y = y1;
		r = y / app.uh; c = x / app.uw;
		reached = (y % app.uh == 0 && x % app.uw == 0);
	}

	public void draw() {									// 描画処理（シーングラフにアクセス）
		shape.setLayoutX(x);							// 図形座標を変更
		shape.setLayoutY(y);
	}

	public void move() {}
}

// 壁のクラス
class Wall extends GameElem {
	public Wall(GameApp app) {
		super(app);
		Rectangle s = new Rectangle(app.uw, app.uh);
		s.setFill(Color.DARKGRAY);
		setShape(s);
	}
}
