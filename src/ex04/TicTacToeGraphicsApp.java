package ex04;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;

// TicTacToeから派生させたグラフィックスバージョンクラス
class TicTacToeGraphics extends TicTacToe {
	TicTacToeGraphicsApp app;
	int selR = -1, selC = -1;									// マウス選択位置（行,列）

	public TicTacToeGraphics(TicTacToeGraphicsApp app) {
		this.app = app;
	}

	void human(char p) {
		selR = -1;														// 未選択状態にしておく
		while (selR == -1) {										// マウス選択を待つ
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (!playing) return;									// ゲーム強制終了なら戻る
		}
		if (bd[selR][selC] != '　') human(p);				// 空白でないので再試行
		else bd[selR][selC] = p;									// 空白ならマーク代入
		System.out.println("human   :" + p + " = " + selR + "," + selC);
	}

	void disp() {
		Platform.runLater(new Runnable() {				// JavaFXアプリケーションスレッドで実行
			public void run() {
				app.draw(bd); 										// 描画処理
			}
		});
	}
}

// グラフィックスウィンドウアプリケーションクラス
public class TicTacToeGraphicsApp extends Application {
	int w = 300, h = 300;
	GraphicsContext g;											// 描画コンテキスト
	TicTacToeGraphics game;									// ゲームオブジェクト

	public static void main(String[] args) {
		launch(args);
	}

	public void initGame() {
		game =  new TicTacToeGraphics(this);
	}

	public void start(Stage stage) {						// 開始処理
		Canvas canvas = new Canvas(w, h);			// 描画キャンバス作成
		g = canvas.getGraphicsContext2D();
		StackPane pane = new StackPane();
		pane.getChildren().add(canvas);
		stage.setScene(new Scene(pane));
		stage.show();

		new Thread() {												// ゲームスレッド生成
			public void run() {
				initGame();
				game.play();											// ゲームスレッド内でゲーム実行
			}
		}.start();															// ゲームスレッド開始

		pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {			// クリック時の処理
				if (game.selR == -1) {							// 未選択状態なら
					game.selC = (int)(e.getX() / w * 3);
					game.selR = (int)(e.getY() / h * 3);
				}
			}
		});
	}

	public void stop() {	game.playing = false; }		// 終了させる（継続フラグオフ）

	void draw(Character[][] bd) {								// ゲーム盤描画
		int dw = w / 3, dh = h / 3;
		g.clearRect(0, 0, w, h);
		for (int i = - 1; i < 3; i++) {
			int x = i * dw, y = i * dh;
			g.strokeLine(x, 0, x, h);
			g.strokeLine(0, y, w, y);
		}
		double mw = dw * 0.6, mh = dh * 0.6;
		for (int r = 0; r < 3; r++) {
			for (int c = 0 ; c < 3; c++) {
				if (bd[r][c] != '　') {
					double x1 = c*dw + (dw-mw)*0.5, y1 = r*dh + (dh-mh)*0.5;
					double x2 = x1 + mw, y2 = y1 + mh;
					if (bd[r][c] == '○') {
						g.strokeOval(x1, y1, mw, mh);
					} else if (bd[r][c] == '×') {
						g.strokeLine(x1,y1,x2,y2);
						g.strokeLine(x2,y1,x1,y2);
					}
				}
			}
		}
	}
}