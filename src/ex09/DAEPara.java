package ex09;

import java.util.function.Consumer;
import java.util.stream.IntStream;

// 並列デノイジングオートエンコーダクラス(Denoising AutoEncoder)
public class DAEPara extends DAE {
	int paraN;

	void loopPara(int n, Consumer<Integer> body) {
		int t = (int)Math.ceil((double)n / paraN);
		IntStream.range(0, paraN).parallel().forEach(i -> {
			int st = i*t, ed = Math.min((i+1)*t, n);
			for (int j = st; j < ed; j++) {
				body.accept(j);
			}
		});
	}

	public DAEPara(int n, int m, int paraN) {
		super(n, m);
		this.paraN = paraN;
	}
	
	void encode(double[] x, double[] y) {			// エンコード
		loopPara(m, j -> {
			double s = 0.0;
			for (int i = 0; i < n; i++) s += w[j][i] * x[i];
			y[j] = sigmoid(s + b1[j]);
		});
	}

	void decode(double[] y, double[] z) {			// デコード
		loopPara(n, i -> {
			double s = 0.0;
			for (int j = 0; j < m; j++) s += w[j][i] * y[j];
			z[i] = sigmoid(s +  + b2[i]);
		});
	}

	void initDelta() {												// 誤差修正値初期化
		for (int i = 0; i < n; i++) b2Delta[i] = 0.0;
		loopPara(m, j -> {
			b1Delta[j] = 0.0;
			for (int i = 0; i < n; i++) wDelta[j][i] = 0.0;
		});
	}

	void accumDelta(double[] x, double[] nx) {	// 誤差修正値蓄積
		for (int i = 0; i < n; i++) {
			b2Tmp[i] = x[i] - z[i];
			b2Delta[i] += b2Tmp[i];
		}
		loopPara(m, j -> {
			double s = 0.0;
			for (int i = 0; i < n; i++) s += w[j][i] * b2Tmp[i];
			b1Tmp[j] = s * y[j] * (1 - y[j]);
			b1Delta[j] += b1Tmp[j];
			for (int i = 0; i < n; i++) wDelta[j][i]+= b1Tmp[j] * nx[i] + b2Tmp[i] * y[j];
		});
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
			loopPara(m, j -> {
				b1[j] += lRate * b1Delta[j];	 												// 中間層バイアス修正
				for (int i = 0; i < n; i++)
					w[j][i] += lRate * wDelta[j][i] - wgtDec * w[j][i]; 			// 重み修正
			});
		}
	}
}