package ex07;

import ex06.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

// グラフィックスウィンドウアプリケーションクラス
public class GameApp4 extends GameApp2 {
	public static void main(String[] args) {
		launch(args);
	}

	public void initGame() {
		super.initGame();
		for (GameElem e : elems) {
			if (e.typ == GameElem.ALIEN) ((FSMAlien)e).initAStar(this);
		}
	}

	public GameElem makeElem(int typ) {				// キャラクタ生成
		switch (typ) {
			case GameElem.PLAYER:	return new FSMPlayer(this, bread);	// 新たなプレイヤークラス
			case GameElem.ALIEN:		return new FSMAlien(this, bread);	// 新たな敵クラス
			default: return super.makeElem(typ);												// 他は同じ生成法
		}
	}
}

// パンくず，有限状態マシンをを追加したFSMPlayerクラス
class FSMPlayer extends BreadPlayer {
	int atteckWait = 0;

	public FSMPlayer(GameApp app, Breadcrumbs bread) {
		super(app, bread);
	}

	public void move() {
		if (atteckWait > 0) { 										// 攻撃ダメージ中
			atteckWait -= 1;
			return;
		}
		super.move();
	}
}

// パンくず，A*，有限状態マシンをを追加したFSMAlienクラス
class FSMAlien extends AStarAlien {
	FSM fsm;
	FSMPlayer touchedPlayer;								// 接触プレイヤー
	int atteckWait = 0;
	Text text;

	public FSMAlien(GameApp app, Breadcrumbs bread) {
		super(app, bread);
		fsm = new FSM();											// 有限状態マシン作成
		text = new Text("");										// 状態表示用
		text.setFont(Font.font ("MeiryoUI", 11));
		app.shapes.add(text);
	}

	public void  draw() {
		super.draw();
		text.setX(x + 3);
		text.setY(y + app.uh*0.5);
		text.setText(FSM.stateLabel[fsm.state] + "\n   " + (int)fsm.energy);
	}

	public void nextMove() {		// 移動先は有限状態マシンで決定する
		fsm.action();
		switch (fsm.state) {
			case FSM.SEARCH:	nextMoveBread();			// パンくず拾い
											break;
			case FSM.CHASE:		nextMoveAStar();			// A*アルゴリズム
											break;
			case FSM.ATTACK:	atteckWait = 50;			// 攻撃
											touchedPlayer.atteckWait = 100;
											break;
			case FSM.SLEEP:	break;									// 休止中は処理なし
			case FSM.MOVE:
			case FSM.ESCAPE:	nextMoveRandom();	// ランダム
											break;
		}
	}

	public void move() {
		if (atteckWait > 0) {							// 攻撃中
			atteckWait -= 1;
			return;
		}
		nextMove();										// 次の移動先を決定
		astar.routeLine.visible = fsm.state == FSM.CHASE;
		// プレイヤーとの衝突判定
		if (fsm.state == FSM.ESCAPE) {		// 逃避中は接触無視
			touchedPlayer = null;
		} else {
			touchedPlayer = null;
			for (GameElem e : app.elems) {
				if (e instanceof FSMPlayer) {
					FSMPlayer p = (FSMPlayer)e;
					if (Math.abs(p.x - (x + dirOffset[nextDir][0])) < app.uw &&
						Math.abs(p.y - (y + dirOffset[nextDir][1])) < app.uh) {
						touchedPlayer = p;
					}
				}
			}
		}
		fsm.touched = touchedPlayer != null;
		// 攻撃中か休止中は移動しない
		if (fsm.touched || fsm.state == FSM.SLEEP) return;
		moveExec();										// 移動実行
	}
}