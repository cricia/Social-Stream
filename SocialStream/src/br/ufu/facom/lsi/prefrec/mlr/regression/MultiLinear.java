package br.ufu.facom.lsi.prefrec.mlr.regression;

import br.ufu.facom.lsi.prefrec.mlr.matrix.Matrix;
import br.ufu.facom.lsi.prefrec.mlr.matrix.MatrixMathematics;
import br.ufu.facom.lsi.prefrec.mlr.matrix.NoSquareException;

/**
 * 
 * @author ata
 *
 * This class provide methods to calculate the coefficients in a multi-linear regression moddelling.
 *
 */
public class MultiLinear {

	private Matrix X;
	final private Matrix Y;
	final private boolean bias;
	
	public MultiLinear(final Matrix x, final Matrix y) {
		this(x,y,true);
	}
	
	public MultiLinear(final Matrix x, final Matrix y, final boolean bias) {
		super();
		this.X = x;
		this.Y = y;
		this.bias = bias;
	}
	/**
	 * beta a matrix which gives the coefficients of a multi-linear regression equation Y = sum_of(beta_i * x_i) 
	 * beta = Inverse_of(X_transpose * X) * X_transpose * Y
	 * @return
	 * @throws NoSquareException
	 */
	public Matrix calculate() throws NoSquareException {
		if (bias)
			this.X = X.insertColumnWithValue1();
		checkDiemnsion();
		final Matrix Xtr = MatrixMathematics.transpose(X); //X'
		final Matrix XXtr = MatrixMathematics.multiply(Xtr,X); //XX'
		final Matrix inverse_of_XXtr = MatrixMathematics.inverse(XXtr); //(XX')^-1
		if (inverse_of_XXtr == null) {
			System.out.println("Matrix X'X does not have any inverse. So MLR failed to create the model for these data.");
			return null;
		}
		final Matrix XtrY = MatrixMathematics.multiply(Xtr,Y); //X'Y
		return MatrixMathematics.multiply(inverse_of_XXtr,XtrY); //(XX')^-1 X'Y
	}

	/**
	 * Preconditions checks
	 */
	void checkDiemnsion() {
		if (X == null) 
			throw new NullPointerException("X matrix cannot be null.");
		if (Y == null) 
			throw new NullPointerException("Y matrix cannot be null.");
		
		if (X.getNcols() > X.getNrows()) {
			throw new IllegalArgumentException("The number of columns in X matrix (descriptors) cannot be more than the number of rows");
		}
		if (X.getNrows() != Y.getNrows()) {
			throw new IllegalArgumentException("The number of rows in X matrix should be the same as the number of rows in Y matrix. ");
		}
	}
	
}
