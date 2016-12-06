package ex07;

// 有限状態マシンクラス
public class FSM {
	static final int  MOVE		= 1;				// 移動（エネルギー貯える）
	static final int  SEARCH	= 2;				// 探索（エネルギーやや消費する）
	static final int  CHASE		= 3;				// 追跡（エネルギー消費する）
	static final int  ATTACK	= 4;				// 攻撃（エネルギーゼロになる，知性上がる）
	static final int  ESCAPE		= 5;				// 逃避（エネルギー低下）
	static final int  SLEEP		= 6;				// 休止（エネルギー貯える）

	int state								= MOVE;	// 状態
	double energy					= 50.0;			// エネルギー
	double intelligence			= 8.0;			// 知性
	boolean touched				= false;		// 接触したか
	int fullEnergy						= 100;			// エネルギー満タン
	int fullIntelligence				= 10	;			// 知性高レベル

	static String[] stateLabel =
		{ "", "Move", "Search", "Chase", "Attack", "Escape", "Sleep" };

	void action() {
		switch(state) {
		case MOVE:												// 移動状態のとき
			if (touched) state = ATTACK	;			// プレイヤーに接触したら攻撃する
			// 移動中はエネルギー充てん
			else if (energy < fullEnergy) energy += 0.1;
			// エネルギーが溜まったら探索に移行
			else if (energy >= fullEnergy) state = SEARCH;
			break;

		case SEARCH:											// 探索状態のとき
			energy -= 0.05	;									// 探索中はエネルギー消費
			if (energy <= 0) state = MOVE;			// エネルギー切れたら移動に移行
			else if (touched) state = ATTACK;		// プレイヤーに接触したら攻撃する
			// 知性が高ければ追跡に移行
			else if (intelligence >= fullIntelligence) state = CHASE;
			break;

		case CHASE:												// 追跡状態のとき
			energy -= 0.1;										// 追跡中はエネルギー消費
			if (energy <= 0) state = MOVE;			// エネルギー切れたら移動に移行
			else if (touched)	state = ATTACK;	// プレイヤーに接触したら攻撃する
			break;

		case ATTACK:											// 攻撃状態のとき
			energy = Math.max(0, energy - 10);	// 攻撃したらエネルギー消費
			intelligence += 1;									// 攻撃経験により知性を上げる
			state = ESCAPE;										// 攻撃後は逃避に移行
			break;

		case ESCAPE:												// 逃避状態のとき
			energy -= 0.1;										// エネルギー低下していく
			if (energy <= 0) state = SLEEP;			// エネルギー切れたら休止に移行
			break;

		case SLEEP:	// 休止状態のとき
			if (energy < fullEnergy) energy += 0.1;	//休止中はエネルギー充てん
			// エネルギーが半分溜まれば移動へ移行
			if (energy >= fullEnergy/2) state = MOVE;
			break;
		}
	}
}
