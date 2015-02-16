package entityEMCoref;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.ACEDoc;
import model.Entity;
import model.EntityMention;
import model.EntityMention.Animacy;
import model.EntityMention.Gender;
import model.EntityMention.Grammatic;
import model.EntityMention.MentionType;
import model.EntityMention.Person;
import model.EntityMention.Numb;
import model.syntaxTree.MyTree;
import model.syntaxTree.MyTreeNode;

import org.tartarus.martin.Stemmer;

import util.Common;
//import edu.stanford.nlp.ling.BasicDatum;
//import edu.stanford.nlp.ling.Datum;

public class EMUtil {

	public static boolean train;

	public static double alpha = 1;
	// 0.0000001;
	// -7

	public static HashSet<String> location = Common
			.readFile2Set("location_suffix");

	public static HashSet<String> pronouns = new HashSet<String>(Arrays.asList(
			"你", "我", "他", "她", "它", "你们", "我们", "他们", "她们", "它们"
	// , "这", "这里", "那", "那里"
			));

	public static ArrayList<String> engPronounList = new ArrayList<String>(
			Arrays.asList("you", "i", "he", "she", "it", "we", "they"));

	public static HashSet<String> engPronouns = new HashSet<String>(
			Arrays.asList("you", "i", "he", "she", "it", "we", "they"));

	public static HashMap<String, Integer> pronounArr = new HashMap<String, Integer>() {
		/**
				 * 
				 */
		public static final long serialVersionUID = 1L;
		{
			put("你", 1);
			put("我", 2);
			put("他", 3);
			put("她", 4);
			put("它", 5);
			put("你们", 1);
			put("我们", 2);
			put("他们", 3);
			put("她们", 4);
			put("它们", 5);
		}
	};

//	public static enum Person {
//		first, second, third
//	};
//
//	public static enum Numb {
//		single, plural, fake
//	};
//
//	public static enum Gender {
//		male, female, neuter, unknown, fake
//	};

	public static enum PersonEng {
		I, YOU, HE, SHE, WE, THEY, IT, UNKNOWN, YOUS
	}

//	public static enum Animacy {
//		animate, unanimate, unknown, fake
//	}
//
//	public static enum Grammatic {
//		subject, object, modifier, other
//	};
//
//	public static enum MentionType {
//		pronoun, proper, common, tmporal
//	}

	public static String getSemantic(EntityMention m) {
		if (true) {
			return m.head;
		}
		if (!m.ner.equals("OTHER")) {
			return m.ner;
		}
		String sems[] = Common.getSemantic(m.head);
		String sem = "unknown";
		if (sems != null) {
			sem = sems[0];
		}
		return sem.substring(0, 7);
	}

	public static String getCilin(EntityMention m) {
		if (!m.ner.equals("OTHER")) {
			return m.ner;
		}
		String sems[] = Common.getSemantic(m.head);
		String sem = "unknown";
		if (sems != null) {
			sem = sems[0];
		}
		return sem.substring(0, 5);
	}

//	public static CoNLLPart getGoldPart(CoNLLPart part, String stage) {
//		String documentID = "/users/yzcchen/chen3/CoNLL/conll-2012/v4/data/"
//				+ stage + "/data/chinese/annotations/" + part.docName
//				+ ".v4_gold_conll";
//		// System.out.println(documentID);
//		CoNLLDocument document = new CoNLLDocument(documentID);
//		CoNLLPart goldPart = document.getParts().get(part.getPartID());
//		return goldPart;
//	}

//	public static HashSet<Integer> getPRHeads(CoNLLPart part) {
//		HashSet<Integer> prSet = new HashSet<Integer>();
//		for (CoNLLSentence s : part.getCoNLLSentences()) {
//			for (CoNLLWord w : s.getWords()) {
//				if (w.posTag.equals("PN")) {
//					prSet.add(w.index);
//				}
//			}
//		}
//		return prSet;
//	}

//	public static HashSet<Integer> getNEHeads(CoNLLPart part) {
//		HashSet<Integer> set = new HashSet<Integer>();
//		for (Element ele : part.getNameEntities()) {
//			set.add(ele.end);
//		}
//		return set;
//	}

	public static HashSet<String> firsts = new HashSet<String>(Arrays.asList(
			"我", "我们"));

	public static HashSet<String> seconds = new HashSet<String>(Arrays.asList(
			"你", "你们"));

	public static HashSet<String> thirds = new HashSet<String>(Arrays.asList(
			"他", "她", "它", "他们", "她们", "它们"));

	public static HashSet<String> singles = new HashSet<String>(Arrays.asList(
			"你", "我", "他", "她", "它"));

	public static HashSet<String> plurals = new HashSet<String>(Arrays.asList(
			"你们", "我们", "他们", "她们", "它们"));

	public static HashSet<String> males = new HashSet<String>(Arrays.asList(
			"他", "他们"));

	public static HashSet<String> females = new HashSet<String>(Arrays.asList(
			"她", "她们"));

	public static HashSet<String> neuters = new HashSet<String>(Arrays.asList(
			"它", "它们", "你", "我", "我们", "你们"));

	public static HashSet<String> animates = new HashSet<String>(Arrays.asList(
			"你", "我", "他", "她", "你们", "我们", "他们", "她们"));

	public static HashSet<String> unanimates = new HashSet<String>(
			Arrays.asList("它", "它们"));

	public final static Set<String> removeChars = new HashSet<String>(
			Arrays.asList(new String[] { "什么的", "哪", "什么", "谁", "啥", "哪儿",
					"哪里", "人们", "年", "原因", "啥时", "问题", "情况", "未来", "战争", "人",
					"时候", "可能" }));

	public static ArrayList<String> pronounList = new ArrayList<String>(
			Arrays.asList("你", "我", "他", "她", "它", "你们", "我们", "他们", "她们", "它们"));

//	public static void addEmptyCategoryNode(EntityMentionMention zero) {
//		MyTreeNode V = zero.V;
//		MyTreeNode newNP = new MyTreeNode();
//		newNP.value = "NP";
//		int VIdx = V.childIndex;
//		V.parent.addChild(VIdx, newNP);
//
//		MyTreeNode empty = new MyTreeNode();
//		empty.value = "-NONE-";
//		newNP.addChild(empty);
//
//		MyTreeNode child = new MyTreeNode();
//		child.value = "*pro*";
//		empty.addChild(child);
//		child.emptyCategory = true;
//		zero.NP = newNP;
//	}

//	public static HashMap<String, HashMap<String, HashSet<String>>> extractGoldKeys() {
//		HashMap<String, HashMap<String, HashSet<String>>> allKeys = new HashMap<String, HashMap<String, HashSet<String>>>();
//		ArrayList<String> fnLines = Common.getLines("chinese_list_all_test");
//		for (String line : fnLines) {
//			CoNLLDocument goldDoc = new CoNLLDocument(line.replace(
//					"auto_conll", "gold_conll"));
//			for (CoNLLPart part : goldDoc.getParts()) {
//				HashMap<String, HashSet<String>> keys = EMUtil
//						.getGoldAnaphorKeys(part.getChains(), part);
//				allKeys.put(part.getPartName(), keys);
//			}
//		}
//		return allKeys;
//	}

//	public static HashMap<String, HashMap<String, String>> extractSysKeys(
//			String path) {
//		CoNLLDocument sysDoc = new CoNLLDocument(path);
//		HashMap<String, HashMap<String, String>> allSys = new HashMap<String, HashMap<String, String>>();
//		for (CoNLLPart part : sysDoc.getParts()) {
//			CoNLLPart goldPart = EMUtil.getGoldPart(part, "test");
//			HashSet<String> goldNEs = EMUtil.getGoldNEs(goldPart);
//			HashSet<String> goldPNs = EMUtil.getGoldPNs(goldPart);
//
//			HashMap<String, String> sys = new HashMap<String, String>();
//			allSys.put(part.getPartName(), sys);
//			ArrayList<Entity> chains = part.getChains();
//			for (Entity e : chains) {
//				Collections.sort(e.mentions);
//				for (int i = 0; i < e.mentions.size(); i++) {
//					Mention m1 = e.mentions.get(i);
//					if (goldNEs.contains(m1.toName())
//							|| goldPNs.contains(m1.toName())
//							|| goldNEs.contains(m1.end + "," + m1.end)) {
//						continue;
//					}
//
//					for (int j = i - 1; j >= 0; j--) {
//						Mention m2 = e.mentions.get(j);
//						if (!goldPNs.contains(m2.toName()) && m2.end != m1.end) {
//							String[] s = new String[2];
//							s[0] = m1.toName();
//							s[1] = m2.toName();
//							sys.put(m1.toName(), m2.toName());
//							break;
//						}
//					}
//				}
//			}
//		}
//		return allSys;
//	}

//	public static HashMap<String, CoNLLSentence> loadTranslateEngCoNLL(
//			CoNLLDocument doc, String setting) {
//		ArrayList<String> chiLines = Common.getLines(setting + "/docs/train.f");
//		HashMap<String, CoNLLSentence> sMap = new HashMap<String, CoNLLSentence>();
//		for (int i = 0; i < chiLines.size(); i++) {
//			String chi = chiLines.get(i);
//			CoNLLSentence s = doc.getParts().get(0).getCoNLLSentences().get(i);
//			sMap.put(chi, s);
//		}
//		return sMap;
//	}

	public static short getProIdx(String extent) {
		int idx = pronounList.indexOf(extent);
		return (short) idx;
	}

	public static Numb getNumber(String pro) {
		if (singles.contains(pro)) {
			return Numb.SINGULAR;
		} else if (plurals.contains(pro)) {
			return Numb.PLURAL;
		} else {
			Common.bangErrorPOS("Not support number: " + pro);
			return null;
		}
	}

	public static Gender getGender(String pro) {
		if (males.contains(pro)) {
			return Gender.MALE;
		} else if (females.contains(pro)) {
			return Gender.FEMALE;
		} else if (neuters.contains(pro)) {
			return Gender.NEUTRAL;
		} else {
			Common.bangErrorPOS("Not support gender: " + pro);
			return null;
		}
	}

	public static Person getPerson(String pro) {
		if (firsts.contains(pro)) {
			return Person.FIRST;
		} else if (seconds.contains(pro)) {
			return Person.SECOND;
		} else if (thirds.contains(pro)) {
			return Person.THIRD;
		} else {
			Common.bangErrorPOS("Not support person: " + pro);
			return null;
		}
	}

	public static Person getAntPerson(String pro) {
		if (firsts.contains(pro)) {
			return Person.FIRST;
		} else if (seconds.contains(pro)) {
			return Person.SECOND;
		} else {
			return Person.THIRD;
		}
	}

	public static Animacy getAnimacy(String pro) {
		if (animates.contains(pro)) {
			return Animacy.ANIMATE;
		} else if (unanimates.contains(pro)) {
			return Animacy.INANIMATE;
		} else {
			Common.bangErrorPOS("Not support animacy: " + pro);
			return null;
		}
	}

//	public static ArrayList<EntityMention> getAnaphorZeros(ArrayList<Entity> chains) {
//		ArrayList<EntityMention> zeros = new ArrayList<EntityMention>();
//		for (Entity entity : chains) {
//			for (int i = 0; i < entity.mentions.size(); i++) {
//				Mention m2 = entity.mentions.get(i);
//				if (m2.end != -1) {
//					continue;
//				}
//				for (int j = 0; j < i; j++) {
//					Mention m1 = entity.mentions.get(j);
//					if (m1.end != -1) {
//						zeros.add(m2);
//						break;
//					}
//				}
//			}
//		}
//		return zeros;
//	}

//	public static ArrayList<EntityMention> getZeros(ArrayList<Entity> chains) {
//		ArrayList<EntityMention> zeros = new ArrayList<EntityMention>();
//		for (Entity entity : chains) {
//			for (int i = 0; i < entity.mentions.size(); i++) {
//				Mention m2 = entity.mentions.get(i);
//				if (m2.end == -1) {
//					zeros.add(m2);
//				}
//			}
//		}
//		return zeros;
//	}

