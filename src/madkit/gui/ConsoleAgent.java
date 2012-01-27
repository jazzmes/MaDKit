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
package madkit.gui;

import java.io.PrintStream;

import javax.swing.JFrame;

import madkit.kernel.AbstractAgent;

/**
 * This agent displays standard out and err
 * prints in its GUI. This agent is useful
 * when the application is not launched from
 * a command line or an IDE
 * 
 * @author Fabien Michel
 * @since MadKit 5.0.0.14
 * @version 0.9
 * 
 */
public class ConsoleAgent extends AbstractAgent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6101789640805528218L;
	
	final static private PrintStream systemOut = System.out;
	final static private PrintStream systemErr = System.err;
	
	@Override
	public void setupFrame(final JFrame frame) {
		final OutputPanel outP = new OutputPanel(this);
		final PrintStream ps = new PrintStream(outP.getOutputStream());
		frame.add(outP);
		System.setErr(ps);
		System.setOut(ps);
		frame.setSize(800, 500);
	}
	
	@Override
	protected void end() {
		System.setErr(systemErr);
		System.setOut(systemOut);
	}

	
}