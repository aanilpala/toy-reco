import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

public class RecommenderTest {

	private int users_size;
	private int items_size;
	private double density;
	
	private Set<Pair<Integer, Integer>> training_set;
	private Set<Pair<Pair<Integer,Integer>, Boolean>> test_set;
	
	public RecommenderTest(int users_size, int items_size, double sparsity) {
		
		this.users_size = users_size;
		this.items_size = items_size;
		this.density = 1.0 - sparsity;
		
		training_set = new HashSet<Pair<Integer, Integer>>();
		test_set = new HashSet<Pair<Pair<Integer,Integer>, Boolean>>();
	}
	
	public void generate_rec_scenerio(byte[] data) {
		
		Random rand = new Random();
		
		int training_set_size = (int) (users_size*items_size*density);
		int test_set_size = (int) (training_set_size*Constants.test_set_ratio);
		
		for(int ctr = 0; ctr < training_set_size; ctr++) {
			
			while(true) {
				 int user_id = rand.nextInt(users_size);
				 int item_id = rand.nextInt(items_size);
				 
				 if(data[item_id*users_size*3 + user_id*3] > 0) { // if the random pixel have a relatively high red balance then we assume correspoding user nad item is somehow connected
					 Pair<Integer, Integer> cur = new Pair<Integer, Integer>(user_id, item_id);
					 if(training_set.contains(cur)) continue;
					 else {
						 training_set.add(cur);
						 break;
					 }
				 }
			}
			
		}
		
		for(int ctr = 0; ctr < test_set_size; ctr++) {
			
			while(true) {
				 int user_id = rand.nextInt(users_size);
				 int item_id = rand.nextInt(items_size);
				 
				 boolean pos_ex;
				 if(rand.nextBoolean()) pos_ex = true;
				 else pos_ex = false;
				 
				 if((pos_ex && data[item_id*users_size*3 + user_id*3] > 0) || (!pos_ex && data[item_id*users_size*3 + user_id*3] < 0)) {
					 Pair<Integer, Integer> cur = new Pair<Integer, Integer>(user_id, item_id);
					 if(training_set.contains(cur)) continue;
					 else {
						 
						 Pair<Pair<Integer, Integer>, Boolean> triple;
						 if(pos_ex) triple = new Pair<Pair<Integer,Integer>,Boolean>(cur, true);
						 else triple = new Pair<Pair<Integer,Integer>,Boolean>(cur, false);
						 
						 if(test_set.contains(triple)) continue;
						 test_set.add(triple);
						 
						 break;
					 }
				 }
			}
			
		}
	}
	
	private void print_rec_scenetio() {
		
		try {
			BufferedWriter training_out = new BufferedWriter(new FileWriter("./training.txt"));
			BufferedWriter test_out = new BufferedWriter(new FileWriter("./test.txt"));
			
			for(Pair<Integer, Integer> each : training_set) {
				training_out.write(each.a + ";" + each.b + "\n");
			}
			
			for(Pair<Pair<Integer, Integer>, Boolean> each : test_set) {
				test_out.write(each.a.a + ";" + each.a.b + ";" + each.b + "\n");
			}
			
			training_out.flush();
			training_out.close();
			
			test_out.flush();
			test_out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void test(RecommenderEngine rec_eng) {
		
		int count = 0;
		int false_pos = 0;
		int false_neg = 0;
		
		try {
			BufferedReader test_in = new BufferedReader(new FileReader(new File("./test.txt")));
			
			String line;
			while((line = test_in.readLine()) != null) {
				
				count++;
				
				String[] tokens = line.split(";");
			
				Boolean b = Boolean.parseBoolean(tokens[2]);
			
				if(b) {
					
					HashSet<Integer> recommended_item_ids = rec_eng.recommend(Integer.parseInt(tokens[0]));
					
					int item_to_be_recomended = Integer.parseInt(tokens[1]);
					
					if(!recommended_item_ids.contains(item_to_be_recomended)) false_neg++;
					
				}
				else {
					
					HashSet<Integer> recommended_item_ids = rec_eng.recommend(Integer.parseInt(tokens[0]));
					
					int item_not_to_be_recomended = Integer.parseInt(tokens[1]);
					
					if(recommended_item_ids.contains(item_not_to_be_recomended)) false_pos++;
				}
			}
			
			test_in.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println("FP Ratio: " + (false_pos / (float) count)*100 + "%");
		System.out.println("FN Ratio: " + (false_neg / (float) count)*100 + "%");
		System.out.println("Accuracy: " + (1-(false_neg + false_pos) / (float) count)*100 + "%");
	}
	
	
	public static void main(String[] args) {
		
		// preparing the test
		
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(new File("/Users/anilpa/GitHub/toy-reco/data/test3.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		
		byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		
		RecommenderTest rec_test = new RecommenderTest(height, width, Constants.sparsity);
		
		rec_test.generate_rec_scenerio(data);
		rec_test.print_rec_scenetio();
		
		// testing
		
		RecommenderEngine rec_eng = new RecommenderEngine(height, width);
		try {
			rec_eng.train("./training.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		rec_test.test(rec_eng);
		
	}

	

	
}