	public static void setPronounAttri(EntityMention m, ACEDoc part) {
//		if (m.isAZP) {
//			ArrayList<EntityMention> corefs = new ArrayList<EntityMention>();
//			Mention represent = null;
//			for (EntityMention t : m.entity.mentions) {
//				if ((!t.equals(m)) && t.end != -1) {
//					corefs.add(t);
//				}
//				if (EMUtil.pronouns.contains(t.extent)) {
//					represent = t;
//				}
//			}
//			if (represent != null) {
//				setPronounAttri(represent, part);
//				m.number = represent.number;
//				m.gender = represent.gender;
//
//				if (part.getWord(m.start).speaker.equals(part
//						.getWord(represent.start).speaker)) {
//					m.person = represent.person;
//				} else if (!part.getWord(m.start).speaker.equals(part
//						.getWord(represent.start).speaker)) {
//					if (represent.person.equals(Person.FIRST)) {
//						m.person = Person.SECOND;
//					} else if (represent.person.equals(Person.SECOND)) {
//						m.person = Person.FIRST;
//					} else {
//						m.person = Person.THIRD;
//					}
//				}
//				m.animacy = represent.animacy;
//			} else {
//				int[] numbers = new int[Numb.values().length];
//				int[] genders = new int[Gender.values().length];
//				int[] persons = new int[Person.values().length];
//				int[] animacys = new int[Animacy.values().length];
//				for (EntityMention t : corefs) {
//					// TODO
//					t.animacy = EMUtil.getAntAnimacy(t);
//					animacys[t.animacy.ordinal()]++;
//
//					t.gender = EMUtil.getAntGender(t);
//					genders[t.gender.ordinal()]++;
//					t.number = EMUtil.getAntNumber(t);
//					numbers[t.number.ordinal()]++;
//
//					t.person = EMUtil.getAntPerson(t.head);
//					if (part.getWord(m.start).speaker.equals(part
//							.getWord(t.start).speaker)) {
//						persons[t.person.ordinal()]++;
//					} else {
//						if (t.person == Person.FIRST) {
//							persons[Person.SECOND.ordinal()]++;
//						} else if (t.person.equals(Person.SECOND)) {
//							persons[Person.FIRST.ordinal()]++;
//						} else {
//							persons[Person.THIRD.ordinal()]++;
//						}
//					}
//				}
//
//				m.number = Numb.SINGULAR;
//				int max = 0;
//				for (int i = 0; i < numbers.length; i++) {
//					if (numbers[i] > max) {
//						max = numbers[i];
//						m.number = Numb.values()[i];
//					}
//				}
//
//				m.gender = Gender.MALE;
//				max = 0;
//				for (int i = 0; i < genders.length; i++) {
//					if (genders[i] > max) {
//						max = genders[i];
//						m.gender = Gender.values()[i];
//					}
//				}
//
//				m.person = Person.THIRD;
//				max = 0;
//				for (int i = 0; i < persons.length; i++) {
//					if (persons[i] > max) {
//						max = persons[i];
//						m.person = Person.values()[i];
//					}
//				}
//
//				m.animacy = Animacy.ANIMATE;
//				max = 0;
//				for (int i = 0; i < animacys.length; i++) {
//					if (animacys[i] > max) {
//						max = animacys[i];
//						m.animacy = Animacy.values()[i];
//					}
//				}
//			}
//
//			// find most reprsentive
//			// for(EntityMention m)
//			return;
//		}

		// assign number, gender, person, animacy
		if (EMUtil.singles.contains(m.head)) {
			m.number = Numb.SINGULAR;
		} else if (EMUtil.plurals.contains(m.head)) {
			m.number = Numb.PLURAL;
		} else {
			Common.bangErrorPOS("");
		}

		if (EMUtil.males.contains(m.head)) {
			m.gender = Gender.MALE;
		} else if (EMUtil.females.contains(m.head)) {
			m.gender = Gender.FEMALE;
		} else if (EMUtil.neuters.contains(m.head)) {
			m.gender = Gender.NEUTRAL;
		} else {
			Common.bangErrorPOS(m.head);
		}

		if (EMUtil.firsts.contains(m.head)) {
			m.person = Person.FIRST;
		} else if (EMUtil.seconds.contains(m.head)) {
			m.person = Person.SECOND;
		} else if (EMUtil.thirds.contains(m.head)) {
			m.person = Person.THIRD;
		} else {
			Common.bangErrorPOS(m.head);
		}

		if (EMUtil.animates.contains(m.head)) {
			m.animacy = Animacy.ANIMATE;
		} else if (EMUtil.unanimates.contains(m.head)) {
			m.animacy = Animacy.INANIMATE;
		} else {
			Common.bangErrorPOS(m.head);
		}
	}

//	public static void assignVNode(EntityMention zero, CoNLLPart part) {
//		MyTreeNode V = null;
//		zero.sentenceID = part.getWord(zero.start).sentence.getSentenceIdx();
//		CoNLLSentence s = part.getCoNLLSentences().get(zero.sentenceID);
//		MyTreeNode root = s.syntaxTree.root;
//		CoNLLWord word = part.getWord(zero.start);
//		MyTreeNode leaf = root.getLeaves().get(word.indexInSentence);
//
//		for (MyTreeNode node : leaf.getAncestors()) {
//			if (node.value.toLowerCase().startsWith("vp")
//					&& node.getLeaves().get(0) == leaf) {
//				V = node;
//			}
//		}
//
//		if (V == null) {
//			for (MyTreeNode node : leaf.getAncestors()) {
//				if (node.value.startsWith("DFL")
//						&& node.getLeaves().get(0) == leaf) {
//					V = node;
//				}
//			}
//		}
//
//		if (V == null) {
//			int offset = 1;
//			while (true) {
//				word = part.getWord(zero.start + (offset++));
//				leaf = root.getLeaves().get(word.indexInSentence);
//				for (MyTreeNode node : leaf.getAncestors()) {
//					if (node.value.toLowerCase().startsWith("vp")
//							&& node.getLeaves().get(0) == leaf) {
//						V = node;
//					}
//				}
//				if (V != null) {
//					break;
//				}
//				if (zero.start + offset == part.getWordCount()) {
//					break;
//				}
//			}
//		}
//
//		if (V == null) {
//			leaf = root.getLeaves().get(
//					part.getWord(zero.start).indexInSentence);
//			for (MyTreeNode node : leaf.getAncestors()) {
//				if (node.value.startsWith("NP")
//						&& node.getLeaves().get(0) == leaf) {
//					V = node;
//				}
//			}
//		}
//		zero.V = V;
//	}

	public static Numb getAntNumber(String str) {
		boolean plura = false;

		if (str.contains("和")) {
			plura = true;
		}
		if (str.contains("些")) {
			plura = true;
		}
		if (str.contains("多")) {
			plura = true;
		}

		if (str.endsWith("们")) {
			plura = true;
		}
		
		if (str.contains("每")) {
			plura = true;
		}

		if (plura) {
			return Numb.PLURAL;
		} else {
			return Numb.SINGULAR;
		}
	}

	public static Numb getAntNumber(EntityMention mention) {
		MyTreeNode np = mention.treeNode;
		boolean plura = false;
		for (MyTreeNode leaf : np.getLeaves()) {
			if (leaf.value.equals("、")) {
				plura = true;
			}
			if (leaf.parent.value.equals("CD") && !leaf.value.equals("一")) {
				plura = true;
			}
			if (leaf.value.equals("和")) {
				plura = true;
			}
			if (leaf.value.contains("些")) {
				plura = true;
			}
			if (leaf.value.contains("每")) {
				plura = true;
			}
			if (leaf.value.contains("多")) {
				plura = true;
			}
			if (leaf.value.contains("双")) {
				plura = true;
			}
		}
		if (mention.extent.endsWith("们")) {
			plura = true;
		}

		if (plura) {
			mention.number = Numb.PLURAL;
			return Numb.PLURAL;
		} else {
			mention.number = Numb.SINGULAR;
			return Numb.SINGULAR;
		}
	}

//	public static void assignNE(ArrayList<EntityMention> mentions,
//			ArrayList<Element> elements) {
//		if (elements == null) {
//			return;
//		}
//		for (EntityMention mention : mentions) {
//			int end = mention.end;
//			for (Element element : elements) {
//				if (element.start <= end && end <= element.end) {
//					// if (headStart == element.end) {
//					mention.NE = element.content;
//					// System.out.println(mention.extent + " : " + mention.NE);
//				}
//			}
//		}
//	}

	public static double getPrior(String key, HashMap<String, Double> map,
			double overall, int space) {
		double denominator = overall + space;
		double numerator = 1;
		if (map.containsKey(key)) {
			numerator += map.get(key);
		}
		return numerator / denominator;
	}

	public static MyTreeNode getMaxNPTreeNode(MyTreeNode node) {
		ArrayList<MyTreeNode> ancestors = node.getAncestors();
		MyTreeNode np = null;
		for (int i = ancestors.size() - 1; i >= 0; i--) {
			MyTreeNode tmp = ancestors.get(i);
			MyTreeNode lastLeaf = tmp.getLeaf(tmp.getLeaves().size() - 1);
			if (tmp.value.equals("NP") && lastLeaf == node) {
				np = tmp;
			}
		}
		if (np == null) {
			np = node.parent;
		}
		return np;
	}

	public static MyTreeNode getMinNPTreeNode(MyTreeNode node) {
		ArrayList<MyTreeNode> ancestors = node.getAncestors();
		MyTreeNode np = null;
		for (int i = 0; i < ancestors.size(); i++) {
			MyTreeNode tmp = ancestors.get(i);
			MyTreeNode lastLeaf = tmp.getLeaf(tmp.getLeaves().size() - 1);
			if (tmp.value.equals("NP") && lastLeaf == node) {
				np = tmp;
			}
		}
		if (np == null) {
			np = node.parent;
		}
		return np;
	}

	public static boolean characterContain(String h1, String h2) {
		HashSet<Character> c1 = new HashSet<Character>();
		for (int i = 0; i < h1.length(); i++) {
			c1.add(h1.charAt(i));
		}

		HashSet<Character> c2 = new HashSet<Character>();
		for (int i = 0; i < h2.length(); i++) {
			c2.add(h2.charAt(i));
		}

		return c1.containsAll(c2);
	}

	public static String getPorterStem(String w) {
		Stemmer stemmer = new Stemmer();
		for (int i = 0; i < w.length(); i++) {
			stemmer.add(w.charAt(i));
		}
		stemmer.stem();
		String s = stemmer.toString();
		return s;
	}

	public static HashMap<String, String> semanticMap = Common
			.readFile2Map2("semanticTypes.all");
	public static HashMap<String, String> subTypeMap = Common
			.readFile2Map2("subTypes.all");

//	public static String getACESubType(EntityMention m, CoNLLPart part) {
//		String instance = EMUtil.getSemanticInstance(m, part);
//		String subtype = EMUtil.getACESubType(instance);
//		if (subtype == null || subtype.equalsIgnoreCase("other")) {
//			if (m.NE.equals("PERSON")) {
//				if (m.number == Numb.SINGULAR) {
//					subtype = "p-individual";
//				} else {
//					subtype = "p-group";
//				}
//			} else if (!m.NE.equals("OTHER")) {
//				subtype = m.NE.toLowerCase();
//			} else {
//				String sems[] = Common.getSemantic(m.head);
//				String sem = "unknown";
//				if (sems != null) {
//					sem = sems[0];
//				}
//				return sem.substring(0, 4);
//			}
//		}
//		return subtype;
//	}

//	private static String getACEType(EntityMention m, CoNLLPart part) {
//		String instance = EMUtil.getSemanticInstance(m, part);
//		String subtype = "";
//		if (m.NE.equals("PERSON")) {
//			subtype = "per";
//		} else if (!m.NE.equals("OTHER")) {
//			subtype = m.NE.toLowerCase();
//		} else {
//			subtype = EMUtil.getACEType(instance);
//			if (subtype == null || subtype.equalsIgnoreCase("other")) {
//				String sems[] = Common.getSemantic(m.head);
//				String sem = "unknown";
//				if (sems != null) {
//					sem = sems[0];
//				}
//				subtype = sem.substring(0, 4);
//			}
//		}
//		
//		return subtype;
//	}

//	private static String getACEType(EntityMention m, CoNLLPart part) {
//		String instance = EMUtil.getSemanticInstance(m, part);
//		String subtype = EMUtil.getACEType(instance);
//		// String subtype = semanticMap.get(m.head.replaceAll("\\s+", ""));
//		if (subtype == null || subtype.equalsIgnoreCase("other")) {
//			if (m.NE.equals("PERSON")) {
//				subtype = "per";
//			} else if (!m.NE.equals("OTHER")) {
//				subtype = m.NE.toLowerCase();
//			} else {
//				String sems[] = Common.getSemantic(m.head);
//				String sem = "unknown";
//				if (sems != null) {
//					sem = sems[0];
//				}
//				return sem.substring(0, 4);
//			}
//		}
//		return subtype;
//	}

