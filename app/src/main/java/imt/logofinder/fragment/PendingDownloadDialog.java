package imt.logofinder.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import imt.logofinder.R;

/**
 * Created by Nico on 18/02/2018.
 */

public class PendingDownloadDialog extends DialogFragment {

    private TextView textViewStatus = null;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View layout = inflater.inflate(R.layout.layout_dialog_pending,null);

        textViewStatus = layout.findViewById(R.id.textView_frag_pending_status);
        textViewStatus.setText("Téléchargement...");

        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();

        return alertDialog;
    }


    public TextView getTextViewStatus() {
        return textViewStatus;
    }
}
