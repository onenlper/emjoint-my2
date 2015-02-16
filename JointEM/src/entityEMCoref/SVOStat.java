package entityEMCoref;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

import model.EntityMention.Animacy;
import model.EntityMention.Gender;
import model.EntityMention.Numb;
import model.EntityMention.Person;

import util.Common;

public class SVOStat implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public HashMap<String, Integer> unigrams;
	public int unigramAll = 1246070771;

	public HashMap<String, Integer> svCounts;
	public HashMap<String, Integer> svoCounts;

	public HashMap<String, Integer> vCounts;
	public HashMap<String, Integer> voCounts;
	public int svoAll = 66724995;
	
	public HashMap<String, HashMap<Integer, Integer>> numberStat;
	public HashMap<String, HashMap<Integer, Integer>> genderStat;
	public HashMap<String, HashMap<Integer, Integer>> personStat;
	public HashMap<String, HashMap<Integer, Integer>> animacyStat;
	
	
	public HashMap<String, HashMap<Integer, Integer>> numberStat2;
	public HashMap<String, HashMap<Integer, Integer>> genderStat2;
	public HashMap<String, HashMap<Integer, Integer>> personStat2;
	public HashMap<String, HashMap<Integer, Integer>> animacyStat2;
	static HashSet<String> allV = Common.readFile2Set("allV");
	static HashSet<String> allO = Common.readFile2Set("allO");
	public void loadMIInfo() {
		try {
			unigrams = new HashMap<String, Integer>();
			BufferedReader br = new BufferedReader(new FileReader(
					"unigram.giga"));
			String line = "";
			while ((line = br.readLine()) != null) {
				int k = line.lastIndexOf(' ');
				int count = Integer.parseInt(line.substring(k + 1));
				String key = line.substring(0, k).trim();
				addMap(unigrams, key, count);
			}
			br.close();

			br = new BufferedReader(new FileReader("svo.giga"));
			svCounts = new HashMap<String, Integer>();
			vCounts = new HashMap<String, Integer>();
			svoCounts = new HashMap<String, Integer>();
			voCounts = new HashMap<String, Integer>();

			numberStat = new HashMap<String, HashMap<Integer, Integer>>();
			genderStat = new HashMap<String, HashMap<Integer, Integer>>();
			personStat = new HashMap<String, HashMap<Integer, Integer>>();
			animacyStat = new HashMap<String, HashMap<Integer, Integer>>();
			
			numberStat2 = new HashMap<String, HashMap<Integer, Integer>>();
			genderStat2 = new HashMap<String, HashMap<Integer, Integer>>();
			personStat2 = new HashMap<String, HashMap<Integer, Integer>>();
			animacyStat2 = new HashMap<String, HashMap<Integer, Integer>>();
			
			while ((line = br.readLine()) != null) {
				int k = line.lastIndexOf(' ');
				int count = Integer.parseInt(line.substring(k + 1));
				String key = line.substring(0, k).trim();
				String tks[] = key.split("\\s+");
				String s = tks[0];
				String v = tks[1];
				
				if(!allV.contains(v)) {
					continue;
				}
				
				addMap(svCounts, s + " " + v, count);
				addMap(vCounts, v, count);
				
				Numb num = EMUtil.getAntNumber(s);
				addAttri(v, numberStat, num.ordinal(), count);
				
				Gender gen = EMUtil.getAntGender(s);
				if(gen!=Gender.UNKNOWN) {
					addAttri(v, genderStat, gen.ordinal(), count);
				}
				Person per = EMUtil.getAntPerson(s);
				addAttri(v, personStat, per.ordinal(), count);
				
				Animacy ani = EMUtil.getAntAnimacy(s);
				if(ani!=Animacy.UNKNOWN) {
					addAttri(v, animacyStat, ani.ordinal(), count);
				}

				if (tks.length == 3) {
					String o = tks[2];
					if(!allO.contains(o)) {
						continue;
					}
					addMap(svoCounts, s + " " + v + " " + o, count);
					addMap(voCounts, v + " " + o, count);
					
					key = v + " " + o;
					addAttri(key, numberStat2, num.ordinal(), count);
					
					if(gen!=Gender.UNKNOWN) {
						addAttri(key, genderStat2, gen.ordinal(), count);
					}
					addAttri(key, personStat2, per.ordinal(), count);
					
					if(ani!=Animacy.UNKNOWN) {
						addAttri(key, animacyStat2, ani.ordinal(), count);
					}
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addAttri(String s, HashMap<String, HashMap<Integer, Integer>> stat, int attrVal, int count) {
		HashMap<Integer, Integer> map = stat.get(s);
		if(map==null) {
			map = new HashMap<Integer, Integer>();
			stat.put(s, map);
		}
		Integer i = map.get(attrVal);
		if(i==null) {
			map.put(attrVal, count);
		} else {
			map.put(attrVal, count + i.intValue());
		}
	}
	
	
	public void loadMIInfo2() {
		try {
			unigrams = new HashMap<String, Integer>();
			BufferedReader br = new BufferedReader(new FileReader(
					"unigram.giga"));
			String line = "";
			while ((line = br.readLine()) != null) {
				int k = line.lastIndexOf(' ');
				int count = Integer.parseInt(line.substring(k + 1));
				String tks[] = line.split("\\s+");
				String key = EMUtil.getAntAnimacy2(tks[0]).name();
//				System.out.println(key);
				addMap(unigrams, key, count);
			}
			br.close();

			br = new BufferedReader(new FileReader("svo.giga"));
			svCounts = new HashMap<String, Integer>();
			vCounts = new HashMap<String, Integer>();
			svoCounts = new HashMap<String, Integer>();
			voCounts = new HashMap<String, Integer>();

			while ((line = br.readLine()) != null) {
				int k = line.lastIndexOf(' ');
				int count = Integer.parseInt(line.substring(k + 1));
				String key = line.substring(0, k).trim();
				String tks[] = key.split("\\s+");
				String s = tks[0];
				
				s = EMUtil.getAntAnimacy2(tks[0]).name();				
				
				String v = tks[1];

				addMap(svCounts, s + " " + v, count);
				addMap(vCounts, v, count);

				if (tks.length == 3) {
					String o = tks[2];
					addMap(svoCounts, s + " " + v + " " + o, count);
					addMap(voCounts, v + " " + o, count);
				}

			}
			br.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addMap(HashMap<String, Integer> map, String key, int val) {
		Integer count = map.get(key);
		if (count == null) {
			map.put(key, val);
		} else {
			map.put(key, count.intValue() + val);
		}
	}
}