	public static double getP_C(EntityMention ant, EntityMention m, ACEDoc part) {
		if (ant.isFake) {
			return 0;
		}

		double ret = 0;

		if (ant.gender != m.gender || ant.number != m.number
				|| ant.animacy != m.animacy) {
			return 0;
		}

		String subtype1 = ant.subType;
		String subtype2 = m.subType;

		if (subtype1.equals("unkn") || subtype2.equals("unkn")
				|| !subtype1.equals(subtype2)) {
			if (!ant.head.equals(m.head)) {
//				return 0;
			}
		}

		// // if(!ant.head.contains(m.head)) {
		// //// return 0;
		// // }
		//
		 if (m.gram == Grammatic.subject) {
		 double mi1 = EMUtil.calMISubject(m, m, part);
		 double mi2 = EMUtil.calMISubject(ant, m, part);
		 if (mi2 < 0 && mi2 < mi1) {
		 return 0;
		 }
		 }
		 if (m.gram == Grammatic.object) {
		 double mi1 = EMUtil.calMIObject(m, m, part);
		 double mi2 = EMUtil.calMIObject(ant, m, part);
		 if (mi2 < 0 && mi2 < mi1) {
		 return 0;
		 }
		 }

		if (Context.wordInclusion(ant, m, part) == 0) {
			return 0;
		}

		if (Context.isIWithI(ant, m, part) == 1) {
			return 0;
		}

//		int sDiss = m.s. - ant.s.getSentenceIdx();
//		if (ant.head.contains(m.head) || sDiss < 0 || true) {
			return 1;
//		}

		// if(ant.end!=ant.start && part.getWord(ant.end-1).word.equals(m.head)
		// &&
		// (ant.NE.equals("PERSON") || ant.NE.equals("LOC") ||
		// ant.NE.equals("GPE") || part.getWord(ant.end).posTag.equals("NR"))
		// && part.getWord(m.end).posTag.equals("NN")
		// ) {
		// return 2;
		// }
//		return 0;
		// if(Context.wordInclusion(ant, m, part)==0 &&
		// part.getWord(ant.headID).posTag.equals("NN")) {
		// return 0;
		// }
		//
		// if(Context.chHaveDifferentLocation(ant, m, part)==1) {
		// return 0;
		// }
		// if (Context.numberInLaterMention(ant, m, part) == 1) {
		// // return 0;
		// }
		// if(ant.start==m.start && m.start==m.end &&
		// (ant.extent.contains(m.extent)||m.extent.contains(ant.extent))) {
		// return 1;
		// }
	}

	public static final double log2 = Math.log(2);

	public static double klDivergence(double[] p1, double[] p2) {
		double klDiv = 0.0;
		for (int i = 0; i < p1.length; ++i) {
			if (p1[i] == 0) {
				continue;
			}
			if (p2[i] == 0.0) {
				continue;
			} // Limin
			klDiv += p1[i] * Math.log(p1[i] / p2[i]);
		}
		return klDiv / log2; // moved this division out of the loop -DM
	}

//	public static String getModifiers(EntityMention em, ACEDoc doc) {
//		List<String> removeW = Arrays.asList(new String[] { "这个", "这", "那个",
//				"全", "此", "本", "那", "自己", "的", "该", "公司", "这些", "那些", "'s" });
//		ArrayList<String> removeWords = new ArrayList<String>();
//		removeWords.addAll(removeW);
//		
//		for (MyTreeNode leaf : em.maxTreeNode.getLeaves()) {
//
//		}
//		
////		for (int i = em.start; i < em.headID; i++) {
////			if (part.getWord(i).posTag.equalsIgnoreCase("DT") && i < em.end
////					&& part.getWord(i + 1).posTag.equalsIgnoreCase("M")) {
////				removeWords.add(part.getWord(i).word);
////				removeWords.add(part.getWord(i + 1).word);
////			}
////			if (part.getWord(i).posTag.equals("PU")) {
////				removeWords.add(part.getWord(i).word);
////			}
////		}
//		StringBuilder sb = new StringBuilder();
//		for (int i = em.start; i < em.headID; i++) {
//			String w = part.getWord(i).word;
//			if (removeWords.contains(w)) {
//				continue;
//			}
//			sb.append(w);
//		}
//		if (sb.toString().isEmpty()) {
//			return "nulllll";
//		} else {
//			return sb.toString();
//		}
//	}

//	public static void setMentionAttri(EntityMention m, CoNLLPart part) {
//		int startIdx = part.getWord(m.start).indexInSentence;
//		int endIdx = part.getWord(m.end).indexInSentence;
//		CoNLLSentence sentence = part.getWord(m.start).sentence;
//		m.startInS = startIdx;
//		m.endInS = endIdx;
//		m.sentenceID = sentence.getSentenceIdx();
//		m.s = sentence;
//
//		// System.out.println(sentence.getSyntaxTree().leaves.size() + "#" +
//		// sentence.getWords().size());
//
//		MyTreeNode leftLeaf = sentence.getSyntaxTree().leaves.get(startIdx);
//		MyTreeNode rightLeaf = sentence.getSyntaxTree().leaves.get(endIdx);
//
//		ArrayList<MyTreeNode> leftAns = leftLeaf.getAncestors();
//		ArrayList<MyTreeNode> rightAns = rightLeaf.getAncestors();
//
//		// MyTreeNode treeNode = treeNode2;
//		MyTreeNode treeNode = null;
//		for (int i = 0; i < leftAns.size() && i < rightAns.size(); i++) {
//			// System.out.println(leftAns.get(i).value + "#" + rightAns.get(i) +
//			// " : " + (leftAns.get(i)==rightAns.get(i)));
//			if (leftAns.get(i) == rightAns.get(i)
//					&& leftAns.get(i).value.equals("NP")) {
//				ArrayList<MyTreeNode> leaves = leftAns.get(i).getLeaves();
//				if (leaves.get(leaves.size() - 1) == rightLeaf) {
//					treeNode = leftAns.get(i);
//					if (leaves.get(0) == leftLeaf) {
//						break;
//					}
//				}
//			} else if (leftAns.get(i) != rightAns.get(i)) {
//				break;
//			}
//		}
//
//		if (treeNode == null) {
//			for (int i = 0; i < leftAns.size() && i < rightAns.size(); i++) {
//				// System.out.println(leftAns.get(i).value + "#" +
//				// rightAns.get(i) + " : " + (leftAns.get(i)==rightAns.get(i)));
//				if (leftAns.get(i) == rightAns.get(i)
//						&& leftAns.get(i).value.equals("NP")) {
//					treeNode = leftAns.get(i);
//				} else if (leftAns.get(i) != rightAns.get(i)) {
//					break;
//				}
//			}
//		}
//
//		if (treeNode == null) {
//			treeNode = rightLeaf.parent;
//		}
//		// System.out.println(em.extent + " # " + part.getPartName());
//		MyTreeNode head = treeNode.getHeadLeaf();
//		// head = treeNode.getLeaves().get(treeNode.getLeaves().size());
//		m.headID = sentence.getWord(head.leafIdx).index;
//		m.headInS = head.leafIdx;
//		m.head = head.value;
//		m.NP = treeNode;
//
//		MyTreeNode maxTree = getMaxNPTreeNode(rightLeaf);
//		MyTreeNode minTree = getMinNPTreeNode(rightLeaf);
//		int begin = maxTree.getLeaves().get(0).leafIdx;
//		int end = minTree.getLeaves().get(0).leafIdx;
//
//		for (int i = begin; i < end; i++) {
//			m.modifyList.add(sentence.getWord(i).word);
//		}
//
//		// em.headInS = em.endInS;
//		// em.head = sentence.getWord(em.headInS).word;
//
//		if (m.head.equals(",")) {
//			sentence.syntaxTree.root.setAllMark(true);
//			// Common.bangErrorPOS(sentence.syntaxTree.root.getPlainText(true));
//		}
//
//		if (head.parent.value.equals("NR") || head.parent.value.equals("NNP")
//				|| head.parent.value.equals("NNPS")) {
//			m.mType = EMUtil.MentionType.proper;
//		} else if (head.parent.value.equals("PN")
//				|| head.parent.value.equals("PRP")
//				|| head.parent.value.equals("PRP$")) {
//			m.mType = EMUtil.MentionType.pronoun;
//		} else if (head.parent.value.equals("NN")
//				|| head.parent.value.equals("NNS")) {
//			m.mType = EMUtil.MentionType.common;
//		} else {
//			m.mType = EMUtil.MentionType.tmporal;
//		}
//		// check subject or object
//		boolean subject = false;
//
//		boolean haveNPAncestor = false;
//		if (treeNode != null) {
//			for (MyTreeNode tmp : treeNode.getAncestors()) {
//				if (tmp.value.equals("NP")
//						&& tmp.getLeaf(tmp.getLeaves().size() - 1) != treeNode
//								.getLeaf(treeNode.getLeaves().size() - 1)) {
//					haveNPAncestor = true;
//					break;
//				}
//			}
//		}
//
//		if (haveNPAncestor) {
//			m.nested = true;
//		}
//
//		if (haveNPAncestor) {
//			m.gram = Grammatic.modifier;
//		} else if (treeNode.parent == null) {
//			m.gram = EMUtil.Grammatic.other;
//		} else {
//			for (int i = treeNode.childIndex + 1; i < treeNode.parent.children
//					.size(); i++) {
//				MyTreeNode sibling = treeNode.parent.children.get(i);
//				if (sibling.value.equals("VP")) {
//					subject = true;
//					m.V = sibling;
//					break;
//				}
//			}
//			if (subject) {
//				m.gram = EMUtil.Grammatic.subject;
//			} else {
//				boolean object = false;
//				if (treeNode.parent.value.equals("VP")) {
//					for (int i = 0; i < treeNode.childIndex; i++) {
//						MyTreeNode sibling = treeNode.parent.children.get(i);
//						if (sibling.value.startsWith("V")) {
//							object = true;
//							m.V = sibling;
//							break;
//						}
//					}
//				}
//				if (object) {
//					m.gram = EMUtil.Grammatic.object;
//				}
//			}
//		}
//		m.animacy = EMUtil.getAntAnimacy(m);
//		m.gender = EMUtil.getAntGender(m);
//		m.number = EMUtil.getAntNumber(m);
//		m.semantic = EMUtil.getSemantic(m);
//
//		MyTreeNode ip = head.getAncestors().get(0);
//		for (int i = head.getAncestors().size() - 1; i >= 0; i--) {
//			MyTreeNode node = head.getAncestors().get(i);
//			if (node.value.equals("IP")) {
//				ip = node;
//				break;
//			}
//		}
//		for (MyTreeNode l : ip.getLeaves()) {
//			if (l.parent.value.equals("NT")) {
//				ArrayList<String> nts = m.moreModifiers.get("NT");
//				if (nts == null) {
//					nts = new ArrayList<String>();
//					m.moreModifiers.put("NT", nts);
//				}
//				nts.add(l.value);
//			}
//			if (l.parent.value.equals("CD")) {
//				ArrayList<String> cds = m.moreModifiers.get("CD");
//				if (cds == null) {
//					cds = new ArrayList<String>();
//					m.moreModifiers.put("CD", cds);
//				}
//				cds.add(l.value);
//			}
//		}
//
//		for (int i = m.start; i <= m.end; i++) {
//			if (part.getWord(i).posTag.equals("CC")
//					|| part.getWord(i).word.equals("、")) {
//				m.isCC = true;
//			}
//		}
//	}

//	public static Mention formPhrase(MyTreeNode treeNode, CoNLLSentence sentence) {
//		ArrayList<MyTreeNode> leaves = treeNode.getLeaves();
//		int startIdx = leaves.get(0).leafIdx;
//		int endIdx = leaves.get(leaves.size() - 1).leafIdx;
//		int start = sentence.getWord(startIdx).index;
//		int end = sentence.getWord(endIdx).index;
//		Mention em = new Mention();
//		em.start = start;
//		em.end = end;
//
//		StringBuilder sb = new StringBuilder();
//		for (int i = startIdx; i <= endIdx; i++) {
//			sb.append(sentence.getWord(i).word).append(" ");
//		}
//		em.extent = sb.toString().trim();
//		setMentionAttri(em, sentence.part);
//		// changeStr(em);
//		return em;
//	}

	public static void changeStr(EntityMention em) {
		if (em.extent.equals("这些")) {
			em.extent = "它们";
			em.head = "它们";
		}

		if (em.extent.equals("这") || em.extent.equals("那")
				|| em.extent.equals("这个")) {
			em.extent = "它";
			em.head = "它";
		}

		if (em.extent.equals("您")) {
			em.extent = "你";
			em.head = "你";
		}

		if (em.extent.equals("双方")) {
			em.extent = "他们";
			em.head = "他们";
		}
	}

