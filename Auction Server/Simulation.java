package p1;


/**
 * Class provided for ease of test. This will not be used in the project 
 * evaluation, so feel free to modify it as you like.
 */ 
public class Simulation
{
    public static void main(String[] args)
    {                
        int nrSellers = 500;
        int nrBidders = 100;
        
        Thread[] sellerThreads = new Thread[nrSellers];
        Thread[] bidderThreads = new Thread[nrBidders];
        Seller[] sellers = new Seller[nrSellers];
        Bidder[] bidders = new Bidder[nrBidders];
        
        // Start the sellers
        for (int i=0; i<nrSellers; ++i)
        {
            sellers[i] = new Seller(
            		AuctionServer.getInstance(), 
            		"Seller"+i, 
            		100, 50, i
            );
            sellerThreads[i] = new Thread(sellers[i]);
            sellerThreads[i].start();
        }
        
        // Start the buyers
        for (int i=0; i<nrBidders; ++i)
        {
            bidders[i] = new Bidder(
            		AuctionServer.getInstance(), 
            		"Buyer"+i, 
            		1000, 20, 150, i
            );
            bidderThreads[i] = new Thread(bidders[i]);
            bidderThreads[i].start();
        }
        
        // Join on the sellers
        for (int i=0; i<nrSellers; ++i)
        {
            try
            {
                sellerThreads[i].join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        
        // Join on the bidders
        for (int i=0; i<nrBidders; ++i)
        {
            try
            {
                bidderThreads[i].join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        
        // TODO: Add code as needed to debug
        // revenue
        // sold items
        
        AuctionServer ac = AuctionServer.getInstance();
        System.out.println("Total items sold : "+ac.soldItemsCount());
        
        int rev = 0;
        
        for(Bidder bid : bidders){
//        	System.out.println(bid.cashSpent());
        	rev = rev + bid.cashSpent();
        }
        
        System.out.println("Revenue of all bidders : $"+rev);
        System.out.println("AuactionServer rev : $"+ac.revenue());
        ac.sumOfHighestBides();
//        System.out.println("Total items expired before bidding started = "+ac.itemsExpiredBeforeBiddingStated());
        ac.checkTotalItems();
        ac.compareItems();
        /*to check whether at any point items up for bidding exceeds max Server capacity*/
        System.out.print("if(List Size > Server Capacity) : ");
        System.out.print(AuctionServer.serverCapacity < ac.listSize());
        System.out.println();
        System.out.print("if(Max Bid by buyer > Max Bid Count) : ");
        System.out.print(ac.maxBidCount());
        System.out.println();
    }
}