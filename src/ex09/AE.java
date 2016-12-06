package ex09;

import java.util.Arrays;

// オートエンコーダクラス（AutoEncoder）
public class AE {
	int n;													// 入出力ユニット数
	int m;													// 中間ユニット数
	double[] y;											// 中間層
	double[] z;											// 出力層
	double[][] w;										// ランダムな重み
	double[] b1;										// バイアス1
	double[] b2;										// バイアス2
	double[][] wDelta;								// 重み修正値
	double[] b1Delta;								// 中間層修正値
	double[] b2Delta;								// 出力層修正値
	double[] b1Tmp;								// 中間層作業変数
	double[] b2Tmp;								// 出力層作業変数

	public AE(int n, int m) {
		this.n = n; this.m = m;
		y = new double[m];
		z = new double[n];
		w = new double[m][n];
		for (int j = 0; j < m; j++) {
			for (int i = 0; i < n; i++) w[j][i] = (Math.random() * 2 - 1) * 0.01;
		}	
		b1 = new double[m];
		b2 = new double[n];
		Arrays.fill(b1, 0.0);
		Arrays.fill(b2, 0.0);
		wDelta = new double[m][n];
		b1Delta = new double[m];
		b2Delta = new double[n];
		b1Tmp = new double[m];
		b2Tmp = new double[n];
	}
	
	double sigmoid(double x) {							// シグモイド関数
		return 1 / (1 + Math.pow(Math.E, -x));	
	}

	void encode(double[] x, double[] y) {		// エンコード
		for (int j = 0; j < m; j++) {
			double s = 0.0;
			for (int i = 0; i < n; i++) s += w[j][i] * x[i];
			y[j] = sigmoid(s + b1[j]);
		}
	}

	void decode(double[] y, double[] z) {		// デコード
		for (int i = 0; i < n; i++) {
			double s = 0.0;
			for (int j = 0; j < m; j++) s += w[j][i] * y[j];
			z[i] = sigmoid(s +  + b2[i]);
		}
	}

	void reconstruct(double[] data) {				// 入力信号の再構築
		encode(data, y);										// エンコード
		decode(y, z);												// デコード
	}

	void initDelta() {											// 誤差修正値初期化
		for (int i = 0; i < n; i++) b2Delta[i] = 0.0;
		for (int j = 0; j < m; j++) {
			b1Delta[j] = 0.0;
			for (int i = 0; i < n; i++) wDelta[j][i] = 0.0;
		}
	}

	void accumDelta(double[] x) {					// 誤差修正値蓄積
		for (int i = 0; i < n; i++) {
			b2Tmp[i] = x[i] - z[i];
			b2Delta[i] += b2Tmp[i];
		}
		for (int j = 0; j < m; j++) {
			double s = 0.0;
			for (int i = 0; i < n; i++) s+= w[j][i] * b2Tmp[i];
			b1Tmp[j] = s * y[j] * (1 - y[j]);
			b1Delta[j] += b1Tmp[j];
			for (int i = 0; i < n; i++) wDelta[j][i] += b1Tmp[j] * x[i] + b2Tmp[i] * y[j];
		}
	}

	void train(double[][] data, int patN, int trainN, int batchSize, double lRate) {	// 学習
		for (int t = 0; t < trainN * patN / batchSize; t++) {					// 学習ループ
			initDelta();
			for (int i = 0; i < batchSize; i++) {											// ミニバッチループ
				int idx = (t * batchSize + i) % patN;
				reconstruct(data[idx]);
				accumDelta(data[idx]);
			}
			for (int i = 0; i < n; i++) b2[i] += lRate * b2Delta[i]; 			// 出力層バイアス修正
			for (int j = 0; j < m; j++) {
				b1[j] += lRate * b1Delta[j];	 												// 中間層バイアス修正
				for (int i = 0; i < n; i++) w[j][i] += lRate * wDelta[j][i]; 	// 重み修正
			}
		}
	}
}
