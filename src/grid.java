import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class grid extends Agent
{
    int grid_KW[]={1000};
    int[] grid1_price={3};
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
            //MessageTemplate mt= MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = receive();

            if(msg!=null)
            {
                if (msg.getPerformative() == ACLMessage.CFP)
                {
                    System.out.println(msg.getSender().getLocalName()+ " approaching " + getAID().getLocalName());
                    String temp = Integer.toString(grid_KW[i]);
                    String temp1=Integer.toString(grid1_price[i]);
                    temp=temp.concat(" "+temp1);
                    ACLMessage reply =new ACLMessage(ACLMessage.PROPOSE);
                    reply.setContent(temp);
                    reply.addReceiver(new AID(msg.getSender().getLocalName(),AID.ISLOCALNAME));
                    send(reply);
                }
                else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL)
                {
                    if(msg.getSender().getLocalName().equals("load1"))
                        grid_KW[i] = grid_KW[i] - Integer.parseInt(msg.getContent());
                    else if(msg.getSender().getLocalName().equals("load2"))
                    {
                        i++;
                        if (i == grid_KW.length)
                        {
                            //resetting to initial values
                            //the values given in the grid_KW[] have to be given here again
                            i=0;
                            grid_KW[0]=1000;
                            //grid_KW[1]=1300;
                            //grid_KW[2]=700;
                            //grid_KW[3]=800;
                        }

                    }
                }
                else if (msg.getPerformative() == ACLMessage.REJECT_PROPOSAL)
                {
                    if(msg.getSender().getLocalName().equals("load2"))
                    {
                        i++;
                        if(i==grid_KW.length)
                        {
                            i = 0;
                            grid_KW[0]=1000;
                           // grid_KW[1]=1300;
                           // grid_KW[2]=700;
                           // grid_KW[3]=800;
                        }

                    }
                }
            }
            else
                block();
        }
    }//end of power_request

}
