package ex06;

// グラフィックスウィンドウアプリケーションクラス
public class BoidApp2 extends BoidApp {
	public static void main(String[] args) {
		launch(args);
	}

	public void init() {																			// 初期化処理
		for (int i = 0; i < count; i++) boids[i] = new Boid2(this);		// 全個体生成
	}
}

// Boid2個体クラス（Boidから派生）
class Boid2 extends Boid {
	static double chohesionRate = 0.01;			// 結合パラメータ（群れの中心に向かう強さ）
	static double separationDis = 25;				// 分離パラメータ（ぶつからないための距離）
	static double alignmentRate = 0.5;				// 整列パラメータ（群れ合わせる強さ）
	static int speedLimit = 8;

	public Boid2(BoidApp2 app) {
		super(app);
	}

	public void moveDecide() {				// 移動量決定処理を置き換える
		chohesion();									// 結合（群れの中心に向かう）
		separation();									// 分離（ぶつからないよう距離をとる）
		alignment	();									// 整列（群れと同じ方向と速度に合わせる）
		double rate = Math.sqrt(dx*dx + dy*dy) / speedLimit;
		if (rate > 1.0) {								// 速度制限
			dx /= rate; dy /= rate;
		}
	}

	void chohesion() {							// 結合（群れの中心に向かう）
		double cx = 0, cy = 0;
		for (Boid b : app.boids) { cx += b.x; cy += b.y; }
		cx /= app.count; cy /= app.count;
		dx += (cx - x) * chohesionRate;
		dy += (cy - y) * chohesionRate;
	}

	void separation() {							// 分離（ぶつからないよう距離をとる）
		for (Boid b : app.boids) {
			if (b != this) {
				double ax = b.x - x, ay = b.y - y;
				double dis = Math.sqrt(ax*ax + ay*ay);
				if (dis < separationDis) {
					dx -= ax; dy -= ay;
				}
			}
		}
	}

	void alignment() {								// 整列（群れと同じ方向と速度に合わせる）
		double ax = 0, ay = 0;
		for (Boid b : app.boids) { ax += b.dx; ay += b.dy; }
		ax /= app.count; ay /= app.count;
		dx += (ax - dx) * alignmentRate;
		dy += (ay - dy) * alignmentRate;
	}
}
