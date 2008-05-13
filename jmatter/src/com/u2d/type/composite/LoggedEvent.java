package com.u2d.type.composite;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.ComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.TextEO;
import com.u2d.type.LogEventType;
import com.u2d.app.User;
import com.u2d.element.Command;

/**
 * Date: Jun 9, 2005
 * Time: 10:43:48 AM
 *
 * @author Eitan Suez
 */
public class LoggedEvent extends AbstractComplexEObject
{
   private final StringEO _msg = new StringEO();
   private final TextEO _longMsg = new TextEO();
   private User _user;
   private Command _command;
   private ComplexEObject _object;
   private final LogEventType _eventType = new LogEventType(INFO);

   public static final String DEBUG = "Debug";
   public static final String INFO = "Information";
   public static final String IMPORTANT = "Important";
   public static final String ERROR = "Error";
   public static final String LOGIN = "Login";
   public static final String LOGOUT = "Logout";

   public static String[] fieldOrder = {"eventType", "msg", "longMsg", "user", "command", "object"};

   public LoggedEvent() { }

   public LoggedEvent(LogEventType type, ComplexEObject targetObject, Command command,
                      String msg)
   {
      _eventType.setValue(type);
      _object = targetObject;
      _command = command;
      _msg.setValue(msg);
      _user = currentUser();
   }
   
   public LoggedEvent(String msg, String longMsg, ComplexEObject targetObject)
   {
      _msg.setValue(msg);
      _longMsg.setValue(longMsg);
      _object = targetObject;
      _user = currentUser();
   }

   public Title title()
   {
      return _createdOn.title().append(":", _user).append(":", _msg);
   }


   public static String pluralName() { return "Log"; }

   public StringEO getMsg() { return _msg; }
   public TextEO getLongMsg() { return _longMsg; }

   public User getUser() { return _user; }
   public void setUser(User user)
   {
      User oldUser = _user;
      _user = user;
      firePropertyChange("user", oldUser, user);
   }

   public Command getCommand() { return _command; }
   public void setCommand(Command command)
   {
      Command oldCmd = _command;
      _command = command;
      firePropertyChange("command", oldCmd, _command);
   }

   public ComplexEObject getObject() { return _object; }
   public void setObject(ComplexEObject object)
   {
      ComplexEObject oldObject = _object;
      _object = object;
      firePropertyChange("object", oldObject, _object);
   }

   public LogEventType getEventType() { return _eventType; }

}
