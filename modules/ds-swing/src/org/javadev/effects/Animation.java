/*
Copyright (c) 2004 
Sam Berlin sberlin@limepeer.com
Luca Lutterotti Luca.Lutterotti@ing.unitn.it
Dmitry Markman, PhD dmarkman@mac.com
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:
1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products
   derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.javadev.effects;

import java.awt.Component;

/**
 * @author Luca Lutterotti
 * @author Sam Berlin
 * @author Dmitry Markman
 */

public interface Animation {
    
    /**
     * Draws an animation moving from 'first' to 'last'.
     * AnimationListener listens to the animation and is notified
     * when the animation is finished.
     * @return a Component which the animation is drawn on.
     */
    public Component animate(Component first, Component last, AnimationListener listener);

    /**
     * set direction of the animation.
     * meaning of the param direction is the function of every particulatr animator
     * @param direction
     */
    public void setDirection(boolean direction);

    /**
     * set duration of the animation.
     * @param duration duration of the animation in the ms
     */
    public void setAnimationDuration(int duration);


}