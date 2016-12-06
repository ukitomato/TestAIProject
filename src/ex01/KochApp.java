package ex01;

// グラフィックスウィンドウアプリケーションクラス
public class KochApp extends CurveApp {			// CurveAppクラスを継承
	public static void main(String[] args) {
		launch(args);
	}

   	void run() {											// runをカスタマイズする
		Koch c = new Koch();					// コッホ曲線オブジェクトの生成
		c.move(50, 300);							// 開始位置の設定
		c.draw(5, 500, 0);							// 再帰レベル，長さ，角度を与えて描画
	}
}

// コッホ曲線クラス
class Koch extends Curve {					// Curveクラスを継承
	void draw(int n, double len, double angle) {	// 描画処理
		if (n == 1) {									// n=1なら線を一本描く（長さと角度で）
			forward(len, angle);
		} else {												// n>1なら4回再帰呼び出しで描く
			double l = len / (2 / Math.sqrt(2.0) + 2);	// 長さの縮小
			double a = Math.PI  * 0.25;
			draw(n-1, l, angle);									// 再帰描画（直進）
			draw(n-1, l, angle-a); 								// 再帰描画（-a回転）
			draw(n-1, l, angle+a); 								// 再帰描画（+a回転）
			draw(n-1, l, angle);									// 再帰描画（直進）
		}
	}
}
