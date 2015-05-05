/**
 * 
 */
package br.ufu.facom.lsi.prefrec.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Klerisson
 *
 */
public class PrefDataBaseInList<E extends PrefDataBaseIn> extends
		ArrayList<PrefDataBaseIn> {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public PrefDataBaseInList() {
		super();
	}

	/**
	 * @param initialCapacity
	 */
	public PrefDataBaseInList(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * @param c
	 */
	public PrefDataBaseInList(Collection<? extends PrefDataBaseIn> c) {
		super(c);
	}

	public Map<Integer, List<User>> toUserByFold() {
		// Fold - User
		HashMap<Integer, List<User>> result = initHash();
		for (PrefDataBaseIn pref : this) {
			List<User> foldUserList = result.get(pref.getFold());
			User user = new User(new Long(pref.getUser()));
			int index = foldUserList.indexOf(user);
			if (index != -1) {
				user = foldUserList.get(index);
			} else {
				foldUserList.add(user);
			}
			List<Item> itemList = user.getItems();
			if (itemList == null) {
				itemList = new ArrayList<Item>();
				user.setItems(itemList);
			}

			Item item = new Item(new Long(pref.getItem()), pref.getRate(), pref.getPrediction());
			// TODO there's no chance of a replicated item, isn't there?!!
			// index = itemList.indexOf(item);
			// if(index == -1){
			// item = itemList.get(index);
			// }
			itemList.add(item);
			
		}
		return result;
	}

	private HashMap<Integer, List<User>> initHash() {
		final HashMap<Integer, List<User>> result = new HashMap<Integer, List<User>>();
		Set<Integer> folds = new HashSet<Integer>();
		this.stream().forEach(prefData -> folds.add(prefData.getFold()));
		folds.stream().forEach(
				id -> result.put(id, new ArrayList<User>()));
		return result;
	}

	public void writeObject() {
		try (final FileOutputStream fout = new FileOutputStream(
				"..\\list.PrefDataIn");
				ObjectOutputStream oos = new ObjectOutputStream(fout);) {

			oos.writeObject(this);
			oos.flush();

		} catch (Exception e) {
			System.err.println("Fail to write PrefDataBase file.");
		}
	}

	@SuppressWarnings("unchecked")
	public void readObject() {

		try (FileInputStream streamIn = new FileInputStream(
				"..\\list.PrefDataIn");
				ObjectInputStream objectinputstream = new ObjectInputStream(
						streamIn);) {

			this.clear();
			this.addAll((PrefDataBaseInList<PrefDataBaseIn>) objectinputstream
					.readObject());

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
