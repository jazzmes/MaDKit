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
package madkit.simulation;

import static madkit.kernel.AbstractAgent.ReturnCode.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import madkit.kernel.AbstractAgent;
import madkit.kernel.AbstractAgent.ReturnCode;
import madkit.kernel.JunitMadkit;
import madkit.kernel.Watcher;
import madkit.simulation.probe.PropertyProbe;
import madkit.testing.util.agent.NormalAA;
import madkit.testing.util.agent.SimulatedAgent;
import madkit.testing.util.agent.SimulatedAgentBis;

import org.junit.Test;

/**
 * @author Fabien Michel
 * @since MaDKit 5.0.0.2
 * @version 0.9
 * 
 */
@SuppressWarnings("serial")
public class PropertyProbeTest extends JunitMadkit {

	@Test
	public void primitiveTypeProbing() {
		launchTest(new Watcher() {
			protected void activate() {
				SimulatedAgent agent;
				assertEquals(SUCCESS, launchAgent(agent = new SimulatedAgent()));
				PropertyProbe<AbstractAgent, Integer> fp = new PropertyProbe<AbstractAgent, Integer>(COMMUNITY, GROUP, ROLE,
						"privatePrimitiveField");
				addProbe(fp);
				assertTrue(1 == fp.getPropertyValue(agent));
				PropertyProbe<AbstractAgent, Double> fp2 = new PropertyProbe<AbstractAgent, Double>(COMMUNITY, GROUP, ROLE,
						"publicPrimitiveField");
				addProbe(fp2);
				assertTrue(2 == fp2.getPropertyValue(agent));
				agent.setPrivatePrimitiveField(10);
				assertTrue(10 == fp.getPropertyValue(agent));
				assertEquals(SUCCESS, launchAgent(agent = new SimulatedAgent() {
					@Override
					public void setPrivatePrimitiveField(int privatePrimitiveField) {
						super.setPrivatePrimitiveField(100);
					}
				}));
				agent.setPrivatePrimitiveField(10);
				assertEquals(2, fp.size());
				assertTrue(100 == fp.getPropertyValue(agent));
			}
		});
	}

	@Test
	public void multiTypeProbing() {
		launchTest(new Watcher() {
			protected void activate() {
				SimulatedAgent agent;
				SimulatedAgentBis agentBis;
				assertEquals(SUCCESS, launchAgent(agent = new SimulatedAgent()));
				assertEquals(SUCCESS, launchAgent(agentBis = new SimulatedAgentBis()));
				PropertyProbe<AbstractAgent, Integer> fp = new PropertyProbe<AbstractAgent, Integer>(COMMUNITY, GROUP, ROLE,
						"privatePrimitiveField");
				addProbe(fp);
				assertTrue(1 == fp.getPropertyValue(agent));
				assertTrue(1 == fp.getPropertyValue(agentBis));
				PropertyProbe<AbstractAgent, Double> fp2 = new PropertyProbe<AbstractAgent, Double>(COMMUNITY, GROUP, ROLE,
						"publicPrimitiveField");
				addProbe(fp2);
				double i = fp2.getPropertyValue(agent);
				System.err.println(i);
				assertTrue(2 == fp2.getPropertyValue(agent));
				agent.setPrivatePrimitiveField(10);
				assertTrue(10 == fp.getPropertyValue(agent));
				assertEquals(SUCCESS, launchAgent(agent = new SimulatedAgent() {
					@Override
					public void setPrivatePrimitiveField(int privatePrimitiveField) {
						super.setPrivatePrimitiveField(100);
					}
				}));
				agent.setPrivatePrimitiveField(10);
				assertEquals(3, fp.size());
				assertTrue(100 == fp.getPropertyValue(agent));
			}
		});
	}

	@Test
	public void wrongTypeProbing() {
		launchTest(new Watcher() {
			protected void activate() {
				SimulatedAgent agent;
				assertEquals(SUCCESS, launchAgent(agent = new SimulatedAgent()));
				PropertyProbe<AbstractAgent, String> fp = new PropertyProbe<AbstractAgent, String>(COMMUNITY, GROUP, ROLE,
						"privatePrimitiveField");
				addProbe(fp);
				try {
					System.err.println(fp.getPropertyValue(agent));
					noExceptionFailure();
				} catch (ClassCastException e) {
					throw e;
				}
			}
		},ReturnCode.AGENT_CRASH);
	}

