package ex09;

// デノイジングオートエンコーダクラス(Denoising AutoEncoder)
public class DAE extends AE {
	double[] nx;								// 入力層（ノイズ付加用）

	public DAE(int n, int m) {
		super(n, m);
		nx = new double[n];
	}
	
	void accumDelta(double[] x, double[] nx) {		// 誤差修正値蓄積
		for (int i = 0; i < n; i++) {
			b2Tmp[i] = x[i] - z[i];
			b2Delta[i] += b2Tmp[i];
		}
		for (int j = 0; j < m; j++) {
			double s = 0.0;
			for (int i = 0; i < n; i++) s += w[j][i] * b2Tmp[i];
			b1Tmp[j] = s * y[j] * (1 - y[j]);
			b1Delta[j] += b1Tmp[j];
			for (int i = 0; i < n; i++) wDelta[j][i]+= b1Tmp[j] * nx[i] + b2Tmp[i] * y[j];
		}
	}

	void addNoise(double[] x, double[] nx, double noiseRate) {
		for (int i = 0; i < n; i++) nx[i] = Math.random() <= noiseRate ? 0 : x[i];
	}

	void trainPattern(double[] x, double noiseRate) {		// ノイズを付加した学習
		addNoise(x, nx, noiseRate);											// ノイズ付加データの作成
		reconstruct(nx);
		accumDelta(x, nx);
	}

	void train(double[][] data, int patN, int trainN, int batchSize, 	// 学習
			double lRate, double noiseRate, double wgtDec) { 
		for (int t = 0; t < trainN * patN / batchSize; t++) {					// 学習ループ
			initDelta();
			for (int i = 0; i < batchSize; i++) {											// ミニバッチループ
				int idx = (t * batchSize + i) % patN;
				trainPattern(data[idx], noiseRate);
			}
			for (int i = 0; i < n; i++) b2[i] += lRate * b2Delta[i]; 			// 出力層バイアス修正
			for (int j = 0; j < m; j++) {
				b1[j] += lRate * b1Delta[j];	 												// 中間層バイアス修正
				for (int i = 0; i < n; i++)
					w[j][i] += lRate * wDelta[j][i] - wgtDec * w[j][i]; 			// 重み修正
			}
		}
	}
}
