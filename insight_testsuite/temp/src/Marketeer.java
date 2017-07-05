
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


/**
 * Class to store Transaction information
 * @author Bharat Shrinevas
 *
 */
class Transactions {
	
	Long id;
	Timestamp timestamp;
	Float amount;
	
	public Transactions(Long id, Timestamp t, Float amt) {
		this.id = id;
		this.timestamp = t;
		this.amount = amt;
	}
		
	
	Long getId() {
		return id;
	}

	void setId(Long id) {
		this.id = id;
	}
	
	

	Timestamp getTimestamp() {
		return timestamp;
	}

	void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
	

	Float getAmount() {
		return amount;
	}

	void setAmount(Float amount) {
		this.amount = amount;
	}
	
	
}

/**
 * Class to store Fraudulent transactions
 * @author Bharat Shrinevas
 *
 */
class FraudulentTransaction extends Transactions {
	
	public FraudulentTransaction(Long id, Timestamp t, Float amt, Float mean, Float std) {
		
		super(id, t, amt);
		this.mean = mean;
		this.std = std; 
		
	}
	
	Float mean;
	Float std;
	
	
	Float getMean() {
		return mean;
	}
	void setMean(Float mean) {
		this.mean = mean;
	}
	
	
	Float getStd() {
		return std;
	}
	void setStd(Float std) {
		this.std = std;
	}
	
}


/**
 * Represents a node in the Graph
 * @author Bharat Shrinevas
 *
 */
class Person {
	
	Long id;
	ArrayList<Transactions> transactionList;
	LinkedList<Long> friends;
	
	public Person(Long id) {
		
		this.id = id;
		transactionList = new ArrayList<Transactions>();
		friends = new LinkedList<Long>();
		
	}
	
	Long getId() {
		return id;
	}
	void setId(Long id) {
		this.id = id;
	}
	
	
	ArrayList<Transactions> getTransactionList() {
		return transactionList;
	}
	void setTransactionList(ArrayList<Transactions> transactionList) {
		this.transactionList = transactionList;
	}
	
	
	LinkedList<Long> getFriends() {
		return friends;
	}
	void setFriends(LinkedList<Long> friends) {
		this.friends = friends;
	}
	
	void printTransactionList() {
		for(Transactions t : this.transactionList) {
			System.out.print("id:" + t.getId() + "  timestamp:" + t.getTimestamp() + "  amount:" + t.getAmount());
			System.out.println();
		}
	}
		
}

/**
 * Data structure to store the friends Graph using adjacency lists.
 * @author Bharat Shrinevas
 *
 */
class Graph {

	//Map of adjacency lists for each node
    Map<Long, Person> adj;
    
    public Graph() {
	    adj = new HashMap<Long, Person>();
    }
    

    void addFriend(Long v1, Long v2) {
        adj.get(v1).getFriends().add(v2);
    }

    void removeFriend(Long v1, Long v2) {
        adj.get(v1).getFriends().remove(v2);
        adj.get(v2).getFriends().remove(v1);
    }

    LinkedList<Long> getFriends(Long v) {
        return adj.get(v).getFriends();
    }
    
    void addNode(Long nodeId) {
    	if(!adj.containsKey(nodeId))
    		adj.put(nodeId, new Person(nodeId));
    }
    
    
    Person getNode(Long v) {
    	return adj.get(v);
    }
    
    
    /**
     * Finds neighbors X degrees apart.
     * @param person
     * @param degrees
     * @return List<Long>
     * 
     * NOTE: A quicker implementation would be to use Matrix factorization.
     */
    List<Long> getNeighbors(Person person, Integer degrees) {
    	
    	
    	List<Long> friendsList = new ArrayList<Long>(adj.get(person.getId()).getFriends());
    	HashMap<Integer, List<Long>> friendsMap = new HashMap<Integer, List<Long>>();
    	friendsMap.put(0, friendsList);
    	
    	if(degrees == 1) {
    		return friendsList;
    	}
    	
    	Set<Long> friendsSet = new HashSet<Long>(adj.get(person.getId()).getFriends());
    	Set<Long> tempSet = null;    	
    	for(int i=1; i<degrees; i++) {
    		tempSet = new HashSet<Long>();
    		for(Long friend: friendsMap.get(i-1)) {    			
    			tempSet.addAll(adj.get(friend).getFriends());
    			if(tempSet.contains(person.getId()))
    				tempSet.remove(person.getId());
    		}
    		friendsMap.put(i, new ArrayList<Long>(tempSet));
    		friendsSet.addAll(tempSet);
    	}
    	
    	return new ArrayList<Long>(friendsSet);
    }
    
