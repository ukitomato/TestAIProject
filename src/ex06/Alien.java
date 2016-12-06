package ex06;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

// ノンプレイヤークラス
public class Alien extends GameElem {
	public int nextDir = 0;
	public int[][] dirOffset = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 }};
	public Rectangle s;
	int[] tryPlan = { 1, 3, 2 };							// 右，左，後

	public Alien(GameApp app) {
		super(app);
		s = new Rectangle(app.uw, app.uh);
		s.setArcWidth(15);
		s.setArcHeight(15);
		s.setScaleX(0.9);
		s.setScaleY(0.9);
		s.setStroke(Color.FIREBRICK);
		s.setFill(Color.INDIANRED);
		setShape(s);
	}
	
	public void nextMove() {							// 移動先を決定する
		nextMoveRandom();
	}
	
	public void nextMoveRandom() {			// ランダム方向転換による移動
		nextDir = Math.random() < 0.005 ?
							(dir + 1 + (int)((Math.random() * 2) + 0.5)) % 4 : dir;
	}

	public void moveExec() {				// 可能であれば移動実行
		int x1 = x + dirOffset[nextDir][0];
		int  y1 = y + dirOffset[nextDir][1];
		if (app.isWall(x1, y1) || app.getCollision(this, x1, y1) != -1) {
			// 後ろに転換する確率は下げる
			nextDir = (nextDir + tryPlan[(int)(Math.random() * 2.1)]) % 4;
			x1 = x + dirOffset[nextDir][0];
			y1 = y + dirOffset[nextDir][1];
		}
		if (!app.isWall(x1, y1) && app.getCollision(this, x1, y1) == -1) {
			dir = nextDir;						// 壁でなく他のキャラクタに衝突しなければ進む
			setPos(x1, y1);
		}
	}
	
	public void move() {
		nextMove();								// 次の移動先を決定する
		moveExec();								// 移動実行
	}
}
