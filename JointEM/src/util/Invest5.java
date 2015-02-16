package util;

import java.util.ArrayList;

import model.ACEChiDoc;
import model.ACEDoc;

public class Invest5 {

	public static void main(String args[]) {
		ArrayList<String> lines = Common.getLines("ACE_Chinese_train" + args[0]);
		
		int chain = 0;
		int mentions = 0;
		for(String line : lines) {
			ACEDoc doc = new ACEChiDoc(line);
			chain += doc.goldEntities.size();
			mentions += doc.goldEntityMentions.size();
			
			if(doc.content.contains("沙米里")) {
				System.out.println(doc.content);
				System.out.println(line);
			}
		}
		
		System.out.println(chain);
		System.out.println(mentions);
		
//		HashMap<Integer, double[]> maps = new HashMap<Integer, double[]>();
//		
//		for(String line : lines) {
//			ACEChiDoc doc = new ACEChiDoc(line);
//			for(EventChain ec : doc.goldEventChains) {
//				for(int i=0;i<ec.getEventMentions().size();i++) {
//					EventMention ev = ec.getEventMentions().get(i);
//					int sentenceID = doc.positionMap.get(ev.getAnchorStart())[0];
//					double[] st = maps.get(sentenceID);
//					if(st==null) {
//						st = new double[2];
//						maps.put(sentenceID, st);
//					}
//					if(ec.getEventMentions().size()!=1 && i!=0) {
//						st[0] += 1;
//					} else {
//						st[1] += 1;
//					}
//				}
//			}
//		}
//		ArrayList<Integer> set = new ArrayList<Integer>(maps.keySet());
//		Collections.sort(set);
//		double good = 0;
//		double bad = 0;
////		Collections.reverse(set);
//		
//		for(Integer key : set) {
//			double[] st = maps.get(key);
//			good += st[0];
//			bad += st[1];
//			
//			System.out.println(key + ":\t" + good + ":" + bad + "\t" + (good/(good + bad)));
//		}
	}
}
