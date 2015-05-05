// Copyright (C) 2011 Zeno Gantner, Chris Newell
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
//

package org.mymedialite.eval;

/**
 * Different modes for choosing candidate items in item recommender evaluation.
 * @version 2.03
 */
public enum CandidateItems {

  /**
   * Use all items in the training set.
   */
  TRAINING,

  /**
   * Use all items in the test set.
   */
  TEST,

  /**
   * Use all items that are both in the training and the test set.
   */
  OVERLAP,

  /**
   * Use all items that are both in the training and the test set.
   */
  UNION,

  /**
   * Use items provided in a list given by the user.
   */
  EXPLICIT
  
}

