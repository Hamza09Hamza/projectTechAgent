package part1;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.List;

public class SellerAgent extends Agent {

    private double startingPrice;
    private double reservePrice;
    private double currentBid;
    private String currentBidder;
    private boolean auctionStarted = false;
    private List<String> buyers = new ArrayList<>();
    private AuctionBehaviour auctionBehaviour;

    @Override
    protected void setup() {
        // Parse arguments or use defaults
        Object[] args = getArguments();
        if (args != null && args.length == 2) {
            startingPrice = Double.parseDouble((String) args[0]);
            reservePrice = Double.parseDouble((String) args[1]);
        } else {
            startingPrice = 100;
            reservePrice = 130;
        }
        currentBid = startingPrice;
        System.out.println("SellerAgent started. Starting price: " + startingPrice);

        // Send self-message to kick off auction immediately
        ACLMessage kick = new ACLMessage(ACLMessage.INFORM);
        kick.addReceiver(getAID());
        kick.setConversationId("auction-start");
        kick.setContent("Start the auction");
        send(kick);

        // Behaviours
        addBehaviour(new StartAuctionBehaviour());
        auctionBehaviour = new AuctionBehaviour();
        addBehaviour(auctionBehaviour);

        // End auction after 60 seconds
        addBehaviour(new WakerBehaviour(this, 60000) {
            @Override
            protected void onWake() {
                endAuction();
            }
        });
    }

    private class StartAuctionBehaviour extends Behaviour {

        private boolean finished = false;

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.and(
                    MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                    MessageTemplate.MatchConversationId("auction-start")
            );
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                // Initialize buyers list (or DF lookup in a more flexible version)
                buyers.clear();
                for (int i = 1; i <= 3; i++) {
                    buyers.add("BuyerAgent" + i);
                }
                auctionStarted = true;
                System.out.println("Auction started. Buyers: " + buyers);
                notifyAllBuyers();
                finished = true;
            } else {
                block();
            }
        }

        @Override
        public boolean done() {
            return finished;
        }
    }

    private class AuctionBehaviour extends Behaviour {

        private boolean finished = false;

        @Override
        public void action() {
            if (!auctionStarted) {
                block();
                return;
            }

            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                String content = msg.getContent();
                String bidder = msg.getSender().getLocalName();
                try {
                    double bid = Double.parseDouble(content);
                    System.out.println("Received bid: " + bid + " from " + bidder);

                    if (bid > currentBid) {
                        currentBid = bid;
                        currentBidder = bidder;
                        notifyAllBuyers();
                    } else {
                        System.out.println("Bid rejected (lower than current bid).");
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid bid format from " + bidder + ": " + content);
                }
            } else {
                block();
            }
        }

        @Override
        public boolean done() {
            return finished;
        }

        public void finish() {
            this.finished = true;
        }
    }

    private void notifyAllBuyers() {
        for (String buyer : buyers) {
            ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
            inform.addReceiver(new AID(buyer, AID.ISLOCALNAME));
            inform.setContent("Current highest bid: " + currentBid);
            send(inform);
        }
        System.out.println("Notified buyers of new highest bid: " + currentBid);
    }

    private void endAuction() {
        System.out.println("Ending auction...");
        // Inform buyers that auction is over
        for (String buyer : buyers) {
            ACLMessage endMsg = new ACLMessage(ACLMessage.CANCEL);
            endMsg.addReceiver(new AID(buyer, AID.ISLOCALNAME));
            endMsg.setContent("AUCTION_OVER");
            send(endMsg);
        }

        if (currentBid >= reservePrice && currentBidder != null) {
            System.out.println("Auction ended. Winner: " + currentBidder + " with bid: " + currentBid);
        } else {
            System.out.println("Auction ended. No winner.");
        }

        // Stop the auction behaviour
        auctionBehaviour.finish();
        doDelete();
    }
}
