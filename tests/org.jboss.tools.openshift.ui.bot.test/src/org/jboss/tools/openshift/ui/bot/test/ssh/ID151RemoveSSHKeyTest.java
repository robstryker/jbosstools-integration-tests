package org.jboss.tools.openshift.ui.bot.test.ssh;

import static org.junit.Assert.assertTrue;

import org.jboss.reddeer.common.exception.RedDeerException;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.core.condition.JobIsRunning;
import org.jboss.reddeer.core.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.swt.condition.ButtonWithTextIsEnabled;
import org.jboss.reddeer.swt.impl.button.OkButton;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.menu.ContextMenu;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.table.DefaultTable;
import org.jboss.tools.openshift.reddeer.condition.TableIsEnabled;
import org.jboss.tools.openshift.reddeer.view.OpenShiftExplorerView;
import org.jboss.tools.openshift.ui.bot.test.util.Datastore;
import org.jboss.tools.openshift.reddeer.utils.OpenShiftLabel;
import org.junit.Test;

/**
 * Test removing SSH key from OpenShift account.
 * 
 * @author mlabuda@redhat.com
 *
 */
public class ID151RemoveSSHKeyTest {

	@Test
	public void testRemoveSSHKey() {
		removeSSHKey();
	}
		
	public static void removeSSHKey() {
		OpenShiftExplorerView explorer = new OpenShiftExplorerView();
		explorer.reopen();
		explorer.getConnection(Datastore.USERNAME).select();
		
		// Sometimes there occurs progress information shell
		try {
			new DefaultShell("Progress information");
			new WaitWhile(new ShellWithTextIsAvailable("Progress information"),
					TimePeriod.LONG);
		} catch (RedDeerException ex) {
			
		}
		
		explorer.getConnection(Datastore.USERNAME).select();
		new ContextMenu(OpenShiftLabel.ContextMenu.MANAGE_SSH_KEYS).select();
		
		new DefaultShell(OpenShiftLabel.Shell.MANAGE_SSH_KEYS);
		new PushButton(OpenShiftLabel.Button.REFRESH).click();
		
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);

		new WaitUntil(new TableIsEnabled(new DefaultTable()), TimePeriod.LONG);		
		
		assertTrue("There should be right one SSH key uploaded on OpenShift. Rerun test please.",
				new DefaultTable().getItems().size() == 1);
		
		new DefaultTable().getItem(0).select();
			
		new WaitUntil(new ButtonWithTextIsEnabled(new PushButton(OpenShiftLabel.Button.REMOVE)), 
				TimePeriod.SHORT);
			
		new PushButton(OpenShiftLabel.Button.REMOVE).click();
			
		new DefaultShell(OpenShiftLabel.Shell.REMOVE_SSH_KEY);
		new OkButton().click();
			
		new WaitWhile(new ShellWithTextIsAvailable(OpenShiftLabel.Shell.REMOVE_SSH_KEY),
				TimePeriod.LONG);
		
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
			
		new PushButton(OpenShiftLabel.Button.REFRESH).click();
			
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		
		assertTrue("There should not be any SSH key at this moment", new DefaultTable().getItems().size() == 0);
			
		new DefaultShell(OpenShiftLabel.Shell.MANAGE_SSH_KEYS);
		new OkButton().click();
		
		new WaitWhile(new ShellWithTextIsAvailable(OpenShiftLabel.Shell.MANAGE_SSH_KEYS), TimePeriod.LONG);
		
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
	}
}
