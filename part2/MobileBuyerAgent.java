package part2;

import java.util.HashMap;
import java.util.Map;

import jade.core.AID;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.PlatformID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class MobileBuyerAgent extends Agent {

    private Map<String, Double> weights;
    private double bestScore = Double.NEGATIVE_INFINITY;
    private String bestSeller = null;
    private String[] sellers = {"Seller1", "Seller2", "Seller3"};

    @Override
    protected void setup() {
        // Initialize preference weights
        weights = new HashMap<>();
        weights.put("Price", 0.4);
        weights.put("Quality", 0.5);
        weights.put("DeliveryCost", 0.1);

        System.out.println("MobileBuyerAgent " + getLocalName() + " started.");

        // Send CFP to all sellers
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        for (String seller : sellers) {
            cfp.addReceiver(new AID(seller, AID.ISLOCALNAME));
        }
        cfp.setContent("Request proposal for product X");
        send(cfp);

        // Add behaviours
        addBehaviour(new HandleProposalsBehaviour());
        addBehaviour(new EndAuctionBehaviour());

        // Schedule migration after 10 seconds
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            String target = (String) args[0];
            addBehaviour(new WakerBehaviour(this, 10000) {
                @Override
                protected void onWake() {
                    addBehaviour(new MigrateBehaviour(target));
                }
            });
        }
    }

    private class MigrateBehaviour extends OneShotBehaviour {

        private String target;

        public MigrateBehaviour(String target) {
            this.target = target;
        }

        @Override
        public void action() {
            try {
                if ("SubContainer".equals(target)) {
                    doMove(new ContainerID(target, null));
                    log("Moved to container: " + target);
                } else if ("AnotherPlatform".equals(target)) {
                    AID remoteAMS = new AID("ams@192.168.56.1:1098/JADE", AID.ISGUID);
                    remoteAMS.addAddresses("http://SWIFT.mshome.net:7778/acc");
                    doMove(new PlatformID(remoteAMS));
                    log("Moved to platform: " + target);
                }
            } catch (Exception e) {
                log("Migration failed: " + e.getMessage());
            }
        }
    }

    private class HandleProposalsBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
            ACLMessage msg = receive(mt);
            if (msg != null) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Double> proposal = (Map<String, Double>) msg.getContentObject();
                    double score = evaluateProposal(proposal);
                    String sender = msg.getSender().getLocalName();
                    log("Received proposal from " + sender + " with score: " + score);
                    if (score > bestScore) {
                        bestScore = score;
                        bestSeller = sender;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                block();
            }
        }

        private double evaluateProposal(Map<String, Double> proposal) {
            double normPrice = normalize(proposal.get("Price"), 50, 150);
            double normQuality = normalize(proposal.get("Quality"), 1, 10);
            double normDelivery = normalize(proposal.get("DeliveryCost"), 5, 20);
            return weights.get("Price") * (1 - normPrice)
                    + weights.get("Quality") * normQuality
                    + weights.get("DeliveryCost") * (1 - normDelivery);
        }

        private double normalize(double value, double min, double max) {
            return (value - min) / (max - min);
        }
    }

    private class EndAuctionBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = receive(mt);
            if (msg != null && "Auction ended".equals(msg.getContent())) {
                if (bestSeller != null) {
                    log("Winner: " + bestSeller + " with score: " + bestScore);
                    AuctionInterface.getInstance().displayWinner(bestSeller, bestScore);
                } else {
                    log("Auction ended with no proposals.");
                }
                myAgent.doDelete();
            } else {
                block();
            }
        }
    }

    private void log(String msg) {
        System.out.println(msg);
        AuctionInterface.getInstance().log(msg);
    }
}

