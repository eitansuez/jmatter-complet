/*
 * Created on May 5, 2004
 */
package com.u2d.app;

import javax.swing.Icon;
import com.u2d.element.CommandInfo;
import com.u2d.type.atom.*;
import com.u2d.type.composite.*;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.pattern.*;
import com.u2d.reflection.ParamAt;
import com.u2d.reflection.CommandAt;

/**
 * @author Eitan Suez
 */
public class User extends AbstractComplexEObject
{
   private final StringEO _username = new StringEO();
   private final Password _password = new Password();
   private final BooleanEO _locked = new BooleanEO(false);
   private final Name _name = new Name();
   private final TextEO _desktop = new TextEO();
   private final Photo _photo = new Photo();

   private Role _role;
   public static String roleInverseFieldName = "users";

   public static String[] fieldOrder = {"username", "password", "locked", "name",
         "photo", "role", "desktop"};
   public static String[] identities = {"username"};
     // design decision to not make password field editable after first creation.
     // must go through changepwd cmd 
   public static String[] readOnly = {"locked", "desktop"};

   public User()
   {
      field("desktop").setHidden(true);
   }

   public User(String username)
   {
      this();
      _username.setValue(username);
   }

   public User(String username, String password)
   {
      this(username);
      _password.setValue(password);
   }

   public User(String username, String password, Role role)
   {
      this(username, password);
      _role = role;
      _role.getUsers().add(this);
   }

   public StringEO getUsername() { return _username; }
   public Password getPassword() { return _password; }
   public BooleanEO getLocked() { return _locked; }
   public Name getName() { return _name; }

   public Photo getPhoto() { return _photo; }

   public TextEO getDesktop() { return _desktop; }

   public Role getRole() { return _role; }
   public void setRole(Role role)
   {
      Role oldRole = _role;
      _role = role;
      firePropertyChange("role", oldRole, _role);
   }

   public Title title()
   {
      if (_name.isEmpty())
         return _username.title();
      return _name.title().appendParens(_username);
   }

   @CommandAt(mnemonic='p')
   public String ChangePassword(CommandInfo cmdInfo,
                                @ParamAt("New Password") Password password)
   {
      _password.setValue(password);
      save();
      log(LoggedEvent.INFO, cmdInfo.getCommand(), "User changed password");
      return "Password has been changed";
   }


   // ===

   public class LockedState extends ReadState
   {
      public String getName() { return LOCKED; }
      @CommandAt
      public void Unlock(CommandInfo cmdInfo)
      {
         switchState(_unlockedState);
      }
   }

   public class UnlockedState extends ReadState
   {
      public String getName() { return UNLOCKED; }
      @CommandAt
      public void Lock(CommandInfo cmdInfo)
      {
         switchState(_lockedState);
      }
   }

   private transient final State _lockedState, _unlockedState;
   {
      _lockedState = new LockedState();
      _unlockedState = new UnlockedState();
      _stateMap.put(_lockedState.getName(), _lockedState);
      _stateMap.put(_unlockedState.getName(), _unlockedState);
   }
   private static final String LOCKED = "Locked";
   private static final String UNLOCKED = "Unlocked";

   public State startState() { return _unlockedState; }

   public State restoredState()
   {
      String statename = _locked.booleanValue() ? LOCKED : UNLOCKED;
      return (State) _stateMap.get(statename);
   }

   private void switchState(State state)
   {
      if (_currentState.equals(state))
         return;  // no need to switch.

      setState(state, true /* shallow */);
      _locked.setValue(LOCKED.equals(state.getName()));
      firePropertyChange("icon", null, null);

      save();
   }

   /* *** custom icon code:  use photo as icon if possible  *** */
   private transient PhotoIconAssistant _assistant =
         new PhotoIconAssistant(this, _photo);
   public Icon iconLg() { return _assistant.iconLg(); }
   public Icon iconSm() { return _assistant.iconSm(); }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (!(obj instanceof User)) return false;
      User user = (User) obj;
      return _username.equals(user.getUsername());
   }

   public int hashCode()
   {
      return _username.hashCode();
   }
   
   public void applyRestrictions() { getRole().applyRestrictions(); }
   public void liftRestrictions() { getRole().liftRestrictions(); }


}
