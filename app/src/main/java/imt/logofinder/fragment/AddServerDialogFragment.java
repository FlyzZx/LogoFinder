package imt.logofinder.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import imt.logofinder.R;
import imt.logofinder.activity.OptionsActivity;

/**
 * Created by Tom on 17/02/2018.
 */

public class AddServerDialogFragment extends DialogFragment{

    EditText serverName = null;
    EditText serverPath = null;
    Button btnConfirm = null;

    CreateServerListener createServerListener = null;

    public interface CreateServerListener{
        void onServerCreation(String servername,String serverpath);
    }

    public void setCreateServerListener(CreateServerListener createServerListener){
        this.createServerListener = createServerListener;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View layout = inflater.inflate(R.layout.layout_dialog_add_server,null);

        serverName = layout.findViewById(R.id.editText_serverName);
        serverPath = layout.findViewById(R.id.editText_serverPath);
        btnConfirm = layout.findViewById(R.id.btn_dialog_confirm);
        builder.setView(layout);


        final AlertDialog alertDialog = builder.create();
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (serverName.getText().toString().equals("") || serverPath.getText().toString().equals("")) {
                    Snackbar.make(layout, R.string.dialog_wrong, Snackbar.LENGTH_SHORT).show();
                } else {
                    String srvName = serverName.getText().toString();
                    String srvPath = serverPath.getText().toString();

                    createServerListener.onServerCreation(srvName,srvPath);
                    alertDialog.dismiss();
                }
            }
        });
    return alertDialog;
    }
}