	public static String getFirstVerb(MyTreeNode vp) {
		ArrayList<MyTreeNode> leaves = vp.getLeaves();
		for (MyTreeNode leaf : leaves) {
			if (leaf.parent.value.startsWith("V")) {
				return leaf.value;
			}
		}
		return "";
	}

//	public static ArrayList<EntityMention> extractMention(CoNLLPart part) {
//		ArrayList<EntityMention> ms = new ArrayList<EntityMention>();
//		for (CoNLLSentence s : part.getCoNLLSentences()) {
//			ms.addAll(extractMention(s));
//		}
//		return ms;
//	}

//	public static ArrayList<EntityMention> extractMention(CoNLLSentence sentence) {
//		ArrayList<EntityMention> nounPhrases = new ArrayList<EntityMention>();
//		MyTree tree = sentence.getSyntaxTree();
//		MyTreeNode root = tree.root;
//		ArrayList<MyTreeNode> frontie = new ArrayList<MyTreeNode>();
//		frontie.add(root);
//		while (frontie.size() > 0) {
//			MyTreeNode tn = frontie.remove(0);
//			if (tn.value.toUpperCase().startsWith("NP")) {
//				Mention element = formPhrase(tn, sentence);
//				if (element != null) {
//					nounPhrases.add(element);
//				}
//			}
//			ArrayList<MyTreeNode> tns = tn.children;
//			frontie.addAll(tns);
//		}
//
//		// remove inner NP
//		ArrayList<EntityMention> removes = new ArrayList<EntityMention>();
//		for (EntityMention s : nounPhrases) {
//			for (EntityMention l : nounPhrases) {
//				if (s.end == l.end && l.end - l.start > s.end - s.start) {
//					if (sentence.part.folder.equals("nw")) {
//						l.innerMs.add(s);
//					}
//					else
//					removes.add(s);
//				}
//			}
//		}
//
//		nounPhrases.removeAll(removes);
//		removeDuplicateMentions(nounPhrases);
//		Collections.sort(nounPhrases);
////		 if(true) {
////		 return nounPhrases;
////		 }
//
//		EMUtil.assignNE(nounPhrases, sentence.part.getNameEntities());
//
//		CoNLLPart part = sentence.part;
//		// TODO
//		for (int i = 0; i < nounPhrases.size(); i++) {
//			Mention mention = nounPhrases.get(i);
//			if (mention.NE.equalsIgnoreCase("QUANTITY")
//					|| mention.NE.equalsIgnoreCase("CARDINAL")
//					|| mention.NE.equalsIgnoreCase("PERCENT")
//					|| mention.NE.equalsIgnoreCase("MONEY")) {
//				removes.add(mention);
//				continue;
//			}
//
//			if (mention.extent.equalsIgnoreCase("我")
//					&& (mention.end + 2) < part.getWordCount()
//					&& part.getWord(mention.end + 1).word.equals("啊")
//					&& part.getWord(mention.end + 2).word.equals("，")) {
//				removes.add(mention);
//				continue;
//			}
//
//			if (ChDictionary.getInstance().removeChars.contains(mention.head)) {
//				removes.add(mention);
//				continue;
//			}
//
//			// 没 问题
//			if (mention.extent.equalsIgnoreCase("问题") && mention.start > 0
//					&& part.getWord(mention.start - 1).word.equals("没")) {
//				removes.add(mention);
//				continue;
//			}
//
//			// 你 知道
//			if (mention.extent.equalsIgnoreCase("你") && mention.start > 0
//					&& part.getWord(mention.start + 1).word.equals("知道")) {
//				removes.add(mention);
//				continue;
//			}
//
//			//
//			if (mention.extent.contains("什么") || mention.extent.contains("多少")) {
//				removes.add(mention);
//				continue;
//			}
//			String lastWord = part.getWord(mention.end).word;
//			if (mention.extent.endsWith("的")
//					|| (mention.extent.endsWith("人")
//							&& mention.start == mention.end && ChDictionary
//								.getInstance().countries.contains(lastWord
//							.substring(0, lastWord.length() - 1)))) {
//				removes.add(mention);
//				continue;
//			}
//			// ｑｕｏｔ
//			if (ChDictionary.getInstance().removeWords.contains(mention.extent)) {
//				removes.add(mention);
//				continue;
//			}
//		}
//		nounPhrases.removeAll(removes);
//		removeDuplicateMentions(nounPhrases);
//		Collections.sort(nounPhrases);
//
//		for (EntityMention m : nounPhrases) {
//			m.semClass = getACEType(m, part);
//			m.subType = getACESubType(m, part);
//
//			// if(m.ACESubtype.equals("p-individual")) {
//			// m.number = Numb.single;
//			// if(m.ACESubtype.equals("p-group")) {
//			// m.number = Numb.plural;
//			// }
//		}
//		return nounPhrases;
//	}

	private static void removeDuplicateMentions(ArrayList<EntityMention> mentions) {
		HashSet<EntityMention> mentionsHash = new HashSet<EntityMention>();
		mentionsHash.addAll(mentions);
		mentions.clear();
		mentions.addAll(mentionsHash);
	}

	// overall should be added 1
	public static int getBucket(int current, int overall, int buckets) {
		double perBucket = (double) overall / (double) buckets;
		int bucket = (int) (current * 1.0 / perBucket);

		if (bucket > 4) {
			Common.bangErrorPOS(current + " " + overall);
		}
		return bucket;
	}

	// public static Context buildContext_deprecate(EntityMention ant, Mention
	// pronoun,
	// CoNLLPart part) {
	// int antID = ant.end;
	// int pronounID = pronoun.start;
	//
	// int antSID = part.getWord(antID).sentence.getSentenceIdx();
	// int proSID = part.getWord(pronounID).sentence.getSentenceIdx();
	//
	// short senDis = (short) (proSID - antSID);
	//
	// short antPos = 0;
	// if (senDis == 0) {
	// antPos = (short) (pronounID - antID);
	// antPos = (short) getBucket(antPos,
	// part.getWord(pronounID).indexInSentence, 6);
	// } else {
	// antPos = (short) part.getWord(antID).indexInSentence;
	// antPos = (short) getBucket(antPos,
	// part.getWord(antID).sentence.words.size(), 6);
	// }
	//
	// short proPos = (short) getBucket(
	// part.getWord(pronounID).indexInSentence,
	// part.getWord(antID).sentence.words.size(), 4);
	//
	// short antSynactic = (short) Grammatic.subject.ordinal();
	//
	// short antType = (short) ant.mType.ordinal();
	//
	// return Context.getContext(senDis, antPos, antSynactic, proPos, antType);
	// }

