package jmri.jmrit.throttle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.beans.*;

import org.jdom.Element;

/**
 * A JInternalFrame that provides a way for the user to enter a
 * decoder address. This class also store AddressListeners and
 * notifies them when the user enters a new address.
 */
public class AddressPanel extends JInternalFrame
{

    private ArrayList listeners;
    private JTextField addressField;
    private int previousAddress;
    private int currentAddress;

    /** The longest 4 character string. Used for resizing. */
    private static final String LONGEST_STRING = "mmmm";

    /**
     * Constructor
     */
    public AddressPanel()
    {
        initGUI();
    }

    /**
     * Add an AddressListener. AddressListeners are notified when the
     * user selects a new address.
     * @param l
     */
    public void addAddressListener(AddressListener l)
    {
        if (listeners == null)
        {
            listeners = new ArrayList(2);
        }
        if (!listeners.contains(l))
        {
            listeners.add(l);
        }
    }


    /**
     * Create, initialize and place the GUI objects.
     */
    private void initGUI()
     {
         JPanel mainPanel = new JPanel();
         this.setContentPane(mainPanel);

         mainPanel.setLayout(new GridBagLayout());
         GridBagConstraints constraints = new GridBagConstraints();
         constraints.anchor = GridBagConstraints.CENTER;
         constraints.fill = GridBagConstraints.HORIZONTAL;
         constraints.gridheight = 1;
         constraints.gridwidth = 1;
         constraints.ipadx = 0;
         constraints.ipady = 0;
         Insets insets = new Insets(2, 2, 2, 2);
         constraints.insets = insets;
         constraints.weightx = 1;
         constraints.weighty = 1;
         constraints.gridx = 0;
         constraints.gridy = 0;

         addressField = new JTextField();
         addressField.setColumns(4);
         addressField.setFont(new Font("", Font.PLAIN, 32));
         mainPanel.add(addressField, constraints);

         JButton setButton = new JButton("Set");
         constraints.gridx = 1;
         constraints.fill = GridBagConstraints.NONE;
         mainPanel.add(setButton, constraints);

         setButton.addActionListener(
                 new ActionListener()
         {
             public void actionPerformed(ActionEvent e)
             {
                 changeOfAddress();
             }
         });

         this.addComponentListener(
                 new ComponentAdapter()
         {
             public void componentResized(ComponentEvent e)
             {
                 changeFontSizes();
             }
         });
    }

    /**
     * A resizing has occurred, so determine the optimum font size
     * for the addressField.
     */
    private void changeFontSizes()
    {
        double fieldWidth = addressField.getSize().getWidth();
        int stringWidth = addressField.getFontMetrics(addressField.getFont()).
                          stringWidth(LONGEST_STRING);
        int fontSize = addressField.getFont().getSize();
        if (stringWidth > fieldWidth) // component has shrunk.
        {
            while (stringWidth > fieldWidth)
            {
                fontSize -= 2;
                Font f = new Font("", Font.PLAIN, fontSize);
                addressField.setFont(f);
                stringWidth = addressField.getFontMetrics(addressField.getFont()).
                              stringWidth(LONGEST_STRING);
            }
        }
        else // component has grown
        {
            while (fieldWidth - stringWidth > 10)
            {
                fontSize += 2;
                Font f = new Font("", Font.PLAIN, fontSize);
                addressField.setFont(f);
                stringWidth = addressField.getFontMetrics(addressField.getFont()).
                              stringWidth(LONGEST_STRING);
            }
        }

    }

    /**
     * The user has selected a new address. Notify all listeners.
     */
     public void changeOfAddress()
     {
         try
         {
             Integer value = new Integer(addressField.getText());
             previousAddress = currentAddress;
             currentAddress = value.intValue();
             if (currentAddress != previousAddress){
                 // send notification of new address
                 if (listeners != null)
                 {
                     for (int i=0; i<listeners.size(); i++)
                     {
                         AddressListener l = (AddressListener)listeners.get(i);
                         log.debug("Notify address listener "+l);
                         l.notifyAddressChanged(previousAddress, currentAddress);
                     }
                 }
             }
         }
         catch (NumberFormatException ex)
         {
             addressField.setText(String.valueOf(currentAddress));
         }

     }

     /**
      * Create an Element of this object's preferences.
      * <ul>
      * <li> Window Preferences
      * <li> Address value
      * </ul>
      *
      * @return org.jdom.Element for this objects preferences. Defined
      * in DTD/throttle-config
      */
     public Element getXml()
     {
         Element me = new Element("AddressPanel");
         Element window = new Element("window");
         WindowPreferences wp = new WindowPreferences();
         com.sun.java.util.collections.ArrayList children =
                 new com.sun.java.util.collections.ArrayList(1);
         children.add(wp.getPreferences(this));
         Element address = new Element("address");
         address.addAttribute("value", String.valueOf(addressField.getText()));
         children.add(address);
         me.setChildren(children);
         return me;
     }

     /**
      * Use the Element passed to initialize based on user prefs.
      * @param The Element containing prefs as defined
      * in DTD/throttle-config
      */
     public void setXml(Element e)
     {
         Element window = e.getChild("window");
         WindowPreferences wp = new WindowPreferences();
         wp.setPreferences(this, window);

         Element addressElement = e.getChild("address");
         String address = addressElement.getAttribute("value").getValue();
         addressField.setText(address);
         changeOfAddress();

     }
    static org.apache.log4j.Category log = org.apache.log4j.Category.getInstance(AddressPanel.class.getName());

}