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
package madkit.message;

import madkit.action.SchedulingAction;

/**
 * This message class could be used to interact with a Scheduler agent.
 * Here is an example :
 * 
 * <pre>
 * sendMessage(schedulerAddress, new SchedulingMessage(SchedulingAction.PAUSE));
 * </pre>
 * 
 * 
 * @author Fabien Michel
 * @since MaDKit 5.0.0.14
 * @version 0.9
 * 
 */
public class SchedulingMessage extends EnumMessage<SchedulingAction> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1433336977900865385L;

	public SchedulingMessage(SchedulingAction schedulerAction, Object... info) {
		super(schedulerAction, info);
	}

}
