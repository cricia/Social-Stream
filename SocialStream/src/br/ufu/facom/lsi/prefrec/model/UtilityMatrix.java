package br.ufu.facom.lsi.prefrec.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Klerisson
 *
 */
public class UtilityMatrix implements Serializable {

	private static final long serialVersionUID = -4891906983420220873L;

	private List<User> users;
	private List<Long> uniqueItemIds;

	public UtilityMatrix() {
		super();
		this.users = new ArrayList<>();
	}

	public boolean addUser(User user) {
		if (this.uniqueItemIds == null) {
			this.uniqueItemIds = new ArrayList<Long>();
			for(Item item : user.getItems()){
				this.uniqueItemIds.add(item.getId());
			}
			Collections.sort(this.uniqueItemIds, new Comparator<Long>() {
				@Override
				public int compare(Long o1, Long o2) {
					return o1.compareTo(o2);
				}
			} );
		}
		return this.users.add(user);
	}

	/**
	 * @return the users
	 */
	public List<User> getUsers() {
		return users;
	}

	/**
	 * 
	 * @param user
	 * @return user item list
	 */
	public List<Item> getUserItemList(User user) {
		int idx = this.users.indexOf(user);
		if (idx != -1) {
			return this.users.get(idx).getItems();
		} else {
			return null;
		}
	}
	
	public User getUserItemList(Long userId) {
		for(User u : this.users){
			if(u.getId() == userId){
				return u; 
			}
		}
		return null;
	}

	/**
	 * @return the uniqueItemIds
	 */
	public List<Long> getUniqueItemIds() {
		return uniqueItemIds;
	}
}
