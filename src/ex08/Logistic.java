package ex08;

// ロジスティック回帰クラス
public class Logistic {
	int n, m;								// 入力数，出力数
	double[][] w;						// 重み
	double[] b;							// バイアス
	double[][] wDelta;				// 重み修正値
	double[] bDelta;				// 中間層修正値
	double[] bTmp;					// 中間層作業変数
	double[] out;						// 出力層
	double[] err;						// 出力差

	public Logistic(int n, int m) {
		this.n = n; this.m = m;
		w = new double[m][n];
		b = new double[m];
		wDelta = new double[m][n];
		bDelta = new double[m];
		bTmp = new double[m];
		out = new double[m];
		err = new double[m];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) w[j][i] = (Math.random() * 2 - 1) * 0.01;
		}
		for (int j = 0; j < m; j++) b[j] = 0.0;
	}

	void softmax(double[] x) {											// ソフトマックス関数
		double s = 0.0;
		for (int i = 0; i < m; i++) { x[i] = Math.exp(x[i]); s += x[i]; }
		for (int i = 0; i < m; i++) x[i] /= s;
	}

	void forward(double[] data) {										// 出力計算
		for (int j = 0; j < m; j++) {
			out[j] = b[j];
			for (int i = 0; i < n; i++) out[j] += w[j][i] * data[i];
		}
		softmax(out);
	}

	void accumDelta(double[] x, double[] t) {					// 修正量加算
		for (int j = 0; j < m; j++) {
			double err = t[j] - out[j];
			bDelta[j] += err;
			for (int i = 0; i < n; i++)  wDelta[j][i] += err * x[i]; 
		}
	}

	void train(double[][] data, double[][] teach,
				int patN, int trainN, int batchSize, double learnRate) {		// 学習
		double rate = learnRate / batchSize;
		for (int t = 0; t < trainN * patN / batchSize; t++) {		// 学習ループ
			for (int j = 0; j < m; j++) {												// 修正量初期化ループ
				bDelta[j] = 0.0; 
				for (int i = 0; i < n; i++) wDelta[j][i] = 0.0;	
			}
			for (int i = 0; i < batchSize; i++) {								// バッチループ
				int idx = (t * batchSize + i) % patN;
				forward(data[idx]); 													// 出力計算
				accumDelta(data[idx], teach[idx]);							// 修正量加算
			}
			for (int j = 0; j < m; j++) {												// 修正量適用ループ
				b[j] += rate * bDelta[j];
				for (int i = 0; i < n; i++) w[j][i] += rate * wDelta[j][i];
			}
		}
	}
	
	int getResult() {						// 最大の出力を認識結果と判定する
		double max = 0.0;
		int idx = -1;
		for (int j = 0; j < m; j++) if (out[j] > max) { max = out[j]; idx = j; }
		return idx;
	}
}
