package entityEMCoref;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import model.ACEChiDoc;
import model.ACEDoc;
import model.Entity;
import model.EntityMention;
import model.ParseResult;
import model.EntityMention.MentionType;
import model.syntaxTree.MyTreeNode;
import util.Common;
import util.Util;
import edu.stanford.nlp.classify.LinearClassifier;
import entityEMCoref.ResolveGroup.Entry;

public class ApplyEM {

	String folder;

	Parameter numberP;
	Parameter genderP;
	Parameter animacyP;
	Parameter semanticP;
	Parameter gramP;
	Parameter cilinP;

	double contextOverall;

	HashMap<String, Double> contextPrior;

	int overallGuessPronoun;

	HashMap<Short, Double> pronounPrior;
	HashMap<Integer, HashMap<Short, Integer>> counts;
	HashMap<Integer, Integer> denomCounts;
	HashMap<Integer, HashSet<Integer>> subSpace;

	HashMap<String, Double> fracContextCount;

	LinearClassifier<String, String> classifier;

	@SuppressWarnings("unchecked")
	public ApplyEM(String folder) {
		this.folder = folder;
		try {
			ObjectInputStream modelInput = new ObjectInputStream(
					new FileInputStream("EMModel"));
			numberP = (Parameter) modelInput.readObject();
			genderP = (Parameter) modelInput.readObject();
			animacyP = (Parameter) modelInput.readObject();
			semanticP = (Parameter) modelInput.readObject();
			gramP = (Parameter) modelInput.readObject();
			cilinP = (Parameter) modelInput.readObject();
			fracContextCount = (HashMap<String, Double>) modelInput
					.readObject();
			contextPrior = (HashMap<String, Double>) modelInput.readObject();

			Context.ss = (HashSet<String>) modelInput.readObject();
			Context.vs = (HashSet<String>) modelInput.readObject();
			// Context.svoStat = (SVOStat)modelInput.readObject();
			modelInput.close();

			// ObjectInputStream modelInput2 = new ObjectInputStream(
			// new FileInputStream("giga2/EMModel"));
			// numberP = (Parameter) modelInput2.readObject();
			// genderP = (Parameter) modelInput2.readObject();
			// animacyP = (Parameter) modelInput2.readObject();
			// personP = (Parameter) modelInput2.readObject();
			// personQP = (Parameter) modelInput2.readObject();
			// fracContextCount = (HashMap<String, Double>) modelInput2
			// .readObject();
			// contextPrior = (HashMap<String, Double>)
			// modelInput2.readObject();

			// modelInput2.close();
			// loadGuessProb();
//			EMUtil.loadPredictNE(folder, "test");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<String> goods = new ArrayList<String>();
	public static ArrayList<String> bads = new ArrayList<String>();

	double good = 0;
	double bad = 0;

	
	
	public void test() {
		String dataset = "test";
		ArrayList<String> files = Common.getLines("ACE_Chinese_test" + folder);

		HashMap<String, ArrayList<EntityMention>> corefResults = new HashMap<String, ArrayList<EntityMention>>();

		// ArrayList<HashSet<String>> goldAnaphorses = new
		// ArrayList<HashSet<String>>();
//		HashMap<String, HashMap<String, String>> maps = EMUtil
//				.extractSysKeys("key.chinese.test.open.systemParse");
//		int all2 = 0;
//		for (String key : maps.keySet()) {
//			all2 += maps.get(key).size();
//		}
//		HashMap<String, HashMap<String, HashSet<String>>> goldKeyses = EMUtil
//				.extractGoldKeys();

		for (int g=0;g<files.size();g++) {
//			if(g%5!=part) {
//				continue;
//			}
			String file = files.get(g);
			// System.out.println(file);
			ACEDoc document = new ACEChiDoc(file
			// .replace("auto_conll", "gold_conll")
			);

//			document.language = "chinese";
			int a = file.indexOf("annotations");
			a += "annotations/".length();
			int b = file.lastIndexOf(".");
			String docName = file.substring(a, b);

//			for (int k = 0; k < document.getParts().size(); k++) {
//				CoNLLPart part = document.getParts().get(k);
//				part.setNameEntities(EMUtil.predictNEs.get(part.getDocument()
//						.getDocumentID() + "_" + part.getPartID()));
//
//				CoNLLPart goldPart = EMUtil.getGoldPart(part, dataset);
//				HashSet<String> goldPNs = EMUtil.getGoldPNs(goldPart);
//				HashSet<String> goldNEs = EMUtil.getGoldNEs(goldPart);

				ArrayList<Entity> goldChains = document.goldEntities;

				HashMap<String, Integer> chainMap = EMUtil
						.formChainMap(goldChains);

				ArrayList<EntityMention> corefResult = new ArrayList<EntityMention>();
				corefResults.put(document.fileID, corefResult);

				ArrayList<EntityMention> goldBoundaryNPMentions = Util.getSieveCorefMentions(document);

				for (EntityMention m : goldBoundaryNPMentions) {
					ArrayList<EntityMention> ms = new ArrayList<EntityMention>();
					ms.add(m);
					// EMUtil.alignMentions(m.s, ms, docName);
				}

				Collections.sort(goldBoundaryNPMentions);

				ArrayList<EntityMention> candidates = new ArrayList<EntityMention>();
				for (EntityMention m : goldBoundaryNPMentions) {
//					if (!goldPNs.contains(m.toName())) {
//					if()
						candidates.add(m);
//					}
				}

				Collections.sort(candidates);

//				HashMap<String, HashSet<String>> goldAnaNouns = EMUtil
//						.getGoldAnaphorKeys(goldChains, goldPart);

				ArrayList<EntityMention> anaphors = new ArrayList<EntityMention>();
				for (EntityMention m : goldBoundaryNPMentions) {
					if(m.mentionType==MentionType.Pronominal) {
						anaphors.add(m);
					}
				}

				findAntecedent(file, document, chainMap, anaphors, candidates);

				for (EntityMention m : anaphors) {
//					if (goldPNs.contains(m.toName())
//							|| goldNEs.contains(m.toName())
//							|| goldNEs.contains(m.end + "," + m.end)
//							|| m.antecedent == null) {
//						continue;
//					}
//					for (EntityMention i : m.innerMs) {
//						if (goldPNs.contains(i.toName())
//								|| goldNEs.contains(i.toName())
//								|| goldNEs.contains(i.end + "," + i.end)) {
//							continue;
//						}
//						i.antecedent = m.antecedent;
//						corefResult.add(i);
//					}
					corefResult.add(m);
				}
			}
//		}
		System.out.println("Good: " + good);
		System.out.println("Bad: " + bad);
		System.out.println("Precission: " + good / (good + bad) * 100);

//		evaluate(corefResults, goldKeyses);
//		int all = 0;
//		for (String key : maps.keySet()) {
//			all += maps.get(key).size();
//		}
//		System.out.println(all + "@@@");
//		System.out.println(all2 + "@@@");

		System.out.println(ApplyEM.allL);
		System.out.println(zeroAnt + "/" + allAnt + ":" + zeroAnt / allAnt);
		System.out.println("Bad_P_C:" + badP_C);
	}

	static double min_amongMax = 1;

	static ArrayList<String> goodAnas = new ArrayList<String>();

	static double allAnt = 0;
	static double zeroAnt = 0;

	static double badP_C = 0;

	static HashMap<String, HashSet<String>> chainMaps = new HashMap<String, HashSet<String>>();

	private void findAntecedent(String file, ACEDoc part,
			HashMap<String, Integer> chainMap, ArrayList<EntityMention> anaphors,
			ArrayList<EntityMention> allCandidates) {
		for (EntityMention anaphor : anaphors) {
			anaphor.sentenceID = part.getParseResult(anaphor.headStart).id;
			anaphor.s = part.getParseResult(anaphor.headStart);

			EntityMention antecedent = null;
			double maxP = 0;
			Collections.sort(allCandidates);

			ArrayList<EntityMention> cands = new ArrayList<EntityMention>();

			for (int h = allCandidates.size() - 1; h >= 0; h--) {
				EntityMention cand = allCandidates.get(h);
				cand.sentenceID = part.getParseResult(cand.headStart).id;
				cand.s = part.getParseResult(cand.headStart);

				if (cand.start < anaphor.start
						&& anaphor.sentenceID - cand.sentenceID <= EMLearn.maxDistance
						&& cand.end != anaphor.end
				// && !predictBadOnes.contains(part.getPartName() + ":" +
				// cand.toName())
				) {

					cands.add(cand);
				}
			}
			EntityMention fake = new EntityMention();
			fake.extent = "fakkkkke";
			fake.head = "fakkkkke";
			fake.isFake = true;
			cands.add(fake);

			ResolveGroup rg = new ResolveGroup(anaphor, part, cands);
			int seq = 0;
			for (EntityMention cand : cands) {
				Entry entry = new Entry(cand, null, part);
				rg.entries.add(entry);
				entry.p_c = EMUtil.getP_C(cand, anaphor, part);
				if (entry.p_c != 0) {
					seq += 1;
				}
				
				if(!chainMaps.containsKey(entry.antName) && !entry.isFake) {
					HashSet<String> set = new HashSet<String>();
					set.add(entry.antName);
					chainMaps.put(entry.antName, set);
				}
			}
			for (Entry entry : rg.entries) {
				if (entry.isFake) {
					entry.p_c = Entry.p_fake_decay / (Entry.p_fake_decay + seq);
				} else if (entry.p_c != 0) {
					entry.p_c = 1 / (Entry.p_fake_decay + seq);
				}
			}

//			ArrayList<Entry> goodEntries = new ArrayList<Entry>();
//			ArrayList<Entry> fakeEntries = new ArrayList<Entry>();
//			ArrayList<Entry> badEntries = new ArrayList<Entry>();
//			for (int i = 0; i < rg.entries.size(); i++) {
//				Entry entry = rg.entries.get(i);
//				if (entry.isFake) {
//					fakeEntries.add(entry);
//				} else if (entry.ant.head.contains(anaphor.head)) {
//					goodEntries.add(entry);
//				} else {
//					badEntries.add(entry);
//				}
//			}
//			ArrayList<Entry> allEntries = new ArrayList<Entry>();
//			allEntries.addAll(goodEntries);
//			allEntries.addAll(fakeEntries);
//			allEntries.addAll(badEntries);
//			for (int i = 0; i < allEntries.size(); i++) {
//				allEntries.get(i).seq = i;
//			}

			EMLearn.sortEntries(rg, chainMaps);
			
			double probs[] = new double[cands.size()];

			ArrayList<EntityMention> goldCorefs = new ArrayList<EntityMention>();

			for (int i = 0; i < rg.entries.size(); i++) {
				Entry entry = rg.entries.get(i);
				EntityMention cand = cands.get(i);

				boolean coref = chainMap.containsKey(anaphor.toName())
						&& chainMap.containsKey(cand.toName())
						&& chainMap.get(anaphor.toName()).intValue() == chainMap
								.get(cand.toName()).intValue();
				Context.coref = coref;
				Context.gM1 = chainMap.containsKey(cand.toName());
				Context.gM2 = chainMap.containsKey(anaphor.toName());
				entry.context = Context.buildContext(cand, anaphor, part,
						cands, entry.seq);
				if (Context.doit) {
					anaphor.antecedent = cand;
					break;
				}
				cand.msg = Context.message;

				allAnt++;
				if (entry.p_c == 0) {
					if (coref) {
						badP_C++;
					}
					zeroAnt++;
				}
				if (coref && entry.p_c != 0) {
					goldCorefs.add(cand);
				}
			}

			//TODO
			String antName = "";
			if (anaphor.antecedent == null)
				for (int i = 0; i < rg.entries.size(); i++) {
					Entry entry = rg.entries.get(i);
					EntityMention cand = entry.ant;
					Context context = entry.context;

					// if(entry.p_c!=0) {
					// entry.p_c = 1.0/(seq+1);
					// }

					// calculate P(overt-pronoun|ant-context)
					// if(entry.p_c!=0) {
					// antecedent = cand;
					// break;
					// }
					// if(entry.p_c==0 && coref &&
					// !cand.head.equals(anaphor.head)) {
					// System.out.println(coref);
					// print(cand, anaphor, part, chainMap);
					// }

					double p_number = numberP.getVal(entry.number.name(),
							EMUtil.getAntNumber(anaphor).name());
					double p_animacy = animacyP.getVal(entry.animacy.name(),
							EMUtil.getAntAnimacy(anaphor).name());
					double p_gender = genderP.getVal(entry.gender.name(),
							EMUtil.getAntGender(anaphor).name());
					double p_sem = semanticP.getVal(entry.sem,
							EMUtil.getSemantic(anaphor));

//					double p_cilin = cilinP.getVal(entry.cilin,
//							EMUtil.getModifiers(anaphor, part));

					double p_gram = semanticP.getVal(entry.gram.name(),
							anaphor.gram.name());

					double p_context = 0.0000000000000000000000000000000000000000000001;
					if (fracContextCount.containsKey(context.toString())) {
						p_context = (1.0 * EMUtil.alpha + fracContextCount
								.get(context.toString()))
								/ (2.0 * EMUtil.alpha + contextPrior
										.get(context.toString()));
					} else {
						p_context = 1.0 / 2;
					}

					double p2nd = p_context * entry.p_c;
					p2nd *= 1
					// p_number *
					// p_gender *
					// p_animacy *
					* p_sem
					// * p_cilin
					// * p_gram
					;
					double p = p2nd;
					probs[i] = p;
					if (p > maxP && p != 0) {
						antecedent = cand;
						maxP = p;
						antName = entry.antName;
					}
				}

//			if (antecedent != null && antecedent.isFake) {
//				if (goldCorefs.size() != 0) {
//					// anaphor.antecedent= goldCorefs.get(0);
//					System.out.println("Anaphor: " + anaphor.extent + " "
//							+ anaphor.ACEType + " # " + anaphor.ACESubtype
//							+ " # " + chainMap.containsKey(anaphor.toName()));
//					System.out.println("Selected: " + antecedent.extent + " "
//							+ antecedent.ACEType + " # "
//							+ antecedent.ACESubtype + " # "
//							+ chainMap.containsKey(antecedent.toName()));
//					System.out.println("True Ante: ");
//					for (EntityMention m : goldCorefs) {
//						System.out.println(m.extent + " " + m.ACEType + " # "
//								+ m.ACESubtype);
//					}
//					System.out.println("---------------------------");
//				}
//			}

			if (antecedent != null && !antecedent.isFake
					&& anaphor.antecedent == null) {
				HashSet<String> corefs = chainMaps.get(antName);
				corefs.add(rg.anaphorName);
				chainMaps.put(rg.anaphorName, corefs);
				
				anaphor.antecedent = antecedent;

				boolean coref = chainMap.containsKey(anaphor.toName())
						&& chainMap.containsKey(antecedent.toName())
						&& chainMap.get(anaphor.toName()).intValue() == chainMap
								.get(antecedent.toName()).intValue();

				if (!coref && goldCorefs.size() != 0) {
					// anaphor.antecedent= goldCorefs.get(0);
					System.out.println("Anaphor: " + anaphor.extent + " "
							+ anaphor.semClass + " # " + anaphor.subType
							+ " # " + chainMap.containsKey(anaphor.toName()));
					System.out.println("Selected: " + antecedent.extent + " "
							+ antecedent.semClass + " # "
							+ antecedent.subType + " # "
							+ chainMap.containsKey(antecedent.toName()));
					System.out.println("True Ante: ");
					for (EntityMention m : goldCorefs) {
						System.out.println(m.extent + " " + m.type + " # "
								+ m.subType);
					}
					System.out.println("---------------------------");
					// print(antecedent, anaphor, part, chainMap);
				}

				if (!coref && goldCorefs.size() == 0) {
					// anaphor.antecedent= null;
					System.out.println("Anaphor: " + anaphor.extent + " "
							+ anaphor.semClass + " # " + anaphor.subType
							+ " # " + chainMap.containsKey(anaphor.toName()));
					System.out.println("Selected: " + antecedent.extent + " "
							+ antecedent.semClass + " # "
							+ antecedent.subType + " # "
							+ chainMap.containsKey(antecedent.toName()));
					System.out.println("True Ante: EMPTY");
					System.out.println("---------------------------");
					// print(antecedent, anaphor, part, chainMap);
				}

			}
		}
	}

	public static void print(EntityMention antecedent, EntityMention anaphor,
			ACEDoc part, HashMap<String, Integer> chainMap) {
		System.out.println(antecedent.extent + " # "
				+ chainMap.containsKey(antecedent.toName()));
		System.out.println(antecedent.s.toString());
		System.out.println(anaphor.extent + " # "
				+ chainMap.containsKey(anaphor.toName()));
		System.out.println(anaphor.s.sentence);
		System.out.println(part.docID);
		System.out.println("----");
	}

	static int allL = 0;

	protected void printResult(EntityMention zero, EntityMention systemAnte, ACEDoc part) {
		StringBuilder sb = new StringBuilder();
		ParseResult s = part.getParseResult(zero.headStart);
//		for (int i = word.indexInSentence; i < s.words.size(); i++) {
//			sb.append(s.words.get(i)).append(" ");
//		}
//		System.out.println(sb.toString() + " # " + zero.start);
		// System.out.println("========");
	}

	// public void addEmptyCategoryNode(EntityMention zero) {
	// MyTreeNode V = zero.V;
	// MyTreeNode newNP = new MyTreeNode();
	// newNP.value = "NP";
	// int VIdx = V.childIndex;
	// V.parent.addChild(VIdx, newNP);
	//
	// MyTreeNode empty = new MyTreeNode();
	// empty.value = "-NONE-";
	// newNP.addChild(empty);
	//
	// MyTreeNode child = new MyTreeNode();
	// child.value = zero.extent;
	// empty.addChild(child);
	// child.emptyCategory = true;
	// zero.NP = newNP;
	// }

	static String prefix = "/shared/mlrdir1/disk1/mlr/corpora/CoNLL-2012/conll-2012-train-v0/data/files/data/chinese/annotations/";
	static String anno = "annotations/";
	static String suffix = ".coref";

	// private static ArrayList<EntityMention> getGoldNouns(ArrayList<Entity>
	// entities,
	// CoNLLPart goldPart) {
	// ArrayList<EntityMention> goldAnaphors = new ArrayList<EntityMention>();
	// for (Entity e : entities) {
	// Collections.sort(e.mentions);
	// for (int i = 1; i < e.mentions.size(); i++) {
	// Mention m1 = e.mentions.get(i);
	// String pos1 = goldPart.getWord(m1.end).posTag;
	// if (pos1.equals("PN") || pos1.equals("NR") || pos1.equals("NT")) {
	// continue;
	// }
	// goldAnaphors.add(m1);
	// }
	// }
	// Collections.sort(goldAnaphors);
	// for (EntityMention m : goldAnaphors) {
	// EMUtil.setMentionAttri(m, goldPart);
	// }
	// return goldAnaphors;
	// }
	//
	// private static ArrayList<EntityMention> getGoldAnaphorNouns(
	// ArrayList<Entity> entities, CoNLLPart goldPart) {
	// ArrayList<EntityMention> goldAnaphors = new ArrayList<EntityMention>();
	// for (Entity e : entities) {
	// Collections.sort(e.mentions);
	// for (int i = 1; i < e.mentions.size(); i++) {
	// Mention m1 = e.mentions.get(i);
	// String pos1 = goldPart.getWord(m1.end).posTag;
	// if (pos1.equals("PN") || pos1.equals("NR") || pos1.equals("NT")) {
	// continue;
	// }
	// HashSet<String> ants = new HashSet<String>();
	// for (int j = i - 1; j >= 0; j--) {
	// Mention m2 = e.mentions.get(j);
	// String pos2 = goldPart.getWord(m2.end).posTag;
	// if (!pos2.equals("PN") && m1.end != m2.end) {
	// ants.add(m2.toName());
	// }
	// }
	// if (ants.size() != 0) {
	// goldAnaphors.add(m1);
	// }
	// }
	// }
	// Collections.sort(goldAnaphors);
	// for (EntityMention m : goldAnaphors) {
	// EMUtil.setMentionAttri(m, goldPart);
	// }
	// return goldAnaphors;
	// }

	public static void evaluate(HashMap<String, ArrayList<EntityMention>> anaphorses,
			HashMap<String, HashMap<String, HashSet<String>>> goldKeyses) {
		double gold = 0;
		double system = 0;
		double hit = 0;

		for (String key : anaphorses.keySet()) {
			ArrayList<EntityMention> anaphors = anaphorses.get(key);
			HashMap<String, HashSet<String>> keys = goldKeyses.get(key);
			gold += keys.size();
			system += anaphors.size();
			for (EntityMention anaphor : anaphors) {
				EntityMention ant = anaphor.antecedent;
				if (keys.containsKey(anaphor.toName())
						&& keys.get(anaphor.toName()).contains(ant.toName())) {
					hit++;
				}
			}
		}

		double r = hit / gold;
		double p = hit / system;
		double f = 2 * r * p / (r + p);
		System.out.println("============");
		System.out.println("Hit: " + hit);
		System.out.println("Gold: " + gold);
		System.out.println("System: " + system);
		System.out.println("============");
		System.out.println("Recall: " + r * 100);
		System.out.println("Precision: " + p * 100);
		System.out.println("F-score: " + f * 100);
	}

	static ArrayList<String> corrects = new ArrayList<String>();

	public static void main(String args[]) {
		if (args.length != 1) {
			System.err.println("java ~ folder");
			System.exit(1);
		}
//		// EMUtil.loadAlign();
//		ArrayList<String> allMs = Common.getLines("allMs");
//		ArrayList<String> preds = Common
//				.getLines("/users/yzcchen/tool/svmlight/svm.anaphor.pred");
//		predictBadOnes = new HashSet<String>();
//		for (int i = 0; i < allMs.size(); i++) {
//			String m = allMs.get(i);
//			String pred = preds.get(i);
//			if (Double.parseDouble(pred) < 0) {
//				predictBadOnes.add(m);
//			}
//		}
		run(args[0]);
//		run("nw");
//		run("mz");
//		run("wb");
//		run("bn");
//		run("bc");
//		run("tc");
	}

//	static int part;
	
	public static void run(String folder) {
		EMUtil.train = false;
		ApplyEM test = new ApplyEM(folder);
		
		
//		for(int i=0;i<5;i++) {
//			part = i;
			test.test();
//			Common.pause("!!#");
//		}

		System.out.println("RUNN: " + folder);
		Common.outputHashSet(Context.todo, "todo.word2vec");
		if (Context.todo.size() != 0) {
			System.out.println("!!!!! TODO WORD2VEC!!!!");
			System.out.println("check file: todo.word2vec "
					+ Context.todo.size());
		}
		// Common.outputLines(goodAnas, "goodAnaphors");

		// Common.outputHashSet(EMUtil.semanticInstances, "semanticInstance");

		Common.pause("!!#");
	}
}
