//Copyright (C) 2011 Zeno Gantner, Chris Newell
//
//This file is part of MyMediaLite.
//
//MyMediaLite is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//MyMediaLite is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with MyMediaLite.  If not, see <http://www.gnu.org/licenses/>.

package org.mymedialite.datatype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Combines two List objects.
 * @version 2.03
 */
public class CombinedList<T> implements List<T> {

  List<T> first;
  List<T> second;

  /**
   * Create a new CombinedList object.
   * @param list1 first list
   * @param list2 second list
   */
  public CombinedList(List<T> list1, List<T> list2) {
    first = list1;
    second = list2;
  }

  /**
   * 
   */
  @Override
  public T get(int index) {
    if (index < first.size())
      return first.get(index);
    else
      return second.get(index - first.size());
  }

  /**
   * 
   */
  @Override
  public int size() {
    return first.size() + second.size();
  }
  
  /**
   * 
   */
  @Override
  public boolean add(T item) {
    return second.add(item);
  }

  /**
   * 
   */
  @Override
  public void add(int index, T item) { 
    throw new UnsupportedOperationException();
  }
  
  /**
   * 
   */
  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  /**
   * 
   */
  @Override
  public boolean contains(Object item) {
    for (int i = 0; i < first.size(); i++)
      if (first.get(i).equals(item)) return true;
    
    for (int i = first.size(); i < first.size() + second.size(); i++)
      if (second.get(i - first.size()).equals(item)) return true;
    
    return false;
  }

  /**
   * 
   */
  @Override
  public int indexOf(Object item) {
    throw new UnsupportedOperationException();}

  /**
   * 
   */
  @Override
  public boolean remove(Object item) {
    throw new UnsupportedOperationException(); 
  }

  /**
   * 
   */
  @Override
  public T remove(int index) {
    if (index < first.size())
      return first.remove(index);
    else
      return second.remove(index - first.size());
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    return second.addAll(c);
  }

  @Override
  public boolean addAll(int index, Collection<? extends T> c) {
    throw new UnsupportedOperationException(); 
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    for (Object o : c)
      if (!contains(o)) return false;
    return true;
  }

  @Override
  public boolean isEmpty() {
    return first.isEmpty() && second.isEmpty();
  }

  @Override
  public Iterator<T> iterator() { throw new UnsupportedOperationException(); }

  @Override
  public int lastIndexOf(Object o) {
    int lastIndex = second.lastIndexOf(o);
    if (lastIndex == -1) lastIndex = first.lastIndexOf(o);
    return lastIndex;
  }

  @Override
  public ListIterator<T> listIterator() {
    List<T> list = new ArrayList<T>(first);
    list.addAll(second);
    return list.listIterator();
  }

  @Override
  public ListIterator<T> listIterator(int index) {
    List<T> list = new ArrayList<T>();
    if (index < first.size()) {
      list.addAll(first.subList(index, first.size()));
      list.addAll(second);
    } else {
      list.addAll(second.subList(index, second.size()));
    }
    return list.listIterator();  
  }
  
  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException(); 
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException(); 
  }

  @Override
  public T set(int index, T element) {
    throw new UnsupportedOperationException(); 
  }

  // TODO Needs checking
  @Override
  public List<T> subList(int fromIndex, int toIndex) {
    List<T> list = new ArrayList<T>();
    if (fromIndex < first.size())
      list.addAll(first.subList(fromIndex, Math.min(first.size(), toIndex)));
    
    if(toIndex >= first.size())
      list.addAll(second.subList(Math.max(0, fromIndex - first.size()), toIndex - first.size()));

    return list;
  }

  @Override
  public Object[] toArray() {
    Object[] array = new Object[size()]; 
    System.arraycopy(first.toArray(), 0, array, 0, first.size());
    System.arraycopy(second.toArray(), 0, array, first.size(), second.size());
    return array;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E> E[] toArray(E[] a) {
    E[] firstArray  = (E[]) Arrays.copyOf(first.toArray(),  first.size(),  a.getClass());
    E[] secondArray = (E[]) Arrays.copyOf(second.toArray(), second.size(), a.getClass());    
    E[] array = (E[]) Arrays.copyOf(firstArray, size(), a.getClass());
    System.arraycopy(secondArray, 0, array, first.size(), second.size());
    return array;
  }

}
