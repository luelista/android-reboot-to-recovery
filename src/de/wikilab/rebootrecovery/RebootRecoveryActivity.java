package de.wikilab.rebootrecovery;
/**
 *    This file is part of "Reboot to Recovery".
 *
 *    "Reboot to Recovery" is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    "Reboot to Recovery" is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with "Reboot to Recovery".  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import java.io.DataOutputStream;
import java.io.IOException;
import android.util.Log;

public class RebootRecoveryActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new AlertDialog.Builder(this)
		.setTitle(R.string.question)
		.setMessage(R.string.warn)
		.setPositiveButton(R.string.reboot, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				ExecuteAsRootBase e = new ExecuteAsRootBase() {
					@Override
					protected ArrayList<String> getCommandsToExecute() {
						ArrayList<String> a = new ArrayList<String>();
						a.add("reboot recovery");
						return a;
					}
				};
				e.execute();
			}
		})
		.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				RebootRecoveryActivity.this.finish();
			}
		})
		.setCancelable(false)
		.show();
	}

	/**
	 * Executes commands as root user
	 * @author http://muzikant-android.blogspot.com/2011/02/how-to-get-root-access-and-execute.html
	 */
	public abstract class ExecuteAsRootBase {
	  public final boolean execute() {
	    boolean retval = false;
	    try {
	      ArrayList<String> commands = getCommandsToExecute();
	      if (null != commands && commands.size() > 0) {
	        Process suProcess = Runtime.getRuntime().exec("su");

	        DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());

	        // Execute commands that require root access
	        for (String currCommand : commands) {
	          os.writeBytes(currCommand + "\n");
	          os.flush();
	        }

	        os.writeBytes("exit\n");
	        os.flush();

	        try {
	          int suProcessRetval = suProcess.waitFor();
	          if (255 != suProcessRetval) {
	            // Root access granted
	            retval = true;
	          } else {
	            // Root access denied
	            retval = false;
	          }
	        } catch (Exception ex) {
	          Log.e("Error executing root action", ex.toString());
	        }
	      }
	    } catch (IOException ex) {
	      Log.w("ROOT", "Can't get root access", ex);
	    } catch (SecurityException ex) {
	      Log.w("ROOT", "Can't get root access", ex);
	    } catch (Exception ex) {
	      Log.w("ROOT", "Error executing internal operation", ex);
	    }
	    
	    return retval;
	  }
	  
	  protected abstract ArrayList<String> getCommandsToExecute();
	}

}