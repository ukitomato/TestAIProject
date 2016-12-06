package ex09;

import ex08.*;

// 多層デノイジングオートエンコーダクラス(Stacked Denoising AutoEncoder)
public class SAE {
	int layerN;
	int[] unitN;
	int stackN;
	DAEPara[] ae;						// デノイジングオートエンコーダ
	MLPPara mlp;					// 多層パーセプトロン
	double[][][] cache;			// 中間層のキャッシュ

	public SAE(int layerN, int[] unitN, double wMax) {
		this.layerN = layerN;
		this.unitN = unitN;
		stackN = layerN - 2;
		cache = new double[stackN][][];

		// 多層パーセプトロンの構築（並列バージョン）
		mlp = new MLPPara(layerN, unitN, wMax, 8);

		// 多層デノイジングオートエンコーダの構築（並列バージョン）
		ae = new DAEPara[stackN];
		for (int l = 0; l < stackN; l++) {
			ae[l] = new DAEPara(unitN[l], unitN[l + 1], 8);
		}
	}

	// プレトレーニング
	void preTrain(double[][] d, int patN, double[][] param) {
		for (int l = 0; l < stackN; l++) {						// 層ループ
			double[][] in = (l == 0) ? d : cache[l - 1];
			double[] p = param[l];
			ae[l].train(in, patN, (int)p[0], (int)p[1], p[2], p[3], p[4]);
			cache[l] = new double[patN][];
			for (int i = 0; i < patN; i++) {
				cache[l][i] = new double[ae[l].m];
				ae[l].encode(in[i], cache[l][i]);				//中間層値保存
			}
		}
	}

	// ファインチューニング
	void fineTune(double[][] d, double[][] t, int patN, double[] p,
							MLP.ReportFun fun) {
		for (int l = 0; l < mlp.layerN-2; l++) {
			for (int j = 0; j < mlp.unitN[l+1]; j++) {
				for (int i = 0; i < mlp.unitN[l]; i++) {
					mlp.w[l+1][j][i] = ae[l].w[j][i];			// AEからMLPへ重みコピー
				}
				mlp.w[l+1][j][mlp.unitN[l]] = ae[l].b1[j];
			}
		}
		mlp.train(d, t, patN, (int)p[0], p[1], fun);
	}

	// 順伝搬
	double[] forward(double[] d) {
		mlp.forward(d);
		return mlp.unit[mlp.layerN-1];
	}

	int getResult() {						// 最大の出力を認識結果と判定する
		return mlp.getResult();
	}
}
