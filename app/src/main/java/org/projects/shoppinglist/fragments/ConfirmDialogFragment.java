package org.projects.shoppinglist.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import org.projects.shoppinglist.R;

/**
 * You can pretty much reuse this class in your own project
 * if you want you can modify some of the text shown below.
 * of course if it was for a multilingual app you would put
 * he actual text that is now hardcoded inside the strings.xml file
 *
 * Created by jcr on 13-04-2018.
 */
public class ConfirmDialogFragment extends DialogFragment {

    public ConfirmDialogFragment() {

    }

    /**
     * Callback for positive action/click
     */
    private OnPositiveListener pCallback = null;

    /**
     * Callback for negative action/click
     */
    private OnNegativeListener nCallback = null;

    /**
     * Interface to be implemented if a user of the dialog wants to handle positive clicks.
     */
    public interface OnPositiveListener {
        void onPositiveClicked();
    }

    /**
     * Interface to be implemented if a user of the dialog wants to handle negative clicks.
     */
    public interface OnNegativeListener {
        void onNegativeClicked();
    }

    //This method will be called when the dialog fragment is called
    //and "attached" to the current activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            //we can ONLY do this cast IF the activity
            //actually implements the interface.
            if (activity instanceof OnPositiveListener) {
                pCallback = (OnPositiveListener) activity;
            }

            if (activity instanceof OnNegativeListener) {
                nCallback = (OnNegativeListener) activity;
            }
        } catch (ClassCastException e) {
            //This kills the program, because we have not
            //implemented the interface in the activity
            throw new ClassCastException(activity.toString()
                    + " must implement the correct listener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Here we create a new dialogbuilder;

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle(R.string.dialogTitle);
        alert.setMessage(R.string.dialogQuestion);
        alert.setPositiveButton(R.string.yes, pListener);
        alert.setNegativeButton(R.string.no, nListener);

        return alert.create();
    }

    //This is our positive listener for when the user presses the yes button
    DialogInterface.OnClickListener pListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            // these will be executed when user click Yes button
            positiveClick();
        }
    };

    //This is our negative listener for when the user presses the no button.
    DialogInterface.OnClickListener nListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            // these will be executed when user click No button
            negativeClick();
        }
    };

    //This method ensures that we actually now call the implemented
    //method defined in the ACTIVITY!
    protected void positiveClick()
    {
        if (pCallback != null) {
            pCallback.onPositiveClicked();
        }
    }

    //This method is empty, because it will be overriden
    protected void negativeClick()
    {
        if (nCallback != null) {
            nCallback.onNegativeClicked();
        }
    }

}
