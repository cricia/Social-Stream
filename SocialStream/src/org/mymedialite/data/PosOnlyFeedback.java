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
// You should have received a copy of the GNU General Public License
// along with MyMediaLite.  If not, see <http://www.gnu.org/licenses/>.

package org.mymedialite.data;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.ArrayList;
import java.util.List;

import org.mymedialite.datatype.IBooleanMatrix;

/**
 * Data structure for implicit, positive-only user feedback.
 * This data structure supports incremental updates.
 * @version 2.03
 */
public class PosOnlyFeedback<T extends IBooleanMatrix> extends DataSet implements IPosOnlyFeedback {

  /** By-user access, users are stored in the rows, items in the columns */
  public IBooleanMatrix userMatrix;

  /** By-item access, items are stored in the rows, users in the columns */
  public IBooleanMatrix itemMatrix;

  private Class<T> c;

  /**
   * Create a PosOnlyFeedback object.
   * @param c the Class<T>
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
  public PosOnlyFeedback(Class<T> c) throws InstantiationException, IllegalAccessException {
    super();
    this.c = c;
    userMatrix = c.newInstance();
  }

  /**
   * By-user access, users are stored in the rows, items in the columns.
   */
  public IBooleanMatrix userMatrix() {
    if(userMatrix == null) userMatrix = getUserMatrixCopy();
    return userMatrix;
  }

  /**
   * By-item access, items are stored in the rows, users in the columns.
   */
  public IBooleanMatrix itemMatrix() {
    if(itemMatrix == null) itemMatrix = getItemMatrixCopy();
    return itemMatrix;
  }

  @Override
  public IBooleanMatrix getUserMatrixCopy() {
    T matrix = null;
    try {
      matrix = c.newInstance();
      for (int index = 0; index < size(); index++)
        matrix.set(users.getInt(index), items.getInt(index), true);
    } catch (Exception e) { }
    return matrix;
  }

  @Override
  public IBooleanMatrix getItemMatrixCopy() {
    T matrix = null;
    try {
      matrix = c.newInstance();
      for (int index = 0; index < size(); index++)
        matrix.set(items.getInt(index), users.getInt(index), true);
    } catch (Exception e) { }
    return matrix;
  }

  /**
   * Add a user-item event to the data structure
   * @param user_id the user ID
   * @param item_id the item ID
   */
  public void add(int user_id, int item_id) {
    users.add(user_id);
    items.add(item_id);
    if (userMatrix != null) userMatrix.set(user_id, item_id, true);
    if (itemMatrix != null) itemMatrix.set(item_id, user_id, true);
    if (user_id > maxUserID) maxUserID = user_id;
    if (item_id > maxItemID) maxItemID = item_id;
  }

  /**
   * Remove a user-item event from the data structure.
   * @param user_id the user ID
   * @param item_id >the item ID
   */
  public void remove(int user_id, int item_id) {
    Integer index;
    while((index = tryGetIndex(user_id, item_id)) != null) {
      users.remove(index);
      items.remove(index);
    }

    if (userMatrix != null) userMatrix.set(user_id, item_id, false);
    if (itemMatrix != null) itemMatrix.set(item_id, user_id, false);
  }

  /**
   * Remove the event with a given index
   * @param index the index of the event to be removed
   */
  public void remove(int index) {
    int user_id = users.getInt(index);
    int item_id = items.getInt(index);
    users.remove(index);
    items.remove(index);

    if (tryGetIndex(user_id, item_id) == -1) {
      if (userMatrix != null) userMatrix.set(user_id, item_id, false);
      if (itemMatrix != null) itemMatrix.set(item_id, user_id, false);
    }
  }

  /**
   * Remove all feedback by a given user.
   * @param user_id the user ID
   */
  public void removeUser(int user_id) {
    IntList indices = new IntArrayList();
    if (byUser != null)
      indices = byUser().get(user_id);
    else if (userMatrix != null)
      indices = new IntArrayList(userMatrix.get(user_id));
    else
      for (int index = 0; index < size(); index++)
        if (users.getInt(index) == user_id)
          indices.add(index);

    // assumption: indices is sorted
    for (int i = indices.size() - 1; i >= 0; i--) {
      users.remove(indices.getInt(i));
      items.remove(indices.getInt(i));
    }

    if (userMatrix != null)
      userMatrix.get(user_id).clear();
    if (itemMatrix != null)
      for (int i = 0; i < itemMatrix.numberOfRows(); i++)
        itemMatrix.get(i).remove(user_id);
  }

  /**
   * Remove all feedback about a given item</summary>
   * @param item_id the item ID
   */
  public void removeItem(int item_id) {
    IntList indices = new IntArrayList();
    if (byItem != null)
      indices = byItem().get(item_id);
    else if (itemMatrix != null)
      indices = new IntArrayList(itemMatrix.get(item_id));
    else
      for (int index = 0; index < size(); index++)
        if (items.getInt(index) == item_id)
          indices.add(index);

    // assumption: indices is sorted
    for (int i = indices.size() - 1; i >= 0; i--) {
      users.remove(indices.getInt(i));
      items.remove(indices.getInt(i));
    }

    if (userMatrix != null)
      for (int u = 0; u < userMatrix.numberOfRows(); u++)
        userMatrix.get(u).remove(item_id);

    if (itemMatrix != null)
      itemMatrix.get(item_id).clear();
  }

  @Override
  public IPosOnlyFeedback transpose() {
    PosOnlyFeedback<T> transpose = null;
    try {
      transpose = new PosOnlyFeedback<T>(c);
      transpose.users = new IntArrayList(this.items);
      transpose.items = new IntArrayList(this.users);
    } catch (Exception e) { }
    return transpose;
  }

}