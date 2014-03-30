package edu.sfsu.cs.orange.ocr;

import java.util.ArrayList;
import java.util.LinkedList;

public class FactoidList {

	private ArrayList<String> factoid = new ArrayList<String>();
	private ArrayList<Integer> indexHistory = new ArrayList<Integer>();
	private int currentPointer = 0;

	public FactoidList() {
		this.addPredefinedFactoids();

	}

	private void addPredefinedFactoids() {
		this.addFactoid("An avocado has more than twice as much potassium as a banana.");
		this.addFactoid("Green-tipped bananas are healthier than over-ripe bananas.");
		this.addFactoid("Broccoli contains twice the vitamin C of an orange.");
		this.addFactoid("Cilantro is a member of the carrot family.");
		this.addFactoid("To obtain the maximum nutritional benefits, onions should be eaten raw or lightly steamed.");
		this.addFactoid("The peanut is actually a legume, not a nut (which is why they are often roasted).");
		this.addFactoid("Parsley contains three times as much vitamin C as oranges, and twice as much iron as spinach.");
		this.addFactoid("Non-organic peanut butters are high in pesticides and fungus and contain aflatoxin, a potent carcinogenic mold.");
		this.addFactoid("Radishes have antibacterial and anti-fungal properties.");
		this.addFactoid("Sweet potatoes are high in sugar and should be used sparingly.");
		this.addFactoid("A tomato grown in a hothouse has half the vitamin C content as a vine-ripened tomato.");
		this.addFactoid("Zucchinis can grow as large as baseball bats but have little flavor when they reach this size.");
		this.addFactoid("A 20 oz. bottle of Coca-Cola has more sugar than a large Cinnabon.");
		this.addFactoid("Ginger can help reduce exercise--induced muscle pain by up to 25%.");
		this.addFactoid("Neither strawberries, raspberries, or blackberries are actually berries.");
		this.addFactoid("Bananas, tomatoes, pumpkin, watermelon and avocado are all berries.");
		this.addFactoid("On a calorie-by-calorie basis, oysters provide 4 times more vitamin D than skim milk.");
		this.addFactoid("Chia sees are capable of absorbing 9-10 times their weight in water.");
		this.addFactoid("Over 69% of American adults are either overweight or obese.");
		this.addFactoid("Alaska produces more than half of all seafood caught in the US.");
		this.addFactoid("1 cup of air-popped popcorn contains more antioxidants than the average fruit or vegetable.");
		this.addFactoid("9-in-10 Americans are potassium deficient.");
		this.addFactoid("If fishing continues on its current trajectory, the entire global fishing industry is expected to reach a state of collapse by 2048.");
		this.addFactoid("Evidence suggests Aspartame and MSG promote cancer growth and its propensity to spread.");
		this.addFactoid("Soda intake leaches calcium from bones, which contributes to lower bone mineral density and the development of osteoporosis.");
		this.addFactoid("Garlic may help prevent against various types of stomach, GI, and colorectal cancers.");
		this.addFactoid("Pecans and walnuts have more antioxidants than blueberries, cranberries, blackberries, and currants.");
	}

	private void addFactoid(String f) {
		factoid.add(f);
	}

	private String getFactoid(int index) {
		return factoid.get(index);
	}

	private int getListSize() {
		return factoid.size();
	}

	private int getRandomIndex() {
		int n = this.getListSize();
		int randomIndex = (int) Math.round(Math.random() * (n - 1));
		return randomIndex;

	}

	public String getInitialFactoid() {
		currentPointer = 0;
		int randomIndex = getRandomIndex();
		indexHistory.add(randomIndex);
		return getFactoid(randomIndex);
	}

	public String getNext() {
		String factoid_return = "";
		int randomIndex;
		if (indexHistory.size() == currentPointer + 1) {
			// pointed to last
			randomIndex = getRandomIndex();
			factoid_return = getFactoid(randomIndex);
			indexHistory.add(randomIndex);
		} else {
			// traverse through history
			factoid_return = getFactoid(indexHistory.get(currentPointer + 1));
		}
		// move pointer to next
		currentPointer++;

		return factoid_return;

	}

	public String getPrev() {
		String factoid_return = "";
		if (currentPointer == 0) {
			// pointed to first
			factoid_return = getFactoid(indexHistory.get(0));
		} else {
			// traverse through history
			factoid_return = getFactoid(indexHistory.get(currentPointer - 1));
			currentPointer--;
		}

		return factoid_return;

	}
}
