import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class RecommenderEngine {

	private int users_size;
	private int items_size;

	
	private int number_of_hash_funcs = 6;
	private int[][] signature_matrix;
	
	private Map<Integer, HashSet<Integer>> sparse_utility_matrix;
	
	
	public RecommenderEngine(int users_size, int items_size) { 
		
		this.users_size = users_size;
		this.items_size = items_size;
		
		signature_matrix = new int[number_of_hash_funcs][users_size];
		sparse_utility_matrix = new HashMap<Integer, HashSet<Integer>>();

		for(int ctr = 0; ctr < number_of_hash_funcs; ctr++) {
			for (int ctr2 = 0; ctr2 < users_size; ctr2++) {
				signature_matrix[ctr][ctr2] = Integer.MAX_VALUE;
			}
		}
		
	}

	public void train(String string) throws IOException {
		
		
		// building the (sparse) utility matrix
		BufferedReader train_in = new BufferedReader(new FileReader(new File(string)));
		
		String line;
		while((line = train_in.readLine()) != null) {
			String[] tokens = line.split(";");
			
			int user_id = Integer.parseInt(tokens[0]);
			int item_id = Integer. parseInt(tokens[1]);
			
			HashSet<Integer> list = sparse_utility_matrix.get(user_id);
			if(list == null) {
				list = new HashSet<Integer>();
				list.add(item_id);
				
				sparse_utility_matrix.put(user_id, list);
			}
			else
				list.add(item_id);
		}
		
		train_in.close();
		
		// building the signature matrix
		
		for(int cur_item_id = 0; cur_item_id < items_size; cur_item_id++) {
			ArrayList<Integer> active_set = new ArrayList<Integer>();
			for(int cur_user : sparse_utility_matrix.keySet()) {
				HashSet<Integer> user_ids = sparse_utility_matrix.get(cur_user);
				if(user_ids.contains(cur_item_id)) active_set.add(cur_user);
			}
			for(int cur_hash_func_index = 0; cur_hash_func_index < number_of_hash_funcs; cur_hash_func_index++) {
				int hash_val = hash(cur_hash_func_index, cur_item_id);
				for(int user_id : active_set) {
					if(hash_val < signature_matrix[cur_hash_func_index][user_id])
						signature_matrix[cur_hash_func_index][user_id] = hash_val;
				}
			}
		}
	}
	
	public HashSet<Integer> recommend(int user_id) {
		
		HashSet<Integer> similar_users = new HashSet<Integer>();
		
		for(int current_user_id = 0; current_user_id < users_size; current_user_id++) {
			if(current_user_id == user_id) continue; 
			double similarity = 0.0;
			for(int cur_hash_func_index = 0; cur_hash_func_index < number_of_hash_funcs; cur_hash_func_index++) {
				if(signature_matrix[cur_hash_func_index][current_user_id] == signature_matrix[cur_hash_func_index][user_id]) similarity += 1.0;
			}
			similarity /= number_of_hash_funcs;
			if(similarity >= Constants.similarity_threshold)
				similar_users.add(current_user_id);
		}
		
		HashSet<Integer> recommended_item_ids = new HashSet<Integer>();
		
		Iterator<Integer> it = similar_users.iterator();
		
		while(it.hasNext()) {
			int cur_similar_user_id = it.next();
			recommended_item_ids.addAll(sparse_utility_matrix.get(cur_similar_user_id));
		}
		
		return recommended_item_ids;
	}
	
	private int hash(int hash_func_index, int input) {
		
		if(hash_func_index == 0) return (31*input + 1) % items_size;
		if(hash_func_index == 1) return (17*input + 3) % items_size;
		if(hash_func_index == 2) return (5*input + 19) % items_size;
		if(hash_func_index == 3) return (993)*input + 11 % items_size;
		if(hash_func_index == 4) return (13)*input + 23 % items_size;
		if(hash_func_index == 5) return (19)*input + 5 % items_size;
		
		
		return -1;
		
	}

}
