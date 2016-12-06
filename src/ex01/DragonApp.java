package ex01;

// グラフィックスウィンドウアプリケーションクラス
public class DragonApp extends CurveApp {		// CurveAppクラスを継承
	public static void main(String[] args) {
		launch(args);
	}

	void run() {												// runをカスタマイズする
		Dragon c = new Dragon(); 				// ドラゴン曲線オブジェクトの生成
		c.move(150, 300); 								// 開始位置の設定
		c.draw(13, 300, 0, 1); 						// 再帰レベル，長さ，角度，スイッチを与えて描画
	}
}

// ドラゴン曲線クラス
class Dragon extends Curve {					// Curveクラスを継承
	void draw(int n, double len, double angle, int sw) {	// 描画処理
		if (n == 1) {										// n=1なら線を一本描く（長さと角度で）
			forward(len, angle);
		} else {																// n>1なら2回再帰呼び出しで描く
			double l = len / (2 / Math.sqrt(2.0));		// 長さの縮小
			double a = Math.PI * 0.25 * sw;				// 角度の計算（swで+-反転）
			draw(n-1, l, angle-a, 1); 							// 再帰描画（-a回転）
			draw(n-1, l, angle+a, -1); 						// 再帰描画（+a回転，sw反転）
		}
	}
}