	@SuppressWarnings("unused")
	@Test
	public void wrongSourceProbing() {
		launchTest(new Watcher() {
			protected void activate() {
				assertEquals(SUCCESS, launchAgent(new SimulatedAgent()));
				PropertyProbe<AbstractAgent, Integer> fp = new PropertyProbe<AbstractAgent, Integer>(COMMUNITY, GROUP, ROLE,
						"privatePrimitiveField");
				addProbe(fp);
				try {
					NormalAA normalAA = new NormalAA(){
						String privatePrimitiveField="test";
					};
					System.err.println(fp.getPropertyValue(normalAA));
					int i = fp.getPropertyValue(normalAA);
					noExceptionFailure();
				} catch (ClassCastException e) {
					throw e;
				}
			}
		},ReturnCode.AGENT_CRASH);
	}

	@Test
	public void wrongTypeSetting() {
		launchTest(new Watcher() {
			protected void activate() {
				SimulatedAgent agent;
				assertEquals(SUCCESS, launchAgent(agent = new SimulatedAgent()));
				PropertyProbe<AbstractAgent, Object> fp = new PropertyProbe<AbstractAgent, Object>(COMMUNITY, GROUP, ROLE,
						"privatePrimitiveField");
				addProbe(fp);
				try {
					fp.setPropertyValue(agent,"a");
					noExceptionFailure();
				} catch (SimulationException e) {
					throw e;
				}
			}
		},ReturnCode.AGENT_CRASH);
	}

	@Test
	public void noSuchFieldProbing() {
		launchTest(new AbstractAgent() {
			protected void activate() {
				launchDefaultAgent(this);
				PropertyProbe<AbstractAgent, String> fp = new PropertyProbe<AbstractAgent, String>(COMMUNITY, GROUP, ROLE,
						"privatePrimitiveField");
				Watcher s = new Watcher();
				assertEquals(SUCCESS, launchAgent(s));
				s.addProbe(fp);
				try {
					System.err.println(fp.getPropertyValue(fp.getCurrentAgentsList().get(0)));
					noExceptionFailure();
				} catch (NullPointerException e) {
					throw e;
				}
			}
		},ReturnCode.AGENT_CRASH);
	}
	
	@Test
	public void testGetMinAndGetMax() {
		launchTest(new AbstractAgent() {

			protected void activate() {
				for (int i = 0; i < 10; i++) {
					// launchDefaultAgent(this);
					SimulatedAgent agent;
					assertEquals(SUCCESS, launchAgent(agent = new SimulatedAgent()));
					agent.publicPrimitiveField = i;
				}
				PropertyProbe<AbstractAgent, String> fp = new PropertyProbe<AbstractAgent, String>(
						COMMUNITY, GROUP, ROLE, "publicPrimitiveField");
				Watcher s = new Watcher();
				assertEquals(SUCCESS, launchAgent(s));
				s.addProbe(fp);
				assertEquals(9d, fp.getMaxValue());
				assertEquals(0d, fp.getMinValue());
			}
		}, ReturnCode.SUCCESS);
	}

	@Test
	public void getMinAndGetMaxnotComparable(){
		launchTest(new AbstractAgent() {
			protected void activate() {
//				launchDefaultAgent(this);
				SimulatedAgent agent;
				assertEquals(SUCCESS, launchAgent(agent = new SimulatedAgent()));
				PropertyProbe<AbstractAgent, String> fp = new PropertyProbe<AbstractAgent, String>(COMMUNITY, GROUP, ROLE,
						"objectField");
				Watcher s = new Watcher();
				assertEquals(SUCCESS, launchAgent(s));
				s.addProbe(fp);
				try {
					System.err.println(fp.getMaxValue());
					noExceptionFailure();
				} catch (SimulationException e) {
					e.printStackTrace();
				}
				try {
					System.err.println(fp.getMinValue());
					noExceptionFailure();
				} catch (SimulationException e) {
					e.printStackTrace();
				}
			}
		},ReturnCode.SUCCESS);
	}

}