	// public static Context buildContext(EntityMention ant, Mention pronoun,
	// int totalDis, int precedDis) {
	// short antSenPos = (short) (pronoun.sentenceID - ant.sentenceID);
	// int dis = 0;
	// if (antSenPos == 0) {
	// dis += pronoun.headInS - ant.headInS;
	// } else {
	// dis += pronoun.headInS;
	// dis += ant.headInS;
	// dis += precedDis;
	// }
	//
	// short antHeadPos = (short) ((dis) / ((totalDis + 1) / 6.0));
	// // short antGram = (short)ant.gram.ordinal();
	// short antGram = (short)Grammatic.subject.ordinal();
	// // short proPos = (short) (pronoun.headInS / (pronoun.s.words.size() /
	// 4.0));
	// short proPos = 1;
	// short antType = (short) ant.mType.ordinal();
	//
	// return Context.getContext(antSenPos, antHeadPos, antGram, proPos,
	// antType);
	// }

//	public static String getPathString(ArrayList<GraphNode> path) {
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < path.size() - 1; i++) {
//			GraphNode n = path.get(i);
//			if (i != 0) {
//				sb.append(n.token).append(" ");
//			}
//			String dep = n.getEdgeName(path.get(i + 1));
//			sb.append(dep).append(" ");
//		}
//
//		return sb.toString();
//	}

//	public static ArrayList<GraphNode> findPath(GraphNode from, GraphNode to) {
//		ArrayList<GraphNode> path = new ArrayList<GraphNode>();
//
//		HashSet<GraphNode> visited = new HashSet<GraphNode>();
//		ArrayList<GraphNode> fronties = new ArrayList<GraphNode>();
//		fronties.add(from);
//		if (from == null) {
//			return path;
//		}
//		if (to == null) {
//			return path;
//		}
//
//		if (from != to) {
//			loop: while (true) {
//				ArrayList<GraphNode> nextLevel = new ArrayList<GraphNode>();
//				for (GraphNode node : fronties) {
//					if (node == null) {
//						Common.bangErrorPOS("Null Dep node");
//					}
//					for (GraphNode next : node.nexts) {
//						if (!visited.contains(next)) {
//							next.backNode = node;
//							if (next.backNode == next) {
//								Common.bangErrorPOS("Self Dep: ");
//							}
//							if (next == to) {
//								break loop;
//							}
//							nextLevel.add(next);
//						}
//					}
//					visited.add(node);
//				}
//				fronties = nextLevel;
//				if (fronties.size() == 0) {
//					// Token t1 = Util.getTkFromDepNode(from, s);
//					// Token t2 = Util.getTkFromDepNode(to, s);
//					// System.out.println(t1.word + " " + t1.idInSentence);
//					// System.out.println(t2.word + " " + t2.idInSentence);
//					// System.out.println(s.d.fn);
//					// Common.bangErrorPOS("");
//					System.out.println("No Path");
//					return path;
//				}
//			}
//		}
//
//		GraphNode tmp = to;
//		while (true) {
//			path.add(0, tmp);
//			if (tmp == from) {
//				break;
//			}
//			tmp = tmp.backNode;
//			// System.out.println(fronties.size() + " " + (tmp==tmp.backNode));
//		}
//		return path;
//	}

	public static String measureStr = "把  般  班  瓣  磅  帮  包  辈  杯  本  笔  柄  拨  部  餐  册  层  场  场  成  尺  重  出  处  串  幢  床  次  簇  撮  打  袋  代  担  档  道  滴  点  顶  栋  堵  度  端  段  对  堆  队  顿  吨  朵  发  番  方  分  份  封  峰  付  幅  副  服  杆  个  根  公尺  公分  公斤  公里  公顷  公升  股  挂  管  行  盒  户  壶  伙  记  级  剂  架  家  加仑  件  间  绞  角  届  截  节  斤  茎  局  具  句  居  卷  圈  卡  客  棵  颗  克  孔  口  块  捆  类  里  粒  辆  两  列  立方英尺  立方米  领  缕  轮  摞  毛  枚  门  米  面  秒  名  亩  幕  排  派  盘  泡  喷  盆  匹  批  片  篇  撇  瓶  平方公里  期  起  爿  千克  千瓦  顷  曲  圈  群  人工  扇  勺  身  升  手  首  束  双  丝  艘  所  台  摊  滩  趟  堂  套  天  条  挑  贴  挺  筒  桶  通  头  团  坨  丸  碗  位  尾  味  窝  席  线  箱  项  些  牙  眼  样  页  英亩  员  元  则  盏  丈  章  张  阵  支  枝  只  种  轴  株  幢  桩  桌  宗  组  尊  座  ";

	public static HashSet<String> measures = new HashSet<String>(
			Arrays.asList(measureStr.trim().split("\\s+")));

	public static String definitePersonStr = "帮 辈 拨 代 队 户 位 员 名";
	public static HashSet<String> definitePersonMeasures = new HashSet<String>(
			Arrays.asList(definitePersonStr.trim().split("\\s+")));

	public static String ge = "个";

	static HashMap<String, HashMap<String, Integer>> peopleness;

	public static Animacy getAntAnimacy_(String head) {
		if (peopleness == null) {
			loadMeassure();
		}
		if (pronouns.contains(head)) {
			return getAnimacy(head);
		}

		Animacy ret;
		if (head.endsWith("人") || head.equals("自己")) {
			ret = Animacy.ANIMATE;
		} else if (peopleness.containsKey(head)) {
			HashMap<String, Integer> subMap = peopleness.get(head);
			int anim = 0;
			int unanim = 0;

			for (String definitePerson : definitePersonMeasures) {
				if (subMap.containsKey(definitePerson)) {
					anim += subMap.get(definitePerson);
				}
			}

			for (String key : subMap.keySet()) {
				if (!definitePersonMeasures.contains(key) && !key.equals(ge)) {
					unanim += subMap.get(key);
				}
			}
			int geCount = 0;
			if (subMap.containsKey(ge)) {
				geCount += subMap.get(ge);
			}
			if (anim > unanim) {
				ret = Animacy.ANIMATE;
			}
			// else if (anim != 0 && anim + geCount > unanim && anim>.5*unanim)
			// {
			// ret = Animacy.animate;
			//
			// }
			else {
				ret = Animacy.INANIMATE;
			}
			// System.out.println(head + " " + an.name());
		} else {
			// if (location.contains(head.substring(head.length() - 1))) {
			// ret = Animacy.unanimate;
			// } else if (mention.mType == MentionType.proper) {
			// ret = Animacy.animate;
			// // System.out.println(mention.extent);
			// } else {
			ret = Animacy.UNKNOWN;
			// ret = Animacy.unanimate;
			// System.out.println(head + " " + mention.NE);
			missed.add(head);
			// }
		}
		// System.out.println(head + " " + ret.name() + " " + mention.NE);
		return ret;
	}

	public static Animacy getAntAnimacy(String head) {
		if (peopleness == null) {
			loadMeassure();
		}
		if (pronouns.contains(head)) {
			return getAnimacy(head);
		}

		// if (!peopleness.containsKey(head)
		// && mention.mType == MentionType.common) {
		// head = head.substring(head.length() - 1);
		// }
		//
		Animacy ret;
		if (head.endsWith("人") || head.equals("自己")) {
			ret = Animacy.ANIMATE;
		}
		// else if (mention.mType == MentionType.tmporal) {
		// ret = Animacy.unanimate;
		// } else if (!mention.NE.equalsIgnoreCase("OTHER")) {
		// if (mention.NE.equals("PERSON")) {
		// ret = Animacy.animate;
		// } else {
		// ret = Animacy.unanimate;
		// }
		// }
		else if (peopleness.containsKey(head)) {
			HashMap<String, Integer> subMap = peopleness.get(head);
			int anim = 0;
			int unanim = 0;

			for (String definitePerson : definitePersonMeasures) {
				if (subMap.containsKey(definitePerson)) {
					anim += subMap.get(definitePerson);
				}
			}

			for (String key : subMap.keySet()) {
				if (!definitePersonMeasures.contains(key) && !key.equals(ge)) {
					unanim += subMap.get(key);
				}
			}
			int geCount = 0;
			if (subMap.containsKey(ge)) {
				geCount += subMap.get(ge);
			}
			if (anim > unanim) {
				ret = Animacy.ANIMATE;
			}
			// else if (anim != 0 && anim + geCount > unanim && anim>.5*unanim)
			// {
			// ret = Animacy.animate;
			//
			// }
			else {
				ret = Animacy.INANIMATE;
			}
			// System.out.println(head + " " + an.name());
		} else {
			// if (location.contains(head.substring(head.length() - 1))) {
			// ret = Animacy.unanimate;
			// } else if (mention.mType == MentionType.proper) {
			// ret = Animacy.animate;
			// // System.out.println(mention.extent);
			// } else {
			ret = Animacy.UNKNOWN;
			// ret = Animacy.unanimate;
			// System.out.println(head + " " + mention.NE);
			missed.add(head);
			// }
		}
		// System.out.println(head + " " + ret.name() + " " + mention.NE);
		return ret;
	}

	public static Animacy getAntAnimacy(EntityMention mention) {
		if (peopleness == null) {
			loadMeassure();
		}
		String head = mention.head;
		if (pronouns.contains(head)) {
			return getAnimacy(head);
		}

//		if (!peopleness.containsKey(head)
//				&& mention.mType == MentionType.common) {
//			head = head.substring(head.length() - 1);
//		}

		Animacy ret;
		if (head.endsWith("人") || head.equals("自己")) {
			ret = Animacy.ANIMATE;
//		} else if (mention.mType == MentionType.tmporal) {
//			ret = Animacy.INANIMATE;
		} else if (!mention.ner.equalsIgnoreCase("OTHER")) {
			if (mention.ner.equals("PERSON")) {
				ret = Animacy.ANIMATE;
			} else {
				ret = Animacy.INANIMATE;
			}
		} else if (peopleness.containsKey(head)) {
			HashMap<String, Integer> subMap = peopleness.get(head);
			int anim = 0;
			int unanim = 0;

			for (String definitePerson : definitePersonMeasures) {
				if (subMap.containsKey(definitePerson)) {
					anim += subMap.get(definitePerson);
				}
			}

			for (String key : subMap.keySet()) {
				if (!definitePersonMeasures.contains(key) && !key.equals(ge)) {
					unanim += subMap.get(key);
				}
			}
			int geCount = 0;
			if (subMap.containsKey(ge)) {
				geCount += subMap.get(ge);
			}
			if (anim > unanim) {
				ret = Animacy.ANIMATE;
			}
			// else if (anim != 0 && anim + geCount > unanim && anim>.5*unanim)
			// {
			// ret = Animacy.animate;
			//
			// }
			else {
				ret = Animacy.INANIMATE;
			}
			// System.out.println(head + " " + an.name());
		} else {
			// if (location.contains(head.substring(head.length() - 1))) {
			// ret = Animacy.unanimate;
			// } else if (mention.mType == MentionType.proper) {
			// ret = Animacy.animate;
			// // System.out.println(mention.extent);
			// } else {
			ret = Animacy.UNKNOWN;
			// ret = Animacy.unanimate;
			// System.out.println(head + " " + mention.NE);
			missed.add(head);
			// }
		}
		// System.out.println(head + " " + ret.name() + " " + mention.NE);
		return ret;
	}

	public static HashSet<String> missed = new HashSet<String>();

	public static void loadMeassure() {
		peopleness = new HashMap<String, HashMap<String, Integer>>();
		HashMap<String, Integer> MCounts = new HashMap<String, Integer>();
		ArrayList<String> lines = Common.getLines("animacy.giga");
		for (String line : lines) {
			String tks[] = line.split("\\s+");
			String noun = tks[0];
			String M = tks[1];
			int count = Integer.parseInt(tks[2]);
			HashMap<String, Integer> subMap = peopleness.get(noun);
			if (subMap == null) {
				subMap = new HashMap<String, Integer>();
				peopleness.put(noun, subMap);
			}
			subMap.put(M, count);

			Integer mc = MCounts.get(M);
			if (mc == null) {
				MCounts.put(M, count);
			} else {
				MCounts.put(M, mc.intValue() + count);
			}
		}
		// HashSet<Integer> set = new HashSet<Integer>();
		// set.addAll(MCounts.values());
		//
		// HashSet<String> frequent = new HashSet<String>();
		//
		// for (String M : MCounts.keySet()) {
		// System.out.println(M + MCounts.get(M));
		// if (MCounts.get(M) > 500) {
		// frequent.add(M);
		// }
		// }
		// ArrayList<Integer> arr = new ArrayList<Integer>();
		// arr.addAll(set);
		// Collections.sort(arr);
		// Collections.reverse(arr);
		//
		// System.out.println("#" + arr.get(100));
		// System.out.println("@" + frequent.size());
		// System.out.println(frequent);
	}

	static HashMap<String, HashMap<String, Integer>> genderStat;

	public static void loadGender() {
		if (peopleness == null) {
			loadMeassure();
		}
		genderStat = new HashMap<String, HashMap<String, Integer>>();
		ArrayList<String> lines = Common.getLines("collectStats.giga.bak");
		for (String line : lines) {
			String tks[] = line.split("\\s+");
			String noun = tks[0];
			String M = tks[1];
			int count = Integer.parseInt(tks[2]);
			HashMap<String, Integer> subMap = peopleness.get(noun);
			if (subMap == null) {
				subMap = new HashMap<String, Integer>();
				peopleness.put(noun, subMap);
			}
			subMap.put(M, count);
		}
	}

	public static Gender getAntGender(String head) {
		if (genderStat == null) {
			loadGender();
		}
		if (pronouns.contains(head)) {
			return getGender(head);
		}
		Animacy anim = getAntAnimacy(head);
		if (anim == Animacy.INANIMATE || anim == Animacy.UNKNOWN) {
			return Gender.NEUTRAL;
		}

		HashMap<String, Integer> subMap = genderStat.get(head);
		int male = 0;
		int female = 0;
		if (subMap != null) {
			for (String malePronoun : EMUtil.males) {
				if (subMap.containsKey(malePronoun)) {
					male += subMap.get(malePronoun);
				}
			}
			for (String femalePronoun : EMUtil.females) {
				if (subMap.containsKey(femalePronoun)) {
					female += subMap.get(femalePronoun);
				}
			}
		}
		if (female > male) {
			// System.out.println(head + " : FEMALE");
			return Gender.FEMALE;
		} else {
			// System.out.println(head + " : MALE " + m.animacy + " " + m.NE);
			return Gender.MALE;
		}
	}

	public static Gender getAntGender(EntityMention m) {
		if (genderStat == null) {
			loadGender();
		}
		String head = m.head;
		if (pronouns.contains(head)) {
			return getGender(head);
		}
		Animacy anim = getAntAnimacy(m);
		if (anim == Animacy.INANIMATE || anim == Animacy.UNKNOWN) {
			return Gender.NEUTRAL;
		}

		HashMap<String, Integer> subMap = genderStat.get(head);
		int male = 0;
		int female = 0;
		if (subMap != null) {
			for (String malePronoun : EMUtil.males) {
				if (subMap.containsKey(malePronoun)) {
					male += subMap.get(malePronoun);
				}
			}
			for (String femalePronoun : EMUtil.females) {
				if (subMap.containsKey(femalePronoun)) {
					female += subMap.get(femalePronoun);
				}
			}
		}
		if (female > male) {
			// System.out.println(head + " : FEMALE");
			return Gender.FEMALE;
		} else {
			// System.out.println(head + " : MALE " + m.animacy + " " + m.NE);
			return Gender.MALE;
		}
	}

//	public static HashMap<String, ArrayList<Element>> predictNEs;

	public static HashMap<String, String> NEMap;

//	public static void loadPredictNE(String folder, String mode) {
//		if (predictNEs == null) {
//			NEMap = new HashMap<String, String>();
//			predictNEs = new HashMap<String, ArrayList<Element>>();
//			String fn = "chinese_" + folder + ".neresult.test";
//			ArrayList<String> lines = Common.getLines(fn);
//			for (int i = 0; i < lines.size(); i++) {
//				String line = lines.get(i);
//				if (line.isEmpty()) {
//					continue;
//				}
//				String tokens[] = line.split("\\s+");
//				int length = tokens.length;
//				String label = tokens[length - 1];
//				int wordID = Integer.valueOf(tokens[length - 3]);
//				int partID = Integer.valueOf(tokens[length - 4]);
//				String docID = tokens[length - 5];
//				String key = docID + "_" + partID;
//				if (label.startsWith("B")) {
//					String content = label.substring(2);
//					int k = i + 1;
//					while (!lines.get(k).isEmpty()
//							&& lines.get(k).trim().split("\\s+")[length - 1]
//									.startsWith("I")) {
//						k++;
//					}
//					int start = wordID;
//					int end = Integer
//							.valueOf(lines.get(k - 1).split("\\s+")[length - 3]);
//					Element element = new Element();
//					element.start = start;
//					element.end = end;
//					element.content = content;
//
//					StringBuilder sb = new StringBuilder();
//					for (int m = i; m <= k - 1; m++) {
//						sb.append(lines.get(m).split("\\s+")[0]);
//					}
//
//					EMUtil.NEMap.put(sb.toString(), content);
//					// System.out.println(sb.toString() + " " + content + " " +
//					// i + " " + (k-1));
//					if (predictNEs.containsKey(key)) {
//						predictNEs.get(key).add(element);
//					} else {
//						ArrayList<Element> ems = new ArrayList<Element>();
//						ems.add(element);
//						predictNEs.put(key, ems);
//					}
//				}
//			}
//		}
//	}

//	public static void pruneChMentions(ArrayList<EntityMention> mentions,
//			ACEDoc part) {
//		ArrayList<EntityMention> removes = new ArrayList<EntityMention>();
//		Collections.sort(mentions);
//		ArrayList<EntityMention> copyMentions = new ArrayList<EntityMention>(
//				mentions.size());
//		copyMentions.addAll(mentions);
//
//		for (int i = 0; i < mentions.size(); i++) {
//			EntityMention em = mentions.get(i);
//			for (int j = 0; j < copyMentions.size(); j++) {
//				EntityMention em2 = copyMentions.get(j);
//				if (em.end == em2.end
//						&& (em.end - em.start < em2.end - em2.start)) {
////					if (!part.label.contains("nw/")) {
//						// if (em.start > 0 && part.getWord(em.start -
//						// 1).posTag.equalsIgnoreCase("CC")) {
//						// continue;
//						// }
//						removes.add(em);
//						break;
//					}
//				}
//			}
//		}
//		mentions.removeAll(removes);
//		removes.clear();
//
//		for (int i = 0; i < mentions.size(); i++) {
//			Mention mention = mentions.get(i);
//			if (mention.NE.equalsIgnoreCase("QUANTITY")
//					|| mention.NE.equalsIgnoreCase("CARDINAL")
//					|| mention.NE.equalsIgnoreCase("PERCENT")
//					|| mention.NE.equalsIgnoreCase("MONEY")) {
//				removes.add(mention);
//				continue;
//			}
//
//			if (mention.extent.equalsIgnoreCase("我")
//					&& (mention.end + 2) < part.getWordCount()
//					&& part.getWord(mention.end + 1).word.equals("啊")
//					&& part.getWord(mention.end + 2).word.equals("，")) {
//				removes.add(mention);
//				continue;
//			}
//
//			if (EMUtil.removeChars.contains(mention.head)) {
//				removes.add(mention);
//				continue;
//			}
//
//			// 没 问题
//			if (mention.extent.equalsIgnoreCase("问题") && mention.start > 0
//					&& part.getWord(mention.start - 1).word.equals("没")) {
//				removes.add(mention);
//				continue;
//			}
//
//			// 你 知道
//			if (mention.extent.equalsIgnoreCase("你") && mention.start > 0
//					&& part.getWord(mention.start + 1).word.equals("知道")) {
//				removes.add(mention);
//				continue;
//			}
//
//			//
//			if (mention.extent.contains("什么") || mention.extent.contains("多少")) {
//				removes.add(mention);
//				continue;
//			}
//			String lastWord = part.getWord(mention.end).word;
//			if (mention.extent.endsWith("的")
//					|| (mention.extent.endsWith("人")
//							&& mention.start == mention.end && countries
//								.contains(lastWord.substring(0,
//										lastWord.length() - 1)))) {
//				removes.add(mention);
//				continue;
//			}
//			// ｑｕｏｔ
//			if (removeWords.contains(mention.extent)) {
//				removes.add(mention);
//				continue;
//			}
//		}
//		for (EntityMention remove : removes) {
//			mentions.remove(remove);
//		}
//		HashSet<EntityMention> mentionsHash = new HashSet<EntityMention>();
//		mentionsHash.addAll(mentions);
//		mentions.clear();
//		mentions.addAll(mentionsHash);
//	}

	public final static HashSet<String> countries = Common
			.readFile2Set("country2");

	public final static Set<String> removeWords = new HashSet<String>(
			Arrays.asList(new String[] { "_", "ｑｕｏｔ", "人", "时候", "问题", "情况",
					"未来", "战争", "可能" }));

	public static String getPredicateNode(MyTreeNode vp) {
		ArrayList<MyTreeNode> leaves = vp.getLeaves();
		HashSet<String> filter = new HashSet<String>(Arrays.asList("是", "有",
				"要", "会", "可以", "没有", "为", "无", "应该", "让", "能", "必须"));
		for (MyTreeNode leaf : leaves) {
			if (leaf.parent.value.startsWith("V")
					&& !filter.contains(leaf.value)) {
				return leaf.value;
			}
		}
		return null;
	}

	public static String getObjectNP(MyTreeNode vp) {
		ArrayList<MyTreeNode> leaves = vp.getLeaves();
		for (MyTreeNode leaf : leaves) {
			if (leaf.parent.value.startsWith("V")) {
				ArrayList<MyTreeNode> possibleNPs = leaf.parent
						.getRightSisters();
				for (MyTreeNode tmp : possibleNPs) {
					if (tmp.value.startsWith("NP")
							|| tmp.value.startsWith("QP")) {
						return tmp.getLeaves().get(tmp.getLeaves().size() - 1).value;
					}
				}
			}
		}
		return null;
	}

	public static String getObjectNP2(MyTreeNode vp) {
		while (true) {
			boolean haveVP = false;
			for (MyTreeNode child : vp.children) {
				if (child.value.equalsIgnoreCase("VP")) {
					haveVP = true;
					vp = child;
					break;
				}
			}
			if (!haveVP) {
				break;
			}
		}
		// System.out.println(tmp.children.get(0).value);
		ArrayList<MyTreeNode> possibleNPs = vp.children;
		for (MyTreeNode tm : possibleNPs) {
			if (tm.value.startsWith("NP") || tm.value.startsWith("QP")) {
				return tm.getLeaves().get(tm.getLeaves().size() - 1).value;
			}
		}
		return "";
	}

	// public static Datum<String, String> svmlightToStanford(
	// ArrayList<String> feas, String label) {
	// return new BasicDatum<String, String>(feas, label);
	// }
	//
	// protected static Datum<String, String> svmlightToStanford(String
	// svmlight) {
	// String tks[] = svmlight.split("\\s+");
	// String label = tks[0];
	// List<String> features = new ArrayList<String>();
	// for (int i = 1; i < tks.length; i++) {
	// int k = tks[i].indexOf(":");
	// String idx = tks[i].substring(0, k);
	// int val = Integer.parseInt(tks[i].substring(k + 1));
	// if (val != 1) {
	// Common.bangErrorPOS("Binary expected!");
	// }
	// features.add(idx);
	// }
	// return new BasicDatum<String, String>(features, label);
	// }

	public static HashMap<String, Integer> formChainMap(
			ArrayList<Entity> entities) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i < entities.size(); i++) {
			for (EntityMention m : entities.get(i).mentions) {
				map.put(m.toName(), i);
			}
		}
		return map;
	}

	public static HashMap<String, ArrayList<EntityMention>> formChainMap2(
			ArrayList<Entity> entities) {
		HashMap<String, ArrayList<EntityMention>> map = new HashMap<String, ArrayList<EntityMention>>();
		for (int i = 0; i < entities.size(); i++) {
			for (EntityMention m : entities.get(i).mentions) {
				map.put(m.toName(), entities.get(i).mentions);
			}
		}
		return map;
	}

	public static ArrayList<EntityMention> getInBetweenMention(
			ArrayList<EntityMention> mentions, int start, int end) {
		ArrayList<EntityMention> zeros = new ArrayList<EntityMention>();
		for (EntityMention m : mentions) {
			if (m.start >= start && m.start <= end
					&& (m.end == -1 || (m.end >= start && m.end <= end))) {
				zeros.add(m);
			}
		}
		return zeros;
	}

