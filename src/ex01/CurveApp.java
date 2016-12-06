package ex01;

import javafx.application.Application;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.StackPane;
import javafx.scene.canvas.*;
import javafx.scene.transform.Affine;

// グラフィックスウィンドウアプリケーションクラス
public class CurveApp extends Application {	float scale = 1;										// 描画倍率

	// JavaFXでは main メソッドなしで実行できるが，Eclipseの実行ボタンで簡単に実行
	// できるように main メソッドは記述しておく
	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage stage) {			// 開始処理（開始時に自動的に呼ばれる）
		StackPane pane = new StackPane();
		Canvas canvas = new Canvas(600*scale, 600*scale);			// 描画キャンバス作成
		pane.getChildren().add(canvas);
		stage.setScene(new Scene(pane));
		stage.show();
		Curve.g = canvas.getGraphicsContext2D();							// 描画コンテキストを取得
		Curve.g.setTransform(new Affine(scale, 0, 0, 0, scale, 0));	// 描画倍率設定
		run();																							// 描画処理の呼び出し
	}

	void run() {												// 描画処理
		Curve c = new Curve();						// 曲線オブジェクトの生成
		c.move(50, 300);								// 開始点の設定
		c.draw(500, 0);									// 長さと角度を与えて描画してみる
	}
}

// 曲線描画の基礎クラス
class Curve {
	static GraphicsContext g;						// 描画コンテキストをCurve側に作成しておく
	double lastX = 0.0, lastY = 0.0;

	void move(double x, double y) {		// 現在位置の移動
		lastX = x;
		lastY = y;
	}

	void forward(double len, double angle) {		// 長さと角度で現在位置から線を描画
		double x = lastX + len * Math.cos(angle);
		double y = lastY + len * Math.sin(angle);
		g.strokeLine(lastX, lastY, x, y);						// 線を引いて
		move(x, y);														// 現在位置を更新
	}

	void draw(double len, double angle) {			// 描画処理
		forward(len, angle);										// ここでは前進描画するだけ
	}
}
