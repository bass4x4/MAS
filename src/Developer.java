import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static jade.core.AID.ISLOCALNAME;

public class Developer extends Agent {

    public static final String NAME = "developer";
    public static final AID AID = new AID(NAME, ISLOCALNAME);

    public static final String START_TESTING = "START_TESTING";
    public static final String TASK_INFO = "TASK_INFO";
    public static final String EXIT = "EXIT";
    public static final String REQUEST_DATES = "REQUEST_DATES";

    @Override
    protected void setup() {
        System.out.println("Agent " + getAID().getName() + " is ready");
        addBehaviour(new DeveloperBehaviour());
    }

    @Override
    protected void takeDown() {
        System.out.println("Developer " + getAID().getName() + " terminating");
    }

    private static class DeveloperBehaviour extends Behaviour {
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
            if (localName.equals(Storage.NAME)) {
                if (message.equals(Storage.NEW_TASK)) {
                    ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                    reply.setLanguage("Russian");
                    reply.addReceiver(Storage.AID);
                    reply.setContent(REQUEST_DATES);
                    myAgent.send(reply);
                } else if (message.equals(Storage.SEND_DATES)) {
                    ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                    reply.setLanguage("Russian");
                    if (canExecuteTask()) {
                        reply.addReceiver(Tester.AID);
                        reply.setContent(START_TESTING);
                    } else {
                        reply.addReceiver(Storage.AID);
                        reply.setContent(EXIT);
                    }
                    myAgent.send(reply);
                } else {
                    System.out.println("Unknown message");
                }
            } else if (localName.equals(Tester.NAME)) {
                if (message.equals(Tester.REQUEST_INFO)) {
                    ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                    reply.setLanguage("Russian");
                    reply.addReceiver(Tester.AID);
                    reply.setContent(TASK_INFO);
                    myAgent.send(reply);
                } else if (message.equals(Tester.HAS_ERRORS)) {
                    ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                    reply.setLanguage("Russian");
                    reply.addReceiver(Tester.AID);
                    reply.setContent(START_TESTING);
                    myAgent.send(reply);
                } else if (message.equals(Tester.TASK_DONE)) {
                    done = true;
                } else {
                    System.out.println("Unknown message");
                }
            } else {
                System.out.println("Wrong sender");
            }
        }

        private boolean canExecuteTask() throws IOException {
            System.out.println("Can developer execute the task? true/false");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            return reader.readLine().equals("true");
        }
    }
}
