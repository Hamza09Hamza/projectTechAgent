package part1;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Random;

public class BuyerAgent extends Agent {

    private double maxBid;
    private double currentBid;
    private Random random;
    private boolean auctionOver = false;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length == 1) {
            maxBid = Double.parseDouble((String) args[0]);
        } else {
            maxBid = 200; 
        }
        random = new Random();
        System.out.println(getLocalName() + " started with max bid: " + maxBid);

        addBehaviour(new BiddingBehaviour());
    }

    private class BiddingBehaviour extends Behaviour {

        private boolean finished = false;

        @Override
        public void action() {
            // Listen for either new highest bid or auction-over
            MessageTemplate mt = MessageTemplate.or(
                    MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                    MessageTemplate.MatchPerformative(ACLMessage.CANCEL)
            );
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                String content = msg.getContent();
                if (msg.getPerformative() == ACLMessage.CANCEL && "AUCTION_OVER".equals(content)) {
                    // Auction ended
                    System.out.println(getLocalName() + " received AUCTION_OVER, finishing.");
                    finished = true;
                    return;
                }

                // New highest bid announcement
                System.out.println(getLocalName() + " received: " + content);
                try {
                    double highestBid = Double.parseDouble(content.split(": ")[1]);
                    if (highestBid < maxBid && random.nextInt(100) < 80) {
                        double bidIncrement = 1 + random.nextInt(10);
                        double bid = Math.min(maxBid, highestBid + bidIncrement);

                        ACLMessage propose = new ACLMessage(ACLMessage.PROPOSE);
                        propose.addReceiver(new AID("SellerAgent", AID.ISLOCALNAME));
                        propose.setContent(String.valueOf(bid));
                        myAgent.send(propose);

                        currentBid = bid;
                        System.out.println(getLocalName() + " placed bid: " + bid);
                    } else {
                        System.out.println(getLocalName() + " decided not to bid this round.");
                    }
                } catch (Exception e) {
                    System.err.println(getLocalName() + " failed to parse bid info: " + content);
                }
            } else {
                block();
            }
        }

        @Override
        public boolean done() {
            return finished;
        }
    }
}
