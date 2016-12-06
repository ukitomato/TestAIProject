package ex06;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

// グラフィックスウィンドウアプリケーションクラス
public class BoidApp extends Application {
	int w = 800;																// ウィンドウ内部の幅
	int h = 800;																	// ウィンドウ内部の高さ
	int count = 30;															// 個体数
	int range = 100;															// 個体発生位置範囲
	Pane pane = new Pane();
	ObservableList<Node> shapes = pane.getChildren();	// 全図形
	Boid[] boids = new Boid[count];										// 全個体
	boolean active = true;															// スレッド継続フラグ

	public static void main(String[] args) {
		launch(args);
	}
	
	public void init() {														// 初期化処理
		for (int i = 0; i < count; i++) boids[i] = new Boid(this);
	}

	public void start(Stage stage) {								// 開始処理
		stage.setScene(new Scene(pane, w, h));
		stage.show();

		new Thread() {														// 移動計算スレッド
			public void run() {
				while (active) {
					for (Boid b : boids) {
			  			b.moveDecide();									// 移動量決定
			  			b.move();												// 移動位置計算
					}
					try {
						Thread.sleep(15);									// 速度調整
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			 	}
			}
		}.start();

		new AnimationTimer() {										// 描画タイマー処理
			public void handle(long now) {
				for (Boid b : boids) {
					b.draw();														// 描画
				}
			}
		}.start();
	}

	public void stop() { active = false; }		// 終了時の処理（スレッド継続フラグオフ）
}

// Boid個体クラス
class Boid {
	BoidApp app;
	int x, y;
	double dx = 1.0, dy = 1.0;
	Rectangle shape = new Rectangle(10, 10);			// 図形生成

	public Boid(BoidApp app) {
		this.app = app;
		app.shapes.add(shape);										// 図形追加
		x = (int)(app.w / 2 + app.range * (Math.random() - 0.5));
		y = (int)(app.h / 2 + app.range * (Math.random() - 0.5));
	}

	public void moveDecide() {
		dx = (int)(Math.random() * 2.0 + 0.5) - 1;				// ランダム移動方向
		dy = (int)(Math.random() * 2.0 + 0.5) - 1;
	}

	public void move() {
		x += dx; y += dy;
		if (x < 0 || x >= app.w) { dx = -dx; x += dx * 2; }	// 壁なら方向転換
		if (y < 0 || y >= app.h) { dy = -dy; y += dy * 2; }
	}

	public void draw() {
		shape.setX(x - 5);														// 図形座標を変更
		shape.setY(y - 5);
	}
}
