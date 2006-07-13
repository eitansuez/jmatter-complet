package com.u2d.model;

import com.u2d.pattern.State;
import com.u2d.element.CommandInfo;

public abstract class EditableState extends State
{
   public abstract void Cancel(CommandInfo cmdInfo);
}
   

