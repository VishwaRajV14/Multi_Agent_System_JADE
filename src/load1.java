import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.core.Agent;



public class load1 extends Agent
{
    int i=0;
    int[] Load1_KW = {300};
    int load1_KW=Load1_KW[i];
    String[] source={"solar1","wind1","battery1","solar2","wind2","battery2","grid"};

    class source_agents implements Cloneable
    {
        int source_KW;
        int source_price;
        String source_name;

        @Override
        public Object clone() throws CloneNotSupportedException
        {
            return super.clone();
        }

    }

    @Override
    protected void setup() { addBehaviour(new Power_Receiver()); }


    private class Power_Receiver extends CyclicBehaviour
    {
        int s_count=0;// the cyclic count and source string count
        int step = 0;
        source_agents[] source_agents_object = new source_agents[source.length];

        @Override
        public void action()
        {
            if (step==0)//send cfp to all source agents
            {
                System.out.println(getAID().getLocalName() + " is ready for power consumption and needs " + load1_KW + " KW");
                ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                for(int i=0;i<source.length;i++)
                {
                    cfp.addReceiver(new AID(source[i], AID.ISLOCALNAME));
                }
                send(cfp);
                step=1;
            }

            if(step==1) // accepting proposals from all agents to choose the best
            {
                ACLMessage reply = receive();

                if (reply != null)
                {
                    if(reply.getPerformative()==ACLMessage.PROPOSE)
                    {

                        source_agents_object[s_count] = new source_agents();

                        String temp = reply.getContent();
                        String str[] = temp.split(" ");

                        source_agents_object[s_count].source_name = reply.getSender().getLocalName();
                        source_agents_object[s_count].source_KW = Integer.parseInt(str[0]);
                        source_agents_object[s_count].source_price = Integer.parseInt((str[1]));

                        s_count++;
                        if (s_count == source.length)
                        {
                            // data is collected now the best of them have to be approached
                            //sorting based on lowest price with max power capacity

                            for (int i = 0; i < source_agents_object.length; i++)
                            {
                                for (int j = 0; j < source_agents_object.length - i - 1; j++)
                                {
                                    if (source_agents_object[j].source_price > source_agents_object[j + 1].source_price)
                                    {
                                        source_agents swap = source_agents_object[j];
                                        source_agents_object[j] = source_agents_object[j + 1];
                                        source_agents_object[j + 1] = swap;
                                    }
                                    else if (source_agents_object[j].source_price == source_agents_object[j + 1].source_price)
                                    {
                                        if (source_agents_object[j].source_KW < source_agents_object[j + 1].source_KW)
                                        {
                                            source_agents swap = source_agents_object[j];
                                            source_agents_object[j] = source_agents_object[j + 1];
                                            source_agents_object[j + 1] = swap;
                                        }
                                    }
                                }
                            }
                            //                                        TAPPING

                            //this is created for the sake of proper console output and tapping from sources
                            source_agents[] tapped = new source_agents[source.length];

                            int flag = 0;
                            for (int i = 0; i < tapped.length; i++)
                            {
                                try
                                {
                                    Object object = source_agents_object[i].clone();
                                    tapped[i] = (source_agents) object;
                                } catch (CloneNotSupportedException e)
                                {
                                    e.printStackTrace();
                                }
                                // the above code snippet is for cloning one object to another

                                if (tapped[i].source_KW != 0)
                                {
                                    if (load1_KW > tapped[i].source_KW)
                                    {
                                        load1_KW -= tapped[i].source_KW;
                                        ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                                        System.out.println(getAID().getLocalName() + " tapped " + tapped[i].source_KW + " KW of power from " + tapped[i].source_name);
                                        msg.addReceiver(new AID(tapped[i].source_name, AID.ISLOCALNAME));
                                        msg.setContent(Integer.toString(tapped[i].source_KW));
                                        send(msg);
                                    }
                                    else if (load1_KW < tapped[i].source_KW && load1_KW != 0)
                                    {
                                        tapped[i].source_KW = load1_KW;
                                        load1_KW = 0;
                                        ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                                        System.out.println(getAID().getLocalName() + " tapped " + tapped[i].source_KW + " KW of power from " + tapped[i].source_name);
                                        msg.addReceiver(new AID(tapped[i].source_name, AID.ISLOCALNAME));
                                        msg.setContent(Integer.toString(tapped[i].source_KW));
                                        send(msg);
                                        flag = 1;
                                    }
                                    else if (load1_KW == 0 && flag != 1)
                                    {
                                        tapped[i].source_KW = 0;
                                        ACLMessage msg = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
                                        System.out.println(getAID().getLocalName() + " rejected the proposal of " + tapped[i].source_name);
                                        msg.addReceiver(new AID(tapped[i].source_name, AID.ISLOCALNAME));
                                        msg.setContent(Integer.toString(tapped[i].source_KW));
                                        send(msg);
                                    }
                                    if (flag == 1)
                                        flag = 0;
                                }
                                else
                                    {
                                    tapped[i].source_KW = 0;
                                    ACLMessage msg = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
                                    System.out.println(getAID().getLocalName() + " rejected the proposal of " + tapped[i].source_name);
                                    msg.addReceiver(new AID(tapped[i].source_name, AID.ISLOCALNAME));
                                    msg.setContent(Integer.toString(tapped[i].source_KW));
                                    send(msg);
                                }

                            }

                            int total_price = 0;
                            System.out.println("\nOUTPUT:");
                            System.out.println("--------");
                            System.out.println("Load1 demand:" + Load1_KW[i]);
                            for (int i = 0; i < tapped.length; i++)
                            {
                                System.out.println("\nPower Available in " + source_agents_object[i].source_name + ":" + source_agents_object[i].source_KW);
                                System.out.println("Power tapped from " + tapped[i].source_name + ":" + tapped[i].source_KW);
                                System.out.println("Price per unit KW: Rs" + tapped[i].source_price);
                                System.out.println("Amount spent for " + tapped[i].source_name + ": Rs" + tapped[i].source_price * tapped[i].source_KW);
                                total_price = Integer.sum(total_price, tapped[i].source_price * tapped[i].source_KW);
                            }
                            System.out.println("\nTotal Price: Rs" + total_price);
                            System.out.println("----------------------------------------------------------------------------------");

                            //sending info to load2 so that it can start its operation

                            ACLMessage msg1 = new ACLMessage(ACLMessage.INFORM);
                            msg1.addReceiver(new AID("load2", AID.ISLOCALNAME));
                            send(msg1);
                        }
                    }
                    if(reply.getPerformative()==ACLMessage.INFORM)
                    {
                        //cyclic behaviour for going to next load value
                        step = 0;
                        i++;
                        if (i == Load1_KW.length)
                            i = 0;
                        s_count = 0;
                        load1_KW = Load1_KW[i];
                    }

                }
                else
                    block();
            }

        }

    }
}











