/*
 * Copyright 1997-2011 Fabien Michel, Olivier Gutknecht, Jacques Ferber
 * 
 * This file is part of MadKit.
 * 
 * MadKit is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * MadKit is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with MadKit. If not, see <http://www.gnu.org/licenses/>.
 */
package madkit.networking;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.logging.Level;

import madkit.action.KernelAction;
import madkit.agr.CloudCommunity;
import madkit.kernel.AbstractAgent;
import madkit.kernel.AgentAddress;
import madkit.kernel.JunitMadKit;
import madkit.kernel.Madkit;
import madkit.kernel.Madkit.BooleanOption;
import madkit.kernel.Madkit.LevelOption;

import org.junit.Test;

/**
 * @author Fabien Michel
 * @since MadKit 5.0.0.10
 * @version 0.9
 * 
 */
@SuppressWarnings("serial")
public class ConnectionTest extends JunitMadKit {

	@Test
	public void simpleConnectionTest() {
//		pause(5000);
		addMadkitArgs(BooleanOption.network.toString()
//				,
				,LevelOption.networkLogLevel.toString(),"ALL"
//				LevelOption.kernelLogLevel.toString(),"FINER",
//				BooleanOption.createLogFiles.toString(),
//				Option.launchAgents.toString(),ForEverAgent.class.getName()
				);
		launchTest(new AbstractAgent() {
			@Override
			protected void activate() {
				Madkit m = launchMKNetworkInstance(Level.OFF);
				pause(200);
				List<AgentAddress> l = getAgentsWithRole(
						CloudCommunity.NAME, 
						CloudCommunity.Groups.NETWORK_AGENTS,
						CloudCommunity.Roles.NET_AGENT);
				for (AgentAddress agentAddress : l) {
					System.err.println(agentAddress);
				}
				assertEquals(2, l.size());
				m.doAction(KernelAction.EXIT);
				KernelAction.EXIT.getActionFor(this).actionPerformed(null);
				pause(100);
			}
		});
	}
}