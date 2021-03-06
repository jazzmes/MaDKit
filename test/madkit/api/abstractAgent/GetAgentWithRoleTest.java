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

import static madkit.kernel.AbstractAgent.ReturnCode.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import madkit.agr.Organization;
import madkit.kernel.AbstractAgent;
import madkit.kernel.JunitMadkit;

import org.junit.Test;

/**
 * @author Fabien Michel
 * @since MaDKit 5.0.0.7
 * @version 0.9
 * 
 */
@SuppressWarnings("serial")
public class GetAgentWithRoleTest extends JunitMadkit {

	final AbstractAgent target = new AbstractAgent() {
		protected void activate() {
			assertEquals(SUCCESS, createGroup(COMMUNITY, GROUP));
		}
	};

	@Test
	public void nullArgs() {
		launchTest(new AbstractAgent() {
			protected void activate() {
				try {
					getAgentWithRole(null, null, null);
					noExceptionFailure();
				} catch (NullPointerException e) {
					// e.printStackTrace();
				}
				try {
					assertEquals(SUCCESS, createGroup(COMMUNITY, GROUP));
					assertNull(getAgentWithRole(COMMUNITY, null, null));
					noExceptionFailure();
				} catch (NullPointerException e) {
					// e.printStackTrace();
				}
				try {
					assertNull(getAgentWithRole(COMMUNITY, GROUP, null));
					noExceptionFailure();
				} catch (NullPointerException e) {
					// e.printStackTrace();
				}
				try {
					assertNull(getAgentWithRole(null, GROUP, ROLE));
					noExceptionFailure();
				} catch (NullPointerException e) {
					// e.printStackTrace();
				}
				try {
					assertNull(getAgentWithRole(null, null, ROLE));
					noExceptionFailure();
				} catch (NullPointerException e) {
					// e.printStackTrace();
				}
				try {
					assertNull(getAgentWithRole(COMMUNITY, null, ROLE));
					noExceptionFailure();
				} catch (NullPointerException e) {
					// e.printStackTrace();
				}
			}
		});
	}

	@Test
	public void returnNull() {
		launchTest(new AbstractAgent() {
			protected void activate() {
				launchAgent(target);
				assertEquals(SUCCESS, target.requestRole(COMMUNITY, GROUP, ROLE));
				assertNull(getAgentWithRole(aa(), GROUP, ROLE));
				assertNull(getAgentWithRole(COMMUNITY, aa(), ROLE));
				assertNull(getAgentWithRole(COMMUNITY, GROUP, aa()));
				assertNotNull(getAgentWithRole(COMMUNITY, GROUP, ROLE));
				assertEquals(SUCCESS, target.leaveRole(COMMUNITY, GROUP, ROLE));
				assertNull(getAgentWithRole(COMMUNITY, GROUP, ROLE));
			}
		});
	}

	@Test
	public void returnNotNullOnManagerRole() {
		launchTest(new AbstractAgent() {
			protected void activate() {
				launchAgent(target);
				assertNotNull(getAgentWithRole(COMMUNITY, GROUP, Organization.GROUP_MANAGER_ROLE));
			}
		});
	}

	@Test
	public void returnNullOnManagerRole() {
		launchTest(new AbstractAgent() {
			protected void activate() {
				assertEquals(SUCCESS, createGroup(COMMUNITY, GROUP));
				assertNull(getAgentWithRole(COMMUNITY, GROUP, Organization.GROUP_MANAGER_ROLE));
			}
		});
	}
}
