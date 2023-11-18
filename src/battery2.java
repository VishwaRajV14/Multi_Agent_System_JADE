import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class battery2 extends Agent
{
    int[] battery2_KW={3};
    int[] battery2_price={7};
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

            if(msg!=null)
            {
                if (msg.getPerformative() == ACLMessage.CFP)
                {
                    System.out.println(msg.getSender().getLocalName() + " approaching " + getAID().getLocalName());
                    String temp = Integer.toString(battery2_KW[i]);
                    String temp1 = Integer.toString(battery2_price[i]);
                    temp = temp.concat(" " + temp1);
                    ACLMessage reply = new ACLMessage(ACLMessage.PROPOSE);
                    reply.setContent(temp);
                    reply.addReceiver(new AID(msg.getSender().getLocalName(), AID.ISLOCALNAME));
                    send(reply);
                }
                else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL)
                {

                    if(msg.getSender().getLocalName().equals("load1"))
                        battery2_KW[i] = battery2_KW[i] - Integer.parseInt(msg.getContent());
                    else if(msg.getSender().getLocalName().equals("load2"))
                    {
                        i++;
                        if (i == battery2_KW.length)
                        {
                            //resetting to initial values
                            //the values given in the battery2_KW[] have to be given here again
                            i=0;
                            battery2_KW[0]=3;
                           // battery1_KW[1]=100;
                           // battery1_KW[2]=20;
                           // battery1_KW[3]=10;
                        }
                    }
                }
                else if (msg.getPerformative() == ACLMessage.REJECT_PROPOSAL)
                {
                    if(msg.getSender().getLocalName().equals("load2"))
                    {
                        i++;
                        if (i == battery2_KW.length)
                        {
                            i=0;
                            battery2_KW[0]=3;
                           // battery1_KW[1]=100;
                           // battery1_KW[2]=20;
                          //  battery1_KW[3]=10;
                        }
                    }
                }
            }
            else
                block();
        }
    }//end of power_request
}

