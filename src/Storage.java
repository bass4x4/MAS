import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import static jade.core.AID.ISLOCALNAME;

public class Storage extends Agent {

    public static final String NAME = "storage";
    public static final AID AID = new AID(NAME, ISLOCALNAME);

    public static final String NEW_TASK = "NEW_TASK";
    public static final String SEND_DATES = "SEND_DATES";

    @Override
    protected void setup() {
        System.out.println("Agent " + getAID().getName() + " is ready");
        addBehaviour(new StorageBehaviour());
    }

    @Override
    protected void takeDown() {
        System.out.println("Storage " + getAID().getName() + " terminating");
    }

    private static class StorageBehaviour extends Behaviour {
        private boolean done = false;
        private boolean started = false;

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                System.out.printf("%s received in %s%n", msg.getContent(), myAgent.getLocalName());
                checkMessage(msg);
            } else if (!started) {
                ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                reply.setLanguage("Russian");
                reply.addReceiver(Developer.AID);
                reply.setContent(NEW_TASK);
                myAgent.send(reply);
                started = true;
            }
        }

        private void checkMessage(ACLMessage msg) {
            String message = msg.getContent();
            AID sender = msg.getSender();
            String localName = sender.getLocalName();
            if (localName.equals(Developer.NAME)) {
                if (message.equals(Developer.REQUEST_DATES)) {
                    ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                    reply.setLanguage("Russian");
                    reply.addReceiver(Developer.AID);
                    reply.setContent(SEND_DATES);
                    myAgent.send(reply);
                } else if (message.equals(Developer.EXIT)) {
                    done = true;
                }
            } else {
                done = true;
            }
        }

        @Override
        public boolean done() {
            return done;
        }
    }
}
