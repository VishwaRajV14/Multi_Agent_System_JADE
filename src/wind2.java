import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class wind2 extends Agent
{
    int[] wind1_KW={250};
    int[] wind1_price={7};
    int i=0;

    @Override
    protected void setup()
    {
        addBehaviour(new Power_RequestReceiver());
    }


    private class Power_RequestReceiver extends CyclicBehaviour
    {
        @Override
        public void action()
        {
            // MessageTemplate mt= MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = receive();

            if(msg!=null) {
                if (msg.getPerformative() == ACLMessage.CFP) {
                    System.out.println(msg.getSender().getLocalName() + " approaching " + getAID().getLocalName());
                    String temp = Integer.toString(wind1_KW[i]);
                    String temp1 = Integer.toString(wind1_price[i]);
                    temp = temp.concat(" " + temp1);
                    ACLMessage reply = new ACLMessage(ACLMessage.PROPOSE);
                    reply.setContent(temp);
                    reply.addReceiver(new AID(msg.getSender().getLocalName(), AID.ISLOCALNAME));
                    send(reply);
                }
                else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL)
                {
                    if (msg.getSender().getLocalName().equals("load1"))
                        wind1_KW[i] = wind1_KW[i] - Integer.parseInt(msg.getContent());
                    else if (msg.getSender().getLocalName().equals("load2"))
                    {
                        i++;
                        if (i == wind1_KW.length)
                        {
                            i=0;
                            wind1_KW[0]=250;
                           // wind1_KW[1]=100;
                           // wind1_KW[2]=300;
                           // wind1_KW[3]=40;
                        }
                    }
                }
                else if (msg.getPerformative() == ACLMessage.REJECT_PROPOSAL)
                {
                    if (msg.getSender().getLocalName().equals("load2"))
                    {
                        i++;
                        if (i == wind1_KW.length)
                        {
                            //resetting to initial values
                            //the values given in the wind2_KW[] have to be given here again
                            i=0;
                            wind1_KW[0]=250;
                           // wind1_KW[1]=100;
                           // wind1_KW[2]=300;
                           // wind1_KW[3]=40;
                        }
                    }
                }
            }
            else
                block();
        }
    }//end of power_request

}