//	public static ArrayList<ArrayList<CoNLLWord>> split(CoNLLSentence s,
//			ArrayList<EntityMention> zeros) {
//		ArrayList<ArrayList<CoNLLWord>> groups = new ArrayList<ArrayList<CoNLLWord>>();
//
//		ArrayList<Integer> spliter = new ArrayList<Integer>();
//		Collections.sort(zeros);
//		spliter.add(0);
//		for (int i = 0; i < zeros.size() - 1; i++) {
//			Mention z1 = zeros.get(i);
//			Mention z2 = zeros.get(i + 1);
//			int split = -1;
//			for (int p = z1.start; p <= z2.start; p++) {
//				String word = s.part.getWord(p).word;
//				if (word.equals(".") || word.equals("。") || word.equals("？")
//						|| word.equals("?")) {
//					split = s.part.getWord(p).indexInSentence + 1;
//					break;
//				}
//				if (split == -1
//						&& (word.equals(",") || word.equals("，")
//								|| word.equals("，") || word.equals("、"))) {
//					split = s.part.getWord(p).indexInSentence + 1;
//				}
//			}
//			if (split != -1) {
//				spliter.add(split);
//			}
//		}
//		spliter.add(s.getWords().size());
//
//		for (int i = 0; i < spliter.size() - 1; i++) {
//			ArrayList<CoNLLWord> group = new ArrayList<CoNLLWord>();
//			group.addAll(s.getWords().subList(spliter.get(i),
//					spliter.get(i + 1)));
//			groups.add(group);
//		}
//		return groups;
//	}

//	public static ArrayList<CoNLLWord> addZero(ArrayList<CoNLLWord> list,
//			Mention z, String pronoun) {
//		ArrayList<CoNLLWord> newList = new ArrayList<CoNLLWord>(list);
//		CoNLLWord zp = new CoNLLWord();
//		zp.word = pronoun;
//		for (int i = 0; i < newList.size(); i++) {
//			if (newList.get(i).index == z.start) {
//				zp.index = z.start;
//				zp.indexInSentence = newList.get(i).indexInSentence;
//				zp.isZeroWord = true;
//				newList.add(i, zp);
//				break;
//			}
//		}
//		return newList;
//	}

//	public static String listToString(ArrayList<CoNLLWord> tks) {
//		StringBuilder sb = new StringBuilder();
//		for (CoNLLWord tk : tks) {
//			sb.append(tk.word).append(" ");
//		}
//		return sb.toString().trim();
//	}

//	public static ArrayList<ArrayList<CoNLLWord>> getAllPossibleFill(
//			ArrayList<CoNLLWord> group, ArrayList<EntityMention> zeroInGroup) {
//		ArrayList<ArrayList<CoNLLWord>> combinations = new ArrayList<ArrayList<CoNLLWord>>();
//		combinations.add(group);
//
//		for (EntityMention z : zeroInGroup) {
//			ArrayList<ArrayList<CoNLLWord>> tmpCombinations = new ArrayList<ArrayList<CoNLLWord>>();
//			for (String op : EMUtil.pronouns) {
//				for (ArrayList<CoNLLWord> combination : combinations) {
//					ArrayList<CoNLLWord> newList = EMUtil.addZero(combination,
//							z, op);
//					tmpCombinations.add(newList);
//				}
//			}
//			combinations = tmpCombinations;
//		}
//		return combinations;
//	}

//	public static ArrayList<EntityMention> removeDuplcate(
//			ArrayList<EntityMention> anaphorZeros) {
//		HashSet<EntityMention> zeroSet = new HashSet<EntityMention>(anaphorZeros);
//		anaphorZeros = new ArrayList<EntityMention>(zeroSet);
//		Collections.sort(anaphorZeros);
//		return anaphorZeros;
//	}

	public static ArrayList<EntityMention> getPronouns(ArrayList<EntityMention> cluster) {
		ArrayList<EntityMention> pronouns = new ArrayList<EntityMention>();
		for (EntityMention m : cluster) {
			if (m.end != -1 && EMUtil.pronouns.contains(m.extent)) {
				pronouns.add(m);
			}
		}
		return pronouns;
	}

	public static ArrayList<EntityMention> getNotPronouns(ArrayList<EntityMention> cluster) {
		ArrayList<EntityMention> notPronouns = new ArrayList<EntityMention>();
		for (EntityMention m : cluster) {
			if (m.end != -1 && !EMUtil.pronouns.contains(m.extent)) {
				notPronouns.add(m);
			}
		}
		return notPronouns;
	}

	public static int[] getWinner(int[] vote) {
		int[] ret = new int[2];
		ret[1] = -1;
		for (int v = 0; v < vote.length; v++) {
			if (vote[v] > ret[0]) {
				ret[0] = vote[v];
				ret[1] = v;
			}
		}
		return ret;
	}

//	public static Numb inferNumber(ArrayList<EntityMention> cluster) {
//		ArrayList<EntityMention> pronouns = getPronouns(cluster);
//		int[] vote = new int[Numb.values().length];
//		for (EntityMention p : pronouns) {
//			vote[getNumber(p.extent).ordinal()]++;
//		}
//		int ret[] = getWinner(vote);
//		int max = ret[0];
//		int win = ret[1];
//		if (win != -1) {
//			int best = 0;
//			for (int v : vote) {
//				if (v == max) {
//					best++;
//				}
//			}
//			if (best == 1) {
//				return Numb.values()[win];
//			}
//		}
//		ArrayList<EntityMention> notPronouns = getNotPronouns(cluster);
//		for (EntityMention m : notPronouns) {
//			vote[getAntNumber2(m.extent).ordinal()]++;
//		}
//		ret = getWinner(vote);
//		if (ret[1] == -1) {
//			return Numb.single;
//		} else {
//			return Numb.values()[ret[1]];
//		}
//	}

	//
	public static Numb getAntNumber2(String str) {
		boolean plura = false;
		if (str.contains("部分") || str.contains("两") || str.contains("和")
				|| str.contains("些") || str.contains("多") || str.endsWith("们")
				|| str.contains("大家") || str.contains("四")
				|| str.contains("一点") || str.contains("双") || str.contains("与")
				|| str.contains("位") || str.contains("４") || str.contains("众")
				|| str.contains("万") || str.contains("几")) {
			plura = true;
		}
		if (plura) {
			return Numb.PLURAL;
		} else {
			return Numb.SINGULAR;
		}
	}

//	public static Gender inferGender(ArrayList<EntityMention> cluster) {
//		ArrayList<EntityMention> pronouns = getPronouns(cluster);
//		int[] vote = new int[Gender.values().length - 1];
//		for (EntityMention p : pronouns) {
//			vote[getGender(p.extent).ordinal()]++;
//		}
//		int ret[] = getWinner(vote);
//		int max = ret[0];
//		int win = ret[1];
//		if (win != -1) {
//			int best = 0;
//			for (int v : vote) {
//				if (v == max) {
//					best++;
//				}
//			}
//			if (best == 1) {
//				return Gender.values()[win];
//			}
//		}
//		ArrayList<EntityMention> notPronouns = getNotPronouns(cluster);
//		for (EntityMention m : notPronouns) {
//			vote[getAntGender2(m.head).ordinal()]++;
//		}
//		ret = getWinner(vote);
//		if (ret[1] == -1) {
//			return Gender.male;
//		} else {
//			return Gender.values()[ret[1]];
//		}
//	}

	public static Gender getAntGender2(String head) {
		if (genderStat == null) {
			loadGender();
		}
		if (pronouns.contains(head)) {
			return getGender(head);
		}
		if (head.equals("母亲") || head.endsWith("女士") || head.endsWith("太太")) {
			return Gender.FEMALE;
		}
		if (head.endsWith("嗲")) {
			return Gender.MALE;
		}
		Animacy anim = getAntAnimacy2(head);
		if (anim == Animacy.INANIMATE || anim == Animacy.UNKNOWN) {
			return Gender.NEUTRAL;
		}
		HashMap<String, Integer> subMap = genderStat.get(head);
		int male = 0;
		int female = 0;
		if (subMap != null) {
			for (String malePronoun : EMUtil.males) {
				if (subMap.containsKey(malePronoun)) {
					male += subMap.get(malePronoun);
				}
			}
			for (String femalePronoun : EMUtil.females) {
				if (subMap.containsKey(femalePronoun)) {
					female += subMap.get(femalePronoun);
				}
			}
		}
		if (female > male) {
			// System.out.println(head + " : FEMALE");
			return Gender.FEMALE;
		} else {
			// System.out.println(head + " : MALE " + m.animacy + " " + m.NE);
			return Gender.MALE;
		}
	}

