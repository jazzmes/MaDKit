/*
 * Copyright 1997-2011 Fabien Michel, Olivier Gutknecht, Jacques Ferber
 * 
 * This file is part of MadKit.
 * 
 * MadKit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * MadKit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with MadKit. If not, see <http://www.gnu.org/licenses/>.
 */
package madkit.kernel;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * This class defines a logger specialized for MadKit agents.
 * 
 * @author Fabien Michel
 * @version 0.9
 * @since MadKit 5.0.0.5
 *
 */
public class AgentLogger extends Logger {

	final static private Map<AbstractAgent,AgentLogger> agentLoggers = new HashMap<AbstractAgent,AgentLogger>();//TODO evaluate foot print
	final static Level talkLevel = Level.parse("1100");

	final public static Formatter agentFormatter = new Formatter(){//TODO create Formatter hierarchy
		@Override
		final public String format(final LogRecord record) {
			final Level lvl = record.getLevel();
			if(lvl.equals(talkLevel)){
				return record.getMessage();
			}
			return record.getLoggerName()+" "+lvl.getLocalizedName()+" : "+formatMessage(record)+"\n";
		}
	};

	final public static Formatter agentFileFormatter = new Formatter(){
		@Override
		final public String format(final LogRecord record) {
			final Level lvl = record.getLevel();
			if(lvl.equals(talkLevel)){
				return record.getMessage();
			}
			return lvl.getLocalizedName()+" : "+formatMessage(record)+"\n";
		}
	};

	//	final static Formatter unregisteredAgentFormatter = new Formatter(){
	//		@Override
	//		final public String format(final LogRecord record) {
	//			final Level lvl = record.getLevel();
	//			if(lvl.equals(talkLevel)){
	//				return record.getMessage();
	//			}
	//			new Exception().printStackTrace();
	//			return record.getSourceMethodName() + lvl.getLocalizedName()+" : "+formatMessage(record)+"\n";
	//		}
	//	};

	final static AgentLogger defaultAgentLogger = new AgentLogger();

	private Level warningLogLevel = Level.parse(Madkit.defaultConfig.getProperty(Madkit.warningLogLevel));
	final private AbstractAgent myAgent;



	static AgentLogger getLogger(AbstractAgent agent) {
		AgentLogger al = agentLoggers.get(agent);
		if(al == null){
			al = new AgentLogger(agent);
			agentLoggers.put(agent, al);
			//			LogManager.getLogManager().addLogger(al);
		}
		return al;
	}

	static void removeLogger(AbstractAgent agent) {
		agentLoggers.remove(agent);
	}

	//	public static void renameLogger(AbstractAgent agent) {
	//		AgentLogger al = agentLoggers.get(agent);
	//		if(! al.getName().equals(agent.getName())){
	//			
	//		}
	//		if(al == null){
	//			al = new AgentLogger(agent);
	//			agentLoggers.put(agent, al);
	//			LogManager.getLogManager().addLogger(al);
	//		}
	//		return al;
	//	}



	/**
	 * @return the warningLogLevel
	 */
	public Level getWarningLogLevel() {
		return warningLogLevel;
	}
	
	@Override
	public ResourceBundle getResourceBundle() {
		return Madkit.getResourceBundle();
	}

	/** 
	 * Sets the agent's log level above which MadKit warnings are displayed
	 * 
	 * @param warningLogLevel the log level to set
	 */
	public void setWarningLogLevel(Level warningLogLevel) {
		this.warningLogLevel = warningLogLevel;
		updateAgentUi();
	}

	private void updateAgentUi() {
		if (myAgent != null) {
			madkit.gui.GUIToolkit.updateAgentUI(myAgent);
		}
	}

