package ex08;

import java.util.function.Consumer;
import java.util.stream.IntStream;

// 多層パーセプトロンクラス（並列演算バージョン）
public class MLPPara extends MLP {
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

	public MLPPara(int layerN, int[] unitN, double wMax, int paraN) {
		super(layerN, unitN, wMax);
		this.paraN = paraN;
	}

	void softmax(double[] x) {								// ソフトマックス関数
		double s = 0.0;
		for (int i = 0; i < x.length; i++) {
			x[i] = Math.exp(x[i]);
			s += x[i];
		}
		for (int i = 0; i < x.length; i++) x[i] /= s;
	}

	public void forward(double[] d) {									// 順伝搬
		for (int j = 0; j < unitN[0]; j++) unit[0][j] = d[j];		// 入力層
		for (int l = 0; l < outLayer-1; l++) {								// 中間層
			final int ll = l;
			loopPara(unitN[l+1],  j -> {
				double s = 0.0;
				for (int i = 0; i < unitN[ll]+1; i++) {
					s += w[ll+1][j][i] * unit[ll][i];
				}
				unit[ll+1][j] = sigmoid(s);
			});
		}
		loopPara(unitN[outLayer], j -> {									// 出力層
			unit[outLayer][j] = 0.0;
			for (int i = 0; i < unitN[outLayer-1]+1; i++) {
				unit[outLayer][j] +=
					w[outLayer][j][i] * unit[outLayer-1][i];
			}
		});
		softmax(unit[outLayer]);
	}

	void backPropagate(double[] d, double[] t) {				// 逆伝搬
		for (int j = 0; j < unitN[outLayer]; j++) {					// 出力層
			delta[outLayer][j] = t[j] - unit[outLayer][j];
		};
		for (int j = 0; j < unitN[outLayer]; j++) {
			err += delta[outLayer][j] * delta[outLayer][j];
		}
		for (int l = outLayer-1; l > 0; l--) {								// 中間層
			final int ll = l;
			loopPara(unitN[l], j -> {
				double df = unit[ll][j] * (1.0 - unit[ll][j]);
				double s = 0.0;
				for (int k = 0; k < unitN[ll+1]; k++) {
					s+=  delta[ll+1][k] * w[ll+1][k][j];
				}	
				delta[ll][j] = df * s;
			});
		}
	}

	void update(double rate) {												// 重み更新
		for (int l = layerN-1; l > 0; l--) {
			final int ll = l;
			loopPara(unitN[l], j -> {
				for (int i = 0; i < unitN[ll-1]+1; i++) {
					w[ll][j][i] += rate * delta[ll][j] * unit[ll-1][i];
				}
			});
		}
	}
}
