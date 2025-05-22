package part2;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.Serializable;
import java.util.HashMap;

import java.util.Map;

public class SellerAgent extends Agent {

    private Map<String, Double> criteria;
    private String[] buyerNames = {"MobileBuyer"};

    @Override
    protected void setup() {
        // Random criteria if none passed
        criteria = new HashMap<>();
        criteria.put("Price", Math.random() * 100 + 50);
        criteria.put("Quality", Math.random() * 5 + 5);
        criteria.put("DeliveryCost", Math.random() * 20 + 5);
        System.out.println(getLocalName() + " criteria: " + criteria);

        // Behaviours
        addBehaviour(new CFPListener());
        addBehaviour(new WakerBehaviour(this, 10000) {
            @Override
            protected void onWake() {
                endAuction();
            }
        });
    }

    private class CFPListener extends Behaviour {

        private boolean finished = false;

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage cfp = receive(mt);
            if (cfp != null) {
                // Send proposal
                ACLMessage reply = cfp.createReply();
                reply.setPerformative(ACLMessage.PROPOSE);
                try {
                    reply.setContentObject((Serializable) criteria);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                send(reply);
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

    private void endAuction() {
        System.out.println(getLocalName() + ": Auction ended.");
        for (String buyer : buyerNames) {
            ACLMessage end = new ACLMessage(ACLMessage.INFORM);
            end.addReceiver(new AID(buyer, AID.ISLOCALNAME));
            end.setContent("Auction ended");
            send(end);
        }
        doDelete();
    }
}