	AgentLogger(){
		super("[UNREGISTERED AGENT]", null);
		myAgent = null;
		setUseParentHandlers(false);
		super.setLevel(Level.parse(Madkit.defaultConfig.getProperty(Madkit.agentLogLevel)));
		setWarningLogLevel(Level.parse(Madkit.defaultConfig.getProperty(Madkit.warningLogLevel)));
		if(! Boolean.parseBoolean(Madkit.defaultConfig.getProperty(Madkit.noAgentConsoleLog))){
			addHandler(new ConsoleHandler());
		}
	}

	AgentLogger(AbstractAgent agent){
		super("["+agent.getName()+"]", null);
		myAgent = agent;
		setUseParentHandlers(false);
		super.setLevel(Level.parse(agent.getMadkitProperty(Madkit.agentLogLevel)));
		setWarningLogLevel(Level.parse(agent.getMadkitProperty(Madkit.warningLogLevel)));
		if(! Boolean.parseBoolean(agent.getMadkitProperty(Madkit.noAgentConsoleLog))){
			ConsoleHandler ch = new ConsoleHandler();
			addHandler(ch);
			ch.setFormatter(agentFormatter);
		}
		if(Boolean.parseBoolean(agent.getMadkitProperty(Madkit.createLogFiles))){
			addHandler(Utils.getFileHandler(agent.getMadkitProperty(Madkit.logDirectory)+getName()));
		}
	}

	@Override
	public synchronized void addHandler(Handler handler) throws SecurityException {
		super.addHandler(handler);
		handler.setLevel(getLevel());
	}
	
	static void resetLoggers(){
		for (Logger l : agentLoggers.values()) {
			for(Handler h : l.getHandlers()){
				l.removeHandler(h);
				try {
					h.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
//			l.setLevel(null);
		}
	}

	/**
	 * Log a TALK message. This uses a special level which could be used 
	 * to produce messages that will be rendered as they are, without any 
	 * formatting work nor end-of-line character.
	 * <p>
	 * If the logger's level is not {@link Level#OFF} 
	 * then the given message is forwarded to all the
	 * registered output Handler objects.
	 * <p>
	 * If the logger's level is {@link Level#OFF} 
	 * then the message is only printed to {@link System#err}
	 * @param   msg	The string message 
	 */
	public void talk(String msg){
		if(getLevel() == Level.OFF)
			System.err.print(msg);
		else
			log(talkLevel,msg);
	}

	//	/**
	//	 * @see java.util.logging.Logger#log(java.util.logging.Level, java.lang.String)
	//	 */
	//	@Override
	//	public void log(Level level, String msg) {
	//		if(level == talkLevel)
	//			
	//		super.log(level, msg);
	//	}

	//	void addGUIHandlerFor(AbstractAgent agent){
	//		if(agent.getGUIComponent() != null && agent.getGUIComponent() instanceof AgentGUIModel){
	//			OutputStream os = ((AgentGUIModel) agent.getGUIComponent()).getOutputStream();
	//			if(os != null){
	//				Handler h = new StreamHandler(os, AgentLogger.agentFileFormatter){
	//					@Override
	//					public synchronized void publish(LogRecord record) {
	//						super.publish(record);
	//						flush();
	//					}
	//				};
	//				h.setLevel(getLevel());
	//				addHandler(h);
	//			}
	//		}
	//	}

	/**
	 * @see java.util.logging.Logger#setLevel(java.util.logging.Level)
	 */
	@Override
	public void setLevel(Level newLevel) throws SecurityException {
		super.setLevel(newLevel);
		for(Handler h : getHandlers()){
			h.setLevel(newLevel);
		}
		updateAgentUi();
	}

	@Override
	public String toString() {
		return "Agent Logger"+ getName();
	}

	/**
	 * @see java.util.logging.Logger#log(java.util.logging.LogRecord)
	 */
	@Override
	public void log(final LogRecord record) {
		Throwable t = record.getThrown();
		if(t != null){
			//			if(record.getLevel() == Level.WARNING && getLevel().intValue() > warningLogLevel.intValue()){
			//				return;
			//			}
			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			pw.close();
			record.setMessage("\n ** "+sw);
		}
		super.log(record);
	}

}