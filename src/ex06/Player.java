package ex06;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

// プレイヤークラス
public class Player extends GameElem {
	int lastDx = -1, lastDy = -1;
	
	public Player(GameApp app) {
		super(app);
		Rectangle s = new Rectangle(app.uw, app.uh);
		s.setArcWidth(15);
		s.setArcHeight(15);
		s.setScaleX(0.9);
		s.setScaleY(0.9);
		s.setStroke(Color.ROYALBLUE);
		s.setFill(Color.CORNFLOWERBLUE);
		setShape(s);
	}
	
	public void move() {
		int dx = 0, dy = 0;
		if (app.keyCode == null) {								// キーが離された後の状態
			if (x % app.uw != 0 || y % app.uh != 0) {		// 1マスの中間位置ならば
				dx = lastDx;												// 移動を継続
				dy = lastDy;
			} else {
				app.keyCode = null;
				return;
			}
		} else {
			switch (app.keyCode) {									// 押されているキーに対する処理
				case LEFT:			dx = -1;	break;
				case RIGHT:		dx = 1;		break;
				case UP:			dy = -1;	break;
				case DOWN:	dy = 1;		break;
				default: return;
			}
		}
		// 壁でなく他のキャラクタに衝突しなければ進む
		int x1 = x + dx, y1 = y + dy;
		if (!app.isWall(x1, y1) && app.getCollision(this, x1, y1) == -1) {
			setPos(x1, y1);
			lastDx = dx;
			lastDy = dy;
		}
	}
}
