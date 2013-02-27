package com.kapouta.aurubik.lib.rubikube;

import com.kapouta.aurubik.lib.rubikube.piece.RubikPiece;

public class SudoKubeModel extends RubiKubeModel {
	public SudoKubeModel() {
		this(3);// default is a 3 x 3 x 3
	}

	public SudoKubeModel(int sideSize) {
		super(sideSize);
		for (int cnt = 0; cnt < RubiKubeModel.NSIDES; cnt++) {
			int tmp = 1;
			for (RubikPiece side : this.getSide(cnt)) {
				side.getSideFacingToward(cnt).setValue(tmp);
				tmp++;
			}
		}
	}

	public boolean isFinished() {
		int completed = 0x1ff;// binarry for 0001 1111 1111
		int tmp = 0;

		for (int side_counter = 0; side_counter < NSIDES; side_counter++) {
			tmp = 0;
			RubikPiece[] tmp_side_array = getSide(side_counter);

			for (RubikPiece tmp_piece : tmp_side_array) {
				tmp |= (1 << (tmp_piece.getSideFacingToward(side_counter)
						.getValue() - 1));
			}
			// checks if this side is completed
			if (tmp != completed) {
				return false;
			}
		}

		return true;

	}

}
