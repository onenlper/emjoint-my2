package entityEMCoref;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.ACEDoc;
import model.EntityMention;
import model.syntaxTree.MyTreeNode;
import util.Common;

public class Context implements Serializable {

	/**
         * 
         */
	private static final long serialVersionUID = 1L;
	// short antSenPos; // 3 values
	// short antHeadPos; //
	// short antGram; //
	// short proPos; //
	// short antType;// pronoun, proper, common

	public String feaL;

	public static HashMap<String, Context> contextCache = new HashMap<String, Context>();

	public static boolean coref = false;

	public static boolean gM1 = false;
	public static boolean gM2 = false;

	public static Context getContext(int[] feas) {
		// long feaL = 0;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < feas.length; i++) {
			// if (feas[i] >= 10) {
			// Common.bangErrorPOS("Can't larger than 10:" + feas[i]
			// + "  Fea:" + i);
			// }
			// feaL += Math.pow(10, i) * feas[i];
			sb.append(feas[i]).append("#");
		}
		if (contextCache.containsKey(sb.toString())) {
			return contextCache.get(sb.toString());
		} else {
			Context c = new Context(sb.toString());
			contextCache.put(sb.toString(), c);
			return c;
		}
	}

	private Context(String feaL) {
		this.feaL = feaL;
	}

	public int hashCode() {
		return this.toString().hashCode();
	}

	public boolean equals(Object obj) {
		Context c2 = (Context) obj;
		return (this.feaL == c2.feaL);
	}

	public String toString() {
		return this.feaL;
	}

	public static SVOStat svoStat;

	static short[] feas = new short[18];

	public static HashMap<String, Double> simiCache = Common
			.readFile2Map5("simiCache");

	public static HashSet<String> todo = new HashSet<String>();

	public static double getSimi(String h1, String h2) {
		String key = "";
		if (h1.compareTo(h2) > 0) {
			key = h2 + " " + h1;
		} else {
			key = h1 + " " + h2;
		}
		if (simiCache.containsKey(key)) {
			return simiCache.get(key);
		} else {
			todo.add(key);
			return -1;
		}
	}

	private static HashSet<String> yago;
//	static HashSet<String> preps = Common.readFile2Set("yago/prep");

//	private static String findHead(String text) {
//		String tks[] = text.split("\\s+");
//		String head = tks[tks.length - 1];
//
//		for (int i = tks.length - 1; i >= 0; i--) {
//			if (preps.contains(tks[i]) && i != 0) {
//				head = tks[i - 1];
//			}
//		}
//		return head;
//	}