    /**
     * Validates if a transaction is fraudulent or otherwise
     * @param personId
     * @param amount
     * @param degree
     * @param numTransactions
     * @return FraudulentTransaction
     */
    FraudulentTransaction validateTransaction(Long personId, Float amount, Integer degree, Integer numTransactions) {
    	
    	FraudulentTransaction fdt = null;
    	//find friends upto <degree> apart 
    	List<Long> friends = getNeighbors(adj.get(personId), degree);
    	ArrayList<Transactions> allTransactions = new ArrayList<Transactions>();
    	
    	
    	//compute mean and SD for the transactions
    	for(Long friend : friends) {
    		ArrayList<Transactions> transactions = this.getNode(friend).getTransactionList();
    	    Collections.sort(transactions, new Comparator<Transactions>() {
    	    	@Override
    	    	public int compare(Transactions o1, Transactions o2) {
    	    		if(o1.getTimestamp().before(o2.getTimestamp()))
    	    			return 1;
    	    		else if(o2.getTimestamp().before(o1.getTimestamp()))
    	    			return -1;
    	    		else
    	    			return 0;
    	    	}
    	    });
    	    
    	    if(numTransactions > transactions.size())
    	    	allTransactions.addAll(transactions);
    	    else
    	    	allTransactions.addAll(transactions.subList(0, numTransactions));    	    
    	}
    	

	    Collections.sort(allTransactions, new Comparator<Transactions>() {
	    	@Override
	    	public int compare(Transactions o1, Transactions o2) {
	    		if(o1.getTimestamp().before(o2.getTimestamp()))
	    			return 1;
	    		else if(o2.getTimestamp().before(o1.getTimestamp()))
	    			return -1;
	    		else
	    			return 0;
	    	}
	    });

    	//if less than 2 transactions not enough data to determine anomalous transaction
    	if(allTransactions.size() < 2)
    		return null;

	    //take the top 50 transactions or the number of transactions greater than 2
    	List<Transactions> finalTransactions = null;
    	if(allTransactions.size() < numTransactions) {
    		finalTransactions = allTransactions.subList(0, allTransactions.size());
    	} else {
    		finalTransactions = allTransactions.subList(0, numTransactions);
    	}
    	
	    //compute SD and mean
	    float mean = GraphMath.mean(finalTransactions);
	    float std = GraphMath.Std(finalTransactions);
	    
	    if (amount > (mean + (3*std))) {
	    	fdt = new FraudulentTransaction(personId, null, amount, mean, std);
	    }
	    
	    return fdt;
    	    	
    }
    
    
    /**
     * Visual view of the graph
     */
    void printGraph() {
    	final Set<Map.Entry<Long, Person>> entries = adj.entrySet();
    	
    	for (Map.Entry<Long, Person> entry : entries) {
    		Long key = entry.getKey();
    	    LinkedList<Long> friendList = entry.getValue().getFriends();
    	    System.out.print(key + " : --> ");
    	    System.out.print(friendList.toString());
    	    System.out.println();    	    
    		
    	}
    }
    
}



    
    
/**
 * Math class for calculating Mean and Standard Deviation    
 * @author Bharat Shrinevas
 *
 */
class GraphMath {
	
	/**
	 * Calculate Mean
	 * @param numberList
	 * @return Float
	 */
	static Float mean(List<Transactions> transactionList) {
		
		Float total = 0F;
		
		if(transactionList.isEmpty())
			return 0F;
		
		for(Transactions t : transactionList) {
			total += t.getAmount();
		}
		
		return total/transactionList.size();
		
	}
	
	/**
	 * Calculate Standard Deviation
	 * @param numberList
	 * @return Float
	 */
	static Float Std(List<Transactions> transactionList) {
		
		Float mean = mean(transactionList);
		Float tempSD = 0f;
		for(Transactions t : transactionList) {
			tempSD += (float) Math.pow(t.getAmount()-mean, 2);
		}
		
		tempSD = tempSD/transactionList.size();
		
		return (float) Math.sqrt(tempSD);
	}

}

/**
 * Main execution class
 * @author Bharat Shrinevas
 *
 */
public class Marketeer {
	

