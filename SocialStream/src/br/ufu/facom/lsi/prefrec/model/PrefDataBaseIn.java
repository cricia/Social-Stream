package br.ufu.facom.lsi.prefrec.model;

import java.io.Serializable;

public class PrefDataBaseIn implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer fold;
	private Integer User;
	private Double rate;
	private Double prediction;
	private Integer item;
	
	/**
	 * 
	 */
	public PrefDataBaseIn() {
		super();
	}

	/**
	 * @param fold
	 * @param user
	 * @param rate
	 * @param prediction
	 */
	public PrefDataBaseIn(Integer fold, Integer user, Double rate,
			Double prediction) {
		super();
		this.fold = fold;
		User = user;
		this.rate = rate;
		this.prediction = prediction;
	}

	/**
	 * @return the fold
	 */
	public Integer getFold() {
		return fold;
	}

	/**
	 * @param fold the fold to set
	 */
	public void setFold(Integer fold) {
		this.fold = fold;
	}

	/**
	 * @return the user
	 */
	public Integer getUser() {
		return User;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(Integer user) {
		User = user;
	}

	/**
	 * @return the rate
	 */
	public Double getRate() {
		return rate;
	}

	/**
	 * @param rate the rate to set
	 */
	public void setRate(Double rate) {
		this.rate = rate;
	}

	/**
	 * @return the prediction
	 */
	public Double getPrediction() {
		return prediction;
	}

	/**
	 * @param prediction the prediction to set
	 */
	public void setPrediction(Double prediction) {
		this.prediction = prediction;
	}

	/**
	 * @return the item
	 */
	public Integer getItem() {
		return item;
	}

	/**
	 * @param item the item to set
	 */
	public void setItem(Integer item) {
		this.item = item;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PrefDataBaseIn [fold=" + fold + ", User=" + User + ", rate="
				+ rate + ", prediction=" + prediction + ", item=" + item + "]";
	}

}