//	public static HashSet<String> getYago() {
//		if (yago != null) {
//			return yago;
//		}
//		yago = new HashSet<String>();
//		try {
//			BufferedReader br = new BufferedReader(new FileReader("yagoTypes"));
//			String line;
//			while ((line = br.readLine()) != null) {
//				String tks[] = line.split("###");
//				String t1 = tks[0].trim();
//				String t2 = tks[1].trim();
//				String key1 = t1 + "###" + t2;
//
//				if (t2.endsWith("economy")) {
//					continue;
//				}
//
//				// yago.add(key1.toLowerCase().trim());
//				//
//				// String key2 = t1 + "###" + findHead(t2);
//				// yago.add(key2.toLowerCase().trim());
//
//				String key3 = t1 + "###" + EMUtil.getPorterStem(findHead(t2));
//				yago.add(key3.toLowerCase().trim());
//			}
//			br.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Common.outputHashSet(yago, "yagoSet");
//		return yago;
//	}

	// public static String getStrConcat(String t1, String t2) {
	// String key = "";
	// if(t1.compareTo(t2)<0) {
	// key = t1 + "###" + t2;
	// } else {
	// key = t2 + "###" + t1;
	// }
	// return key;
	// }

	public static boolean doit = false;

	private static HashMap<String, HashSet<String>> ch_eng_dic;

	public static HashSet<String> getSimpleTrans(String ch) {
		if (ch_eng_dic == null) {
			ch_eng_dic = new HashMap<String, HashSet<String>>();
			ArrayList<String> lines = Common.getLines("chi_eng.trans2");
			lines.addAll(Common.getLines("chi_eng.trans"));

			for (String line : lines) {
				String tks[] = line.split("#####");
				if (tks.length != 2) {
//					System.out.println(line);
					continue;
				}
				String chn = tks[0];
				String eng = tks[1];
				if (!ch_eng_dic.containsKey(chn)) {
					HashSet<String> set = new HashSet<String>();
					ch_eng_dic.put(chn, set);
				}
				ch_eng_dic.get(chn).add(eng);
			}
		}
		if (ch_eng_dic.containsKey(ch)) {
			return ch_eng_dic.get(ch);
		} else {
			return new HashSet<String>();
		}
	}

	public static Context buildContext(EntityMention ant,
			EntityMention anaphor, ACEDoc part,
			ArrayList<EntityMention> allCands, int mentionDis) {
		doit = false;
		if (!ant.isFake) {
			ant.s = part.getParseResult(ant.headStart);
		}
		anaphor.s = part.getParseResult(anaphor.headStart);

		int id = 0;
		int[] feas = new int[10];
		feas[id++] = getMentionDiss(mentionDis);
		if (ant.isFake) {
			// feas[id++] = -anaphor.head.hashCode();
			// return getContext(feas);
		}
		// exact match

		// feas[id++] = getIsFake(ant, anaphor, part);
		// feas[id++] = getHasSameHead(allCands, anaphor, part);

		feas[id++] = getDistance(ant, anaphor, part); //
		feas[id++] = isExactMatch(ant, anaphor, part); // 2
		feas[id++] = headMatch(ant, anaphor, part); // 2
		feas[id++] = isSamePredicate(ant, anaphor, part);

		// if(subtype1!=null && subtype2!=null && subtype1.equals(subtype2)) {
		// // System.out.println(subtype1 + " # " + subtype2);
		// // System.out.println(ant.head + " # " + m.head);
		// // System.out.println("==========================");
		// feas[id++] = 1;
		// } else {
		// feas[id++] = 0;
		// }

		// HashSet<String> antExtentX =
		// getSimpleTrans(ant.extent.replaceAll("\\s+", ""));
		// HashSet<String> antHeadX = getSimpleTrans(ant.head);
		//
		// HashSet<String> anaphorExtentX =
		// getSimpleTrans(anaphor.extent.replaceAll("\\s+", ""));
		// HashSet<String> anaphorHeadX = getSimpleTrans(anaphor.head);
		//
		// HashSet<String> antX = new HashSet<String>(antExtentX);
		// antX.addAll(antHeadX);
		//
		// HashSet<String> anaphorX = new HashSet<String>(anaphorExtentX);
		// anaphorX.addAll(anaphorHeadX);
		//
		// out: for(String x1 : antX) {
		// for(String x2 : anaphorX) {
		// String key = x1 + "###" + x2;
		// String key2 = x1 + "###" + EMUtil.getPorterStem(findHead(x2));
		// if(getYago().contains(key.toLowerCase()) ||
		// getYago().contains(key2.toLowerCase())) {
		// if(coref) {
		// doit = true;
		// break out;
		// }
		// }
		// }
		//
		// }
		// if(!ant.isFake && ant.getXSpan()!=null && anaphor.getXSpan()!=null
		// // && !ant.NE.equals("OTHER")
		// ) {
		// String t1 = ant.getXSpan().getExtent();
		// String t2 = anaphor.getXSpan().getExtent();
		//
		// if(t1.equals(t2)) {
		// // feas[2] = 1;
		// if(coref)
		// doit = true;
		// }
		//
		// String key = t1 + "###" + t2;
		// String tks[] = anaphor.getXSpan().extent.split("\\s+");
		// String key2 = t1 + "###" + findHead(t2);
		//
		// String key3= t1 + "###" + EMUtil.getPorterStem(findHead(t2));
		//
		// // System.out.println(key);
		// if(getYago().contains(key.toLowerCase())
		// || getYago().contains(key2.toLowerCase())
		// || getYago().contains(key3.toLowerCase())
		// ) {
		// System.out.println(t1 + "###" + t2 + " # " + gM1 + ":" + gM2 + ":" +
		// coref);
		// System.out.println(key3);
		// // System.out.println(part.getDocument().getFilePath());
		// // System.out.println("----------");
		// // feas[id++] = 1;
		// // if(coref)
		// // doit = true;
		// } else {
		// // feas[id++] = 0;
		// }
		// } else {
		// // feas[id++] = 0;
		// }

		// if(!ant.isFake) {
		// System.out.println(ant.extent);
		// System.out.println(ant.getXSpan()==null?"null":ant.getXSpan().extent);
		// System.out.println("----------");
		// }
		// System.out.println(anaphor.extent);
		// System.out.println(anaphor.getXSpan()==null?"null":anaphor.getXSpan().extent);
		// System.out.println("----------");

		// feas[id++] = sameProperHeadLastWord(ant, anaphor, part);
		// feas[id++] = isIWithI(ant, anaphor, part); // 2
		// feas[id++] = nested(ant, anaphor, part);
		// feas[id++] = headMatch2(ant, anaphor, part); //
		// feas[id++] = head5(ant, anaphor, part);
		// feas[id++] = isSameGrammatic(ant, anaphor, part);
		// feas[id++] = wordInclusion(ant, anaphor, part);
		// feas[id++] = haveIncompatibleModify(ant, anaphor, part); // 3
		// feas[id++] = (short)((ant.end!=ant.start &&
		// part.getWord(ant.end-1).word.equals(anaphor.head))?1:0);
		// feas[id++] = head6(ant, anaphor, part);
		// feas[id++] = (short)(EMUtil.characterContain(ant.head,
		// anaphor.head)?1:0);

		if (feas[0] == 1 && feas[4] == 0) {
			// System.out.println(ant.extent);
			// System.out.println(anaphor.extent);
			//
			// for(String m1 : ant.modifyList) {
			// System.out.println("m1: " + m1);
			// }
			// System.out.println("---------");
			// for(String m2 : anaphor.modifyList) {
			// System.out.println("m2: " + m2);
			// }
			// System.out.println(part.getPartName());
			// Common.pause("");
		}

		// feas[id++] = headSieve1(ant, anaphor, part);
		// feas[id++] = headSieve2(ant, anaphor, part);
		// feas[id++] = headSieve3(ant, anaphor, part);
		// feas[id++] = sieve4Rule(ant, anaphor, part);

		//
		// feas[id++] = chHaveDifferentLocation(ant, anaphor, part);
		// feas[id++] = numberInLaterMention(ant, anaphor, part);

		// feas[id++] = modifierMatch(ant, anaphor, part);
		// feas[id++] = isSemanticSame(ant, anaphor, part);

		// feas[id++] = head6(ant, anaphor, part);
		return getContext(feas);
	}

	// public static short nested(EntityMention ant, EntityMention anaphor,
	// ACEDoc part) {
	// if(ant.nested) {
	// if(anaphor.nested) {
	// return 0;
	// } else {
	// return 1;
	// }
	// } else {
	// if(anaphor.nested) {
	// return 0;
	// } else {
	// return 1;
	// }
	// }
	// }

	public static short head6(EntityMention ant, EntityMention anaphor,
			ACEDoc part) {
		if (ant.head.equals(anaphor.head) && !anaphor.extent.equals(ant.extent)
				&& anaphor.extent.contains(ant.extent)) {
			return 1;
		}
		return 0;
	}

	// public static boolean ccCompatible(EntityMention ant, EntityMention
	// anaphor, ACEDoc part) {
	// if(ant.extent.equals(anaphor.extent)) {
	// return true;
	// }
	// if(ant.isCC || anaphor.isCC) {
	// return false;
	// } else {
	// return true;
	// }
	// }

	public static short head5(EntityMention ant, EntityMention anaphor,
			ACEDoc part) {
		if (ant.head.contains(anaphor.head) || anaphor.head.contains(ant.head)) {
			boolean overlap = false;
			for (String modifier : ant.modifyList) {
				for (String modifier2 : anaphor.modifyList) {
					if (modifier.equals(modifier2)) {
						overlap = true;
					}
				}
			}
			if (overlap) {
				return 1;
			}
		}
		return 0;
	}

	public static short exactMatchSieve1(EntityMention ant,
			EntityMention anaphor, ACEDoc part) {
		if (ant.extent.equals(anaphor.extent)) {
			boolean modiferCompatible = true;
			ArrayList<String> curModifiers = anaphor.modifyList;
			ArrayList<String> canModifiers = ant.modifyList;
			HashSet<String> curModifiersHash = new HashSet<String>();
			curModifiersHash.addAll(curModifiers);
			HashSet<String> canModifiersHash = new HashSet<String>();
			canModifiersHash.addAll(canModifiers);
			for (String canModifier : canModifiers) {
				if (!curModifiersHash.contains(canModifier)) {
					modiferCompatible = false;
					break;
				}
			}
			for (String curModifier : curModifiers) {
				if (!canModifiersHash.contains(curModifier)) {
					modiferCompatible = false;
					break;
				}
			}
			if (modiferCompatible) {
				return 1;
			}
		}
		return 0;
	}

	// public static short headSieve1(EntityMention ant, EntityMention anaphor,
	// ACEDoc part) {
	// if(ant.head.equals(anaphor.head)) {
	// if(wordInclusion(ant, anaphor, part)==1 && haveIncompatibleModify(ant,
	// anaphor, part)!=1) {
	// return 1;
	// }
	// }
	// return 0;
	// }
	//
	// public static short headSieve2(EntityMention ant, EntityMention anaphor,
	// ACEDoc part) {
	// if(ant.head.contains(anaphor.head)) {
	// if(wordInclusion(ant, anaphor, part)==1) {
	// return 1;
	// }
	// }
	// return 0;
	// }
	//
	// public static short headSieve3(EntityMention ant, EntityMention anaphor,
	// ACEDoc part) {
	// if(ant.head.equals(anaphor.head)) {
	// if(haveIncompatibleModify(ant, anaphor, part)!=1) {
	// return 1;
	// }
	// }
	// return 0;
	// }

	// public static boolean wordInclusion2(EntityMention ant, EntityMention
	// anaphor,
	// ACEDoc part) {
	// List<String> removeW = Arrays.asList(new String[] { "这个", "这", "那个", "全",
	// "此", "本",
	// "那", "自己", "的", "该", "公司", "这些", "那些", "'s" });
	// ArrayList<String> removeWords = new ArrayList<String>();
	// removeWords.addAll(removeW);
	// HashSet<String> mentionClusterStrs = new HashSet<String>();
	// for (int i = anaphor.start; i <= anaphor.end; i++) {
	// mentionClusterStrs.add(part.getWord(i).orig.toLowerCase());
	// if (part.getWord(i).posTag.equalsIgnoreCase("DT")
	// && i < anaphor.end
	// && part.getWord(i + 1).posTag.equalsIgnoreCase("M")) {
	// removeWords.add(part.getWord(i).word);
	// removeWords.add(part.getWord(i + 1).word);
	// }
	//
	// if(part.getWord(i).posTag.equals("PU")) {
	// removeWords.add(part.getWord(i).word);
	// }
	// }
	// mentionClusterStrs.removeAll(removeWords);
	//
	// mentionClusterStrs.remove(anaphor.head.toLowerCase());
	// HashSet<String> candidateClusterStrs = new HashSet<String>();
	// for (int i = ant.start; i <= ant.end; i++) {
	// candidateClusterStrs.add(part.getWord(i).word.toLowerCase());
	// }
	// candidateClusterStrs.remove(ant.head.toLowerCase());
	//
	// HashSet<Character> s1 = new HashSet<Character>();
	// for(String k : candidateClusterStrs) {
	// for(int i=0;i<k.length();i++) {
	// Character c = k.charAt(i);
	// s1.add(c);
	// }
	// }
	//
	// HashSet<Character> s2 = new HashSet<Character>();
	// for(String k : mentionClusterStrs) {
	// for(int i=0;i<k.length();i++) {
	// Character c = k.charAt(i);
	// s2.add(c);
	// }
	// }
	//
	// if(s1.containsAll(s2) && s2.size()!=0 && s2.containsAll(s1)) {
	// return true;
	// }
	// return false;
	// // if (candidateClusterStrs.containsAll(mentionClusterStrs))
	// // return 1;
	// // else
	// // return 0;
	// }

	public static short wordInclusion(EntityMention antecedent, EntityMention mention,
			ACEDoc part) {
		List<String> removeW = Arrays.asList(new String[] { "这个", "这", "那个",
				"那", "自己", "的", "该", "公司", "这些", "那些", "'s" });
		ArrayList<String> removeWords = new ArrayList<String>();
		removeWords.addAll(removeW);
		HashSet<String> mentionClusterStrs = new HashSet<String>();
		for (EntityMention em : mention.entity.mentions) {
			for (MyTreeNode leaf : em.maxTreeNode.getLeaves()) {
				mentionClusterStrs.add(leaf.value);
				// if (ruleCoref.part.getWord(i).posTag.equalsIgnoreCase("DT")
				// && i < em.end
				// && ruleCoref.part.getWord(i +
				// 1).posTag.equalsIgnoreCase("M")) {
				// // removeWords.add(ruleCoref.part.getWord(i).word);
				// // removeWords.add(ruleCoref.part.getWord(i + 1).word);
				// }
			}
		}
		mentionClusterStrs.removeAll(removeWords);

		mentionClusterStrs.remove(mention.head.toLowerCase());
		HashSet<String> candidateClusterStrs = new HashSet<String>();
		for (EntityMention e : antecedent.entity.mentions) {
//			System.out.println(e.maxTreeNode);
			for (MyTreeNode leaf : e.maxTreeNode.getLeaves()) {
				candidateClusterStrs.add(leaf.value);
			}
			candidateClusterStrs.remove(e.head.toLowerCase());
		}
		if (candidateClusterStrs.containsAll(mentionClusterStrs))
			return 1;
		else
			return 0;

		// if (candidateClusterStrs.containsAll(mentionClusterStrs))
		// return 1;
		// else
		// return 0;
	}

	private static short isSemanticSame(EntityMention ana,
			EntityMention anaphor, ACEDoc part) {
		String s1 = EMUtil.getSemantic(ana);
		String s2 = EMUtil.getSemantic(anaphor);
		if (s1.equals(s2) && !"unknown".startsWith(s1)) {
			return 1;
		} else {
			return 0;
		}
	}

	private static short modifierMatch(EntityMention ana,
			EntityMention anaphor, ACEDoc part) {
		HashSet<String> m1s = new HashSet<String>(ana.modifyList);
		HashSet<String> m2s = new HashSet<String>(anaphor.modifyList);

		m1s.removeAll(anaphor.modifyList);
		m2s.remove(ana.modifyList);

		if (m1s.size() != 0) {
			return 1;
		} else if (m2s.size() != 0) {
			return 2;
		}
		return 0;
	}

	private static short getMentionDiss(int diss) {
		if (diss == 0) {
			return 0;
		} else {
			return 1;
		}

		// if(diss>5) {
		// return 5;
		// } else {
		// return (short) diss;
		// }
		// return (short) diss;
	}

	private static short isSamePredicate(EntityMention ant,
			EntityMention anaphor, ACEDoc part) {
		boolean sameV = false;
		if (ant.V != null && anaphor.V != null) {
			String v1 = EMUtil.getPredicateNode(ant.V);
			String v2 = EMUtil.getPredicateNode(anaphor.V);
			if (v1 != null && v2 != null && v1.equals(v2)) {
				sameV = true;
			}
		}

		if (ant.gram == anaphor.gram) {
			if (sameV) {
				return 3;
			}
			return 2;
		} else {
			if (sameV) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	private static short isSameGrammatic(EntityMention ant,
			EntityMention anaphor, ACEDoc part) {
		if (ant.gram == anaphor.gram) {
			return 1;
		} else {
			return 0;
		}
	}

	private static short getHasSameHead(ArrayList<EntityMention> cands,
			EntityMention anaphor, ACEDoc part) {
		// StringBuilder sb = new StringBuilder();
		// sb.append(anaphor.extent).append(":");
		// for(EntityMention c : cands) {
		// sb.append(c.extent).append("#");
		// }
		// System.out.println(sb.toString().trim());
		// System.out.println("=================");
		// System.out.println(part.getPartName());

		boolean hasSameHead = false;
		for (EntityMention m : cands) {
			if (m.head.equals(anaphor.head)
					&& m.extent.contains(anaphor.extent)) {
				hasSameHead = true;
			}
		}
		if (hasSameHead) {
			return 1;
		} else {
			return 0;
		}
	}

	private static short getIsFake(EntityMention ant, EntityMention anaphor,
			ACEDoc part) {
		if (ant.isFake) {
			return 0;
		} else {
			return 1;
		}
	}

	private static short getDistance(EntityMention ant, EntityMention anaphor,
			ACEDoc part) {
		short diss = 0;
		if (ant.isFake) {
			diss = (short) (anaphor.s.id + 1);
		} else {
			diss = (short) (anaphor.s.id - ant.s.id);
		}
		// if(diss>10) {
		// return 10;
		// } else {
		// return (short) diss;
		// }
		return (short) (Math.log(diss) / Math.log(2));
	}

	private static short isExactMatch(EntityMention ant, EntityMention anaphor,
			ACEDoc part) {
		if (ant.extent.equalsIgnoreCase(anaphor.extent)) {
			return 1;
		} else {
			return 0;
		}
	}

	public static short isAbb(EntityMention ant, EntityMention anaphor,
			ACEDoc part) {
		if (Common.isAbbreviation(ant.extent, anaphor.extent)) {
			return 1;
		}
		return 0;
	}

	public static short headMatch(EntityMention ant, EntityMention anaphor,
			ACEDoc part) {
		if (ant.head.equals(anaphor.head)) {
			return 1;
		} else if (ant.head.startsWith(anaphor.head)) {
			return 2;
		} else if (ant.head.endsWith(anaphor.head)) {
			return 3;
			// } else if (ant.head.contains(anaphor.head)){
			// return 4;
		} else {
			return 0;
		}
		// if(ant.head.equals(anaphor.head)) {
		// return 1;
		// } else if (ant.head.contains(anaphor.head)) {
		// return 2;
		// } else if(anaphor.head.contains(ant.head)) {
		// return 3;
		// } else if(anaphor.ACESubtype.equals(ant.ACESubtype)){
		// return 4;
		// } else {
		// return 5;
		// }
	}

	public static short headMatch2(EntityMention ant, EntityMention anaphor,
			ACEDoc part) {
		if (ant.head.contains(anaphor.head)) {
			return 1;
		} else {
			return 0;
		}
	}

	public static short isIWithI(EntityMention ant, EntityMention anaphor,
			ACEDoc part) {
		if (ant.end <= anaphor.start) {
			return 0;
		}
		return 1;
	}

	// public static short haveIncompatibleModify(EntityMention ant,
	// EntityMention anaphor,
	// ACEDoc part) {
	// // if (anaphor.isFake || !ant.head.equalsIgnoreCase(anaphor.head)) {
	// // return 0;
	// // } else if (ant.head.equals(anaphor.head)) {
	// // if (ant.extent.contains(anaphor.extent)) {
	// // return 1;
	// // } else {
	// // return 2;
	// // }
	// // }
	//
	// boolean thisHasExtra = false;
	// Set<String> thisWordSet = new HashSet<String>();
	// Set<String> antWordSet = new HashSet<String>();
	// Set<String> locationModifier = new HashSet<String>(Arrays.asList("东",
	// "南", "西", "北", "中", "东面", "南面", "西面", "北面", "中部", "东北", "西部",
	// "南部", "下", "上", "新", "旧", "前"));
	// for (int i = anaphor.start; i <= anaphor.end; i++) {
	// String w1 = part.getWord(i).orig.toLowerCase();
	// String pos1 = part.getWord(i).posTag;
	// if ((pos1.startsWith("PU") || w1.equalsIgnoreCase(anaphor.head))) {
	// continue;
	// }
	// thisWordSet.add(w1);
	// }
	// for (int j = ant.start; j <= ant.end; j++) {
	// String w2 = part.getWord(j).orig.toLowerCase();
	// antWordSet.add(w2);
	// }
	// for (String w : thisWordSet) {
	// if (!antWordSet.contains(w)) {
	// thisHasExtra = true;
	// }
	// }
	// boolean hasLocationModifier = false;
	// for (String l : locationModifier) {
	// if (antWordSet.contains(l) && !thisWordSet.contains(l)) {
	// hasLocationModifier = true;
	// }
	// }
	// if (thisHasExtra || hasLocationModifier) {
	// return 1;
	// }
	//
	// return 2;
	// }

	private static void moreFea(short antPos, short proPos, short antSynactic,
			short antType, short nearest, short NPClause, short VPClause,
			short[] feas) {
		// feas[1] = nearest;
		feas[2] = antPos;
		feas[3] = antSynactic;
		feas[4] = proPos;
		feas[5] = antType;
		// feas[11] = NPClause;
		// feas[12] = VPClause;
	}

	public static double voP = 0;
	public static double svoP = 0;
	public static double MI = 0;

	public static String message;

	public static HashSet<String> ss = new HashSet<String>();
	public static HashSet<String> vs = new HashSet<String>();

	public static double calMI2(EntityMention ant, EntityMention pronoun) {
		if (svoStat == null) {
			svoStat = new SVOStat();
			svoStat.loadMIInfo();
		}
		String v = EMUtil.getFirstVerb(pronoun.V);
		String o = EMUtil.getObjectNP(pronoun.V);

		String s = EMUtil.getAntAnimacy(ant).name();
		double subjC = getValue(svoStat.unigrams, s);
		// System.out.println(subjC + "##" + s + "###" +
		// svoStat.unigrams.size());

		double subjP = (subjC + 1)
				/ (svoStat.unigramAll + svoStat.unigrams.size());

		// if (o != null && svoStat.voCounts.containsKey(v + " " + o)) {
		// double voC = getValue(svoStat.voCounts, v + " " + o);
		// voP = (voC) / (svoStat.svoAll);
		//
		// double svoC = getValue(svoStat.svoCounts, s + " " + v + " " + o);
		// svoP = (svoC) / (svoStat.svoAll);
		//
		// } else {
		if (!svoStat.vCounts.containsKey(v) || svoStat.vCounts.get(v) < 1000) {
			return 1;
		}

		double voC = getValue(svoStat.vCounts, v);
		voP = (voC) / (svoStat.svoAll);

		double svoC = getValue(svoStat.svCounts, s + " " + v);
		svoP = (svoC) / (svoStat.svoAll);
		// }

		// }

		double MI = Math.log(svoP / (voP * subjP));
		// System.out.println(subjP + " " + voP + " " + svoP);
		// System.out.println(MI + s + " " + v + " " + o);
		// System.out.println("======");

		message = subjP + " " + voP + " " + svoP + '\n' + MI + s + " " + v
				+ " " + o + '\n' + "======";
		return MI;
	}

	// public static double calMI(EntityMention ant, EntityMention pronoun) {
	// if (true)
	// return 1;
	// if (svoStat == null) {
	// long start = System.currentTimeMillis();
	// ObjectInputStream modelInput;
	// // try {
	// // modelInput = new ObjectInputStream(new FileInputStream(
	// // "/dev/shm/svoStat"));
	// // svoStat = (SVOStat) modelInput.readObject();
	// svoStat = new SVOStat();
	// svoStat.loadMIInfo();
	// // } catch (FileNotFoundException e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // } catch (IOException e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // } catch (ClassNotFoundException e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	// // System.out.println(System.currentTimeMillis() - start);
	// }
	// String s = ant.head;
	// String pos = ant.s.getWord(ant.headInS).posTag;
	// String v = EMUtil.getFirstVerb(pronoun.V);
	// String o = EMUtil.getObjectNP(pronoun.V);
	//
	// // System.out.println(s + " " + v + " " + o);
	// String NE = ant.NE;
	// if (ant.NE.equals("OTHER") && EMUtil.NEMap != null
	// && EMUtil.NEMap.containsKey(ant.head)) {
	// NE = EMUtil.NEMap.get(ant.head);
	// }
	//
	// if (NE.equals("PERSON")) {
	// s = "他";
	// pos = "PN";
	// } else if (NE.equals("LOC") || NE.equals("GPE") || NE.equals("ORG")) {
	// s = "它";
	// pos = "PN";
	// }
	// // else if(NE.equals("ORG")) {
	// // s = "公司";
	// // pos = "NN";
	// // }
	//
	// if (!svoStat.unigrams.containsKey(s + " " + pos)
	// || svoStat.unigrams.get(s + " " + pos) < 15000) {
	// return 1;
	// }
	//
	// if (EMUtil.train) {
	// ss.add(s);
	// vs.add(v);
	// } else if (!ss.contains(s) || vs.contains(v)) {
	// // return 1;
	// }
	//
	// double subjC = getValue(svoStat.unigrams, s + " " + pos);
	// double subjP = (subjC + 1)
	// / (svoStat.unigramAll + svoStat.unigrams.size());
	//
	// // if (o != null && svoStat.voCounts.containsKey(v + " " + o)) {
	// // double voC = getValue(svoStat.voCounts, v + " " + o);
	// // voP = (voC) / (svoStat.svoAll);
	// //
	// // double svoC = getValue(svoStat.svoCounts, s + " " + v + " " + o);
	// // svoP = (svoC) / (svoStat.svoAll);
	// // } else {
	// if (!svoStat.vCounts.containsKey(v) || svoStat.vCounts.get(v) < 1000) {
	// return 1;
	// }
	//
	// double voC = getValue(svoStat.vCounts, v);
	// voP = (voC) / (svoStat.svoAll);
	//
	// double svoC = getValue(svoStat.svCounts, s + " " + v);
	// svoP = (svoC) / (svoStat.svoAll);
	// // }
	//
	// double MI = Math.log(svoP / (voP * subjP));
	// // System.out.println(subjP + " " + voP + " " + svoP);
	// // System.out.println(MI + s + " " + v + " " + o);
	// // System.out.println("======");
	//
	// message = subjP + " " + voP + " " + svoP + '\n' + MI + s + " " + NE
	// + " " + v + " " + o + '\n' + "======";
	// return MI;
	// }

	public static double getValue(HashMap<String, Integer> map, String key) {
		if (map.containsKey(key)) {
			return map.get(key);
		} else {
			return 0.00000001;
		}
	}

	// public static short getClauseType(MyTreeNode node, MyTreeNode root) {
	// int IPCounts = node.getXAncestors("IP").size();
	// if (IPCounts > 1) {
	// // subordinate clause
	// return 2;
	// } else {
	// int totalIPCounts = 0;
	// ArrayList<MyTreeNode> frontie = new ArrayList<MyTreeNode>();
	// frontie.add(root);
	// while (frontie.size() > 0) {
	// MyTreeNode tn = frontie.remove(0);
	// if (tn.value.toLowerCase().startsWith("ip")) {
	// totalIPCounts++;
	// }
	// frontie.addAll(tn.children);
	// }
	// if (totalIPCounts > 1) {
	// // matrix clause
	// return 1;
	// } else {
	// // independent clause
	// return 0;
	// }
	// }
	// }

	// public static short sieve4Rule(EntityMention a, EntityMention m, ACEDoc
	// part) {
	// if(sameProperHeadLastWord(a, m, part)==1) {
	// // if(chHaveDifferentLocation(a, m, part)==0)
	// // && numberInLaterMention(a, m, part)==0)
	// return 1;
	// }
	// return 0;
	// }

	// public static short sameProperHeadLastWord(EntityMention a, EntityMention
	// m, ACEDoc part) {
	// String ner1 = a.NE;
	// String ner2 = m.NE;
	// if (a.head.equalsIgnoreCase(m.head) &&
	// part.getWord(a.headID).posTag.equals("NR")
	// && part.getWord(m.headID).posTag.equals("NR")) {
	// return 1;
	// }
	// if(a.head.equalsIgnoreCase(m.head) && ner1.equalsIgnoreCase(ner2) &&
	// (ner1.equalsIgnoreCase("PERSON") || ner1.equalsIgnoreCase("GPE") ||
	// ner1.equalsIgnoreCase("LOC"))) {
	// // return 1;
	// }
	// return 0;
	// }

	// public static short chHaveDifferentLocation(EntityMention antecedent,
	// EntityMention mention, ACEDoc part) {
	// // state and country cannot be coref
	// if
	// ((ChDictionary.getInstance().statesAbbreviation.containsKey(antecedent.extent)
	// || ChDictionary.getInstance().statesAbbreviation
	// .containsValue(mention.extent))
	// && (antecedent.head.equalsIgnoreCase("国")))
	// return 1;
	// Set<String> locationM = new HashSet<String>();
	// Set<String> locationA = new HashSet<String>();
	// String mString = mention.extent.toLowerCase();
	// String aString = antecedent.extent.toLowerCase();
	// Set<String> locationModifier = new HashSet<String>(Arrays.asList("东",
	// "南", "西", "北", "中", "东面", "南面", "西面",
	// "北面", "中部", "东北", "西部", "南部", "下", "上", "新", "旧"));
	//
	// for (int i = mention.start; i <= mention.end; i++) {
	// String word = part.getWord(i).word;
	// if (locationModifier.contains(word)) {
	// return 1;
	// }
	// if (part.getWord(i).rawNamedEntity.equals("LOC")) {
	// String loc = part.getWord(i).word;
	// if (ChDictionary.getInstance().statesAbbreviation.containsKey(loc))
	// loc = ChDictionary.getInstance().statesAbbreviation.get(loc);
	// locationM.add(loc);
	// }
	// }
	// for (int i = antecedent.start; i <= antecedent.end; i++) {
	// String word = part.getWord(i).word;
	// if (locationModifier.contains(word)) {
	// return 1;
	// }
	// if (part.getWord(i).rawNamedEntity.equals("LOC")) {
	// String loc = part.getWord(i).word;
	// if (ChDictionary.getInstance().statesAbbreviation.containsKey(loc))
	// loc = ChDictionary.getInstance().statesAbbreviation.get(loc);
	// locationA.add(loc);
	// }
	// }
	// boolean mHasExtra = false;
	// boolean aHasExtra = false;
	// for (String s : locationM) {
	// if (!aString.contains(s.toLowerCase()))
	// mHasExtra = true;
	// }
	// for (String s : locationA) {
	// if (!mString.contains(s.toLowerCase()))
	// aHasExtra = true;
	// }
	// if (mHasExtra && aHasExtra) {
	// return 1;
	// }
	// return 0;
	// }

	// public static short numberInLaterMention(EntityMention ant, EntityMention
	// mention, ACEDoc part) {
	// Set<String> antecedentWords = new HashSet<String>();
	// Set<String> numbers = new HashSet<String>();
	// numbers.addAll(ChDictionary.getInstance().singleWords);
	// numbers.addAll(ChDictionary.getInstance().pluralWords);
	// for (int i = ant.start; i <= ant.end; i++) {
	// antecedentWords.add(part.getWord(i).word.toLowerCase());
	// }
	// for (int i = mention.start; i < mention.end; i++) {
	// String word = part.getWord(i).word.toLowerCase();
	// try {
	// Double.parseDouble(word);
	// if (!antecedentWords.contains(word))
	// return 1;
	// } catch (NumberFormatException e) {
	// if (numbers.contains(word.toLowerCase()) &&
	// !antecedentWords.contains(word))
	// return 1;
	// continue;
	// }
	// }
	// return 0;
	// }
}
