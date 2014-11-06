package com.ganesia.crimereport.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class Crime {
	
	private List<CrimeItem> crimeList = new ArrayList<CrimeItem>();
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	public Crime(Crime _Crime) {
		// deep copy
		for (CrimeItem c : _Crime.getCrimeList()) {
			this.crimeList.add(c);
		}
	}
	
	/**
	* 
	* @return
	* The crimeList
	*/
	public List<CrimeItem> getCrimeList() {
	return crimeList;
	}
	
	/**
	* 
	* @param crimeList
	* The crimeList
	*/
	public void setCrimeList(List<CrimeItem> crimeList) {
	this.crimeList = crimeList;
	}
	
	public Map<String, Object> getAdditionalProperties() {
	return this.additionalProperties;
	}
	
	public void setAdditionalProperty(String name, Object value) {
	this.additionalProperties.put(name, value);
	}
	
	// calculation
	public LinkedHashMap<String,Integer> topThreeCrime() {
		// create HashMap
		Map<String, List<CrimeItem>> hm = new HashMap<String, List<CrimeItem>>();
		// arrange crime data based on its crime type
		for (CrimeItem c : this.crimeList) {
			// check if key does not exist
			if (!hm.containsKey(c.getCrimeType())) {
				// initialize a list of CrimeItem
				List<CrimeItem> tempCrimeList = new ArrayList<CrimeItem>();
				tempCrimeList.add(c); 
				hm.put(c.getCrimeType(), tempCrimeList);
			}
			else {
				// if key exists, append the element to the tempCrimeList
				hm.get(c.getCrimeType()).add(c);
			}
		}
		// sort and return the top three crime
		return sortHashMapByValues(hm);
	
	}
	
	private LinkedHashMap<String, Integer> sortHashMapByValues(Map<String,List<CrimeItem>> passedMap) {
		LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		
		List<String> mapKeys = new ArrayList<String>(passedMap.keySet());
		List mapValues = new ArrayList(passedMap.values());
		// convert mapValues from List<CrimeItem> to List<Integer>
		for (int i=0; i < mapValues.size(); i++) {
			// type of e is List<CrimeItem>
			// casting object to List<CrimeItem>
			List<CrimeItem> castedObject = (List<CrimeItem>) mapValues.get(i);
			mapValues.set(i, castedObject.size());
		}
		
		// ascending sort
		Collections.sort(mapValues);
		Collections.sort(mapKeys);
		// descending sort by using reverse
		Collections.reverse(mapValues); 
		Collections.reverse(mapKeys);
		
		Iterator valueIt = mapValues.iterator();
		int flag = 0;
		while (valueIt.hasNext() && flag <3) {
			int val = (Integer) valueIt.next();
			Iterator keyIt = mapKeys.iterator();
			
			while (keyIt.hasNext()) {
				Object key = keyIt.next();
				List<CrimeItem> temp = (List<CrimeItem>) passedMap.get(key);
				int comp1 = temp.size();
				int comp2 = val;
				
				if (comp1 == comp2){
					passedMap.remove(key);
					mapKeys.remove(key);
					sortedMap.put((String)key, (Integer)val);
					break;
				}
			}
			flag++;
			// to limit the results only 3
		}
		
		return sortedMap;
	}
}