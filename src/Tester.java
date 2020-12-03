import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static jade.core.AID.ISLOCALNAME;

public class Tester extends Agent {

    public static final String NAME = "tester";
    public static final AID AID = new AID(NAME, ISLOCALNAME);

    public static final String TASK_DONE = "TASK_DONE";
    public static final String HAS_ERRORS = "HAS_ERRORS";
    public static final String REQUEST_INFO = "REQUEST_INFO";

    @Override
    protected void setup() {
        System.out.println("Agent " + getAID().getName() + " is ready");
        addBehaviour(new TesterBehaviour());
    }

    @Override
    protected void takeDown() {
        System.out.println("Tester " + getAID().getName() + " terminating");
    }

    private static class TesterBehaviour extends Behaviour {
        private boolean done = false;

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                System.out.printf("%s received in %s%n", msg.getContent(), myAgent.getLocalName());
                try {
                    checkMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public boolean done() {
            return done;
        }

        private void checkMessage(ACLMessage msg) throws IOException {
            String message = msg.getContent();
            AID sender = msg.getSender();
            String localName = sender.getLocalName();
            if (localName.equals(Developer.NAME)) {
                if (message.equals(Developer.TASK_INFO)) {
                    ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                    reply.setLanguage("Russian");
                    if (taskIsDoneCorrectly()) {
                        reply.addReceiver(Storage.AID);
                        reply.setContent(TASK_DONE);
                        ACLMessage replyDeveloper = new ACLMessage(ACLMessage.INFORM);
                        replyDeveloper.setLanguage("Russian");
                        replyDeveloper.addReceiver(Developer.AID);
                        replyDeveloper.setContent(TASK_DONE);
                        myAgent.send(replyDeveloper);
                        done = true;
                    } else {
                        reply.addReceiver(Developer.AID);
                        reply.setContent(HAS_ERRORS);
                    }
                    myAgent.send(reply);
                } else if (message.equals(Developer.START_TESTING)) {
                    ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                    reply.setLanguage("Russian");
                    reply.addReceiver(Developer.AID);
                    reply.setContent(REQUEST_INFO);
                    myAgent.send(reply);
                } else {
                    System.out.println("Unknown message");
                }
            } else {
                System.out.println("Wrong sender");
            }
        }

        private boolean taskIsDoneCorrectly() throws IOException {
            System.out.println("Is task ok? true/false");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            return reader.readLine().equals("true");
        }
    }
}
