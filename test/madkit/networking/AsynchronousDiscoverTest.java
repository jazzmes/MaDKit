/*
 * Copyright 1997-2012 Fabien Michel, Olivier Gutknecht, Jacques Ferber
 * 
 * This file is part of MaDKit.
 * 
 * MaDKit is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * MaDKit is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MaDKit. If not, see <http://www.gnu.org/licenses/>.
 */
package madkit.networking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import madkit.action.KernelAction;
import madkit.agr.CloudCommunity;
import madkit.agr.LocalCommunity;
import madkit.agr.LocalCommunity.Groups;
import madkit.kernel.AbstractAgent;
import madkit.kernel.AgentAddress;
import madkit.kernel.JunitMadkit;
import madkit.kernel.Madkit.BooleanOption;

import org.junit.Test;

/**
 * @author Fabien Michel
 * @since MaDKit 5.0.0.10
 * @version 0.9
 * 
 */
@SuppressWarnings("serial")
public class AsynchronousDiscoverTest extends JunitMadkit {

	@Test
	public void multipleAsynchroneConnectionTest() {
		addMadkitArgs(BooleanOption.network.toString());
//		 addMadkitArgs(LevelOption.networkLogLevel.toString(),"FINER");
		launchTest(new AbstractAgent() {
			@Override
			protected void activate() {
				launchThreadedMKNetworkInstance();
				launchThreadedMKNetworkInstance();
				launchThreadedMKNetworkInstance();
				KernelAction.LAUNCH_NETWORK.getActionFor(this).actionPerformed(null);
				launchThreadedMKNetworkInstance();
				launchThreadedMKNetworkInstance();
				pause(100);
				int i = 0;
				while (getAgentsWithRole(CloudCommunity.NAME, CloudCommunity.Groups.NETWORK_AGENTS, CloudCommunity.Roles.NET_AGENT) == null || getAgentsWithRole(CloudCommunity.NAME, CloudCommunity.Groups.NETWORK_AGENTS, CloudCommunity.Roles.NET_AGENT)
						.size() != 6) {
					pause(500);
					if (i++ == 10)
						break;
				}
				if (logger != null)
					logger.info("" + getAgentsWithRole(LocalCommunity.NAME, Groups.NETWORK, LocalCommunity.Roles.NET_AGENT));
				assertEquals(6,
						getAgentsWithRole(CloudCommunity.NAME, CloudCommunity.Groups.NETWORK_AGENTS, CloudCommunity.Roles.NET_AGENT)
								.size());
				pause(500);
				KernelAction.STOP_NETWORK.getActionFor(this).actionPerformed(null);
				pause(500);

				// not connected
				assertFalse(isCommunity(CloudCommunity.NAME));

				// second round
				KernelAction.LAUNCH_NETWORK.getActionFor(this).actionPerformed(null);
				List<AgentAddress> l = getAgentsWithRole(CloudCommunity.NAME, CloudCommunity.Groups.NETWORK_AGENTS,
						CloudCommunity.Roles.NET_AGENT);
				while (l == null || l.size() != 6) {
					pause(500);
					l = getAgentsWithRole(CloudCommunity.NAME, CloudCommunity.Groups.NETWORK_AGENTS, CloudCommunity.Roles.NET_AGENT);
					if (i++ == 100)
						break;
				}
				for (AgentAddress agentAddress : l) {
					System.err.println(agentAddress);
				}
				assertEquals(6, l.size());
			}
		});
	}

}
