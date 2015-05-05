/**
 * 
 */
package br.ufu.facom.lsi.prefrec.model;

import java.io.Serializable;

/**
 * @author Klerisson
 *
 */
public class Item implements Serializable {

	private static final long serialVersionUID = 2614346518688065606L;

	private Long id;
	private Double rate;
	private Double prediction;
	
	/**
	 * @param id
	 * @param rate
	 */
	public Item(Long id, Double rate) {
		super();
		this.id = id;
		this.rate = rate;
	}

	public Item(Long id, Double rate, Double prediction) {
		super();
		this.id = id;
		this.rate = rate;
		this.prediction = prediction;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the rate
	 */
	public Double getRate() {
		return rate;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
