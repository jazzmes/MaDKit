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
package madkit.testing.util.agent;

import static madkit.kernel.AbstractAgent.ReturnCode.SUCCESS;
import static madkit.kernel.JunitMadkit.COMMUNITY;
import static madkit.kernel.JunitMadkit.GROUP;
import static madkit.kernel.JunitMadkit.ROLE;
import static org.junit.Assert.assertEquals;

import java.util.logging.Level;

import madkit.kernel.Agent;

/**
 * @author Fabien Michel
 * @since MaDKit 5.0.0.12
 * @version 0.9
 * 
 */
public class LeaveGroupInEndNormalAgent extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LeaveGroupInEndNormalAgent() {
		setName(getLogger().getName());
	}

	@Override
	protected void activate() {
		setLogLevel(Level.ALL);
		assertEquals(SUCCESS, createGroup(COMMUNITY, GROUP,true));
		assertEquals(SUCCESS, requestRole(COMMUNITY, GROUP, ROLE));
	}
	
	protected void live() {
	}
	
	@Override
	protected void end() {
		assertEquals(SUCCESS, leaveGroup(COMMUNITY, GROUP));
		pause(1000);
	}

}
