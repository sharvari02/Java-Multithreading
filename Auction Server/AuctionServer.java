package p1;


/**
 *  @author Sharvari Phatak
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class AuctionServer
{
	/**
	 * Singleton: the following code makes the server a Singleton. You should
	 * not edit the code in the following noted section.
	 * 
	 * For test purposes, we made the constructor protected. 
	 */

	/* Singleton: Begin code that you SHOULD NOT CHANGE! */
	protected AuctionServer()
	{
	}

	private static AuctionServer instance = new AuctionServer();

	public static AuctionServer getInstance()
	{
		return instance;
	}

	/* Singleton: End code that you SHOULD NOT CHANGE! */

	/* Statistic variables and server constants: Begin code you should likely leave alone. */


	/**
	 * Server statistic variables and access methods:
	 */
	private int soldItemsCount = 0;
	private int revenue = 0;
	
	public int soldItemsCount()
	{
		return this.soldItemsCount;
	}

	public boolean maxBidCount()
	{
		for(Map.Entry<String, Integer> en : itemsPerBuyer.entrySet()){
//			System.out.println("#####################   MaxBid count for "+ en.getKey()+" is: "+en.getValue());
			if(en.getValue() > maxBidCount){
				
				return true;
			}
		}
		return false;
	}

	public void compareItems(){
		
		int count = 0;
		for(Map.Entry<String, Integer> en : itemsPerSeller.entrySet()){
//			System.out.println(en.getValue());
			count = count + en.getValue();
		}
		System.out.println("Count for items items Submitted by all the sellers = "+count);
		System.out.println("Items up for bidding "+itemsUpForBidding.size());
	}
	
	public int revenue()
	{
		return this.revenue;
	}

	public int listSize(){
		return this.itemsUpForBidding.size();
	}


	/**
	 * Server restriction constants:
	 */
	public static final int maxBidCount = 10; // The maximum number of bids at any given time for a buyer.
	public static final int maxSellerItems = 20; // The maximum number of items that a seller can submit at any given time.
	public static final int serverCapacity = 80; // The maximum number of active items at a given time.


	Object highstBidding = new Object(); // for highestBid list
	Object itemsBidding = new Object(); // for for itemsUpForBidding
	Object highestBidder = new Object(); // for highestBidders, itemsPerBuyer and itemSoldTo
	Object invalidation = new Object(); // for invalidInitialPriceCount and disqualifiedSeller
	Object items = new Object(); // for itemsAndIDs, itemsPerSeller and itemsExpiredBeforBidding
	
	/* Statistic variables and server constants: End code you should likely leave alone. */



	/**
	 * Some variables we think will be of potential use as you implement the server...
	 */

	// List of items currently up for bidding (will eventually remove things that have expired).
	private List<Item> itemsUpForBidding = new ArrayList<Item>();
	
	//List of disqualified sellers
	private List<String> disqualifiedSellers = new ArrayList<String>();


	// The last value used as a listing ID.  We'll assume the first thing added gets a listing ID of 0.
	private int lastListingID = -1; 

	// List of item IDs and actual items.  This is a running list with everything ever added to the auction.
	private HashMap<Integer, Item> itemsAndIDs = new HashMap<Integer, Item>();

	// List of itemIDs and the highest bid for each item.  This is a running list with everything ever added to the auction.
	private HashMap<Integer, Integer> highestBids = new HashMap<Integer, Integer>();

	// List of itemIDs and the person who made the highest bid for each item.   This is a running list with everything ever bid upon.
	private HashMap<Integer, String> highestBidders = new HashMap<Integer, String>(); 

	// List of sellers and how many items they have currently up for bidding.
	private HashMap<String, Integer> itemsPerSeller = new HashMap<String, Integer>();

	// List of buyers and how many items on which they are currently bidding.
	private HashMap<String, Integer> itemsPerBuyer = new HashMap<String, Integer>();

	// List of sellers and how many times they have submitted initial price > $75
	private HashMap<String, Integer> invalidInitialPriceCount = new HashMap<String, Integer>();
	
	// List of sellers and number of items that got expired before anybody can bid
	private List<Integer> itemsExpiredBeforeBidding = new ArrayList<Integer>();
	
	//List of item and buyer who bought that item with highest bid
	private HashMap<Integer, String> itemSoldTo = new HashMap<Integer, String>();
	
	
	public void checkTotalItems(){
		System.out.println("Total items up for bidding = "+itemsAndIDs.size());
		System.out.println("Last Listing ID : "+(lastListingID+1));
	}
	
	public void sumOfHighestBides(){
		
		int sum = 0;
		int ct = 0;
		int ct1 = 0;
		for(Map.Entry<Integer, Integer> entry : highestBids.entrySet())
		{
			ct++;
//			System.out.println("current item in highest bid = "+entry.getKey());
//			System.out.println("bidded amount = "+entry.getValue());
			sum = sum + entry.getValue();
			
		}
		for(Map.Entry<Integer, String> entry1 : highestBidders.entrySet())
		{
			ct1++;
//			System.out.println("current item in highest bidder table = "+entry1.getKey());
//			System.out.println("------------------------------------> Bidder Name : "+entry1.getValue());
		}
		
		System.out.println("\n---------------------------------------> count for highestBidders = "+ct1+"\n");
		System.out.println("---------------------------------------> count for highestBidds = "+ct);
		System.out.println("Sum of highest bids = $"+sum);
		
	}
	
	public int itemsExpiredBeforeBiddingStated(){
		int ct = 0;
		System.out.println("Length = "+itemsExpiredBeforeBidding.size());
		for(int i : itemsExpiredBeforeBidding){
			ct++;
			System.out.println("Item "+i+" expired before bidding started");
		}
		return ct;
	}
	
	
	// Object used for instance synchronization if you need to do it at some point 
	// since as a good practice we don't use synchronized (this) if we are doing internal
	// synchronization.
	//
	// private Object instanceLock = new Object(); 

	/*
	 *  The code from this point forward can and should be changed to correctly and safely 
	 *  implement the methods as needed to create a working multi-threaded server for the 
	 *  system.  If you need to add Object instances here to use for locking, place a comment
	 *  with them saying what they represent.  Note that if they just represent one structure
	 *  then you should probably be using that structure's intrinsic lock.
	 */


	/**
	 * Attempt to submit an <code>Item</code> to the auction
	 * @param sellerName Name of t][p--=][he <code>Seller</code>
	 * @param itemName Name of the <code>Item</code>
	 * @param lowestBiddingPrice Opening price
	 * @param biddingDurationMs Bidding duration in milliseconds
	 * @return A positive, unique listing ID if the <code>Item</code> listed successfully, otherwise -1
	 */
	//invariants: sellerName != null, -1< lowestBiddingPrice < 100, 
	//pre: the seller and item must be listed by AuctionServer
	// bidding duration should be greater than 0
	//post : returns uniqueId if submitted successfully
	
	public int submitItem(String sellerName, String itemName, int lowestBiddingPrice, int biddingDurationMs)
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   Make sure there's room in the auction site.
		//   If the seller is a new one, add them to the list of sellers.
		//   If the seller has too many items up for bidding, don't let them add this one.
		//   Don't forget to increment the number of things the seller has currently listed.

		/* 
		 * Acquire Lock(itemsBidding)
		 * 	if(SN and IN already present in itemsUpForBidding) return -1; can't submit same item twice
		 * 	else if(SN in disqualifiedSellers list) return -1; disqualified seller
		 * 	else if(upForBidding list size => serverMaxCap) return -1; max capacity reached
		 * 	else
		 * 	 Acquire Lock(items) 
		 * 		if(SN in itemsPerSeller)
		 * 		get number of items that seller bidding currently say i
		 * 		if(i >= sellerMax) return -1; seller reached max items count
		 * 		else get i 
		 * 		if(lowestBiddingPrice > 75)
		 * 		  Acquire Lock(invalidation)
		 * 			check if(SN present in invalidInitialPriceCount list)
		 * 					get count
		 * 					if(count > 3) return -1
		 * 					else count++
		 * 					if count == 3 
		 * 					add that seller to disqualiiedSellerList and continue
		 * 					else increment the count in invalidInitialPriceCount for that seller
		 * 		 Release Lock(invalidation)
		 * 		if(i > 0)
		 * 		increment count in itemsPerSeller by sellerName
		 * 		else add new entry in itemsPerSeller
		 *  
		 * 		create new item, add entry to itemsUpForBidding and ItemsAndIDs
		 * 		return lastListingID++;
		 * 	 Release Lock(items)
		 * Release Lock(itemsBidding) 
		 */
	
	
	if(!sellerName.isEmpty() && !itemName.isEmpty() && lowestBiddingPrice >= 0 && biddingDurationMs > 0){
		synchronized (itemsBidding) {
		//System.out.println("----------------------> 1");
			int count = 0; // count of items submitted by seller
			int invalidCount = 0; // count of initial Price submitted more than 75 by this seller 
			boolean flag = false;
			
			for(Item i : itemsUpForBidding){
				if(i.name().equalsIgnoreCase(itemName) && i.seller().equalsIgnoreCase(sellerName)){
				System.out.println("This item is already submitted by "+sellerName);
				return -1;
				}
			}
			
				for(String s : disqualifiedSellers){
					System.out.println("----------------------> 3");
					if(s.equalsIgnoreCase(sellerName)){
						System.out.println("The seller "+sellerName+ " is disqualified for submitting initial price more than 75 for more than 3 times");
						return -1;
					}
				}
				if(lowestBiddingPrice > 99){
					System.out.println("Bidding price must be less than or equal to $99");
					return -1;
				}	
			
				if(serverCapacity > itemsUpForBidding.size()){
					//System.out.println("----------------------> 4");
				 synchronized (items) {	
					// System.out.println("----------------------> 5");
						for(Map.Entry<String, Integer> entry : itemsPerSeller.entrySet())
						{
							if(entry.getKey().equals(sellerName)){
								count = entry.getValue();
								
								if(count >= maxSellerItems){
									System.out.println("Max capacity to submit items by seller "+sellerName+" has reached");
									return -1;
								}
								break;
							}
						}
						if(lowestBiddingPrice > 75){
							synchronized (invalidation) {
							
							for(Map.Entry<String, Integer> en : invalidInitialPriceCount.entrySet())
							{
								if(en.getKey().equals(sellerName)){
									invalidCount = en.getValue();
									if(invalidCount > 3){ //this condition must not reach as disqualifiedSellerList is checked already; but writing to prevent code from breaking at any point 
										System.out.println("Submitted bidding price greather than 75 for more than 3 times");
										return -1;
									}
									en.setValue(invalidCount++);
									flag = true;
									if(invalidCount == 3)
										disqualifiedSellers.add(sellerName);
									break;
								}
							}
							if(!flag){
								invalidInitialPriceCount.put(sellerName, 1);
								}
						}
							
						}
						lastListingID++;
//						int  listingID = 0;
//						if(lastListingID < 80){
						int listingID = lastListingID;
//						}
//						else{
//							int i = 0;
//							while(i < 80){
//							for(Item it : itemsUpForBidding){
//								if(! (i == it.listingID())){
//									listingID = i;
//									break;
//								}
//								else i++;
//							}
//							break;
//							}
//						}
			
						//System.out.println("Listing ID = "+listingID);
			
						Item i = new Item(sellerName, itemName, listingID, lowestBiddingPrice, biddingDurationMs);
						itemsUpForBidding.add(i);
						itemsAndIDs.put(listingID, i);
					
		
						if(count > 0){ // seller has submitted at least one item.
							for(Map.Entry<String, Integer> entry : itemsPerSeller.entrySet())
							{
								if(entry.getKey().equals(sellerName)){
									entry.setValue(count + 1);
									break;
								}
							}
						}
						else{
							itemsPerSeller.put(sellerName, 1);
						}
			
						//System.out.println("Item submitted successfully");
			
						return listingID;
					}
			}
		}
	}
	//System.out.println("sellerName = "+sellerName+ " item = "+itemName+ "bidding amt = "+lowestBiddingPrice+" duration = "+biddingDurationMs);
	//System.out.println("Seller name and item name should not be null AND lowest bidding price and bidding duration must be a positive integer");
		return -1;
	}



	/**
	 * Get all <code>Items</code> active in the auction
	 * @return A copy of the <code>List</code> of <code>Items</code>
	 */
	//pre: none
	//post: returns List of items
	
	
	public List<Item> getItems()
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//    Don't forget that whatever you return is now outside of your control.
		
		/*
		 * create a new list named copyItemList
		 * Acquire Lock(itemsBidding) 
		 * 	for each item in upForBidding list copy them to new list
		 * 	if copyItemList is empty return blank list
		 * Release Lock(itemsBidding)
		 * return copyItemList 
		 */
		List<Item> copyItemList = new ArrayList<>();
		
		synchronized (itemsBidding) {
		
		for(Item i : itemsUpForBidding){
		copyItemList.add(i);
		}
		if(copyItemList.isEmpty())
			System.out.println("No item up for bidding");
		
		}
		//System.out.println("List returnrd successfully");
		return copyItemList;
	}


	/**
	 * Attempt to submit a bid for an <code>Item</code>
	 * @return True if successfully bid, false otherwise
	 */
	
	// invariant: bidderName != null, listingID > 0, biddingAmount > initialBiddingprice
	//pre : none
	// post : return true if bid gets submitted successfully else false
	public boolean submitBid(String bidderName, int listingID, int biddingAmount)
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   See if the item exists.
		//   See if it can be bid upon.
		//   See if this bidder has too many items in their bidding list.
		//   Get current bidding info.
		//   See if they already hold the highest bid.
		//   See if the new bid isn't better than the existing/opening bid floor.
		//   Decrement the former winning bidder's count
		//   Put your bid in place
		
		/* 
		 * Acquire Lock(itemsBidding)
		 * 	check if (listingID is present in itemsUpForBids)
		 * 		if(lowestBiddingPrice > biddingAmount) return false; bidding amount should be more than initial bidding price
		 * 		if(biddingOpen)
		 * 		Acquire Lock(HighestBidding)
		 * 			if(item is present in highestBids)
		 * 				if(biddingAmount > currentHighestBid)
		 * 					Acquire Lock(highestBidder)
		 * 						if(item is present in HighestBidders)
		 * 							if(the person who bid on this item is BN) return false;//can't bid again unless someone else bids 
		 * 							else
		 * 								update entries in HighestBidder, highestBids list and itemPerBuyer List
		 * 								return true
		 * 					Release Lock(highestBidder)
		 * 			else
		 * 				Acquire Lock(highestBidder)
		 * 					update entry in itemsPerBuyer
		 * 					add new entry in highestBidders
		 * 				Release Lock(highestBidder)
		 * 				add new entry n highestBids
		 * 				return true
		 * 		Release Lock(highestBidding)
		 * 		else
		 * 			return false
		 * 	else
		 * 		return false
		 * release Lock(itemsBidding)
		 * 
		 * */
				//int max = 0;
		 if(bidderName != null && listingID > 0 && biddingAmount > 0){
			 
			 synchronized (itemsBidding) {
				 //System.out.println("Submitted Successfully 3");
				 boolean f1 = false; // to check if item is present in highestBids
				 String name = null;
	
				 for(Item i : itemsUpForBidding){
					 if(i.listingID() == listingID){
						 if(i.lowestBiddingPrice() <= biddingAmount){
							 //System.out.println("Submitted Successfully 3");
							 if(i.biddingOpen()){
								 //System.out.println("Submitted Successfully 4");
								 synchronized (highstBidding) {
//									 System.out.println("Submitted Successfully 3");
									 
									 for(Map.Entry<Integer, Integer> ent : highestBids.entrySet()){
										 if(ent.getKey() == listingID){
											 if(biddingAmount > ent.getValue()){
												 synchronized (highestBidder) {
													for(Map.Entry<Integer, String> en : highestBidders.entrySet()){
														 if(en.getKey() == listingID){
															 name = en.getValue();
															 if(name.equalsIgnoreCase(bidderName)){
																System.out.println(bidderName+" You have already bidded the highest amount for this item. Wait for someone else to bid the highest amount.");
																return false;
															 }
															 else{
																 
															 
																f1 = true;
																
																for(Map.Entry<String,Integer> entry : itemsPerBuyer.entrySet()){
																	if(entry.getKey().equalsIgnoreCase(bidderName)){
																		if(entry.getValue() >= maxBidCount){
//																			System.out.println("*******************************    BIDEER "+bidderName+" has count"+entry.getValue());
																			return false;
																		}
																		else{
//																			System.out.println("-------------------- WON Initial = "+entry.getValue()+" , New = "+(entry.getValue()+1));
																			entry.setValue(entry.getValue() + 1);
																		}
																	}
																	if(entry.getKey().equalsIgnoreCase(name)){
//																		System.out.println("-------------------- PRE Initial = "+entry.getValue()+" , New = "+(entry.getValue()-1));
																		entry.setValue(entry.getValue() - 1);
																		
																		if(entry.getValue() <1)
																			itemsPerBuyer.remove(entry);
																	}
																	
																}

																highestBidders.replace(listingID, bidderName);
																highestBids.replace(listingID, biddingAmount);
																//System.out.println("Submitted Successfully 1");
																return true;
															 }
														 
														 }
															
													}
												 }
											 }
										 }
									 }
									 
									 if(!f1){//no records in higestBids table
										 boolean f = false; //to check if buyer is present in itemsPerBuyer
										 synchronized (highestBidders) {
										
											 for(Map.Entry<String,Integer> entry : itemsPerBuyer.entrySet()){
													if(entry.getKey().equalsIgnoreCase(bidderName)){
														f = true;
//														System.out.println("Buyer "+entry.getKey()+" has maxBid count"+entry.getValue());
															if(entry.getValue() >= maxBidCount){
//																System.out.println("Buyer"+entry.getKey()+" restricted");
																return false;
															}
													}
//													System.out.println("-------------------- New to max bid Initial = "+entry.getValue()+" , New = "+(entry.getValue()+1));
//														entry.setValue(entry.getValue() + 1);
												}
											 if(!f){
												 itemsPerBuyer.put(bidderName, 1);
											 }
											 highestBidders.put(listingID, bidderName);
											
										}
										 highestBids.put(listingID, biddingAmount);
										 return true;
									 }
								 }
							 }
							 else{
								 //System.out.println("Item no longer up for bidding");
								 return false;
							 }
						 }
						 else{
							 System.out.println("Bidding price should be higher than initial bidding amount");
							 return false;
						 }
					 }
//					 else{
//						 //System.out.println("Listing ID not present");
//						 return false;
//					 }
				 }
			 }
				
			}
		return false;
	}

	/**
	 * Check the status of a <code>Bidder</code>'s bid on an <code>Item</code>
	 * @param bidderName Name of <code>Bidder</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return 1 (success) if bid is over and this <code>Bidder</code> has won<br>
	 * 2 (open) if this <code>Item</code> is still up for auction<br>
	 * 3 (failed) If this <code>Bidder</code> did not win or the <code>Item</code> does not exist
	 */
	
	//invariant: listingID > 0 and unique AND bidderName != null
	//pre: none
	//post : return 1 (success) if bid is over and this Bidder has won
	 //		 return 2 (open) if this Item is still up for auction
	 // 	 return 3 (failed) If this Bidder did not win or the Item does not exist
	// thread-safe : item up for bidding
	public int checkBidStatus(String bidderName, int listingID)
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   If the bidding is closed, clean up for that item.
		//     Remove item from the list of things up for bidding.
		//     Decrease the count of items being bid on by the winning bidder if there was any...
		//     Update the number of open bids for this seller
		
		/*
		 * Acquire Lock(itemBidding)
		 * 	if(ID in itemsUpForBidding)
		 * 		if(biddingOpen)
		 * 			return 2
		 * 		else 
		 * 			remove item from itemsUpForBidding
		 * 			Acquire Lock(items)
		 * 				update itemsPerSeller
		 * 			Release Lock(items)
		 * 			Acquire Lock(highestBidder)
		 * 				update itemPer itemsPerBuyer
		 * 				if(ID in highestBidders)
		 * 					if(BidderName == highestBiider)
		 * 						add to itemSoldTo list
		 * 						increment soldItemCount
		 * 						calculate revenue
		 * 						return 1
		 * 					else
		 * 						Remove entry from HighestBidders and HighestBids 
		 * 			Release Lock(highestBidders)
		 * 		return 3
		 * Release Lock(itemBidding)
		 * 
		 * */
		
		if(bidderName != null && listingID > 0 ){
			synchronized (itemsBidding) {
				boolean f = false;
				boolean f1 = false;
				boolean f2 = false;
				String seller;
				int price = 0;
				for(Item i : itemsUpForBidding){
					
					if(i.listingID() == listingID){
						f = true;
						
						if(i.biddingOpen()){
							return 2; // Open
						}
						else{
							seller = i.seller();
							
							//System.out.println(itemsUpForBidding);
							//System.out.println("removing "+listingID);
							itemsUpForBidding.remove(i); //Removing from itemsUpForBidding
								
							synchronized (items) {
//							System.out.println("chk bid status");
								for(Map.Entry<String,Integer> entry : itemsPerSeller.entrySet()){
									if(entry.getKey().equalsIgnoreCase(seller)){
										int val = entry.getValue();
										if(val>=1)
											entry.setValue(val - 1);
										else
											itemsPerSeller.remove(entry);
									}
//								itemsPerSeller.replace(i.seller(), itemsPerSeller.get(i.seller())-1);
								}
							}
							synchronized (highestBidder) {
//								System.out.println("inside lock");
								for(Map.Entry<String,Integer> en : itemsPerBuyer.entrySet()){
									if(en.getKey().equalsIgnoreCase(bidderName)){
										int val = en.getValue();
//										f1 = true;
										if(val>1)
											en.setValue(val - 1);
										else
											itemsPerBuyer.remove(en);
										break;
									}
								}
								for(Map.Entry<Integer, String> s : highestBidders.entrySet()){
									if(s.getKey() == listingID)
										f1 = true;
									if(s.getValue().equalsIgnoreCase(bidderName) && s.getKey() == listingID){
										itemSoldTo.put(listingID, bidderName);
//										System.out.println("bidding closed for "+listingID+" ,Item sold to +"+bidderName);
										soldItemsCount = soldItemsCount + 1;
										if(highestBids.containsKey(listingID))
											price = highestBids.get(listingID);
										//System.out.println("price = "+price);
										revenue = revenue + price;
										f2 = true;
										//System.out.println("item sold to list count"+itemSoldTo.size()+" with price "+price);
										
										return 1; // Success
									}
								}
								if(f1 && !f2){
									highestBids.remove(listingID);
									highestBidders.remove(listingID);
								}
							}
							if(!f1){ // no-one bid for that item before it got expired
								synchronized (items) {
									itemsExpiredBeforeBidding.add(listingID);
								}
							}
							//System.out.println("timeout for item "+i.name());
							return 3;
							
						}
					}
				}
				
				if(!f){
					//System.out.println("item not listed");
					return 3;
				}
			
			}
		}
		return -1;
	}

	/**
	 * Check the current bid for an <code>Item</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return The highest bid so far or the opening price if no bid has been made,
	 * -1 if no <code>Item</code> exists
	 */
	
	//invariant : listingID > 0
	//pre : none
	//post : returns highestBids if listingID is present in highestBidders else biddingAmount
	

	
	public int itemPrice(int listingID)
	{
		// TODO: IMPLEMENT CODE HERE
		/* pseudo code: 
		 * 	Acquire Lock
		 * 		if (listingID is in ItemsUpForBidding)
		 * 			Acquire Lock(highestBidding)
		 * 				if (listingID is in highestBids)
		 * 					return highestBidfingAmount
		 * 			Release Lock(highestBidding)
		 * 					return initialBiddingAmount //if not in highestBids
		 * Release Lock	
		 * 	
		 * */
		if(listingID >= 0){
			synchronized(itemsBidding){
				for(Item i : itemsUpForBidding){
					if(i.listingID() == listingID){
						synchronized (highstBidding) {
							
						for(Map.Entry<Integer, Integer> en : highestBids.entrySet()){
							if(en.getKey() == listingID){
								//System.out.println("Highest bid retrived");
								return en.getValue();
							}
						}
						
						}
						
						return i.lowestBiddingPrice();
					}
				}
			}
		}
		return -1;
	}

	/**
	 * Check whether an <code>Item</code> has been bid upon yet
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return True if there is no bid or the <code>Item</code> does not exist, false otherwise
	 */
	//invariant: listingID > 0
	//pre: none
	//post : returns true if no-one has bid for this item or listingID is not there in ItemsUpForBidding else false
	public Boolean itemUnbid(int listingID)
	{
		// TODO: IMPLEMENT CODE HERE
		/* pseudo code: 
		 * Acquire Lock(itemsBidding)
		 * 	if( listingID is in ItemsUpForBidding)
		 * 		Acquire Lock(highestBidding){
		 * 			if(highestBids contain listingID)(i.e. if someone has bid for this item or not)
		 * 				return false;  no more unbid item
		 * 			else return true
		 * 		Release Lock(highestBidding)
		 * 	else return true
		 * Release Lock(itemsBidding)
		 *   
		 * */
		
		if(listingID >= 0){
			synchronized (itemsBidding) {
				boolean f = false;
				for( Item i : itemsUpForBidding){
					if(i.listingID() == listingID)
					{
						f = true;
						synchronized (highstBidding) {
							for(Map.Entry<Integer,Integer> en : highestBids.entrySet()){
								if(en.getKey() == listingID)
									return false;
							}
						}
						return true;
					}
				}
				if(!f)
					return true; // since it is true that the non-existing Item has not yet been successfully bid upon.
			}
		}
		return true;
	}

}