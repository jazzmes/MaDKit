/*
 * Copyright 1997-2012 Fabien Michel, Olivier Gutknecht, Jacques Ferber
 * 
 * This file is part of MaDKit.
 * 
 * MaDKit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * MaDKit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MaDKit. If not, see <http://www.gnu.org/licenses/>.
 */
package madkit.api.abstractAgent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import madkit.kernel.AbstractAgent;
import madkit.kernel.AbstractAgent.ReturnCode;
import madkit.kernel.AbstractAgent.State;
import madkit.kernel.JunitMadkit;
import madkit.kernel.Scheduler;
import madkit.simulation.SimulationException;
import madkit.simulation.activator.GenericBehaviorActivator;
import madkit.testing.util.agent.FaultyAA;
import madkit.testing.util.agent.SimulatedAgent;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Fabien Michel
 * @since MaDKit 5.0.0.14
 * @version 0.9
 * 
 */
@SuppressWarnings("serial")
public class LaunchAgentBucketWithRolesWithListTest extends JunitMadkit {

	GenericBehaviorActivator<AbstractAgent>	buggy;
	static int											size	= 1001;

	@Before
	public void setUp() throws Exception {
		buggy = new GenericBehaviorActivator<AbstractAgent>(COMMUNITY, GROUP,
				ROLE, "doIt");
	}

	@Test
	public void cannotLaunch() {
		launchTest(new AbstractAgent() {

			protected void activate() {
				launchAgentBucket(FaultyAA.class.getName(), size, COMMUNITY + ";"
						+ GROUP + ";" + ROLE);
			}
		});
	}

	@Test
	public void nullArg() {
		launchTest(new AbstractAgent() {

			protected void activate() {
				List<AbstractAgent> l = new ArrayList<AbstractAgent>();
				for (int i = 0; i < 1; i++) {
					l.add(null);
				}
				launchAgentBucket(l, COMMUNITY + ";" + GROUP + ";" + ROLE);
			}
		});
	}

	@Test
	public void wrongCGR() {
		launchTest(new AbstractAgent() {

			protected void activate() {
				try {
					launchAgentBucket(FaultyAA.class.getName(), size, COMMUNITY
							+ ";" + GROUP + ";" + ROLE);
					JunitMadkit.noExceptionFailure();
				} catch (IllegalArgumentException e) {
					throw e;
				}
			}
		}, ReturnCode.AGENT_CRASH);
	}

	@Test
	public void returnSuccess() {
		launchTest(new AbstractAgent() {

			protected void activate() {
				List<SimulatedAgent> l = new ArrayList<SimulatedAgent>();
				for (int i = 0; i < size; i++) {
					l.add(new SimulatedAgent());
				}
				launchAgentBucket(l, COMMUNITY + ";" + GROUP + ";" + ROLE);
				testAgents(l);
				assertEquals(size, getAgentsWithRole(COMMUNITY, GROUP, ROLE).size());
			}
		});
	}

	@Test
	public void inScheduledAgent() {
		launchTest(new Scheduler() {

			protected void activate() {
				GenericBehaviorActivator<AbstractAgent> test = new GenericBehaviorActivator<AbstractAgent>(
						COMMUNITY, GROUP, ROLE, "launchAgentBucketWithRoles");
				launchAgent(new SimulatedAgent());
				addActivator(test);
				try {
					test.execute();
				} catch (SimulationException e) {
					e.printStackTrace();
					throw e;
				}
			}
		}, ReturnCode.SUCCESS);
	}

	public static void testAgents(List<? extends AbstractAgent> l) {
		for (AbstractAgent abstractAgent : l) {
			assertTrue(abstractAgent.isAlive());
			assertEquals(State.ACTIVATED, abstractAgent.getState());
			assertTrue(((SimulatedAgent) abstractAgent).goneThroughActivate());
		}
	}

}
