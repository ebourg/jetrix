/**
 * Java TetriNET Server
 * Copyright (C) 2001  Emmanuel Bourg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.lfjr.jts;

import java.io.*;
import java.util.*;

public class Channel extends Thread
{
    private int     startingLevel = 1;
    private int     stackHeight = 0;
    private int     linesForLevel = 2;
    private int     levelIncrease = 1;
    private int     linesForSpecial = 1;
    private int     specialsAdded = 1;
    private int     capacity = 18;
    private int[]   pieceOccurancy = {15, 15, 14, 14, 14, 14, 14};
    private int[]   specialOccurancy = {32, 20, 1, 11, 1, 14, 1, 6, 14};
    private boolean averagedLevels = true;
    private boolean classicRules = true;

    MessageQueue    mq;

    boolean         running = true;

    boolean         persistent = false;

    static int      GAME_STATE_PAUSED = 0;
    static int      GAME_STATE_STARTED = 1;
    static int      GAME_STATE_STOPPED = 2;

    int             gameState = GAME_STATE_STOPPED;

    int             MAX_PLAYER = 6;

    Vector          listeJoueurs = new Vector(6);

    Stack           incomingClients = new Stack();

    public Channel()
    {
        mq = new MessageQueue();

        for (int i = 0; i<6; i++)
        {
            listeJoueurs.addElement(null);
        }

        start();
    }

    public void run()
    {

        while (running)
        {
            /*

            try
            {

                Message message = mq.get();

                StringTokenizer stringtokenizer = new StringTokenizer(message, " ");

                String          cmd = stringtokenizer.nextToken();

                if ("team".equals(cmd))
                {
                    int    i = Integer.parseInt(stringtokenizer.nextToken());
                    String s3 = "";

                    if (stringtokenizer.hasMoreTokens())
                    {
                        s3 = stringtokenizer.nextToken();
                    }

                    ((TetriNETClient) listeJoueurs.elementAt(i - 1)).setTeam(s3);

                    int i4 = 0;

                    while (i4<listeJoueurs.size())
                    {
                        TetriNETClient tetrinetclient8 = (TetriNETClient) listeJoueurs.elementAt(i4);

                        if (tetrinetclient8!=null && i4 + 1!=i)
                        {
                            tetrinetclient8.sendMessage(message);
                        }

                        i4++;
                    }
                }
                else if ("server".equals(cmd))
                {
                    // server command
                    String svrcmd = stringtokenizer.nextToken();

                    if ("addclient".equals(svrcmd))
                    {
                        System.out.println("new client, thanks !");

                        int i2;

                        for (i2 = 0; i2<6 && listeJoueurs.elementAt(i2)!=null; i2++);

                        if (i2>=6)
                        {
                            //System.out.println("arf, plus de place ! On fait quoi ? " + this);
                        }
                        else
                        {
                            TetriNETClient tetrinetclient2 = (TetriNETClient) incomingClients.pop();

                            listeJoueurs.setElementAt(tetrinetclient2, i2);
                            tetrinetclient2.sendMessage("pline 0 \022Hello " + tetrinetclient2.getNickname() + ", you are in channel " + this);
                            tetrinetclient2.sendMessage("playernum " + (i2 + 1));
                            tetrinetclient2.setSlot(i2 + 1);

                            int l4 = 0;

                            while (l4<listeJoueurs.size())
                            {
                                TetriNETClient tetrinetclient11 = (TetriNETClient) listeJoueurs.elementAt(l4);

                                if (tetrinetclient11!=null && l4!=i2)
                                {
                                    tetrinetclient11.sendMessage("playerjoin " + (i2 + 1) + " " + tetrinetclient2.getNickname());
                                    tetrinetclient2.sendMessage("playerjoin " + (l4 + 1) + " " + tetrinetclient11.getNickname());
                                    tetrinetclient2.sendMessage("team " + (l4 + 1) + " " + tetrinetclient11.getTeam());
                                }

                                l4++;
                            }
                        }
                    }
                }
                else if ("pline".equals(cmd))
                {
                    int    j = Integer.parseInt(stringtokenizer.nextToken());
                    String s4 = stringtokenizer.nextToken();

                    if (!s4.startsWith("/"))
                    {
                        int j4 = 0;

                        while (j4<listeJoueurs.size())
                        {
                            TetriNETClient tetrinetclient9 = (TetriNETClient) listeJoueurs.elementAt(j4);

                            if (tetrinetclient9!=null && j4 + 1!=j)
                            {
                                tetrinetclient9.sendMessage("pline " + j + " " + ChatColors.slots[j4] + message.substring(8));
                            }

                            j4++;
                        }
                    }
                    else if ("/list".equalsIgnoreCase(s4) || "/who".equalsIgnoreCase(s4) || "/move".equalsIgnoreCase(s4) || "/tell".equalsIgnoreCase(s4) || "/g".equalsIgnoreCase(s4) || "/br".equalsIgnoreCase(s4) || "/duel".equalsIgnoreCase(s4) || "/gu".equalsIgnoreCase(s4) || "/kick".equalsIgnoreCase(s4) || "/ban".equalsIgnoreCase(s4) || "/shutup".equalsIgnoreCase(s4) || "/start".equalsIgnoreCase(s4) || "/op".equalsIgnoreCase(s4) ||!"/help".equalsIgnoreCase(s4))
                    {
                        ;
                    }
                }
                // gmsg <playername> message
                else if ("gmsg".equals(cmd))
                {
                    //int k = Integer.parseInt(stringtokenizer.nextToken());
                    int j2 = 0;

                    while (j2<listeJoueurs.size())
                    {
                        TetriNETClient tetrinetclient3 = (TetriNETClient) listeJoueurs.elementAt(j2);

                        if (tetrinetclient3!=null )
                        {
                            tetrinetclient3.sendMessage(message);
                        }

                        j2++;
                    }
                }
                else if ("plineact".equals(cmd))
                {
                    int l = Integer.parseInt(stringtokenizer.nextToken());
                    int k2 = 0;

                    while (k2<listeJoueurs.size())
                    {
                        TetriNETClient tetrinetclient4 = (TetriNETClient) listeJoueurs.elementAt(k2);

                        if (tetrinetclient4!=null && k2 + 1!=l)
                        {
                            tetrinetclient4.sendMessage(message);
                        }

                        k2++;
                    }
                }
                // startgame <0 = stop | 1 = start> <playernumber>
                else if ("startgame".equals(cmd))
                {
                    boolean flag = "1".equals(stringtokenizer.nextToken());
                    int     l2 = Integer.parseInt(stringtokenizer.nextToken());

                    if (flag)
                    {
                        String s5 = "newgame " + stackHeight + " " + startingLevel + " " + linesForLevel + " " + levelIncrease + " " + linesForSpecial + " " + specialsAdded + " " + capacity + " ";

                        for (int i5 = 0; i5<pieceOccurancy.length; i5++)
                        {
                            for (int l5 = 0; l5<pieceOccurancy[i5]; l5++)
                            {
                                s5 = s5 + (i5 + 1);
                            }
                        }

                        s5 = s5 + " ";

                        for (int j5 = 0; j5<specialOccurancy.length; j5++)
                        {
                            for (int i6 = 0; i6<specialOccurancy[j5]; i6++)
                            {
                                s5 = s5 + (j5 + 1);
                            }

                        }

                        s5 = s5 + " " + (averagedLevels ? "1" : "0") + " " + (classicRules ? "1" : "0");
                        gameState = GAME_STATE_STARTED;

                        int k5 = 0;

                        while (k5<listeJoueurs.size())
                        {
                            TetriNETClient tetrinetclient12 = (TetriNETClient) listeJoueurs.elementAt(k5);

                            if (tetrinetclient12!=null)
                            {
                                tetrinetclient12.sendMessage(s5);
                            }

                            k5++;
                        }
                    }
                    else
                    {
                        gameState = GAME_STATE_STOPPED;

                        int k4 = 0;

                        while (k4<listeJoueurs.size())
                        {
                            TetriNETClient tetrinetclient10 = (TetriNETClient) listeJoueurs.elementAt(k4);

                            if (tetrinetclient10!=null)
                            {
                                tetrinetclient10.sendMessage("endgame");
                            }

                            k4++;
                        }
                    }
                }
                else if ("pause".equals(cmd))
                {
                    gameState = GAME_STATE_PAUSED;

                    int i1 = 0;

                    while (i1<listeJoueurs.size())
                    {
                        TetriNETClient tetrinetclient1 = (TetriNETClient) listeJoueurs.elementAt(i1);

                        if (tetrinetclient1!=null)
                        {
                            tetrinetclient1.sendMessage("pause " + stringtokenizer.nextToken());
                        }

                        i1++;
                    }
                }
                else if ("playerlost".equals(cmd))
                {
                    int i3 = 0;

                    while (i3<listeJoueurs.size())
                    {
                        TetriNETClient tetrinetclient = (TetriNETClient) listeJoueurs.elementAt(i3);

                        if (tetrinetclient!=null)
                        {
                            tetrinetclient.sendMessage(message);
                        }

                        i3++;
                    }
                }
                else if ("lvl".equals(cmd)) {}
                else if ("sb".equals(cmd))
                {                	
                    int to = Integer.parseInt(stringtokenizer.nextToken());
                    String bonus = stringtokenizer.nextToken();
                    int from = Integer.parseInt(stringtokenizer.nextToken());   
                    
                    //System.out.println(message+" ("+to+":"+bonus+":"+from+")");                 
                    
                    int j3 = 1;

                    while (j3<=listeJoueurs.size())
                    {
                        TetriNETClient client = (TetriNETClient) listeJoueurs.elementAt(j3-1);

                        if (client!=null && (j3 != from)) { client.sendMessage(message); }

                        j3++;
                    }
                }
                else if ("f".equals(cmd))
                {

                    int k1 = Integer.parseInt(stringtokenizer.nextToken());
                    int k3 = 0;

                    while (k3<listeJoueurs.size())
                    {
                        TetriNETClient tetrinetclient6 = (TetriNETClient) listeJoueurs.elementAt(k3);

                        if (tetrinetclient6!=null && k3 + 1!=k1)
                        {
                            tetrinetclient6.sendMessage(message);
                        }

                        k3++;
                    }
                }
                else if ("disconnected".equals(cmd))
                {

                    int l1 = Integer.parseInt(stringtokenizer.nextToken());

                    //System.out.println("Player leaving " + l1);

                    if (l1>0 && l1<=MAX_PLAYER)
                    {
                        listeJoueurs.setElementAt(null, l1 - 1);
                    }

                    //System.out.println(listeJoueurs);

                    int l3 = 0;

                    while (l3<listeJoueurs.size())
                    {
                        TetriNETClient tetrinetclient7 = (TetriNETClient) listeJoueurs.elementAt(l3);

                        if (tetrinetclient7!=null)
                        {
                            tetrinetclient7.sendMessage("playerleave " + l1);
                        }

                        l3++;
                    }
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            
            */

        }

        System.out.println("closing channel : " + this);
    }


    public void addMessage(Message m)
    {
        mq.put(m);
    }


    public void addPlayer(TetriNETClient tc)
    {
        listeJoueurs.addElement(tc);
    }


    public void removePlayer(TetriNETClient tc)
    {
        listeJoueurs.removeElement(tc);
    }


    public boolean isFull()
    {
        int count = 0;

        for (int i = 0; i<MAX_PLAYER; i++)
        {
            if (listeJoueurs.elementAt(i++)!=null)
            {
                count++;

            }
        }

        return count>=MAX_PLAYER;
    }


    public boolean isPersistent()
    {
        return persistent;
    }


    public void addClient(TetriNETClient c)
    {
        incomingClients.push(c);
    }


    public void sortPlayers() {}


    public void swichPlayers(int i, int j) {}

}