	public static void main(String[] args) {
	        	
	    	JSONParser jsonParser = new JSONParser(); 
	    	Graph marketeerGraph = new Graph();
	    	int degree=1, numTransactions=1;
	    	
	    	//Read batch_log.json and create the Graph and store Transaction details
	    	try (Stream<String> lines = Files.lines(Paths.get("./log_input/batch_log.json"))) {
	    		long ctr = 1;
	    		long transactionId = 0;
	    		
		    	for (String line : (Iterable<String>) lines::iterator) {
		    		JSONObject jsonObj = (JSONObject)jsonParser.parse(line);
		    		if(jsonObj == null)
		    			continue;
		    		if(ctr == 1) {
		    			degree = Integer.valueOf((String)jsonObj.get("D"));
		    			numTransactions = Integer.valueOf((String)jsonObj.get("T"));
		    			ctr++;
		    			continue;
		    		}
		    		
		    		String event_type = (String)jsonObj.get("event_type");
		    		Long id=null,id1=null,id2=null;
		    		Float amount=null;
		    		
		    		Timestamp ts = Timestamp.valueOf((String)jsonObj.get("timestamp"));
		    		
		    		if(event_type.equals("befriend") || event_type.equals("unfriend")) {
		    			id1 = Long.valueOf((String)jsonObj.get("id1"));
		    			id2 = Long.valueOf((String)jsonObj.get("id2"));
		    			
		    			if(event_type.equals("befriend")) {
		    				System.out.println("Adding Friend :" + id1 + ", " + id2);
			    			marketeerGraph.addNode(id1);
			    			marketeerGraph.addNode(id2);
			    			marketeerGraph.addFriend(id1, id2);
			    			marketeerGraph.addFriend(id2, id1);	    				
		    			} else {
		    				System.out.println("Remvoing Friend :" + id1 + ", " + id2);
			    			marketeerGraph.removeFriend(id1, id2);
			    			marketeerGraph.removeFriend(id2, id1);	    					    				
		    			}
		    			
		    		} else {
		    			id = Long.valueOf((String)jsonObj.get("id"));
		    			amount = Float.valueOf((String)jsonObj.get("amount"));
		    			marketeerGraph.addNode(id);
		    			Transactions t = new Transactions(++transactionId, ts, amount);
		    			marketeerGraph.getNode(id).getTransactionList().add(t);	    			
		    		}
		    			    			    			    		    			    		
		    		ctr++;
		    		
		    	}
		    	
		    	System.out.println("Number of Lines Read = " + (ctr-1));    	
		    	
	
	    	} catch(Exception ex) {
	    		System.out.println(ex.getMessage());
	        }
	    	
	    	File file = new  File("./log_output/flagged_purchases.json");
	    	FileWriter fw = null;
	    	BufferedWriter bw  = null;
	    	
	    	try (Stream<String> lines = Files.lines(Paths.get("./log_input/stream_log.json"))) {
	    		
	    		Long id=null,id1=null,id2=null;
	    		Float amount=null;
				DecimalFormat df = new DecimalFormat();
				df.setMaximumFractionDigits(2);
				df.setMinimumFractionDigits(2);

	    		
	    		if (!file.exists()) {
	                file.createNewFile();
	            }
	    		
	    		fw = new FileWriter(file.getAbsoluteFile());
	    		bw = new BufferedWriter(fw);
	    		

	    		for (String line : (Iterable<String>) lines::iterator) {
		    		JSONObject jsonObj = (JSONObject)jsonParser.parse(line);
		    		if(jsonObj == null)
		    			continue;
		    		
		    		String event_type = (String)jsonObj.get("event_type");
		    		Timestamp ts = Timestamp.valueOf((String)jsonObj.get("timestamp"));
		    		String formattedTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ts);
		    		
		    		if(event_type.equals("purchase")) {
		    			id = Long.valueOf((String)jsonObj.get("id"));
		    			amount = Float.valueOf((String)jsonObj.get("amount"));
		    			System.out.println("Processing Purchase :" + id + " for Fraud.");
		    			FraudulentTransaction ft = marketeerGraph.validateTransaction(id, amount, degree, numTransactions);
		    			if(ft != null) { //write to file
		    				
		    				StringBuilder sb = new StringBuilder();
		    				String str = sb.append("{\"event_type\":\"purchase\", \"timestamp\":\"").append(formattedTimestamp).append("\", \"id\":\"").append(id)
		    						.append("\", \"amount\":\"").append(amount).append("\", \"mean\":\"").append(df.format(ft.getMean())) 
		    						.append("\", \"sd\":").append("\"").append(df.format(ft.getStd())).append("\"}").toString();
		    				bw.write(str);
		    				bw.write("\n");
		    			}
		    		}
		    		
		    		if(event_type.equals("befriend") || event_type.equals("unfriend")) {
		    			id1 = Long.valueOf((String)jsonObj.get("id1"));
		    			id2 = Long.valueOf((String)jsonObj.get("id2"));
		    			
		    			if(event_type.equals("befriend")) {
		    				System.out.println("Adding Friend :" + id1 + ", " + id2);
			    			marketeerGraph.addNode(id1);
			    			marketeerGraph.addNode(id2);
			    			marketeerGraph.addFriend(id1, id2);
			    			marketeerGraph.addFriend(id2, id1);	    				
		    			} else {
		    				System.out.println("Removing Friend :" + id1 + ", " + id2);
			    			marketeerGraph.removeFriend(id1, id2);
			    			marketeerGraph.removeFriend(id2, id1);	    					    				
		    			}
		    		}

	    		}
	    		
	    	} catch(Exception ex) {
  			  StringWriter sw = new StringWriter();
  			  PrintWriter pw = new PrintWriter(sw);
  			  ex.printStackTrace(pw);
  			  System.out.println(sw.toString());
	    		
	    	  } finally {
	    		  try {
	    			  bw.close();
	    		  } catch(IOException Iox) {
	    			  StringWriter sw = new StringWriter();
	    			  PrintWriter pw = new PrintWriter(sw);
	    			  Iox.printStackTrace(pw);
	    			  System.out.println(sw.toString());
	    		  }
			}
	    	
	    	}
    	
}
	

	
	



