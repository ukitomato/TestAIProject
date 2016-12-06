package ex09;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.transform.Affine;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

// イメージ可視化クラス
public class Visualizer {
	int w, h;									// イメージ幅と高さ
	int r, c;									// 表示行数と列数
	double scale = 1.0;				// 表示スケール
	byte[][] imgData = null;		// 作業用
	GraphicsContext g = null;

	public Visualizer(int w, int h, int r, int c, double scale) {		//scale: 表示倍率
		this.w = w;
		this.h = h;
		this.r = r;
		this.c = c;
		this.scale = scale;
		new javafx.embed.swing.JFXPanel();		// JavaFXアプリ以外でJavaFXを使う初期化
		Platform.runLater(new Runnable() {		// ウインドウ作成
			public void run() {
				while (imgData == null) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				Stage stage = new Stage();
				Canvas canvas = new Canvas(scale * ((w+1)*c+1), scale * ((h+1)*r+1));
				StackPane pane = new StackPane();
				pane.getChildren().add(canvas);
				stage.setScene(new Scene(pane));
				stage.show();
				g = canvas.getGraphicsContext2D();
				g.setTransform(new Affine(scale, 0, 0, 0, scale, 0));	// 描画倍率
			}
		});
	}

	void draw() {		// 描画処理
		Platform.runLater(new Runnable() {				// JavaFXアプリケーションスレッドで実行
			public void run() {
				for (int i = 0; i < r; i++) {
					for (int j = 0; j < c; j++) {
						g.drawImage(getImage(i * c + j), j * (w+1) + 1, i * (h+1) + 1);
					}
				}
			}
		});
	}

	void dispDataImage(double[][] data) {				// 入出力のイメージ表示
		dispDataImage(data, data.length);
	}

	void dispDataImage(double[][] data, int n) {	// 入出力のイメージ表示（n個まで）
		imgData = new byte[n][];
		for (int i = 0; i < n; i++) {
			imgData[i] = new byte[data[i].length];
			for (int j = 0; j < data[i].length; j++) {
				imgData[i][j] = (byte)(data[i][j] * 255);
			}
		}
		draw();
	}

	void dispWeightImage(double[][] weight) {		// 重みのイメージ表示
		imgData = new byte[weight.length][];
		for (int i = 0; i < weight.length; i++) {
			imgData[i] = new byte[weight[i].length];
			double[] v = Arrays.copyOfRange(weight[i], 0, w*h);
			double mi = Double.MAX_VALUE, mx = Double.MIN_VALUE;
			for (int j = 0; j < v.length; j++) {
				if (v[j] > mx) mx = v[j];
				else if (v[j] < mi) mi = v[j];
			}
			double rg = mx - mi;
			for (int j = 0; j < v.length; j++) {
				imgData[i][j] = (byte)((v[j] - mi) / rg * 255);
			}
		}
		draw();
	}

	int grayScale(byte b) {			// 1バイトデータをグレースケールに変換
		int v = b & 0xFF;
		return (v<<16) | (v<<8) | v;
	}

	Image getImage(int idx) {		// JavaFXの扱うイメージオブジェクトに変換
		BufferedImage b = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		byte[] d = imgData[idx];
		int[] x = new int[d.length];
		for (int i = 0; i < x.length; i++) {
			x[i] = grayScale(d[i]);
		}
		b.setRGB(0, 0, w, h, x, 0, w);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ImageIO.write(b, "bmp", out);
			out.flush();
			Image img = new Image(new ByteArrayInputStream(out.toByteArray()));
			out.close();
			return img;
		} catch (IOException e) {
			System.out.println(e);
			return null;
		}
    }
}

