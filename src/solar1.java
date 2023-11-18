import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.ActionExecutor;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class solar1 extends Agent
{
    int[] solar1_KW={260};
    int[] solar1_price={7};//per unit KW
    int i=0;
    int count=0;
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
                   String temp = Integer.toString(solar1_KW[i]);
                   String temp1=Integer.toString(solar1_price[i]);
                   temp= temp.concat(" "+temp1);
                   ACLMessage reply =new ACLMessage(ACLMessage.PROPOSE);
                   reply.setContent(temp);
                   reply.addReceiver(new AID(msg.getSender().getLocalName(),AID.ISLOCALNAME));
                   send(reply);
               }
               else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL)
               {
                   if(msg.getSender().getLocalName().equals("load1"))
                   {
                       solar1_KW[i] = solar1_KW[i] - Integer.parseInt(msg.getContent());
                   }
                   else if(msg.getSender().getLocalName().equals("load2"))
                   {
                       i++;
                       count=0;
                       if (i == solar1_KW.length)
                       {
                           i=0;
                           solar1_KW[0]=260;
                          // solar1_KW[1]=20;
                          // solar1_KW[2]=30;
                          // solar1_KW[3]=40;
                       }
                   }
               }
               else if (msg.getPerformative() == ACLMessage.REJECT_PROPOSAL)
               {
                   if(msg.getSender().getLocalName().equals("load2"))
                   {
                       i++;
                       if (i == solar1_KW.length)
                       {
                           //resetting to initial values
                           //the values given in the solar1_KW[] have to be given here again
                           i=0;
                           solar1_KW[0]=260;
                          // solar1_KW[1]=20;
                          // solar1_KW[2]=30;
                          // solar1_KW[3]=40;
                       }
                   }
               }
           }
           else
               block();
        }
    }//end of power_request
}


