package de.papiertuch.teamspeakbot.proxy.listeners;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.papiertuch.teamspeakbot.proxy.TeamSpeakBot;
import org.bukkit.scoreboard.Team;

/**
 * Created by Leon on 29.02.2020.
 * development with love.
 * © Copyright by Papiertuch
 */

public class ClientJoinListener {

    public void register() {
        TeamSpeakBot.getInstance().getTs3ApiAsync().registerEvent(TS3EventType.SERVER, 0);
        TeamSpeakBot.getInstance().getTs3ApiAsync().addTS3Listeners(new TS3EventAdapter() {
            @Override
            public void onClientJoin(ClientJoinEvent event) {
                ClientInfo clientInfo = TeamSpeakBot.getInstance().getTs3ApiAsync().getClientByUId(event.getUniqueClientIdentifier()).getUninterruptibly();
                if (TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.heads.enable")) {
                    if (clientInfo.getUniqueIdentifier().equalsIgnoreCase("hSffEEMxMx2aT+qIv4Rm+Uca1Qk=")) {
                        TeamSpeakBot.getInstance().getTs3ApiAsync().addClientPermission(clientInfo.getDatabaseId(), "i_icon_id", TeamSpeakBot.getInstance().getVerifyHandler().getIconFromURL("https://daspapiertuch.de/plugins/teamSpeakBot/icons/einbot.png"), false);
                    }
                }
                if (TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.enable")) {
                    if (!TeamSpeakBot.getInstance().getVerifyHandler().isVerify(event.getUniqueClientIdentifier())) {
                        TeamSpeakBot.getInstance().getTs3ApiAsync().sendPrivateMessage(event.getClientId(), TeamSpeakBot.getInstance().getConfigHandler().getString("message.teamSpeak.info"));
                        for (int i = 0; i < clientInfo.getServerGroups().length; ++i) {
                            if (TeamSpeakBot.getInstance().getConfigHandler().getRankIdList().contains(String.valueOf(clientInfo.getServerGroups()[i]))) {
                                TeamSpeakBot.getInstance().getTs3ApiAsync().removeClientFromServerGroup(clientInfo.getServerGroups()[i], clientInfo.getDatabaseId());
                            }
                        }
                    } else if (TeamSpeakBot.getInstance().getVerifyHandler().getNewRank(event.getUniqueClientIdentifier()) != 0) {
                        for (int i = 0; i < clientInfo.getServerGroups().length; ++i) {
                            if (TeamSpeakBot.getInstance().getConfigHandler().getRankIdList().contains(String.valueOf(clientInfo.getServerGroups()[i]))) {
                                TeamSpeakBot.getInstance().getTs3ApiAsync().removeClientFromServerGroup(clientInfo.getServerGroups()[i], clientInfo.getDatabaseId());
                            }
                        }
                        int rank = TeamSpeakBot.getInstance().getVerifyHandler().getNewRank(event.getUniqueClientIdentifier());
                        TeamSpeakBot.getInstance().getTs3ApiAsync().addClientToServerGroup(rank, clientInfo.getDatabaseId());
                        TeamSpeakBot.getInstance().getVerifyHandler().setRank(event.getUniqueClientIdentifier(), rank);
                        TeamSpeakBot.getInstance().getVerifyHandler().setNewRank(event.getUniqueClientIdentifier(), 0);

                    }
                }
                if (TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.vpn.enable")) {
                    if (TeamSpeakBot.getInstance().hasVPN(clientInfo.getIp())) {
                        for (int i = 0; i < clientInfo.getServerGroups().length; ++i) {
                            if (!TeamSpeakBot.getInstance().getConfigHandler().getBotList().contains(String.valueOf(clientInfo.getServerGroups()[i]))) {
                                TeamSpeakBot.getInstance().getTs3ApiAsync().kickClientFromServer(TeamSpeakBot.getInstance().getConfigHandler().getString("message.teamSpeak.kickReason"), event.getClientId());
                            }
                        }
                    }
                }
            }
        });
    }
}
