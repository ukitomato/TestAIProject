package ex09;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class Data {
	public static byte[][] imgData;
	public static byte[] labelData;
	static int w = 0;
	static int h = 0;
	static byte[] buf = new byte[4];

	// int（32bit）データを読み込む
	static int readInt(BufferedInputStream st) throws IOException {
		st.read(buf);													// 4バイト配列に読み込む
		return ByteBuffer.wrap(buf).getInt();			// 4バイト配列を1個のInt型に変換
	}

	static void readFile(String fileName, Consumer<BufferedInputStream> fun) {
		BufferedInputStream st = null;
		try {
			st = new BufferedInputStream(new FileInputStream(fileName));
			readInt(st);						// ストリームからmagic numberを読み飛ばしておく
			fun.accept(st);				// funにストリームを渡してそれぞれの処理をさせる
		} catch (Exception e) {
			System.out.println(e);
		} finally { 
			if (st != null) {
				try {
					st.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	static double[][] readImage(String fileName, int m) {	// イメージの読み込み
		readFile(fileName, st -> {
			try {
				int num = readInt(st);				// イメージ数
				h = readInt(st);							// 縦サイズ
				w = readInt(st);							// 横サイズ
				int n = m > 0 ? m : num;
				int size = h * w;
				imgData = new byte[n][size];
				for (int i = 0; i < n; i++) {
					st.read(imgData[i], 0, size);
				}
				System.out.printf("Image loaded: %d/%d\n", n, num);
			} catch (IOException e) {
                throw new UncheckedIOException(e);
			}
		});
		return getData(imgData);						// パターン数 × イメージ(Double型配列)を返す
	}

	static double[][] readLabel(String fileName, int m) {	// ラベルの読み込み
		readFile(fileName, st -> {
			try {
				int num = readInt(st);					// ラベル数
				int n = m > 0 ? m : num;
				labelData = new byte[n];
				st.read(labelData, 0, n);
				System.out.printf("Label loaded: %d/%d\n", n, num);
			} catch (IOException e) {
                throw new UncheckedIOException(e);
			}
		});
		return getLabel(labelData);					// パターン数 × ラベル(Double型配列)を返す
	}

	static double[][] getData(byte[][] img) {	// イメージデータを0.0-1.0のレンジに変換する
		double[][] d = new double[img.length][];
		for (int i = 0; i < img.length; i++) {
			d[i] = new double[img[i].length];
			for (int j = 0; j < img[i].length; j++) {
				d[i][j] = (img[i][j] & 0xFF) / 255.0;
			}
		}
		return d;
	}

	static double[][] getLabel(byte[] label) {	// ラベルデータ1個を0~9に対応させた配列にする
		double[][] d = new double[label.length][];
		for (int i = 0; i < label.length; i++) {
			byte v = labelData[i];
			d[i] = new double[10];
			d[i][v] = 1.0;
		}
		return d;
	}
}