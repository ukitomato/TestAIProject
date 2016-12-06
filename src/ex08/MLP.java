package ex08;

// ニューラルネットワーク（多層パーセプトロン，Multi-Layer Perceptron）
public class MLP {
	public int layerN;							// 層の数
	public int[] unitN;							// 各層のユニット数
	public double[][] unit;					// ユニット値
	public double[][][] w;					// 重み
	double[][] delta;							// 重み修正量
	int outLayer;									// 出力層の添え字
	double err;

	public MLP(int layerN, int[] unitN,double wMax) {
		// ネットワークの構築と初期化
		this.layerN = layerN;
		this.unitN = unitN;
		outLayer = layerN - 1;
		unit = new double[layerN][];
		delta = new double[layerN][];
		w = new double[layerN][][];
		for (int l = 0; l < layerN; l++) {
			int u = unitN[l];
			unit[l] = new double[u + (l == outLayer ? 0 : 1)];
			if (l < outLayer) unit[l][u] = 1.0;		// バイアス用ユニット（常に1）
			if (l > 0) {
				int v = unitN[l-1]+1;
				delta[l] = new double[u];
				w[l] = new double[u][v];
			}
		}
		for (int l = 1; l < layerN; l++) {
			for (int j = 0; j < unitN[l]; j++) {
				for (int i = 0; i < unitN[l-1]+1; i++) {
					w[l][j][i] = (Math.random()*2 - 1) * wMax;	// 重みの初期化
				}
			}
		}
	}

	double sigmoid(double x) {								 // シグモイド関数
		 return 1 / (1 + Math.pow(Math.E, -x));
	}

	void softmax(double[] x) {								// ソフトマックス関数
		if (x.length == 1) {					// 出力ユニットが1個ならシグモイド関数で
			for (int i = 0; i < x.length; i++) x[i] = sigmoid(x[i]);
		} else {
			double s = 0.0;
			for (int i = 0; i < x.length; i++) {
				x[i] = Math.exp(x[i]);
				s += x[i];
			}
			for (int i = 0; i < x.length; i++) x[i] /= s;
		}
	}

	public void forward(double[] d) {					// 順伝搬
		// 入力層の値セット
		for (int j = 0; j < unitN[0]; j++) unit[0][j] = d[j];
		// 中間層の値計算ループ
		for (int l = 0; l < outLayer-1; l++) {
			for (int j = 0; j < unitN[l+1]; j++) {
				double s = 0.0;
				for (int i = 0; i < unitN[l]+1; i++) {
					s += w[l+1][j][i] * unit[l][i];
				}
				unit[l+1][j] = sigmoid(s);
			}
		}
		// 出力層の値計算（多クラス分類）
		for (int j = 0; j < unitN[outLayer]; j++) {
			unit[outLayer][j] = 0.0;
			for (int i = 0; i < unitN[outLayer-1]+1; i++) {
				unit[outLayer][j] +=
						w[outLayer][j][i] * unit[outLayer-1][i];
			}
		}
		softmax(unit[outLayer]);
	}

	void backPropagate(double[] d, double[] t) {		// 逆伝搬
		for (int j = 0; j < unitN[outLayer]; j++) {			// 出力層
			double e = t[j] - unit[outLayer][j];
			delta[outLayer][j] = e;
			err += e * e;
		}
		for (int l = outLayer-1; l > 0; l--) {						// 中間層
			for (int j = 0; j < unitN[l]; j++) {
				double df = unit[l][j] * (1.0 - unit[l][j]);
				double s = 0.0;
				for (int k = 0; k < unitN[l+1]; k++) {
					s+= delta[l+1][k] * w[l+1][k][j];
				}
				delta[l][j] = df * s;
			}
		}
	}

	void update(double rate) {										// 重み更新
		for (int l = layerN-1; l > 0; l--) {
			for (int j = 0; j < unitN[l]; j++) {
				for (int i = 0; i < unitN[l-1]+1; i++) {
					w[l][j][i] += rate * delta[l][j] * unit[l-1][i];
				}
			}
		}
	}

	// 学習レポート用の関数型インタフェース
	public interface ReportFun { void action(int t, double err); }

	// 学習
	public void train(double[][] data, double[][] teach, int patN,
									int trainN, double learnRate, ReportFun fun) {
		for (int t = 0; t < trainN ; t++) {						// 学習ループ
			err = 0.0;
			for (int p = 0; p < patN; p++) {					// パターンループ
				forward(data[p]);										// 順伝搬
				backPropagate(data[p], teach[p]);			// 逆伝搬
				update(learnRate)	;									// 重み更新
			}
			// 学習レポート関数の呼び出し（学習回数とエラーの値を使う何らかの処理）
			if (fun != null) fun.action(t, err / patN / unitN[layerN-1]);
		}
	}

	public int getResult() {						// 最大の出力を認識結果と判定する
		double max = 0.0;
		int idx = -1;
		double[] out = unit[layerN - 1];
		for (int j = 0; j < out.length; j++) {
			if (out[j] > max) {
				max = out[j];
				idx = j;
			}
		}
		return idx;
	}
}
