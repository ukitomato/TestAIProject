package ex04;

import java.util.Scanner;

import my.Cons;

// TicTacToe基本バージョンクラス
class TicTacToe {
	// 3x3ゲーム盤，2次元配列
	Character[][] bd = Cons.makeCharArray2(3, 3, '　');

	// 勝ちの添え字パターン生成
 	Cons a = Cons.of(0,1,2);
	Cons pat =  new Cons(
			a.map(x -> Cons.of(x, x)), 											// 斜め3マス
			a.map((Integer x) -> Cons.of(2-x, x)),						// 斜め3マス
			a.map(r -> a.map(c -> Cons.of(r,c))).						// 水平3マス × 3
			append(a.map(c -> a.map(r -> Cons.of(r,c)))));		// 垂直3マス × 3

	boolean playing = true;			// ゲーム続行フラグ
	char winner = '　';					// 勝者格納用
	Scanner scan;							// キー入力先

	boolean goal(char p) {				// 勝者判定（3マス並んだか）
		return pat.exists((Cons t) -> t.forall((Cons a) -> bd[a.getI(0)][a.getI(1)] == p));
	}

	boolean fin() {							// 終了判定（もう置けないか）
		return !Cons.range(0, 3).exists((Integer r) ->
						Cons.range(0, 3).exists((Integer c) -> bd[r][c] == '　'));
	}

	void computer(char p) {			// コンピュータの手（ランダム）
		Cons free = Cons.range(0, 3).flatMap((Integer r) ->
								Cons.range(0, 3).map((Integer c) -> Cons.of(r, c)))
								.filter((Cons x) -> bd[x.getI(0)][x.getI(1)] == '　');
		Cons f = (Cons)free.get((int)(Math.random() * free.length()));
		int r = f.getI(0);
		int c = f.getI(1);
		bd[r][c] = p;
		System.out.println("computer:" + p + " = " + r + "," + c);
	}

	void human(char p) {									// 人間の手（行，列のキー入力）
		System.out.print("row col =>");
		int r = scan.nextInt();								// キー入力データをrに格納
		int c = scan.nextInt();								// キー入力データをcに格納
		if (bd[r][c] == '　') bd[r][c] = p;
		else human(p);											// 置けない場所なので再試行
	}

	char turn(char p) {										// プレイヤー交代
		return p == '○' ? '×' : '○';
	}

	void disp() {													// 盤表示
		System.out.println(
				Cons.fromArray(bd).map((Character[] x) ->
					Cons.fromArray(x).mkString("｜")).mkString("\n"));
	}

	void play() {													// ゲームメインループ
		scan = new Scanner(System.in);				// キー入力
		char p = '○';												// 最初のプレイヤー設定
		disp();
		do {
			if (p == '○') human(p);						// プレイヤーに応じた処理
			else computer(p);
			disp();
			if (goal(p)) {											// 勝ったか？
				winner = p;
				playing = false;
			} else if (fin()) {										// 終了したか？
				playing = false;
			} else {
				p = turn(p);										// プレイヤー交代
			}
		} while (playing);
		if (winner != '　') System.out.println(winner + " Win!");
		else System.out.println("drawn");
		scan.close();
	}
}

// アプリケーション起動クラス
public class TicTacToeApp {
	public static void main(String[] args) {
		new TicTacToe().play();
	}
}
