package ex06;

// グラフィックスウィンドウアプリケーションクラス
public class BoidApp1 extends BoidApp {
	public static void main(String[] args) {
		launch(args);
	}
	
	public void init() {																			// 初期化処理
		for (int i = 0; i < count; i++) boids[i] = new Boid1(this);		// 全個体生成
	}
}

// Boid1個体クラス（Boidから派生）
class Boid1 extends Boid {
	public Boid1(BoidApp1 app) {
		super(app);
	}

	public void moveDecide() {															// ランダムに方向変換
		if (Math.random() < 0.05) dx = (int)(Math.random() * 2.0 + 0.5) - 1;
		if (Math.random() < 0.05) dy = (int)(Math.random() * 2.0 + 0.5) - 1;		
	}
}
