// Copyright (C) 2010 Zeno Gantner, Andreas Hoffmann
// Copyright (C) 2011 Zeno Gantner
//
// This file is part of MyMediaLite.
//
// MyMediaLite is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// MyMediaLite is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with MyMediaLite.  If not, see <http://www.gnu.org/licenses/>.

package org.mymedialite;

import org.mymedialite.datatype.SparseBooleanMatrix;

/**
 * Interface for recommenders that take binary item attributes into
 * account
 * 
 * @author Zeno Gantner, Andreas Hoffmann
 * @version 2.03
 */
public interface IItemAttributeAwareRecommender extends IRecommender {

	/**
	 * Getter for number of binary item attributes
	 */
	public int numItemAttributes();

	/**
	 * Getter for binary item attributes
	 */
	public SparseBooleanMatrix getItemAttributes();

	/**
	 * Setter for binary item attributes
	 */
	public void setItemAttributes(SparseBooleanMatrix s);
	
}