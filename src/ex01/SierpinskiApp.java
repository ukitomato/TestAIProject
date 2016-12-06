package ex01;

// グラフィックスウィンドウアプリケーションクラス
public class SierpinskiApp extends CurveApp {	// CurveAppクラスを継承
	public static void main(String[] args) {
		launch(args);
	}
	
	void run() {													// runをカスタマイズする
		Sierpinski c = new Sierpinski(); 			// シェルピンスキー曲線オブジェクトの生成
		c.draw(6, 300, 300, 150); 						//再帰レベル，開始位置，長さを与えて描画
	}
}

// シェルピンスキー曲線クラス
class Sierpinski extends Curve {										// Curveを継承
	void draw(int n, double len, double x, double y) {	// 描画処理
		double l = len / 2;
		double x1 = x - l;
		double x2 = x + l;
		double y1 = y + l * Math.sqrt(3);
		if (n == 1) {											// n=1なら線を3本使って三角形を描く
			g.strokeLine(x, y, x1, y1);
			g.strokeLine(x1, y1, x2, y1);
			g.strokeLine(x2, y1, x, y);
		} else {														// n>1なら3角形三つを再帰呼び出しで描く
			double l2 = l / 2;		 										// 長さの縮小
			draw(n-1, l, x, y); 												// 再帰（上の三角形）
			draw(n-1, l, x - l2, y + l2 * Math.sqrt(3)); 		// 再帰（左下の三角形）
			draw(n-1, l, x + l2, y + l2 * Math.sqrt(3)); 		// 再帰（右下の三角形）
		}
	}
}
