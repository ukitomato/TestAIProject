package ex08;

// ロジスティック回帰アプリケーションクラス
public class LogisticApp {
	int patN						= 4;				// パターン数
	int trainN					= 1000;		// 学習回数
	int batchSize				= 1;				// バッチサイズ
	double learnRate		= 0.1; 			// 学習率

	double[][] trainData;						// 学習用データ
	double[][] testData;						// 認識テストデータ

	String[] trainDataString = {			// 学習用データ文字列
		"0000110000",		"0001111000",		"0001111000",		"0000011000",	
		"0000110000",		"0011111100",		"0011111100",		"0000111000",	
		"0001110000",		"0110001110",		"0110001110",		"0000111000",	
		"0001110000",		"0110001110",		"0110001110",		"0001111000",	
		"0000110000",		"0000011100",		"0000011100",		"0011011000",	
		"0000110000",		"0000011000",		"0000011100",		"0011011000",	
		"0000110000",		"0001100000",		"0110001110",		"0111111110",	
		"0000110000",		"0011100000",		"0110001110",		"0111111110",	
		"0000110000",		"0111111110",		"0011111100",		"0000011000",	
		"0000110000",		"0111111110",		"0001111000",		"0000011000"
	};
	
	String[] testDataString = {			// 認識テストデータ文字列
		"0000000000",		"0001110000",		"0000111000",		"0000000000",	
		"0000011000",		"0011011000",		"0000111100",		"0000001000",	
		"0000011000",		"0000001100",		"0000001100",		"0000011000",	
		"0000011000",		"0000001100",		"0000001100",		"0000110000",	
		"0000011000",		"0000001100",		"0000011000",		"0001100000",	
		"0000110000",		"0000011000",		"0000011000",		"0011000000",	
		"0000110000",		"0000110000",		"0000000110",		"0110011100",	
		"0000110000",		"0001100000",		"0000000110",		"1111111100",	
		"0000110000",		"0011111000",		"0000011100",		"0000110000",	
		"0000000000",		"0011111000",		"0001110000",		"0000100000"
	};
	
	double[][] teachData = {				// 教師データ
		{ 1, 0, 0, 0 }, { 0, 1, 0, 0 }, { 0, 0, 1, 0 }, { 0, 0, 0, 1 }
	};

	double[][] buildData(String[] s, int r, int c) {		// 文字列からデータを作成
		int n = r * c;														// 1パターンの要素数
		double[][] x = new double[patN][n];
		for (int i = 0; i < patN; i++) {
			for (int j = 0; j < n; j++) {
				int k = j / c * patN + i;
				int kk = j % c;
				x[i][j] = s[k].charAt(kk) == '1' ? 1.0 : 0.0;
			}
		}
		return x;
	}

	void disp(double[][] data, int r, int c) {				// データを表示
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < patN; j++) {
				for (int k = 0; k < c; k++) {
					int idx = i * c + k;	
					System.out.print(data[j][idx] > 0 ? "#" : " ");
				}
				System.out.print(" | ");
			}
			System.out.println();
		}
		System.out.println();
	}

	public void run() {													// 実行
		// データの初期化
		trainData = buildData(trainDataString, 10, 10);
		testData = buildData(testDataString, 10, 10);
		System.out.println("== 学習データ入力 ==");	disp(trainData, 10, 10);
		System.out.println("== 認識テスト入力 ==");	disp(testData,10, 10);

		// 学習
		Logistic lg = new Logistic(100, 4);
		lg.train(trainData, teachData, patN, trainN, batchSize, learnRate);	
	
		// 認識テスト
		System.out.println("== 認識テスト結果 ==");
		for (double[] d : testData) {
			lg.forward(d);																						// 認識
			for (int j = 0; j < lg.m; j++) System.out.printf("%.2f\t",lg.out[j]);	//出力層表示
			int idx = lg.getResult();
			System.out.println(" => これは " + (idx+1) +" です");
		}
	}

	public static void main(String[] args) {
		new LogisticApp().run();
	}
}