//	public static Animacy inferAnimacy(ArrayList<EntityMention> cluster) {
//		ArrayList<EntityMention> pronouns = getPronouns(cluster);
//		int[] vote = new int[Animacy.values().length - 1];
//		for (EntityMention p : pronouns) {
//			vote[getAnimacy(p.extent).ordinal()]++;
//		}
//		int ret[] = getWinner(vote);
//		int max = ret[0];
//		int win = ret[1];
//		if (win != -1) {
//			int best = 0;
//			for (int v : vote) {
//				if (v == max) {
//					best++;
//				}
//			}
//			if (best == 1) {
//				return Animacy.values()[win];
//			}
//		}
//		ArrayList<EntityMention> notPronouns = getNotPronouns(cluster);
//		for (EntityMention m : notPronouns) {
//			if (m.NE.startsWith("PER")) {
//				vote[Animacy.animate.ordinal()]++;
//			} else {
//				Animacy ani = getAntAnimacy2(m.head);
//				if (ani != Animacy.unknown) {
//					vote[ani.ordinal()]++;
//				}
//			}
//		}
//		ret = getWinner(vote);
//		if (ret[1] == -1) {
//			return Animacy.animate;
//		} else {
//			return Animacy.values()[ret[1]];
//		}
//	}

	//
	public static Animacy getAntAnimacy2(String head) {
		if (peopleness == null) {
			loadMeassure();
		}
		if (pronouns.contains(head)) {
			return getAnimacy(head);
		}
		Animacy ret;
		if (head.equals("上帝") || head.endsWith("们") || head.contains("总统")) {
			return Animacy.ANIMATE;
		}
		if (head.endsWith("人") || head.equals("自己")) {
			ret = Animacy.ANIMATE;
		} else if (peopleness.containsKey(head)) {
			HashMap<String, Integer> subMap = peopleness.get(head);
			int anim = 0;
			int unanim = 0;

			for (String definitePerson : definitePersonMeasures) {
				if (subMap.containsKey(definitePerson)) {
					anim += subMap.get(definitePerson);
				}
			}

			for (String key : subMap.keySet()) {
				if (!definitePersonMeasures.contains(key) && !key.equals(ge)) {
					unanim += subMap.get(key);
				}
			}
			int geCount = 0;
			if (subMap.containsKey(ge)) {
				geCount += subMap.get(ge);
			}
			if (anim > unanim) {
				ret = Animacy.ANIMATE;
			}

			else {
				ret = Animacy.INANIMATE;
			}
		} else {
			ret = Animacy.UNKNOWN;
			missed.add(head);
		}
		return ret;
	}

	public static Person flipPerson(Person p, boolean sameSpeaker, String pro) {
		if (sameSpeaker) {
			return p;
		} else {
			if (p == Person.FIRST && pro.equals("我")) {
				return Person.SECOND;
			} else if (p == Person.SECOND) {
				return Person.FIRST;
			} else {
				return Person.THIRD;
			}
		}
	}

//	public static Person inferPerson(ArrayList<EntityMention> cluster, Mention zp,
//			CoNLLPart part) {
//		ArrayList<EntityMention> pronouns = getPronouns(cluster);
//		int[] vote = new int[Person.values().length];
//		for (EntityMention p : pronouns) {
//			boolean sameSpeaker = part.getWord(zp.start).speaker.equals(part
//					.getWord(p.start).speaker);
//			vote[flipPerson(getPerson(p.extent), sameSpeaker, p.extent)
//					.ordinal()]++;
//		}
//		int ret[] = getWinner(vote);
//		int max = ret[0];
//		int win = ret[1];
//		if (win != -1) {
//			int best = 0;
//			for (int v : vote) {
//				if (v == max) {
//					best++;
//				}
//			}
//			if (best == 1) {
//				return Person.values()[win];
//			}
//		}
//		ArrayList<EntityMention> notPronouns = getNotPronouns(cluster);
//		for (EntityMention m : notPronouns) {
//			boolean sameSpeaker = part.getWord(zp.start).speaker.equals(part
//					.getWord(m.start).speaker);
//			vote[flipPerson(getAntPerson(m.extent), sameSpeaker, m.extent)
//					.ordinal()]++;
//		}
//		ret = getWinner(vote);
//		if (ret[1] == -1) {
//			return Person.third;
//		} else {
//			return Person.values()[ret[1]];
//		}
//	}

//	public static String getOnePronoun(Person per, Numb num, Gender gen,
//			Animacy ani) {
//		if (per == Person.FIRST) {
//			if (gen == Gender.MALE) {
//				if (num == Numb.SINGULAR) {
//					if (ani == Animacy.ANIMATE) {
//						return "我";
//					} else if (ani == Animacy.unanimate) {
//						return "我";
//					}
//				} else if (num == Numb.plural) {
//					if (ani == Animacy.animate) {
//						return "我们";
//					} else if (ani == Animacy.unanimate) {
//						return "我们";
//					}
//				}
//			} else if (gen == Gender.female) {
//				if (num == Numb.single) {
//					if (ani == Animacy.animate) {
//						return "我";
//					} else if (ani == Animacy.unanimate) {
//						return "我";
//					}
//				} else if (num == Numb.plural) {
//					if (ani == Animacy.animate) {
//						return "我们";
//					} else if (ani == Animacy.unanimate) {
//						return "我们";
//					}
//				}
//			} else if (gen == Gender.neuter) {
//				if (num == Numb.single) {
//					if (ani == Animacy.animate) {
//						return "我";
//					} else if (ani == Animacy.unanimate) {
//						return "我";
//					}
//				} else if (num == Numb.plural) {
//					if (ani == Animacy.animate) {
//						return "我们";
//					} else if (ani == Animacy.unanimate) {
//						return "我们";
//					}
//				}
//			}
//		} else if (per == Person.second) {
//			if (gen == Gender.male) {
//				if (num == Numb.single) {
//					if (ani == Animacy.animate) {
//						return "你";
//					} else if (ani == Animacy.unanimate) {
//						return "你";
//					}
//				} else if (num == Numb.plural) {
//					if (ani == Animacy.animate) {
//						return "你们";
//					} else if (ani == Animacy.unanimate) {
//						return "你们";
//					}
//				}
//			} else if (gen == Gender.female) {
//				if (num == Numb.single) {
//					if (ani == Animacy.animate) {
//						return "你";
//					} else if (ani == Animacy.unanimate) {
//						return "你";
//					}
//				} else if (num == Numb.plural) {
//					if (ani == Animacy.animate) {
//						return "你们";
//					} else if (ani == Animacy.unanimate) {
//						return "你们";
//					}
//				}
//			} else if (gen == Gender.neuter) {
//				if (num == Numb.single) {
//					if (ani == Animacy.animate) {
//						return "你";
//					} else if (ani == Animacy.unanimate) {
//						return "你";
//					}
//				} else if (num == Numb.plural) {
//					if (ani == Animacy.animate) {
//						return "你们";
//					} else if (ani == Animacy.unanimate) {
//						return "你们";
//					}
//				}
//			}
//		} else if (per == Person.third) {
//			if (gen == Gender.male) {
//				if (num == Numb.single) {
//					if (ani == Animacy.animate) {
//						return "他";
//					} else if (ani == Animacy.unanimate) {
//						return "它";
//					}
//				} else if (num == Numb.plural) {
//					if (ani == Animacy.animate) {
//						return "他们";
//					} else if (ani == Animacy.unanimate) {
//						return "它们";
//					}
//				}
//			} else if (gen == Gender.female) {
//				if (num == Numb.single) {
//					if (ani == Animacy.animate) {
//						return "她";
//					} else if (ani == Animacy.unanimate) {
//						return "它";
//					}
//				} else if (num == Numb.plural) {
//					if (ani == Animacy.animate) {
//						return "她们";
//					} else if (ani == Animacy.unanimate) {
//						return "它们";
//					}
//				}
//			} else if (gen == Gender.neuter) {
//				if (num == Numb.single) {
//					if (ani == Animacy.animate) {
//						return "它";
//					} else if (ani == Animacy.unanimate) {
//						return "它";
//					}
//				} else if (num == Numb.plural) {
//					if (ani == Animacy.animate) {
//						return "他们";
//					} else if (ani == Animacy.unanimate) {
//						return "它们";
//					}
//				}
//			}
//		}
//		return null;
//	}

	public static String decideOP(double[] pers, double[] nums, double[] gens,
			double[] anis) {
		// "你", "我", "他", "她", "它", "你们", "我们", "他们", "她们", "它们"
		double max = 0;
		int win = -1;
		for (int i = 0; i < EMUtil.pronounList.size(); i++) {
			String pro = EMUtil.pronounList.get(i);
			double per = pers[EMUtil.getPerson(pro).ordinal()];
			double num = nums[EMUtil.getNumber(pro).ordinal()];
			double gen = gens[EMUtil.getGender(pro).ordinal()];
			double ani = anis[EMUtil.getAnimacy(pro).ordinal()];

			double p = per + num + gen + ani;
			if (p > max) {
				max = p;
				win = i;
			}
		}
		if (win == -1) {
			return "他";
		}
		return EMUtil.pronounList.get(win);
	}

//	public static HashMap<String, String> getGoldNEs2(CoNLLPart goldPart) {
//		HashMap<String, String> goldNEs = new HashMap<String, String>();
//		for (Element ne : goldPart.getNameEntities()) {
//			goldNEs.put(ne.start + "," + ne.end, ne.content);
//			goldNEs.put(ne.end + "," + ne.end, ne.content);
//		}
//		return goldNEs;
//	}

