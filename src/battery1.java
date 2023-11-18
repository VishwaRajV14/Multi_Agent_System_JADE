import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class battery1 extends Agent
{
    int[] battery1_KW={0};
    int[] battery1_price={7};
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
                    String temp = Integer.toString(battery1_KW[i]);
                    String temp1 = Integer.toString(battery1_price[i]);
                    temp = temp.concat(" " + temp1);
                    ACLMessage reply = new ACLMessage(ACLMessage.PROPOSE);
                    reply.setContent(temp);
                    reply.addReceiver(new AID(msg.getSender().getLocalName(), AID.ISLOCALNAME));
                    send(reply);
                }
                else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL)
                {

                    if(msg.getSender().getLocalName().equals("load1"))
                        battery1_KW[i] = battery1_KW[i] - Integer.parseInt(msg.getContent());
                    else if(msg.getSender().getLocalName().equals("load2"))
                    {
                        i++;
                        if (i == battery1_KW.length)
                        {
                            //resetting to initial values
                            //the values given in the battery1_KW[] have to be given here again
                            i=0;
                            battery1_KW[0]=0;
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
                        if (i == battery1_KW.length)
                        {
                            i=0;
                            battery1_KW[0]=0;
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
