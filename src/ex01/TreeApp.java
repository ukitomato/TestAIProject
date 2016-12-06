package ex01;

// グラフィックスウィンドウアプリケーションクラス
public class TreeApp extends CurveApp {		// CurveAppクラスを継承
	public static void main(String[] args) {
		launch(args);
	}

	void run() {														// runをカスタマイズする
		Tree c = new Tree(); 									// ツリー曲線オブジェクトの生成
		c.move(300, 600); 										// 開始位置の設定
		c.draw(7, 450, Math.PI * -0.5, 1); 			// 再帰レベル，長さ，角度を与えて描画
	}
}

// ツリー曲線クラス
class Tree extends Curve {								// Curveクラスを継承
	void draw(int n, double len, double angle, int sw) {		// 描画処理
		double x = lastX;
		double y = lastY;
		if (n == 1) {												// n=1なら線を一本描く（長さと角度で）
			forward(len, angle);
		} else {															// n>1なら3回再帰呼び出しで描く
			double l = len / (2/Math.sqrt(2.0));	// 長さの縮小
			double a = Math.PI * 0.15 * sw;			// 角度の計算（swで＋－反転）
			forward(l*0.33, angle);							// 直進で線を描く
			draw(n-1, l*0.8, angle-a, 1);					// 再帰描画（-a回転）

			forward(l*0.33, angle); 							// 直進で線を描く
			draw(n-1, l*0.7, angle+a*1.5, -1);		// 再帰描画（-a回転，sw反転）

			forward(l*0.33, angle); 							// 直進で線を描く
			draw(n-1, l*0.6, angle, 1); 					// 再帰描画（直進）
		}
		lastX = x;
		lastY = y;
	}
}