//	public static HashSet<String> getGoldNEs(CoNLLPart goldPart) {
//		HashSet<String> goldNEs = new HashSet<String>();
//		for (Element ne : goldPart.getNameEntities()) {
//			goldNEs.add(ne.start + "," + ne.end);
//			goldNEs.add(ne.end + "," + ne.end);
//		}
//		return goldNEs;
//	}
//
//	public static HashSet<String> getGoldPNs(CoNLLPart goldPart) {
//		HashSet<String> goldPNs = new HashSet<String>();
//		for (int i = 0; i < goldPart.getWordCount(); i++) {
//			if (goldPart.getWord(i).posTag.equals("PN")) {
//				goldPNs.add(i + "," + i);
//			}
//		}
//		return goldPNs;
//	}
//
//	public static HashSet<String> getGoldInChain(ArrayList<Entity> chains) {
//		HashSet<String> set = new HashSet<String>();
//		for (Entity e : chains) {
//			for (EntityMention m : e.getMentions()) {
//				set.add(m.toName());
//			}
//		}
//		return set;
//	}
//
//	public static HashMap<String, HashSet<String>> getGoldAnaphorKeys(
//			ArrayList<Entity> entities, CoNLLPart goldPart) {
//		HashSet<String> neSet = new HashSet<String>();
//		for (Element ne : goldPart.getNameEntities()) {
//			neSet.add(ne.start + "," + ne.end);
//			neSet.add(ne.end + "," + ne.end);
//		}
//
//		HashSet<String> pnSet = new HashSet<String>();
//		for (int i = 0; i < goldPart.getWordCount(); i++) {
//			CoNLLWord w = goldPart.getWord(i);
//			if (w.posTag.equals("PN")) {
//				pnSet.add(w.index + "," + w.index);
//			}
//		}
//
//		HashMap<String, HashSet<String>> anaphorKeys = new HashMap<String, HashSet<String>>();
//		for (Entity e : entities) {
//			Collections.sort(e.mentions);
//			for (int i = 1; i < e.mentions.size(); i++) {
//				Mention m1 = e.mentions.get(i);
//				if (neSet.contains(m1.toName()) || pnSet.contains(m1.toName())
//						|| neSet.contains(m1.end + "," + m1.end)) {
//					continue;
//				}
//				HashSet<String> ants = new HashSet<String>();
//				for (int j = i - 1; j >= 0; j--) {
//					Mention m2 = e.mentions.get(j);
//					if (!pnSet.contains(m2.toName()) && m2.end != m1.end) {
//						ants.add(m2.toName());
//					}
//				}
//				if (ants.size() != 0) {
//					anaphorKeys.put(m1.toName(), ants);
//				}
//			}
//		}
//		return anaphorKeys;
//	}

	public static SVOStat svoStat;

	public static double calMIObject(EntityMention ant, EntityMention anaphor, ACEDoc doc) {
		// if(true)
		// return 1;
		if (svoStat == null) {
			svoStat = new SVOStat();
			svoStat.loadMIInfo();
		}
		String o = ant.head;
		String pos = doc.getPostag(ant.headStart);
		String v = EMUtil.getFirstVerb(anaphor.V);

		String NE = ant.ner;
		if (ant.ner.equals("OTHER") && EMUtil.NEMap != null
				&& EMUtil.NEMap.containsKey(ant.head)) {
			NE = EMUtil.NEMap.get(ant.head);
		}

		if (NE.equals("PERSON")) {
			o = "他";
			pos = "PN";
		} else if (NE.equals("LOC") || NE.equals("GPE") || NE.equals("ORG")) {
			o = "它";
			pos = "PN";
		}

		if (!svoStat.unigrams.containsKey(o + " " + pos)
				|| svoStat.unigrams.get(o + " " + pos) < 15000) {
			return 1;
		}

		double objC = getValue(svoStat.unigrams, o + " " + pos);
		double objP = (objC + 1)
				/ (svoStat.unigramAll + svoStat.unigrams.size());

		if (!svoStat.vCounts.containsKey(v) || svoStat.vCounts.get(v) < 1000) {
			return 1;
		}

		double vC = getValue(svoStat.vCounts, v);
		double vP = (vC) / (svoStat.svoAll);

		double voC = getValue(svoStat.voCounts, v + " " + o);
		double voP = (voC) / (svoStat.svoAll);

		double MI = Math.log(voP / (vP * objP));

		return MI;
	}

	public static double calMISubject(EntityMention ant, EntityMention anaphor, ACEDoc doc) {
		// if(true)
		// return 1;
		if (svoStat == null) {
			svoStat = new SVOStat();
			svoStat.loadMIInfo();
		}
		String s = ant.head;
		String pos = doc.getPostag(ant.headStart);
		String v = EMUtil.getFirstVerb(anaphor.V);

		String NE = ant.ner;
		if (ant.ner.equals("OTHER") && EMUtil.NEMap != null
				&& EMUtil.NEMap.containsKey(ant.head)) {
			NE = EMUtil.NEMap.get(ant.head);
		}

		if (NE.equals("PERSON")) {
			s = "他";
			pos = "PN";
		} else if (NE.equals("LOC") || NE.equals("GPE") || NE.equals("ORG")) {
			s = "它";
			pos = "PN";
		}

		if (!svoStat.unigrams.containsKey(s + " " + pos)
				|| svoStat.unigrams.get(s + " " + pos) < 15000) {
			return 1;
		}

		double subjC = getValue(svoStat.unigrams, s + " " + pos);
		double subjP = (subjC + 1)
				/ (svoStat.unigramAll + svoStat.unigrams.size());

		if (!svoStat.vCounts.containsKey(v) || svoStat.vCounts.get(v) < 1000) {
			return 1;
		}

		double voC = getValue(svoStat.vCounts, v);
		double voP = (voC) / (svoStat.svoAll);

		double svoC = getValue(svoStat.svCounts, s + " " + v);
		double svoP = (svoC) / (svoStat.svoAll);

		double MI = Math.log(svoP / (voP * subjP));

		return MI;
	}

	public static double getValue(HashMap<String, Integer> map, String key) {
		if (map.containsKey(key)) {
			return map.get(key);
		} else {
			return 0.00000001;
		}
	}

//	public static HashMap<String, ArrayList<SentForAlign[]>> alignMap;
//	static HashMap<String, ArrayList<CoNLLSentence>> engSMap;

	public static boolean isLoadAlign = false;

//	public static void loadAlign() {
//		if (isLoadAlign) {
//			return;
//		}
//		alignMap = DocumentMap
//				.loadRealBAAlignResult("/users/yzcchen/chen3/ijcnlp2013/googleMTALL/chi_MT/align/");
//		System.out.println("Done1.");
//		CoNLLPart.processDiscourse = false;
//		CoNLLDocument engDoc = new CoNLLDocument("MT.chiCoNLL.all");
//		engDoc.language = "english";
//		CoNLLPart.processDiscourse = true;
//		engSMap = new HashMap<String, ArrayList<CoNLLSentence>>();
//		for (CoNLLPart part : engDoc.getParts()) {
//			// Common.pause(part.getPartName());
//			// Common.pause(part.getPartID());
//			String key = part.documentID;
//			// Common.pause(part.getCoNLLSentences().get(0).
//			ArrayList<CoNLLSentence> lst = engSMap.get(key);
//			if (lst == null) {
//				lst = new ArrayList<CoNLLSentence>();
//				engSMap.put(key, lst);
//			}
//			for (CoNLLSentence s : part.getCoNLLSentences()) {
//				lst.add(s);
//			}
//		}
//		System.out.println("Done2.");
//		isLoadAlign = true;
//	}

//	public static void alignMentions(CoNLLSentence chiS,
//			ArrayList<EntityMention> chiNPs, String docName) {
//		ArrayList<CoNLLWord> segWords = chiS.words;
//		int chiSegStart = segWords.get(0).index;
//
//		String chiStr = EMUtil.listToString(segWords);
//		ArrayList<SentForAlign[]> aligns = alignMap.get(docName);
//		SentForAlign[] align = aligns.get(chiS.idInDoc);
//		String engStr = align[1].getText();
//		CoNLLSentence engCoNLLS = engSMap.get(docName).get(chiS.idInDoc);
//		if (!engStr.equalsIgnoreCase(engCoNLLS.getText())
//				|| !chiStr.equalsIgnoreCase(align[0].getText())) {
//			// System.out.println(chiStr + "@");
//			// System.out.println(engStr);
//			// System.out.println(engCoNLLS.getText());
//			// System.out.println("---------");
//			// Common.pause("");
//		}
//		// construct mention map between two s
//		for (EntityMention cm : chiNPs) {
//			cm.units.clear();
//			Mention.chiSpanMaps.remove(cm.getReadName());
//		}
//
//		for (int i = 0; i < align[0].units.size(); i++) {
//			align[0].units.get(i).sentence = chiS;
//		}
//		for (int i = 0; i < align[1].units.size(); i++) {
//			align[1].units.get(i).sentence = engCoNLLS;
//		}
//
//		for (EntityMention em : chiNPs) {
//			int from = em.start - chiSegStart;
//			int to = em.end - chiSegStart;
//
//			StringBuilder sb = new StringBuilder();
//			for (int g = from; g <= to; g++) {
//				Unit unit = align[0].units.get(g);
//				unit.sentence = chiS;
//				unit.addMention(em);
//				em.units.add(unit);
//				sb.append(unit.getToken()).append(" ");
//			}
//			if (!sb.toString().trim().equalsIgnoreCase(em.extent)) {
//				// System.out.println("#" + sb.toString().trim()
//				// + "#" + em.extent.trim() + "#");
//				// System.out.println(em.start + "," + em.end);
//				// Common.pause("");
//			}
//		}
//		ParseTreeMention ptm = new ParseTreeMention();
//		ArrayList<EntityMention> engMentions = ptm.getMentions(engCoNLLS);
//		int engStart = engCoNLLS.getWords().get(0).index;
//		for (EntityMention em : engMentions) {
//			StringBuilder sb = new StringBuilder();
//			for (int g = em.start - engStart; g <= em.end - engStart; g++) {
//				Unit unit = align[1].units.get(g);
//				unit.sentence = engCoNLLS;
//				unit.addMention(em);
//				em.units.add(unit);
//				sb.append(unit.getToken()).append(" ");
//			}
//			if (!em.extent.trim().equalsIgnoreCase(sb.toString().trim())) {
//				// System.out.println(sb.toString().trim());
//				// System.out.println(em.extent.trim());
//				// System.out.println("English mention not equal");
//				// Common.pause("");
//			}
//		}
//		for (int g = 1; g <= 4; g++) {
//			Mention.assignMode = g;
//			for (EntityMention m : chiNPs) {
//				m.getXSpan();
//			}
//		}
//	}

	// static HashSet<String> semanticInstances = new HashSet<String>();

	static HashMap<String, String> ACESubTypeMap;
	static HashMap<String, String> ACETypeMap;

	public static String types[] = { "wea", "veh", "per", "fac", "gpe", "loc",
			"org", "other" };

	static String subTypes[] = { "f-airport", "f-building-grounds", "f-path",
			"f-plant", "f-subarea-facility", "g-continent",
			"g-county-or-district", "g-gpe-cluster", "g-nation",
			"g-population-center", "g-special", "g-state-or-province",
			"l-address", "l-boundary", "l-celestial", "l-land-region-natural",
			"l-region-general", "l-region-international", "l-water-body",
			"o-commercial", "o-educational", "o-entertainment", "o-government",
			"o-media", "o-medical-science", "o-non-governmental",
			"o-religious", "o-sports", "p-group", "p-indeterminate",
			"p-individual", "v-air", "v-land", "v-subarea-vehicle",
			"v-underspecified", "v-water", "w-biological", "w-blunt",
			"w-chemical", "w-exploding", "w-nuclear", "w-projectile",
			"w-sharp", "w-shooting", "w-underspecified", "o-other", "other" };

	public static void loadACESemantic() {
		ACETypeMap = new HashMap<String, String>();
		ACESubTypeMap = new HashMap<String, String>();

		ArrayList<String> instanceLines = Common.getLines("semanticInstance");
		ArrayList<String> typePreds = Common.getLines("svmTypePred");
		ArrayList<String> subTypePreds = Common.getLines("svmSubTypePred");
		for (int i = 0; i < instanceLines.size(); i++) {
			String instance = instanceLines.get(i);
			String typePred = types[Integer.parseInt(typePreds.get(i).split(
					"\\s+")[0]) - 1];
			String subtypePred = subTypes[Integer.parseInt(subTypePreds.get(i)
					.split("\\s+")[0]) - 1];
			ACETypeMap.put(instance, typePred);
			ACESubTypeMap.put(instance, subtypePred);
		}
	}

	private static String getACEType(String instance) {
		if (ACETypeMap == null) {
			loadACESemantic();
		}
		return ACETypeMap.get(instance);
	}

	private static String getACESubType(String instance) {
		if (ACESubTypeMap == null) {
			loadACESemantic();
		}
		return ACESubTypeMap.get(instance);
	}

//	public static String getSemanticInstance(EntityMention m, ACEDoc part) {
//		StringBuilder sb = new StringBuilder();
//		sb.append(part.fileID).append("!@#$%");
//		sb.append(m.head).append("!@#$%");
//		sb.append(m.ner).append("!@#$%");
//		int headID = m.headStart;
//
//		String cL2 = null;
//		String cL1 = null;
//		String cR1 = null;
//		String cR2 = null;
//
//		String wL2 = null;
//		String wL1 = null;
//		String wR1 = null;
//		String wR2 = null;
//
//		if (headID > 0) {
//			wL1 = part.getWord(headID - 1);
//		}
//		if (headID > 1) {
//			wL2 = part.getWord(headID - 2);
//		}
//		if (headID + 1 < part.getWordCount()) {
//			wR1 = part.getWord(headID + 1);
//		}
//		if (headID + 2 < part.getWordCount()) {
//			wR2 = part.getWord(headID + 2);
//		}
//
//		if (wL1 != null) {
//			cL1 = wL1.substring(wL1.length() - 1);
//		}
//		if (wL1 != null && wL1.length() > 1) {
//			cL2 = wL1.substring(wL1.length() - 2, wL1.length() - 1);
//		} else if (wL1 != null && wL1.length() == 1 && wL2 != null) {
//			cL2 = wL2.substring(wL2.length() - 1);
//		}
//
//		if (wR1 != null) {
//			cR1 = wR1.substring(0, 1);
//		}
//		if (wR1 != null && wR1.length() > 1) {
//			cR2 = wR1.substring(1, 2);
//		} else if (wR1 != null && wR1.length() == 1 && wR2 != null) {
//			cR2 = wR2.substring(0, 1);
//		}
//		sb.append(cL2).append("!@#$%");
//		sb.append(cL1).append("!@#$%");
//		sb.append(cR1).append("!@#$%");
//		sb.append(cR2).append("!@#$%");
//
//		String instance = sb.toString().trim();
//
//		// semanticInstances.add(instance);
//
//		return instance;
//	}

	public static void main(String args[]) {
		// loadMeassure();
		System.out.println(measures.size());
	}
}
