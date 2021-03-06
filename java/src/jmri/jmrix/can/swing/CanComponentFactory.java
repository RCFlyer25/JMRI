// CanComponentFactory.java
package jmri.jmrix.can.swing;

import jmri.jmrix.can.CanSystemConnectionMemo;

/**
 * Provide access to Swing components for the LocoNet subsystem.
 *
 * @author	Bob Jacobsen Copyright (C) 2010
 * @version $Revision: 19643 $
 * @since 2.99.2
 */
public class CanComponentFactory extends jmri.jmrix.swing.ComponentFactory {

    public CanComponentFactory(CanSystemConnectionMemo memo) {
        this.memo = memo;
    }

    CanSystemConnectionMemo memo;

    /**
     * Provide a menu with all items attached to this system connection
     */
    public javax.swing.JMenu getMenu() {
        if (memo.getDisabled()) {
            return null;
        }
        return new CanMenu(memo);
    }
}


/* @(#)CanComponentFactory.java */
